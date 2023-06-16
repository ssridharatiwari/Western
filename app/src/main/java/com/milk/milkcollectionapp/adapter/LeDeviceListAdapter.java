package com.milk.milkcollectionapp.adapter;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.model.DeviceItem;

import java.util.ArrayList;

public class LeDeviceListAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> mLeDevices;
    private ArrayList<DeviceItem> data;
    private LayoutInflater mInflator;
    private int layout;

    public LeDeviceListAdapter(Context context, int layout) {
        this.mLeDevices = new ArrayList<>();
        this.mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
        this.data = new ArrayList<>();
    }

    public void addDevice(BluetoothDevice device, Context svContext) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device);
            if (ActivityCompat.checkSelfPermission(svContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(svContext, "Please grant bluetooth permission", Toast.LENGTH_SHORT).show();
                return;
            }
            DeviceItem deviceItem = new DeviceItem(device.getName(), device.getAddress());
            data.add(deviceItem);
            notifyDataSetChanged();
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        // general listView optimization code.
        if (view == null) {
            view = mInflator.inflate(layout, viewGroup, false);
        }
        DeviceItem deviceItem = data.get(pos);
        TextView data = (TextView)view.findViewById(R.id.item_bluetoothdevice_tv_deviceid);
        data.setText(deviceItem.getName());
        TextView content = (TextView)view.findViewById(R.id.item_bluetoothdevice_tv_uuid);
        content.setText(deviceItem.getAddress());
        return view;
    }
}