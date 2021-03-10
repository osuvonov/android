package org.telegram.irooms.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.irooms.database.Company;
import org.telegram.irooms.database.Task;

import java.util.ArrayList;

public class IRoomJsonParser {

    public static ArrayList<Company> getCompanies(String companies) {
        ArrayList<Company> companyList = new ArrayList<>();
        try {

            JSONObject jsonObject = new JSONObject(companies);
            JSONArray companyArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < companyArray.length(); i++) {
                JSONObject jsonCompany = companyArray.getJSONObject(i);
                int id = jsonCompany.getInt("id");
                String name = jsonCompany.getString("name");
                String logo = jsonCompany.getString("logo");
                int owner_id = jsonCompany.getInt("owner_id");
                String created_at = jsonCompany.getString("created_at");
                ArrayList<Integer> members =
                        new Gson().fromJson(jsonCompany.getString("members"), new TypeToken<ArrayList<Integer>>() {
                        }.getType());

                Company company = new Company(id, name);
                company.setCreatedAt(created_at);
                company.setLogo(logo == null ? "" : logo);
                company.setOwnerId(owner_id);
                company.setMembers(members);
                companyList.add(company);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return companyList;
    }

    public static ArrayList<Company> getCompaniesFromSocketAuth(String json) {
        ArrayList<Company> companyList = new ArrayList<>();
        try {

            JSONObject jsonObject = new JSONObject(json);
            JSONObject companies = jsonObject.getJSONObject("result");
            JSONArray companyArray = companies.getJSONArray("companies");

            for (int i = 0; i < companyArray.length(); i++) {
                JSONObject jsonCompany = companyArray.getJSONObject(i);
                int id = jsonCompany.getInt("id");
                String name = jsonCompany.getString("name");
                String logo = jsonCompany.getString("logo");
                int owner_id = jsonCompany.getInt("owner_id");
                String created_at = jsonCompany.getString("created_at");
                ArrayList<Integer> members =
                        new Gson().fromJson(jsonCompany.getString("members"), new TypeToken<ArrayList<Integer>>() {
                        }.getType());

                Company company = new Company(id, name);
                company.setCreatedAt(created_at);
                company.setLogo(logo == null ? "" : logo);
                company.setOwnerId(owner_id);
                company.setMembers(members);
                companyList.add(company);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return companyList;
    }

    public static Company getCompany(String args) {
        try {
            JSONObject jsonCompany = new JSONObject(args);
            int id = jsonCompany.getInt("id");
            String name = jsonCompany.getString("name");
            String logo = jsonCompany.getString("logo");
            int owner_id = jsonCompany.getInt("owner_id");
            String created_at = jsonCompany.getString("created_at");
            ArrayList<Integer> members =
                    new Gson().fromJson(jsonCompany.getString("members"), new TypeToken<ArrayList<Integer>>() {
                    }.getType());
            Company company = new Company(id, name);
            company.setCreatedAt(created_at);
            company.setLogo(logo == null ? "" : logo);
            company.setOwnerId(owner_id);
            company.setMembers(members);
            return company;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Task> getTasks(String tasks) {
        ArrayList<Task> taskList = new ArrayList<>();
        try {

            JSONObject jsonObject = new JSONObject(tasks);

            JSONArray taskArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < taskArray.length(); i++) {

                JSONObject jsonTask = taskArray.getJSONObject(i);

                long id = jsonTask.getLong("id");
                String description = jsonTask.getString("description");
                String logo = !jsonTask.isNull("logo") ? jsonTask.getString("logo") : "";
                long creator_id = !jsonTask.isNull("creator_id") ? jsonTask.getLong("creator_id") : 0;
                long lastUpdater = !jsonTask.isNull("last_updated_by") ? jsonTask.getLong("last_updated_by") : 0;
                long company_id = !jsonTask.isNull("company_id") ? jsonTask.getLong("company_id") : 0;
                long chat_id = !jsonTask.isNull("chat_id") ? jsonTask.getLong("chat_id") : 0;
                long message_id = !jsonTask.isNull("message_id") ? jsonTask.getLong("message_id") : -1;
                String created_at = !jsonTask.isNull("created_at") ? jsonTask.getString("created_at") : "";
                String updated_at = !jsonTask.isNull("updated_at") ? jsonTask.getString("updated_at") : "";
                String expires_at = !jsonTask.isNull("expires_at") ? jsonTask.getString("expires_at"):"";
                String completed_at = !jsonTask.isNull("completed_at") ? jsonTask.getString("completed_at") : "";
                String localID = !jsonTask.isNull("local_id") ? jsonTask.getString("local_id") : "";
                String status =  !jsonTask.isNull("status") ? jsonTask.getString("status"):"";
                int status_code =  !jsonTask.isNull("status_code") ? jsonTask.getInt("status_code"):0;
                ArrayList<Integer> members =
                        new Gson().fromJson(jsonTask.getString("members"), new TypeToken<ArrayList<Integer>>() {
                        }.getType());

                Task task = new Task(id, company_id);
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
                taskList.add(task);
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
            long company_id = !jsonTask.isNull("company_id") ? jsonTask.getLong("company_id") : 0;
            long chat_id = !jsonTask.isNull("chat_id") ? jsonTask.getLong("chat_id") : 0;
            long message_id = !jsonTask.isNull("message_id") ? jsonTask.getLong("message_id") : -1;
            String created_at = !jsonTask.isNull("created_at") ? jsonTask.getString("created_at") : "";
            String updated_at = !jsonTask.isNull("updated_at") ? jsonTask.getString("updated_at") : "";
            String expires_at = !jsonTask.isNull("expires_at")? jsonTask.getString("expires_at"):"" ;
            String completed_at = !jsonTask.isNull("completed_at") ? jsonTask.getString("completed_at") : "";
            String localID = !jsonTask.isNull("local_id") ? jsonTask.getString("local_id") : "";
            String status =  !jsonTask.isNull("status") ? jsonTask.getString("status"):"";
            int status_code =  !jsonTask.isNull("status_code") ? jsonTask.getInt("status_code"):0;
            ArrayList<Integer> members =
                    new Gson().fromJson(jsonTask.getString("members"), new TypeToken<ArrayList<Integer>>() {
                    }.getType());

            Task task = new Task(id, company_id);
            task.setChatId(chat_id);
            task.setCompletedAt(completed_at);
            task.setCreatedAt(created_at);
            task.setMessageId(message_id);
            task.setDescription(description);
            task.setUpdatedAt(updated_at);
            task.setExpiresAt(expires_at);
            task.setCreatorId(creator_id);
            task.setLogo(logo);
            task.setStatus_code(status_code);
            task.setStatus(status);
            task.setMembers(members);
            task.setLastUpdater(lastUpdater);

            task.setLocal_id(localID);
            return task;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Integer> getAddedMembersToTeam(String json) {
        ArrayList<Integer> members = new ArrayList<>();
        try {
            JSONObject jsonCompany = new JSONObject(json);
            String success = jsonCompany.getString("success");
            if (success.equals("true")) {
                ArrayList<Integer> teamMembers =
                        new Gson().fromJson(jsonCompany.getString("result"), new TypeToken<ArrayList<Integer>>() {
                        }.getType());
                return teamMembers == null ? members : teamMembers;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return members;
    }
}
