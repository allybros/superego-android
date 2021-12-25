package com.allybros.superego.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.allybros.superego.R;
import com.allybros.superego.widget.SegoProgressBar;

public class PlayGroundActivity extends AppCompatActivity {
    SegoProgressBar segoProgress;
    EditText etStartPercent;
    EditText etEndPercent;
    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_ground);
        segoProgress = findViewById(R.id.segoProgress);
        bt = findViewById(R.id.bt);
        etEndPercent = findViewById(R.id.etEndPercent);
        etStartPercent = findViewById(R.id.etStartPercent);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startPercent = etStartPercent.getText().toString();
                String endPercent = etEndPercent.getText().toString();
                int intStartPercent;
                int intEndPercent;
                try {
                    intStartPercent = Integer.parseInt(startPercent);
                    intEndPercent = Integer.parseInt(endPercent);
                } catch (Exception e){
                    intStartPercent = 1;
                    intEndPercent = 99;
                }
                segoProgress.setNewPercent(intStartPercent, intEndPercent);
            }
        });

    }
}