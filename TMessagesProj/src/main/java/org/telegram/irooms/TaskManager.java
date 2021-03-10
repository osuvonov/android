package org.telegram.irooms;

public class TaskManager {
    private static TaskManager instance;

    public static TaskManager getInstance() {
        if (instance == null) {
            synchronized (TaskManager.class) {
                if (instance == null) {
                    instance = new TaskManager();
                }
            }
        }
        return instance;
    }

    public void onLocalTaskCreated(){

    }


    public void onLocalTaskUpdated(){

    }

    public void onCloudTaskCreated(){

    }

    public void onCloudTaskUpdated(){

    }

}
