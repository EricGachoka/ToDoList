package com.example.enrico.todolist.json;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.example.enrico.todolist.data.DataHandler;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class JsonHelper {
    private static JsonHelper uniqueInstance = new JsonHelper();
    private SharedPreferences.Editor editor;
    private final String MY_GLOBAL_PREFS = "my_global_prefs";
    private String SAVED_MAP = "saved_map";
    private DataHandler dataHandler = DataHandler.getInstance();

    public JsonHelper() {
    }

    public static JsonHelper getInstance() {
        return  uniqueInstance;
    }

    private String exportToJson() {
        ToDoItems toDoItems = new ToDoItems();
        toDoItems.setItems(dataHandler.getMap());

        Gson gson = new Gson();
        String jsonString = gson.toJson(toDoItems);

        return jsonString;
    }

    private void importFromJson(String jsonString) {
        Gson gson = new Gson();
        ToDoItems toDoItems = gson.fromJson(jsonString, ToDoItems.class);
        dataHandler.setMap(toDoItems.getItems());
        dataHandler.setCategoryList(dataHandler.getMap().keySet());
    }

    public void saveMapToPrefs(Context context) {
        editor = context.getSharedPreferences(MY_GLOBAL_PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(SAVED_MAP, exportToJson());
        editor.apply();
    }

    public void getMapFromPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(MY_GLOBAL_PREFS, context.MODE_PRIVATE);
        String jsonString = prefs.getString(SAVED_MAP, "");
        if (!TextUtils.isEmpty(jsonString)) {
            importFromJson(jsonString);
        }
    }

    private static class ToDoItems {
        Map<String, String> items;

        private Map<String, String> getItems() {
            return items;
        }

        private void setItems(Map<String, String> items) {
            this.items = items;
        }
    }

}
