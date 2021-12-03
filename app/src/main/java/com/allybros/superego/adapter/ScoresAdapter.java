package com.allybros.superego.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.allybros.superego.R;
import com.allybros.superego.unit.Score;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.Random;

public class ScoresAdapter extends ArrayAdapter<Score> {
    //CDN URL for emojis
    private static final String EMOJI_END_POINT = "https://api.allybros.com/twemoji/?name=";
    private ArrayList<Score> scores;
    private AdView adResultBanner;
    private Boolean isContainTitle;
    private View.OnClickListener shareListener;

    public ScoresAdapter(Context context, ArrayList<Score> scores, Boolean isContainTitle, @Nullable View.OnClickListener shareListener) {
        super(context, R.layout.scores_list_row, scores);
        this.scores = scores;
        this.isContainTitle = isContainTitle;
        if(shareListener != null) this.shareListener = shareListener;
        scores.add(new Score(-2016, 0));    //It provide to adding ad row

        if(this.isContainTitle) scores.add(0, new Score(-2016, 0));    //It provide to adding title row
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            if (position == scores.size() - 1) {
                convertView = inflater.inflate(R.layout.list_ad_row, parent,false);
                adResultBanner = convertView.findViewById(R.id.resultBannerAdd);
                prepareBannerAd();
                return convertView;
            }
            else{
                convertView = inflater.inflate(R.layout.scores_list_row, parent,false);
                if(isContainTitle && position == 0) {
                    ConstraintLayout llTraitTitle = convertView.findViewById(R.id.clTraitTitle);
                    final ImageView ivShareResults = convertView.findViewById(R.id.ivShareResults);
                    llTraitTitle.setVisibility(View.VISIBLE);
                    ivShareResults.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareListener.onClick(ivShareResults);
                        }
                    });

                    return convertView;
                }
            }
        }

        ImageView traitImage = convertView.findViewById(R.id.traitEmojiView);
        TextView traitNameView = convertView.findViewById(R.id.traitNameView);
        FrameLayout traitEmojiContainer = convertView.findViewById(R.id.imageViewContainer);
        ConstraintLayout clTraitRow = convertView.findViewById(R.id.clTraitRow);
        if (traitImage == null || traitNameView == null || traitEmojiContainer == null) return new View(getContext());

        Score s = getItem(position);

        if (s != null){
            //Set name
            traitNameView.setText(s.getTraitName());
            //Load emoji
            Uri myUrl = Uri.parse(EMOJI_END_POINT + s.getEmojiName());
            GlideToVectorYou.justLoadImage((Activity) getContext(), myUrl , traitImage);
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
        return convertView;
    }

    private void prepareBannerAd(){
        // Initialize mobile ads
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.d("MobileAds", "Initialized.");
            }
        });
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

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
