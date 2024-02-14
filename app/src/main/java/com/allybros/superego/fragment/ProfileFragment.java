package com.allybros.superego.fragment;


import static com.allybros.superego.unit.ConstantValues.WEB_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.allybros.superego.R;
import com.allybros.superego.activity.SettingsActivity;
import com.allybros.superego.activity.UserPageActivity;
import com.allybros.superego.activity.WebViewActivity;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.api.mapper.UserMapper;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.User;
import com.allybros.superego.util.SessionManager;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvUserbio, badgeCredit, badgeRated, tvPersonalitySectionTitle, tvPersonalityCardTitle, tvPersonalityCardShortName, tvPersonalityCardDescription;
    private Button btnShareTest, btnShareResults, btnInfoShare, btnNewTest;
    private CardView clPersonalityCard, clShareTest;
    private LinearLayout llCreateTest;
    private ImageView btnSettings, ivPersonalityCard;
    private CircleImageView imageViewAvatar;
    private SwipeRefreshLayout profileSwipeLayout;
    private AdView adProfileBanner;
    boolean isTestCreated = false;
    //Current session
    private SessionManager sessionManager = SessionManager.getInstance();

    private Activity parentActivity;

    public ProfileFragment(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initProfileCard();
        initButtons();
        initShareTest();
        prepareBannerAd();
        initSwipeLayout();
        setupInfoCards();
    }

    private void setupInfoCards() {
        clPersonalityCard = getView().findViewById(R.id.clPersonalityCard);
        tvPersonalitySectionTitle = getView().findViewById(R.id.tvPersonalitySectionTitle);
        clShareTest = getView().findViewById(R.id.clShareTest);
        llCreateTest = getView().findViewById(R.id.llCreateTest);
        btnNewTest = getView().findViewById(R.id.btnCreateTest);

        ivPersonalityCard = getView().findViewById(R.id.ivPersonalityCard);
        tvPersonalityCardTitle = getView().findViewById(R.id.tvPersonalityCardTitle);
        tvPersonalityCardShortName = getView().findViewById(R.id.tvPersonalityCardShortName);
        tvPersonalityCardDescription = getView().findViewById(R.id.tvPersonalityCardDescription);

        btnNewTest.setOnClickListener(v -> {
            // Check internet connection
            ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                launchCreateTest();
            } else {
                Snackbar.make(profileSwipeLayout, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();
                Log.d("CONNECTION", String.valueOf(isConnected));
            }
        });

        if (sessionManager.getUser().getTestId() != null && !sessionManager.getUser().getTestId().isEmpty()) {
            if (sessionManager.getUser().getOcean() == null) {
                tvPersonalitySectionTitle.setVisibility(View.VISIBLE);
                clShareTest.setVisibility(View.VISIBLE);
                clPersonalityCard.setVisibility(View.GONE);
                llCreateTest.setVisibility(View.VISIBLE);
            } else {
                tvPersonalitySectionTitle.setVisibility(View.VISIBLE);
                clShareTest.setVisibility(View.GONE);
                clPersonalityCard.setVisibility(View.VISIBLE);
                llCreateTest.setVisibility(View.VISIBLE);
                setupPersonalityCard();
            }
        } else {
            tvPersonalitySectionTitle.setVisibility(View.GONE);
            clShareTest.setVisibility(View.GONE);
            clPersonalityCard.setVisibility(View.GONE);
            llCreateTest.setVisibility(View.VISIBLE);
        }
    }

    private void setupPersonalityCard() {
        GlideToVectorYou.justLoadImage((Activity) getContext(), Uri.parse(sessionManager.getUser().getPersonality().getImg_url()), ivPersonalityCard);
        tvPersonalityCardTitle.setText(sessionManager.getUser().getPersonality().getTitle());
        tvPersonalityCardShortName.setText(sessionManager.getUser().getPersonality().getType());
        tvPersonalityCardDescription.setText(sessionManager.getUser().getPersonality().getDescription());

        tvPersonalityCardTitle.setTextColor(Color.parseColor(sessionManager.getUser().getPersonality().getPrimary_color()));
        tvPersonalityCardShortName.setTextColor(Color.parseColor(sessionManager.getUser().getPersonality().getSecondary_color()));
    }

    /**
     * Reloads profile content with API.
     */
    public void reloadProfile() {
        // Call API task
        isTestCreated = true;
        String sessionToken = SessionManager.getInstance().getSessionToken();
        LoadProfileTask loadProfileTask = new LoadProfileTask(sessionToken);
        loadProfileTask.setOnResponseListener(response -> {
            if (response.getStatus() == ErrorCodes.SUCCESS) {
                // Successfully response
                User sessionUser = UserMapper.fromProfileResponse(response);
                SessionManager.getInstance().setUser(sessionUser);
                UserPageActivity pageActivity = (UserPageActivity) parentActivity;
                if (pageActivity != null) pageActivity.refreshFragments(0);
            } else {
                // An error occurred
                Toast.makeText(getContext(), getString(R.string.error_check_connection), Toast.LENGTH_SHORT).show();
            }
        });
        loadProfileTask.execute(getContext());
    }

    @SuppressLint("DefaultLocale")
    private void initProfileCard() {
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

        tvUsername.setText("@" + sessionManager.getUser().getUsername());
        badgeCredit.setText(String.valueOf(sessionManager.getUser().getCredit()));
        badgeRated.setText(String.valueOf(sessionManager.getUser().getRated()));


        Picasso.get().load(sessionManager.getUser().getImage()).error(R.drawable.default_avatar).into(imageViewAvatar);
    }

    /**
     * Set up toolbar button actions
     */
    private void initButtons() {
        //Initialize toolbar buttons
        btnShareTest = getView().findViewById(R.id.btnShareTest);
        btnShareResults = getView().findViewById(R.id.btnShareResult);
        btnSettings = getView().findViewById(R.id.btnSettings);

        if (!sessionManager.getUser().hasResults()) {
            btnShareResults.setAlpha(0.6f);
        }

        btnShareTest.setOnClickListener(v -> {
            if (sessionManager.getUser().hasTest()) {
                shareTest();
            } else {
                Snackbar.make(profileSwipeLayout, R.string.alert_no_test, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.action_btn_new_test, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnNewTest.performClick();
                    }
                }).setActionTextColor(getResources().getColor(R.color.materialLightPurple)).show();
                Snackbar.make(profileSwipeLayout, R.string.alert_no_test, BaseTransientBottomBar.LENGTH_LONG).show();

            }
        });

        btnShareResults.setOnClickListener(v -> {
            if (sessionManager.getUser().hasResults()) {
                shareResults();
            } else {
                Snackbar.make(profileSwipeLayout, R.string.alert_no_results, BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Set up profile info card
     */
    private void initShareTest() {
        btnInfoShare = getView().findViewById(R.id.btnInfoShare);

        btnInfoShare.setOnClickListener(v -> shareTest());
    }

    /**
     * Initializes, configure refresh layout.
     */
    private void initSwipeLayout() {
        //Setup refresh layout
        profileSwipeLayout = getView().findViewById(R.id.profileSwipeLayout);
        profileSwipeLayout.setOnRefreshListener(() -> {
            // Check internet connection
            ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                reloadProfile();
            } else {
                Log.d("CONNECTION", String.valueOf(isConnected));
                Snackbar.make(profileSwipeLayout, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();
                profileSwipeLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Initializes, loads and shows profile banner ad.
     */
    private void prepareBannerAd() {
        // Initialize mobile ads
        MobileAds.initialize(getActivity(), initializationStatus -> Log.d("MobileAds", "Initialized."));
        adProfileBanner = getView().findViewById(R.id.profileBannerAdd);
        final String adTag = "ad_profile_banner";
        // Load ad
        AdRequest adRequest = new AdRequest.Builder().build();
        adProfileBanner.loadAd(adRequest);
        // Set ad listener
        adProfileBanner.setAdListener(new AdListener() {
        });
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
     * Navigates to create test page
     */
    private void navigateToCreateTest() {
        if (!sessionManager.getUser().hasTest() && !isTestCreated) {
            btnShareTest.setAlpha(0.6f);
            launchCreateTest();
        }
    }

    private void launchCreateTest() {
        ConstantValues constantValues = new ConstantValues();
        String createTestUrl = constantValues.getWebUrl(ConstantValues.CREATE_TEST);
        Intent intent = new Intent(getContext(), WebViewActivity.class);
        intent.putExtra("url", createTestUrl);
        intent.putExtra("title", getString(R.string.activity_label_new_test));
        intent.putExtra("action", "new-test");
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sessionManager.getUser().getAvatar() != null)
            imageViewAvatar.setImageBitmap(sessionManager.getUser().getAvatar());
        navigateToCreateTest();
    }

}