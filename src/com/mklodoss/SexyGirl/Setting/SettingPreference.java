package com.mklodoss.SexyGirl.setting;

import android.content.Context;
import android.content.SharedPreferences;
import com.mklodoss.SexyGirl.MainApplication;

/**
 * Created by Administrator on 2014/8/6.
 */
public class SettingPreference {

    public static final String NAME = "config";
    private static SettingPreference settings;
    private static SharedPreferences sp;

    private static final String KEY_LAUNCHER_ON_START_DAY_TIME = "launcher_on_start_day_time";

    private static final String KEY_NAV_DRAWER_SELECT_ITEM = "key_nav_drawer_select_item";

    private int currentItem;


    private SettingPreference() {
        sp = MainApplication._application.getApplicationContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        currentItem = sp.getInt(KEY_NAV_DRAWER_SELECT_ITEM, 0);
    }

    /**
     * @return
     */
    public static SettingPreference getInstance() {
        if(settings == null) {
            settings = new SettingPreference();
        }
        return settings;
    }

    /**
     * 设置启动监听每日一次类型的回调时间
     *
     * @param createTime 回调时间
     */
    public void setLauncherOnStartDayTime(long createTime) {
        sp.edit().putLong(KEY_LAUNCHER_ON_START_DAY_TIME, createTime).commit();
    }

    /**
     * 获取上次启动监听每日一次类型的回调时间
     *
     * @return 回调时间
     */
    public long getLauncherOnStartDayTime() {
        return sp.getLong(KEY_LAUNCHER_ON_START_DAY_TIME, 0);
    }

    /**
     * 获取当前Item
     * @return
     */
    public int getCurrentItem() {
        return currentItem;
    }

    /**
     * 设置当前Item
     * @param currentItem
     */
    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
        sp.edit().putInt(KEY_NAV_DRAWER_SELECT_ITEM, currentItem).commit();
    }
}
