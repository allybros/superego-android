package com.allybros.superego.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.allybros.superego.R;
import com.allybros.superego.fragments.ProfilFragment;
import com.allybros.superego.fragments.ResultsFragment;
import com.allybros.superego.util.PagerAdapter;

public class UserPageActivity extends AppCompatActivity {

    private ActionBar toolbar;
    ViewPager viewPager;
    MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        toolbar = getSupportActionBar();


        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.navigation_profile:
                        toolbar.setTitle(R.string.profile);
                        viewPager.setCurrentItem(0);
                        fragment = new ProfilFragment();
                        return true;
                    case R.id.navigation_results:
                        viewPager.setCurrentItem(1);
                        toolbar.setTitle(R.string.results);
                        fragment = new ResultsFragment();
                        return true;
                }
                return false;
            }
        });

        toolbar.setTitle(R.string.profile);


        viewPager = (ViewPager) findViewById(R.id.simpleViewPager);
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {}

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);
                toolbar.setTitle(prevMenuItem.getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int position) {}
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }


    private void setupViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ProfilFragment(), getResources().getString(R.string.profile));
        adapter.addFrag(new ResultsFragment(), getResources().getString(R.string.results));
        viewPager.setAdapter(adapter);
    }
}