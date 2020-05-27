package com.allybros.superego.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.allybros.superego.R;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.User;
import com.allybros.superego.util.HelperMethods;

import java.util.ArrayList;

public class SearchAdapter extends ArrayAdapter<User> {

    public SearchAdapter(@NonNull Context context, ArrayList<User> users) {
        super(context, R.layout.search_user_row, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.search_user_row, parent,false);
        }

        final User u = getItem(position);
        if (u == null) return convertView;

        CircledNetworkImageView ivSearchUserAvatar = convertView.findViewById(R.id.nivSearchUserAvatar);
        TextView tvSearchUsername = convertView.findViewById(R.id.tvSearchUsername);
        TextView tvSearchUserbio = convertView.findViewById(R.id.tvSearchUserBio);

        String bioSum = u.getUserBio();
        if (u.getUserBio() == null) {
            bioSum = convertView.getContext().getString(R.string.default_bio);
        }
        else if (u.getUserBio().length() >= 75){
            bioSum = u.getUserBio().substring(0,75);
            bioSum += "...";
        }

        tvSearchUsername.setText(u.getUsername());
        tvSearchUserbio.setText(bioSum);
        HelperMethods.imageLoadFromUrlCache(getContext(), ConstantValues.AVATAR_URL+u.getAvatarName(), ivSearchUserAvatar);

        return convertView;
    }


}
