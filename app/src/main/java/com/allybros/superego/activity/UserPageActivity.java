package com.allybros.superego.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.allybros.superego.R;
import com.allybros.superego.api.LogoutTask;
import com.allybros.superego.fragments.ProfilFragment;
import com.allybros.superego.fragments.ResultsFragment;
import com.allybros.superego.util.PagerAdapter;

public class UserPageActivity extends AppCompatActivity {
    final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                String session_token;
                SharedPreferences pref = getApplicationContext().getSharedPreferences(USER_INFORMATION_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                session_token=pref.getString("session_token","");
                editor.clear();
                editor.commit();
                LogoutTask.logoutTask(getApplicationContext(),session_token);

                Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ResultsFragment resultsFragment = new ResultsFragment();
        resultsFragment.setActivity(this);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ProfilFragment(), getResources().getString(R.string.profile));
        adapter.addFrag(resultsFragment, getResources().getString(R.string.results));
        viewPager.setAdapter(adapter);
    }
}