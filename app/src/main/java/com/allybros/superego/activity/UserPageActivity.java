package com.allybros.superego.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.allybros.superego.R;
import com.allybros.superego.util.PagerAdapter;

public class UserPageActivity extends AppCompatActivity {

    ViewPager simpleViewPager;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);


        simpleViewPager = (ViewPager) findViewById(R.id.simpleViewPager);
        tabLayout = (TabLayout) findViewById(R.id.simpleTabLayout);
        TabLayout.Tab profilTab = tabLayout.newTab();
        profilTab.setText(R.string.profil);
        tabLayout.addTab(profilTab);


        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager() , tabLayout.getTabCount());
        simpleViewPager.setAdapter(adapter);

        simpleViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }
}