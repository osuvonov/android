package org.telegram.irooms.task;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.telegram.irooms.database.Company;
import org.telegram.irooms.database.CompanyDao;
import org.telegram.irooms.database.Task;
import org.telegram.irooms.database.TaskDao;
import org.telegram.irooms.database.TaskDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;

/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */
public class TaskRepository {

    private TaskDao taskDao;

    private CompanyDao companyDao;

    private LiveData<List<Company>> allCompanies;

    private LiveData<List<Task>> allTasks;

    public TaskRepository(Application application) {
        TaskDatabase db = TaskDatabase.getDatabase(application);
        taskDao = db.taskDao();
        companyDao = db.companyDao();
        allTasks = taskDao.getAllTasks();
        allCompanies = companyDao.getAllCompanies();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Task>> getAllTasks() {

        return allTasks;
    }

    public Company getCompany(int id) {
        return companyDao.getCompany(id);
    }

    public Task getTask(int id) {
        return taskDao.getTask(id);
    }


    public ArrayList<Company> getCompanyList() {
        return (ArrayList<Company>) companyDao.getCompanyList();
    }

    public ArrayList<Task> getCompanyTasks(int companyId, int chatId) {
        return (ArrayList<Task>) taskDao.getTasksByChatId(companyId, chatId);
    }

    public ArrayList<Task> getChatRelatedTasks( ) {
        return (ArrayList<Task>) taskDao.getTasks( );
    }

    public LiveData<List<Company>> getAllCompanies() {
        return allCompanies;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.createTask(task);
        });
    }

    public void update(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.updateTask(task);
        });
    }

    public void deleteDataBase() {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.deleteAll();
            companyDao.deleteAll();
        });
    }

    public void insert(Company company) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            companyDao.createCompany(company);
        });
    }

    public void updateCompanyMembers(int companyId, String members, boolean addMembers) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            Company company = companyDao.getCompany(companyId);

            if (!addMembers) {
                ArrayList<Integer> membersToBeDeleted = new Gson().fromJson(members, new TypeToken<ArrayList<Integer>>() {
                }.getType());
                for (int i = 0; i < company.getMembers().size(); i++) {
                    for (int j = 0; j < membersToBeDeleted.size(); j++) {
                        if (company.getMembers().get(i).intValue() == membersToBeDeleted.get(j).intValue()) {
                            company.getMembers().remove(i);
                            i--;
                        }
                    }
                }
                companyDao.updateCompany(company);
            } else {
                ArrayList<Integer> membersToBeAdded = new Gson().fromJson(members, new TypeToken<ArrayList<Integer>>() {
                }.getType());
                if (company.getMembers() == null || company.getMembers().size() == 0) {
                    company.setMembers(membersToBeAdded);
                } else {
                    for (int j = 0; j < membersToBeAdded.size(); j++) {
                        boolean found = false;
                        for (int i = 0; i < company.getMembers().size(); i++) {
                            if (company.getMembers().get(i).intValue() == membersToBeAdded.get(j).intValue()) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            company.getMembers().add(membersToBeAdded.get(j));
                        }
                    }
                }
                companyDao.updateCompany(company);
            }
        });
    }
}
