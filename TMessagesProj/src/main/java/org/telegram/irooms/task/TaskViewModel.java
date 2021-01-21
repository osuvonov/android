package org.telegram.irooms.task;

import android.app.Application;

import org.telegram.irooms.database.Task;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class TaskViewModel extends AndroidViewModel {

    private TaskRepository taskRepository;

    private LiveData<List<Task>> allTasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository =  TaskRepository.getInstance(application);
       // allTasks = taskRepository.getAllTasks();
    }


}
