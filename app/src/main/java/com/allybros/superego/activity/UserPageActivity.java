package com.allybros.superego.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.allybros.superego.R;
import com.allybros.superego.util.PagerAdapter;

import me.majiajie.pagerbottomtabstrip.PageNavigationView;

public class UserPageActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);


        viewPager = (ViewPager) findViewById(R.id.simpleViewPager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.simpleTabLayout);
        tabLayout.setupWithViewPager(viewPager);

    }


    private void setupViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ProfilFragment(), "Profil");
        adapter.addFrag(new ResultsFragment(), "Results");
        viewPager.setAdapter(adapter);
    }
}