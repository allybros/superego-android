package com.allybros.superego.fragment;

import static com.allybros.superego.unit.ConstantValues.EMOJI_END_POINT;
import static com.allybros.superego.unit.ConstantValues.WEB_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.allybros.superego.R;
import com.allybros.superego.activity.LoginActivity;
import com.allybros.superego.activity.UserPageActivity;
import com.allybros.superego.activity.WebViewActivity;
import com.allybros.superego.api.ApiTask;
import com.allybros.superego.api.EarnRewardTask;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.api.mapper.UserMapper;
import com.allybros.superego.api.response.ApiStatusResponse;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.Ocean;
import com.allybros.superego.unit.TraitScore;
import com.allybros.superego.unit.User;
import com.allybros.superego.util.SessionManager;
import com.allybros.superego.widget.SegoProgressBar;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Random;

public class ResultsFragment extends Fragment {
    private TextView tvRemainingRates;
    private SwipeRefreshLayout swipeLayout;
    private AdView adResultBanner;
    private Button btnShowAd, btnShareTestResult, btnCreateTest;
    private RewardedAd rewardedAd;
    private LinearLayout llScoresContainer;
    private Activity parentActivity;

    //3 states of Result screen represented in an Enum
    private enum State {
        NONE, NONE_TEST, PARTIAL, COMPLETE
    }

    public ResultsFragment(Activity parentActivity) {
        this.parentActivity = parentActivity;
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
        setupView();
    }

    private State getState() {
        if (SessionManager.getInstance().getUser().getScores().size() >= 6)
            return State.COMPLETE;
        else if (!SessionManager.getInstance().getUser().getScores().isEmpty())
            return State.PARTIAL;
        else if (SessionManager.getInstance().getUser().getTestId() != null && !SessionManager.getInstance().getUser().getTestId().isEmpty()) {
            return State.NONE;
        } else {
            return State.NONE_TEST;
        }

    }

