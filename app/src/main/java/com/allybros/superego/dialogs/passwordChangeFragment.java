package com.allybros.superego.dialogs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.allybros.superego.R;
import com.allybros.superego.api.ChangePasswordTask;

import static android.content.Context.MODE_PRIVATE;

public class passwordChangeFragment extends DialogFragment {
    final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";
    Button btChange, btCancel;
    static EditText oldPassword;
    static EditText newPassword;
    static EditText newPasswordAgain;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_password_change, container,false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        btChange=getView().findViewById(R.id.bt_change_password);
        btCancel=getView().findViewById(R.id.bt_password_change_cancel);

        oldPassword=getView().findViewById(R.id.old_password);
        newPassword=getView().findViewById(R.id.new_password);
        newPasswordAgain=getView().findViewById(R.id.new_password_again);



        btChange.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences pref = getContext().getSharedPreferences(USER_INFORMATION_PREF, MODE_PRIVATE);
                String sessionToken= pref.getString("session_token", "");
                String oldPass=passwordChangeFragment.oldPassword.getText().toString();
                String newPass=passwordChangeFragment.newPassword.getText().toString();
                String newPassAgain=passwordChangeFragment.newPasswordAgain.getText().toString();
                ChangePasswordTask.changePasswordTask(getContext(),oldPass,sessionToken,newPass,newPassAgain);

            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }
}
