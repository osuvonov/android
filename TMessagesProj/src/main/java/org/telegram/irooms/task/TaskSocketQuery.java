package org.telegram.irooms.task;

public class TaskSocketQuery {
        private int company_id;
        private long chat_id;
        private int limit;
        private int offset;
        String order_by;// 'created_at', // id, updated_at
        boolean asc = true;

        public int getCompany_id() {
            return company_id;
        }

        public void setCompany_id(int company_id) {
            this.company_id = company_id;
        }

        public long getChat_id() {
            return chat_id;
        }

        public void setChat_id(long chat_id) {
            this.chat_id = chat_id;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public String getOrder_by() {
            return order_by;
        }

        public void setOrder_by(String order_by) {
            this.order_by = order_by;
        }

        public boolean isAsc() {
            return asc;
        }

        public void setAsc(boolean asc) {
            this.asc = asc;
        }
    }
