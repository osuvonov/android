package org.telegram.irooms.models;

import java.util.ArrayList;

public class TaskThreading {

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

    public class SendMessageResponse {
        private boolean success;
        private TaskMessage result; // if success is true
        private Error error;    // if success is false
    }

    public class EditMessageRequest {
        private long id; // public class id
        private String text;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public class EditMessageResponse {
        private boolean success;
        private TaskMessage result; // if success is true
        private Error error;    // if success is false
    }

    public class GetMessagesRequest {
        private long task_id;
        private String updated_since; // iso date

        public long getTask_id() {
            return task_id;
        }

        public void setTask_id(long task_id) {
            this.task_id = task_id;
        }

        public String getUpdated_since() {
            return updated_since;
        }

        public void setUpdated_since(String updated_since) {
            this.updated_since = updated_since;
        }

    }

    public class GetMessagesResponse {
        private boolean success;
        private ArrayList<TaskMessage> result; // if success is true
        private Error error;    // if success is false
    }

    public class UpdateThreadInfoRequest {
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

    public class UpdateThreadInfoResponse {
        private long task_id;
        private long last_read_message_id;
    }

    public class GetThreadsInfoRequest {
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

    public class GetThreadsInfoResponse {
        private boolean success;
        private ArrayList<ThreadInfo> result; // if success is true
        private Error error;    // if success is false
    }
}
