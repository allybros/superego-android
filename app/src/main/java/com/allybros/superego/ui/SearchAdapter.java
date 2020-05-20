package com.allybros.superego.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.allybros.superego.R;
import com.allybros.superego.activity.WebViewActivity;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.User;
import com.allybros.superego.util.HelperMethods;

import java.util.ArrayList;

public class SearchAdapter extends ArrayAdapter<User> {

    public SearchAdapter(@NonNull Context context, ArrayList<User> users) {
        super(context, R.layout.search_user_row, users);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        if (u.getUserBio().length() >= 75){
            bioSum = u.getUserBio().substring(0,75);
            bioSum += "...";
        }

        tvSearchUsername.setText(u.getUsername());
        tvSearchUserbio.setText(bioSum);
        HelperMethods.imageLoadFromUrl(getContext(), ConstantValues.AVATAR_URL+u.getAvatarName(), ivSearchUserAvatar);
        // Set web activity title
        String belirtmeHali = ""+turkceBelirtmeHaliBulucu(u.getUsername());
        final String webActivityTitle = getContext().getString(R.string.title_activity_rate_user, u.getUsername(), belirtmeHali);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String testUrl = ConstantValues.RATE_URL + u.getTestId();
                Intent i = new Intent(getContext(), WebViewActivity.class);
                i.putExtra("url", testUrl);
                i.putExtra("title", webActivityTitle);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(i);
            }
        });

        return convertView;
    }

    /**
     * Builds title for specifically Turkish language structure
     * İsmin belirtme haline uygun bir başlık oluşturur. (Orçun'u, Umut'u)
     * Türkçe de pek yakıştı değil mi?
     */
    private String turkceBelirtmeHaliBulucu(String isim){
        char[] unluler = "aeıioöuü".toCharArray();

        for (int i = isim.length()-1; i >= 0; i--) {
            for (char c : unluler) {
                if (isim.charAt(i) == c){
                    switch (c) {
                        case 'a':
                        case 'ı':
                            return "ı";
                        case 'e':
                        case 'i':
                            return "i";
                        case 'o':
                        case 'u':
                            return "u";
                        case 'ö':
                        case 'ü':
                            return "ü";
                    }
                }
            }
        }
        return "i";
    }
}
