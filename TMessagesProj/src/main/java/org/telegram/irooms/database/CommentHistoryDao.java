package org.telegram.irooms.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public abstract class CommentHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertRequestHistory(CommentRequestHistory requestHistory);

    @Query("update comment_request_history set last_request=:date where task_id=:task_id")
    public abstract void updateRequestHistory(long task_id, String date);

    @Query("select * from comment_request_history where task_id=:task_id ")
    public abstract CommentRequestHistory getLastTaskMessageRequest(long task_id);

    @Query("delete from comment_request_history")
    public abstract void deleteAll();
}
