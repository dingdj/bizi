/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mklodoss.SexyGirl.displayingbitmaps.ui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;
import com.mklodoss.SexyGirl.BuildConfig;
import com.mklodoss.SexyGirl.R;
import com.mklodoss.SexyGirl.displayingbitmaps.util.ImageCache;
import com.mklodoss.SexyGirl.displayingbitmaps.util.ImageFetcher;
import com.mklodoss.SexyGirl.displayingbitmaps.util.Utils;
import com.mklodoss.SexyGirl.model.LocalBelle;
import com.mklodoss.SexyGirl.util.AppRuntime;
import com.mklodoss.SexyGirl.util.CollectHelper;
import com.mklodoss.SexyGirl.util.Toaster;
import org.apache.http.Header;

import java.io.File;
import java.io.FileInputStream;

public class ImageDetailActivity extends FragmentActivity implements OnClickListener {
    private static final String IMAGE_CACHE_DIR = "images";
    public static final String EXTRA_IMAGE = "extra_image";
    public static final String EXTRA_CATEGORY = "category";

    private ImagePagerAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    private ViewPager mPager;
    private ProgressDialog mProgressDialog;
    private volatile int position;
    private CollectHelper mCollectHelper;
    private static final int WHAT_SAVE_FAIL = 2000;
    private static final int WHAT_SAVE_SUCCESS = 1000;
    private static final int WHAT_WALLPAPER_FAIL = 4000;
    private static final int WHAT_WALLPAPER_SUCCESS = 3000;

