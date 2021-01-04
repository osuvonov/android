package org.telegram.irooms;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.exoplayer2.util.Log;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.irooms.database.Company;
import org.telegram.irooms.database.Task;
import org.telegram.irooms.network.Backend;
import org.telegram.irooms.network.IRoomJsonParser;
import org.telegram.irooms.network.VolleyCallback;
import org.telegram.irooms.task.TaskRepository;
import org.telegram.irooms.task.TaskRunner;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.concurrent.Callable;


public class IRoomsManager {

    private static IRoomsManager instance;
    private String editUrl = "https://irooms.io/edit";

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

    public void createCompany(Context context, String name, IRoomsCallback callback) {
        Backend.getInstance().createCompany(context, name, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    new TaskRunner().executeAsync(() -> {
                        TaskRepository taskRepository = new TaskRepository((Application) context.getApplicationContext());

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
        runner.executeAsync(new Callable<ArrayList<Company>>() {
            @Override
            public ArrayList<Company> call() throws Exception {
                TaskRepository repository = new TaskRepository((Application) context.getApplicationContext());
                return repository.getCompanyList();
            }
        }, new TaskRunner.TaskCompletionListener<ArrayList<Company>>() {
            @Override
            public void onComplete(ArrayList<Company> result) {
                callback.onSuccess(new Gson().toJson(result));
            }
        });
    }

    public void addMembersToCompany(Context context, int companyId, ArrayList<Integer> members, IRoomsCallback callback) {
        Backend.getInstance().addMembersToCompany(context, companyId, members, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    new TaskRunner().executeAsync(() -> {
                        TaskRepository taskRepository = new TaskRepository((Application) context.getApplicationContext());

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

    public void deleteMembersFromCompany(Context context, int companyId, ArrayList<Integer> members, IRoomsCallback callback) {
        Backend.getInstance().deleteMembersFromCompany(context, companyId, members, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    new TaskRunner().executeAsync(() -> {
                        TaskRepository taskRepository = new TaskRepository((Application) context.getApplicationContext());

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

    public void createTask(Context context, Task task, IRoomsCallback callback) {
        Backend.getInstance().createTask(context, task, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    new TaskRunner().executeAsync(() -> {

                        Task myTask = IRoomJsonParser.getTask(response, false);

                        TaskRepository taskRepository = new TaskRepository((Application) context.getApplicationContext());

                        taskRepository.insert(myTask);
                        return myTask;
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

    public void editTask(Context context, Task task, IRoomsCallback callback) {
        Backend.getInstance().editTask(context, task, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    new TaskRunner().executeAsync(() -> {

                        Task myTask = IRoomJsonParser.getTask(response, false);

                        TaskRepository taskRepository = new TaskRepository((Application) context.getApplicationContext());

                        taskRepository.update(myTask);
                        return myTask;
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

    public void getMyCompanies(final Context context, IRoomsCallback callback) {
        Backend.getInstance().getMyCompanies(context, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    TaskRunner runner = new TaskRunner();
                    runner.executeAsync(() -> {
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(Constants.HAS_COMPANY, false).commit();
                        TaskRepository repository = new TaskRepository((Application) context.getApplicationContext());
                        JSONObject jsonObject = new JSONObject(response);
                        ArrayList<Company> companies = IRoomJsonParser.getCompanies(jsonObject.toString());

                        if (companies.size() > 0) {
                            for (Company company : companies) {
                                if (company.getOwner_id() == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
                                    PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(Constants.HAS_COMPANY, true).commit();
                                }
                                repository.insert(company);
                            }
                        } else {
                            setSelectedCompany(context, new Company(), false);
                        }
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                        if (preferences.getString(Constants.SELECTED_COMPANY_NAME, "") .equals("")&&companies.size()==1) {
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
            TaskRepository repo = new TaskRepository((Application) context.getApplicationContext());
            return repo.getCompany(companyId);

        }, companyIRoomCallback::onSuccess);
    }

    public void getMyTasks(Context context, IRoomsCallback callback) {
        Backend.getInstance().getTasks(context, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    ArrayList<Task> tasks = IRoomJsonParser.getTasks(jsonObject.toString());
                    if (tasks.size() > 0) {
                        TaskRunner taskRunner = new TaskRunner();
                        taskRunner.executeAsync(() -> {
                            TaskRepository repo = new TaskRepository((Application) context.getApplicationContext());
                            for (int i = 0; i < tasks.size(); i++) {
                                repo.insert(tasks.get(i));
                            }
                            return tasks;
                        }, result -> {
                        });
                    }
                    callback.onSuccess(response);
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

    public void getTask(Context context, long taskId, IRoomCallback<Task> callback) {
        Backend.getInstance().getTask(context, taskId, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    Task task = IRoomJsonParser.getTask(jsonObject.toString(), false);

                    if (task != null) {
                        callback.onSuccess(task);
                        TaskRunner taskRunner = new TaskRunner();
                        taskRunner.executeAsync(() -> {
                            TaskRepository repo = new TaskRepository((Application) context.getApplicationContext());
                            repo.insert(task);
                            return task;
                        }, result -> {

                        });
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

    public void setSelectedCompany(Context context, Company company, boolean isOwner) {

        PreferenceManager.getDefaultSharedPreferences(context).
                edit().putString(Constants.SELECTED_COMPANY_NAME, company.getName()).commit();

        PreferenceManager.getDefaultSharedPreferences(context).
                edit().putInt(Constants.SELECTED_COMPANY_ID, company.getId()).commit();

        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(Constants.IS_OWNER, isOwner).commit();
    }

    public String getSelectedCompanyName(Context context) {
        String name = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.SELECTED_COMPANY_NAME, "");
        return name.equals("") ? "" : name;
    }

    public void getCompanyTasks(Context context, int dialog_id, IRoomCallback<ArrayList<Task>> arrayListIRoomCallback) {
        TaskRunner runner = new TaskRunner();
        runner.executeAsync(() -> {
            TaskRepository repository = new TaskRepository((Application) context.getApplicationContext());
            int companyId = PreferenceManager.getDefaultSharedPreferences(context).getInt(Constants.SELECTED_COMPANY_ID, -1);
            return repository.getCompanyTasks(companyId, dialog_id);
        }, result -> arrayListIRoomCallback.onSuccess(result));
    }

    public void getChatRelatedTasks(Context context, IRoomCallback<ArrayList<Task>> arrayListIRoomCallback) {
        TaskRunner runner = new TaskRunner();
        runner.executeAsync(() -> {
            TaskRepository repository = new TaskRepository((Application) context.getApplicationContext());
            return repository.getChatRelatedTasks();
        }, result -> arrayListIRoomCallback.onSuccess(result));
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
