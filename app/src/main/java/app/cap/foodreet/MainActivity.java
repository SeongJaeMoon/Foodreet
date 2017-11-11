package app.cap.foodreet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import app.cap.foodreet.Adapter.InfoEntity;
import app.cap.foodreet.Adapter.ViewPagerAdapter;

//일반 사용자용 MAIN

public class MainActivity extends AppCompatActivity{

    //viewPager.setOffscreenPageLimit()
    static final int VIEWPAGER_OFF_SCREEN_PAGE_LIMIT = 6;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private ViewPagerAdapter mViewPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private List<InfoEntity> infoEntities = new ArrayList<>();
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //뷰페이저 연결
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        infoEntities.add(new InfoEntity(getResources().getString(R.string.profile), "0"));
        infoEntities.add(new InfoEntity(getResources().getString(R.string.map), "1"));
        infoEntities.add(new InfoEntity(getResources().getString(R.string.store), "2"));
        infoEntities.add(new InfoEntity(getResources().getString(R.string.setting), "3"));
        mViewPagerAdapter.init(infoEntities);
        // 뷰페이저 어댑터와 연결
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mViewPagerAdapter);

        // 뷰페이저 속성
        // mViewPager.setOffscreenPageLimit(VIEWPAGER_OFF_SCREEN_PAGE_LIMIT);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //옵션 메뉴에서 선택
            infoEntities.clear();
            infoEntities.add(new InfoEntity(getResources().getString(R.string.profile),
                    String.valueOf(new Random().nextInt(100))));
            infoEntities.add(new InfoEntity(getResources().getString(R.string.map),
                    String.valueOf(new Random().nextInt(100))));
            infoEntities.add(new InfoEntity(getResources().getString(R.string.store),
                    String.valueOf(new Random().nextInt(100))));
            infoEntities.add(new InfoEntity(getResources().getString(R.string.setting),
                    String.valueOf(new Random().nextInt(100))));
            mViewPagerAdapter.refreshAllFragment(infoEntities);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.w(TAG, "onStart");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "onDestroy");
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.w(TAG, "onStop");
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.w(TAG, "onPause");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(TAG, "onRestart");
    }
}
