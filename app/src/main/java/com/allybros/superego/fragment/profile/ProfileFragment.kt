package com.allybros.superego.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.allybros.superego.R
import com.allybros.superego.api.LoadProfileTask
import com.allybros.superego.unit.ConstantValues
import com.allybros.superego.unit.ErrorCodes
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.allybros.superego.activity.userpage.UserPageActivity
import com.allybros.superego.activity.login.LoginActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import com.squareup.picasso.Picasso
import com.allybros.superego.activity.settings.SettingsActivity
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.AdListener
import com.allybros.superego.activity.webview.WebViewActivity
import com.allybros.superego.databinding.FragmentProfileBinding
import com.allybros.superego.util.SessionManager
import com.google.android.gms.ads.AdRequest
import androidx.databinding.DataBindingUtil

class ProfileFragment : Fragment() {
    //btnNewTest : Button? = null TODO Add share button in Designs

    //API Receivers
    private var refreshReceiver: BroadcastReceiver? = null
    private var rewardReceiver: BroadcastReceiver? = null

    //Current session
    private val sessionManager = SessionManager.getInstance()

    lateinit var binding: FragmentProfileBinding
    val viewModel: ProfileVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupReceivers()
        initProfileCard()
        initButtons()
        initInfoCard()
        prepareBannerAd()
        initSwipeLayout()
    }

    /**
     * Reloads profile content with API.
     */
    fun reloadProfile() {
        // Call API task
        LoadProfileTask.loadProfileTask(
            requireActivity().applicationContext,
            sessionManager.sessionToken,
            ConstantValues.ACTION_REFRESH_PROFILE
        )
    }

    /**
     * Does configure receivers
     */
    private fun setupReceivers() {
        // Set Up receivers
        refreshReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val status = intent.getIntExtra("status", 0)
                Log.d("receiver", "Got message: $status")
                when (status) {
                    ErrorCodes.SYSFAIL -> Snackbar.make(
                        binding.profileSwipeLayout,
                        R.string.error_no_connection,
                        BaseTransientBottomBar.LENGTH_LONG
                    ).show()
                    ErrorCodes.SUCCESS -> {
                        Log.d("Profile refresh", "Success")
                        val userPageActivity = activity as UserPageActivity?
                        userPageActivity!!.refreshFragments(0)
                    }
                }

                // Disable progress view
                val upa = activity as UserPageActivity?
                upa?.setProgressVisibility(false)
                binding.profileSwipeLayout.isRefreshing = false
            }
        }
        rewardReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val status = intent.getIntExtra("status", 0)
                when (status) {
                    ErrorCodes.SYSFAIL -> {
                        val builder = AlertDialog.Builder(
                            activity!!, R.style.SegoAlertDialog
                        )
                        builder.setTitle("insightof.me")
                        builder.setMessage(R.string.error_no_connection)
                        builder.setPositiveButton(getString(R.string.action_ok)) { dialog, id ->
                            val intent = Intent(getContext(), LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }
                        builder.show()
                    }
                    ErrorCodes.SUCCESS -> {
                        val builder1 = AlertDialog.Builder(
                            activity!!, R.style.SegoAlertDialog
                        )
                        builder1.setTitle("insightof.me")
                        builder1.setMessage(R.string.message_earn_reward_succeed)
                        builder1.setPositiveButton(getString(R.string.action_ok)) { dialog, id -> reloadProfile() }
                        builder1.show()
                    }
                    ErrorCodes.SESSION_EXPIRED -> {
                        val builder2 = AlertDialog.Builder(
                            activity!!, R.style.SegoAlertDialog
                        )
                        builder2.setTitle("insightof.me")
                        builder2.setMessage(R.string.error_session_expired)
                        builder2.setPositiveButton(getString(R.string.action_ok)) { dialog, id -> }
                        builder2.show()
                    }
                }
            }
        }

        //Registers Receivers
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(refreshReceiver!!, IntentFilter(ConstantValues.ACTION_REFRESH_PROFILE))
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(rewardReceiver!!, IntentFilter(ConstantValues.ACTION_EARNED_REWARD))
    }

    @SuppressLint("DefaultLocale")
    private fun initProfileCard() {
        //Populate views
        if (sessionManager.user.userBio != null && sessionManager.user.userBio.isNotEmpty()) {
            binding.tvUserbio.text = sessionManager.user.userBio
        } else {
            binding.tvUserbio.setText(R.string.default_bio_profile)
        }
        binding.tvUsername.text = "@" + sessionManager.user.username
        binding.badgeCredit.text = sessionManager.user.credit.toString()
        binding.badgeRated.text = sessionManager.user.rated.toString()
        Picasso.get().load(sessionManager.user.image)
            .into(binding.userAvatar)
    }

    /**
     * Set up toolbar button actions
     */
    private fun initButtons() {
        //Initialize toolbar buttons
//        btnNewTest = getView().findViewById(R.id.btnAddTest); //TODO Add share button in Designs

        if (!sessionManager.user.hasResults()) {
            binding.btnShareResult.alpha = 0.6f
        }

        //Shows alert dialog for creating test
        if (!sessionManager.user.hasTest()) {
            binding.btnShareTest.alpha = 0.6f
            showDialog()
        }

//        btnNewTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {//TODO Add share button in Designs
//                // Check internet connection
//                ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
//                if(isConnected){
//                    Intent addTestIntent = new Intent(getContext(), WebViewActivity.class);
//                    addTestIntent.putExtra("url", ConstantValues.CREATE_TEST);
//                    addTestIntent.putExtra("title", getString(R.string.activity_label_new_test));
//                    startActivity(addTestIntent);
//                }
//                else {
//                    Snackbar.make(profileSwipeLayout, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();
//                    Log.d("CONNECTION", String.valueOf(isConnected));
//                }
//            }
//        });
        binding.btnShareTest.setOnClickListener {
            if (sessionManager.user.hasTest()) {
                shareTest()
            } else {
//                   Snackbar.make(profileSwipeLayout, R.string.alert_no_test, BaseTransientBottomBar.LENGTH_LONG)
//                       .setAction(R.string.action_btn_new_test, new View.OnClickListener() {//TODO Add share button in Designs
//                           @Override
//                           public void onClick(View v) {
//                               btnNewTest.performClick();
//                           }
//                       }).setActionTextColor(getResources().getColor(R.color.materialLightPurple))
//                       .show();
                Snackbar.make(
                    binding.profileSwipeLayout,
                    R.string.alert_no_test,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show()
            }
        }
        binding.btnShareResult.setOnClickListener {
            if (sessionManager.user.hasResults()) {
                shareResults()
            } else {
                Snackbar.make(
                    binding.profileSwipeLayout,
                    R.string.alert_no_results,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show()
            }
        }
        binding.btnSettings.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Set up profile info card
     */
    private fun initInfoCard() {
        binding.btnInfoShare.setOnClickListener { shareTest() }
    }

    /**
     * Initializes, configure refresh layout.
     */
    private fun initSwipeLayout() {
        //Setup refresh layout
        binding.profileSwipeLayout.setOnRefreshListener { // Check internet connection
            val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
            if (isConnected) {
                LoadProfileTask.loadProfileTask(
                    context,
                    sessionManager.sessionToken,
                    ConstantValues.ACTION_REFRESH_PROFILE
                )
            } else {
                Log.d("CONNECTION", isConnected.toString())
                Snackbar.make(
                    binding.profileSwipeLayout,
                    R.string.error_no_connection,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show()
                binding.profileSwipeLayout.isRefreshing = false
            }
        }
    }

    /**
     * Initializes, loads and shows profile banner ad.
     */
    private fun prepareBannerAd() {
        // Initialize mobile ads
        MobileAds.initialize(activity) { Log.d("MobileAds", "Initialized.") }
        val adTag = "ad_profile_banner"
        // Load ad
        val adRequest = AdRequest.Builder().build()
        binding.profileBannerAdd.loadAd(adRequest)
        // Set ad listener
        binding.profileBannerAdd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(adTag, "Profile banner ad loaded.")
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Log.d(adTag, "Profile banner ad couldn't be loaded")
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d(adTag, "Profile banner ad opened.")
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d(adTag, "Profile banner ad clicked.")
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.d(adTag, "User left application")
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d(adTag, "User returned from ad.")
            }
        }
    }

    /**
     * Shows share result dialog
     */
    private fun shareResults() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val testResultId = SessionManager.getInstance().user.testResultId
        val testUrl = String.format("https://insightof.me/%s", sessionManager.user.testId)
        val shareBody = getString(
            R.string.body_share_results, """
     $testResultId
     
     """.trimIndent(), testUrl
        )
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.action_btn_share_results)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(
            Intent.createChooser(
                sharingIntent,
                getString(R.string.action_btn_share_results)
            )
        )
    }

    /**
     * Shows share test dialog
     */
    private fun shareTest() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val testUrl = String.format("https://insightof.me/%s", sessionManager.user.testId)
        val shareBody = getString(R.string.body_share_test, testUrl)
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.action_btn_share_test)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(
            Intent.createChooser(
                sharingIntent,
                getString(R.string.action_btn_share_test)
            )
        )
    }

    /**
     * Shows a create test dialog
     */
    private fun showDialog() {
        val dialog = Dialog(requireContext(), R.style.SegoAlertDialog)
        dialog.setContentView(R.layout.dialog_create_test)
        val dialogButton = dialog.findViewById<View>(R.id.dialog_button_create_test) as Button
        dialogButton.setOnClickListener {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("url", ConstantValues.CREATE_TEST)
            intent.putExtra("title", getString(R.string.activity_label_new_test))
            startActivity(intent)
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager.user.avatar != null) binding.userAvatar.setImageBitmap(sessionManager.user.avatar)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(refreshReceiver!!)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(rewardReceiver!!)
        binding.unbind()
    }
}