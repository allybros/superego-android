package com.allybros.superego.widget;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.allybros.superego.R;

public class SegoAlertButton extends LinearLayout {

    private final Button segoBtnPrimary;
    private final Button segoBtnSecondary;

    public SegoAlertButton(@NonNull Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.widget_sego_alert_button, this, true);
        segoBtnPrimary = findViewById(R.id.btSegoPrimary);
        segoBtnSecondary = findViewById(R.id.btSegoSecondary);
        setPrimary(false);
    }

    public void setText(String text) {
        this.segoBtnPrimary.setText(text);
        this.segoBtnSecondary.setText(text);
    }

    public void setPrimary(boolean primary) {
        if (primary) {
            segoBtnSecondary.setVisibility(GONE);
            segoBtnPrimary.setVisibility(VISIBLE);
        }
        else {
            segoBtnPrimary.setVisibility(GONE);
            segoBtnSecondary.setVisibility(VISIBLE);
        }
    }

    public void setAction(Activity activity, String action) {
        OnClickListener onClickListener = v -> {
            Log.d("SegoAlertButton", "Performing action " + action);
            // Handle the action
            if (action.equals("superego://alert/exit")) {
                // Exit the app
                System.exit(0);
            } else if (action.equals("superego://alert/dismiss")) {
                activity.finish();
            } else {
                // Open url
                try {
                    Uri uriUrl = Uri.parse(action);
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    activity.startActivity(launchBrowser);
                } catch (ActivityNotFoundException exception) {
                    Log.e("ActivityIntent", "Activity not found");
                }

            }
        };
        segoBtnSecondary.setOnClickListener(onClickListener);
        segoBtnPrimary.setOnClickListener(onClickListener);

    }
}
