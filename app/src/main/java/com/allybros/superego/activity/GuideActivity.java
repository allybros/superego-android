package com.allybros.superego.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.allybros.superego.fragment.GuideFragment;
import com.allybros.superego.R;
import com.google.android.material.tabs.TabLayout;

import static com.allybros.superego.unit.ConstantValues.IS_SHOWN;

public class GuideActivity extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;

    private ViewPager mPager;
    private PagerAdapter pagerAdapter;
    private Button btGoBack, btGoForward, btGoForwardCenter;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        mPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabDots);
        btGoBack = findViewById(R.id.btGoBack);
        btGoForward = findViewById(R.id.btGoForward);
        btGoForwardCenter = findViewById(R.id.btGoForwardCenter);


        pagerAdapter = new GuidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(mPager, true);
        setInitViews();
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * Sets Buttons for state of viewpager
     */
    private void setButtons(){
        switch (mPager.getCurrentItem()){
            case 0:
                btGoBack.setVisibility(View.GONE);
                btGoForward.setVisibility(View.GONE);
                btGoForwardCenter.setVisibility(View.VISIBLE);
                btGoForwardCenter.setOnClickListener(v -> mPager.setCurrentItem(mPager.getCurrentItem() + 1));
                btGoForwardCenter.setText(R.string.action_btn_continue);
                break;
            case 1:
                btGoForwardCenter.setVisibility(View.GONE);
                btGoForward.setVisibility(View.VISIBLE);
                btGoBack.setVisibility(View.VISIBLE);
                btGoForward.setOnClickListener(v -> mPager.setCurrentItem(mPager.getCurrentItem() + 1));
                btGoBack.setOnClickListener(v -> mPager.setCurrentItem(mPager.getCurrentItem() - 1));
                btGoForward.setText(R.string.action_btn_continue);
                btGoBack.setText(R.string.action_btn_back);
                break;
            case 2:
                btGoForward.setOnClickListener(v -> {
                    correctShown();
                    finish();
                });
                btGoForward.setText(R.string.action_btn_start);
                btGoBack.setText(R.string.action_btn_back);

                break;
        }

    }

    private void setInitViews(){
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setButtons();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        btGoBack.setVisibility(View.GONE);
        btGoForward.setVisibility(View.GONE);
        btGoForwardCenter.setVisibility(View.VISIBLE);
        btGoForwardCenter.setOnClickListener(v ->
                mPager.setCurrentItem(mPager.getCurrentItem() + 1));
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class GuidePagerAdapter extends FragmentStatePagerAdapter {

        public GuidePagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new GuideFragment(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    private void correctShown(){
        SharedPreferences pref = getSharedPreferences(IS_SHOWN, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isShown", true);
        editor.commit();
    }

}