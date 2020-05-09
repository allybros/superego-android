package com.allybros.superego.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.allybros.superego.R;
import com.allybros.superego.fragments.ProfileFragment;
import com.allybros.superego.fragments.ResultsFragment;
import com.allybros.superego.util.PagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserPageActivity extends AppCompatActivity {
    final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";

    private ActionBar toolbar;
    ViewPager viewPager;
    MenuItem prevMenuItem;
    BottomNavigationItemView navigationResult;
    BottomNavigationItemView navigationProfile;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        toolbar = getSupportActionBar();

        navigationResult =findViewById(R.id.navigation_results);
        navigationProfile =findViewById(R.id.navigation_profile);

        //These lines are for first view
        navigationProfile.setTextColor(ColorStateList.valueOf(getColor(R.color.White)));
        navigationResult.setTextColor(ColorStateList.valueOf(getColor(R.color.grey)));

        navigationProfile.setIconTintList(ColorStateList.valueOf(getColor(R.color.White)));
        navigationResult.setIconTintList(ColorStateList.valueOf(getColor(R.color.grey)));

        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("RestrictedApi")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.navigation_profile:
                        toolbar.setTitle(R.string.profile);

                        navigationProfile.setTextColor(ColorStateList.valueOf(getColor(R.color.White)));
                        navigationResult.setTextColor(ColorStateList.valueOf(getColor(R.color.grey)));

                        navigationProfile.setIconTintList(ColorStateList.valueOf(getColor(R.color.White)));
                        navigationResult.setIconTintList(ColorStateList.valueOf(getColor(R.color.grey)));

                        viewPager.setCurrentItem(0);
                        fragment = new ProfileFragment();
                        return true;
                    case R.id.navigation_results:
                        viewPager.setCurrentItem(1);
                        toolbar.setTitle(R.string.results);
                        fragment = new ResultsFragment();

                        navigationProfile.setTextColor(ColorStateList.valueOf(getColor(R.color.grey)));
                        navigationResult.setTextColor(ColorStateList.valueOf(getColor(R.color.White)));

                        navigationProfile.setIconTintList(ColorStateList.valueOf(getColor(R.color.grey)));
                        navigationResult.setIconTintList(ColorStateList.valueOf(getColor(R.color.White)));

                        return true;
                }
                return false;
            }
        });

        toolbar.setTitle(R.string.profile);


        viewPager = (ViewPager) findViewById(R.id.simpleViewPager);
        initViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {}

            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("RestrictedApi")
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
                if("Profil".equals(prevMenuItem.getTitle())){
                    navigationProfile.setTextColor(ColorStateList.valueOf(getColor(R.color.White)));
                    navigationResult.setTextColor(ColorStateList.valueOf(getColor(R.color.grey)));

                    navigationProfile.setIconTintList(ColorStateList.valueOf(getColor(R.color.White)));
                    navigationResult.setIconTintList(ColorStateList.valueOf(getColor(R.color.grey)));
                }else{
                    navigationProfile.setTextColor(ColorStateList.valueOf(getColor(R.color.grey)));
                    navigationResult.setTextColor(ColorStateList.valueOf(getColor(R.color.White)));

                    navigationProfile.setIconTintList(ColorStateList.valueOf(getColor(R.color.grey)));
                    navigationResult.setIconTintList(ColorStateList.valueOf(getColor(R.color.White)));
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent1=new Intent(getApplicationContext(), SettingsActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ProfileFragment(), getResources().getString(R.string.profile));
        adapter.addFrag(new ResultsFragment(), getResources().getString(R.string.results));
        viewPager.setAdapter(adapter);
    }

    /**
     * Update fragments with state of currentUser
     * @param pageIndex Index of the fragment will be shown
     */
    public void updateFragments(int pageIndex){
        initViewPager(this.viewPager);
        this.viewPager.setCurrentItem(pageIndex);
    }
}