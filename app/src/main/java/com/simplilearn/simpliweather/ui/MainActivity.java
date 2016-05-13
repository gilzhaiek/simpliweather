package com.simplilearn.simpliweather.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.simplilearn.simpliweather.R;

import java.util.ArrayList;
import java.util.List;

// AppCompatActivity <- I bring my own activity class which supports older version
// Activity <- I use the provided Activity.class that pre-installed on my device

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
    private static final int WEATHER_IDX = 0;
    private static final int PREF_IDX = 1;

    private DrawerLayout mDrawerLayout;
    private ViewPager mViewPager;
    private NavigationView mNavigationView;
    private TabLayout mTabs;

    private WeatherFragment mWeatherFragment;
    private PrefsFragment mPrefsFragment;

    private final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 66;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting ViewPager for each Tabs
        mViewPager = (ViewPager) findViewById(R.id.weather_view_pager);
        mWeatherFragment = new WeatherFragment();
        mPrefsFragment = new PrefsFragment();

        WeatherFPAdapter adapter = new WeatherFPAdapter(getSupportFragmentManager());
        adapter.addFragment(mWeatherFragment, "Home");
        adapter.addFragment(mPrefsFragment, "Settings");
        mViewPager.setAdapter(adapter);

        mTabs = (TabLayout) findViewById(R.id.weather_tabs);
        mTabs.setupWithViewPager(mViewPager);
        mTabs.setOnTabSelectedListener(this);

        // Create Navigation drawer and inflate layout
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set behavior of Navigation drawer
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // Set item in checked state
                menuItem.setChecked(true);

                int id = menuItem.getItemId();
                if(id == R.id.nav_weather) {
                    mViewPager.setCurrentItem(WEATHER_IDX);
                } else {
                    mViewPager.setCurrentItem(PREF_IDX);
                }

                // Closing drawer on item click
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLocation();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int tabPos = tab.getPosition();
        mNavigationView.getMenu().getItem(tabPos).setChecked(true);
        mViewPager.setCurrentItem(tabPos);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        onTabSelected(tab);
    }

    static class WeatherFPAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public WeatherFPAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_COARSE_LOCATION: {
                if(checkCallingOrSelfPermission(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    Location location = null;
                    LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(location != null) {
                        mWeatherFragment.setLocationUpdateWeather(location.getLatitude(), location.getLongitude());
                    }
                }
                return;
            }
        }
    }

    private void updateLocation() {
        Location location = null;
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if(checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if(location == null) {
            if(checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
            }
        }

        if(location != null) {
            mWeatherFragment.setLocationUpdateWeather(location.getLatitude(),location.getLongitude());
        }
    }
}
