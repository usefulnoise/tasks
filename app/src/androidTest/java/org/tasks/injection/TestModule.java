package org.tasks.injection;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.todoroo.astrid.dao.Database;
import com.todoroo.astrid.dao.TaskDao;

import org.tasks.data.AlarmDao;
import org.tasks.data.GoogleTaskDao;
import org.tasks.data.GoogleTaskListDao;
import org.tasks.data.LocationDao;
import org.tasks.data.TagDao;
import org.tasks.data.TagDataDao;
import org.tasks.data.TaskListMetadataDao;
import org.tasks.data.UserActivityDao;
import org.tasks.notifications.NotificationDao;
import org.tasks.preferences.PermissionChecker;
import org.tasks.preferences.PermissivePermissionChecker;

import dagger.Module;
import dagger.Provides;

@Module
public class TestModule {
    private Context context;

    public TestModule(Context context) {
        this.context = context;
    }

    @Provides
    @ApplicationScope
    public Database getDatabase() {
        return Room.inMemoryDatabaseBuilder(context, Database.class)
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    public NotificationDao getNotificationDao(Database appDatabase) {
        return appDatabase.notificationDao();
    }

    @Provides
    public TagDataDao getTagDataDao(Database database) {
        return database.getTagDataDao();
    }

    @Provides
    public UserActivityDao getUserActivityDao(Database database) {
        return database.getUserActivityDao();
    }

    @Provides
    public TaskListMetadataDao getTaskListMetadataDao(Database database) {
        return database.getTaskListMetadataDao();
    }

    @Provides
    public GoogleTaskListDao getGoogleTaskListDao(Database database) {
        return database.getGoogleTaskListDao();
    }

    @Provides
    public AlarmDao getAlarmDao(Database database) {
        return database.getAlarmDao();
    }

    @Provides
    public GoogleTaskDao getGoogleTaskDao(Database database) {
        return database.getGoogleTaskDao();
    }

    @Provides
    public TagDao getTagDao(Database database) {
        return database.getTagDao();
    }

    @Provides
    public LocationDao getLocationDao(Database database) {
        return database.getLocationDao();
    }

    @Provides
    public TaskDao getTaskDao(Database database) {
        TaskDao taskDao = database.getTaskDao();
        taskDao.initialize(context);
        return taskDao;
    }

    @ApplicationScope
    @Provides
    @ForApplication
    public Context getContext() {
        return context;
    }

    @Provides
    public PermissionChecker getPermissionChecker() {
        return new PermissivePermissionChecker(context);
    }
}
