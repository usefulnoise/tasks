package org.tasks.intents;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;

import com.google.common.base.Strings;
import com.todoroo.astrid.activity.TaskListActivity;
import com.todoroo.astrid.api.Filter;
import com.todoroo.astrid.data.Task;

public class TaskIntents {

    public static TaskStackBuilder getEditTaskStack(Context context, final Filter filter, Task task) {
        Intent intent = getTaskListIntent(context, filter);
        intent.putExtra(TaskListActivity.OPEN_NEW_TASK, task);
        return TaskStackBuilder.create(context).addNextIntent(intent);
    }

    public static TaskStackBuilder getEditTaskStack(Context context, final Filter filter, final long taskId) {
        Intent intent = getTaskListIntent(context, filter);
        intent.putExtra(TaskListActivity.OPEN_TASK, taskId);
        return TaskStackBuilder.create(context).addNextIntent(intent);
    }

    public static Intent getNewTaskIntent(Context context, String filterId) {
        return getEditTaskIntent(context, filterId, 0L);
    }

    public static Intent getEditTaskIntent(Context context, String filterId, long taskId) {
        Intent taskListIntent = getTaskListByIdIntent(context, filterId);
        taskListIntent.putExtra(TaskListActivity.OPEN_TASK, taskId);
        return taskListIntent;
    }

    public static Intent getTaskListIntent(Context context, final Filter filter) {
        Intent intent = new Intent(context, TaskListActivity.class);
        if (filter != null) {
            intent.putExtra(TaskListActivity.OPEN_FILTER, filter);
        }
        return intent;
    }

    public static Intent getTaskListByIdIntent(Context context, final String filterId) {
        Intent intent = new Intent(context, TaskListActivity.class);
        if (!Strings.isNullOrEmpty(filterId)) {
            intent.putExtra(TaskListActivity.LOAD_FILTER, filterId);
        }
        return intent;
    }
}
