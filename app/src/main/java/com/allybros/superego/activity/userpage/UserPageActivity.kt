package com.allybros.superego.activity.userpage

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.allybros.superego.fragment.profile.ProfileFragment
import com.allybros.superego.fragment.search.SearchFragment
import com.allybros.superego.util.InputMethodWatcher
import android.os.Bundle
import com.allybros.superego.R
import android.view.ViewGroup
import com.allybros.superego.util.InputMethodWatcher.KeyboardStatusListener
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import android.annotation.SuppressLint
import com.daimajia.androidanimations.library.YoYo
import com.daimajia.androidanimations.library.Techniques
import android.os.Build
import android.content.res.ColorStateList
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.allybros.superego.activity.settings.SettingsActivity
import com.allybros.superego.adapter.PagerAdapter
import com.allybros.superego.databinding.ActivityUserPageBinding
import com.allybros.superego.fragment.result.complate.ResultComplateFragment
import com.allybros.superego.fragment.result.none.ResultNoneFragment
import com.allybros.superego.fragment.result.partial.ResultPartialFragment
import com.allybros.superego.unit.State
import com.allybros.superego.util.SessionManager
import java.util.ArrayList

class UserPageActivity : AppCompatActivity() {
    private val navigationItems = ArrayList<BottomNavigationItemView>()
    private var profileFragment: ProfileFragment? = null
    private var resultsFragment: Fragment? = null
    private var searchFragment: SearchFragment? = null

    private var inputMethodWatcher: InputMethodWatcher? = null

    lateinit var binding: ActivityUserPageBinding
    val viewModel: UserPageVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_page)

        //Add navigation items
        navigationItems.add(findViewById<View>(R.id.navigation_profile) as BottomNavigationItemView)
        navigationItems.add(findViewById<View>(R.id.navigation_results) as BottomNavigationItemView)
        navigationItems.add(findViewById<View>(R.id.navigation_search) as BottomNavigationItemView)

        initInputMethodWatcher()
        initViewPager()
        setViewPagerAdapter()
        initBottomNavigation()
        setActivePage(0) //Set active page as "Profile" initially
    }

    override fun onResume() {
        //Return from webviews
        if (SessionManager.getInstance().isModified) {
            SessionManager.getInstance().user // Remove modified flag
            setViewPagerAdapter()
            setProgressVisibility(true)
            profileFragment!!.reloadProfile()
        }
        super.onResume()
    }

    /**
     * Initializes input method watcher for detecting virtual keyboard.
     */
    private fun initInputMethodWatcher() {
        val contentRoot = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
        inputMethodWatcher = InputMethodWatcher(contentRoot)
        inputMethodWatcher!!.setKeyboardStatusListener(object : KeyboardStatusListener {
            override fun onShown() {
                Log.d("Caught keyboard", "Shown")
                setBottomNavigationVisible(false)
            }

            override fun onHidden() {
                Log.d("Caught keyboard", "Hidden")
                setBottomNavigationVisible(true)
            }
        })
    }

    /**
     * Initializes view pager
     */
    private fun initViewPager() {
        binding.mainViewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(position: Int) {
                setActivePage(position)
            }

            override fun onPageScrollStateChanged(position: Int) {}
        })
        //Prepare all fragments for performance
        binding.mainViewPager.offscreenPageLimit = navigationItems.size - 1
    }

    /**
     * Creates fragments and prepares viewpager
     */
    private fun setViewPagerAdapter() {
        val adapter = PagerAdapter(supportFragmentManager)
        profileFragment = ProfileFragment()
        resultsFragment = getResultFragment()
        searchFragment = SearchFragment()
        adapter.addFrag(profileFragment!!, resources.getString(R.string.activity_label_profile))
        adapter.addFrag(resultsFragment!!, resources.getString(R.string.activity_label_results))
        adapter.addFrag(searchFragment!!, resources.getString(R.string.activity_label_search))
        binding.mainViewPager.adapter = adapter
    }

    private fun getResultFragment(): Fragment {
        return when (viewModel.state) {
            State.PARTIAL ->
                ResultPartialFragment()
            State.COMPLETE ->
                ResultComplateFragment()
            else ->
                ResultNoneFragment()
        }
    }


    /**
     * Initialize bottom navigation bar
     */
    private fun initBottomNavigation() {
        binding.navigation.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            for (navItem in navigationItems) {
                if (navItem.id == menuItem.itemId) {
                    setActivePage(navItem.itemPosition)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    /**
     * Show bottom navigation or not.
     * @param visible True if navigation bar needs to be visible
     */
    fun setBottomNavigationVisible(visible: Boolean) {
        if (visible) {
            binding.navigation.visibility = View.VISIBLE
            YoYo.with(Techniques.FadeInUp).duration(300).playOn(binding.navigation)
        } else {
            YoYo.with(Techniques.FadeOutDown).duration(300).playOn(binding.navigation)
            binding.navigation.visibility = View.GONE
        }
    }

    /**
     * Updates navigation bar and action bar with page index
     * @param index Page Index
     */
    @SuppressLint("RestrictedApi")
    private fun setActivePage(index: Int) {
        //Get active item index and title
        val activeNavItem = navigationItems[index]
        val activePageTitle = activeNavItem.itemData.title as String
        //Set actionbar and view pager
        val actionBar = supportActionBar
        if (actionBar != null) actionBar.title = activePageTitle
        binding.mainViewPager.currentItem = index
        //Disable all navigation Items
        for (navItem in navigationItems) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                navItem.setTextColor(ColorStateList.valueOf(getColor(R.color.bgNavigationItemPassive)))
                navItem.setIconTintList(ColorStateList.valueOf(getColor(R.color.bgNavigationItemPassive)))
            }
            navItem.setChecked(false)
        }
        //Enable selected item
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activeNavItem.setTextColor(ColorStateList.valueOf(getColor(R.color.bgNavigationItemActive)))
            activeNavItem.setIconTintList(ColorStateList.valueOf(getColor(R.color.bgNavigationItemActive)))
        }
        activeNavItem.setChecked(true)
        binding.mainViewPager.currentItem = index
        //Hide soft keyboard if showing.
        if (inputMethodWatcher!!.isKeyboardShown) {
            Log.d("Page changed", "Hide soft keyboard")
            inputMethodWatcher!!.hideSoftKeyboard()
        }
    }

    /**
     * Update fragments with state of currentUser
     * @param pageIndex Index of the fragment will be shown
     */
    fun refreshFragments(pageIndex: Int) {
        YoYo.with(Techniques.FadeOut).duration(400).playOn(binding.mainViewPager)
        setViewPagerAdapter()
        YoYo.with(Techniques.FadeIn).duration(400).playOn(binding.mainViewPager)
        setActivePage(pageIndex)
    }

    /**
     * Shows progress bar if visible flag set
     * @param visible progress view visiblity
     */
    fun setProgressVisibility(visible: Boolean) {
        if (visible) {
            binding.progressUserPage.visibility = View.VISIBLE
            binding.mainViewPager.alpha = 0.6f
            binding.mainViewPager.isEnabled = false
        } else {
            binding.progressUserPage.visibility = View.INVISIBLE
            binding.mainViewPager.alpha = 1f
            binding.mainViewPager.isEnabled = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent1 = Intent(applicationContext, SettingsActivity::class.java)
                startActivity(intent1)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }
}