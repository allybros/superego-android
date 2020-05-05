package com.allybros.superego.fragments;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.allybros.superego.activity.SplashActivity;
import com.allybros.superego.api.EarnRewardTask;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.api.LoginTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.User;
import com.allybros.superego.util.CircledNetworkImageView;
import com.allybros.superego.util.HelperMethods;
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

public class ProfilFragment extends Fragment {

    private TextView tvUsernameProfilPage,tvUserInfoProfilPage,tvAppInformationProfilePage;
    private Button addTest,copyTestLink, shareTest, btCredit, btScore;
    private CircledNetworkImageView avatar;
    private String session_token,uid,password;
    private SwipeRefreshLayout profileSwipeLayout;
    private RewardedAd rewardedAd;
    private AdView mAdView;

    public static final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";
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

        chechkLogin();
        initializeViewComponents();
        loadProfile();



        profileSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadProfileTask.loadProfileTask(getContext(),SplashActivity.session_token,ConstantValues.getActionRefreshProfile());

            }
        });
    }

    private void loadProfile(){
        tvUserInfoProfilPage.setText(User.getUserBio());
        tvUsernameProfilPage.setText(User.getUsername());
        btCredit.setText(User.getCredit()+getString(R.string.credit));
        if(User.getRated()>=5){
            btCredit.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.selector_credit));
            btCredit.setEnabled(true);
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .repeat(5)
                    .playOn(getView().findViewById(R.id.credit));
            if(User.getRated()>=10){
                btScore.setText(String.valueOf(getString(R.string.complated)));
            }
        }
        btScore.setText(String.valueOf(User.getRated()+getString(R.string.rated)));
        Log.d("Test--> ",""+User.getTestId());
        if(User.getTestId().equals("null")){
            tvAppInformationProfilePage.setText(R.string.empty_test);
        }else{
            tvAppInformationProfilePage.setText(R.string.sendTest);
        }
        HelperMethods.imageLoadFromUrl(getContext(), ConstantValues.getAvatarUrl()+User.getImage(),avatar);

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
        addTest =(Button) getView().findViewById(R.id.addTest);
        copyTestLink=(Button) getView().findViewById(R.id.copyTestLink);
        shareTest =(Button) getView().findViewById(R.id.shareTest);
        tvUserInfoProfilPage =(TextView) getView().findViewById(R.id.tvUserInfoProfilPage);
        tvUsernameProfilPage =(TextView) getView().findViewById(R.id.tvUsernameProfilPage);
        tvAppInformationProfilePage=(TextView) getView().findViewById(R.id.tvAppInformationProfilePage);
        avatar= (CircledNetworkImageView) getView().findViewById(R.id.profile_image);
        profileSwipeLayout = (SwipeRefreshLayout) getView().findViewById(R.id.profileSwipeLayout);
        btCredit = (Button) getView().findViewById(R.id.credit);
        btScore = (Button) getView().findViewById(R.id.score);
        mAdView = getView().findViewById(R.id.bannerAdd);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(refreshReceiver, new IntentFilter("profile-refresh-status-share"));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(rewardReceiver, new IntentFilter(ConstantValues.getActionEarnedReward()));


    }

    private void chechkLogin(){
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
        addTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addTestIntent= new Intent(getContext(), AddTestActivity.class);
                startActivity(addTestIntent);
            }
        });

        copyTestLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("testUrl", User.getTestId());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(),getString(R.string.link_copied),Toast.LENGTH_SHORT).show();
            }
        });

        shareTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Beni nasıl görüyorsun? -->"+User.getTestId();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.shareTest);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
        btCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("insightof.me");
                builder.setMessage(getString(R.string.add_text));
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
                                    EarnRewardTask.EarnRewardTask(getContext(),SplashActivity.session_token);
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

            tvAppInformationProfilePage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("Click","Clika");
                    if(User.getTestId().equals("null")){
                        Intent addTestIntent= new Intent(getContext(), AddTestActivity.class);
                        startActivity(addTestIntent);
                    }else{
                        Log.d("Click","Cliks");
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "Beni nasıl görüyorsun? -->"+User.getTestId();
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.shareTest);
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
                            LoadProfileTask.loadProfileTask(getContext(),SplashActivity.session_token,null);
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
        if(User.getAvatar()!=null) avatar.setImageBitmap(User.getAvatar());
    }
}