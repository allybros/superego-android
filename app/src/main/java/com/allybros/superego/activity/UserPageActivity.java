package com.allybros.superego.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.allybros.superego.R;
import com.allybros.superego.fragment.ProfileFragment;
import com.allybros.superego.fragment.ResultsFragment;
import com.allybros.superego.fragment.SearchFragment;
import com.allybros.superego.adapter.PagerAdapter;
import com.allybros.superego.util.InputMethodWatcher;
import com.allybros.superego.util.SessionManager;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class UserPageActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private BottomNavigationView navigation;
    private ArrayList<BottomNavigationItemView> navigationItems = new ArrayList<>();
    private ProfileFragment profileFragment;
    private ResultsFragment resultsFragment;
    private SearchFragment searchFragment;
    private InputMethodWatcher inputMethodWatcher;
    private MaterialProgressBar userPageProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        //Add navigation items
        navigationItems.add(findViewById(R.id.navigation_profile));
        navigationItems.add(findViewById(R.id.navigation_results));
        navigationItems.add(findViewById(R.id.navigation_search));

        userPageProgressBar = findViewById(R.id.progressUserPage);

        initInputMethodWatcher();
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
            setViewPagerAdapter();
            setProgressVisibility(true);
            profileFragment.reloadProfile();
        }
        super.onResume();
    }

    /**
     * Initializes input method watcher for detecting virtual keyboard.
     */
    private void initInputMethodWatcher(){
        View contentRoot = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        inputMethodWatcher = new InputMethodWatcher(contentRoot);
        inputMethodWatcher.setKeyboardStatusListener(new InputMethodWatcher.KeyboardStatusListener() {
            @Override
            public void onShown() {
                Log.d("Caught keyboard", "Shown");
                setBottomNavigationVisible(false);
            }

            @Override
            public void onHidden() {
                Log.d("Caught keyboard", "Hidden");
                setBottomNavigationVisible(true);
            }
        });
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
        profileFragment = new ProfileFragment(this);
        resultsFragment = new ResultsFragment(this);
        searchFragment = new SearchFragment(this);
        adapter.addFrag(profileFragment, getResources().getString(R.string.activity_label_profile));
        adapter.addFrag(resultsFragment, getResources().getString(R.string.activity_label_results));
        adapter.addFrag(searchFragment, getResources().getString(R.string.activity_label_search));
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
     * Show bottom navigation or not.
     * @param visible True if navigation bar needs to be visible
     */
    public void setBottomNavigationVisible(boolean visible){
        if (visible) {
            navigation.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.FadeInUp).duration(300).playOn(navigation);
        } else {
            YoYo.with(Techniques.FadeOutDown).duration(300).playOn(navigation);
            navigation.setVisibility(View.GONE);
        }
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                navItem.setTextColor(ColorStateList.valueOf(getColor(R.color.bgNavigationItemPassive)));
                navItem.setIconTintList(ColorStateList.valueOf(getColor(R.color.bgNavigationItemPassive)));
            }
            navItem.setChecked(false);
        }
        //Enable selected item
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activeNavItem.setTextColor(ColorStateList.valueOf(getColor(R.color.bgNavigationItemActive)));
            activeNavItem.setIconTintList(ColorStateList.valueOf(getColor(R.color.bgNavigationItemActive)));
        }
        activeNavItem.setChecked(true);
        viewPager.setCurrentItem(index);
        //Hide soft keyboard if showing.
        if (inputMethodWatcher.isKeyboardShown()){
            Log.d("Page changed", "Hide soft keyboard");
            inputMethodWatcher.hideSoftKeyboard();
        }
    }

    /**
     * Update fragments with state of currentUser
     * @param pageIndex Index of the fragment will be shown
     */
    public void refreshFragments(int pageIndex){
        YoYo.with(Techniques.FadeOut).duration(400).playOn(this.viewPager);
        setViewPagerAdapter();
        YoYo.with(Techniques.FadeIn).duration(400).playOn(this.viewPager);
        setActivePage(pageIndex);
    }

    /**
     * Shows progress bar if visible flag set
     * @param visible progress view visiblity
     */
    public void setProgressVisibility(boolean visible){
        if (visible) {
            this.userPageProgressBar.setVisibility(View.VISIBLE);
            this.viewPager.setAlpha(0.6f);
            this.viewPager.setEnabled(false);
        } else {
            this.userPageProgressBar.setVisibility(View.INVISIBLE);
            this.viewPager.setAlpha(1f);
            this.viewPager.setEnabled(true);
        }
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
