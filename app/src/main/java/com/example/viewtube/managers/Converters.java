package com.example.viewtube.managers;
import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    @TypeConverter
    public static String fromList(List<String> likedBy) {
        if (likedBy == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(likedBy);
    }

    @TypeConverter
    public static List<String> fromString(String likedByString) {
        if (likedByString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(likedByString, type);
    }
}
