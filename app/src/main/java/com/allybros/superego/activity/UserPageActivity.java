package com.allybros.superego.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.allybros.superego.R;
import com.allybros.superego.fragments.ProfileFragment;
import com.allybros.superego.fragments.ResultsFragment;
import com.allybros.superego.fragments.SearchFragment;
import com.allybros.superego.ui.PagerAdapter;
import com.allybros.superego.util.SessionManager;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class UserPageActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private BottomNavigationView navigation;
    private ArrayList<BottomNavigationItemView> navigationItems = new ArrayList<>();
    private ProfileFragment profileFragment;
    private ResultsFragment resultsFragment;
    private SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        //Add navigation items
        navigationItems.add((BottomNavigationItemView) findViewById(R.id.navigation_profile));
        navigationItems.add((BottomNavigationItemView) findViewById(R.id.navigation_results));
        navigationItems.add((BottomNavigationItemView) findViewById(R.id.navigation_search));

        initViewPager();
        setViewPagerAdapter();
        initBottomNavigation();
        setActivePage(0); //Set active page as "Profile" initially
    }

    @Override
    protected void onResume() {
        //Return from webviews
        if (SessionManager.getInstance().isModified()) {
            SessionManager.getInstance().getUser(); // Remove modified flag
            refreshFragments(0);
        }
        super.onResume();
    }

    /**
     * Initializes view pager
     */
    private void initViewPager(){
        viewPager = findViewById(R.id.mainViewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {}

            public void onPageSelected(int position) {
                setActivePage(position);
            }

            @Override
            public void onPageScrollStateChanged(int position) {}
        });
        //Prepare all fragments for performance
        viewPager.setOffscreenPageLimit(navigationItems.size() - 1);
    }

    /**
     * Creates fragments and prepares viewpager
     */
    private void setViewPagerAdapter() {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        profileFragment = new ProfileFragment();
        resultsFragment = new ResultsFragment();
        searchFragment = new SearchFragment();
        adapter.addFrag(profileFragment, getResources().getString(R.string.profile));
        adapter.addFrag(resultsFragment, getResources().getString(R.string.results));
        adapter.addFrag(searchFragment, getResources().getString(R.string.title_search));
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
                startActivity(intent1);
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
