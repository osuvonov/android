package org.telegram.irooms.database;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

@TypeConverters(Converters.class)
@Database(entities = {Task.class, Company.class, RequestHistory.class}, version = 21, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {

    private static String currentDb;

    public abstract TaskDao taskDao();

    public abstract CompanyDao companyDao();

    public abstract RequestHistoryDao requestHistoryDao();


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
                CompanyDao companyDao = INSTANCE.companyDao();
                companyDao.deleteAll();
                RequestHistoryDao requestHistory = INSTANCE.requestHistoryDao();
                requestHistory.deleteAll();
            });
        }

    };
}