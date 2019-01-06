package io.testproject.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class SerializationHelper {

    private static Gson gson = new GsonBuilder().create();

    static <T> T FromJson(String jsonString, Class<T> clazz) {
        return gson.fromJson(jsonString, clazz);
    }

    static String ToJson(Object data) {
        return gson.toJson(data);
    }
}