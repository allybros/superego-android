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
import androidx.viewpager.widget.ViewPager;

import com.allybros.superego.R;
import com.allybros.superego.fragments.ProfileFragment;
import com.allybros.superego.fragments.ResultsFragment;
import com.allybros.superego.util.PagerAdapter;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class UserPageActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private BottomNavigationView navigation;
    private ArrayList<BottomNavigationItemView> navigationItems = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        //Add navigation items
        navigationItems.add((BottomNavigationItemView) findViewById(R.id.navigation_profile));
        navigationItems.add((BottomNavigationItemView) findViewById(R.id.navigation_results));

        initViewPager();
        setViewPagerAdapter();
        initBottomNavigation();
        setActivePage(0); //Set active page as "Profile" initially
    }

    /**
     * Initializes view pager
     */
    private void initViewPager(){
        viewPager = findViewById(R.id.simpleViewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {}

            public void onPageSelected(int position) {
                setActivePage(position);
            }

            @Override
            public void onPageScrollStateChanged(int position) {}
        });
    }

    /**
     * Creates fragments and prepares viewpager
     */
    private void setViewPagerAdapter() {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ProfileFragment(), getResources().getString(R.string.profile));
        adapter.addFrag(new ResultsFragment(), getResources().getString(R.string.results));
        this.viewPager.setAdapter(adapter);
    }

    /**
     * Initialize bottom navigation bar
     */
    private void initBottomNavigation(){
        navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                for (BottomNavigationItemView navItem : navigationItems) {
                    if (navItem.getId() == menuItem.getItemId()){
                        setActivePage(navItem.getItemPosition());
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Updates navigation bar and action bar with page index
     * @param index Page Index
     */
    @SuppressLint("RestrictedApi")
    private void setActivePage(int index){
        //Get active item index and title
        BottomNavigationItemView activeNavItem = navigationItems.get(index);
        String activePageTitle = (String) activeNavItem.getItemData().getTitle();
        //Set actionbar and view pager
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) actionBar.setTitle(activePageTitle);
        viewPager.setCurrentItem(index);
        //Disable all navigation Items
        for (BottomNavigationItemView navItem: navigationItems) {
            navItem.setTextColor(ColorStateList.valueOf(getColor(R.color.grey)));
            navItem.setIconTintList(ColorStateList.valueOf(getColor(R.color.grey)));
            navItem.setChecked(false);
        }
        //Enable selected item
        activeNavItem.setTextColor(ColorStateList.valueOf(getColor(R.color.White)));
        activeNavItem.setIconTintList(ColorStateList.valueOf(getColor(R.color.White)));
        activeNavItem.setChecked(true);
        viewPager.setCurrentItem(index);
    }

    /**
     * Update fragments with state of currentUser
     * @param pageIndex Index of the fragment will be shown
     */
    public void refreshFragments(int pageIndex){
        YoYo.with(Techniques.FadeOut).duration(400).playOn(this.viewPager);
        setViewPagerAdapter();
        YoYo.with(Techniques.FadeIn).duration(400).playOn(this.viewPager);
        this.viewPager.setCurrentItem(pageIndex);
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

}
