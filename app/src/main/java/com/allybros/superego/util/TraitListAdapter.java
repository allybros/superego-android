package com.allybros.superego.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allybros.superego.R;
import com.allybros.superego.activity.SplashActivity;
import com.allybros.superego.fragments.ResultsFragment;
import com.allybros.superego.unit.Trait;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class TraitListAdapter extends ArrayAdapter<Trait> {

    public TraitListAdapter(Context context, ArrayList<Trait> traits) {
        super(context, R.layout.trait_list_row,traits);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater Inflater=LayoutInflater.from(getContext());
        View customView=Inflater.inflate(R.layout.trait_list_row,parent,false);
        final View anaEkran = Inflater.inflate(R.layout.fragment_results,parent,false);
        Trait tmp=getItem(position);


        final ImageView imgHaber=(ImageView) customView.findViewById(R.id.traitImage);
        TextView tvTitle=(TextView) customView.findViewById(R.id.traitName);


        if (tmp.getValue()>=0){
            tvTitle.setText(SplashActivity.allTraits.get(tmp.getTraitNo()).getPositiveName());

            Uri myUrl = Uri.parse(SplashActivity.allTraits.get(tmp.getTraitNo()).getPositiveIconURL());

            /*
            Glide.with(customView.getContext()).load(SplashActivity.allTraits.get(tmp.getTraitNo()).getPositiveIconURL())
                .placeholder(R.drawable.umut_reis)
                .error(R.drawable.umut_reis)
                .into(imgHaber);*/
            GlideToVectorYou.justLoadImage((Activity) getContext(), myUrl , imgHaber);

        }else {
            tvTitle.setText(SplashActivity.allTraits.get(tmp.getTraitNo()).getNegativeName());

       /*     Glide.with(customView.getContext()).load(SplashActivity.allTraits.get(tmp.getTraitNo()).getNegativeIconURL())
                .placeholder(R.drawable.umut_reis)
                .error(R.drawable.umut_reis)
                .into(imgHaber);*/

        }
        return customView;

    }
}
