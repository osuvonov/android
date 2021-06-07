package org.telegram.irooms.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public abstract class RequestHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertRequestHistory(RequestHistory requestHistory);

    @Query("update request_history set last_request=:date where chat_id=:chat_id")
    public abstract void updateRequestHistory(long chat_id, String date);

    @Query("select * from request_history where chat_id=:chat_id and user_id=:userId")
    public abstract RequestHistory getLastRequestDateForChat(long chat_id, int userId);

    @Query("delete from request_history")
    public abstract void deleteAll();
}
