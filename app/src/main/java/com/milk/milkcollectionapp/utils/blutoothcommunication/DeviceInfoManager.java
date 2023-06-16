package com.milk.milkcollectionapp.utils.blutoothcommunication;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

import com.milk.milkcollectionapp.AppApplication;

public class DeviceInfoManager {
    private static DeviceInfoManager manager = new DeviceInfoManager();
    private SimplePreference preference;

    private final static String keyName = "deviceName";
    private final static String keyAddress = "deviceAddress";

    private DeviceInfoManager() {
        preference = AppApplication.getPreference();
    }

    public static DeviceInfoManager getInstance() {
        return manager;
    }

    @SuppressLint("MissingPermission")
    public void setDeviceInfo(BluetoothDevice deviceInfo) {
        preference.putString(keyName, deviceInfo.getName());
        preference.putString(keyAddress, deviceInfo.getAddress());
    }

    public String getDeviceName() {
        return preference.getString(keyName, null);
    }

    public String getDeviceAddress() {
        return preference.getString(keyAddress, null);
    }
}
