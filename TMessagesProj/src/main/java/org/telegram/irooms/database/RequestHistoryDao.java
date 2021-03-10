package org.telegram.irooms.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "request_history")
public class RequestHistory {
    @ColumnInfo(name="chat_id")
    private long chat_id;

    @ColumnInfo(name="last_request")
    private String lastRequest;

}
