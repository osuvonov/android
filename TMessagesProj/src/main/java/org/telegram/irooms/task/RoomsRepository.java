package org.telegram.irooms.task;

import android.app.Application;

import org.telegram.irooms.IRoomsManager;
import org.telegram.irooms.database.CommentHistoryDao;
import org.telegram.irooms.database.CommentRequestHistory;
import org.telegram.irooms.database.RequestHistory;
import org.telegram.irooms.database.RequestHistoryDao;
import org.telegram.irooms.database.Task;
import org.telegram.irooms.database.TaskDao;
import org.telegram.irooms.database.TaskDatabase;
import org.telegram.irooms.database.TaskMessageDao;
import org.telegram.irooms.models.TaskMessage;

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

    public String getLastRequestDateForChat(long chat_id, int userId) {
        RequestHistory requestHistory = requestHistoryDao.getLastRequestDateForChat(chat_id, userId);
        if (requestHistory == null) {
            return "";
        }
        String lastRequest = requestHistory.getLastRequest();

        return lastRequest == null ? "" : lastRequest;
    }

    public void insert(RequestHistory requestHistory) {
        requestHistoryDao.insertRequestHistory(requestHistory);
    }

    // ---------------------    PAGINATION TASKS   ----------------------------------------


    public ArrayList<Task> getChatRelatedTasks(long[] chatID, int limit, int offset) {
        return (ArrayList<Task>) taskDao.getTasksByChatId(chatID, limit, offset);
    }

    public ArrayList<Task> getChatRelatedTasks(long chatID, int limit, int offset) {

        return (ArrayList<Task>) taskDao.getTaskByChatId(chatID, limit, offset);
    }

    public ArrayList<Task> getPrivateChatTasks(int selectedAccountUserId, int ownerId, int limit, int offset) {

        return (ArrayList<Task>) taskDao.getPrivateChatTasksNoTeam(selectedAccountUserId, ownerId, limit, offset);

    }

    public ArrayList<Task> getAccountTasks(int ownerId, int limit, int offset) {
        return (ArrayList<Task>) taskDao.getAccountTasks(ownerId + "", limit, offset);
    }


    // ---------------------   END PAGINATION TASKS   ----------------------------------------

    public void getChatRelatedTasks(long[] chatID, IRoomsManager.IRoomCallback<ArrayList<Task>> arrayListIRoomCallback) {
        ArrayList<Task> list = (ArrayList<Task>) taskDao.getTasksByChatId(chatID);

        arrayListIRoomCallback.onSuccess(list);
    }

    public void getChatRelatedTasks(long chatID, IRoomsManager.IRoomCallback<ArrayList<Task>> arrayListIRoomCallback) {
        ArrayList<Task> list = (ArrayList<Task>) taskDao.getTaskByChatId(chatID);

        arrayListIRoomCallback.onSuccess(list);
    }

    public void getPrivateChatTasks(int selectedAccountUserId, int ownerId, IRoomsManager.IRoomCallback<ArrayList<Task>> arrayListIRoomCallback) {

        arrayListIRoomCallback.onSuccess((ArrayList<Task>) taskDao.getPrivateChatTasksNoTeam(selectedAccountUserId, ownerId));

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

    private TaskMessageDao taskMessageDao;

    private CommentHistoryDao commentHistoryDao;

    private static RoomsRepository instance;
    private static String currentDb;

    public static RoomsRepository getInstance(Application application, String dbName) {
        if (currentDb != null) {
            if (!currentDb.equals(dbName)) {
                instance = null;
            }
        }
        if (instance == null) {
            synchronized (RoomsRepository.class) {
                if (instance == null) {
                    currentDb = dbName;
                    instance = new RoomsRepository(application, dbName);
                }
            }
        }
        return instance;
    }


    private RoomsRepository(Application application, String dbName) {

        TaskDatabase db = TaskDatabase.getDatabase(application, dbName);
        taskDao = db.taskDao();
        requestHistoryDao = db.requestHistoryDao();
        taskMessageDao = db.taskMessageDao();
        commentHistoryDao = db.commentHistoryDao();
    }

    public Task getTask(long id) {
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

    public void updateLastReadMessageId(long taskId, long lastReadId) {
        taskDao.updateLastReadMessageId(taskId, lastReadId);
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Task task) {
        taskDao.createTask(task);
    }

    //-------------------------- task messages(comments)-----------------
    public List<TaskMessage> getTaskMessages(long taskId) {
        return taskMessageDao.getTaskMessages(taskId);
    }

    public void insertTaskMessage(TaskMessage taskMessage) {
        taskMessageDao.createTaskMessage(taskMessage);
    }

    public void insertTaskMessages(ArrayList<TaskMessage> taskMessages) {
        taskMessageDao.createTaskMessages(taskMessages);
    }

    public void updateTaskMessage(TaskMessage taskMessage) {
        taskMessageDao.updateTaskMessage(taskMessage);
    }

    //-------------------------- messages request history(comments)-----------------
    public CommentRequestHistory getLastTaskMessageRequest(long taskId) {
        return commentHistoryDao.getLastTaskMessageRequest(taskId);
    }

    public void insertTaskMessageHistory(CommentRequestHistory commentRequestHistory) {
        commentHistoryDao.insertRequestHistory(commentRequestHistory);
    }

    public void updateTaskMessageHistory(long taskId, String date) {
        commentHistoryDao.updateRequestHistory(taskId, date);
    }
}
