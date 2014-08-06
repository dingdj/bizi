package com.mklodoss.SexyGirl.db;

import android.database.sqlite.SQLiteDatabase;
import com.mklodoss.SexyGirl.model.LocalBelle;
import com.mklodoss.SexyGirl.model.Series;
import com.mklodoss.SexyGirl.model.CollectedBelle;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import java.util.Map;

/**
 * Created by Administrator on 2014/8/6.
 */
public class DaoSession extends AbstractDaoSession {
    private final CollectedBelleDao collectedBelleDao;
    private final DaoConfig collectedBelleDaoConfig;
    private final LocalBelleDao localBelleDao;
    private final DaoConfig localBelleDaoConfig;
    private final SeriesDao seriesDao;
    private final DaoConfig seriesDaoConfig;

    public DaoSession(SQLiteDatabase paramSQLiteDatabase, IdentityScopeType paramIdentityScopeType, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> paramMap) {
        super(paramSQLiteDatabase);
        this.localBelleDaoConfig = ((DaoConfig) paramMap.get(LocalBelleDao.class)).clone();
        this.localBelleDaoConfig.initIdentityScope(paramIdentityScopeType);
        this.collectedBelleDaoConfig = ((DaoConfig) paramMap.get(CollectedBelleDao.class)).clone();
        this.collectedBelleDaoConfig.initIdentityScope(paramIdentityScopeType);
        this.seriesDaoConfig = ((DaoConfig) paramMap.get(SeriesDao.class)).clone();
        this.seriesDaoConfig.initIdentityScope(paramIdentityScopeType);
        this.localBelleDao = new LocalBelleDao(this.localBelleDaoConfig, this);
        this.collectedBelleDao = new CollectedBelleDao(this.collectedBelleDaoConfig, this);
        this.seriesDao = new SeriesDao(this.seriesDaoConfig, this);
        registerDao(LocalBelle.class, this.localBelleDao);
        registerDao(CollectedBelle.class, this.collectedBelleDao);
        registerDao(Series.class, this.seriesDao);
    }

    public void clear() {
        this.localBelleDaoConfig.getIdentityScope().clear();
        this.collectedBelleDaoConfig.getIdentityScope().clear();
        this.seriesDaoConfig.getIdentityScope().clear();
    }

    public CollectedBelleDao getCollectedBelleDao() {
        return this.collectedBelleDao;
    }

    public LocalBelleDao getLocalBelleDao() {
        return this.localBelleDao;
    }

    public SeriesDao getSeriesDao() {
        return this.seriesDao;
    }
}
