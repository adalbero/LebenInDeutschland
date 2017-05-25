package com.adalbero.app.lebenindeutschland;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Adalbero on 25/05/2017.
 */

public class JSONUtil {
    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public static Map<String, String> toStringMap(JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            map.put(key, value.toString());
        }
        return map;
    }

    public static List<String> toStringList(JSONArray array) throws JSONException {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            list.add(value.toString());
        }
        return list;
    }

    public static String toJSONString(Map<String, String> map) throws JSONException {
        JSONObject json = new JSONObject(map);
        return json.toString(0);
    }

    public static String toJSONString(List<String> list) throws JSONException {
        JSONArray json = new JSONArray(list);
        return json.toString(0);
    }

    public static JSONObject toJSONObject(String str) throws JSONException {
        return new JSONObject(str);
    }

    public static JSONArray toJSONArray(String str) throws JSONException {
        return new JSONArray(str);
    }
}
