package com.allybros.superego.fragment;

import static com.allybros.superego.unit.ConstantValues.EMOJI_END_POINT;
import static com.allybros.superego.unit.ConstantValues.WEB_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.allybros.superego.R;
import com.allybros.superego.activity.UserPageActivity;
import com.allybros.superego.activity.WebViewActivity;
import com.allybros.superego.api.EarnRewardTask;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.Ocean;
import com.allybros.superego.unit.Score;
import com.allybros.superego.unit.User;
import com.allybros.superego.adapter.ScoresAdapter;
import com.allybros.superego.util.SessionManager;
import com.allybros.superego.widget.SegoProgressBar;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.OnAdMetadataChangedListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewarded.ServerSideVerificationOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;


public class ResultsFragment extends Fragment {
    private TextView tvRemainingRates;
    private ListView listViewTraits;
    private SwipeRefreshLayout swipeLayout;
    private User currentUser;
    private BroadcastReceiver resultsRefreshReceiver;
    private AdView adResultBanner;
    private Button btnShowAd, btnShareTestResult, btnCreateTest;
    private RewardedAd rewardedAd;

    ScoresAdapter clearHelper;

    private ImageView ivShareResults;

    private LinearLayout llScoresContainer;
    private SessionManager sessionManager = SessionManager.getInstance();

