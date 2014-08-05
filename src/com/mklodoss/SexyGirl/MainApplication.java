package com.mklodoss.SexyGirl;

import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mklodoss.SexyGirl.displayingbitmaps.util.ImageCache;
import com.mklodoss.SexyGirl.displayingbitmaps.util.ImageFetcher;
import com.mklodoss.SexyGirl.util.Config;

/**
 * Created by dingdj on 2014/8/4.
 */
public class MainApplication extends Application{

    RequestQueue queue;

    public static  MainApplication _application;

    private ImageFetcher mImageFetcher;

    private ImageCache.ImageCacheParams cacheParams;

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);
        _application = this;
        cacheParams =
                new ImageCache.ImageCacheParams(this, Config.IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.8f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size));
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
    }

    public RequestQueue getQueue() {
        return queue;
    }

    public ImageFetcher getmImageFetcher() {
        return mImageFetcher;
    }

    public ImageCache.ImageCacheParams getCacheParams() {
        return cacheParams;
    }

}
