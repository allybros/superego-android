package com.allybros.superego.fragment.result.partial

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.allybros.superego.R
import com.allybros.superego.activity.userpage.UserPageActivity
import com.allybros.superego.adapter.ScoresAdapter
import com.allybros.superego.api.EarnRewardTask
import com.allybros.superego.api.LoadProfileTask
import com.allybros.superego.databinding.FragmentResultsPartialBinding
import com.allybros.superego.unit.ConstantValues
import com.allybros.superego.unit.ErrorCodes
import com.allybros.superego.unit.State
import com.allybros.superego.unit.User
import com.allybros.superego.util.SessionManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

//TODO Implement databinding and viewmodel
class ResultPartialFragment : Fragment() {
    private var tvRemainingRates: TextView? = null
    private var listViewTraits: ListView? = null
    private var swipeLayout: SwipeRefreshLayout? = null
    private val currentUser: User = SessionManager.getInstance().user
    private var resultsRefreshReceiver: BroadcastReceiver? = null
    private var adResultBanner: AdView? = null
    private var btnShowAd: Button? = null
    private var btnShareTestResult: Button? = null
    private var rewardedAd: RewardedAd? = null
    var clearHelper: ScoresAdapter? = null
    private var ivShareResults: ImageView? = null
    private val sessionManager = SessionManager.getInstance()

