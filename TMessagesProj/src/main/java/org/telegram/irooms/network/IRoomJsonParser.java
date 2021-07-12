package org.telegram.irooms.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.irooms.database.Task;
import org.telegram.irooms.models.TaskMessage;
import org.telegram.irooms.models.ThreadInfo;

import java.util.ArrayList;

public class IRoomJsonParser {

    public static ArrayList<Task> getTasks(String tasks) {
        ArrayList<Task> taskList = new ArrayList<>();
        try {

            JSONObject jsonObject = new JSONObject(tasks);

            JSONArray taskArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < taskArray.length(); i++) {
                try {
                    JSONObject jsonTask = taskArray.getJSONObject(i);

                    long id = jsonTask.getLong("id");
                    String description = jsonTask.getString("description");
                    String logo = !jsonTask.isNull("logo") ? jsonTask.getString("logo") : "";
                    long creator_id = !jsonTask.isNull("creator_id") ? jsonTask.getLong("creator_id") : 0;
                    long lastUpdater = !jsonTask.isNull("last_updated_by") ? jsonTask.getLong("last_updated_by") : 0;
                     long chat_id = !jsonTask.isNull("chat_id") ? jsonTask.getLong("chat_id") : 0;
                    long message_id = !jsonTask.isNull("message_id") ? jsonTask.getLong("message_id") : -1;
                    String created_at = !jsonTask.isNull("created_at") ? jsonTask.getString("created_at") : "";
                    String updated_at = !jsonTask.isNull("updated_at") ? jsonTask.getString("updated_at") : "";
                    String expires_at = !jsonTask.isNull("expires_at") ? jsonTask.getString("expires_at") : "";
                    String completed_at = !jsonTask.isNull("completed_at") ? jsonTask.getString("completed_at") : "";
                    String localID = !jsonTask.isNull("local_id") ? jsonTask.getString("local_id") : "";
                    String status = !jsonTask.isNull("status") ? jsonTask.getString("status") : "";
                    String chatType = !jsonTask.isNull("chat_type") ? jsonTask.getString("chat_type") : "";
                    String platform = !jsonTask.isNull("platform") ? jsonTask.getString("platform") : "";
                    int commentsCount = jsonTask.optInt("comments_count");

                    int status_code = !jsonTask.isNull("status_code") ? jsonTask.getInt("status_code") : 0;
                    ArrayList<Integer> members =
                            new Gson().fromJson(jsonTask.getString("members"), new TypeToken<ArrayList<Integer>>() {
                            }.getType());
                    ArrayList<String> reminders =
                            new Gson().fromJson(jsonTask.optString("reminders"), new TypeToken<ArrayList<String>>() {
                            }.getType());
                    if (reminders==null){
                        reminders=new ArrayList<>();
                    }
                    Task task = new Task(id);
                    task.setChatId(chat_id);
                    task.setCompletedAt(completed_at);
                    task.setCreatedAt(created_at);
                    task.setMessageId(message_id);
                    task.setDescription(description);
                    task.setUpdatedAt(updated_at);
                    task.setExpiresAt(expires_at);
                    task.setCreatorId(creator_id);
                    task.setLogo(logo);
                    task.setMembers(members);
                    task.setStatus_code(status_code);
                    task.setStatus(status);
                    task.setLocal_id(localID);
                    task.setLocalStatus(3);
                    task.setLastUpdater(lastUpdater);
                    task.setChat_type(chatType);
                    task.setPlatform(platform);
                    task.setComments_count(commentsCount);
                    task.setReminders(reminders);

                    taskList.add(task);

                } catch (Exception x) {
                    x.printStackTrace();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return taskList;
    }

    public static Task getTask(String taskjson, boolean edited) {
        try {
            JSONObject jsonObject = new JSONObject(taskjson);

            JSONObject jsonTask;
            if (edited) {
                jsonTask = jsonObject;
            } else {
                jsonTask = jsonObject.getJSONObject("result");
            }

            long id = jsonTask.getLong("id");
            String description = jsonTask.getString("description");
            String logo = !jsonTask.isNull("logo") ? jsonTask.getString("logo") : "";
            long creator_id = !jsonTask.isNull("creator_id") ? jsonTask.getLong("creator_id") : 0;
            long lastUpdater = !jsonTask.isNull("last_updated_by") ? jsonTask.getLong("last_updated_by") : 0;
            long chat_id = !jsonTask.isNull("chat_id") ? jsonTask.getLong("chat_id") : 0;
            long message_id = !jsonTask.isNull("message_id") ? jsonTask.getLong("message_id") : -1;
            String created_at = !jsonTask.isNull("created_at") ? jsonTask.getString("created_at") : "";
            String updated_at = !jsonTask.isNull("updated_at") ? jsonTask.getString("updated_at") : "";
            String expires_at = !jsonTask.isNull("expires_at") ? jsonTask.getString("expires_at") : "";
            String completed_at = !jsonTask.isNull("completed_at") ? jsonTask.getString("completed_at") : "";
            String localID = !jsonTask.isNull("local_id") ? jsonTask.getString("local_id") : "";
            String status = !jsonTask.isNull("status") ? jsonTask.getString("status") : "";
            String chatType = !jsonTask.isNull("chat_type") ? jsonTask.getString("chat_type") : "";
            String platform = !jsonTask.isNull("platform") ? jsonTask.getString("platform") : "";
            int commentsCount = jsonTask.optInt("comments_count");

            int status_code = !jsonTask.isNull("status_code") ? jsonTask.getInt("status_code") : 0;
            ArrayList<Integer> members =
                    new Gson().fromJson(jsonTask.getString("members"), new TypeToken<ArrayList<Integer>>() {
                    }.getType());

            ArrayList<String> reminders =
                    new Gson().fromJson(jsonTask.optString("reminders"), new TypeToken<ArrayList<String>>() {
                    }.getType());
            if (reminders==null){
                reminders=new ArrayList<>();
            }
            Task task = new Task(id);
            task.setChatId(chat_id);
            task.setCompletedAt(completed_at);
            task.setCreatedAt(created_at);
            task.setMessageId(message_id);
            task.setDescription(description);
            task.setUpdatedAt(updated_at);
            task.setExpiresAt(expires_at);
            task.setCreatorId(creator_id);
            task.setLogo(logo);
            task.setPlatform(platform);
            task.setStatus_code(status_code);
            task.setStatus(status);
            task.setMembers(members);
            task.setLastUpdater(lastUpdater);
            task.setChat_type(chatType);
            task.setComments_count(commentsCount);
            task.setReminders(reminders);

            task.setLocal_id(localID);
            return task;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TaskMessage getTaskMessage(String messagejson) {
        try {
            JSONObject jsonObject = new JSONObject(messagejson);

            JSONObject jsonTaskMessage;

            jsonTaskMessage = jsonObject.optJSONObject("result");
            if (jsonTaskMessage == null) {
                jsonTaskMessage = jsonObject;
            }


            long id = jsonTaskMessage.getLong("id");
            int from_id = !jsonTaskMessage.isNull("from_id") ? jsonTaskMessage.getInt("from_id") : 0;
            long task_id = !jsonTaskMessage.isNull("task_id") ? jsonTaskMessage.getLong("task_id") : 0;
            long reply_to = !jsonTaskMessage.isNull("reply_to") ? jsonTaskMessage.getLong("reply_to") : 0;
            String date = !jsonTaskMessage.isNull("date") ? jsonTaskMessage.getString("date") : "";
            String edit_date = !jsonTaskMessage.isNull("edit_date") ? jsonTaskMessage.getString("edit_date") : "";
            String text = !jsonTaskMessage.isNull("text") ? jsonTaskMessage.getString("text") : "";

            TaskMessage message = new TaskMessage();
            message.setId(id);
            message.setDate(date);
            message.setEdit_date(edit_date);
            message.setFrom_id(from_id);
            message.setReply_to(reply_to);
            message.setTask_id(task_id);
            message.setText(text);
            return message;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static ArrayList<TaskMessage> getTaskMessages(String response) {
        ArrayList<TaskMessage> messages = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTaskMessage;

                jsonTaskMessage = jsonArray.getJSONObject(i);


                long id = jsonTaskMessage.getLong("id");
                int from_id = !jsonTaskMessage.isNull("from_id") ? jsonTaskMessage.getInt("from_id") : 0;
                long task_id = !jsonTaskMessage.isNull("task_id") ? jsonTaskMessage.getLong("task_id") : 0;
                long reply_to = !jsonTaskMessage.isNull("reply_to") ? jsonTaskMessage.getLong("reply_to") : 0;
                String date = !jsonTaskMessage.isNull("date") ? jsonTaskMessage.getString("date") : "";
                String edit_date = !jsonTaskMessage.isNull("edit_date") ? jsonTaskMessage.getString("edit_date") : "";
                String text = !jsonTaskMessage.isNull("text") ? jsonTaskMessage.getString("text") : "";

                TaskMessage message = new TaskMessage();
                message.setId(id);
                message.setDate(date);
                message.setEdit_date(edit_date);
                message.setFrom_id(from_id);
                message.setReply_to(reply_to);
                message.setTask_id(task_id);
                message.setText(text);

                messages.add(message);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public static ArrayList<ThreadInfo> getThreadInfos(String response) {
        ArrayList<ThreadInfo> messages = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTaskMessage;

                jsonTaskMessage = jsonArray.getJSONObject(i);


                long id = jsonTaskMessage.optLong("last_read_message_id");
                long task_id = jsonTaskMessage.optLong("task_id");

                ThreadInfo message = new ThreadInfo();
                message.setTask_id(task_id);
                message.setLast_read_message_id(id);

                messages.add(message);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return messages;

    }

    public static ThreadInfo getThreadInfo(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject threadI = jsonObject.getJSONObject("result");

            long id = threadI.optLong("last_read_message_id");
            long task_id = threadI.optLong("task_id");

            ThreadInfo threadInfo = new ThreadInfo();
            threadInfo.setTask_id(task_id);
            threadInfo.setLast_read_message_id(id);
            return threadInfo;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ThreadInfo();
    }

}
