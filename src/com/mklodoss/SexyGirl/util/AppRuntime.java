package com.mklodoss.SexyGirl.util;

import com.ddj.commonkit.StringUtils;

/**
 * Created by Administrator on 2014/8/6.
 */
public class AppRuntime {
    public static String PACKAGE_NAME = "";
    public static String RAW_URL_CACHE_DIR = "/sdcard/";

    public static String makeRawUrl(String paramString) {
        if (StringUtils.isNotEmpty(paramString)) {
            if (paramString.indexOf("_tn") != -1) {
                return paramString.replace("_tn", "");
            }
        }
        return paramString;
    }
}
