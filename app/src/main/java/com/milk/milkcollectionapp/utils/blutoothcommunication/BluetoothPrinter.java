package com.milk.milkcollectionapp.utils.blutoothcommunication;

import android.Manifest;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.milk.milkcollectionapp.activity.MainActivity;

import java.io.IOException;
import java.io.OutputStream;

public class BluetoothPrinter {
    private BluetoothSocket btSocket = null;
    private OutputStream btOutputStream = null;
    public static BluetoothPrinter shared;
    public EscPosPrinter printer;

    public BluetoothPrinter(EscPosPrinter printer) {
        this.printer = printer;
    }

    public BluetoothPrinter() {
    }

    public static BluetoothPrinter getInstace() {
        if (shared == null) {
            shared = new BluetoothPrinter();
        }
        return shared;
    }

    public boolean isConnected() {
        return btSocket != null && btSocket.isConnected();
    }

    public void finish() {
        if (btSocket != null) {
            try {
                btOutputStream.close();
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btSocket = null;
        }
    }

    public void sendData(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.instace, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.instace, new String[]{Manifest.permission.BLUETOOTH}, 1);
            } else if (ActivityCompat.checkSelfPermission(MainActivity.instace, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.instace, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 2);
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(MainActivity.instace, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.instace, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 3);
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(MainActivity.instace, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.instace, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 4);
            } else {
                BluetoothConnection connection = BluetoothPrintersConnections.selectFirstPaired();
                if (connection == null) {
                    Toast.makeText(MainActivity.instace, "Printer not connected", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    printer = new EscPosPrinter(connection, 203, 48f, 32);
                    printer.printFormattedText(text);
                } catch (EscPosConnectionException | EscPosEncodingException | EscPosBarcodeException | EscPosParserException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
