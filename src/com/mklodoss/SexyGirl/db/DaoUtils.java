package com.mklodoss.SexyGirl.db;

import android.content.Context;

/**
 * Created by Administrator on 2014/8/6.
 */
public class DaoUtils {
    private static final String DATABASE_NAME = "sexy_belles";
    private static DaoSession sDaoSession;

    public static DaoSession getDaoSession(Context paramContext) {
        try {
            if (sDaoSession == null) {
                sDaoSession = new DaoMaster(new DaoMaster.DevOpenHelper(paramContext, DATABASE_NAME, null).getWritableDatabase()).newSession();
            }
            DaoSession localDaoSession = sDaoSession;
            return localDaoSession;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
