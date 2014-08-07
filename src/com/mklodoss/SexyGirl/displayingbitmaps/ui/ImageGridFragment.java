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
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import com.mklodoss.SexyGirl.BuildConfig;
import com.mklodoss.SexyGirl.R;
import com.mklodoss.SexyGirl.displayingbitmaps.util.Utils;
import com.mklodoss.SexyGirl.event.SeriesUpdatedEvent;
import com.mklodoss.SexyGirl.logger.Log;
import com.mklodoss.SexyGirl.model.CollectedBelle;
import com.mklodoss.SexyGirl.model.LocalBelle;
import com.mklodoss.SexyGirl.util.BelleHelper;
import com.mklodoss.SexyGirl.util.CollectHelper;
import com.mklodoss.SexyGirl.util.Toaster;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import de.greenrobot.event.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * The main fragment that powers the ImageGridActivity screen. Fairly straight forward GridView
 * implementation with the key addition being the ImageWorker class w/ImageCache to load children
 * asynchronously, keeping the UI nice and smooth and caching thumbnails for quick retrieval. The
 * cache is retained over configuration changes like orientation change so the images are populated
 * quickly if, for example, the user rotates the device.
 */
public class ImageGridFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "ImageGridFragment";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    //private ImageFetcher mImageFetcher;
    public static final String ARG_PLANET_NUMBER = "categoty";
    private int category;
    public volatile static List<LocalBelle> list = new ArrayList<LocalBelle>();
    GridView mGridView;
    LayoutInflater layoutInflater;
    ProgressDialog progressDialog;

    /**
     * Empty constructor as per the Fragment documentation
     */
    public ImageGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutInflater = getLayoutInflater(savedInstanceState);
        setHasOptionsMenu(false);

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        mAdapter = new ImageAdapter(getActivity());

       /* mImageFetcher = MainApplication._application.getmImageFetcher();
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(),
                MainApplication._application.getCacheParams());*/

        //获取分类数据
        category = getArguments().getInt(ARG_PLANET_NUMBER);
        if (category == -1) { //我的收藏
            list.clear();
            list.addAll(CollectHelper.getInstance().loadAll());
        } else {
            list.clear();
            list.addAll(BelleHelper.getInstance().getLocaleBell(this.getActivity(), category,
                    new BelleHelper.LocalBelleNotifyCallBack(
                            new WeakReference<ImageGridFragment>(this))));
        }
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setProgressStyle(0);
        adapterNotify(false);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        final View v = inflater.inflate(R.layout.image_grid_fragment, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setBackgroundColor(Color.GRAY);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        //mImageFetcher.setPauseWork(true);
                    }
                } else {
                    //mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight((int) (columnWidth * 1.46));
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
                                }
                                if (Utils.hasJellyBean()) {
                                    mGridView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    mGridView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    }
                });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().stop();
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        final Intent i = new Intent(getActivity(), ImageDetailActivity.class);
        i.putExtra(ImageDetailActivity.EXTRA_IMAGE, (int) id);
        i.putExtra(ImageDetailActivity.EXTRA_CATEGORY, category);
        if (Utils.hasJellyBean()) {
            // makeThumbnailScaleUpAnimation() looks kind of ugly here as the loading spinner may
            // show plus the thumbnail image in GridView is cropped. so using
            // makeScaleUpAnimation() instead.
            ActivityOptions options =
                    ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight());
            getActivity().startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }
    }



    /**
     * The main adapter that backs the GridView. This is fairly standard except the number of
     * columns in the GridView is used to create a fake top row of empty views as we use a
     * transparent ActionBar and don't want the real top row of images to start off covered by it.
     */
    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private int mActionBarHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;
        //        public String[] urls = Images.imageThumbUrls;
        public String[] urls = new String[0];

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            // Calculate ActionBar height
           /* TypedValue tv = new TypedValue();
            if (context.getTheme().resolveAttribute(
                    android.R.attr.actionBarSize, tv, true)) {
                mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, context.getResources().getDisplayMetrics());
            }*/
        }

        @Override
        public int getCount() {
            // If columns have yet to be determined, return no items
            if (getNumColumns() == 0) {
                return 0;
            }

            // Size + number of columns for top empty row
            return urls.length + mNumColumns;
        }

        @Override
        public Object getItem(int position) {
            return position < mNumColumns ?
                    null : urls[position - mNumColumns];
        }

        @Override
        public long getItemId(int position) {
            return position < mNumColumns ? 0 : position - mNumColumns;
        }

        @Override
        public int getViewTypeCount() {
            // Two types of views, the normal ImageView and the top row of empty views
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (position < mNumColumns) ? 1 : 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            //BEGIN_INCLUDE(load_gridview_item)
            // First check if this is the top row
            if (position < mNumColumns) {
                if (convertView == null) {
                    convertView = new View(mContext);
                }
                // Set empty view with height of ActionBar
                convertView.setLayoutParams(new AbsListView.LayoutParams(
                        LayoutParams.MATCH_PARENT, mActionBarHeight));
                return convertView;
            }

            // Now handle the main ImageView thumbnails
            View view;
            RecyclingImageView imageView;
            final ProgressBar progressBar;
            if (convertView == null) { // if it's not recycled, instantiate and initialize
                view = layoutInflater.inflate(R.layout.thumb_image_view, mGridView, false);
                imageView = (RecyclingImageView) view.findViewById(R.id.photo);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
                ViewHolder holder = new ViewHolder(view);
                view.setTag(holder);
                view.setLayoutParams(mImageViewLayoutParams);
            } else { // Otherwise re-use the converted view
                view = convertView;
                ViewHolder holder = (ViewHolder) convertView.getTag();
                imageView = holder.photo;
                progressBar = holder.progressBar;
            }

            // Check the height matches our calculated column width
            /*if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(new RelativeLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            }*/

            //imageView.setImageDrawable(getResources().getDrawable(R.drawable.loading));
            // Finally load the image asynchronously into the ImageView, this also takes care of
            // setting a placeholder image while the background thread runs
            //mImageFetcher.loadImage(urls[position - mNumColumns], imageView);

            progressBar.setVisibility(View.GONE);
            ImageLoader.getInstance().displayImage(urls[position - mNumColumns], imageView, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String s, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            });
            return view;
            //END_INCLUDE(load_gridview_item)
        }

        /**
         * Sets the item height. Useful for when we know the column width so the height can be set
         * to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
            //mImageFetcher.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }


    /**
     * 更新adapter数据
     */
    private void adapterNotify(boolean notify) {
        if (list != null && list.size() > 0) {
            if(!isMyCollect()) {
                progressDialog.dismiss();
            }
            String[] urls = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                LocalBelle localBelle = list.get(i);
                urls[i] = localBelle.getUrl();
            }
            mAdapter.urls = urls;
            if (notify) {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            if (!isMyCollect()) {
                progressDialog.show();
            } else {
                Toaster.show(getActivity(), R.string.has_no_collect);
            }
        }
    }

    /**
     * 有新的图片同步
     */
    public void updateBellList() {
        android.util.Log.e("tttttttt", "onEventMainThread----------------------------");
        list = BelleHelper.getInstance().getLocalBelleList();
        ImageLoader.getInstance().resume();
        adapterNotify(true);
    }


    /**
     *强制从网络获取数据
     */
    public void getBellListFromNetWork() {
        if(!isMyCollect()) {
            ImageLoader.getInstance().pause();
            BelleHelper.getInstance().getLocaleBellFromNetwork(this.getActivity(), category,
                    new BelleHelper.LocalBelleNotifyCallBack(
                            new WeakReference<ImageGridFragment>(this)));
            progressDialog.show();
        }
    }

    private static final class ViewHolder {
        public RecyclingImageView photo;
        public ProgressBar progressBar;

        public ViewHolder(View paramView) {
            this.photo = ((RecyclingImageView) paramView.findViewById(R.id.photo));
            this.progressBar = ((ProgressBar) paramView.findViewById(R.id.progressBar));
        }
    }

    /**
     * 是否是我的收藏
     *
     * @return
     */
    public boolean isMyCollect() {
        return -1 == category;
    }

}
