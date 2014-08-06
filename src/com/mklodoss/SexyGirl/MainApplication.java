package com.mklodoss.SexyGirl;

import android.app.Application;
import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ddj.commonkit.android.apk.ApkUtil;
import com.mklodoss.SexyGirl.displayingbitmaps.util.ImageCache;
import com.mklodoss.SexyGirl.displayingbitmaps.util.ImageFetcher;
import com.mklodoss.SexyGirl.util.AppRuntime;
import com.mklodoss.SexyGirl.util.Config;
import com.mklodoss.SexyGirl.util.DeviceInfo;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;

/**
 * Created by dingdj on 2014/8/4.
 */
public class MainApplication extends Application {

    RequestQueue queue;

    public static MainApplication _application;

    /*private ImageFetcher mImageFetcher;

    private ImageCache.ImageCacheParams cacheParams;*/

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);
        _application = this;
       /* cacheParams =
                new ImageCache.ImageCacheParams(this, Config.IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.8f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size));
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);*/
        AppRuntime.PACKAGE_NAME = getPackageName(this);
        DeviceInfo.init(this);
        initImageLoader();
    }

    public RequestQueue getQueue() {
        return queue;
    }

    /*public ImageFetcher getmImageFetcher() {
        return mImageFetcher;
    }

    public ImageCache.ImageCacheParams getCacheParams() {
        return cacheParams;
    }*/

    private void initImageLoader() {
        File localFile = new File("/sdcard/." + getPackageName(getApplicationContext()) + "/imagecache");
        ImageLoaderConfiguration.Builder localBuilder;
        localBuilder = new ImageLoaderConfiguration.Builder(this).
                threadPoolSize(8).
                denyCacheImageMultipleSizesInMemory().
                memoryCacheSize(1024 * (1024 * (DeviceInfo.MEM_SIZE / 16))).
                memoryCache(new WeakMemoryCache()).
                diskCache(new UnlimitedDiscCache(localFile)).
                defaultDisplayImageOptions(
                        new DisplayImageOptions.Builder().
                                resetViewBeforeLoading(true).
                                cacheInMemory(true).
                                cacheOnDisk(true).
                                imageScaleType(ImageScaleType.EXACTLY).build());
        ImageLoader.getInstance().init(localBuilder.build());
    }


    public static String getPackageName(Context paramContext) {
        try {
            String str = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).packageName;
            return str;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return null;
    }

}
