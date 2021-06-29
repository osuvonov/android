package org.telegram.irooms.models;

public class SendMessageRequest {
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private long task_id;
    private long reply_to; // maybe skipped or null
    private String text;
}