    private int category;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            super.handleMessage(message);
            switch (message.what) {
                case WHAT_SAVE_FAIL:
                    Toaster.show(ImageDetailActivity.this, R.string.save_gallery_fail);
                    break;
                case WHAT_SAVE_SUCCESS:
                    Toaster.show(ImageDetailActivity.this, R.string.save_gallery_success);
                    break;
                case WHAT_WALLPAPER_FAIL:
                    Toaster.show(ImageDetailActivity.this, R.string.set_wallpaper_fail);
                    break;
                case WHAT_WALLPAPER_SUCCESS:
                    Toaster.show(ImageDetailActivity.this, R.string.set_wallpaper_success);
                    break;

                default:
                    break;
            }
        }
    };

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Utils.enableStrictMode();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_pager);
        mCollectHelper = CollectHelper.getInstance();

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Fetch screen height and width, to use as our max size when loading images as this
        // activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        // For this sample we'll use half of the longest width to resize our images. As the
        // image scaling ensures the image is larger than this, we should be left with a
        // resolution that is appropriate for both portrait and landscape. For best image quality
        // we shouldn't divide by 2, but this will use more memory and require a larger memory
        // cache.
        final int longest = (height > width ? height : width) / 2;

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, longest);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);

        // Set up ViewPager and backing adapter
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), ImageGridFragment.list.size());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setPageMargin((int) getResources().getDimension(R.dimen.horizontal_page_margin));
        mPager.setOffscreenPageLimit(2);

        // Set up activity to go full screen
        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);

        // Enable some additional newer visibility and ActionBar features to create a more
        // immersive photo viewing experience
        if (Utils.hasHoneycomb()) {
           /* final ActionBar actionBar = getActionBar();

            // Hide title text and set home as up
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);*/

            // Hide and show the ActionBar as the visibility changes
            mPager.setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int vis) {
                            if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                                //actionBar.hide();
                            } else {
                                //actionBar.show();
                            }
                        }
                    });

            // Start low profile mode and hide ActionBar
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            //actionBar.hide();
        }

        // Set the current item based on the extra passed in to this activity
        position = getIntent().getIntExtra(EXTRA_IMAGE, -1);
        category = getIntent().getIntExtra(EXTRA_CATEGORY, -100);
        Log.e("PPPP", "OO position:" + position);
        if (position != -1) {
            mPager.setCurrentItem(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_save:
                downloadLargePic();
                return true;
            case R.id.action_wallpaper:
                setWallpaper();
                return true;
            case R.id.action_collect:
                saveToMyCollect();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called by the ViewPager child fragments to load images via the one ImageFetcher
     */
    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

    /**
     * The main adapter that backs the ViewPager. A subclass of FragmentStatePagerAdapter as there
     * could be a large number of items in the ViewPager and we don't want to retain them all in
     * memory at once but create/destroy them on the fly.
     */
    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        private final int mSize;

        public ImagePagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public Fragment getItem(int position) {
            return ImageDetailFragment.newInstance(ImageGridFragment.list.get(position).url);
        }
    }

    /**
     * Set on the ImageView in the ViewPager children fragments, to enable/disable low profile mode
     * when the ImageView is touched.
     */
    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View v) {
        final int vis = mPager.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(category != -1) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.view_large, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    /**
     * 设为壁纸
     */
    private void setWallpaper() {
        position = mPager.getCurrentItem();
        new Thread() {
            @Override
            public void run() {
                if (ImageGridFragment.list != null && ImageGridFragment.list.size() > position) {
                    LocalBelle belle = ImageGridFragment.list.get(position);
                    String rawUrl = belle.getRawUrl();
                    try {
                        File localFile = new File(AppRuntime.RAW_URL_CACHE_DIR + rawUrl.hashCode());
                        if ((localFile != null) && (localFile.exists())) {
                            WallpaperManager localWallpaperManager = WallpaperManager.getInstance(ImageDetailActivity.this);
                            try {
                                localWallpaperManager.setStream(new FileInputStream(localFile));
                                ImageDetailActivity.this.mHandler.sendEmptyMessage(WHAT_WALLPAPER_SUCCESS);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            ImageDetailActivity.this.mHandler.sendEmptyMessage(WHAT_WALLPAPER_FAIL);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ImageDetailActivity.this.mHandler.sendEmptyMessage(WHAT_WALLPAPER_FAIL);
                    }
                }
            }
        }.start();
    }


    private void downloadLargePic() {
        position = mPager.getCurrentItem();
        if (ImageGridFragment.list != null && ImageGridFragment.list.size() > position) {
            LocalBelle belle = ImageGridFragment.list.get(position);
            final String rawUrl = belle.getRawUrl();
            final String fileFullPath = AppRuntime.RAW_URL_CACHE_DIR + rawUrl.hashCode();
            new Thread() {
                @Override
                public void run() {
                    try {
                        File localFile = new File(fileFullPath);
                        if ((localFile != null) && (localFile.exists())) { //已存在 提示保存到相册成功
                            String str1 = MediaStore.Images.Media.insertImage(getContentResolver(), localFile.getAbsolutePath(), "belle" + rawUrl, "belle" + rawUrl);
                            String[] arrayOfString = {"_data"};
                            Cursor localCursor = getContentResolver().query(Uri.parse(str1), arrayOfString, null, null, null);
                            String str2 = null;
                            if (localCursor != null) {
                                int i = localCursor.getColumnIndexOrThrow("_data");
                                localCursor.moveToFirst();
                                str2 = localCursor.getString(i);
                                localCursor.close();
                            }
                            if (str2 != null) {
                                sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(new File(str2))));
                                ImageDetailActivity.this.mHandler.sendEmptyMessage(WHAT_SAVE_SUCCESS);
                            }
                        } else { //不存在下载
                            ImageDetailActivity.this.mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (ImageDetailActivity.this.mProgressDialog == null) {
                                        ImageDetailActivity.this.mProgressDialog = new ProgressDialog(ImageDetailActivity.this);
                                        ImageDetailActivity.this.mProgressDialog.setProgressStyle(0);
                                        ImageDetailActivity.this.mProgressDialog.setCanceledOnTouchOutside(false);
                                    }
                                    ImageDetailActivity.this.mProgressDialog.setMessage("正在下载大图，请稍后...");
                                    ImageDetailActivity.this.mProgressDialog.show();

                                    new AsyncHttpClient().get(rawUrl, new RangeFileAsyncHttpResponseHandler(new File(fileFullPath)) {
                                        @Override
                                        public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                                            ImageDetailActivity.this.mProgressDialog.dismiss();
                                        }

                                        @Override
                                        public void onSuccess(int i, Header[] headers, File file) {
                                            ImageDetailActivity.this.mProgressDialog.dismiss();
                                            downloadLargePic();
                                        }
                                    });
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ImageDetailActivity.this.mHandler.sendEmptyMessage(WHAT_SAVE_FAIL);
                    } catch (OutOfMemoryError outOfMemoryError) {
                        outOfMemoryError.printStackTrace();
                        System.gc();
                    }
                }
            }.start();

        }
    }

    /**
     * 保存到我的收藏
     */
    private void saveToMyCollect() {
        int position = mPager.getCurrentItem();
        if (ImageGridFragment.list != null && ImageGridFragment.list.size() > position) {
            LocalBelle belle = ImageGridFragment.list.get(position);
            final String url = belle.getUrl();
            if (this.mCollectHelper.isCollected(url)) {
                this.mCollectHelper.cancelCollectBelle(url);
                Toaster.show(this, R.string.cancel_collect_success);
                return;
            }
            this.mCollectHelper.collectBelle(belle);
            Toaster.show(this, R.string.collect_success);
        }
    }

}
