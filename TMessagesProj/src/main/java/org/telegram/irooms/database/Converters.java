package org.telegram.irooms.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static List<Integer> fromJsonString(String value) {
        Type listType = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromNumberList(List<Integer> list) {
        String json = new Gson().toJson(list);
        return json;
    }

    @TypeConverter
    public static String fromStringList(List<String> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static List<String> jsonToStringList(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }
}
