package org.telegram.irooms.models;

public class TaskMessage {
    private long id;
    private int from_id;
    private long task_id;
    private long reply_to;     // maybe null
    private String date;        // iso8601
    private String edit_date;   // iso8601
    private String text;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getFrom_id() {
        return from_id;
    }

    public void setFrom_id(int from_id) {
        this.from_id = from_id;
    }

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }

    public long getReply_to() {
        return reply_to;
    }

    public void setReply_to(long reply_to) {
        this.reply_to = reply_to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEdit_date() {
        return edit_date;
    }

    public void setEdit_date(String edit_date) {
        this.edit_date = edit_date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}