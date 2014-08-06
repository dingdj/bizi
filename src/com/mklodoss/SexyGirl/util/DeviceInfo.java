package com.mklodoss.SexyGirl.util;

import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * Created by Administrator on 2014/8/6.
 */
public class DeviceInfo {
    public static float DENSITY;
    public static int DENSITY_DPI;
    public static String IMEI;
    public static String IMSI;
    public static String MAC_ADDRESS;
    public static int MEM_SIZE;
    public static String PHONE_NUMBER;
    public static int SCREEN_HEIGHT;
    public static int SCREEN_WIDTH;

    public static void init(Context paramContext) {
        DENSITY_DPI = paramContext.getResources().getDisplayMetrics().densityDpi;
        DENSITY = paramContext.getResources().getDisplayMetrics().density;
        SCREEN_WIDTH = paramContext.getResources().getDisplayMetrics().widthPixels;
        SCREEN_HEIGHT = paramContext.getResources().getDisplayMetrics().heightPixels;
        MEM_SIZE = ((ActivityManager) paramContext.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = localTelephonyManager.getDeviceId();
        IMSI = localTelephonyManager.getSubscriberId();
        PHONE_NUMBER = localTelephonyManager.getLine1Number();
        WifiInfo localWifiInfo = ((WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        if (localWifiInfo != null) {
            MAC_ADDRESS = localWifiInfo.getMacAddress();
        }
    }
}
