package com.mklodoss.SexyGirl.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.mklodoss.SexyGirl.model.LocalBelle;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by Administrator on 2014/8/6.
 */
public class LocalBelleDao extends AbstractDao<LocalBelle, Void> {
    public static final String TABLENAME = "LOCAL_BELLE";

    public LocalBelleDao(DaoConfig paramDaoConfig) {
        super(paramDaoConfig);
    }

    public LocalBelleDao(DaoConfig paramDaoConfig, DaoSession paramDaoSession) {
        super(paramDaoConfig, paramDaoSession);
    }

    public static void createTable(SQLiteDatabase paramSQLiteDatabase, boolean paramBoolean) {
        if (paramBoolean) {
        }
        for (String str = "IF NOT EXISTS "; ; str = "") {
            paramSQLiteDatabase.execSQL("CREATE TABLE " + str + "'LOCAL_BELLE' (" + "'ID' INTEGER NOT NULL ," + "'TIME' INTEGER NOT NULL ," + "'TYPE' INTEGER NOT NULL ," + "'DESC' TEXT," + "'URL' TEXT NOT NULL ," + "'RAW_URL' TEXT);");
            return;
        }
    }

    public static void dropTable(SQLiteDatabase paramSQLiteDatabase, boolean paramBoolean) {
        StringBuilder localStringBuilder = new StringBuilder().append("DROP TABLE ");
        if (paramBoolean) {
        }
        for (String str = "IF EXISTS "; ; str = "") {
            paramSQLiteDatabase.execSQL(str + "'LOCAL_BELLE'");
            return;
        }
    }

    protected void bindValues(SQLiteStatement paramSQLiteStatement, LocalBelle paramLocalBelle) {
        paramSQLiteStatement.clearBindings();
        paramSQLiteStatement.bindLong(1, paramLocalBelle.getId());
        paramSQLiteStatement.bindLong(2, paramLocalBelle.getTime());
        paramSQLiteStatement.bindLong(3, paramLocalBelle.getType());
        String str1 = paramLocalBelle.getDesc();
        if (str1 != null) {
            paramSQLiteStatement.bindString(4, str1);
        }
        paramSQLiteStatement.bindString(5, paramLocalBelle.getUrl());
        String str2 = paramLocalBelle.getRawUrl();
        if (str2 != null) {
            paramSQLiteStatement.bindString(6, str2);
        }
    }

    public Void getKey(LocalBelle paramLocalBelle) {
        return null;
    }

    protected boolean isEntityUpdateable() {
        return true;
    }

    public LocalBelle readEntity(Cursor paramCursor, int paramInt) {
        long id = paramCursor.getLong(paramInt + 0);
        long time = paramCursor.getLong(paramInt + 1);
        int type = paramCursor.getInt(paramInt + 2);
        String desc = "";
        String url = "";
        String rawUrl = "";
        if (!paramCursor.isNull(paramInt + 3)) {
            desc = paramCursor.getString(paramInt + 3);
        }
        if (!paramCursor.isNull(paramInt + 4)) {
            url = paramCursor.getString(paramInt + 4);
        }
        if (!paramCursor.isNull(paramInt + 5)) {
            rawUrl = paramCursor.getString(paramInt + 5);
        }

        return new LocalBelle(id, time, type, desc, url, rawUrl);
    }

    public void readEntity(Cursor paramCursor, LocalBelle paramLocalBelle, int paramInt) {
        paramLocalBelle.setId(paramCursor.getLong(paramInt + 0));
        paramLocalBelle.setTime(paramCursor.getLong(paramInt + 1));
        paramLocalBelle.setType(paramCursor.getInt(paramInt + 2));
        if (!paramCursor.isNull(paramInt + 3)) {
            paramLocalBelle.setDesc(paramCursor.getString(paramInt + 3));
        }

        if (!paramCursor.isNull(paramInt + 4)) {
            paramLocalBelle.setUrl(paramCursor.getString(paramInt + 4));
        }

        if (!paramCursor.isNull(paramInt + 5)) {
            paramLocalBelle.setRawUrl(paramCursor.getString(paramInt + 5));
        }
    }

    public Void readKey(Cursor paramCursor, int paramInt) {
        return null;
    }

    protected Void updateKeyAfterInsert(LocalBelle paramLocalBelle, long paramLong) {
        return null;
    }

    public static class Properties {
        public static final Property Desc;
        public static final Property Id = new Property(0, Long.TYPE, "id", false, "ID");
        public static final Property RawUrl = new Property(5, String.class, "rawUrl", false, "RAW_URL");
        public static final Property Time = new Property(1, Long.TYPE, "time", false, "TIME");
        public static final Property Type = new Property(2, Integer.TYPE, "type", false, "TYPE");
        public static final Property Url;

        static {
            Desc = new Property(3, String.class, "desc", false, "DESC");
            Url = new Property(4, String.class, "url", false, "URL");
        }
    }
}
