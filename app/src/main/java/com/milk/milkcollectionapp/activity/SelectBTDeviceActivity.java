package com.milk.milkcollectionapp.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.adapter.LeDeviceListAdapter;
import com.milk.milkcollectionapp.utils.blutoothcommunication.DeviceInfoManager;

import java.util.ArrayList;
import java.util.List;

public class SelectBTDeviceActivity extends AppCompatActivity {
    public boolean mScanning;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private ArrayList<String> deviceList;
    private static final long SCAN_PERIOD = 5000;
    private static final int REQUEST_ENABLE_BT = 10000;
    private Handler mHandler;
    private ListView listView_BluetoothList;
    private Button btnback;
    private ImageView ivripple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bluetoothlist);
        this.setFinishOnTouchOutside(false);
        ivripple = findViewById(R.id.ivripple);
        mHandler = new Handler();

        listView_BluetoothList = findViewById(R.id.bluetoothlist_listview_devices);
        btnback = findViewById(R.id.btnback);

        btnback.setOnClickListener(v -> finish());
        // initialize bluetooth manager & adapter
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        // if bluetooth is not currently enabled,
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            // initialize list view adapter
            deviceList = new ArrayList();
            mLeDeviceListAdapter = new LeDeviceListAdapter(this, R.layout.item_bluetoothdevice);
            listView_BluetoothList.setAdapter(mLeDeviceListAdapter);
            listView_BluetoothList.setOnItemClickListener(new ListViewItemClickListener());
            scanLeDevice(true);
        }
    }

    /**
     * scan Le devices
     * @param enable
     */
    //region
    private void scanLeDevice(final boolean enable) {
//        if (ActivityCompat.checkSelfPermission(SelectBTDeviceActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
        if (enable) {
            // stops scanning after a pre-defined scan period.
            mHandler.postDelayed(() -> {
                mScanning = false;
                bluetoothLeScanner.stopScan(mScanCallback);
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothLeScanner.startScan(mScanCallback);
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(mScanCallback);
        }
    }

    /**
     * scan result call back
     */
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            processResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                processResult(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Toast.makeText(getApplicationContext(), R.string.ble_not_find, Toast.LENGTH_SHORT).show();
        }

        private void processResult(final ScanResult result) {
            runOnUiThread(() -> {
                String deviceName = result.getScanRecord().getDeviceName();
//                if(deviceName != null && !deviceName.equals("") && !deviceList.contains(deviceName) /*&& deviceName.contains("OW")*/)
                {
                    mLeDeviceListAdapter.addDevice(result.getDevice(), SelectBTDeviceActivity.this);
                    deviceList.add(deviceName);
                }
            });
        }
    };

    /**
     * listViewItem click listener
     */
    private class ListViewItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View clickedView, int pos, long id) {
            BluetoothDevice device = mLeDeviceListAdapter.getDevice(pos);
            if (device == null) return;
            DeviceInfoManager manager = DeviceInfoManager.getInstance();
            manager.setDeviceInfo(device);
            Toast.makeText(getBaseContext(), R.string.ble_selected, Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    /**
     * closed activity with onBackPressed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, R.string.ble_canceldfind, Toast.LENGTH_SHORT).show();
    }

    /**
     * onActivityResult, request ble system enable
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // user chose not to enable bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, R.string.ble_canceled, Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else if(requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK){
            finish();
            Intent intent = new Intent(this, SelectBTDeviceActivity.class);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
