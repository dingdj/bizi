package com.mklodoss.SexyGirl.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mklodoss.SexyGirl.MainApplication;
import com.mklodoss.SexyGirl.db.DaoUtils;
import com.mklodoss.SexyGirl.db.SeriesDao;
import com.mklodoss.SexyGirl.event.SeriesUpdatedEvent;
import com.mklodoss.SexyGirl.model.Series;
import com.mklodoss.SexyGirl.volleyex.JsonCookieSupportRequest;
import de.greenrobot.event.EventBus;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by Administrator on 2014/8/6.
 */
public class SeriesHelper {
    private static SeriesHelper mInstance ;
    private List<Series> mSeriesList = new ArrayList();

    private SeriesHelper(Context context) {
        final SeriesDao localSeriesDao = DaoUtils.getDaoSession(context).getSeriesDao();
        this.mSeriesList = localSeriesDao.loadAll();
        if ((this.mSeriesList == null) || (this.mSeriesList.size() == 0))
        {
            this.mSeriesList = defaultSeries();
            localSeriesDao.insertInTx(this.mSeriesList);
        }
        this.mSeriesList.addAll(localSeries());
    }

    private List<Series> defaultSeries()
    {
        ArrayList localArrayList = new ArrayList();
        localArrayList.add(new Series(1, "性感美女", null, null, Integer.valueOf(1)));
        localArrayList.add(new Series(2, "岛国女友", null, null, Integer.valueOf(1)));
        localArrayList.add(new Series(3, "丝袜美腿", null, null, Integer.valueOf(1)));
        localArrayList.add(new Series(4, "有沟必火", null, null, Integer.valueOf(1)));
        localArrayList.add(new Series(5, "有沟必火", null, null, Integer.valueOf(1)));
        localArrayList.add(new Series(11, "明星美女", null, null, Integer.valueOf(1)));
        localArrayList.add(new Series(12, "甜素纯", null, null, Integer.valueOf(1)));
        localArrayList.add(new Series(13, "校花", null, null, Integer.valueOf(1)));
        return localArrayList;
    }

    public static SeriesHelper getInstance()
    {
        if(mInstance == null) {
            mInstance = new SeriesHelper(MainApplication._application);
        }
        return mInstance;
    }

    private List<Series> localSeries()
    {
        ArrayList localArrayList = new ArrayList();
        localArrayList.add(new Series(-1, "我的收藏", "本地", null, Integer.valueOf(1)));
        if ("com.michael.wallpaper".endsWith(AppRuntime.PACKAGE_NAME)) {
            localArrayList.add(new Series(-2, "隐藏美女", "本地", null, Integer.valueOf(1)));
        }
        return localArrayList;
    }

    public List<Series> getSeriesList()
    {
        return this.mSeriesList;
    }

    public void syncSeries(final Context paramContext)
    {
        final SeriesDao localSeriesDao = DaoUtils.getDaoSession(paramContext).getSeriesDao();
        JsonCookieSupportRequest request = new JsonCookieSupportRequest(Request.Method.POST, Config.URL+"/series/list?appid=29560&mode=2", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        List<Series> list = Config.convertCategory(jsonObject);
                        localSeriesDao.deleteAll();
                        SeriesHelper.this.mSeriesList.clear();
                        localSeriesDao.insertInTx(list);
                        SeriesHelper.this.mSeriesList.addAll(list);
                        SeriesHelper.this.mSeriesList.addAll(SeriesHelper.this.localSeries());
                        EventBus.getDefault().post(new SeriesUpdatedEvent());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MainApplication._application, "网络异常，请稍后重试", Toast.LENGTH_LONG);
                Log.e("22", volleyError.toString());
            }
        });
        MainApplication._application.getQueue().add(request);
    }
}
