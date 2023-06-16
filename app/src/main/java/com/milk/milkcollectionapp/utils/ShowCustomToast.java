package com.milk.milkcollectionapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.milk.milkcollectionapp.R;

import es.dmoral.toasty.Toasty;

public class ShowCustomToast {
    private Context con;
    public final int ToastyError = 0;
    public final int ToastySuccess = 1;
    public final int ToastyInfo = 2;
    public final int ToastyWarning = 3;
    public final int ToastyNormal = 4;
    public final int ToastyNormalWithIcon = 5;

    public ShowCustomToast(Context context) {
        this.con = context;
    }

    public void showCustomToast(final Context con, final String toastString, final int ToastType) {
        Toasty.Config.getInstance()
                .tintIcon(true)
//                .setToastTypeface(Typeface.createFromAsset(con.getAssets(), CUSTOMFONTNAME)) // optional
                .setTextSize(18)
                .apply();

        ((Activity) con).runOnUiThread(() -> {
            switch (ToastType) {
                case ToastyError:
                    Toasty.error(con, toastString, Toast.LENGTH_LONG, true).show();
                    break;
                case ToastySuccess:
                    Toasty.success(con, toastString, Toast.LENGTH_LONG, true).show();
                    break;
                case ToastyInfo:
                    Toasty.info(con, toastString, Toast.LENGTH_LONG, true).show();
                    break;
                case ToastyWarning:
                    Toasty.warning(con, toastString, Toast.LENGTH_LONG, true).show();
                    break;
                case ToastyNormal:
                    Toasty.normal(con, toastString, Toast.LENGTH_LONG).show();
                    break;
                case ToastyNormalWithIcon:
                    Drawable icon = con.getResources().getDrawable(R.drawable.logo);
                    Toasty.normal(con, toastString, icon).show();
                    break;
                default:
                    break;
            }
        });
    }
}
