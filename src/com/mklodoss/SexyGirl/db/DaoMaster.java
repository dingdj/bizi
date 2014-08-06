package com.mklodoss.SexyGirl.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

/**
 * Created by Administrator on 2014/8/6.
 */
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 4;

    public DaoMaster(SQLiteDatabase paramSQLiteDatabase) {
        super(paramSQLiteDatabase, 4);
        registerDaoClass(LocalBelleDao.class);
        registerDaoClass(CollectedBelleDao.class);
        registerDaoClass(SeriesDao.class);
    }

    public static void createAllTables(SQLiteDatabase paramSQLiteDatabase, boolean paramBoolean) {
        LocalBelleDao.createTable(paramSQLiteDatabase, paramBoolean);
        CollectedBelleDao.createTable(paramSQLiteDatabase, paramBoolean);
        SeriesDao.createTable(paramSQLiteDatabase, paramBoolean);
    }

    public static void dropAllTables(SQLiteDatabase paramSQLiteDatabase, boolean paramBoolean) {
        LocalBelleDao.dropTable(paramSQLiteDatabase, paramBoolean);
        CollectedBelleDao.dropTable(paramSQLiteDatabase, paramBoolean);
        SeriesDao.dropTable(paramSQLiteDatabase, paramBoolean);
    }

    public DaoSession newSession() {
        return new DaoSession(this.db, IdentityScopeType.Session, this.daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType paramIdentityScopeType) {
        return new DaoSession(this.db, paramIdentityScopeType, this.daoConfigMap);
    }

    public static class DevOpenHelper
            extends DaoMaster.OpenHelper {
        public DevOpenHelper(Context paramContext, String paramString, SQLiteDatabase.CursorFactory paramCursorFactory) {
            super(paramContext, paramString, paramCursorFactory);
        }

        public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {
            Log.i("greenDAO", "Upgrading schema from version " + paramInt1 + " to " + paramInt2 + " by dropping all tables");
            DaoMaster.dropAllTables(paramSQLiteDatabase, true);
            onCreate(paramSQLiteDatabase);
        }
    }

    public static abstract class OpenHelper
            extends SQLiteOpenHelper {
        public OpenHelper(Context paramContext, String paramString, SQLiteDatabase.CursorFactory paramCursorFactory) {
            super(paramContext, paramString, paramCursorFactory, 4);
        }

        public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
            Log.i("greenDAO", "Creating tables for schema version 4");
            DaoMaster.createAllTables(paramSQLiteDatabase, false);
        }
    }
}
