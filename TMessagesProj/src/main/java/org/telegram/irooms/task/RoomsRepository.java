package org.telegram.irooms.task;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.telegram.irooms.IRoomsManager;
import org.telegram.irooms.database.Company;
import org.telegram.irooms.database.CompanyDao;
import org.telegram.irooms.database.RequestHistory;
import org.telegram.irooms.database.RequestHistoryDao;
import org.telegram.irooms.database.Task;
import org.telegram.irooms.database.TaskDao;
import org.telegram.irooms.database.TaskDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */
public class RoomsRepository {

    private final RequestHistoryDao requestHistoryDao;

    public List<Task> getOfflineTasks() {
        return taskDao.getOfflineTasks();
    }

    public void deleteCompanies() {
        companyDao.deleteCompanies();
    }

    public String getLastRequestDateForChat(long chat_id) {
        RequestHistory requestHistory = requestHistoryDao.getLastRequestDateForChat(chat_id);
        if (requestHistory == null) {
            return "";
        }
        String lastRequest = requestHistory.getLastRequest();

        return lastRequest == null ? "" : lastRequest;
    }

    public void insert(RequestHistory requestHistory) {
        requestHistoryDao.insertRequestHistory(requestHistory);
    }

    public void getChatRelatedTasks(long[] chatID, IRoomsManager.IRoomCallback<ArrayList<Task>> arrayListIRoomCallback) {
        ArrayList<Task> list = (ArrayList<Task>) taskDao.getTasksByChatId(chatID);

        arrayListIRoomCallback.onSuccess(list);
    }

    public void getChatAndCompanyRelatedTasks(long chatID, IRoomsManager.IRoomCallback<ArrayList<Task>> arrayListIRoomCallback, int companyID) {
        ArrayList<Task> list = (ArrayList<Task>) taskDao.getTasksByChatAndCompanyId(chatID, companyID);

        arrayListIRoomCallback.onSuccess(list);
    }

    public void getPrivateChatTasks(int companyID, int selectedAccountUserId, int ownerId, IRoomsManager.IRoomCallback<ArrayList<Task>> arrayListIRoomCallback) {
        if (getCompanyList().size() > 0) {
            arrayListIRoomCallback.onSuccess((ArrayList<Task>) taskDao.getPrivateChatTasksTeam(companyID, selectedAccountUserId, ownerId));
        } else {
            arrayListIRoomCallback.onSuccess((ArrayList<Task>) taskDao.getPrivateChatTasksNoTeam(selectedAccountUserId, ownerId));
        }

    }

    public void getAccountTasks(int ownerId, IRoomsManager.IRoomCallback<ArrayList<Task>> arrayListIRoomCallback) {
        arrayListIRoomCallback.onSuccess((ArrayList<Task>) taskDao.getAccountTasks(ownerId + ""));
    }

    public ArrayList<Task> getTasks() {
        return (ArrayList<Task>) taskDao.getTasks();
    }

    public interface LocalTaskChangeListener {
        void onLocalTaskCreated(Task task);

        void onLocalTaskUpdated(Task task);
    }

    private LocalTaskChangeListener localTaskChangeListener;

    public void setLocalTaskChangeListener(LocalTaskChangeListener sl) {
        this.localTaskChangeListener = sl;
    }

    private TaskDao taskDao;

    private CompanyDao companyDao;

    private static RoomsRepository instance;

    public static RoomsRepository getInstance(Application application) {
        if (instance == null) {
            synchronized (RoomsRepository.class) {
                if (instance == null) {
                    instance = new RoomsRepository(application);
                }
            }
        }
        return instance;
    }

    private RoomsRepository(Application application) {
        TaskDatabase db = TaskDatabase.getDatabase(application);
        taskDao = db.taskDao();
        companyDao = db.companyDao();
        requestHistoryDao = db.requestHistoryDao();
    }

    public Company getCompany(int id) {
        return companyDao.getCompany(id);
    }

    public Task getTask(int id) {
        return taskDao.getTask(id);
    }

    // calls when offline task should be created locally
    // does not matter if network is on/off
    public void createLocalTask(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            task.setLocalStatus(1);
            taskDao.createLocalTask(task);
            if (localTaskChangeListener != null) {
                localTaskChangeListener.onLocalTaskCreated(task);
            }
        });
    }

    //
    public void createOnlineTask(Task task, long currentAccountId) {
        task.setLocalStatus(3);

        if (task.getLocal_id() != null && !task.getLocal_id().equals("")) {
            Task task1 = taskDao.getTaskByLocalId(task.getLocal_id());
            if (task1 != null && task1.getId() == -1) {
                taskDao.deleteTask(task.getLocal_id());
            } else {
                taskDao.deleteTask(task.getId());
            }
        } else {
            taskDao.deleteTask(task.getId());
        }
        taskDao.createTask(task);
    }

    public void updateLocalTask(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            if (task.getLocalStatus() == 1) {
                Task task1 = taskDao.getTaskByLocalId(task.getLocal_id());
                if (task1 != null)
                    task.setpId(task1.getpId());
            } else {
                Task task1 = taskDao.getTask(task.getId());
                if (task1 != null) {
                    task.setLocalStatus(2);
                    task.setpId(task1.getpId());
                }
            }
            taskDao.updateTask(task);
            if (localTaskChangeListener != null && task.getLocalStatus() != 1) {
                localTaskChangeListener.onLocalTaskUpdated(task);
            }
        });
    }

    public void updateOnlineTask(Task task) {
        Task task1 = taskDao.getTask(task.getId());
        if (task1 != null) {
            task.setpId(task1.getpId());
        }
        task.setLocalStatus(3);
        taskDao.updateTask(task);
    }

    public ArrayList<Company> getCompanyList() {
        return (ArrayList<Company>) companyDao.getCompanyList();
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Task task) {
        taskDao.createTask(task);
    }

    public void update(Company company) {
        companyDao.updateCompany(company);
    }

    public void insert(Company company) {
        companyDao.createCompany(company);
    }

    public void updateCompanyMembers(int companyId, String members, boolean addMembers) {
        Company company = companyDao.getCompany(companyId);
        if (company == null) {
            return;
        }
        if (!addMembers) {
            ArrayList<Long> membersToBeDeleted = new Gson().fromJson(members, new TypeToken<ArrayList<Long>>() {
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
    }

}
