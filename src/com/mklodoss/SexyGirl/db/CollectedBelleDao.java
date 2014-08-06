package com.mklodoss.SexyGirl.db;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.mklodoss.SexyGirl.model.CollectedBelle;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
/**
 * Created by Administrator on 2014/8/6.
 */
public class CollectedBelleDao extends AbstractDao<CollectedBelle, String>
{
    public static final String TABLENAME = "COLLECTED_BELLE";

    public CollectedBelleDao(DaoConfig paramDaoConfig)
    {
        super(paramDaoConfig);
    }

    public CollectedBelleDao(DaoConfig paramDaoConfig, DaoSession paramDaoSession)
    {
        super(paramDaoConfig, paramDaoSession);
    }

    public static void createTable(SQLiteDatabase paramSQLiteDatabase, boolean paramBoolean)
    {
        if (paramBoolean) {}
        for (String str = "IF NOT EXISTS ";; str = "")
        {
            paramSQLiteDatabase.execSQL("CREATE TABLE " + str + "'COLLECTED_BELLE' (" + "'URL' TEXT PRIMARY KEY NOT NULL ," + "'TIME' INTEGER NOT NULL );");
            return;
        }
    }

    public static void dropTable(SQLiteDatabase paramSQLiteDatabase, boolean paramBoolean)
    {
        StringBuilder localStringBuilder = new StringBuilder().append("DROP TABLE ");
        if (paramBoolean) {}
        for (String str = "IF EXISTS ";; str = "")
        {
            paramSQLiteDatabase.execSQL(str + "'COLLECTED_BELLE'");
            return;
        }
    }

    protected void bindValues(SQLiteStatement paramSQLiteStatement, CollectedBelle paramCollectedBelle)
    {
        paramSQLiteStatement.clearBindings();
        paramSQLiteStatement.bindString(1, paramCollectedBelle.getUrl());
        paramSQLiteStatement.bindLong(2, paramCollectedBelle.getTime());
    }

    public String getKey(CollectedBelle paramCollectedBelle)
    {
        if (paramCollectedBelle != null) {
            return paramCollectedBelle.getUrl();
        }
        return null;
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public CollectedBelle readEntity(Cursor paramCursor, int paramInt)
    {
        return new CollectedBelle(paramCursor.getString(paramInt + 0), paramCursor.getLong(paramInt + 1));
    }

    public void readEntity(Cursor paramCursor, CollectedBelle paramCollectedBelle, int paramInt)
    {
        paramCollectedBelle.setUrl(paramCursor.getString(paramInt + 0));
        paramCollectedBelle.setTime(paramCursor.getLong(paramInt + 1));
    }

    public String readKey(Cursor paramCursor, int paramInt)
    {
        return paramCursor.getString(paramInt + 0);
    }

    protected String updateKeyAfterInsert(CollectedBelle paramCollectedBelle, long paramLong)
    {
        return paramCollectedBelle.getUrl();
    }

    public static class Properties
    {
        public static final Property Time = new Property(1, Long.TYPE, "time", false, "TIME");
        public static final Property Url = new Property(0, String.class, "url", true, "URL");
    }
}
