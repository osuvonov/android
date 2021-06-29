package org.telegram.irooms.database;

import org.telegram.irooms.models.TaskMessage;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public abstract class TaskMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void createTaskMessage(TaskMessage message);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void createTaskMessages(List<TaskMessage> message);

    @Update
    public abstract void updateTaskMessage(TaskMessage message);

    @Query("delete from tbl_task_message")
    public abstract void deleteAll();

    @Query("select * from tbl_task_message where task_id=:task_id")
    public abstract List<TaskMessage> getTaskMessages(long task_id);
}
