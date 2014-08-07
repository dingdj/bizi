package com.mklodoss.SexyGirl.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2014/8/7.
 */
public class Toaster {
    private static Toast toast;

    public static void show(Context context, int resId) {
        show(context, context.getString(resId));
    }

    public static void show(Context context, String string) {
        if (toast == null) {
            toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
        } else {
            toast.setText(string);
        }
        toast.show();
    }
}
