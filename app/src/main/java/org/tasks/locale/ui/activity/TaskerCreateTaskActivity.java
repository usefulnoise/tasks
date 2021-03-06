package org.tasks.locale.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import net.dinglisch.android.tasker.TaskerPlugin;

import org.tasks.R;
import org.tasks.billing.PurchaseHelper;
import org.tasks.billing.PurchaseHelperCallback;
import org.tasks.injection.ActivityComponent;
import org.tasks.locale.bundle.TaskCreationBundle;
import org.tasks.preferences.Preferences;
import org.tasks.ui.MenuColorizer;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class TaskerCreateTaskActivity extends AbstractFragmentPluginAppCompatActivity implements PurchaseHelperCallback, Toolbar.OnMenuItemClickListener {

    private static final int REQUEST_PURCHASE = 10125;
    private static final String EXTRA_PURCHASE_INITIATED = "extra_purchase_initiated";

    @Inject Preferences preferences;
    @Inject PurchaseHelper purchaseHelper;

    @BindView(R.id.title) TextInputEditText title;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.due_date) TextInputEditText dueDate;
    @BindView(R.id.due_time) TextInputEditText dueTime;
    @BindView(R.id.priority) TextInputEditText priority;
    @BindView(R.id.description) TextInputEditText description;

    private Bundle previousBundle;
    private boolean purchaseInitiated;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tasker_create);

        ButterKnife.bind(this);

        toolbar.setTitle(R.string.tasker_create_task);
        final boolean backButtonSavesTask = preferences.backButtonSavesTask();
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this,
                backButtonSavesTask ? R.drawable.ic_close_24dp : R.drawable.ic_save_24dp));
        toolbar.setNavigationOnClickListener(v -> {
            if (backButtonSavesTask) {
                 discardButtonClick();
            } else {
                 save();
            }
        });
        toolbar.setOnMenuItemClickListener(this);
        toolbar.inflateMenu(R.menu.menu_tasker_create_task);
        MenuColorizer.colorToolbar(this, toolbar);

        if (savedInstanceState != null) {
            previousBundle = savedInstanceState.getParcelable(TaskCreationBundle.EXTRA_BUNDLE);
            purchaseInitiated = savedInstanceState.getBoolean(EXTRA_PURCHASE_INITIATED);
            TaskCreationBundle bundle = new TaskCreationBundle(previousBundle);
            title.setText(bundle.getTitle());
        }

        if (!preferences.hasPurchase(R.string.p_purchased_tasker) && !purchaseInitiated) {
            purchaseInitiated = purchaseHelper.purchase(this, getString(R.string.sku_tasker), getString(R.string.p_purchased_tasker), REQUEST_PURCHASE, this);
        }
    }

    @Override
    public void onPostCreateWithPreviousResult(final Bundle previousBundle, final String previousBlurb) {
        this.previousBundle = previousBundle;
        TaskCreationBundle bundle = new TaskCreationBundle(previousBundle);
        title.setText(bundle.getTitle());
        dueDate.setText(bundle.getDueDate());
        dueTime.setText(bundle.getDueTime());
        priority.setText(bundle.getPriority());
        description.setText(bundle.getDescription());
    }

    @Override
    public boolean isBundleValid(final Bundle bundle) {
        return TaskCreationBundle.isBundleValid(bundle);
    }

    @Override
    protected Bundle getResultBundle() {
        TaskCreationBundle bundle = new TaskCreationBundle();
        bundle.setTitle(title.getText().toString().trim());
        bundle.setDueDate(dueDate.getText().toString().trim());
        bundle.setDueTime(dueTime.getText().toString().trim());
        bundle.setPriority(priority.getText().toString().trim());
        bundle.setDescription(description.getText().toString().trim());
        Bundle resultBundle = bundle.build();
        if (TaskerPlugin.Setting.hostSupportsOnFireVariableReplacement(this)) {
            TaskerPlugin.Setting.setVariableReplaceKeys(resultBundle, new String[] {
                    TaskCreationBundle.EXTRA_TITLE,
                    TaskCreationBundle.EXTRA_DUE_DATE,
                    TaskCreationBundle.EXTRA_DUE_TIME,
                    TaskCreationBundle.EXTRA_PRIORITY,
                    TaskCreationBundle.EXTRA_DESCRIPTION
            });
        }
        return resultBundle;
    }

    @Override
    public String getResultBlurb(final Bundle bundle) {
        return title.getText().toString().trim();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PURCHASE) {
            purchaseHelper.handleActivityResult(this, requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        final boolean backButtonSavesTask = preferences.backButtonSavesTask();
        if (backButtonSavesTask) {
             save();
        } else {
             discardButtonClick();
        }
    }

    private void save() {
        finish();
    }

    private void discardButtonClick() {
        mIsCancelled = true;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!isChangingConfigurations()) {
            purchaseHelper.disposeIabHelper();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TaskCreationBundle.EXTRA_BUNDLE, previousBundle);
        outState.putBoolean(EXTRA_PURCHASE_INITIATED, purchaseInitiated);
    }

    @Override
    public void inject(ActivityComponent component) {
        component.inject(this);
    }

    @Override
    public void purchaseCompleted(boolean success, String sku) {
        if (!success) {
            discardButtonClick();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                save();
                return true;
            case R.id.menu_help:
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://tasks.org/help/tasker")));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
