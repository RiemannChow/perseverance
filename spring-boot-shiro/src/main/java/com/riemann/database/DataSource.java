package com.riemann.database;

import java.util.HashMap;
import java.util.Map;

public class DataSource {
    private static Map<String, Map<String, String>> data = new HashMap<>();

    static {
        Map<String, String> data1 = new HashMap<>();
        data1.put("password", "edgar123");
        data1.put("role", "user");
        data1.put("permission", "view");

        Map<String, String> data2 = new HashMap<>();
        data2.put("password", "riemann123");
        data2.put("role", "admin");
        data2.put("permission", "view,edit");

        data.put("edgar", data1);
        data.put("riemann", data2);
    }

    public static Map<String, Map<String, String>> getData() {
        return data;
    }
}
