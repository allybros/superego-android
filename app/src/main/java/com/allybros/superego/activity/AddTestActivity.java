package com.allybros.superego.activity;



import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.allybros.superego.R;

public class AddTestActivity extends AppCompatActivity {
//TODO:Sayfa yalnızca oluşturuldu. Bir ara yapılır inş
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AddTestActivity.this,UserPageActivity.class);
        startActivity(intent);
        finish();
    }
}
