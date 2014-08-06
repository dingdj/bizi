package com.mklodoss.SexyGirl.util;

import com.mklodoss.SexyGirl.model.LocalBelle;
import com.mklodoss.SexyGirl.model.Series;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dingdj on 2014/8/4.
 */
public class Config {

    public static final String IMAGE_CACHE_DIR = "thumbs";
    public static final String URL = "http://mmpicture.duapp.com";
    public static final int APPID = 29560;
    public static final int MODE = 2;
    public static final String App_Id = "appid";
    public static final String Mode = "mode";
    public static final String FETCH_IMAGE_URL = URL + "/belle/random?appid=29560&count=50&type=";


    /**
     * 转换
     * @param jsonObject
     * @return
     */
    public static List<Series> convertCategory(JSONObject jsonObject) {
        List<Series> list = new ArrayList<Series>();
        try {
            JSONArray array = jsonObject.getJSONArray("seriesList");
            for (int i=0; i<array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                Series series = new Series();
                series.type = object.getInt("type");
                series.title = object.getString("title");
                series.setProperty(1);
                list.add(series);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 转换
     * @param jsonObject
     * @return
     */
    public static List<LocalBelle> convertLocalBelle(JSONObject jsonObject) {
        List<LocalBelle> list = new ArrayList<LocalBelle>();
        try {
            JSONArray array = jsonObject.getJSONArray("belles");
            for (int i=0; i<array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                LocalBelle localBelle = new LocalBelle();
                localBelle.id = object.getInt("id");
                localBelle.time = object.getInt("time");
                localBelle.type = object.getInt("type");
                localBelle.url = object.getString("url");
                localBelle.setRawUrl(AppRuntime.makeRawUrl(localBelle.url));
                list.add(localBelle);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

}
