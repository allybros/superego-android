package com.allybros.superego.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.allybros.superego.R;
import com.allybros.superego.activity.LoginActivity;
import com.allybros.superego.activity.SettingsActivity;
import com.allybros.superego.activity.UserPageActivity;
import com.allybros.superego.activity.WebViewActivity;
import com.allybros.superego.api.EarnRewardTask;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.util.SessionManager;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvUserbio, badgeCredit, badgeRated;
    private Button btnShareTest, btnShareResults, btnInfoShare;         //btnNewTest TODO Add share button in Designs
    private ImageView btnSettings;
    private CircleImageView imageViewAvatar;
    private SwipeRefreshLayout profileSwipeLayout;
    private AdView adProfileBanner;
    //API Receivers
    private BroadcastReceiver refreshReceiver;
    private BroadcastReceiver rewardReceiver;
    //Current session
    private SessionManager sessionManager = SessionManager.getInstance();

    public ProfileFragment() {}

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
        setupReceivers();
        initProfileCard();
        initButtons();
        initInfoCard();
        prepareBannerAd();
        initSwipeLayout();
    }

    /**
     * Reloads profile content with API.
     */
    public void reloadProfile(){
        // Call API task
        LoadProfileTask.loadProfileTask(getActivity().getApplicationContext(), sessionManager.getSessionToken(), ConstantValues.ACTION_REFRESH_PROFILE);
    }

    /**
     * Does configure receivers
     */
    private void setupReceivers(){
        // Set Up receivers
        refreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                Log.d("receiver", "Got message: " + status);

                switch (status){
                    case ErrorCodes.SYSFAIL:
                        Snackbar.make(profileSwipeLayout, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();
                        break;

                    case ErrorCodes.SUCCESS:
                        Log.d("Profile refresh","Success");
                        UserPageActivity userPageActivity = (UserPageActivity) getActivity();
                        userPageActivity.refreshFragments(0);
                        break;
                }

                // Disable progress view
                UserPageActivity upa = (UserPageActivity) getActivity();
                if (upa != null) upa.setProgressVisibility(false);
                profileSwipeLayout.setRefreshing(false);
            }
        };

        rewardReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                switch (status){
                    case ErrorCodes.SYSFAIL:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.SegoAlertDialog);
                        builder.setTitle("insightof.me");
                        builder.setMessage(R.string.error_no_connection);
                        builder.setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });
                        builder.show();
                        break;

                    case ErrorCodes.SUCCESS:
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity(), R.style.SegoAlertDialog);
                        builder1.setTitle("insightof.me");
                        builder1.setMessage(R.string.message_earn_reward_succeed);
                        builder1.setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                reloadProfile();
                            }
                        });
                        builder1.show();
                        break;

                    case ErrorCodes.SESSION_EXPIRED:
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity(), R.style.SegoAlertDialog);
                        builder2.setTitle("insightof.me");
                        builder2.setMessage(R.string.error_session_expired);
                        builder2.setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                        builder2.show();
                        break;
                }
            }
        };

        //Registers Receivers
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(refreshReceiver, new IntentFilter(ConstantValues.ACTION_REFRESH_PROFILE));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(rewardReceiver, new IntentFilter(ConstantValues.ACTION_EARNED_REWARD));
    }

    @SuppressLint("DefaultLocale")
    private void initProfileCard(){
        //Set profile card components
        imageViewAvatar = getView().findViewById(R.id.user_avatar);
        tvUserbio = getView().findViewById(R.id.tvUserbio);
        tvUsername = getView().findViewById(R.id.tvUsername);
        badgeCredit = getView().findViewById(R.id.badgeCredit);
        badgeRated = getView().findViewById(R.id.badgeRated);

        //Populate views
        if (sessionManager.getUser().getUserBio() != null && !sessionManager.getUser().getUserBio().isEmpty()) {
            tvUserbio.setText(sessionManager.getUser().getUserBio());
        } else {
            tvUserbio.setText(R.string.default_bio_profile);
        }

        tvUsername.setText("@"+sessionManager.getUser().getUsername());
        badgeCredit.setText(String.valueOf(sessionManager.getUser().getCredit()));
        badgeRated.setText(String.valueOf(sessionManager.getUser().getRated()));


        Picasso.get().load(ConstantValues.AVATAR_URL+sessionManager.getUser().getImage()).into(imageViewAvatar);
    }

    /**
     * Set up toolbar button actions
     */
    private void initButtons(){
        //Initialize toolbar buttons
//        btnNewTest = getView().findViewById(R.id.btnAddTest); //TODO Add share button in Designs
        btnShareTest = getView().findViewById(R.id.btnShareTest);
        btnShareResults = getView().findViewById(R.id.btnShareResult);
        btnSettings = getView().findViewById(R.id.btnSettings);

        if (!sessionManager.getUser().hasResults()) {
            btnShareResults.setAlpha(0.6f);
        }

        //Shows alert dialog for creating test
        if (!sessionManager.getUser().hasTest()) {
            btnShareTest.setAlpha(0.6f);
            showDialog();
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

        btnShareTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.getUser().hasTest()) {
                    shareTest();
                } else {
//                   Snackbar.make(profileSwipeLayout, R.string.alert_no_test, BaseTransientBottomBar.LENGTH_LONG)
//                       .setAction(R.string.action_btn_new_test, new View.OnClickListener() {//TODO Add share button in Designs
//                           @Override
//                           public void onClick(View v) {
//                               btnNewTest.performClick();
//                           }
//                       }).setActionTextColor(getResources().getColor(R.color.materialLightPurple))
//                       .show();

                    Snackbar.make(profileSwipeLayout, R.string.alert_no_test, BaseTransientBottomBar.LENGTH_LONG).show();

                }
            }
        });

        btnShareResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.getUser().hasResults()) {
                    shareResults();
                } else {
                    Snackbar.make(profileSwipeLayout, R.string.alert_no_results, BaseTransientBottomBar.LENGTH_LONG).show();
                }
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Set up profile info card
     */
    private void initInfoCard(){
        btnInfoShare = getView().findViewById(R.id.btnInfoShare);

        btnInfoShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTest();
            }
        });
    }

    /**
     * Initializes, configure refresh layout.
     */
    private void initSwipeLayout(){
        //Setup refresh layout
        profileSwipeLayout = getView().findViewById(R.id.profileSwipeLayout);
        profileSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Check internet connection
                ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if(isConnected) {
                    LoadProfileTask.loadProfileTask(getContext(), sessionManager.getSessionToken(), ConstantValues.ACTION_REFRESH_PROFILE);
                }
                else {
                    Log.d("CONNECTION", String.valueOf(isConnected));
                    Snackbar.make(profileSwipeLayout, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();
                    profileSwipeLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     * Initializes, loads and shows profile banner ad.
     */
    private void prepareBannerAd(){
        // Initialize mobile ads
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.d("MobileAds", "Initialized.");
            }
        });
        adProfileBanner = getView().findViewById(R.id.profileBannerAdd);
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
    }

    /**
     * Shows share result dialog
     */
    private void shareResults(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String testResultId = SessionManager.getInstance().getUser().getTestResultId();
        String testUrl = String.format("https://insightof.me/%s", sessionManager.getUser().getTestId());

        String shareBody = getString(R.string.body_share_results, testResultId+"\n", testUrl);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.action_btn_share_results);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.action_btn_share_results)));
    }

    /**
     * Shows share test dialog
     */
    private void shareTest(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String testUrl = String.format("https://insightof.me/%s", sessionManager.getUser().getTestId());
        String shareBody = getString(R.string.body_share_test, testUrl);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.action_btn_share_test);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.action_btn_share_test)));
    }

    /**
     * Shows a create test dialog
     */
    private void showDialog(){

        final Dialog dialog = new Dialog(getContext(), R.style.SegoAlertDialog);
        dialog.setContentView(R.layout.dialog_create_test);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_button_create_test);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("url", ConstantValues.CREATE_TEST);
                intent.putExtra("title", getString(R.string.activity_label_new_test));
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
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