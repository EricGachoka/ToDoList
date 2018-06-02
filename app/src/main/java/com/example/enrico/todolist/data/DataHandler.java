package com.example.enrico.todolist.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DataHandler {
    private static DataHandler uniqueInstance = new DataHandler();
    private final String TAG = "SelectedPosition";
    private Map<String, String> map = new TreeMap<>();
    private List<String> categoryList = new ArrayList<>();
    private String clickedItem = null;
    private int numItems;
    private boolean isTablet = false;
    private int selectedPosition;
    private boolean isReversed = false;
    private boolean editMode = false;
    private boolean actionMode = false;

    private DataHandler() {}

    public static DataHandler getInstance() {
        return uniqueInstance;
    }

    public boolean getActionMode() {
        return actionMode;
    }

    public void setActionMode(boolean actionMode) {
        this.actionMode = actionMode;
    }

    public boolean getEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean getIsSorted() {
        return isSorted;
    }

    public void setIsSorted(boolean isSorted) {
        this.isSorted = isSorted;
    }

    private boolean isSorted = false;

    public boolean getIsReversed() {
        return isReversed;
    }

    public void setIsReversed(boolean isReversed) {
        this.isReversed = isReversed;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public boolean getIsTablet() {
        return isTablet;
    }

    public void setIsTablet(boolean isTablet) {
        this.isTablet = isTablet;
    }

    public String getClickedItem() {
        return clickedItem;
    }

    public void setClickedItem(String clickedItem) {
        this.clickedItem = clickedItem;
    }

    public int getNumItems() {
        return numItems;
    }

    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

    public int getClickedIndex() {
        return categoryList.indexOf(clickedItem);
    }

    public List<String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(Set<String> categorySet) {
        if (categoryList.isEmpty()) {
            for (String s : categorySet) {
                categoryList.add(s);
            }
            if (!getIsReversed()) {
                Collections.reverse(categoryList);
            }
        }
    }

    public void addCategoryByTime(String category) {
        List<String> list = new LinkedList<>();
        list.add(0, category);

        for (String s : getCategoryList()) {
            list.add(s);
        }
        getCategoryList().clear();
        for (String s : list) {
            getCategoryList().add(s);
        }
    }

    public void addCategoryByAlpha(String category) {
        Set<String> set = new TreeSet<>();
        set.add(category);

        for (String s : getCategoryList()) {
            set.add(s);
        }
        getCategoryList().clear();
        for (String s : set) {
            getCategoryList().add(s);
        }
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> newMap) {
        map = newMap;
    }

    public void addTitleOnly(String title) {
        getMap().put(title, "");
    }

    public void addTodo(String title, String detail) {
        getMap().put(title, detail);
    }

    public void removeTodo(String title) {
        if (getMap().containsKey(title)) {
            map.remove(title);
        }
    }

    public void updateDetail(String title, String detail) {
        addTodo(title, detail);
    }

    public void sortList(){
        Collections.sort(getCategoryList());
    }

}
