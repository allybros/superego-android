package com.allybros.superego.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.allybros.superego.R;

/**
 * Created by orcunk on 13.12.2021 22:10
 */
public class SegoEditText extends RelativeLayout {

    private boolean isPasswordArea = false;
    private RelativeLayout rlRoot;
    private EditText et;
    private ImageView ivEnd;


    public SegoEditText(Context context) {
        super(context);
    }

    public SegoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initArgs(context, attrs);
    }

    public SegoEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initArgs(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void initArgs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SegoEditText);
        int count = array.getIndexCount();
        for (int i= 0; i < count; i++){
            int attr = array.getIndex(i);
            switch (attr){
                case R.styleable.SegoEditText_sego_is_password_input:
                    isPasswordArea = array.getBoolean(attr, false);
                    break;
            }
        }
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_sego_edit_text, this, true);
        rlRoot = findViewById(R.id.rl);
        et = findViewById(R.id.et);
        ivEnd = findViewById(R.id.ivEnd);

        initViews();
    }


    private void initViews() {
        setPasswordView(isPasswordArea);
    }

    private void setPasswordView(boolean isPasswordArea) {
        if(isPasswordArea){
            ivEnd.setVisibility(View.VISIBLE);
            ivEnd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePasswordVisibility();
                }
            });
            et.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            ivEnd.setVisibility(View.GONE);
            et.setInputType(InputType.TYPE_CLASS_TEXT);


        }
    }

    private void changePasswordVisibility() {
        if(et.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
            ivEnd.setImageResource(R.drawable.ic_visibility_off);
            et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else{
            ivEnd.setImageResource(R.drawable.ic_visibility);
            et.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    private void setError(String errorMessage) {
        et.setHint(errorMessage);
        et.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.et_error_background));
    }

    private void clearError() {
        et.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.et_background));
    }

}
