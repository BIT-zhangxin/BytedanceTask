package com.zhangxin.videoapp.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MenuItem;
import com.zhangxin.videoapp.MyAppCompatActivity;
import com.zhangxin.videoapp.R;
import com.zhangxin.videoapp.fragment.CreateFragment;
import com.zhangxin.videoapp.fragment.HomeFragment;
import com.zhangxin.videoapp.fragment.InformationFragment;

public class MainActivity extends MyAppCompatActivity {

    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    MenuItem menuItem;

    private static final int itemNumber=3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager=findViewById(R.id.viewpager_menu);
        bottomNavigationView=findViewById(R.id.navigation_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(
            new OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    switch (itemId){
                        case R.id.item_home:
                            viewPager.setCurrentItem(0);
                            break;
                        case R.id.item_create:
                            viewPager.setCurrentItem(1);
                            break;
                        case R.id.item_information:
                            viewPager.setCurrentItem(2);
                            break;
                    }
                    return false;
                }
            });
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        bottomNavigationView.setSelectedItemId(R.id.item_home);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new HomeFragment();
                case 1:
                    return new CreateFragment();
                case 2:
                    return new InformationFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return itemNumber;
        }
    }

}
