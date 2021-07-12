package org.telegram.irooms.database;

import android.content.Context;

import org.telegram.irooms.models.TaskMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

@TypeConverters(Converters.class)
@Database(entities = {Task.class,RequestHistory.class,
        TaskMessage.class, CommentRequestHistory.class}, version = 31, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {

    private static String currentDb;

    public abstract TaskDao taskDao();

    public abstract RequestHistoryDao requestHistoryDao();

    public abstract TaskMessageDao taskMessageDao();

    public abstract CommentHistoryDao commentHistoryDao();


    // marking the instance as volatile to ensure atomic access to the variable
    private static volatile TaskDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static TaskDatabase getDatabase(final Context context, String dbName) {
        if (currentDb != null) {
            if (!currentDb.equals(dbName)) {
                INSTANCE.close();
                INSTANCE = null;
            }
        }
        if (INSTANCE == null) {
            synchronized (TaskDatabase.class) {
                if (INSTANCE == null) {
                    currentDb = dbName;
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TaskDatabase.class, dbName)
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Override the onCreate method to populate the database.
     * For this sample, we clear the database every time it is created.
     */
    private static Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background
                TaskDao dao = INSTANCE.taskDao();
                dao.deleteAll();
                RequestHistoryDao requestHistory = INSTANCE.requestHistoryDao();
                requestHistory.deleteAll();
                TaskMessageDao taskMessageDao = INSTANCE.taskMessageDao();
                taskMessageDao.deleteAll();
                CommentHistoryDao commentHistoryDao = INSTANCE.commentHistoryDao();
                commentHistoryDao.deleteAll();
            });
        }

    };
}