package com.mklodoss.SexyGirl;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.ddj.commonkit.DateUtil;
import com.mklodoss.SexyGirl.setting.SettingPreference;
import com.mklodoss.SexyGirl.displayingbitmaps.ui.ImageGridFragment;
import com.mklodoss.SexyGirl.event.SeriesUpdatedEvent;
import com.mklodoss.SexyGirl.model.Series;
import com.mklodoss.SexyGirl.util.SeriesHelper;
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
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

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
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
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
        ImageGridFragment imageGridFragment = new ImageGridFragment();
        Bundle args = new Bundle();
        if (list.size() > position) {
            Series series = list.get(position);
            mDrawerTitle = series.title;
            args.putInt(ImageGridFragment.ARG_PLANET_NUMBER, series.type);
            imageGridFragment.setArguments(args);

            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, imageGridFragment);
            ft.commit();
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
        }
        //setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        SettingPreference.getInstance().setCurrentItem(position);
        getActionBar().setTitle(mDrawerTitle);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
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

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
   /* public static class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
            int i = getArguments().getInt(ARG_PLANET_NUMBER);
            String planet = getResources().getStringArray(R.array.planets_array)[i];

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                    "drawable", getActivity().getPackageName());
            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle(planet);
            return rootView;
        }
    }*/

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 有新的目录同步
     * @param paramSeriesUpdatedEvent
     */
    public void onEventMainThread(SeriesUpdatedEvent paramSeriesUpdatedEvent){
        Log.e("ddj", "---------------------onEventMainThread");
        list = SeriesHelper.getInstance().getSeriesList();
        adapter.setList(list);
        adapter.notifyDataSetChanged();
        SettingPreference.getInstance().setCurrentItem(0);
        selectItem(0);
    }
}
