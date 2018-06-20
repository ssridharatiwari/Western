package com.milk.milkcollection.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.milk.milkcollection.Fragment.Fragment_Master;
import com.milk.milkcollection.Fragment.Fragment_Report;
import com.milk.milkcollection.Fragment.Fragment_Setting;
import com.milk.milkcollection.Fragment.Fragment_home;
import com.milk.milkcollection.R;
import com.milk.milkcollection.helper.FSSession;
import com.milk.milkcollection.helper.SharedPreferencesUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;



public class MainActivity extends AppCompatActivity {


    public  static String ruppe = "₹";

    private ArrayList<String> categories;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    public CharSequence mDrawerTitle;
    public CharSequence mTitle;
    Toolbar toolbar;
    public TextView toolbartitle;
    public ImageView toolbariv_home;
    FSSession fsSession;


    NetworkTask networktask;
    public static InputStream nis;
    public static OutputStream nos;
    public static Socket nsocket = new Socket();
    public static SocketAddress sockaddr;


    public static ProgressDialog progress;
    public static MainActivity instace;
    public String demoDate = "";

    SharedPreferencesUtils sharedPreferencesUtils;



    public static MainActivity getInstace(){
        if(instace == null){
            instace = new MainActivity ();
        }
        return instace;
    }
    int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        sharedPreferencesUtils = new SharedPreferencesUtils(MainActivity.this);
        instace = MainActivity.this;

        fsSession = new FSSession(MainActivity.this);

