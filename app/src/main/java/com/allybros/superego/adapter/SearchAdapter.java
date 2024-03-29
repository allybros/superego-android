package com.allybros.superego.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.allybros.superego.R;
import com.allybros.superego.api.response.SearchResponse;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends ArrayAdapter<SearchResponse> {

    public SearchAdapter(@NonNull Context context, SearchResponse[] users) {
        super(context, R.layout.search_user_row, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.search_user_row, parent, false);
        }

        final SearchResponse u = getItem(position);
        if (u == null) return convertView;

        CircleImageView ivSearchUserAvatar = convertView.findViewById(R.id.nivSearchUserAvatar);
        TextView tvSearchUsername = convertView.findViewById(R.id.tvSearchUsername);
        TextView tvSearchUserbio = convertView.findViewById(R.id.tvSearchUserBio);

        String bioSum = u.getUserBio();
        if (u.getUserBio() == null) {
            bioSum = convertView.getContext().getString(R.string.default_bio_search);
        } else if (u.getUserBio().length() >= 30) {
            bioSum = u.getUserBio().substring(0, 30);
            bioSum += "...";
        }

        tvSearchUsername.setText(u.getUsername());
        tvSearchUserbio.setText(bioSum);
        Picasso.get().load(u.getAvatar())
                .error(R.drawable.default_avatar)
                .into(ivSearchUserAvatar);

        return convertView;
    }
}
