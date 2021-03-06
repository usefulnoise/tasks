package org.tasks.backup;

import com.todoroo.astrid.data.Task;

import org.tasks.data.Alarm;
import org.tasks.data.Filter;
import org.tasks.data.GoogleTask;
import org.tasks.data.GoogleTaskList;
import org.tasks.data.Location;
import org.tasks.data.Tag;
import org.tasks.data.TagData;
import org.tasks.data.UserActivity;

import java.util.List;

public class BackupContainer {

    List<TaskBackup> tasks;
    List<TagData> tags;
    List<Filter> filters;
    List<GoogleTaskList> googleTaskLists;

    BackupContainer(List<TaskBackup> tasks, List<TagData> tags, List<Filter> filters, List<GoogleTaskList> googleTaskLists) {
        this.tasks = tasks;
        this.tags = tags;
        this.filters = filters;
        this.googleTaskLists = googleTaskLists;
    }

    static class TaskBackup {
        Task task;
        List<Alarm> alarms;
        List<Location> locations;
        List<Tag> tags;
        List<GoogleTask> google;
        List<UserActivity> comments;

        TaskBackup(Task task, List<Alarm> alarms, List<Location> locations, List<Tag> tags,
                   List<GoogleTask> google, List<UserActivity> comments) {
            this.task = task;
            this.alarms = alarms;
            this.locations = locations;
            this.tags = tags;
            this.google = google;
            this.comments = comments;
        }
    }
}
