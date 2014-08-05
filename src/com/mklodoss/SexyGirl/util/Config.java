package com.mklodoss.SexyGirl.util;

import com.mklodoss.SexyGirl.model.Category;
import com.mklodoss.SexyGirl.model.ImageInfo;
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
    public static List<Category> convertCategory(JSONObject jsonObject) {
        List<Category> list = new ArrayList<Category>();
        try {
            JSONArray array = jsonObject.getJSONArray("seriesList");
            for (int i=0; i<array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                Category category = new Category();
                category.type = object.getInt("type");
                category.title = object.getString("title");
                list.add(category);
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
    public static List<ImageInfo> convertImageInfo(JSONObject jsonObject) {
        List<ImageInfo> list = new ArrayList<ImageInfo>();
        try {
            JSONArray array = jsonObject.getJSONArray("belles");
            for (int i=0; i<array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.id = object.getInt("id");
                imageInfo.time = object.getInt("time");
                imageInfo.type = object.getInt("type");
                imageInfo.url = object.getString("url");
                list.add(imageInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

}
