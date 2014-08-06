package com.mklodoss.SexyGirl.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.mklodoss.SexyGirl.model.Series;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by Administrator on 2014/8/6.
 */
public class SeriesDao extends AbstractDao<Series, Void> {

    public static final String TABLENAME = "SERIES";

    public SeriesDao(DaoConfig paramDaoConfig)
    {
        super(paramDaoConfig);
    }

    public SeriesDao(DaoConfig paramDaoConfig, DaoSession paramDaoSession)
    {
        super(paramDaoConfig, paramDaoSession);
    }

    public static void createTable(SQLiteDatabase paramSQLiteDatabase, boolean paramBoolean)
    {
        if (paramBoolean) {}
        for (String str = "IF NOT EXISTS ";; str = "")
        {
            paramSQLiteDatabase.execSQL("CREATE TABLE " + str + "'SERIES' (" + "'TYPE' INTEGER NOT NULL ," + "'TITLE' TEXT NOT NULL ," + "'CATEGORY' TEXT," + "'TAG3' TEXT," + "'PROPERTY' INTEGER);");
            return;
        }
    }

    public static void dropTable(SQLiteDatabase paramSQLiteDatabase, boolean paramBoolean)
    {
        StringBuilder localStringBuilder = new StringBuilder().append("DROP TABLE ");
        if (paramBoolean) {}
        for (String str = "IF EXISTS ";; str = "")
        {
            paramSQLiteDatabase.execSQL(str + "'SERIES'");
            return;
        }
    }

    protected void bindValues(SQLiteStatement paramSQLiteStatement, Series paramSeries)
    {
        paramSQLiteStatement.clearBindings();
        paramSQLiteStatement.bindLong(1, paramSeries.getType());
        paramSQLiteStatement.bindString(2, paramSeries.getTitle());
        String str1 = paramSeries.getCategory();
        if (str1 != null) {
            paramSQLiteStatement.bindString(3, str1);
        }
        String str2 = paramSeries.getTag3();
        if (str2 != null) {
            paramSQLiteStatement.bindString(4, str2);
        }
        Integer localInteger = paramSeries.getProperty();
        if (localInteger != null) {
            paramSQLiteStatement.bindLong(5, localInteger.intValue());
        }
    }

    public Void getKey(Series paramSeries)
    {
        return null;
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public Series readEntity(Cursor paramCursor, int paramInt)
    {
        return new Series(paramCursor.getInt(paramInt + 0),
                paramCursor.getString(paramInt + 1),
                paramCursor.getString(paramInt + 2),
                paramCursor.getString(paramInt + 3),
                Integer.valueOf(paramCursor.getInt(paramInt + 4)));
    }

    public void readEntity(Cursor paramCursor, Series paramSeries, int paramInt)
    {
        paramSeries.setType(paramCursor.getInt(paramInt + 0));
        paramSeries.setTitle(paramCursor.getString(paramInt + 1));
        if (!paramCursor.isNull(paramInt + 2)){
            paramSeries.setCategory(paramCursor.getString(paramInt + 2));
        }
        if (!paramCursor.isNull(paramInt + 3)){
            paramSeries.setTag3(paramCursor.getString(paramInt + 3));
        }
        if (!paramCursor.isNull(paramInt + 4)){
            paramSeries.setProperty(Integer.valueOf(paramCursor.getInt(paramInt + 4)));
        }
    }

    public Void readKey(Cursor paramCursor, int paramInt)
    {
        return null;
    }

    protected Void updateKeyAfterInsert(Series paramSeries, long paramLong)
    {
        return null;
    }

    public static class Properties
    {
        public static final Property Category;
        public static final Property Property = new Property(4, Integer.class, "property", false, "PROPERTY");
        public static final Property Tag3;
        public static final Property Title;
        public static final Property Type = new Property(0, Integer.TYPE, "type", false, "TYPE");

        static
        {
            Title = new Property(1, String.class, "title", false, "TITLE");
            Category = new Property(2, String.class, "category", false, "CATEGORY");
            Tag3 = new Property(3, String.class, "tag3", false, "TAG3");
        }
    }
}
