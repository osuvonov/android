package org.telegram.irooms.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "request_history")
public class RequestHistory {
    public RequestHistory(){}
    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public RequestHistory(long chat_id, String lastRequest, int userID) {
        this.chat_id = chat_id;
        this.lastRequest = lastRequest;
        this.user_id=userID;
    }

    @PrimaryKey
    @ColumnInfo(name = "chat_id")
    private long chat_id;

    @ColumnInfo(name = "user_id")
    private long user_id;

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
