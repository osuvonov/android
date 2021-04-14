package org.telegram.irooms.network;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

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
import org.telegram.irooms.Utils;
import org.telegram.irooms.database.Task;
import org.telegram.irooms.task.TaskSocketQuery;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.Socket;

public class APIClient {

    private APIClient() {
    }

    private static APIClient instance;

    public static APIClient getInstance() {
        if (instance == null) {
            instance = new APIClient();
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
                                    showToast(context, "Error on adding task: " + errorDescription);
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
                showToast(context, error);
            }
        });
    }

    private void showToast(Context context, String message) {
        try {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    private void makeSocketEmit(Socket socket, String eventName, JSONObject postData, final VolleyCallback callback) {
        if (postData != null) {
            if (!socket.connected()) {
                return;
            }
            socket.emit(eventName, postData, (Ack) response -> {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response[0].toString());
                    String success = jsonObject.getString("success");
                    if (success.equals("true")) {
                        callback.onSuccess(response[0].toString());
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
            });
        } else {
            socket.emit(eventName, (Ack) response -> {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response[0].toString());
                    String success = jsonObject.getString("success");
                    if (success.equals("true")) {
                        callback.onSuccess(response[0].toString());
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
            });
        }
    }

    public void createTaskBySocket(Socket socket, Task task, final VolleyCallback callback) {

        JSONObject postData = null;
        try {
            String json = new Gson().toJson(task);
            postData = new JSONObject(json);
            makeSocketEmit(socket, "createTask", postData, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void editTaskBySocket(Socket socket, Task task, final VolleyCallback callback) {

        JSONObject postData = null;
        try {
            String json = new Gson().toJson(task);
            postData = new JSONObject(json);

            makeSocketEmit(socket, "editTask", postData, callback);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getMyCompaniesBySocket(Context context, Socket socket, final VolleyCallback callback) {
        makeSocketEmit(socket, "getUserCompanies", null, callback);
    }

    public void registerCompanyBySocket(Socket socket, String name, final VolleyCallback volleyCallback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            makeSocketEmit(socket, "registerCompany", jsonObject, volleyCallback);

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

    public void addMembersToCompanyBySocket(Context context, Socket socket, int companyId, ArrayList<Integer> members_, final VolleyCallback callback) {
        AddMemberToCompanyObject company = new AddMemberToCompanyObject();
        company.company_id = companyId;
        company.members = members_;
        JSONObject postData = null;
        try {
            String json = new Gson().toJson(company);
            postData = new JSONObject(json);
            makeSocketEmit(socket, "addCompanyMembers", postData, callback);
        } catch (Exception x) {
        }
    }

    public void deleteMembersFromCompanyBySocket(Context context, Socket socket, int companyId, ArrayList<Integer> members_, final VolleyCallback callback) {
        AddMemberToCompanyObject company = new AddMemberToCompanyObject();
        company.company_id = companyId;
        company.members = members_;

        JSONObject postData;
        try {
            String json = new Gson().toJson(company);
            postData = new JSONObject(json);
            makeSocketEmit(socket, "deleteCompanyMembers", postData, callback);
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
                            long recordId = preferences.getLong("record_id", -1);

                            APIClient.getInstance().subscribeTaskNotification(context, subscToken, recordId, new VolleyCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        long recordId = jsonObject.optLong("result");
                                        if (recordId > 0) {
                                            preferences.edit().putLong("record_id", recordId).commit();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
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
                            showToast(context, "Error on login: " + errorDesc);
                        }

                    } catch (Exception e) {
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

    public void subscribeTaskNotification(Context context, String subscripttoken, long recorID, final VolleyCallback callback) {

        String hostNameCloud = BASE_URL + "/push-subscription/save";
        if (recorID > 0) {
            hostNameCloud = BASE_URL + "/push-subscription/update";
        }

        try {
            JSONObject postData = new JSONObject();

            JSONObject tokenJson = new JSONObject();

            tokenJson.put("token", subscripttoken);

            postData.put("subscription", tokenJson);

            if (recorID > 0) {
                postData.put("record_id", recorID);
            }

            makePostRequest(context, postData, hostNameCloud, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteSubscription(Context context,String token, long recordID, final VolleyCallback callback) {

        String hostNameCloud = BASE_URL + "/push-subscription/delete";

        try {
            JSONObject postData = new JSONObject();

            postData.put("record_id", recordID);
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
                                showToast(context, "Error on adding task: " + errorDescription);
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
         } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getTasksBySocket(Socket socket, TaskSocketQuery query, final VolleyCallback callback) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("company_id", query.getCompany_id());
            jsonObject.put("chat_id", query.getChat_id());
            jsonObject.put("limit", query.getLimit());
            jsonObject.put("offset", query.getOffset());
            jsonObject.put("order_by", query.getOrder_by());
            jsonObject.put("chat_type", query.getChat_type());
            jsonObject.put("from_date", query.getFrom_date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        makeSocketEmit(socket, "getTasks", jsonObject, callback);
    }

    public void getTask(Context context, long taskId, final VolleyCallback callback) {
        String url = BASE_URL + "/tasks/" + taskId;
        makeGetRequest(context, url, callback);
    }

    public void getToken(Context context, TokenListener tokenListener) {
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
                tokenListener.onToken(token);

            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }
        } else {
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
        }
    }

    public interface TokenListener {
        void onToken(String token);

        void onError(String error);
    }
}
