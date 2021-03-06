package org.tasks.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.todoroo.astrid.gtasks.GtasksListService;

import org.tasks.R;
import org.tasks.data.GoogleTaskList;
import org.tasks.dialogs.DialogBuilder;
import org.tasks.gtasks.GoogleTaskListSelectionHandler;
import org.tasks.injection.DialogFragmentComponent;
import org.tasks.injection.InjectingDialogFragment;
import org.tasks.themes.ThemeAccent;
import org.tasks.themes.ThemeCache;
import org.tasks.ui.SingleCheckedArrayAdapter;

import java.util.List;

import javax.inject.Inject;

import static com.google.common.collect.Lists.transform;

public class SupportGoogleTaskListPicker extends InjectingDialogFragment {

    public static SupportGoogleTaskListPicker newSupportGoogleTaskListPicker(GoogleTaskList selected) {
        SupportGoogleTaskListPicker dialog = new SupportGoogleTaskListPicker();
        Bundle arguments = new Bundle();
        if (selected != null) {
            arguments.putParcelable(EXTRA_SELECTED, selected);
        }
        dialog.setArguments(arguments);
        return dialog;
    }

    private static final String EXTRA_SELECTED = "extra_selected";

    @Inject DialogBuilder dialogBuilder;
    @Inject GtasksListService gtasksListService;
    @Inject ThemeCache themeCache;
    @Inject ThemeAccent themeAccent;

    private GoogleTaskListSelectionHandler handler;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        GoogleTaskList selected = arguments == null ? null : arguments.getParcelable(EXTRA_SELECTED);
        return createDialog(getActivity(), themeCache, dialogBuilder, gtasksListService,
                selected, themeAccent, list -> handler.selectedList(list));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        handler = (GoogleTaskListSelectionHandler) activity;
    }

    public static AlertDialog createDialog(Context context, ThemeCache themeCache,
                                           DialogBuilder dialogBuilder, GtasksListService gtasksListService,
                                           GoogleTaskList selected, ThemeAccent themeAccent,
                                           final GoogleTaskListSelectionHandler handler) {
        final List<GoogleTaskList> lists = gtasksListService.getLists();
        List<String> listNames = transform(lists, GoogleTaskList::getTitle);
        SingleCheckedArrayAdapter adapter = new SingleCheckedArrayAdapter(context, listNames, themeAccent) {
            @Override
            protected int getDrawable(int position) {
                return R.drawable.ic_cloud_black_24dp;
            }

            @Override
            protected int getDrawableColor(int position) {
                GoogleTaskList list = lists.get(position);
                int color = list.getColor();
                return color >= 0
                        ? themeCache.getThemeColor(color).getPrimaryColor()
                        : super.getDrawableColor(position);
            }
        };
        if (selected != null) {
            adapter.setChecked(selected.getTitle());
        }
        return dialogBuilder.newDialog()
                .setSingleChoiceItems(adapter, -1, (dialog, which) -> {
                    handler.selectedList(lists.get(which));
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    protected void inject(DialogFragmentComponent component) {
        component.inject(this);
    }
}
