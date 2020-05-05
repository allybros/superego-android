package com.allybros.superego.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allybros.superego.R;
import com.allybros.superego.activity.SplashActivity;
import com.allybros.superego.unit.Score;
import com.allybros.superego.unit.Trait;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Random;
import java.util.zip.Inflater;

public class ScoresAdapter extends ArrayAdapter<Score> {

    private static final String EMOJI_END_POINT = "https://api.allybros.com/twemoji/?name=";
    public ScoresAdapter(Context context, ArrayList<Score> scores) {
        super(context, R.layout.scores_list_row, scores);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.scores_list_row, parent,false);
        }

        ImageView traitImage = convertView.findViewById(R.id.traitEmojiView);
        TextView traitNameView = convertView.findViewById(R.id.traitNameView);

        Score s = getItem(position);
        if (s != null)
            traitNameView.setText(s.getTraitName());
        //Load emoji
        Uri myUrl = Uri.parse(EMOJI_END_POINT + s.getEmojiName());
        GlideToVectorYou.justLoadImage((Activity) getContext(), myUrl , traitImage);
        Random random = new Random();
        int colorImageWindow= Color.argb(255,random.nextInt(256), random.nextInt(256), random.nextInt(256));
        return convertView;
    }
}
