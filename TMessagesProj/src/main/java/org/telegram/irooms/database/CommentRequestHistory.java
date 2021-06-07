package org.telegram.irooms.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "comment_request_history")
public class CommentRequestHistory {
    public CommentRequestHistory() {
    }

    public CommentRequestHistory(long task_id, String date) {
        this.task_id = task_id;
        this.lastRequest = date;
    }

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }

    public String getLastRequest() {
        return lastRequest;
    }

    @PrimaryKey
    @ColumnInfo(name = "task_id")
    private long task_id;

    @ColumnInfo(name = "last_request")
    private String lastRequest;

    public long getTaskId() {
        return task_id;
    }

    public void setTaskId(long chat_id) {
        this.task_id = chat_id;
    }

    public String getLastRequestDate() {
        return lastRequest;
    }

    public void setLastRequest(String lastRequest) {
        this.lastRequest = lastRequest;
    }
}
