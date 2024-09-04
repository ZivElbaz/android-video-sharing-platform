package com.example.viewtube.managers;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

// A class for Room type converters, used to convert complex data types (like List<String>) to
// a format that can be stored in the database and back
public class Converters {

    // Convert a List of Strings (likedBy) into a JSON String to store in the database
    @TypeConverter
    public static String fromList(List<String> likedBy) {
        if (likedBy == null) {
            return null; // Return null if the list is empty
        }
        Gson gson = new Gson();
        return gson.toJson(likedBy); // Convert the list to a JSON string
    }

    // Convert a JSON String (likedByString) back into a List of Strings after fetching from the database
    @TypeConverter
    public static List<String> fromString(String likedByString) {
        if (likedByString == null) {
            return null; // Return null if the string is null
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType(); // Define the type of the list
        return gson.fromJson(likedByString, type); // Convert the JSON string back to a List
    }
}
