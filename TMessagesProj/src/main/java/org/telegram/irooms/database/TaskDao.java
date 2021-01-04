package org.telegram.irooms.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TaskDao {

    @Query("select * from tbl_tasks")
    LiveData<List<Task>> getAllTasks();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void createTask(Task task);

    @Query("delete from tbl_tasks")
    void deleteAll();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(Task task);

    @Query("select * from tbl_tasks where id=:taskId")
    Task getTask(long taskId);


    @Query("select * from tbl_tasks ")
    List<Task> getTasks( );

    @Query("select * from tbl_tasks where company_id=:company_id and chat_id=:chatId")
    List<Task> getTasksByChatId(int company_id, int chatId);

}
