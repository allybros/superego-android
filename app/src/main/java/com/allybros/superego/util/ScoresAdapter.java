package com.allybros.superego.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.allybros.superego.R;
import com.allybros.superego.unit.Score;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import java.util.ArrayList;
import java.util.Random;

public class ScoresAdapter extends ArrayAdapter<Score> {
    //CDN URL for emojis
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
        FrameLayout traitEmojiContainer = convertView.findViewById(R.id.imageViewContainer);

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

        return convertView;
    }
}
