package org.telegram.irooms;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.android.exoplayer2.util.Log;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.irooms.database.Company;
import org.telegram.irooms.database.Task;
import org.telegram.irooms.network.Backend;
import org.telegram.irooms.network.IRoomJsonParser;
import org.telegram.irooms.network.VolleyCallback;
import org.telegram.irooms.task.TaskManagerListener;
import org.telegram.irooms.task.TaskRepository;
import org.telegram.irooms.task.TaskRunner;
import org.telegram.irooms.task.TaskSocketQuery;
import org.telegram.messenger.UserConfig;

import java.util.ArrayList;

import io.socket.client.Socket;


public class IRoomsManager {

    private static IRoomsManager instance;

    public static IRoomsManager getInstance() {
        if (instance == null) {
            instance = new IRoomsManager();
        }
        return instance;
    }

    public void authenticate(Context context, IRoomsCallback callback) {
        Backend.getInstance().loginToRooms(context, new VolleyCallback() {
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
        Backend.getInstance().registerCompanyBySocket(socket, name, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    new TaskRunner().executeAsync(() -> {
                        TaskRepository taskRepository = TaskRepository.getInstance((Application) context.getApplicationContext());

                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject co = jsonObject.getJSONObject("result");

                        if (co != null) {
                            int companyId = co.getInt("id");
                            String companyName = co.getString("name");
                            Company company = new Company(companyId, companyName);
                            taskRepository.insert(company);
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
            TaskRepository repository = TaskRepository.getInstance((Application) context.getApplicationContext());
            return repository.getCompanyList();
        }, result -> callback.onSuccess(new Gson().toJson(result)));
    }

    public void addMembersToCompanyBySocket(Context context, Socket socket, int companyId, ArrayList<Integer> members, IRoomsCallback callback) {
        Backend.getInstance().addMembersToCompanyBySocket(context, socket, companyId, members, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    new TaskRunner().executeAsync(() -> {
                        TaskRepository taskRepository = TaskRepository.getInstance((Application) context.getApplicationContext());

                        taskRepository.updateCompanyMembers(companyId, new Gson().toJson(members), true);
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
        Backend.getInstance().deleteMembersFromCompanyBySocket(context, socket, companyId, members, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    new TaskRunner().executeAsync(() -> {
                        TaskRepository taskRepository = TaskRepository.getInstance((Application) context.getApplicationContext());

                        taskRepository.updateCompanyMembers(companyId, new Gson().toJson(members), false);
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
        Backend.getInstance().createTaskBySocket(context, socket, task, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    new TaskRunner().executeAsync(() -> {

                        Task myTask = IRoomJsonParser.getTask(response, false);

                        if (myTask != null) {
                            TaskRepository taskRepository = TaskRepository.getInstance((Application) context.getApplicationContext());
                            myTask.setLocalStatus(3);

                            taskRepository.createOnlineTask(myTask, UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId());
                            callback.onCreate(myTask);
                        }
                        return myTask;
                    }, result -> {
                    });
                } catch (Exception x) {
                    x.printStackTrace();
                    //callback.onError(x.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                Log.e("socket error: ", error);
            }
        });
    }

    public void editTaskBySocket(Context context, Socket socket, Task task, TaskManagerListener callback) {
        Backend.getInstance().editTaskBySocket(context, socket, task, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    new TaskRunner().executeAsync(() -> {

                        Task myTask = IRoomJsonParser.getTask(response, false);

                        if (myTask != null) {
                            TaskRepository taskRepository = TaskRepository.getInstance((Application) context.getApplicationContext());
                            myTask.setLocalStatus(3);
                            taskRepository.updateOnlineTask(myTask);
                            callback.onUpdate(myTask);
                        }

                        return myTask;
                    }, result -> {
                    });
                } catch (Exception x) {
                    x.printStackTrace();
                    // callback.onError(x.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                // callback.onError(error);
            }
        });
    }

    public void getMyCompanies(final Context context, Socket socket, IRoomsCallback callback) {
        Backend.getInstance().getMyCompaniesBySocket(context, socket, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    TaskRunner runner = new TaskRunner();
                    runner.executeAsync(() -> {
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(Constants.HAS_COMPANY, false).commit();
                        TaskRepository repository = TaskRepository.getInstance((Application) context.getApplicationContext());
                        JSONObject jsonObject = new JSONObject(response);
                        ArrayList<Company> companies = IRoomJsonParser.getCompanies(jsonObject.toString());

                        if (companies.size() > 0) {
                            for (Company company : companies) {
                                if (company.getOwner_id() == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                                    prefs.edit().putBoolean(Constants.HAS_COMPANY, true).commit();
                                    if (prefs.getInt(Constants.SELECTED_COMPANY_ID, -1) == company.getId()) {
                                        prefs.edit().putBoolean(Constants.IS_OWNER, true).commit();
                                    }
                                }
                                repository.insert(company);
                            }
                        } else {
                            setSelectedCompany(context, new Company(), false);
                        }
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                        if (preferences.getString(Constants.SELECTED_COMPANY_NAME, "").equals("") && companies.size() == 1) {
                            IRoomsManager.getInstance().setSelectedCompany(context, companies.get(0), companies.get(0).getOwner_id() == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId);
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

    public void getCompany(Context context, int companyId, IRoomCallback<Company> companyIRoomCallback) {
        TaskRunner taskRunner = new TaskRunner();
        taskRunner.executeAsync(() -> {
            TaskRepository repo = TaskRepository.getInstance((Application) context.getApplicationContext());
            return repo.getCompany(companyId);

        }, companyIRoomCallback::onSuccess);
    }

    public void getMyTasks(Context context, ArrayList<Company> companies, Socket socket, TaskSocketQuery query, IRoomCallback<ArrayList<Task>> callback) {
        TaskRunner runner = new TaskRunner();
        runner.executeAsync(() -> {

            for (Company c : companies) {
                if (c != null) {
                    query.setCompany_id(c.getId());
                    Backend.getInstance().getTasksBySocket(socket, query, new VolleyCallback() {
                        @Override
                        public void onSuccess(String response) {

                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                ArrayList<Task> tasks = IRoomJsonParser.getTasks(jsonObject.toString());

                                if (tasks.size() > 0) {
                                    TaskRunner taskRunner = new TaskRunner();
                                    taskRunner.executeAsync(() -> {
                                        TaskRepository repo = TaskRepository.getInstance((Application) context.getApplicationContext());
                                        long userID = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                                        for (Task task : tasks) {
                                            repo.createOnlineTask(task, userID);
                                        }
                                        return tasks;
                                    }, result -> {
                                    });
                                }
                                if (callback != null) {
                                    callback.onSuccess(tasks);
                                }
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
            }
            return null;
        }, result -> {

        });
    }

    public void setSelectedCompany(Context context, Company company, boolean isOwner) {

        PreferenceManager.getDefaultSharedPreferences(context).
                edit().putString(Constants.SELECTED_COMPANY_NAME, company.getName()).commit();

        PreferenceManager.getDefaultSharedPreferences(context).
                edit().putInt(Constants.SELECTED_COMPANY_ID, company.getId()).commit();

        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(Constants.IS_OWNER, isOwner).commit();

//        try {
//            Socket socket = ((LaunchActivity) context).getmSocket();
//            if (socket != null) {
//                TaskSocketQuery query = new TaskSocketQuery();
//                query.setCompany_id(company.getId());
//                getCompanyTask(context, socket, query, new IRoomsCallback() {
//                    @Override
//                    public void onSuccess(String success) {
//
//                    }
//
//                    @Override
//                    public void onError(String error) {
//
//                    }
//                });
//            }
//        } catch (Exception x) {
//        }
    }

    public String getSelectedCompanyName(Context context) {
        String name = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.SELECTED_COMPANY_NAME, "");
        return name.equals("") ? "" : name;
    }

    public void getCompanyTasks(Context context, int dialog_id, IRoomCallback<ArrayList<Task>> arrayListIRoomCallback) {
        TaskRunner runner = new TaskRunner();
        runner.executeAsync(() -> {
            TaskRepository repository = TaskRepository.getInstance((Application) context.getApplicationContext());
            int companyId = PreferenceManager.getDefaultSharedPreferences(context).getInt(Constants.SELECTED_COMPANY_ID, -1);
            return repository.getCompanyTasks(companyId, dialog_id);
        }, result -> arrayListIRoomCallback.onSuccess(result));
    }

    public void getGroupChatRelatedTasks(Context context, long chatId, IRoomCallback<ArrayList<Task>> arrayListIRoomCallback) {
        TaskRunner runner = new TaskRunner();
        runner.executeAsync(() -> {
                    TaskRepository repository = TaskRepository.getInstance((Application) context.getApplicationContext());
                    long ownerId = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id;
                    repository.getChatRelatedTasks(chatId, arrayListIRoomCallback);
                    return "";
                }, result -> {
                }
        );
    }

    public void getUserTasks(Context context, int selectedAccountUserId, int id, IRoomCallback<ArrayList<Task>> arrayListIRoomCallback) {
        TaskRunner runner = new TaskRunner();
        runner.executeAsync(() -> {
                    TaskRepository repository = TaskRepository.getInstance((Application) context.getApplicationContext());
                    repository.getPrivateChatTasks(selectedAccountUserId, id, arrayListIRoomCallback);
                    return "";
                }, result -> {
                }
        );
    }

    public void getAccountTasks(Context context, int id, IRoomCallback<ArrayList<Task>> arrayListIRoomCallback) {
        TaskRunner runner = new TaskRunner();
        runner.executeAsync(() -> {
                    TaskRepository repository = TaskRepository.getInstance((Application) context.getApplicationContext());
                    repository.getAccountTasks(id, arrayListIRoomCallback);
                    return "";
                }, result -> {
                }
        );
    }

    public void setCompanyRequestAsked(Context context, boolean b) {
        PreferenceManager.getDefaultSharedPreferences(context).
                edit().putBoolean(Constants.COMPANY_REGISTER_REQUEST_ASKED, b).commit();
    }

    public boolean isCompanyCreateRequestBeen(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.COMPANY_REGISTER_REQUEST_ASKED, false);
    }

    public void setDarkTheme(Context context, boolean toDark) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(Constants.DARK_THEME, toDark).apply();
    }

    public boolean isDarkMode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.DARK_THEME, false);
    }

    public interface IRoomsCallback {
        void onSuccess(String success);

        void onError(String error);
    }

    public interface IRoomCallback<R> {

        void onSuccess(R success);

        void onError(String error);
    }

}