    lateinit var binding: FragmentResultsPartialBinding
    val viewModel: ResultPartialVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_results_partial,
            container,
            false
        )
        return binding.root
    }

    @SuppressLint("StringFormatMatches")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupReceiver()
        setupView()
    }

    private val state: State
        get() = when {
            currentUser.scores.size >= 6 -> State.COMPLETE
            currentUser.scores.size >= 1 -> State.PARTIAL
            else -> State.NONE
        }

    private fun prepareBannerAd() {
        // Initialize mobile ads
        MobileAds.initialize(activity) { Log.d("MobileAds", "Initialized.") }
        adResultBanner = requireView().findViewById(R.id.resultBannerAdd)
        val adTag = "ad_result_banner"
        // Load ad
        val adRequest = AdRequest.Builder().build()
        adResultBanner?.loadAd(adRequest)
        // Set ad listener
        adResultBanner?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(adTag, "Result banner ad loaded.")
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Log.d(adTag, "Result banner ad couldn't be loaded")
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d(adTag, "Result banner ad opened.")
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d(adTag, "Result banner ad clicked.")
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

    //Set up view objects
    @SuppressLint("StringFormatMatches")
    private fun setupView() {
        //Setup refresher
        swipeLayout = requireView().findViewById(R.id.swipeLayout)
        if (swipeLayout != null) swipeLayout!!.setOnRefreshListener {
            // Check internet connection
            val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
            if (isConnected) {
                //Start load task
                LoadProfileTask.loadProfileTask(
                    context,
                    SessionManager.getInstance().sessionToken,
                    ConstantValues.ACTION_REFRESH_RESULTS
                )
            } else {
                Snackbar.make(
                    swipeLayout!!,
                    R.string.error_no_connection,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show()
                Log.d("CONNECTION", isConnected.toString())
                swipeLayout!!.isRefreshing = false
            }
        }
        var remainingRates = 10 - (currentUser.rated + currentUser.credit)
        when (state) {
            State.PARTIAL -> {
                //Get views
                listViewTraits = requireView().findViewById(R.id.listViewPartialTraits)
                btnShowAd = requireView().findViewById(R.id.button_get_ego)
                tvRemainingRates = requireView().findViewById(R.id.tvRatedResultPage)
                ivShareResults = requireView().findViewById(R.id.ivShareResults)

                //Populate views

                tvRemainingRates?.text = getString(R.string.remaining_credits, remainingRates)
                clearHelper = listViewTraits?.adapter as ScoresAdapter
                if (clearHelper != null) {
                    clearHelper!!.clear()
                    clearHelper!!.notifyDataSetChanged()
                }
                listViewTraits?.adapter = ScoresAdapter(activity, currentUser.scores, false, null)
                prepareRewardedAd()
                btnShowAd?.setOnClickListener(View.OnClickListener {
                    if (rewardedAd!!.isLoaded) {
                        // Check internet connection
                        val cm =
                            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetwork = cm.activeNetworkInfo
                        val isConnected =
                            activeNetwork != null && activeNetwork.isConnectedOrConnecting
                        if (isConnected) {
                            showRewardedAd()
                        } else {
                            Snackbar.make(
                                swipeLayout!!,
                                R.string.error_no_connection,
                                BaseTransientBottomBar.LENGTH_LONG
                            ).show()
                            Log.d("CONNECTION", isConnected.toString())
                        }
                    } else {
                        // Show error dialog
                        AlertDialog.Builder(requireActivity(), R.style.SegoAlertDialog)
                            .setTitle("insightof.me")
                            .setMessage(R.string.info_reward_ad_not_loaded)
                            .setPositiveButton(getString(R.string.action_ok)) { dialog, which -> dialog.dismiss() }
                            .show()
                        Log.d("EgoRewardAd", "The rewarded ad wasn't loaded yet.")
                    }
                })
                ivShareResults?.setOnClickListener { shareTest() }
            }
            State.COMPLETE -> {
                listViewTraits = requireView().findViewById(R.id.listViewTraits)
                listViewTraits?.adapter = ScoresAdapter(
                    activity,
                    currentUser.scores,
                    true
                ) { shareResults() }
                clearHelper = listViewTraits?.adapter as ScoresAdapter
                if (clearHelper != null) {
                    clearHelper!!.clear()
                    clearHelper!!.notifyDataSetChanged()
                }
            }
            else -> {
                //Get views
                tvRemainingRates = requireView().findViewById(R.id.tvRatedResultPage)
                btnShowAd = requireView().findViewById(R.id.button_get_ego)
                btnShareTestResult = requireView().findViewById(R.id.btnShareTestResult)

                //Populate views
                remainingRates = 5 - currentUser.rated
                tvRemainingRates?.text = getString(R.string.remaining_credits, remainingRates)
                prepareBannerAd()
                prepareRewardedAd()
                btnShareTestResult?.setOnClickListener {
                    if (sessionManager.user.hasTest()) {
                        shareTest()
                    } else {
                        Snackbar.make(
                            swipeLayout!!,
                            R.string.alert_no_test,
                            BaseTransientBottomBar.LENGTH_LONG
                        )
                            .setActionTextColor(resources.getColor(R.color.materialLightPurple))
                            .show()
                    }
                }
            }
        }
    }

    //Set up refresh receiver
    private fun setupReceiver() {
        resultsRefreshReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val status = intent.getIntExtra("status", 0)
                swipeLayout!!.isRefreshing = false

                //Update fragments
                val pageActivity = activity as UserPageActivity?
                pageActivity?.refreshFragments(1)
                if (status == ErrorCodes.SUCCESS) {
                    Log.d("Profile refresh", "Success")
                    swipeLayout!!.isRefreshing = false
                } else {
                    swipeLayout!!.isRefreshing = false //Last
                    Toast.makeText(
                        getContext(),
                        requireContext().getString(R.string.error_no_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        //TODO: Replace when new API package is developed
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            resultsRefreshReceiver!!,
            IntentFilter(ConstantValues.ACTION_REFRESH_RESULTS)
        )
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(resultsRefreshReceiver!!)
        super.onDestroy()
    }

    /**
     * Initializes and loads rewarded video ad.
     */
    private fun prepareRewardedAd() {
        val fragmentContext = activity ?: return
        rewardedAd = RewardedAd(fragmentContext, resources.getString(R.string.admob_ad_interface))
        val adLoadCallback: RewardedAdLoadCallback = object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                // Ad successfully loaded.
                Log.d("Reward Ad", "Ad successfully loaded.")
            }

            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                // Ad failed to load.
                Log.d("Reward Ad", "Ad failed to load. Try again")
                //prepareRewardedAd();
            }
        }
        rewardedAd!!.loadAd(AdRequest.Builder().build(), adLoadCallback)
    }

    /**
     * Shows rewarded Ad and sets RewardAd callback. Calls reward task user if the user earned.
     */
    private fun showRewardedAd() {
        val rewardCallbackTag = "RewardedAdCallback"
        //Prepare rewarded ad callback
        val rewardedAdCallback: RewardedAdCallback = object : RewardedAdCallback() {
            override fun onRewardedAdOpened() {
                Log.d(rewardCallbackTag, "Ad opened.")
            }

            override fun onRewardedAdClosed() {
                Log.d(rewardCallbackTag, "Ad closed.")
            }

            override fun onUserEarnedReward(reward: RewardItem) {
                Log.d(rewardCallbackTag, "User earned reward.")
                EarnRewardTask.EarnRewardTask(context, sessionManager.sessionToken)
                Log.d("Reward", "" + reward.amount)
            }

            override fun onRewardedAdFailedToShow(errorCode: Int) {
                Log.d(rewardCallbackTag, "Ad failed to display.")
            }
        }
        //Show rewarded ad
        rewardedAd!!.show(activity, rewardedAdCallback)
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
}