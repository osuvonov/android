package org.telegram.irooms;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.google.android.exoplayer2.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.irooms.database.Company;
import org.telegram.irooms.database.RequestHistory;
import org.telegram.irooms.database.Task;
import org.telegram.irooms.models.EditMessageRequest;
import org.telegram.irooms.models.GetMessagesRequest;
import org.telegram.irooms.models.GetThreadsInfoRequest;
import org.telegram.irooms.models.SendMessageRequest;
import org.telegram.irooms.models.TaskMessage;
import org.telegram.irooms.models.TaskThreading;
import org.telegram.irooms.models.ThreadInfo;
import org.telegram.irooms.models.UpdateThreadInfoRequest;
import org.telegram.irooms.models.UpdateThreadInfoResponse;
import org.telegram.irooms.network.APIClient;
import org.telegram.irooms.network.IRoomJsonParser;
import org.telegram.irooms.network.VolleyCallback;
import org.telegram.irooms.task.TaskManagerListener;
import org.telegram.irooms.task.RoomsRepository;
import org.telegram.irooms.task.TaskRunner;
import org.telegram.irooms.task.TaskSocketQuery;
import org.telegram.irooms.task.TaskUtil;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.Calendar;

import io.socket.client.Socket;


public class IRoomsManager {

    private static IRoomsManager instance;

    public ArrayList<Company> getCompanyList() {
        return companyArrayList;
    }

    public void setCompanyList(ArrayList<Company> companyArrayList) {
        this.companyArrayList = companyArrayList;
    }

    private ArrayList<Company> companyArrayList = new ArrayList<>();

    public static IRoomsManager getInstance() {
        if (instance == null) {
            instance = new IRoomsManager();
        }
        return instance;
    }

