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

import com.allybros.superego.R;
import com.allybros.superego.activity.SplashActivity;
import com.allybros.superego.unit.Trait;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Random;

public class TraitListAdapter extends ArrayAdapter<Trait> {

    public TraitListAdapter(Context context, ArrayList<Trait> traits) {
        super(context, R.layout.trait_list_row,traits);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Random random = new Random();

        LayoutInflater Inflater=LayoutInflater.from(getContext());
        View customView=Inflater.inflate(R.layout.trait_list_row,parent,false);
        final View fragmentResult = Inflater.inflate(R.layout.fragment_results,parent,false);
        Trait tmp=getItem(position);


        final ImageView traitImage=(ImageView) customView.findViewById(R.id.traitImage);
        MaterialButton traitCardView=customView.findViewById(R.id.trait_card_view);


        if (tmp.getValue()>=0){
            traitCardView.setText(SplashActivity.allTraits.get(tmp.getTraitNo()).getPositiveName());

            Uri myUrl = Uri.parse(SplashActivity.allTraits.get(tmp.getTraitNo()).getPositiveIconURL());

            /*
            Glide.with(customView.getContext()).load(SplashActivity.allTraits.get(tmp.getTraitNo()).getPositiveIconURL())
                .placeholder(R.drawable.umut_reis)
                .error(R.drawable.umut_reis)
                .into(traitImage);*/
            GlideToVectorYou.justLoadImage((Activity) getContext(), myUrl , traitImage);

        }else {
            try{
                traitCardView.setText(SplashActivity.allTraits.get(tmp.getTraitNo()).getNegativeName());
            } catch (Exception e){
                Log.d("sego-Exeption",e.toString());
            }

       /*     Glide.with(customView.getContext()).load(SplashActivity.allTraits.get(tmp.getTraitNo()).getNegativeIconURL())
                .placeholder(R.drawable.umut_reis)
                .error(R.drawable.umut_reis)
                .into(traitImage);*/

        }

        int colorImageWindow= Color.argb(255,random.nextInt(256), random.nextInt(256), random.nextInt(256));
        return customView;

    }
}
