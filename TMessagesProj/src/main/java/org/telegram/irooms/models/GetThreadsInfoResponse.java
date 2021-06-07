package org.telegram.irooms.models;

import java.util.ArrayList;

public class GetThreadsInfoResponse {
        private boolean success;
        private ArrayList<ThreadInfo> result; // if success is true
        private Error error;    // if success is false

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public ArrayList<ThreadInfo> getResult() {
            return result;
        }

        public void setResult(ArrayList<ThreadInfo> result) {
            this.result = result;
        }

        public Error getError() {
            return error;
        }

        public void setError(Error error) {
            this.error = error;
        }
    }