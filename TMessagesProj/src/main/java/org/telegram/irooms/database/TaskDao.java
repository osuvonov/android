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

    @Query("select * from tbl_tasks where company_id=:company_id and chat_id=:chatId")
    public abstract List<Task> getTasksByChatIdAndCompanyId(int company_id, long chatId);

    @Query("select * from tbl_tasks where company_id=:company_id and (chat_id in (:chatId,:ownerId) or creator_id in (:chatId,:ownerId)) ")
    public abstract List<Task> getTasksByChatIdAndCompanyId(int company_id, long chatId, long ownerId);


    @Query("select * from tbl_tasks where (chat_id in (:chatId)) ")
    public abstract List<Task> getTasksByChatId(long chatId);


    @Query("select * from tbl_tasks where local_status in (1,2)")
    public abstract List<Task> getOfflineTasks();

    @Query("update tbl_tasks set id=:taskId where local_id=:localId")
    public abstract void updateFromServer(long taskId, String localId);

    @Query("select max(id) from tbl_tasks")
    public abstract long getTaskMaxId();

    @Query("delete from tbl_tasks where local_status not in(1,2)")
    public abstract void deleteOnlineTasks();

    @Query("delete from tbl_tasks where local_status not in (1,2) and company_id=:companyId")
    public abstract void deleteCompanyOnlineTasks(int companyId);

    @Query("select count(*) from tbl_tasks")
    public abstract int getTasksCount();

    @Transaction
    public void deleteAndInsertAll(int companyId, ArrayList<Task> list) {
        deleteCompanyOnlineTasks(companyId);
        insertAll(list);
    }

    @Query("Select * from tbl_tasks where (members like '%' || :chatId  || '%') or (members like '%' || :accountId  ||  '%' and creator_id=:chatId) or(chat_id =:accountId and creator_id=:chatId) or(chat_id=:chatId and creator_id=:accountId)")
    public abstract List<Task> getPrivateChatTasks(int accountId, int chatId);

    @Query("select * from tbl_tasks where members like '%' || :ownerId  || '%'")
    public abstract List<Task> getAccountTasks(String ownerId);

}