    private void prepareBannerAd() {
        // Initialize mobile ads
        MobileAds.initialize(parentActivity, initializationStatus -> Log.d("MobileAds", "Initialized."));
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
            swipeLayout.setOnRefreshListener(() -> {
                // Check internet connection
                ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    reloadProfile();
                } else {
                    Snackbar.make(swipeLayout, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();
                    Log.d("CONNECTION", String.valueOf(isConnected));
                    swipeLayout.setRefreshing(false);
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
                tvRemainingRates.setText(getString(R.string.remaining_credits, SessionManager.getInstance().getUser().getRemainingRates()));

                fillScores(llScoresContainer, SessionManager.getInstance().getUser().getScores());
                prepareRewardedAd();
                prepareBannerAd();
                btnShowAd.setOnClickListener(v -> {
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
                        new AlertDialog.Builder(parentActivity, R.style.SegoAlertDialog)
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
                });
                break;

            //All results
            case COMPLETE:
                llScoresContainer = getView().findViewById(R.id.llScoresContainer);
                TextView tvPersonalityTitle = getView().findViewById(R.id.tvPersonalityTitle);
                TextView tvPersonalityType = getView().findViewById(R.id.tvPersonalityType);
                TextView tvPersonalityDescription = getView().findViewById(R.id.tvPersonalityDescription);
                ImageView ivPersonality = getView().findViewById(R.id.ivPersonality);
                ImageView ivShareResults = getView().findViewById(R.id.ivShareResults);

                tvPersonalityTitle.setText(SessionManager.getInstance().getUser().getPersonality().getTitle());
                tvPersonalityTitle.setTextColor(Color.parseColor(SessionManager.getInstance().getUser().getPersonality().getPrimary_color()));

                tvPersonalityType.setText(SessionManager.getInstance().getUser().getPersonality().getType());
                tvPersonalityType.setTextColor(Color.parseColor(SessionManager.getInstance().getUser().getPersonality().getSecondary_color()));

                tvPersonalityDescription.setText(SessionManager.getInstance().getUser().getPersonality().getDescription());

                GlideToVectorYou.justLoadImage((Activity) getContext(), Uri.parse(SessionManager.getInstance().getUser().getPersonality().getImg_url()), ivPersonality);

                fillScores(llScoresContainer, SessionManager.getInstance().getUser().getScores());
                fillOcean(SessionManager.getInstance().getUser().getOcean());
                ivShareResults.setOnClickListener(v -> shareResults());

                break;

            //No results
            case NONE_TEST:
                //No test
                btnShowAd = getView().findViewById(R.id.button_get_ego);
                btnCreateTest = getView().findViewById(R.id.btnCreateTest);

                prepareBannerAd();
                btnCreateTest.setOnClickListener(v -> createTest());
                break;
            case NONE:
                //The test is exist
                tvRemainingRates = getView().findViewById(R.id.tvRatedResultPage);
                btnShowAd = getView().findViewById(R.id.button_get_ego);
                btnShareTestResult = getView().findViewById(R.id.btnShareTestResult);

                //Populate views

                tvRemainingRates.setText(getString(R.string.remaining_credits, SessionManager.getInstance().getUser().getRemainingRates()));
                prepareBannerAd();
                btnShareTestResult.setOnClickListener(v -> {
                    if (SessionManager.getInstance().getUser().hasTest()) {
                        shareTest();
                    } else {
                        Snackbar.make(swipeLayout, R.string.alert_no_test, BaseTransientBottomBar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.materialLightPurple))
                                .show();
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
     * @param traitScores
     */
    private void fillScores(LinearLayout llScoresContainer, List<TraitScore> traitScores) {
        for (TraitScore traitScore : traitScores) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View score_row = inflater.inflate(R.layout.scores_list_row, null);

            ImageView traitImage = score_row.findViewById(R.id.traitEmojiView);
            TextView traitNameView = score_row.findViewById(R.id.traitNameView);
            FrameLayout traitEmojiContainer = score_row.findViewById(R.id.imageViewContainer);
            ConstraintLayout clTraitRow = score_row.findViewById(R.id.clTraitRow);

            if (traitScore != null) {
                //Set name
                traitNameView.setText(traitScore.getName());
                //Load emoji
                Uri myUrl = Uri.parse(EMOJI_END_POINT + traitScore.getIcon());
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

    /**
     * Reloads profile content with API.
     */
    private void reloadProfile() {
        // Call API task
        String sessionToken = SessionManager.getInstance().getSessionToken();
        LoadProfileTask loadProfileTask = new LoadProfileTask(sessionToken);
        loadProfileTask.setOnResponseListener(response -> {
            swipeLayout.setRefreshing(false);
            if (response.getStatus() == ErrorCodes.SUCCESS) {
                // Successfully response
                User sessionUser = UserMapper.fromProfileResponse(response);
                SessionManager.getInstance().setUser(sessionUser);
                UserPageActivity pageActivity = (UserPageActivity) parentActivity;
                if (pageActivity != null) pageActivity.refreshFragments(1);
            } else {
                // An error occurred
                Toast.makeText(getContext(), getString(R.string.error_check_connection), Toast.LENGTH_SHORT)
                        .show();
            }
        });
        loadProfileTask.execute(getContext());
    }


    /**
     * Initializes and loads rewarded video ad.
     */
    private void prepareRewardedAd() {
        final Context fragmentContext = parentActivity;
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
        final Activity fragmentContext = parentActivity;
        if (fragmentContext == null) return;
        //Prepare rewarded ad callback
        rewardedAd.show(fragmentContext, rewardItem -> {
            Log.d(rewardCallbackTag, "User earned reward.");
            acceptReward();
        });
    }

    /**
     * Sends a request for accepting reward
     */
    private void acceptReward() {
        EarnRewardTask earnRewardTask = new EarnRewardTask(SessionManager.getInstance().getSessionToken());
        earnRewardTask.setOnResponseListener(this::handleEarnRewardResponse);
        earnRewardTask.execute(getContext());
    }

    /**
     * Handles reward response
     *
     * @param response
     */
    private void handleEarnRewardResponse(ApiStatusResponse response) {
        switch (response.getStatus()) {
            case ErrorCodes.SYSFAIL:
                AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity, R.style.SegoAlertDialog);
                builder.setTitle(getString(R.string.app_name));
                builder.setMessage(R.string.error_no_connection);
                builder.setPositiveButton(getString(R.string.action_ok), (dialog, id) -> {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
                builder.show();
                break;

            case ErrorCodes.SUCCESS:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(parentActivity, R.style.SegoAlertDialog);
                builder1.setTitle(getString(R.string.app_name));
                builder1.setMessage(R.string.message_earn_reward_succeed);
                builder1.setPositiveButton(getString(R.string.action_ok), (dialog, id) -> reloadProfile());
                builder1.show();
                break;

            case ErrorCodes.SESSION_EXPIRED:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(parentActivity, R.style.SegoAlertDialog);
                builder2.setTitle(getString(R.string.app_name));
                builder2.setMessage(R.string.error_session_expired);
                builder2.setPositiveButton(getString(R.string.action_ok), (dialog, id) -> {
                });
                builder2.show();
                break;
        }
    }


    /**
     * Shows share test dialog
     */
    private void shareTest() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String testUrl = String.format(WEB_URL + "%s", SessionManager.getInstance().getUser().getTestId());
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
        String testUrl = String.format(WEB_URL + "%s", SessionManager.getInstance().getUser().getTestId());

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
            ConstantValues constantValues = new ConstantValues();
            String createTestUrl = constantValues.getWebUrl(ConstantValues.CREATE_TEST);
            Intent addTestIntent = new Intent(getContext(), WebViewActivity.class);
            addTestIntent.putExtra("url", createTestUrl);
            addTestIntent.putExtra("title", getString(R.string.activity_label_new_test));
            startActivity(addTestIntent);
        } else {
            Snackbar.make(swipeLayout, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();
            Log.d("CONNECTION", String.valueOf(isConnected));
        }
    }
}
