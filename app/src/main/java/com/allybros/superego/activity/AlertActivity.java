package com.allybros.superego.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.ComponentActivity;

import com.allybros.superego.R;
import com.allybros.superego.unit.Alert;
import com.allybros.superego.unit.AlertButton;
import com.allybros.superego.widget.SegoAlertButton;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.google.gson.Gson;

import java.util.List;

public class AlertActivity extends ComponentActivity {

    private ImageView alertDismissImageView;
    private ImageView alertImageView;
    private TextView alertTitleTextView;
    private TextView alertDescriptionTextView;
    private LinearLayout alertButtonsLinearLayout;

    private Alert alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        initializeComponents();
        // Set content
        this.alert = getAlertFromIntent();
        if (this.alert != null) {
            setContent(this.alert);
        } else {
            Log.e("AlertActivity", "Can not parse read alert data");
        }
        setupUi();
    }

    /**
     * Extracts alert object within the intent
     * @return Received alert object
     */
    private Alert getAlertFromIntent() {
        Intent i = getIntent();
        String alertJson = i.getStringExtra("alert");
        if (alertJson == null) {
            return null;
        }
        // Map json to alert object
        return new Gson().fromJson(alertJson, Alert.class);
    }

    /**
     * Initialize components
     */
    private void initializeComponents() {
        alertDismissImageView = findViewById(R.id.ivAlertDismissIcon);
        alertImageView = findViewById(R.id.ivAlertImage);
        alertTitleTextView = findViewById(R.id.tvAlertTitle);
        alertDescriptionTextView = findViewById(R.id.tvAlertDescription);
        alertButtonsLinearLayout = findViewById(R.id.llAlertButtons);
    }

    /**
     * Set UI functionality
     */
    private void setupUi(){
        alertDismissImageView.setOnClickListener(v -> finish());
    }

    @Override
    @Deprecated
    public void onBackPressed() {
        if (this.alert.getType().equals(Alert.TYPE_NON_DISMISSIBLE)) {
            Log.i("OnBackPressed", "Non dismissible alert blocked");
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Populate UI fields based on alert object
     * @param alert Alert to be rendered
     */
    private void setContent(Alert alert) {
        // Set dismiss icon
        if (alert.getType().equals(Alert.TYPE_NON_DISMISSIBLE)) {
            alertDismissImageView.setVisibility(View.GONE);
        }

        // Set alert image
        GlideToVectorYou.justLoadImage(this, Uri.parse(alert.getImageUrl()), alertImageView);

        // Set text content
        alertTitleTextView.setText(alert.getTitle());
        alertDescriptionTextView.setText(alert.getMessage());

        // Set buttons
        setButtons(alert.getButtons());

    }

    /**
     * Adds alert buttons to alert buttons layout
     * @param buttons list of alert buttons to be rendered
     */
    private void setButtons(List<AlertButton> buttons) {
        if (buttons == null) {
            // Nothing to render
            return;
        }
        for (AlertButton alertButton: buttons) {
            SegoAlertButton button = new SegoAlertButton(this);
            button.setText(alertButton.getText());
            button.setPrimary(alertButton.getType().equals(AlertButton.ALERT_BUTTON_TYPE_PRIMARY));
            button.setAction(this, alertButton.getAction());
            alertButtonsLinearLayout.addView(button);
        }
    }


}
