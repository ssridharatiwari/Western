package com.milk.milkcollectionapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.snackbar.Snackbar;
import com.milk.milkcollectionapp.utils.blutoothcommunication.DeviceInfoManager;
import com.milk.milkcollectionapp.utils.db.MilkDBHelpers;
import com.milk.milkcollectionapp.fragment.Fragment_Master;
import com.milk.milkcollectionapp.fragment.Fragment_Report;
import com.milk.milkcollectionapp.fragment.Fragment_Setting;
import com.milk.milkcollectionapp.fragment.Fragment_home;
import com.milk.milkcollectionapp.fragment.Fragment_sell;
import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.utils.blutoothcommunication.BluetoothLeService;
import com.milk.milkcollectionapp.utils.blutoothcommunication.BluetoothPrinter;
import com.milk.milkcollectionapp.utils.FSSession;
import com.milk.milkcollectionapp.utils.SharedPreferencesUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> categories;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private LinearLayout layDrawer;
    public CharSequence mDrawerTitle;
    public CharSequence mTitle;
    Toolbar toolbar;
    public TextView toolbartitle;
    public ImageView toolbar_print, toolbar_btdevices;
    public static ImageView toolbarMainIcon;
    FSSession fsSession;
    public static String lineValue = "===========================";
    public static ProgressDialog progress;
    public String demoDate = "";
    public SharedPreferencesUtils sharedPreferencesUtils;
    public MilkDBHelpers milkDBHelpers;
    private BluetoothLeService mBluetoothLeService;
    private Snackbar snackbar;

    public static MainActivity instace;
    private TextView tvstatus;
    private boolean mConnected = false;
    private String devicename = "Connecting...";

    public static MainActivity getInstace() {
        if (instace == null) {
            instace = new MainActivity();
        }
        return instace;
    }

    int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        snackbar = Snackbar.make(findViewById(android.R.id.content), "Not connected with Printer Device", Snackbar.LENGTH_LONG);
        tvstatus = findViewById(R.id.tvstatus);

        sharedPreferencesUtils = new SharedPreferencesUtils(MainActivity.this);
        milkDBHelpers = new MilkDBHelpers(MainActivity.this);
        instace = MainActivity.this;

        fsSession = new FSSession(MainActivity.this);

        categories = new ArrayList<>();
        categories.add(getResources().getString(R.string.home));
        categories.add(getResources().getString(R.string.master));
        categories.add(getResources().getString(R.string.report));
        categories.add(getResources().getString(R.string.setting));
        categories.add(getResources().getString(R.string.sales));
        categories.add(getResources().getString(R.string.planguage));

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        layDrawer = (LinearLayout) findViewById(R.id.lay_drawer);
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        toolbarMainIcon = (ImageView) findViewById(R.id.iv_main);
        toolbartitle = (TextView) findViewById(R.id.titletool);
        toolbar_print = (ImageView) findViewById(R.id.iv_print);
        toolbar_btdevices = (ImageView) findViewById(R.id.iv_btdevices);
        setSupportActionBar(toolbar);

        toolbarMainIcon.setOnClickListener(v -> {
            if (mDrawerLayout.isDrawerOpen(layDrawer)) {
                mDrawerLayout.closeDrawer(layDrawer);
                ChangeIconToMenu();
            }else {
                if (toolbarMainIcon.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.back).getConstantState()) {
                    onBackPressed();
                    return;
                }
                mDrawerLayout.openDrawer(layDrawer);
                ChangeIconToBack();
            }
        });

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, categories));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        toolbar_print.setOnClickListener(v -> {
            print("Test paper\nWelcome to\nWestern Electronics Group\n");
//            ViewBTDeviceList();
        });

        toolbar_btdevices.setOnClickListener(v -> {
            ViewBTDeviceList();
        });

        if (savedInstanceState == null) {
            selectItem(0);
        }

        devicename = sharedPreferencesUtils.getConnectedDeviceName();
        requestPermission();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    public void ChangeIconToMenu() {
        toolbarMainIcon.setImageResource((R.drawable.menu));
    }

    public void ChangeIconToBack() {
        toolbarMainIcon.setImageResource((R.drawable.back));
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }

            String deviceaddress = sharedPreferencesUtils.geWgDeviceName();
            if (!deviceaddress.equals("")) {
                ConnectDevice(mBluetoothLeService, deviceaddress);
                tvstatus.setText("Trying to connect with :" + devicename);
            } else {
                ConnectDevice(mBluetoothLeService, DeviceInfoManager.getInstance().getDeviceAddress());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            tvstatus.setText("Trying to connect with : " + devicename);
        }
    };

    private void ConnectDevice(BluetoothLeService mBluetoothLeService, String deviceAddress) {
        if (mBluetoothLeService == null) {
            return;
        }
        // if deviceAddress is null, try to reconnect with last connected device


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        requestBluetoothPermission();
        mBluetoothLeService.connect(deviceAddress);
    }

    private static final int REQUEST_BLUETOOTH_PERMISSION = 109;
    private void requestBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, show the permission request dialog
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_BLUETOOTH_PERMISSION);}
    }

    /**
     * receive connection state
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Intent mIntent = intent;
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.e("TAG", "BroadcastReceiver : Connected!");
                tvstatus.setText("Connected with " + devicename);
                tvstatus.setTextColor(getResources().getColor(R.color.green));
                mConnected = true;
            }
            // disconnected
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.e("TAG", "BroadcastReceiver : Disconnected!");
                mConnected = false;
                tvstatus.setText("Searching : " + devicename);
                tvstatus.setTextColor(getResources().getColor(R.color.red));

                for (int i = 0; i <= 10; i++) {
                    reconnect();
                }
                snackbar.show();
            }
            // found GATT service
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.e("TAG", "BroadcastReceiver : Found GATT!");
            }
        }
    };

    private void reconnect() {
        new Handler().postDelayed(() -> {
            Intent gattServiceIntent = new Intent(this, MainActivity.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
            if (mBluetoothLeService != null) {
                String deviceaddress = sharedPreferencesUtils.geWgDeviceName();
                if (!deviceaddress.equals("")) {
                    ConnectDevice(mBluetoothLeService, deviceaddress);
                } else {
                    ConnectDevice(mBluetoothLeService, DeviceInfoManager.getInstance().getDeviceAddress());
                }
            }
        }, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_WRITE_STORAGE && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, R.string.ble_canceled, Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else if (requestCode == REQUEST_BLUETOOTH_PERMISSION && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, R.string.ble_canceled, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void ViewBTDeviceList() {
        Intent intent = new Intent(this, SelectBTDeviceActivity.class);
        startActivity(intent);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        mDrawerList.setItemChecked(position, true);
        setTitle(categories.get(position));
        mDrawerLayout.closeDrawer(layDrawer);
        ChangeIconToMenu();
        String backStateName = "null";
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.content_frame);
        Log.e(" currentFragment ", currentFragment + "");
        if (currentFragment != null) {
            backStateName = currentFragment.getClass().getName();
            Log.e(" currentFragment Tag ", backStateName + "");
        }
        if (position == 0 && !backStateName.equals("com.milk.milkcollection.Fragment.Fragment_home")) {
            toolbartitle.setText(getResources().getString(R.string.home));
            replaceFragment(new Fragment_home());
        } else if (position == 1 && !backStateName.equals("com.milk.milkcollection.Fragment.Fragment_Master")) {
            toolbartitle.setText(getResources().getString(R.string.master));
            replaceFragment(new Fragment_Master());
        } else if (position == 2 && !backStateName.equals("com.milk.milkcollection.Fragment.Fragment_Report")) {
            toolbartitle.setText(getResources().getString(R.string.report));
            replaceFragment(new Fragment_Report());
        } else if (position == 3) {
            toolbartitle.setText(getResources().getString(R.string.setting));
            replaceFragment(new Fragment_Setting());
        } else if (position == 4) {
            toolbartitle.setText(getResources().getString(R.string.sales));
            replaceFragment(new Fragment_sell());
        } else if (position == 5) {
            changeLanguage(MainActivity.this);
        }
    }

    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(backStateName + "");
        ft.commit();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();
        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(layDrawer)) {
            mDrawerLayout.closeDrawer(layDrawer);
            ChangeIconToMenu();
            return;
        }
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 1) {
            new android.app.AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setNegativeButton("No", null).show();
        } else {
            ChangeIconToMenu();
            getFragmentManager().popBackStack();
        }
    }

    public void changeLanguage(Activity activity) {
        new AlertDialog.Builder(activity).setTitle("Change Language.....")
                .setMessage("Are you sure you want to change language?")
                .setPositiveButton("Hindi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        fsSession.saveData("language", "hi");
                        setLocale("hi");

                    }
                }).setNegativeButton("English", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fsSession.saveData("language", "hi");
                        setLocale("en");
                    }
                }).show();
    }

    public void setLocale(String lang) {
        Log.d("lang1", lang);
        Locale myLocale = new Locale(lang);
        Log.d("lang", lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(refresh);
        finish();
        overridePendingTransition(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
    }

    public void print(String string) {
        String printBy = sharedPreferencesUtils.getprintBy();
        if (printBy.equals("blutooth")) {
            String[] items = string.split("\n");
            int splitValue = 25;
            if (items.length > splitValue) {
                final ArrayList<String> mylist = new ArrayList<String>();
                String check = "";
                for (int i = 0; i < items.length; i++) {
                    check = check + "\n" + items[i];
                    if (i % splitValue == 0 && i > 0) {
                        mylist.add(check);
                        check = "";
                    }
                }
                mylist.add(check);
                printFromBluthooth(" \n");
                recursiveMethod(0, mylist);
            } else {
                instace.printMainMethod(string);
            }
        } else {
            instace.printMainMethod(string);
        }
    }

    public void recursiveMethod(final int count, final ArrayList mylist) {
        Handler handler = new Handler();
        final int finalI = count;
        if (count < mylist.size()) {
            handler.postDelayed(() -> {
                String toPrint = (String) mylist.get(finalI);
                if (finalI == mylist.size() - 1) {
                    toPrint = toPrint + " \n_WESTERN_JAIPUR_ \n\n";
                }
                printFromBluthooth(toPrint);
                recursiveMethod(count + 1, mylist);
            }, 4000);
        }
    }

    public void printMainMethod(String printSTRING) {
        if (sharedPreferencesUtils.getWelcomeText().length() > 0) {
            printSTRING = printSTRING + "\n " + sharedPreferencesUtils.getWelcomeText();
        }

        if (printSTRING.length() > 0) {
            if (printBy.equals("0")) {
                SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(getInstace());
                printBy = sharedPreferencesUtils.getprintBy();
            }

            if (printBy.equals("wifi")) {

            } else if (printBy.equals("blutooth")) {
                printFromBluthooth(printSTRING + "\n\n");
            } else {
                PackageManager pm = instace.getPackageManager();
                try {
                    printSTRING = printSTRING + "\n\n\n";
                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    PackageInfo info = pm.getPackageInfo("com.fidelier.posprinterdriver", PackageManager.GET_META_DATA);
                    waIntent.setPackage("com.fidelier.posprinterdriver");

                    waIntent.putExtra(Intent.EXTRA_TEXT, printSTRING);
                    instace.getInstace().startActivity(Intent.createChooser(waIntent, "Share with"));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();

                    Toast.makeText(instace.getInstace(), "Please Install POS Printer Application", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    String printBy = "0";
    int BLU_ADMIN = 10;
    String data = "";

    public void printFromBluthooth(final String printStiring) {
        data = printStiring;
        showLoading("Printing");
        BluetoothPrinter.getInstace().sendData(data);
        dismiss();
        return;
    }


    static public String oneDecimalString(String value) throws IOException {
        try {
            return String.format("%.1f", Float.parseFloat(value));
        } catch (Exception e) {
            return "0.0";
        }
    }

    static public String oneDecimal(String value) {
        try {
            return String.format("%.1f", Float.parseFloat(value));
        } catch (Exception e) {
            return "0.0";
        }
    }

    static public String twoDecimalString(String value) {
        try {
            String newStr = value.replace(" ", "");
            if (newStr.length() > 0)
                return String.format("%.1f", Float.parseFloat(newStr));
            else
                return "0.0";

        } catch (Exception e) {
            return "0.0";
        }
    }

    static public String twoDecimal(String value) {
        try {
            String newStr = value.replace(" ", "");
            if (newStr.length() > 0)
                return String.format("%.2f", Float.parseFloat(newStr));
            else
                return "0.00";

        } catch (Exception e) {
            return "0.00";
        }
    }

    static public String oneDecimalFloatToString(float value) throws IOException {
        return String.format("%.1f", value);
    }

    static public String twoDecimalFloatToString(float value) throws IOException {
        return String.format("%.2f", value);
    }

    static public String lineBreak() {
        return "===========================\n";
    }

    static public void sendWhatsApp(String message) {
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            PackageManager pm = instace.getPackageManager();
            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            waIntent.setPackage("com.whatsapp");
            waIntent.putExtra(Intent.EXTRA_TEXT, message);
            instace.startActivity(Intent.createChooser(waIntent, "Share with"));
        } catch (Exception e) {
            Toast.makeText(instace, "Whats App Not installed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
            } else {
                Toast.makeText(this, "ehgehfg", Toast.LENGTH_SHORT).show();
            }
        }

//        if (requestCode == BLU_ADMIN) {
//            printFromBluthooth(data);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }

    static public void sendTextSms(String message, String number) {
        Log.e("sms:", message);
        if (ActivityCompat.checkSelfPermission(instace, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(instace,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(instace, new String[]{Manifest.permission.SEND_SMS},
                        instace.MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            try {
                if (number.length() == 10) {
                    SmsManager sms = SmsManager.getDefault();
                    PendingIntent sentPI;
                    String SENT = "SMS_SENT";

                    ArrayList<String> msgArray = sms.divideMessage(message);
                    sms.sendMultipartTextMessage(number, null, msgArray, null, null);
                    Toast.makeText(instace, "SMS Sent", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(instace, "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    static public void shareText(String shareText) {
        try {
            Intent sentIntent = new Intent(Intent.ACTION_SEND);
            sentIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            sentIntent.setType("text/plain");
            instace.startActivity(sentIntent);
        } catch (Exception e) {
            Toast.makeText(instace, "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void showLoading(String message) {
        progress = new ProgressDialog(this);
        progress.setMessage(message);
        progress.setCancelable(false);
        progress.show();
    }

    public static void dismiss() {
        try {
            if (progress.isShowing()) {
                progress.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void showToast(String message) throws IOException {
        Toast.makeText(instace, message, Toast.LENGTH_LONG).show();
    }

    static public String trimString(String str) throws IOException {
        return str.replace(" ", "");
    }

    static public void makeTost(String message) {
        Toast.makeText(instace, message, Toast.LENGTH_LONG).show();
    }

    public String rateString() {
        String method = sharedPreferencesUtils.getRateMethodCode();
        if (method.equals("3"))
            return "Clr";
        else
            return "Snf";
    }

    public boolean isDemo() {
        String isDemo = sharedPreferencesUtils.getISDemo();
        if (isDemo.equals("1"))
            return true;
        else {
            return false;
        }
    }

    public String userID() {
        String userID = sharedPreferencesUtils.getUserID();
        return userID;
    }

    public boolean isDemoNotAccess(String date) {
        if (isDemo()) {
            if (MainActivity.getInstace().demoDate.length() < 1) {
                MainActivity.getInstace().demoDate = sharedPreferencesUtils.getDemoDate();
            }
            String ddate = MainActivity.getInstace().demoDate.replace("-", "");
            if (Integer.parseInt(ddate) < Integer.parseInt(date)) {
                try {
                    showToast("Your Demo Entries are Finished");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    public void setStatus(String status) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("isLogin", status);
        editor.commit();
    }

    static public void makeToast(String message) {
        try {
            Toast.makeText(instace, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void importDB() {
        String current_db_name = "Data.db";
        String pre_db_name = "MyDBName";

        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;

        String dbPath = "/data/" + MainActivity.instace.getPackageName() + "/databases/";

        File preDB = new File(data, dbPath + pre_db_name);
        File currentDB = new File(data, dbPath + current_db_name);

        try {
            source = new FileInputStream(preDB).getChannel();
            destination = new FileOutputStream(currentDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(MainActivity.instace, "Please wait", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    public void shareDialog(final String printString, String title) {
        final CharSequence[] options = {"WhatsApp", "Mail", "Other Share", "Print"};
        android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(MainActivity.getInstace())
                .setTitle(title);
        adb.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("WhatsApp")) {
                    PackageManager pm = MainActivity.getInstace().getPackageManager();
                    try {
                        //   pe.diegoveloper.printerserverapp
                        Intent waIntent = new Intent(Intent.ACTION_SEND);
                        waIntent.setType("text/plain");
                        //  String text = "YOUR TEXT HERE";

                        PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                        //Check if package exists or not. If not then code
                        //in catch block will be called
                        waIntent.setPackage("com.whatsapp");

                        waIntent.putExtra(Intent.EXTRA_TEXT, printString);
                        startActivity(Intent.createChooser(waIntent, "Share with"));

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (options[item].equals("Mail")) {
                    String to = "";
                    String subject = "Milk Collection Report";
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                    email.putExtra(Intent.EXTRA_SUBJECT, subject);
                    email.putExtra(Intent.EXTRA_TEXT, printString);
                    // need this to prompts email client only
                    email.setType("message/rfc822");

                    startActivity(Intent.createChooser(email, "Choose an Email client"));


                } else if (options[item].equals("Other Share")) {

                    Intent sentIntent = new Intent(Intent.ACTION_SEND);
                    sentIntent.putExtra(Intent.EXTRA_TEXT, printString);
                    sentIntent.setType("text/plain");
                    startActivity(sentIntent);

                } else if (options[item].equals("Print")) {
                    print(printString);
                }
            }
        });
        adb.show();
    }


    public void focusOnTextField(EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public void shareFile(String filepath) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(filepath);
        Uri photoURI = FileProvider.getUriForFile(instace, instace.getApplicationContext().getPackageName() + ".provider", fileWithinMyDir);

        if (fileWithinMyDir.exists()) {
            intentShareFile.setType("application/pdf");
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intentShareFile.putExtra(Intent.EXTRA_STREAM, photoURI);
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File");
            startActivity(Intent.createChooser(intentShareFile, "Sharing File"));
        }
    }

    private static final int REQUEST_WRITE_STORAGE = 8000;
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.permission_request, Toast.LENGTH_LONG);
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_WRITE_STORAGE);
        }
    }
}



