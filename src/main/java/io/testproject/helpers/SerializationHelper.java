package io.testproject.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SerializationHelper {

    private static Gson gson = new GsonBuilder().create();

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        LogHelper.Debug("Deserializing: " + jsonString);
        return gson.fromJson(jsonString, clazz);
    }

    public static String toJson(Object data) {
        return gson.toJson(data);
    }
}
