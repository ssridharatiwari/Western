package com.milk.milkcollectionapp;

import android.app.Activity;
import android.widget.Button;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by alfa on 7/30/2017.
 */

public class SClient  extends Activity {
    Button btnClear;
    private OnClickListener btnClearListener = new C00033();
    Button btnConnect;
    private OnClickListener btnConnectListener = new C00011();
    Button btnSend;
    private OnClickListener btnSendListener = new C00022();
    CheckBox checkHex;
    private OnCheckedChangeListener checkHexListener = new C00044();
    EditText editIpAddress;
    EditText editReceive;
    EditText editSend;
    EditText editTcpPort;
    int iPort;
    ImageView imgConnect;
    String ipAddress = new String();
    NetworkTask networktask;
    InputStream nis;
    OutputStream nos;
    Socket nsocket = new Socket();
    SocketAddress sockaddr;
    TextView textInfo;

    class C00011 implements OnClickListener {
        C00011() {
        }

        public void onClick(View v) {
            if (SClient.this.editIpAddress.length() <= 0) {
                SClient.this.textInfo.setText("You must set a valid IP.");
                SClient.this.editIpAddress.requestFocus();
                return;
            }
            SClient.this.ipAddress = SClient.this.editIpAddress.getText().toString();
            if (!myutility.ValidateIPAddress(SClient.this.ipAddress)) {
                SClient.this.textInfo.setText("Ip Address Error.");
                SClient.this.editIpAddress.requestFocus();
            } else if (SClient.this.editTcpPort.length() <= 0) {
                SClient.this.textInfo.setText("You must set a valid TCP port.");
                SClient.this.editTcpPort.requestFocus();
            } else
                {
                SClient.this.iPort = Integer.parseInt(SClient.this.editTcpPort.getText().toString());
                if (SClient.this.iPort <= 0 || SClient.this.iPort > 65535) {
                    SClient.this.textInfo.setText("Port Error.");
                    SClient.this.editTcpPort.requestFocus();
                } else if (SClient.this.btnConnect.getText().equals("Connect")) {
                    try {
                        SClient.this.btnConnect.setText("Wait");
                        SClient.this.sockaddr = new InetSocketAddress(SClient.this.ipAddress, SClient.this.iPort);
                        SClient.this.nsocket = new Socket();
                        SClient.this.networktask = new NetworkTask();
                        SClient.this.networktask.execute(new Void[0]);
                        synchronized (SClient.this.nsocket) {
                            SClient.this.nsocket.wait(10000);
                        }
                        if (SClient.this.nsocket.isConnected()) {
                            SClient.this.SettaConnesso();
                        } else {
                            SClient.this.SettaDisconnesso();
                        }
                    } catch (Exception e) {
                        SClient.this.textInfo.setText("Connection error.");
                        try {
                            SClient.this.nis.close();
                            SClient.this.nos.close();
                            SClient.this.nsocket.close();
                        } catch (IOException e2) {
                        } catch (Exception e3) {
                        }
                    }
                } else if (SClient.this.btnConnect.getText().equals("Disconnect")) {
                    try {
                        SClient.this.nis.close();
                        SClient.this.nos.close();
                        SClient.this.nsocket.close();
                        SClient.this.networktask.cancel(true);
                    } catch (IOException e4) {
                        SClient.this.textInfo.setText(e4.toString());
                    } catch (Exception e5) {
                        SClient.this.textInfo.setText(e5.toString());
                    }
                    SClient.this.btnConnect.setText("Connect");
                    SClient.this.textInfo.setText("Socket Closed.");
                }
            }
        }
    }

    class C00022 implements OnClickListener {
        C00022() {
        }

        public void onClick(View v) {
            String str = new String();
            str = "";
            try {
                if (!SClient.this.nsocket.isConnected())
                {
                    SClient.this.textInfo.setText("Socket not connected.");
                }
                else if (SClient.this.editSend.length() > 0)
                {

                    if (!SClient.this.checkHex.isChecked())
                    {
                        str = SClient.this.editSend.getText().toString();
                    }
                    else if (SClient.this.editSend.length() % 2 == 0)
                    {
                        str = myutility.convertHexToString(SClient.this.editSend.getText().toString());
                    }
                    else
                    {
                        SClient.this.textInfo.setText("Hex format error.");
                    }

                    if (str.length() > 0)
                    {
                        SClient.this.textInfo.setText("Sending Message.");
                        SClient.this.nos.write(str.getBytes("ISO-8859-1"));
                        return;
                    }
                    SClient.this.textInfo.setText("No message to send.");
                }
            } catch (Exception e) {
                SClient.this.textInfo.setText(e.toString());
            }
        }
    }

    class C00033 implements OnClickListener {
        C00033() {

        }

        public void onClick(View v) {


            SClient.this.editSend.setText("");
            SClient.this.editReceive.setText("");
        }


    }

    class C00044 implements OnCheckedChangeListener {
        C00044() {
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String send = new String();
            String receive = new String();
            if (isChecked) {
                if (SClient.this.editSend.length() > 0) {
                    SClient.this.editSend.setText(myutility.convertStringToHex(SClient.this.editSend.getText().toString()));
                }
                if (SClient.this.editReceive.length() > 0)
                {
                //    SClient.this.editReceive.setText(myutility.convertStringToHex(SClient.this.editReceive.getText().toString()));
                    return;
                }
                return;
            }
            if (SClient.this.editSend.length() > 0 && SClient.this.editSend.length() % 2 == 0) {
                SClient.this.editSend.setText(myutility.convertHexToString(SClient.this.editSend.getText().toString()));
            }
            if (SClient.this.editReceive.length() > 0 && SClient.this.editReceive.length() % 2 == 0) {
               // SClient.this.editReceive.setText(myutility.convertHexToString(SClient.this.editReceive.getText().toString()));
            }
        }
    }

