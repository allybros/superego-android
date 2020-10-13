package com.allybros.superego.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.allybros.superego.R;
import com.allybros.superego.activity.UserPageActivity;
import com.allybros.superego.api.EarnRewardTask;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.User;
import com.allybros.superego.ui.ScoresAdapter;
import com.allybros.superego.util.SessionManager;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class ResultsFragment extends Fragment {
    private TextView tvRemainingRates;
    private ListView listViewTraits;
    private SwipeRefreshLayout swipeLayout;
    private User currentUser;
    private BroadcastReceiver resultsRefreshReceiver;
    private AdView adResultBanner;
    private Button btnBadgeCredit, btnShowAd;
    private RewardedAd rewardedAd;
    private SessionManager sessionManager = SessionManager.getInstance();
    //3 states of Result screen represented in an Enum
    private enum State {
        NONE, PARTIAL, COMPLETE
    }

    public ResultsFragment() {
        this.currentUser = SessionManager.getInstance().getUser();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Check state then inflate required layout
        switch (this.getState()) {
            case PARTIAL:
                return inflater.inflate(R.layout.fragment_results_partial, container, false);
            case COMPLETE:
                return inflater.inflate(R.layout.fragment_results_complete, container, false);
            default:
                return inflater.inflate(R.layout.fragment_results_none, container, false);
        }
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupReceiver();
        setupView();
    }

    private State getState(){
        if (currentUser.getScores().size() >= 6)
            return State.COMPLETE;
        else if (currentUser.getScores().size() >= 1)
            return State.PARTIAL;
        else
            return State.NONE;
    }

    private void prepareBannerAd(){
        // Initialize mobile ads
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.d("MobileAds", "Initialized.");
            }
        });
        adResultBanner = getView().findViewById(R.id.resultBannerAdd);
        final String adTag = "ad_result_banner";
        // Load ad
        AdRequest adRequest = new AdRequest.Builder().build();
        adResultBanner.loadAd(adRequest);
        // Set ad listener
        adResultBanner.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(adTag,"Result banner ad loaded.");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d(adTag,"Result banner ad couldn't be loaded");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d(adTag,"Result banner ad opened.");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d(adTag,"Result banner ad clicked.");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.d(adTag,"User left application");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d(adTag,"User returned from ad.");
            }
        });
    }



    //Set up view objects
    @SuppressLint("StringFormatMatches")
    private void setupView(){
        //Setup refresher
        swipeLayout = getView().findViewById(R.id.swipeLayout);
        if (swipeLayout != null)
            swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Check internet connection
                    ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                    if(isConnected){
                        //Start load task
                        LoadProfileTask.loadProfileTask(getContext(),
                                SessionManager.getInstance().getSessionToken(),
                                ConstantValues.ACTION_REFRESH_RESULTS);
                    }
                    else {
                        Snackbar.make(swipeLayout, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();
                        Log.d("CONNECTION", String.valueOf(isConnected));
                        swipeLayout.setRefreshing(false);
                    }

                }
            });

        //Populate views depending on current state
        switch (this.getState()) {
            //One result
            case PARTIAL:
                //Get views
                listViewTraits = getView().findViewById(R.id.listViewPartialTraits);
                tvRemainingRates = getView().findViewById(R.id.tvRemainingRatesPartial);
                btnBadgeCredit = getView().findViewById(R.id.partial_credit_button);
                btnShowAd = getView().findViewById(R.id.button_get_ego);
                //Populate views
                int remainingRates = 10 - (currentUser.getRated() + currentUser.getCredit());
                tvRemainingRates.setText(getString(R.string.remaining_credits, remainingRates));
                listViewTraits.setAdapter( new ScoresAdapter(getActivity(), currentUser.getScores()) );
                btnBadgeCredit.setText(String.format("%d %s", sessionManager.getUser().getCredit(), getString(R.string.credit)));
                prepareRewardedAd();
                btnShowAd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rewardedAd.isLoaded()) {
                            // Check internet connection
                            ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                            if(isConnected){
                                showRewardedAd();
                            }
                            else {
                                Snackbar.make(swipeLayout, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();
                                Log.d("CONNECTION", String.valueOf(isConnected));
                            }
                        } else {
                            // Show error dialog
                            new AlertDialog.Builder(getActivity(), R.style.SegoAlertDialog)
                                    .setTitle("insightof.me")
                                    .setMessage(R.string.info_reward_ad_not_loaded)
                                    .setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                            Log.d("EgoRewardAd", "The rewarded ad wasn't loaded yet.");
                        }
                    }
                });
                break;

            //All results
            case COMPLETE:
                listViewTraits = getView().findViewById(R.id.listViewTraits);
                listViewTraits.setAdapter( new ScoresAdapter(getActivity(), currentUser.getScores()) );
                break;

            //No results
            default:
                //Get views
                tvRemainingRates = getView().findViewById(R.id.tvRatedResultPage);
                //Populate views
                remainingRates = 5 - currentUser.getRated();
                tvRemainingRates.setText( getString(R.string.remaining_credits, remainingRates) );
                prepareBannerAd();
                break;
        }
    }

    //Set up refresh receiver
    private void setupReceiver(){
        resultsRefreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                swipeLayout.setRefreshing(false);

                //Update fragments
                UserPageActivity pageActivity = (UserPageActivity) getActivity();
                if (pageActivity != null) pageActivity.refreshFragments(1);

                if (status == ErrorCodes.SUCCESS) {
                    Log.d("Profile refresh", "Success");
                    swipeLayout.setRefreshing(false);
                } else {
                    swipeLayout.setRefreshing(false); //Last
                    Toast.makeText(getContext(), getContext().getString(R.string.error_no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        };
        //TODO: Replace when new API package is developed
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(resultsRefreshReceiver,new IntentFilter(ConstantValues.ACTION_REFRESH_RESULTS));
    }

    /**
     *  Detects orientation changing and resets view objects and their controller.
     * @param newConfig     represents configs that are current situation of phone. Used for detecting orientation config
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Delete old receivers
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(resultsRefreshReceiver);
            //Reset all view object and their controllers
            setupReceiver();
            setupView();
            prepareBannerAd();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Delete old receivers
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(resultsRefreshReceiver);
            //Reset all view object and their controllers
            setupReceiver();
            setupView();
            prepareBannerAd();
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(resultsRefreshReceiver);
        super.onDestroy();
    }

    /**
     * Initializes and loads rewarded video ad.
     */
    private void prepareRewardedAd(){
        final Context fragmentContext = getActivity();
        if (fragmentContext==null) return;

        this.rewardedAd = new RewardedAd(fragmentContext, getResources().getString(R.string.admob_ad_interface));
        final RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                Log.d("Reward Ad","Ad successfully loaded.");
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                Log.d("Reward Ad","Ad failed to load. Try again");
                prepareRewardedAd();
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }

    /**
     * Shows rewarded Ad and sets RewardAd callback. Calls reward task user if the user earned.
     */
    private void showRewardedAd(){
        final String rewardCallbackTag = "RewardedAdCallback";
        //Prepare rewarded ad callback
        RewardedAdCallback rewardedAdCallback = new RewardedAdCallback() {
            @Override
            public void onRewardedAdOpened() {
                Log.d(rewardCallbackTag,"Ad opened.");
            }
            @Override
            public void onRewardedAdClosed() {
                Log.d(rewardCallbackTag,"Ad closed.");
            }
            @Override
            public void onUserEarnedReward(@NonNull RewardItem reward) {
                Log.d(rewardCallbackTag,"User earned reward.");
                EarnRewardTask.EarnRewardTask(getContext(), sessionManager.getSessionToken());
                Log.d("Reward",""+reward.getAmount());
            }
            @Override
            public void onRewardedAdFailedToShow(int errorCode) {
                Log.d(rewardCallbackTag,"Ad failed to display.");
            }
        };
        //Show rewarded ad
        rewardedAd.show(getActivity(), rewardedAdCallback);
    }
}
