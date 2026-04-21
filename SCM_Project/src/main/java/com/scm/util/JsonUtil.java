package com.scm.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;

/**
 * JsonUtil - Gson-based JSON serialization/deserialization utility.
 *
 * GRASP: Pure Fabrication - utility class, not a domain entity
 * SOLID: SRP - only handles JSON conversion
 */
public class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting()
        .create();

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }
}
