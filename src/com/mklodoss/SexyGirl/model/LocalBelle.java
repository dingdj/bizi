package com.mklodoss.SexyGirl.model;

/**
 * Created by dingdj on 2014/8/4.
 */
public class LocalBelle {
    public long id;
    public long time;
    public int type;
    public String url;
    private String desc;
    private String rawUrl;

    public LocalBelle() {}

    public LocalBelle(long paramLong1, long paramLong2, int paramInt, String paramString1, String paramString2, String paramString3)
    {
        this.id = paramLong1;
        this.time = paramLong2;
        this.type = paramInt;
        this.desc = paramString1;
        this.url = paramString2;
        this.rawUrl = paramString3;
    }

    public String getDesc()
    {
        return this.desc;
    }

    public long getId()
    {
        return this.id;
    }

    public String getRawUrl()
    {
        return this.rawUrl;
    }

    public long getTime()
    {
        return this.time;
    }

    public int getType()
    {
        return this.type;
    }

    public String getUrl()
    {
        return this.url;
    }

    public void setDesc(String paramString)
    {
        this.desc = paramString;
    }

    public void setId(long paramLong)
    {
        this.id = paramLong;
    }

    public void setRawUrl(String paramString)
    {
        this.rawUrl = paramString;
    }

    public void setTime(long paramLong)
    {
        this.time = paramLong;
    }

    public void setType(int paramInt)
    {
        this.type = paramInt;
    }

    public void setUrl(String paramString)
    {
        this.url = paramString;
    }
}
