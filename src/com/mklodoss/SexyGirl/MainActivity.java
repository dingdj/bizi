package com.mklodoss.SexyGirl;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.ddj.commonkit.DateUtil;
import com.mklodoss.SexyGirl.displayingbitmaps.ui.ImageGridFragment;
import com.mklodoss.SexyGirl.event.SeriesUpdatedEvent;
import com.mklodoss.SexyGirl.model.Series;
import com.mklodoss.SexyGirl.setting.SettingPreference;
import com.mklodoss.SexyGirl.util.SeriesHelper;
import com.mklodoss.SexyGirl.util.Toaster;
import com.nostra13.universalimageloader.core.ImageLoader;
import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private static final long ONEHOUR = 1000 * 60 * 60;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private LayoutInflater layoutInflater;
    private DrawerAdapter adapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    List<Series> list = new ArrayList<Series>();
    private ImageGridFragment fragment;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        layoutInflater = getLayoutInflater();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mTitle = getResources().getString(R.string.app_name);

        adapter = new DrawerAdapter();
        mDrawerList.setAdapter(adapter);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        EventBus.getDefault().register(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            long lastTime = SettingPreference.getInstance().getLauncherOnStartDayTime();
            long currentTime = DateUtil.getTodayTime();
            boolean resetDayStatTime = false;

            if (currentTime - lastTime >= ONEHOUR * 24) {
                resetDayStatTime = true;
                //同步开始
                SeriesHelper.getInstance().syncSeries(this);
            }

            if (resetDayStatTime) {
                SettingPreference.getInstance().setLauncherOnStartDayTime(currentTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        list = SeriesHelper.getInstance().getSeriesList();
        adapter.setList(list);
        selectItem(SettingPreference.getInstance().getCurrentItem());
    }

    class DrawerAdapter extends BaseAdapter {

        private List<Series> list = new ArrayList<Series>();

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView = (TextView) layoutInflater.inflate(R.layout.drawer_list_item, null);
            textView.setTag(list.get(i));
            textView.setText(list.get(i).title);
            return textView;
        }

        public List<Series> getList() {
            return list;
        }

        public void setList(List<Series> list) {
            this.list = list;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_refresh:
                // 刷新
                if (fragment != null) {
                    fragment.getBellListFromNetWork();
                }
                return true;
            case R.id.action_clear:
                //清除缓存
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("删除缓存");
                builder.setMessage("确定要删除缓存吗？");
                builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread() {
                            @Override
                            public void run() {
                                ImageLoader.getInstance().clearDiskCache();
                            }
                        }.start();
                        Toaster.show(MainActivity.this, "成功删除缓存");
                    }

                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments

        fragment = new ImageGridFragment();
        Bundle args = new Bundle();
        if (list.size() > position) {
            Series series = list.get(position);
            mDrawerTitle = series.title;
            if(ImageGridFragment.isHideCategory(series.type)){
                Toaster.show(this, R.string.action_cancel_collect);
                return;
            }
            args.putInt(ImageGridFragment.ARG_PLANET_NUMBER, series.type);
            fragment.setArguments(args);

            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
        }
        mDrawerLayout.closeDrawer(mDrawerList);
        SettingPreference.getInstance().setCurrentItem(position);
        if(getActionBar() != null) {
            getActionBar().setTitle(mDrawerTitle);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if(getActionBar() != null) {
            getActionBar().setTitle(mTitle);
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 有新的目录同步
     *
     * @param paramSeriesUpdatedEvent
     */
    public void onEventMainThread(SeriesUpdatedEvent paramSeriesUpdatedEvent) {
        Log.e("ddj", "---------------------onEventMainThread");
        list = SeriesHelper.getInstance().getSeriesList();
        adapter.setList(list);
        adapter.notifyDataSetChanged();
        SettingPreference.getInstance().setCurrentItem(0);
        selectItem(0);
    }

}