    public void authenticate(Context context, IRoomsCallback callback) {
        APIClient.getInstance().loginToRooms(context, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                callback.onSuccess(response);

            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public void registerCompanyBySocket(Context context, Socket socket, String name, IRoomsCallback callback) {
        APIClient.getInstance().registerCompanyBySocket(socket, name, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    new TaskRunner().executeAsync(() -> {
                        String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;
                        RoomsRepository roomsRepository = RoomsRepository.getInstance((Application) context.getApplicationContext(), phone);

                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject co = jsonObject.getJSONObject("result");

                        if (co != null) {
                            int companyId = co.getInt("id");
                            String companyName = co.getString("name");
                            Company company = new Company(companyId, companyName);
                            roomsRepository.insert(company);
                            return company;
                        }

                        return null;
                    }, result -> callback.onSuccess(response));
                } catch (Exception x) {
                    callback.onError(x.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public void getCompanyList(Context context, IRoomsCallback callback) {
        TaskRunner runner = new TaskRunner();
        runner.executeAsync(() -> {
            String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;

            RoomsRepository repository = RoomsRepository.getInstance((Application) context.getApplicationContext(), phone);
            return repository.getCompanyList();
        }, result -> {
            String json = new Gson().toJson(result);
            callback.onSuccess(json);
        });
    }

    public void addMembersToCompanyBySocket(Context context, Socket socket, int companyId, ArrayList<Integer> members, IRoomsCallback callback) {
        APIClient.getInstance().addMembersToCompanyBySocket(context, socket, companyId, members, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    new TaskRunner().executeAsync(() -> {
                        String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;

                        RoomsRepository roomsRepository = RoomsRepository.getInstance((Application) context.getApplicationContext(), phone);
                        String json = new Gson().toJson(members);
                        roomsRepository.updateCompanyMembers(companyId, json, true);
                        return "1";
                    }, result -> callback.onSuccess(response));
                } catch (Exception x) {
                    callback.onError(x.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public void deleteMembersFromCompany(Context context, Socket socket, int companyId, ArrayList<Integer> members, IRoomsCallback callback) {
        APIClient.getInstance().deleteMembersFromCompanyBySocket(context, socket, companyId, members, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    new TaskRunner().executeAsync(() -> {

                        String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;

                        RoomsRepository roomsRepository = RoomsRepository.getInstance((Application) context.getApplicationContext(), phone);
                        String json = new Gson().toJson(members);
                        roomsRepository.updateCompanyMembers(companyId, json, false);
                        return "1";
                    }, result -> callback.onSuccess(response));
                } catch (Exception x) {
                    callback.onError(x.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public void createTaskBySocket(Context context, Socket socket, Task task, TaskManagerListener callback) {
        APIClient.getInstance().createTaskBySocket(socket, task, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    new TaskRunner().executeAsync(() -> {

                        Task myTask = IRoomJsonParser.getTask(response, false);

                        if (myTask != null) {
                            String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;
                            RoomsRepository roomsRepository = RoomsRepository.getInstance((Application) context.getApplicationContext(), phone);
                            myTask.setLocalStatus(3);

                            roomsRepository.createOnlineTask(myTask, UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId());
                            callback.onCreate(myTask);
                        }
                        return myTask;
                    }, result -> {
                    });
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Log.e("socket error: ", error);
            }
        });
    }

    public void editTaskBySocket(Context context, Socket socket, Task task, TaskManagerListener callback) {
        APIClient.getInstance().editTaskBySocket(socket, task, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    new TaskRunner().executeAsync(() -> {

                        Task myTask = IRoomJsonParser.getTask(response, false);

                        if (myTask != null) {

                            String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;

                            RoomsRepository roomsRepository = RoomsRepository.getInstance((Application) context.getApplicationContext(), phone);
                            myTask.setLocalStatus(3);
                            roomsRepository.updateOnlineTask(myTask);
                            callback.onUpdate(myTask);
                        }

                        return myTask;
                    }, result -> {
                    });
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                //       AndroidUtilities.runOnUIThread(() -> Toast.makeText(context, error, Toast.LENGTH_SHORT).show());
                // callback.onError(error);
            }
        });
    }

    public void getMyCompanies(final Context context, Socket socket, IRoomsCallback callback) {
        APIClient.getInstance().getMyCompaniesBySocket(context, socket, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    TaskRunner runner = new TaskRunner();
                    runner.executeAsync(() -> {
                        IRoomsManager.getInstance().setHasCompany(context, true);

                        String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;

                        RoomsRepository repository = RoomsRepository.getInstance((Application) context.getApplicationContext(), phone);
                        JSONObject jsonObject = new JSONObject(response);
                        ArrayList<Company> companies = IRoomJsonParser.getCompanies(jsonObject.toString());

                        if (companies.size() > 0) {
                            for (Company company : companies) {
                                if (company.getOwner_id() == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
                                    IRoomsManager.getInstance().setHasCompany(context, true);
                                    if (IRoomsManager.getInstance().getSelectedCompanyId(context) == company.getId()) {
                                        IRoomsManager.getInstance().setOwnerOfSelectedCompany(context, true);
                                    }
                                }
                                repository.insert(company);
                            }
                        } else {
                            setSelectedCompany(context, new Company(), false);
                        }
                        return null;
                    }, (TaskRunner.TaskCompletionListener<String>) result -> callback.onSuccess(response));

                } catch (Exception x) {
                    Log.d("ERRROR: ", x.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public void setHasCompany(Context context, boolean b) {
        String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;

        SharedPreferences preferences = context.getSharedPreferences(phone, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(Constants.HAS_COMPANY, b).commit();
    }

    public boolean getHasCompany(Context context) {
        String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;

        SharedPreferences preferences = context.getSharedPreferences(phone, Context.MODE_PRIVATE);
        return preferences.getBoolean(Constants.HAS_COMPANY, false);
    }

    public void getCompany(Context context, int companyId, IRoomCallback<Company> companyIRoomCallback) {
        TaskRunner taskRunner = new TaskRunner();
        taskRunner.executeAsync(() -> {

            String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;

            RoomsRepository repo = RoomsRepository.getInstance((Application) context.getApplicationContext(), phone);
            return repo.getCompany(companyId);

        }, success -> companyIRoomCallback.onSuccess(success));
    }

    public void getMyTasks(Context context, Socket socket, TaskSocketQuery query, IRoomCallback<ArrayList<Task>> callback) {
        TaskRunner runner = new TaskRunner();
        runner.executeAsync(() -> {
            TLRPC.User user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
            if (user != null) {
                String phone = user.phone;

                RoomsRepository roomsRepository = RoomsRepository.getInstance((Application) context.getApplicationContext(), phone);
                String date = roomsRepository.getLastRequestDateForChat(query.getChat_id(), user.id);
                query.setFrom_date("".equals(date) ? null : date);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(calendar.getTime());
                calendar.add(Calendar.MINUTE, -1);

                String currentISODate = TaskUtil.getISODate(calendar.getTime());

                APIClient.getInstance().getTasksBySocket(socket, query, new VolleyCallback() {
                    @Override
                    public void onSuccess(String response) {

                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            ArrayList<Task> tasks = IRoomJsonParser.getTasks(jsonObject.toString());

                            if (tasks.size() > 0) {
                                RequestHistory history = new RequestHistory(query.getChat_id(), currentISODate, user.id);
                                roomsRepository.insert(history);

                                long userID = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                                for (Task task : tasks) {
                                    roomsRepository.createOnlineTask(task, userID);
                                }
                            }
                            callback.onSuccess(tasks);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
            }

            return null;
        }, result -> {

        });
    }

    public void setSelectedCompany(Context context, Company company, boolean isOwner) {
        String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;

        SharedPreferences preferences = context.getSharedPreferences(phone, Context.MODE_PRIVATE);
        preferences.edit().putString(Constants.SELECTED_COMPANY_NAME, company.getName()).commit();

        preferences.edit().putInt(Constants.SELECTED_COMPANY_ID, company.getId()).commit();

        preferences.edit().putBoolean(Constants.IS_OWNER, isOwner).commit();

    }


    public String getSelectedCompanyName(Context context) {
        String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;

        SharedPreferences preferences = context.getSharedPreferences(phone, Context.MODE_PRIVATE);
        String name = preferences.getString(Constants.SELECTED_COMPANY_NAME, "");
        return name.equals("") ? "" : name;
    }

    public int getSelectedCompanyId(Context context) {
        String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;

        SharedPreferences preferences = context.getSharedPreferences(phone, Context.MODE_PRIVATE);
        int id = preferences.getInt(Constants.SELECTED_COMPANY_ID, 0);
        return id;
    }

    public boolean isOwnerOfSelectedCompany(Context context) {

        String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;

        SharedPreferences preferences = context.getSharedPreferences(phone, Context.MODE_PRIVATE);

        return preferences.getBoolean(Constants.IS_OWNER, false);
    }

    public void setOwnerOfSelectedCompany(Context context, boolean owner) {

        String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;

        SharedPreferences preferences = context.getSharedPreferences(phone, Context.MODE_PRIVATE);

        preferences.edit().putBoolean(Constants.IS_OWNER, owner).commit();
    }

    public void getGroupChatRelatedTasks(Context context, long[] chatId, int companyId, IRoomCallback<ArrayList<Task>> arrayListIRoomCallback) {
        TaskRunner runner = new TaskRunner();
        runner.executeAsync(() -> {

                    String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;

                    RoomsRepository repository = RoomsRepository.getInstance((Application) context.getApplicationContext(), phone);
//                    if (companyId >= 0) {
//                        repository.getChatAndCompanyRelatedTasks(chatId, arrayListIRoomCallback, companyId);
//                    } else {
                    repository.getChatRelatedTasks(chatId, arrayListIRoomCallback);
//                    }
                    return "";
                }, result -> {
                }
        );
    }

    public void getUserTasks(int companyId, Context context, int selectedAccountUserId, int id, IRoomCallback<ArrayList<Task>> arrayListIRoomCallback) {
        TaskRunner runner = new TaskRunner();
        runner.executeAsync(() -> {

                    String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;

                    RoomsRepository repository = RoomsRepository.getInstance((Application) context.getApplicationContext(), phone);

                    repository.getPrivateChatTasks(companyId, selectedAccountUserId, id, arrayListIRoomCallback);

                    return "";
                }, result -> {
                }
        );
    }

    public void getAccountTasks(Context context, int id, IRoomCallback<ArrayList<Task>> arrayListIRoomCallback) {
        TaskRunner runner = new TaskRunner();
        runner.executeAsync(() -> {
                    String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;

                    RoomsRepository repository = RoomsRepository.getInstance((Application) context.getApplicationContext(), phone);
                    repository.getAccountTasks(id, arrayListIRoomCallback);
                    return "";
                }, result -> {
                }
        );
    }

    public void setDarkTheme(Context context, boolean toDark) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(Constants.DARK_THEME, toDark).apply();
    }

    public boolean isDarkMode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.DARK_THEME, false);
    }

    public void performLogOut(Context context) {
        if (context != null) {

            long recordID = PreferenceManager.getDefaultSharedPreferences(context).getLong("record_id", -1);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putLong("record_id", -1).commit();

            TLRPC.User user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();

            if (user != null) {

                String token = PreferenceManager.getDefaultSharedPreferences(context).getString("token" + user.phone, "");
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("token" + user.phone, "").commit();

                SharedPreferences preferences = context.getSharedPreferences(user.phone, Context.MODE_PRIVATE);
                preferences.edit().clear().apply();
                APIClient.getInstance().deleteSubscription(context, token, recordID, new VolleyCallback() {
                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onError(String error) {
                        Log.e("logout error", error);
                    }
                });
            }
        }
    }

    public Company getTeam(long teamId) {
        if (companyArrayList == null || companyArrayList.size() == 0) {
            return null;
        }
        for (int i = 0; i < companyArrayList.size(); i++) {
            Company team = companyArrayList.get(i);
            if (team.getId() == teamId) {
                return team;
            }
        }

        return null;
    }

    //-----------------Task Threading-----------------
    public void sendMessage(Socket socket, Context context, SendMessageRequest request, final IRoomCallback<TaskMessage> messageResponse) {
        try {
            APIClient.getInstance().sendMessage(socket, request, new VolleyCallback() {
                @Override
                public void onSuccess(String response) {

                    try {
                        new TaskRunner().executeAsync(() -> {
                            JSONObject msgResponse = new JSONObject(response);
                            if (msgResponse.optBoolean("success")) {
                                TaskMessage taskMessage = IRoomJsonParser.getTaskMessage(response);
                                if (taskMessage != null) {
                                    String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;
                                    RoomsRepository roomsRepository = RoomsRepository.getInstance((Application) context.getApplicationContext(), phone);

                                    roomsRepository.insertTaskMessage(taskMessage);
                                }
                                messageResponse.onSuccess(taskMessage);
                            }
                            return null;
                        }, result -> {
                        });
                    } catch (Exception x) {
                        x.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editMessage(Socket socket,Context context, EditMessageRequest request, final IRoomCallback<TaskMessage> messageResponse) {
        try {
            APIClient.getInstance().editMessage(socket, request, new VolleyCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        new TaskRunner().executeAsync(() -> {
                            JSONObject msgResponse = new JSONObject(response);
                            if (msgResponse.optBoolean("success")) {
                                TaskMessage taskMessage = IRoomJsonParser.getTaskMessage(response);
                                if (taskMessage != null) {
                                    String phone = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;
                                    RoomsRepository roomsRepository = RoomsRepository.getInstance((Application) context.getApplicationContext(), phone);

                                    roomsRepository.insertTaskMessage(taskMessage);
                                }
                                messageResponse.onSuccess(taskMessage);
                            }
                            return null;
                        }, result -> {
                        });
                    } catch (Exception x) {
                        x.printStackTrace();
                    }

                }

                @Override
                public void onError(String error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTaskMessages(Socket socket, GetMessagesRequest request, final IRoomCallback<ArrayList<TaskMessage>> messageResponse) {
        try {
            APIClient.getInstance().getTaskMessages(socket, request, new VolleyCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject msgResponse = new JSONObject(response);
                        if (msgResponse.optBoolean("success")) {
                            ArrayList<TaskMessage> taskMessages = IRoomJsonParser.getTaskMessages(response);
                            messageResponse.onSuccess(taskMessages);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTask(Socket socket, long taskId, final IRoomCallback<Task> messageResponse) {
        try {
            APIClient.getInstance().getTask(socket, taskId, new VolleyCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        Log.e("get task...", response);
                        JSONObject msgResponse = new JSONObject(response);
                        if (msgResponse.optBoolean("success")) {
                            Task task = IRoomJsonParser.getTask(response, false);
                            messageResponse.onSuccess(task);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateThreadInfo(Socket socket, UpdateThreadInfoRequest request, final IRoomCallback<UpdateThreadInfoResponse> messageResponse) {
        try {
            APIClient.getInstance().updateThreadInfo(socket, request, new VolleyCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject msgResponse = new JSONObject(response);
                        UpdateThreadInfoResponse updateThreadInfoResponse = new UpdateThreadInfoResponse();
                        updateThreadInfoResponse.setTask_id(msgResponse.optLong("task_id"));
                        updateThreadInfoResponse.setLast_read_message_id(msgResponse.optLong("last_read_message_id"));
                        messageResponse.onSuccess(updateThreadInfoResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getThreadsInfo(Socket socket, GetThreadsInfoRequest request,
                               final IRoomCallback<ArrayList<ThreadInfo>> messageResponse) {
        try {
            APIClient.getInstance().getThreadsInfo(socket, request, new VolleyCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject msgResponse = new JSONObject(response);
                        if (msgResponse.optBoolean("success")) {
                            ArrayList<ThreadInfo> threadInfos = IRoomJsonParser.getThreadInfos(response);
                            messageResponse.onSuccess(threadInfos);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------------------------------------

    public interface IRoomsCallback {
        void onSuccess(String success);

        void onError(String error);
    }

    public interface IRoomCallback<R> {

        void onSuccess(R success);

        void onError(String error);
    }

}
