package org.telegram.irooms.models;

import java.util.ArrayList;

public class TaskThreading {

    public class SendMessageResponse {
        private boolean success;
        private TaskMessage result; // if success is true
        private Error error;    // if success is false
    }

    public class EditMessageResponse {
        private boolean success;
        private TaskMessage result; // if success is true
        private Error error;    // if success is false
    }

    public class GetMessagesResponse {
        private boolean success;
        private ArrayList<TaskMessage> result; // if success is true
        private Error error;    // if success is false
    }
}