    //3 states of Result screen represented in an Enum
    private enum State {
        NONE, NONE_TEST, PARTIAL, COMPLETE
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
            case NONE:
                return inflater.inflate(R.layout.fragment_results_none_test, container, false);
            default:
                return inflater.inflate(R.layout.fragment_result_none_no_test, container, false);
        }
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupReceiver();
        setupView();
    }

    private State getState() {
        if (currentUser.getScores().size() >= 6)
            return State.COMPLETE;
        else if (currentUser.getScores().size() >= 1)
            return State.PARTIAL;
        else if (sessionManager.getUser().getTestId() != null && !sessionManager.getUser().getTestId().isEmpty()) {
            return State.NONE;
        } else {
            return State.NONE_TEST;
        }

    }

    private void prepareBannerAd() {
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
                Log.d(adTag, "Result banner ad loaded.");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d(adTag, "Result banner ad opened.");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d(adTag, "Result banner ad clicked.");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d(adTag, "User returned from ad.");
            }
        });
    }


    //Set up view objects
    @SuppressLint("StringFormatMatches")
    private void setupView() {
        //Setup refresher
        swipeLayout = getView().findViewById(R.id.swipeLayout);
        if (swipeLayout != null)
            swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Check internet connection
                    ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                    if (isConnected) {
                        //Start load task
                        LoadProfileTask.loadProfileTask(getContext(),
                                SessionManager.getInstance().getSessionToken(),
                                ConstantValues.ACTION_REFRESH_RESULTS);
                    } else {
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
                llScoresContainer = getView().findViewById(R.id.llScoresContainer);
                btnShowAd = getView().findViewById(R.id.button_get_ego);
                tvRemainingRates = getView().findViewById(R.id.tvRatedResultPage);

                //Populate views
                int remainingRates = 10 - (currentUser.getRated() + currentUser.getCredit());
                tvRemainingRates.setText(getString(R.string.remaining_credits, remainingRates));

                fillScores(llScoresContainer, currentUser.getScores());
                prepareRewardedAd();
                prepareBannerAd();
                btnShowAd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rewardedAd != null) {
                            // Check internet connection
                            ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                            if (isConnected) {
                                showRewardedAd();
                            } else {
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
                llScoresContainer = getView().findViewById(R.id.llScoresContainer);
                TextView tvPersonalityTitle = getView().findViewById(R.id.tvPersonalityTitle);
                TextView tvPersonalityType = getView().findViewById(R.id.tvPersonalityType);
                TextView tvPersonalityDescription = getView().findViewById(R.id.tvPersonalityDescription);
                ImageView ivPersonality = getView().findViewById(R.id.ivPersonality);

                tvPersonalityTitle.setText(currentUser.getPersonality().getTitle());
                tvPersonalityTitle.setTextColor(Color.parseColor(currentUser.getPersonality().getPrimary_color()));

                tvPersonalityType.setText(currentUser.getPersonality().getType());
                tvPersonalityType.setTextColor(Color.parseColor(currentUser.getPersonality().getSecondary_color()));

                tvPersonalityDescription.setText(currentUser.getPersonality().getDescription());

                Picasso.get().load(currentUser.getPersonality().getImg_url()).into(ivPersonality);

                fillScores(llScoresContainer, currentUser.getScores());
                fillOcean(currentUser.getOcean());
                break;

            //No results
            case NONE_TEST:
                //No test
                btnShowAd = getView().findViewById(R.id.button_get_ego);
                btnCreateTest = getView().findViewById(R.id.btnCreateTest);

                prepareBannerAd();
                btnCreateTest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createTest();
                    }
                });
                break;
            case NONE:
                //The test is exist
                tvRemainingRates = getView().findViewById(R.id.tvRatedResultPage);
                btnShowAd = getView().findViewById(R.id.button_get_ego);
                btnShareTestResult = getView().findViewById(R.id.btnShareTestResult);

                //Populate views
                remainingRates = 5 - currentUser.getRated();
                tvRemainingRates.setText(getString(R.string.remaining_credits, remainingRates));
                prepareBannerAd();
                btnShareTestResult.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sessionManager.getUser().hasTest()) {
                            shareTest();
                        } else {
                            Snackbar.make(swipeLayout, R.string.alert_no_test, BaseTransientBottomBar.LENGTH_LONG)
                                    .setActionTextColor(getResources().getColor(R.color.materialLightPurple))
                                    .show();
                        }
                    }
                });
                break;

        }
    }

    private void fillOcean(Ocean ocean) {
        SegoProgressBar segoProgressO = getView().findViewById(R.id.segoProgressO);
        SegoProgressBar segoProgressC = getView().findViewById(R.id.segoProgressC);
        SegoProgressBar segoProgressE = getView().findViewById(R.id.segoProgressE);
        SegoProgressBar segoProgressA = getView().findViewById(R.id.segoProgressA);
        SegoProgressBar segoProgressN = getView().findViewById(R.id.segoProgressN);

        setSegoProgress(segoProgressO, getLeftPercent(ocean.getO()));
        setSegoProgress(segoProgressC, getLeftPercent(ocean.getC()));
        setSegoProgress(segoProgressE, getLeftPercent(ocean.getE()));
        setSegoProgress(segoProgressA, getLeftPercent(ocean.getA()));
        setSegoProgress(segoProgressN, getLeftPercent(ocean.getN()));
    }

    private void setSegoProgress(SegoProgressBar segoProgress, int leftPercent) {
        segoProgress.setNewPercent(leftPercent, Math.abs(100 - leftPercent));
    }

    private int getLeftPercent(double number) {
        return (int) Math.round(((number + 1) / 2) * 100);
    }

    /**
     * It fills scores container
     *
     * @param llScoresContainer
     * @param scores
     */
    private void fillScores(LinearLayout llScoresContainer, ArrayList<Score> scores) {
        for (Score score : scores) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View score_row = inflater.inflate(R.layout.scores_list_row, null);

            ImageView traitImage = score_row.findViewById(R.id.traitEmojiView);
            TextView traitNameView = score_row.findViewById(R.id.traitNameView);
            FrameLayout traitEmojiContainer = score_row.findViewById(R.id.imageViewContainer);
            ConstraintLayout clTraitRow = score_row.findViewById(R.id.clTraitRow);

            if (score != null) {
                //Set name
                traitNameView.setText(score.getTraitName());
                //Load emoji
                Uri myUrl = Uri.parse(EMOJI_END_POINT + score.getEmojiName());
                GlideToVectorYou.justLoadImage((Activity) getContext(), myUrl, traitImage);
            }

            //Set emoji container background
            Random random = new Random(System.currentTimeMillis());
            // Not too dark or bright. Keep in an interval.
            int r = random.nextInt(120) + 60;
            int g = random.nextInt(120) + 60;
            int b = random.nextInt(120) + 60;
            Drawable shape = traitEmojiContainer.getBackground();
            shape.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.SRC_ATOP);
            clTraitRow.setVisibility(View.VISIBLE);

            llScoresContainer.addView(score_row);
        }
    }


    //Set up refresh receiver
    private void setupReceiver() {
        resultsRefreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status", 0);
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
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(resultsRefreshReceiver, new IntentFilter(ConstantValues.ACTION_REFRESH_RESULTS));
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(resultsRefreshReceiver);
        super.onDestroy();
    }

    /**
     * Initializes and loads rewarded video ad.
     */
    private void prepareRewardedAd() {
        final Context fragmentContext = getActivity();
        if (fragmentContext == null) return;
        RewardedAd.load(
            fragmentContext,
            getResources().getString(R.string.admob_ad_interface),
            new AdRequest.Builder().build(),
            new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.d("Reward Ad", "Ad failed to load. Try again");
                    rewardedAd = null;
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd ad) {
                    super.onAdLoaded(rewardedAd);
                    Log.d("Reward Ad", "Ad successfully loaded.");
                    rewardedAd = ad;
                }
            }
        );
    }

    /**
     * Shows rewarded Ad and sets RewardAd callback. Calls reward task user if the user earned.
     */
    private void showRewardedAd() {
        final String rewardCallbackTag = "RewardedAdCallback";
        final Activity fragmentContext = getActivity();
        if (fragmentContext == null) return;
        //Prepare rewarded ad callback
        rewardedAd.show(fragmentContext, new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                Log.d(rewardCallbackTag, "User earned reward.");
                EarnRewardTask.EarnRewardTask(getContext(), sessionManager.getSessionToken());
            }
        });
    }


    /**
     * Shows share test dialog
     */
    private void shareTest() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String testUrl = String.format(WEB_URL + "%s", sessionManager.getUser().getTestId());
        String shareBody = getString(R.string.body_share_test, testUrl);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.action_btn_share_test);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.action_btn_share_test)));
    }

    /**
     * Shows share result dialog
     */
    private void shareResults() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String testResultId = SessionManager.getInstance().getUser().getTestResultId();
        String testUrl = String.format(WEB_URL + "%s", sessionManager.getUser().getTestId());

        String shareBody = getString(R.string.body_share_results, WEB_URL + "result/" + testResultId + "\n", testUrl);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.action_btn_share_results);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.action_btn_share_results)));
    }

    /**
     * Opens create test page
     */
    private void createTest() {
        //Check internet connection
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            Intent addTestIntent = new Intent(getContext(), WebViewActivity.class);
            addTestIntent.putExtra("url", ConstantValues.CREATE_TEST);
            addTestIntent.putExtra("title", getString(R.string.activity_label_new_test));
            startActivity(addTestIntent);
        } else {
            Snackbar.make(swipeLayout, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();
            Log.d("CONNECTION", String.valueOf(isConnected));
        }
    }
}
