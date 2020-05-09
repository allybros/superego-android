package com.allybros.superego.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import com.allybros.superego.activity.AddTestActivity;
import com.allybros.superego.activity.LoginActivity;
import com.allybros.superego.api.EarnRewardTask;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.api.LoginTask;
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

public class ProfilFragment extends Fragment {

    private TextView tvUsername, tvUserbio, tvProfileInfoCard;
    private Button btnNewTest, btnShareTest, btnShareResults, btnBadgeCredit, btnBadgeRated;
    private CircledNetworkImageView imageViewAvatar;
    private String session_token, uid, password;
    private SwipeRefreshLayout profileSwipeLayout;
    private RewardedAd rewardedAd;
    private AdView mAdView;

    public static final String USER_INFORMATION_PREF = "USER_INFORMATION_PREF";
    private RewardedAdLoadCallback adLoadCallback;
    public ProfilFragment() {
        // Required empty public constructor
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
        checkLogin();
        initializeViewComponents();
        loadProfile();

        profileSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadProfileTask.loadProfileTask(getContext(), SessionManager.getInstance().getSessionToken(), ConstantValues.getActionRefreshProfile());
            }
        });
    }

    private void loadProfile(){
        tvUserbio.setText(SessionManager.getInstance().getUser().getUserBio());
        tvUsername.setText(SessionManager.getInstance().getUser().getUsername());
        btnBadgeCredit.setText(SessionManager.getInstance().getUser().getCredit() + getString(R.string.credit));
        if(SessionManager.getInstance().getUser().getRated()>=5){
            btnBadgeCredit.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.selector_credit));
            btnBadgeCredit.setEnabled(true);
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .repeat(5)
                    .playOn(getView().findViewById(R.id.badgeCredit));
            if(SessionManager.getInstance().getUser().getRated()>=10){
                btnBadgeRated.setText(String.valueOf(getString(R.string.complated)));
            }
        }
        btnBadgeRated.setText(String.valueOf(SessionManager.getInstance().getUser().getRated()+getString(R.string.rated)));
        Log.d("Test--> ",""+SessionManager.getInstance().getUser().getTestId());
        if(SessionManager.getInstance().getUser().getTestId().equals("null")){
            tvProfileInfoCard.setText(R.string.empty_test);
        }else{
            tvProfileInfoCard.setText(R.string.sendTest);
        }
        HelperMethods.imageLoadFromUrl(getContext(), ConstantValues.getAvatarUrl()+SessionManager.getInstance().getUser().getImage(), imageViewAvatar);

        setButtons();
        //BannerAdd Load
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        //Add Load
        rewardedAd = new RewardedAd(getContext(), ConstantValues.getAdmobAddInterfaceId());
        adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                Log.d("ADD-Test","Ad successfully loaded.");
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
                Log.d("ADD-Test","Ad failed to load.");
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }

    private void initializeViewComponents(){
        //Toolbar buttons
        btnNewTest = getView().findViewById(R.id.btnAddTest);
        btnShareTest = getView().findViewById(R.id.btnShareTest);
        btnShareResults = getView().findViewById(R.id.btnShareResult);

        //Profile card components
        imageViewAvatar = getView().findViewById(R.id.profile_image);
        tvUserbio = getView().findViewById(R.id.tvUserbio);
        tvUsername = getView().findViewById(R.id.tvUsername);
        btnBadgeCredit = getView().findViewById(R.id.badgeCredit);
        btnBadgeRated = getView().findViewById(R.id.badgeRated);

        //Info card & ads
        tvProfileInfoCard = getView().findViewById(R.id.tvProfileInfoCard);
        profileSwipeLayout = getView().findViewById(R.id.profileSwipeLayout);
        mAdView = getView().findViewById(R.id.bannerAdd);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(refreshReceiver, new IntentFilter("profile-refresh-status-share"));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(rewardReceiver, new IntentFilter(ConstantValues.getActionEarnedReward()));
    }

    private void checkLogin(){
        //TODO: Bu sistem splash screen static değişkenine dönüştürülecek
        SharedPreferences pref = getContext().getSharedPreferences(USER_INFORMATION_PREF, getContext().MODE_PRIVATE);

        session_token = pref.getString("session_token", "");
        uid=pref.getString("uid","");
        password=pref.getString("password","");
        if(session_token.isEmpty()) {
            LoginTask.loginTask(getContext(),uid,password);
        }
        session_token=pref.getString("session_token","");
    }

    private void setButtons(){
        btnNewTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addTestIntent= new Intent(getContext(), AddTestActivity.class);
                startActivity(addTestIntent);
            }
        });

        btnShareTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String testUrl = String.format("https://insightof.me/%s", SessionManager.getInstance().getUser().getTestId());
                String shareBody = getString(R.string.share_test_body, testUrl);
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.btn_share_test);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.btn_share_test)));
            }
        });

        btnShareResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            ArrayList<Score> scores = SessionManager.getInstance().getUser().getScores();
            StringBuilder resultsBuilder = new StringBuilder();
            for (int i = 0; i < scores.size(); i++) {
                @SuppressLint("DefaultLocale")
                String scoreLine = String.format("%d) %s\n", i+1, scores.get(i).getTraitName() );
                resultsBuilder.append(scoreLine);
            }
            String testUrl = String.format("https://insightof.me/%s", SessionManager.getInstance().getUser().getTestId());
            String shareBody = getString(R.string.share_results_body, resultsBuilder.toString(), testUrl);
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.btn_share_results);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.btn_share_results)));
            }
        });

        btnBadgeCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(Html.fromHtml("<font color='#000000'>"+getString(R.string.app_name)+"</font>"));
                builder.setMessage(Html.fromHtml("<font color='#000000'>"+getString(R.string.add_text)+"</font>"));
                builder.setPositiveButton( getString(R.string.okey), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (rewardedAd.isLoaded()) {
                            Activity activityContext = getActivity();
                            RewardedAdCallback adCallback = new RewardedAdCallback() {
                                @Override
                                public void onRewardedAdOpened() {
                                    // Ad opened.
                                    Log.d("ADD-Test","Ad opened.");
                                }
                                @Override
                                public void onRewardedAdClosed() {
                                    // Ad closed.
                                    Log.d("ADD-Test","Ad closed.");
                                }
                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem reward) {
                                    // User earned reward.
                                    Log.d("ADD-Test","User earned reward.");
                                    EarnRewardTask.EarnRewardTask(getContext(), SessionManager.getInstance().getSessionToken());
                                    Log.d("Reward",""+reward.getAmount());
                                }
                                @Override
                                public void onRewardedAdFailedToShow(int errorCode) {
                                    // Ad failed to display.
                                    Log.d("ADD-Test","Ad failed to display.");
                                }
                            };
                            rewardedAd.show(activityContext, adCallback);
                        } else {
                            Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                        }
                    }
                });
                builder.show();
            }
        });

        //TODO :: seperate Ad initialization
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d("BANNER","Code to be executed when an ad finishes loading");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.d("BANNER","Code to be executed when an ad request fails.");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d("BANNER","Code to be executed when an ad opens an overlay that");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d("BANNER","Code to be executed when the user clicks on an ad.");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.d("BANNER","Code to be executed when the user has left the app.");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d("BANNER","Code to be executed when the user is about to return");
            }
        });

            tvProfileInfoCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("Click","Clika");
                    if(SessionManager.getInstance().getUser().getTestId().equals("null")){
                        Intent addTestIntent= new Intent(getContext(), AddTestActivity.class);
                        startActivity(addTestIntent);
                    }else{
                        Log.d("Click","Cliks");
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "Beni nasıl görüyorsun? -->"+SessionManager.getInstance().getUser().getTestId();
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.btn_share_results);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }
                }
            });
    }

    private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
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
                    profileSwipeLayout.setRefreshing(false);
                    Log.d("Refresh Procceas","Refresaf");
                    loadProfile();
                    break;
            }
        }
    };

    private BroadcastReceiver rewardReceiver = new BroadcastReceiver() {
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
                            LoadProfileTask.loadProfileTask(getContext(), SessionManager.getInstance().getSessionToken(),"load");
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


    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(refreshReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(rewardReceiver);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SessionManager.getInstance().getUser().getAvatar()!=null) imageViewAvatar.setImageBitmap(SessionManager.getInstance().getUser().getAvatar());
    }

}
