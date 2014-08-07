package com.mklodoss.SexyGirl.util;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mklodoss.SexyGirl.MainApplication;
import com.mklodoss.SexyGirl.db.DaoUtils;
import com.mklodoss.SexyGirl.db.LocalBelleDao;
import com.mklodoss.SexyGirl.displayingbitmaps.ui.ImageGridFragment;
import com.mklodoss.SexyGirl.event.LocalBelleUpdatedEvent;
import com.mklodoss.SexyGirl.model.LocalBelle;
import com.mklodoss.SexyGirl.volleyex.JsonCookieSupportRequest;
import de.greenrobot.event.EventBus;
import org.json.JSONObject;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/8/6.
 */
public class BelleHelper {

    private static BelleHelper mInstance = new BelleHelper();
    private List<LocalBelle> localBelleList = new ArrayList();

    public static BelleHelper getInstance()
    {
        return mInstance;
    }

    /**
     * 获取数据 先从数据库获取 数据库没有则从网络获取
     * @param type
     * @return
     */
    public List<LocalBelle> getLocaleBell(Context context, int type, LocalBelleNotifyCallBack callBack) {
        //从数据库获取
        final LocalBelleDao localBelleDao = DaoUtils.getDaoSession(context).getLocalBelleDao();
        localBelleList = localBelleDao.queryRaw("where TYPE = ?", type+"");
        if(localBelleList.size() == 0) { //数据库中没有获取到 从网络中获取
            getLocaleBellFromNetwork(context, type, callBack);
        }
        return localBelleList;
    }

    /**
     * 从网络获取
     * @param context
     * @param type
     */
    public void getLocaleBellFromNetwork(Context context, int type, final LocalBelleNotifyCallBack callBack) {
        final LocalBelleDao localBelleDao = DaoUtils.getDaoSession(context).getLocalBelleDao();
        JsonCookieSupportRequest request = new JsonCookieSupportRequest(Request.Method.POST,
                Config.FETCH_IMAGE_URL+type, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        localBelleList = Config.convertLocalBelle(jsonObject);
                        localBelleDao.insertOrReplaceInTx(localBelleList);
                        if(callBack.getImageGridFragmentReference() != null) {
                            ImageGridFragment imageGridFragment = callBack.getImageGridFragmentReference().get();
                            if(imageGridFragment != null) {
                                imageGridFragment.updateBellList();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                android.util.Log.e("22", volleyError.toString());
            }
        });
        MainApplication._application.getQueue().add(request);
    }

    public List<LocalBelle> getLocalBelleList() {
        return localBelleList;
    }


    public static class LocalBelleNotifyCallBack {
        private Reference<ImageGridFragment> imageGridFragmentReference;

        public LocalBelleNotifyCallBack(Reference<ImageGridFragment> imageGridFragmentReference) {
            this.imageGridFragmentReference = imageGridFragmentReference;
        }

        public Reference<ImageGridFragment> getImageGridFragmentReference() {
            return imageGridFragmentReference;
        }

        public void setImageGridFragmentReference(Reference<ImageGridFragment> imageGridFragmentReference) {
            this.imageGridFragmentReference = imageGridFragmentReference;
        }
    }
}
