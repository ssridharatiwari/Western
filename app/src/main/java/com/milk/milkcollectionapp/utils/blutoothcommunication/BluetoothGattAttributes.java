package com.milk.milkcollectionapp.utils.blutoothcommunication;

import java.util.HashMap;

public class BluetoothGattAttributes {
    private static HashMap<String, String> attributes = new HashMap<>();
    static { }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}