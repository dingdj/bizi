package com.mklodoss.SexyGirl.model;

/**
 * Created by Administrator on 2014/8/6.
 */
public class CollectedBelle {

    private long time;
    private String url;

    public CollectedBelle() {}

    public CollectedBelle(String paramString)
    {
        this.url = paramString;
    }

    public CollectedBelle(String paramString, long paramLong)
    {
        this.url = paramString;
        this.time = paramLong;
    }

    public long getTime()
    {
        return this.time;
    }

    public String getUrl()
    {
        return this.url;
    }

    public void setTime(long paramLong)
    {
        this.time = paramLong;
    }

    public void setUrl(String paramString)
    {
        this.url = paramString;
    }
}
