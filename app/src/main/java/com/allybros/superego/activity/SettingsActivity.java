package com.allybros.superego.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.allybros.superego.R;
import com.allybros.superego.api.ChangeInfoTask;
import com.allybros.superego.api.LogoutTask;
import com.allybros.superego.unit.User;
import com.allybros.superego.util.CircledNetworkImageView;
import com.allybros.superego.util.CustomVolleyRequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

public class SettingsActivity extends AppCompatActivity {
    //TODO:USER_INFORMATION_PREF variable must move in unit/api
    final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";
    TextInputEditText username,email,information;
    TextInputLayout etUsername_text_input,etEmail_text_input,etInformation_text_input;
    CircledNetworkImageView image;
    MaterialButton btChangePhoto, btLogout;   //TODO: Fotoğraf değiştirme işlemi yapılacak.
    private ImageLoader mImageLoader;
    private SlidrInterface slidr;
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = getSupportActionBar();

        image=(CircledNetworkImageView) findViewById(R.id.imageSettings);
        btChangePhoto=(MaterialButton) findViewById(R.id.btChangePhoto);
        btLogout= (MaterialButton) findViewById(R.id.btLogout);
        username=(TextInputEditText) findViewById(R.id.etUsername);
        email=(TextInputEditText) findViewById(R.id.etEmail);
        information=(TextInputEditText) findViewById(R.id.etInformation);
        etUsername_text_input=(TextInputLayout) findViewById(R.id.etUsername_text_input);
        etEmail_text_input=(TextInputLayout) findViewById(R.id.etEmail_text_input);
        etInformation_text_input=(TextInputLayout) findViewById(R.id.etInformation_text_input);

        email.setText(User.getEmail());
        username.setText(User.getUsername());
        information.setText(User.getUserBio());


        // Instantiate the RequestQueue.
        mImageLoader = CustomVolleyRequestQueue.getInstance(getApplicationContext()).getImageLoader();
        //Image URL - This can point to any image file supported by Android
        final String url = "https://api.allybros.com/superego/images.php?get="+ User.getImage();
        mImageLoader.get(url, ImageLoader.getImageListener(image,R.drawable.simple_profile_photo, android.R.drawable.ic_dialog_alert));
        image.setImageUrl(url, mImageLoader);
        toolbar.setTitle("Settings");

        slidr= Slidr.attach(this);
        slidr.unlock();
        btChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"In Progress",Toast.LENGTH_SHORT).show();
            }
        });
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String session_token;
                SharedPreferences pref = getApplicationContext().getSharedPreferences(USER_INFORMATION_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                session_token=pref.getString("session_token","");
                editor.clear();
                editor.commit();
                LogoutTask.logoutTask(getApplicationContext(),session_token);

                Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.aciton_save:
                etEmail_text_input.setErrorEnabled(false);
                etInformation_text_input.setErrorEnabled(false);
                etUsername_text_input.setErrorEnabled(false);
                if(username.getText().toString().isEmpty()) etUsername_text_input.setError("Lütfen değer giriniz");
                if(email.getText().toString().isEmpty()) etEmail_text_input.setError("Lütfen değer giriniz");
                if(information.getText().toString().isEmpty()) etInformation_text_input.setError("Lütfen değer giriniz");
                if(!username.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !information.getText().toString().isEmpty()){
                    ChangeInfoTask.changeInfoTask(getApplicationContext(),username.getText().toString(),email.getText().toString(),information.getText().toString(),SplashActivity.session_token);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
