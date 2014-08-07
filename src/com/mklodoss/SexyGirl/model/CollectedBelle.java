package com.mklodoss.SexyGirl.model;

/**
 * Created by Administrator on 2014/8/6.
 */
public class CollectedBelle extends LocalBelle{

    public CollectedBelle(long paramLong1, long paramLong2, int paramInt, String paramString1, String paramString2, String paramString3)
    {
        super(paramLong1, paramLong2, paramInt, paramString1, paramString2, paramString3);
    }

    public CollectedBelle(LocalBelle belle) {
        this.id = belle.id;
        this.desc = belle.desc;
        this.rawUrl = belle.rawUrl;
        this.time = belle.time;
        this.type = belle.type;
        this.url = belle.url;
    }

}
