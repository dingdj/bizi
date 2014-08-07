package com.mklodoss.SexyGirl.util;

import android.content.Context;
import com.mklodoss.SexyGirl.MainApplication;
import com.mklodoss.SexyGirl.db.DaoSession;
import com.mklodoss.SexyGirl.db.DaoUtils;
import com.mklodoss.SexyGirl.model.CollectedBelle;
import com.mklodoss.SexyGirl.model.LocalBelle;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2014/8/7.
 */
public class CollectHelper {

    private static CollectHelper instance;

    public static CollectHelper getInstance() {
        if(instance == null) {
            instance = new CollectHelper(MainApplication._application);
        }
        return instance;
    }


    private Hashtable<String, Boolean> mCollectHashtable = new Hashtable();
    private Context mContext;
    private DaoSession mSession;

    private CollectHelper(Context context) {
        this.mContext = context.getApplicationContext();
        this.mSession = DaoUtils.getDaoSession(this.mContext);
        List<CollectedBelle> localList = loadAll();
        if (localList != null) {
            Iterator iterator = localList.iterator();
            while (iterator.hasNext()) {
                CollectedBelle collectedBelle = (CollectedBelle) iterator.next();
                this.mCollectHashtable.put(collectedBelle.getUrl(), Boolean.valueOf(true));
            }
        }
    }

    public void cancelCollectBelle(String string) {
        DaoUtils.getDaoSession(this.mContext).getCollectedBelleDao().deleteByKey(string);
        if (this.mCollectHashtable.containsKey(string)) {
            this.mCollectHashtable.put(string, Boolean.valueOf(false));
        }
    }

    public void collectBelle(LocalBelle belle) {
        DaoUtils.getDaoSession(this.mContext).getCollectedBelleDao().insertOrReplace(new CollectedBelle(belle));
        this.mCollectHashtable.put(belle.getUrl(), Boolean.valueOf(true));
    }

    public boolean isCollected(String string) {
        return (this.mCollectHashtable.containsKey(string)) && (((Boolean) this.mCollectHashtable.get(string)).booleanValue());
    }

    public List<CollectedBelle> loadAll() {
        return this.mSession.getCollectedBelleDao().loadAll();
    }
}
