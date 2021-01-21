package org.telegram.irooms.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static List<Long> fromJsonString(String value) {
        Type listType = new TypeToken<ArrayList<Long>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromNumberList(List<Long> list) {
        return new Gson().toJson(list);
    }
}
