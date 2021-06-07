package org.telegram.irooms.models;

public class UpdateThreadInfoResponse {
    public UpdateThreadInfoResponse() {

    }

    private long task_id;
    private long last_read_message_id;

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }

    public long getLast_read_message_id() {
        return last_read_message_id;
    }

    public void setLast_read_message_id(long last_read_message_id) {
        this.last_read_message_id = last_read_message_id;
    }
}