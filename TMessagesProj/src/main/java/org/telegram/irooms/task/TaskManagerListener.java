package org.telegram.irooms.task;

import org.telegram.irooms.database.Task;

public interface TaskManagerListener {
    void onCreate(Task task);

    void onUpdate(Task task);
}
