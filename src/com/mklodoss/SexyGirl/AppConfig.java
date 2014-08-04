package com.mklodoss.SexyGirl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.ddj.commonkit.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;

/**
 * 应用程序配置类
 * Created by dingdj on 2014/7/19.
 */
public class AppConfig {
    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";

    private final static String APP_CONFIG = "config";

    //保存cookie的key
    public final static String CONF_COOKIE = "cookie";


    private static AppConfig appConfig;

    private AppConfig(){

    }

    /**
     * @return
     */
    public static AppConfig getInstance(){
        if(appConfig == null){
            appConfig = new AppConfig();
        }
        return appConfig;
    }

    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    /**
     * 获取cookie
     * @param mContext
     * @return
     */
    public String getCookie(Context mContext) {
        return get(mContext, CONF_COOKIE);
    }


    /**
     * 这个使用自定义的property文件
     * @param key
     * @return
     */
    public String get(Context mContext, String key) {
        Properties props = get(mContext);
        return (props != null) ? props.getProperty(key) : null;
    }


    /**
     * 获取Properties
     * @param mContext
     * @return
     */
    public Properties get(Context mContext) {
        FileInputStream fis = null;
        Properties props = new Properties();
        try {
            // 读取app_config目录下的config
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            if(!dirConf.exists()) {
                dirConf.mkdir();
            }
            fis = new FileInputStream(dirConf.getPath() + File.separator
                    + APP_CONFIG);

            props.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return props;
    }


    /**
     * 保存Cookie
     * @param mContext
     * @param cookie
     */
    public void setCookie(Context mContext, String cookie){
        set(mContext, CONF_COOKIE, cookie);
    }

    /**
     * 设置属性
     * @param mContext
     * @param key
     * @param value
     */
    public void set(Context mContext, String key, String value) {
        Properties props = get(mContext);
        props.setProperty(key, value);
        setProps(mContext, props);
    }

    /**
     * 保存properties文件
     * @param p
     */
    private void setProps(Context mContext, Properties p) {
        FileOutputStream fos = null;
        try {
            // 把config建在(自定义)app_config的目录下
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            File conf = new File(dirConf, APP_CONFIG);
            fos = new FileOutputStream(conf);

            p.store(fos, null);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 處理cookie
     * @param headers
     */
    public void checkSessionCookie(Map<String, String> headers){
        if (headers.containsKey(SET_COOKIE_KEY)) {
            String cookie = headers.get(SET_COOKIE_KEY);
            if (cookie.length() > 0) {
                setCookie(MainApplication._application, cookie);
            }
        }
    }

    /**
     * 如果存在cookie設置
     * @param headers
     */
    public void setCookieIfHave(Map<String, String> headers){
        String cookie = getCookie(MainApplication._application);
        if(StringUtils.isNotEmpty(cookie)) {
            headers.put(COOKIE_KEY, cookie);
        }
    }

}
