package com.example.enrico.todolist.checks;

import java.util.ArrayList;

public class MonitorChecks {
    public static ArrayList<Integer> checks = new ArrayList<>();
    public static boolean checkMode = false;
    public static boolean checkAll = false;
    public static boolean singleModeCheck = false;

    public static void setCurrentPosition(int currentPosition) {
        getChecks().add(currentPosition);
    }

    public static void addCheck(int position) {
        if (!checks.contains(position)) {
            checks.add(position);
        }
    }

    public static void clearChecks() {
        checks.clear();
    }

    public static void removeCheck(int position) {
        if (checks.contains(position)) {
            int index = checks.indexOf(position);
            checks.remove(index);
        }
    }

    public static ArrayList<Integer> getChecks() {
        return checks;
    }

}
