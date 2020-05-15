package com.allybros.superego.fragments;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.allybros.superego.activity.WebViewActivity;
import com.allybros.superego.activity.LoginActivity;
import com.allybros.superego.activity.UserPageActivity;
import com.allybros.superego.api.EarnRewardTask;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.Score;
import com.allybros.superego.util.CircledNetworkImageView;
import com.allybros.superego.util.HelperMethods;
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

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvUserbio, tvProfileInfoCard;
    private Button btnNewTest, btnShareTest, btnShareResults, btnBadgeCredit, btnBadgeRated;
    private CircledNetworkImageView imageViewAvatar;
    private SwipeRefreshLayout profileSwipeLayout;
    private RewardedAd rewardedAd;
    private AdView adProfileBanner;
    //API Receivers
    private BroadcastReceiver refreshReceiver;
    private BroadcastReceiver rewardReceiver;
    //Current session
    private SessionManager sessionManager = SessionManager.getInstance();
    private boolean newTest = false;

    public ProfileFragment() {
        // Set Up receivers
        refreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                Log.d("receiver", "Got message: " + status);
                switch (status){
                    case ErrorCodes.SYSFAIL:
                        profileSwipeLayout.setRefreshing(false); //Last
                        Toast.makeText(getContext(), getContext().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                        break;

                    case ErrorCodes.SUCCESS:
                        Log.d("Profile refresh","Success");
                        profileSwipeLayout.setRefreshing(false);
                        UserPageActivity userPageActivity = (UserPageActivity) getActivity();
                        userPageActivity.refreshFragments(0);
                        break;
                }
            }
        };

        rewardReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                Log.d("receiver", "Got message: " + status);
                switch (status){
                    case ErrorCodes.SYSFAIL:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("insightof.me");
                        builder.setMessage(R.string.reward_earned_sysfail);
                        builder.setPositiveButton( getString(R.string.okey), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            }
                        });
                        builder.show();
                        break;

                    case ErrorCodes.SUCCESS:
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                        builder1.setTitle("insightof.me");
                        builder1.setMessage(R.string.earned_reward);
                        builder1.setPositiveButton( getString(R.string.okey), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                LoadProfileTask.loadProfileTask(getContext(), sessionManager.getSessionToken(),"load");
                            }
                        });
                        builder1.show();
                        break;

                    case ErrorCodes.SESSION_EXPIRED:
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                        builder2.setTitle("insightof.me");
                        builder2.setMessage(R.string.reward_earned_session_expired);
                        builder2.setPositiveButton( getString(R.string.okey), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                        builder2.show();
                        break;
                }
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(refreshReceiver, new IntentFilter("profile-refresh-status-share"));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(rewardReceiver, new IntentFilter(ConstantValues.getActionEarnedReward()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initProfileCard();
        initButtons();
        initInfoCard();
        prepareRewardedAd();
        prepareBannerAd();

        //Setup refresh layout
        profileSwipeLayout = getView().findViewById(R.id.profileSwipeLayout);
        profileSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            LoadProfileTask.loadProfileTask(getContext(), sessionManager.getSessionToken(), ConstantValues.getActionRefreshProfile());
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void initProfileCard(){
        //Set profile card components
        imageViewAvatar = getView().findViewById(R.id.user_avatar);
        tvUserbio = getView().findViewById(R.id.tvUserbio);
        tvUsername = getView().findViewById(R.id.tvUsername);
        btnBadgeCredit = getView().findViewById(R.id.badgeCredit);
        btnBadgeRated = getView().findViewById(R.id.badgeRated);

        //Populate views
        tvUserbio.setText(sessionManager.getUser().getUserBio());
        tvUsername.setText(sessionManager.getUser().getUsername());
        btnBadgeCredit.setText(String.format("%d %s", sessionManager.getUser().getCredit(), getString(R.string.credit)));

        if(sessionManager.getUser().getRated() >= 5){
            //User able to use Ego points
            btnBadgeCredit.setBackground(ContextCompat.getDrawable(getContext() ,R.drawable.selector_credit));
            btnBadgeCredit.setEnabled(true);
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .repeat(5)
                    .playOn(getView().findViewById(R.id.badgeCredit));
            if(sessionManager.getUser().getRated()>=10){
                btnBadgeRated.setText(getString(R.string.complated));
            }
        }

        btnBadgeCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(Html.fromHtml("<font color='#000000'>"+getString(R.string.app_name)+"</font>"));
                builder.setMessage(Html.fromHtml("<font color='#000000'>"+getString(R.string.add_text)+"</font>"));
                builder.setPositiveButton(getString(R.string.okey), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (rewardedAd.isLoaded()) {
                            showRewardedAd();
                        } else {
                            Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        btnBadgeRated.setText(sessionManager.getUser().getRated() + getString(R.string.rated));
        Log.d("Test Id", sessionManager.getUser().getTestId());

        HelperMethods.imageLoadFromUrl(getContext(), ConstantValues.getAvatarUrl()+sessionManager.getUser().getImage(), imageViewAvatar);
    }

    /**
     * Set up toolbar button actions
     */
    private void initButtons(){
        //Initialize toolbar buttons
        btnNewTest = getView().findViewById(R.id.btnAddTest);
        btnShareTest = getView().findViewById(R.id.btnShareTest);
        btnShareResults = getView().findViewById(R.id.btnShareResult);

        btnNewTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent addTestIntent = new Intent(getContext(), WebViewActivity.class);
            addTestIntent.putExtra("url", ConstantValues.getCreateTest());
            addTestIntent.putExtra("title", getString(R.string.title_activity_new_test));
            startActivity(addTestIntent);
            }
        });

        btnShareTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTest();
            }
        });

        btnShareResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareResults();
            }
        });
    }

    /**
     * Set up profile info card
     */
    private void initInfoCard(){
        tvProfileInfoCard = getView().findViewById(R.id.tvProfileInfoCard);

        if(sessionManager.getUser().getTestId().equals("null")){ //User don't have a test
            tvProfileInfoCard.setText(R.string.infocard_no_test);
        }else{//User have test
            tvProfileInfoCard.setText(R.string.infocard_share_test);
        }

        tvProfileInfoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTest();
            }
        });
    }

    /**
     * Initializes, loads and shows profile banner ad.
     */
    private void prepareBannerAd(){
        adProfileBanner = getView().findViewById(R.id.bannerAdd);
        final String adTag = "ad_profile_banner";
        // Load ad
        AdRequest adRequest = new AdRequest.Builder().build();
        adProfileBanner.loadAd(adRequest);
        // Set ad listener
        adProfileBanner.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(adTag,"Profile banner ad loaded.");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d(adTag,"Profile banner ad couldn't be loaded");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d(adTag,"Profile banner ad opened.");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d(adTag,"Profile banner ad clicked.");
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
        // Initialize mobile ads
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.d("MobileAds", "Initialized.");
            }
        });
    }

    /**
     * Initializes and loads rewarded video ad.
     */
    private void prepareRewardedAd(){
        this.rewardedAd = new RewardedAd(getActivity(), ConstantValues.getAdmobAddInterfaceId());
        final RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                Log.d("Reward Ad","Ad successfully loaded.");
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                Log.d("Reward Ad","Ad failed to load.");
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

    /**
     * Shows share result dialog
     */
    private void shareResults(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        ArrayList<Score> scores = sessionManager.getUser().getScores();
        StringBuilder resultsBuilder = new StringBuilder();
        for (int i = 0; i < scores.size(); i++) {
            @SuppressLint("DefaultLocale")
            String scoreLine = String.format("%d) %s\n", i+1, scores.get(i).getTraitName() );
            resultsBuilder.append(scoreLine);
        }
        //TODO: Decouple test url from here
        String testUrl = String.format("https://insightof.me/%s", sessionManager.getUser().getTestId());
        String shareBody = getString(R.string.share_results_body, resultsBuilder.toString(), testUrl);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.btn_share_results);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.btn_share_results)));
    }

    /**
     * Shows share test dialog
     */
    private void shareTest(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String testUrl = String.format("https://insightof.me/%s", sessionManager.getUser().getTestId());
        String shareBody = getString(R.string.share_test_body, testUrl);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.btn_share_test);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.btn_share_test)));
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(refreshReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(rewardReceiver);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(sessionManager.getUser().getAvatar()!=null) imageViewAvatar.setImageBitmap(sessionManager.getUser().getAvatar());
    }

}
