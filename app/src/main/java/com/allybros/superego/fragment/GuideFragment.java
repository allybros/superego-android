package com.allybros.superego.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.allybros.superego.R;

import static com.facebook.FacebookSdk.getApplicationContext;


public class GuideFragment extends Fragment {

    private int position;
    public GuideFragment(int position) {
        this.position = position;
    }

    public static GuideFragment newInstance(int position) {
        GuideFragment fragment = new GuideFragment(position);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);
        drawView(position, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void drawView(int position, View view){
        ImageView imageViewGuide = view.findViewById(R.id.imageViewGuide);
        TextView tvGuideHeader = view.findViewById(R.id.tvGuideHeader);
        TextView tvGuideContent = view.findViewById(R.id.tvGuideContent);
        switch (position){
            case 0:
                //Sets ImageView
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageViewGuide.setImageDrawable(getResources().getDrawable(R.drawable.ic_guide_1, getApplicationContext().getTheme()));
                } else {
                    imageViewGuide.setImageDrawable(getResources().getDrawable(R.drawable.ic_guide_1));
                }
                //Sets texts
                tvGuideHeader.setText(R.string.guide_header_1);
                tvGuideContent.setText(R.string.guide_content_1);
                break;
            case 1:
                //Sets ImageView
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageViewGuide.setImageDrawable(getResources().getDrawable(R.drawable.ic_guide_2, getApplicationContext().getTheme()));
                } else {
                    imageViewGuide.setImageDrawable(getResources().getDrawable(R.drawable.ic_guide_2));
                }
                //Sets texts
                tvGuideHeader.setText(R.string.guide_header_2);
                tvGuideContent.setText(R.string.guide_content_2);
                break;
            case 2:
                //Sets ImageView
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageViewGuide.setImageDrawable(getResources().getDrawable(R.drawable.ic_guide_3, getApplicationContext().getTheme()));
                } else {
                    imageViewGuide.setImageDrawable(getResources().getDrawable(R.drawable.ic_guide_3));
                }
                //Sets texts
                tvGuideHeader.setText(R.string.guide_header_3);
                tvGuideContent.setText(R.string.guide_content_3);
                break;
        }

    }
}