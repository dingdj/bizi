package com.mklodoss.SexyGirl.model;

import java.io.Serializable;

/**
 * Created by dingdj on 2014/8/4.
 */
public class Series implements Serializable {
    public int type;
    public String title;
    private String category;
    private Integer property;
    private String tag3;

    public Series() {}

    public Series(int paramInt, String paramString1, String paramString2, String paramString3, Integer paramInteger)
    {
        this.type = paramInt;
        this.title = paramString1;
        this.category = paramString2;
        this.tag3 = paramString3;
        this.property = paramInteger;
    }

    public String getCategory()
    {
        return this.category;
    }

    public Integer getProperty()
    {
        return this.property;
    }

    public String getTag3()
    {
        return this.tag3;
    }

    public String getTitle()
    {
        return this.title;
    }

    public int getType()
    {
        return this.type;
    }

    public void setCategory(String paramString)
    {
        this.category = paramString;
    }

    public void setProperty(Integer paramInteger)
    {
        this.property = paramInteger;
    }

    public void setTag3(String paramString)
    {
        this.tag3 = paramString;
    }

    public void setTitle(String paramString)
    {
        this.title = paramString;
    }

    public void setType(int paramInt)
    {
        this.type = paramInt;
    }
}
