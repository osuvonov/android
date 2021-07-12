package org.telegram.irooms.database;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public abstract class TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void createTask(Task task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(ArrayList<Task> tasks);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void createLocalTask(Task task);

    @Query("delete from tbl_tasks")
    public abstract void deleteAll();

    @Query("delete from tbl_tasks where local_id=:localID")
    public abstract void deleteTask(String localID);

    @Query("delete from tbl_tasks where id=:id")
    public abstract void deleteTask(long id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateTask(Task task);

    @Query("select * from tbl_tasks where id=:taskId order by id desc limit 1")
    public abstract Task getTask(long taskId);

    @Query("select  * from tbl_tasks where local_id=:localId order by pId desc limit 1")
    public abstract Task getTaskByLocalId(String localId);

    @Query("select * from tbl_tasks ")
    public abstract List<Task> getTasks();

    @Query("select * from tbl_tasks where chat_id in (:chatId) ")
    public abstract List<Task> getTasksByChatId(long[] chatId);

    @Query("select * from tbl_tasks where (chat_id in (:chatId)) ")
    public abstract List<Task> getTaskByChatId(long chatId );

    @Query("select * from tbl_tasks where local_status in (1,2)")
    public abstract List<Task> getOfflineTasks();

    @Transaction
    public void deleteAndInsertAll(  ArrayList<Task> list) {
        insertAll(list);
    }

    @Query("Select * from tbl_tasks where ((members like '%' || :chatId  || '%') or" +
            " (members like '%' || :accountId  ||  '%' and creator_id=:chatId) or" +
            "(chat_id =:accountId and creator_id=:chatId) or" +
            "(chat_id=:chatId and creator_id=:accountId) ) order by pId desc")
    public abstract List<Task> getPrivateChatTasksTeam(  int accountId, int chatId);

    @Query("Select * from tbl_tasks where (chat_id =:accountId and creator_id=:chatId) or(chat_id=:chatId and creator_id=:accountId)")
    public abstract List<Task> getPrivateChatTasksNoTeam(int accountId, int chatId);

    @Query("select * from tbl_tasks where members like '%' || :ownerId  || '%'")
    public abstract List<Task> getAccountTasks(String ownerId);

    //------------------------------------------------ pagination -------------------------------------------------------
    @Query("select * from tbl_tasks where members like '%' || :ownerId  || '%' order by pId desc limit :limit offset :offset")
    public abstract List<Task> getAccountTasks(String ownerId, int limit, int offset);

    @Query("Select * from tbl_tasks where ((members like '%' || :chatId  || '%') or" +
            " (members like '%' || :accountId  ||  '%' and creator_id=:chatId) or" +
            "(chat_id =:accountId and creator_id=:chatId) or" +
            "(chat_id=:chatId and creator_id=:accountId)) order by pId desc limit :limit offset :offset")
    public abstract List<Task> getPrivateChatTasksTeam(int accountId, int chatId, int limit, int offset);

    @Query("Select * from tbl_tasks where (chat_id =:accountId and creator_id=:chatId) or(chat_id=:chatId and" +
            " creator_id=:accountId) order by pId desc limit :limit offset :offset")
    public abstract List<Task> getPrivateChatTasksNoTeam(int accountId, int chatId, int limit, int offset);

    @Query("select * from tbl_tasks where chat_id in (:chatId) order by pId desc limit :limit offset :offset")
    public abstract List<Task> getTasksByChatId(long[] chatId, int limit, int offset);

    @Query("select * from tbl_tasks where (chat_id in (:chatId))  order by pId desc limit :limit offset :offset ")
    public abstract List<Task> getTaskByChatId(long chatId, int limit, int offset);

    @Query("update tbl_tasks set last_read_message_id=:lastReadId where id=:taskId")
    public abstract void updateLastReadMessageId(long taskId, long lastReadId);
}
