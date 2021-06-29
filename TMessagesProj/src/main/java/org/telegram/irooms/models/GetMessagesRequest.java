package org.telegram.irooms.models;

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
