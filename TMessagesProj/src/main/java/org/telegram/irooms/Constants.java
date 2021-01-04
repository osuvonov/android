package org.telegram.irooms;

public interface Constants {
    String API_KEY = "dXAtFhOy7f00kZ0wA02zzA==";
    String TAG = "TASK_TASK";
    int CREATE_COMPANY = 1120;
    String SELECTED_COMPANY_ID = "selected_company_id";
    String SELECTED_COMPANY_NAME = "selected_company_name";
    String IS_OWNER = "is_owner";
    String HAS_COMPANY = "has_company";
    int OWNER_TASK=99;
    int PRIVATE_CHAT_TASK=97;
    int OTHERS_TASK=98;
    String EDIT_TASK_SOCKET_ENDPOINT = "https://live.irooms.io/tasks";

    enum Status {
        New, Done, TODO, Finished
    }
}
