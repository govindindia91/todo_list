package com.example.cleareyes.localoyeproject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import android.content.Intent;
import android.os.Looper;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    ActionBar actionBar;
    ImageButton newTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Intent intent = new Intent(MainActivity.this, AlarmService.class);
        startService(intent);

        mSectionsPagerAdapter = new SectionAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int i)     {

            }
        });


        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };

        actionBar.addTab(actionBar.newTab().setText(R.string.title_section1).setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText(R.string.title_section2).setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText(R.string.title_section3).setTabListener(tabListener));

        newTaskButton = (ImageButton) findViewById(R.id.fab_image_button);
        newTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateTask.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });

        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseHandler db =  new DatabaseHandler(MainActivity.this, "LocalOye", null ,1);
                ArrayList<Task> currentTasks = new ArrayList<>();
                ArrayList<Task> pendingTasks = new ArrayList<>();
                currentTasks = db.getAllActiveTasks("current", false, 0);
                pendingTasks = db.getAllActiveTasks("pending", false, 0);

                if(currentTasks.size() > 0) {
                    for(int i =0 ;i < currentTasks.size();i++) {
                        Date currentDate = new Date(System.currentTimeMillis());
                        Date endDate = new Date(currentTasks.get(i).getEndDate().longValue());
                        //Log.i("dates", currentDate.toString() + " --- " + endDate.toString() + "-- " + endDate.getYear());
                        if(currentDate.getYear() >= (endDate.getYear()-1900) && currentDate.getMonth() >= endDate.getMonth() && currentDate.getDate() > endDate.getDate()) {
                           //Log.i("change date","true");
                            Task task = currentTasks.get(i);
                            task.setTaskState("pending");
                            db.updateTask(task);
                        }
                    }
                }
                if(pendingTasks.size() > 0) {
                    for(int i=0;i< pendingTasks.size();i++) {
                        Date currentDate = new Date(System.currentTimeMillis());
                        Date endDate = new Date(pendingTasks.get(i).getEndDate().longValue());
                        if(currentDate.getYear() <= (endDate.getYear()-1900) && currentDate.getMonth() <= endDate.getMonth() && currentDate.getDate() <= endDate.getDate()) {
                            Log.i("change date","true");
                            Task task = pendingTasks.get(i);
                            task.setTaskState("current");
                            db.updateTask(task);
                        }
                    }
                }
                mSectionsPagerAdapter.notifyDataSetChanged();
                db.close();
            }
        },2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSectionsPagerAdapter = new SectionAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    public class SectionAdapter extends FragmentStatePagerAdapter {

        public SectionAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    CurrentTaskFragment currentTaskFragment = new CurrentTaskFragment();
                    return  currentTaskFragment;

                case 1:
                    PendingTaskFragment pendingTaskFragment = new PendingTaskFragment();
                    return pendingTaskFragment;

                case 2:
                    CompletedTaskFragment completedTaskFragment = new CompletedTaskFragment();
                    return completedTaskFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

}
