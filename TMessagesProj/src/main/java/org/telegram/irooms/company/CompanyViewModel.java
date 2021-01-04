package org.telegram.irooms.company;

import android.app.Application;

import org.telegram.irooms.database.Company;
import org.telegram.irooms.task.TaskRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class CompanyViewModel extends AndroidViewModel {

    private TaskRepository taskRepository;

    private LiveData<List<Company>> allCompanies;

    public CompanyViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
        allCompanies = taskRepository.getAllCompanies();
    }

    public LiveData<List<Company>> getAllCompanies() {
        return allCompanies;
    }

    public void insert(Company task) {
        taskRepository.insert(task);
    }

}