        categories = new ArrayList<>();
        categories.add(getResources().getString(R.string.home));
        categories.add(getResources().getString(R.string.master));
        categories.add(getResources().getString(R.string.report));
        categories.add("Setting");
        categories.add(getResources().getString(R.string.planguage));

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        toolbartitle = (TextView) findViewById(R.id.titletool);
        toolbariv_home = (ImageView) findViewById(R.id.iv_home);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, categories));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            @SuppressLint("RestrictedApi")
            public void onDrawerClosed(View view) {
                //getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @SuppressLint("RestrictedApi")
            public void onDrawerOpened(View drawerView) {
                //getSupportActionBar().setTitle(mDrawerTitle);
                toolbartitle.setText(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        //toolbartitle.setText("Milk Collection");
        mDrawerToggle.syncState();

        toolbariv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment currentFragment = getFragmentManager().findFragmentById(R.id.content_frame);
                String backStateName = currentFragment.getClass().getName();

                Log.e(" currentFragment ", currentFragment + "");
                Log.e(" currentFragment Tag ", backStateName + "");

                if (!backStateName.equals("com.milk.milkcollection.Fragment.Fragment_home")) {

                    toolbartitle.setText(getResources().getString(R.string.home));
                    Fragment fragment = new Fragment_home();
                    android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();

                    ft.replace(R.id.content_frame, fragment);
                    /// ft.replace(R.id.content_frame,);
                    ft.setTransition(ft.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });

       if (savedInstanceState == null) {
            selectItem(0);
       }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        // Load your conten here

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(categories.get(position));
        mDrawerLayout.closeDrawer(mDrawerList);

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

        } else if (position == 3 && !backStateName.equals("Print by")) {

            toolbartitle.setText(getResources().getString(R.string.setting));
            toolbartitle.setText(getResources().getString(R.string.setting));
            replaceFragment(new Fragment_Setting());

        }
        else if (position == 4) {
                changeLanguage(MainActivity.this);
        }



        /*   else if (position == 3) {
            toolbartitle.setText("Add Member");
            Fragment fragment = new Fragment_Addmember();
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment,"Add member");
            ft.addToBackStack("home");
            ft.commit();
        }*/

    }


    private void replaceFragment(Fragment fragment) {

        Log.e(" new Fragment ", fragment + "");
        String backStateName = fragment.getClass().getName();
        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(backStateName + "");
        ft.commit();
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        //getSupportActionBar().setTitle(mTitle);
        //toolbartitle.setText(mTitle);
    }

     /*
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
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


    public void stopSocket()  {
        try {
            if (nsocket.isConnected()) {

                nis.close();
                nos.close();
                nsocket.close();

            } else {
                // Log.e(" is ", "No Connected");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void runConnection() {

        try {
            sockaddr = new InetSocketAddress("192.168.4.1", 80);
            nsocket = new Socket();
            networktask = new NetworkTask();
            networktask.execute(new Void[0]);
            synchronized (nsocket) {
                nsocket.wait(1000000);
            }
            if (nsocket.isConnected()) {

            } else {
                // Log.e(" is ", "No Connected");
            }
        } catch (Exception e) {

            e.getStackTrace();
        }
    }



    private class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                sockaddr = new InetSocketAddress("192.168.4.1", 80);
                nsocket = new Socket();
                networktask = new NetworkTask();
                networktask.execute(new Void[0]);
                synchronized (nsocket) {
                    nsocket.wait(10000);
                }
                if (nsocket.isConnected()) {

                    Log.e(" is ", " Connected");

                } else {
                    Log.e(" is ", "No Connected");
                }
            } catch (Exception e) {

                e.getStackTrace();


                try {
                    nis.close();
                    nos.close();
                    nsocket.close();
                } catch (IOException e2) {
                } catch (Exception e3) {
                }
            }



           // Socket socket = null;
          //  InetSocketAddress sockaddr = null;


        }

    }

    private class NetworkTask extends AsyncTask<Void, byte[], Boolean> {
        protected void onPreExecute() {
        }

        protected Boolean doInBackground(Void... params) {
            boolean result = false;
            try {
                nsocket.connect(sockaddr, 1000);
                synchronized (nsocket) {
                    nsocket.notify();
                }
                if (nsocket.isConnected()) {
                    nis = nsocket.getInputStream();
                    nos = nsocket.getOutputStream();
                    byte[] buffer = new byte[4096];
                    int read = nis.read(buffer, 0, 4096);
                    while (read != -1) {
                        System.arraycopy(buffer, 0, new byte[read], 0, read);
                        publishProgress(new byte[][]{});
                        //   publishProgress(new byte[][]{tempdata});
                        read = nis.read(buffer, 0, 4096);
                    }
                }
                synchronized (nsocket) {
                    nsocket.notify();
                }
                try {
                    nis.close();
                    nos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } catch (IOException e3) {
                try {
                    e3.printStackTrace();
                    synchronized (nsocket) {
                        nsocket.notify();
                        result = true;
                        synchronized (nsocket) {
                            nsocket.notify();
                            try {
                                nis.close();
                                nos.close();
                            } catch (IOException e32) {
                                e32.printStackTrace();
                            } catch (Exception e22) {
                                e22.printStackTrace();
                            }
                        }
                    }
                } catch (Throwable th) {
                    synchronized (nsocket) {
                        nsocket.notify();
                        try {
                            nis.close();
                            nos.close();
                        } catch (IOException e322) {
                            e322.printStackTrace();
                        } catch (Exception e222) {
                            e222.printStackTrace();
                        }
                    }
                }
            } catch (Exception e2222) {
                e2222.printStackTrace();
                synchronized (nsocket) {
                    nsocket.notify();
                    result = true;
                    synchronized (nsocket) {
                        nsocket.notify();
                        try {
                            nis.close();
                            nos.close();
                        } catch (Exception e22222) {
                            e22222.printStackTrace();
                        }
                    }
                }
            }
            return Boolean.valueOf(result);
        }

        protected void onProgressUpdate(byte[]... values) {
            String toReceive = new String();
            String newText = "hellvcv fv";
            // String newText = new String();
            toReceive = "";

            //  newText = editReceive.getText().toString();
            /* if (checkHex.isChecked()) {
                try {
                    toReceive = myutility.convertStringToHex(new String(values[0], "ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    toReceive = new String(values[0], "ISO-8859-1");
                } catch (UnsupportedEncodingException e2) {
                    e2.printStackTrace();
                }
            }*/

            if (values.length > 0) {

                String toReceives = new StringBuilder(String.valueOf(newText)).append(toReceive).toString();
                Log.e(" hello this is one ", toReceives);
            }
        }

        protected void onCancelled() {
        }
    }

    static public void print(String  printSTRING) throws IOException {

        if (printSTRING.length() > 0) {

            SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(instace.getInstace());

            String printBy = sharedPreferencesUtils.getprintBy();



                if (printBy.equals("wifi")) {

                    try {
                        nos = nsocket.getOutputStream();
                        nos.write(printSTRING.getBytes("UTF-8"));
                        nos.write("\n\n\n".getBytes("UTF-8"));
                    } catch (Exception e) {
                        MainActivity.instace.runConnection();

                        nos = nsocket.getOutputStream();
                        nos.write(printSTRING.getBytes("UTF-8"));
                        nos.write("\n\n\n".getBytes("UTF-8"));

                        // Toast.makeText(instace.getInstace(), "Print failed, please try again.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                } else if (printBy.equals("blutooth")) {
                    {
                        PackageManager pm = instace.getPackageManager();
                        try {

                            Intent waIntent = new Intent(Intent.ACTION_SEND);
                            waIntent.setType("text/plain");
                            //   String text = "YOUR TEXT HERE";

                            PackageInfo info = pm.getPackageInfo("pe.diegoveloper.printerserverapp", PackageManager.GET_META_DATA);
                            //Check if package exists or not. If not then code
                            //in catch block will be called
                            waIntent.setPackage("pe.diegoveloper.printerserverapp");

                            waIntent.putExtra(Intent.EXTRA_TEXT, printSTRING );
                            //waIntent.putExtra(Intent.EXTRA_TEXT, "1234567890123456780912345678901234567890");
                            instace.getInstace().startActivity(Intent.createChooser(waIntent, "Share with"));

                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();

                            Toast.makeText(instace.getInstace(), "Please Install Printer Application", Toast.LENGTH_LONG).show();
                        }
                    }

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

    static public String oneDecimalString(String  value) throws IOException {
        try {
            return String.format("%.1f", Float.parseFloat(value));
        }catch (Exception e) {
            return "0.0";
        }
    }

    static public String twoDecimalString(String  value) throws IOException {
        try {
            String newStr = value.replace(" ", "");
                if (newStr.length()>0)
                            return String.format("%.1f", Float.parseFloat(newStr));
                else
                    return "0.0";

        }catch (Exception e) {
            return "0.0";
        }
    }

    static public String oneDecimalFloatToString(float  value) throws IOException {
        return String.format("%.1f", value);
    }

    static public String twoDecimalFloatToString(float  value) throws IOException {
        return  String.format("%.2f", value);
    }

    static public String lineBreak() throws IOException {
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

        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

///             Toast.makeText(this,"Alredy DONE",Toast.LENGTH_SHORT).show();
                TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }



            } else {
                Toast.makeText(this,"ehgehfg",Toast.LENGTH_SHORT).show();
            }
        }
    }


    static public void sendTextSms(String message , String number) {



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

                        sentPI = PendingIntent.getBroadcast(instace, 0,new Intent(SENT), 0);

                        ArrayList<String> msgArray = sms.divideMessage(message);

                        sms.sendMultipartTextMessage(number, null,msgArray, null, null);
                        Toast.makeText(instace, "SMS Sent", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(instace,"SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
    }

    static public void shareText(String shareText ) {
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

    public void showLoading(String message){

        progress = new ProgressDialog(this);
        progress.setMessage(message);
        progress.setCancelable(false);
        progress.show();
    }

    public static void dismiss(){

        try {

                if (progress.isShowing()) {
                    progress.dismiss();
                }


        } catch (Exception e) {

        }




    }

    static public void showToast(String message) throws IOException  {

            Toast.makeText(instace, message, Toast.LENGTH_LONG).show();
    }
    static public String trimString(String str) throws IOException {
        return str.replace(" ", "");
    }

    public String rateString() throws IOException {

        String method = sharedPreferencesUtils.getRateMethodCode();
        if (method.equals("3"))
            return "Clr";
        else
            return "Snf";

    }

    private void GetCaledarDate() {

        Calendar calendar = Calendar.getInstance();
        calendar.getTime();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int ampm = calendar.get(Calendar.AM_PM);
        String date = String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year);

        int sssd = String.valueOf(year).length();
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

    public void decDemoCount() {

        if (isDemo()){
            int count = sharedPreferencesUtils.getDemoCount();
            count--;
            sharedPreferencesUtils.setDmeoCount(count);
        }
    }

    public boolean isDemoNotAccess(String date)  {

        if (isDemo()){

            if (MainActivity.getInstace().demoDate.length() < 1) {
                MainActivity.getInstace().demoDate = sharedPreferencesUtils.getDemoDate();
            }

            String ddate =  MainActivity.getInstace().demoDate.replace("-","");

            if (Integer.parseInt(ddate) < Integer.parseInt(date)){
                try {
                    showToast("Your Demo Entries are Finished");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return true;
            }

        }return false;
    }

    public void setStatus (String status){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("isLogin", status);
        editor.commit();
    }

    static public void makeToast(String message)   {

        try {

            Toast.makeText(instace, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {

        }
    }

    public void importDB() {

        // preExportDB();



        String current_db_name = "Data.db";
        String pre_db_name = "MyDBName";


        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;

        String dbPath = "/data/" + MainActivity.instace.getPackageName() + "/databases/" ;

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




    public void shareDialog(final String printString , String title) {

        final CharSequence[] options = { "WhatsApp", "Mail","Other Share","Print" };
        android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(MainActivity.getInstace())
                .setTitle(title);
        adb.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("WhatsApp")) {
                    PackageManager pm = MainActivity.getInstace().getPackageManager();
                    try {
//pe.diegoveloper.printerserverapp
                        Intent waIntent = new Intent(Intent.ACTION_SEND);
                        waIntent.setType("text/plain");
                        //   String text = "YOUR TEXT HERE";

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


                } else if (options[item].equals("Other Share"))
                {

                    Intent sentIntent = new Intent(Intent.ACTION_SEND);
                    sentIntent.putExtra(Intent.EXTRA_TEXT, printString);
                    sentIntent.setType("text/plain");
                    startActivity(sentIntent);

                }
                else if (options[item].equals("Print")) {
                    try {
                        print(printString);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        adb.show();
    }


}