    class C00055 implements DialogInterface.OnClickListener {
        C00055() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            SClient.this.finish();
        }
    }

    public class NetworkTask extends AsyncTask<Void, byte[], Boolean> {
        protected void onPreExecute() {
        }

        protected Boolean doInBackground(Void... params) {
            boolean result = false;
            try {
                SClient.this.nsocket.connect(SClient.this.sockaddr, 1000);
                synchronized (SClient.this.nsocket) {
                    SClient.this.nsocket.notify();
                }
                if (SClient.this.nsocket.isConnected()) {
                    SClient.this.nis = SClient.this.nsocket.getInputStream();
                    SClient.this.nos = SClient.this.nsocket.getOutputStream();
                    byte[] buffer = new byte[4096];
                    int read = SClient.this.nis.read(buffer, 0, 4096);
                    while (read != -1) {
                        System.arraycopy(buffer, 0, new byte[read], 0, read);
                        publishProgress(new byte[][]{buffer});
                        read = SClient.this.nis.read(buffer, 0, 4096);
                    }
                }
                synchronized (SClient.this.nsocket) {
                    SClient.this.nsocket.notify();
                }
                try {
                    SClient.this.nis.close();
                    SClient.this.nos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } catch (IOException e3) {
                try {
                    e3.printStackTrace();
                    synchronized (SClient.this.nsocket) {
                        SClient.this.nsocket.notify();
                        result = true;
                        synchronized (SClient.this.nsocket) {
                            SClient.this.nsocket.notify();
                            try {
                                SClient.this.nis.close();
                                SClient.this.nos.close();
                            } catch (IOException e32) {
                                e32.printStackTrace();
                            } catch (Exception e22) {
                                e22.printStackTrace();
                            }
                        }
                    }
                } catch (Throwable th) {
                    synchronized (SClient.this.nsocket) {
                        SClient.this.nsocket.notify();
                        try {
                            SClient.this.nis.close();
                            SClient.this.nos.close();
                        } catch (IOException e322) {
                            e322.printStackTrace();
                        } catch (Exception e222) {
                            e222.printStackTrace();
                        }
                    }
                }
            } catch (Exception e2222) {
                e2222.printStackTrace();
                synchronized (SClient.this.nsocket) {
                    SClient.this.nsocket.notify();
                    result = true;
                    synchronized (SClient.this.nsocket) {
                        SClient.this.nsocket.notify();
                        try {
                            SClient.this.nis.close();
                            SClient.this.nos.close();
                        } catch (Exception e22222) {
                            e22222.printStackTrace();
                        }
                    }
                }
            }
            return Boolean.valueOf(result);
        }

        protected void onProgressUpdate(byte[]... values) {
            String toReceive =  new String();
            String newText = new String();
            toReceive = "";
            newText = " ";//SClient.this.editReceive.getText().toString();
            if (SClient.this.checkHex.isChecked()) {
//                try {
//                    toReceive = myutility.convertStringToHex(new String(values[0], "ISO-8859-1"));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
            } else
                {
                try {
                    toReceive = new String(values[0], "ISO-8859-1");
                } catch (UnsupportedEncodingException e2) {
                    e2.printStackTrace();
                }
            }
            if (values.length > 0)
            {
                //SClient.this.editReceive.setText("-"+toReceive.toString()+"-");

                String data = toReceive.toString();

                if (data.length() > 3)
                {
                    data = data.substring(0, 4);
                    SClient.this.editReceive.setText("-"+data+"-");

                }

                // Log.e("hello ",toReceive.toString());
            }
        }

        protected void onCancelled() {

        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);
        this.btnConnect = new Button(this);
        this.btnConnect = (Button) findViewById(R.id.btnConnect);
        this.btnSend = new Button(this);
        this.btnSend = (Button) findViewById(R.id.btnSend);
        this.btnClear = new Button(this);
        this.btnClear = (Button) findViewById(R.id.btnClear);
        this.textInfo = new TextView(this);
        this.textInfo = (TextView) findViewById(R.id.textInfo);
        this.editIpAddress = new EditText(this);
        this.editIpAddress = (EditText) findViewById(R.id.editIpAddress);
        this.editTcpPort = new EditText(this);
        this.editTcpPort = (EditText) findViewById(R.id.editTcpPort);
        this.editSend = new EditText(this);
        this.editSend = (EditText) findViewById(R.id.editSend);
        this.editReceive = new EditText(this);
        this.editReceive = (EditText) findViewById(R.id.editReceive);
        this.checkHex = new CheckBox(this);
        this.checkHex = (CheckBox) findViewById(R.id.checkHex);
        this.networktask = new NetworkTask();
        this.btnConnect.setOnClickListener(this.btnConnectListener);
        this.btnSend.setOnClickListener(this.btnSendListener);
        this.btnClear.setOnClickListener(this.btnClearListener);
        this.checkHex.setOnCheckedChangeListener(this.checkHexListener);
    }

    public void msbox(String str, String str2) {
        Builder dlgAlert = new Builder(this);
        dlgAlert.setTitle(str);
        dlgAlert.setMessage(str2);
        dlgAlert.setPositiveButton("OK", new C00055());
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public void SettaConnesso() {
        this.textInfo.setText("Socket Connected.");
        this.btnConnect.setText("Disconnect");
    }

    public void SettaDisconnesso() {
        this.textInfo.setText("Socket Disconnected.");
        this.btnConnect.setText("Connect");
    }

    protected void onDestroy() {
        super.onDestroy();
        this.networktask.cancel(true);
    }



};
