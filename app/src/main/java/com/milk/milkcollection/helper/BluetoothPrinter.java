package com.milk.milkcollection.helper;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.milk.milkcollection.Activity.MainActivity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class BluetoothPrinter {

    public static final int ALIGN_CENTER = 100;
    public static final int ALIGN_RIGHT = 101;
    public static final int ALIGN_LEFT = 102;

    private static final byte[] NEW_LINE = {10};
    private static final byte[] ESC_ALIGN_CENTER = new byte[]{0x1b, 'a', 0x01};
    private static final byte[] ESC_ALIGN_RIGHT = new byte[]{0x1b, 'a', 0x02};
    private static final byte[] ESC_ALIGN_LEFT = new byte[]{0x1b, 'a', 0x00};

    private BluetoothDevice printer;
    private BluetoothSocket btSocket = null;
    private OutputStream btOutputStream = null;


    public BluetoothPrinter(BluetoothDevice printer) {
        this.printer = printer;
    }

    public BluetoothPrinter() {

    }

    public void connectPrinter(final PrinterConnectListener listener) {
        new ConnectTask(new ConnectTask.BtConnectListener() {
            @Override
            public void onConnected(BluetoothSocket socket) {
                btSocket = socket;
                try {
                    btOutputStream = socket.getOutputStream();
                    listener.onConnected();
                } catch (IOException e) {
                    listener.onFailed();
                }
            }

            @Override
            public void onFailed() {
                listener.onFailed();
            }
        }).execute(printer);
    }


    public static BluetoothPrinter shared;

    public static BluetoothPrinter getInstace(){
        if(shared == null){
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

    public boolean printText(String text) {
        try {
            btOutputStream.write(encodeNonAscii(text).getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean printUnicode(byte[] data) {
        try {
            btOutputStream.write(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean printLine() {
        return printText("________________________________");
    }

    public boolean addNewLine() {
        return printUnicode(NEW_LINE);
    }

    public int addNewLines(int count) {
        int success = 0;
        for (int i = 0; i < count; i++) {
            if (addNewLine()) success++;
        }

        return success;
    }

    public boolean printImage(Bitmap bitmap) {
        byte[] command = decodeBitmap(bitmap);
        return printUnicode(command);
    }

    public void setAlign(int alignType) {
        byte[] d;
        switch (alignType) {
            case ALIGN_CENTER:
                d = ESC_ALIGN_CENTER;
                break;
            case ALIGN_LEFT:
                d = ESC_ALIGN_LEFT;
                break;
            case ALIGN_RIGHT:
                d = ESC_ALIGN_RIGHT;
                break;
            default:
                d = ESC_ALIGN_LEFT;
                break;
        }

        try {
            btOutputStream.write(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLineSpacing(int lineSpacing) {
        byte[] cmd = new byte[]{0x1B, 0x33, (byte) lineSpacing};
        printUnicode(cmd);
    }

    public void setBold(boolean bold) {
        byte[] cmd = new byte[]{0x1B, 0x45, bold ? (byte) 1 : 0};
        printUnicode(cmd);
    }

    public void feedPaper() {
        addNewLine();
        addNewLine();
        addNewLine();
        addNewLine();
    }

    private static class ConnectTask extends AsyncTask<BluetoothDevice, Void, BluetoothSocket> {
        private BtConnectListener listener;

        private ConnectTask(BtConnectListener listener) {
            this.listener = listener;
        }


        @Override
        protected BluetoothSocket doInBackground(BluetoothDevice... bluetoothDevices) {
            BluetoothDevice device = bluetoothDevices[0];
            UUID uuid = device.getUuids()[0].getUuid();
            BluetoothSocket socket = null;
            boolean connected = true;

            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {

            }

            try {
                socket.connect();
            } catch (IOException e) {
                try {
                    socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class})
                            .invoke(device, 1);
                    socket.connect();
                } catch (Exception e2) {
                    connected = false;
                }
            }

            return connected ? socket : null;
        }

        @Override
        protected void onPostExecute(BluetoothSocket bluetoothSocket) {
            if (listener != null) {
                if (bluetoothSocket != null) listener.onConnected(bluetoothSocket);
                else listener.onFailed();
            }
        }

        private interface BtConnectListener {
            void onConnected(BluetoothSocket socket);

            void onFailed();
        }
    }

    public interface PrinterConnectListener {
        void onConnected();
        void onFailed();
    }

    private static String encodeNonAscii(String text) {
        return text.replace('á', 'a')
                .replace('č', 'c')
                .replace('ď', 'd')
                .replace('é', 'e')
                .replace('ě', 'e')
                .replace('í', 'i')
                .replace('ň', 'n')
                .replace('ó', 'o')
                .replace('ř', 'r')
                .replace('š', 's')
                .replace('ť', 't')
                .replace('ú', 'u')
                .replace('ů', 'u')
                .replace('ý', 'y')
                .replace('ž', 'z')
                .replace('Á', 'A')
                .replace('Č', 'C')
                .replace('Ď', 'D')
                .replace('É', 'E')
                .replace('Ě', 'E')
                .replace('Í', 'I')
                .replace('Ň', 'N')
                .replace('Ó', 'O')
                .replace('Ř', 'R')
                .replace('Š', 'S')
                .replace('Ť', 'T')
                .replace('Ú', 'U')
                .replace('Ů', 'U')
                .replace('Ý', 'Y')
                .replace('Ž', 'Z');
    }

    public static byte[] decodeBitmap(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<>();
        StringBuffer sb;
        int zeroCount = bmpWidth % 8;
        String zeroStr = "";
        if (zeroCount > 0) {
            for (int i = 0; i < (8 - zeroCount); i++) zeroStr = zeroStr + "0";
        }

        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;
                if (r > 160 && g > 160 && b > 160) sb.append("0");
                else sb.append("1");
            }
            if (zeroCount > 0) sb.append(zeroStr);
            list.add(sb.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8 : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }

        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    private static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<>();
        for (String binaryStr : list) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);
                String hexString = strToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;
    }

    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = {"0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"};

    private static String strToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    private static byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<>();
        for (String hexStr : list) commandList.add(hexStringToBytes(hexStr));
        return sysCopy(commandList);
    }

    private static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) return null;
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }

        return destArray;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public BluetoothSocket getSocket() {
        return btSocket;
    }

    public BluetoothDevice getDevice() {
        return printer;
    }



    public BluetoothAdapter mBluetoothAdapter;
    public BluetoothSocket mmSocket;
    public BluetoothDevice mmDevice;
    public OutputStream mmOutputStream;
    public InputStream mmInputStream;
    public Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;


    public String inputString = "";

    public void findBT() {

        if (mmDevice == null){
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            Log.e("hi","ji32423");
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                MainActivity.getInstace().startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            Log.e("bluetootsh detals", String.valueOf(pairedDevices));
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(AppString.printername)) {
                        mmDevice = device;
                        Log.e("device name", mmDevice.getName());
                        break;
                    }
                }
            }

            try {
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                mmSocket.connect();
                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();
                readBufferPosition = 0;
                readBuffer = new byte[1024];

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    public  String bluetoothString()  {

        Log.e("device name", mmDevice.getName());
        if (mmInputStream != null) {

            try {

                int bufferSize=1024;
                final byte[] buffer=new byte[bufferSize];
                int bytress = 0;

                bytress = mmInputStream.read(buffer);
                inputString = new String(buffer, 0, bytress);
                Log.e("input string",BluetoothPrinter.getInstace().inputString);
                String mystring = BluetoothPrinter.getInstace().inputString;


                if (mystring.length() > 8) {
                    mystring = mystring.substring((mystring.length()-8), mystring.length());
                    mystring = mystring.replace("$", "");
                    mystring = mystring.replace("L", "");
                    if(mystring.contains("-")){
                        return  "0";
                    } else{

                        if(!(mystring.indexOf(".") > -1 )){
                            mystring = String.valueOf(Float.parseFloat(mystring) / 100);
                        }

                        if (Float.valueOf(mystring.toString()) < 0) {
                            return "0";
                        }
                        return MainActivity.twoDecimal(mystring);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return  "0";
            }
        }

        return  "0";
    }






    public void sendData(String text){
        printFromBluthooth(text);

        Log.e("text",text);

//        if (mmOutputStream == null){
//            findBT();
//        }
//
//        if (mmOutputStream != null){
//            text += "\n";
//            try {
//                mmOutputStream.write(text.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }


    public  BluetoothAdapter btAdapter;
    public  BluetoothDevice mBtDevice;
    public  BluetoothPrinter myprinter;
    String data = "";

    public void findWeg() {


        if ( mmInputStream!=null){
            return;
        }


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {

        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            MainActivity.getInstace().startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals(AppString.printername)) {
                    mmDevice = device;
                    Log.e("device name",mmDevice.getName());
                    break;
                }
            }
        }


        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID "0000110E-0000-1000-8000-00805F9B34FB"
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            readBufferPosition = 0;
            readBuffer = new byte[1024];

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void printFromBluthooth(final String printStiring){

        data = printStiring;

        if (myprinter != null  ) {
            printByPrinter(printStiring);
            return;
        }

        if (mmDevice == null) {
            findBT();
        }
        myprinter = new BluetoothPrinter(mmDevice);
        printByPrinter(printStiring);

//        final Handler handler = new Handler();
//        final boolean bluetoothPrinter = handler.postDelayed(new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.M)
//            @SuppressLint("LongLogTag")
//            @Override
//            public void run() {
//
//
//
//                if (mmDevice == null) {
//                   findBT();
//                }else{
//
//                }
//                myprinter = new BluetoothPrinter(mBtDevice);
//                printByPrinter(printStiring);
//            }
//
//        }, 1);
    }






    public void printByPrinter( final String str){


        if (myprinter.isConnected()) {
            myprinter.setAlign(BluetoothPrinter.ALIGN_LEFT);
            myprinter.printText(str);
//            myprinter.addNewLine();
//            myprinter.addNewLine();
            myprinter.finish();

            Log.e("Already connected",myprinter.getDevice().getName());
        } else {
            Log.e("new connection stiring", str);
            myprinter.connectPrinter(new BluetoothPrinter.PrinterConnectListener() {
                @Override
                public void onConnected() {

                    myprinter.setAlign(BluetoothPrinter.ALIGN_LEFT);
                    myprinter.printText(str);
                    myprinter.addNewLine();
                    myprinter.addNewLine();
                    myprinter.finish();

                    Log.e("after connected",myprinter.getDevice().getName());
                }

                public void onFailed() {
                    Log.d("BluetoothPrinter", "Conection failed");
                }
            });
        }
    }


}
