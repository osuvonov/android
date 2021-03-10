package org.telegram.irooms.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "request_history")
public class RequestHistory {
    public RequestHistory(long chat_id, String lastRequest) {
        this.chat_id = chat_id;
        this.lastRequest = lastRequest;
    }

    @PrimaryKey
    @ColumnInfo(name = "chat_id")
    private long chat_id;

    @ColumnInfo(name = "last_request")
    private String lastRequest;

    public long getChat_id() {
        return chat_id;
    }

    public void setChat_id(long chat_id) {
        this.chat_id = chat_id;
    }

    public String getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(String lastRequest) {
        this.lastRequest = lastRequest;
    }
}
