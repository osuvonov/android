package org.telegram.irooms.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.exoplayer2.util.Log;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.irooms.Constants;
import org.telegram.irooms.IRoomsManager;
import org.telegram.irooms.database.Task;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Backend {

    private Backend() {
    }

    private static Backend instance;

    public static Backend getInstance() {
        if (instance == null) {
            instance = new Backend();
        }
        return instance;
    }

    private static String BASE_URL = "https://api.irooms.io";

    private void makeGetRequest(Context context, String hostNameCloud, VolleyCallback callback) {

        getToken(context, new TokenListener() {
            @Override
            public void onToken(String token) {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, hostNameCloud, null,
                        response -> {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                String success = jsonObject.getString("success");
                                if (success.equals("true")) {
                                    callback.onSuccess(response.toString());
                                } else {
                                    JSONObject error = jsonObject.opt("error") == null ? null : jsonObject.getJSONObject("error");
                                    String errorDescription = "Unknown error";
                                    if (error != null) {
                                        errorDescription = error.getString("description");
                                    }
                                    callback.onError(errorDescription);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                    handleError(context, error);
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Accept", "application/json");
                        headers.put("Content-Type", "application/json");
                        headers.put("Api-Key", Constants.API_KEY);
                        headers.put("Authorization", "Bearer " + token);
                        return headers;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context);

                requestQueue.add(jsonObjectRequest);
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void makePostRequest(Context context, JSONObject postData, String hostNameCloud, final VolleyCallback callback) {

        getToken(context, new TokenListener() {
            @Override
            public void onToken(String token) {
                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, hostNameCloud, postData,
                        response -> {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                String success = jsonObject.getString("success");
                                if (success.equals("true")) {
                                    callback.onSuccess(response.toString());
                                } else {
                                    JSONObject error = jsonObject.opt("error") == null ? null : jsonObject.getJSONObject("error");
                                    String errorDescription = "Unknown error";
                                    if (error != null) {
                                        errorDescription = error.getString("description");
                                    }
                                    callback.onError(errorDescription);
                                    Toast.makeText(context, "Error on adding task: " + errorDescription, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                    handleError(context, error);
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Accept", "application/json");
                        headers.put("Content-Type", "application/json");
                        headers.put("Api-Key", Constants.API_KEY);
                        headers.put("Authorization", "Bearer " + token);
                        return headers;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context);

                requestQueue.add(postRequest);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void createTask(Context context, Task task, final VolleyCallback callback) {

        String hostNameCloud = BASE_URL + "/tasks/create";

        JSONObject postData = null;
        try {
            postData = new JSONObject(new Gson().toJson(task));

            makePostRequest(context, postData, hostNameCloud, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class EditTaskObject {
        private long task_id;
        private String description;
        private int status_code;
        private String status;
        private ArrayList<Integer> members;
        private String expires_at;
    }

    public void editTask(Context context, Task task, final VolleyCallback callback) {

        String hostNameCloud = BASE_URL + "/tasks/edit";

        JSONObject postData = null;
        try {
            EditTaskObject editTask = new EditTaskObject();
            editTask.task_id = task.getId();
            editTask.description = task.getDescription();
            editTask.members = (ArrayList<Integer>) task.getMembers();
            editTask.status = task.getStatus();
            editTask.status_code = task.getStatus_code();
            editTask.expires_at = task.getExpires_at();

            postData = new JSONObject(new Gson().toJson(editTask));

            makePostRequest(context, postData, hostNameCloud, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getMyCompanies(Context context, final VolleyCallback callback) {

        String hostNameCloud = BASE_URL + "/companies";

        makeGetRequest(context, hostNameCloud, callback);
    }

    public void createCompany(Context context, String companyName, final VolleyCallback callback) {

        String hostNameCloud = BASE_URL + "/companies/register";

        JSONObject postData = new JSONObject();
        try {
            postData.put("name", companyName);
            makePostRequest(context, postData, hostNameCloud, callback);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class AddMemberToCompanyObject {

        private int company_id;

        private ArrayList<Integer> members;

        public int getCompany_id() {
            return company_id;
        }

        public void setCompany_id(int company_id) {
            this.company_id = company_id;
        }

        public ArrayList<Integer> getMembers() {
            return members;
        }

        public void setMembers(ArrayList<Integer> members) {
            this.members = members;
        }
    }

    public void addMembersToCompany(Context context, int companyId, ArrayList<Integer> members_, final VolleyCallback callback) {

        String hostNameCloud = BASE_URL + "/companies/add-members";

        AddMemberToCompanyObject company = new AddMemberToCompanyObject();
        company.company_id = companyId;
        company.members = members_;

        JSONObject postData = null;
        try {
            postData = new JSONObject(new Gson().toJson(company));

            makePostRequest(context, postData, hostNameCloud, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteMembersFromCompany(Context context, int companyId, ArrayList<Integer> members_, final VolleyCallback callback) {

        String hostNameCloud = BASE_URL + "/companies/delete-members";

        AddMemberToCompanyObject company = new AddMemberToCompanyObject();
        company.company_id = companyId;
        company.members = members_;

        JSONObject postData = null;
        try {
            postData = new JSONObject(new Gson().toJson(company));

            makePostRequest(context, postData, hostNameCloud, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleError(Context context, VolleyError error) {
        try {
            if (error.networkResponse.statusCode != 200) {
                IRoomsManager.getInstance().authenticate(context, new IRoomsManager.IRoomsCallback() {
                    @Override
                    public void onSuccess(String success) {
                        Log.d("RESPONSE", success);
                    }

                    @Override
                    public void onError(String error) {
                        Log.d("RESPONSE", error);
                    }
                });
            }
            Log.e("TASK NETWORK ERROR: ", error.networkResponse.statusCode + "");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void loginToRooms(Context context, final VolleyCallback callback) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String hostNameCloud = BASE_URL + "/auth/signin";

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        TLRPC.User user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        JSONObject postData = new JSONObject();
        try {
            postData.put("user_id", user.id);
            postData.put("first_name", user.first_name);
            postData.put("phone_number", user.phone);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, hostNameCloud, postData,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        String success = jsonObject.getString("success");
                        if (success.equals("true")) {
                            String token = jsonObject.getString("result");
                            preferences.edit().putString("token" + UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone, token).commit();
                            callback.onSuccess(response.toString());
                            String subscToken = preferences.getString("subscription_token", "");
                            Backend.getInstance().subscribeTaskNotification(context, subscToken, token, new VolleyCallback() {
                                @Override
                                public void onSuccess(String response) {
                                }

                                @Override
                                public void onError(String error) {
                                }
                            });

                        } else {
                            String errorDesc = "Unknown error occurred ";
                            JSONObject error = jsonObject.opt("error") == null ? null : jsonObject.getJSONObject("error");
                            if (error != null) {
                                errorDesc = error.getString("description");
                            }
                            callback.onError(errorDesc);
                            Toast.makeText(context, "Error on login: " + errorDesc, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            handleError(context, error);
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Api-Key", Constants.API_KEY);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public void subscribeTaskNotification(Context context, String subscripttoken, String token, final VolleyCallback callback) {

        String hostNameCloud = BASE_URL + "/push-subscription/save";

        try {
            JSONObject postData = new JSONObject();

            JSONObject tokenJson = new JSONObject();

            tokenJson.put("token", subscripttoken);

            postData.put("subscription", tokenJson);

            makePostRequest(context, postData, hostNameCloud, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getTasks(Context context, final VolleyCallback callback) {

        String url = BASE_URL + "/tasks?full=true";

        makeGetRequest(context, url, callback);
    }

    public void getTask(Context context, long taskId, final VolleyCallback callback) {

        String url = BASE_URL + "/tasks/" + taskId;

        makeGetRequest(context, url, callback);
    }

    private void getToken(Context context, TokenListener tokenListener) {
        TLRPC.User user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        if (user == null) {
            return;
        }
        String token = PreferenceManager.getDefaultSharedPreferences(context).getString("token" + user.phone, "");

        if (token != null && token.length() > 0) {
            try {
                String ar = token.split("\\.")[1];
                byte[] data = Base64.decode(ar, Base64.DEFAULT);
                String plain = new String(data, "UTF-8");
                JSONObject jsonObject = new JSONObject(plain);
                long expiration = jsonObject.getLong("exp");

                if (expiration != 0 && expiration <= System.currentTimeMillis() / 1000) {

                    loginToRooms(context, new VolleyCallback() {
                        @Override
                        public void onSuccess(String response) {
                            String token = PreferenceManager.getDefaultSharedPreferences(context).getString("token" + UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone, "");
                            tokenListener.onToken(token);
                        }

                        @Override
                        public void onError(String error) {
                            tokenListener.onError(error);
                        }
                    });
                    return;
                }
            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }
        }


        tokenListener.onToken(token);
    }

    private interface TokenListener {
        void onToken(String token);

        void onError(String error);
    }
}
