package com.homework.test;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private static final Map<String, String> dataMap = new HashMap<>();

    public static Map<String, String> getMap(){
        return dataMap;
    }
}
