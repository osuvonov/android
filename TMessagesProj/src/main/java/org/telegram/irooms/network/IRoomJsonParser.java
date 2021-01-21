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
                ArrayList<Long> members =
                        new Gson().fromJson(jsonCompany.getString("members"), new TypeToken<ArrayList<Long>>() {
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
                ArrayList<Long> members =
                        new Gson().fromJson(jsonCompany.getString("members"), new TypeToken<ArrayList<Long>>() {
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
            ArrayList<Long> members =
                    new Gson().fromJson(jsonCompany.getString("members"), new TypeToken<ArrayList<Long>>() {
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
                String logo = jsonTask.opt("logo") != null ? jsonTask.getString("logo") : "";
                long creator_id = jsonTask.opt("creator_id") != null ? jsonTask.getLong("creator_id") : 0;
                long lastUpdater = jsonTask.opt("last_updated_by") != null ? jsonTask.getLong("last_updated_by") : 0;

                long company_id = jsonTask.opt("company_id") != null ? jsonTask.getLong("company_id") : 0;

                long chat_id = jsonTask.getLong("chat_id");
                long message_id = jsonTask.getLong("message_id");

                String created_at = jsonTask.getString("created_at");
                String updated_at = jsonTask.getString("updated_at");
                String expires_at = jsonTask.getString("expires_at").equals("null") ? "" : jsonTask.getString("expires_at");
                String completed_at = jsonTask.getString("completed_at");
                String localID = jsonTask.getString("local_id");

                String status = jsonTask.getString("status");
                int status_code = jsonTask.getInt("status_code");
                ArrayList<Long> members =
                        new Gson().fromJson(jsonTask.getString("members"), new TypeToken<ArrayList<Long>>() {
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

            JSONObject taskObject;
            if (edited) {
                taskObject = jsonObject;
            } else {
                taskObject = jsonObject.getJSONObject("result");
            }

            long id = taskObject.getLong("id");
            String description = taskObject.getString("description");
            String logo = taskObject.opt("logo") != null ? taskObject.getString("logo") : "";
            long creator_id = taskObject.opt("creator_id") != null ? taskObject.getLong("creator_id") : 0;
            long lastUpdater = taskObject.opt("last_updated_by") != null ? taskObject.getLong("last_updated_by") : 0;
            long company_id = taskObject.opt("company_id") != null ? taskObject.getLong("company_id") : 0;

            long chat_id = taskObject.getLong("chat_id");
            long message_id = taskObject.getLong("message_id");

            String created_at = taskObject.getString("created_at");
            String updated_at = taskObject.getString("updated_at");
            String expires_at = taskObject.getString("expires_at").equals("null") ? "" : taskObject.getString("expires_at");
            String completed_at = taskObject.getString("completed_at");
            String status = taskObject.getString("status");
            String localID = taskObject.getString("local_id");
            int status_code = taskObject.getInt("status_code");
            ArrayList<Long> members =
                    new Gson().fromJson(taskObject.getString("members"), new TypeToken<ArrayList<Long>>() {
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
}
