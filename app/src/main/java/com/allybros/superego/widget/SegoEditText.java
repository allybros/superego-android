package com.allybros.superego.widget;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.allybros.superego.R;

/**
 * Created by orcunk on 13.12.2021 22:10
 */
public class SegoEditText extends ConstraintLayout {

    private ConstraintLayout rlRoot;
    private TextView tvHeader;
    private EditText et;
    private ImageView ivEnd;
    private boolean isPasswordArea = false;
    private String headerText;
    @ColorInt
    private int headerTextColor = getResources().getColor(R.color.Black);
    @Dimension
    private int headerTextSize = 12;
    @ColorInt
    private int inputTextColor = getResources().getColor(R.color.Black);
    @Dimension
    private int inputTextSize =13;

    public SegoEditText(Context context) {
        super(context);
        init();
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
                case R.styleable.SegoEditText_sego_header_text:
                    headerText = array.getString(attr);
                    break;
                case R.styleable.SegoEditText_sego_header_text_color:
                    headerTextColor = array.getColor(attr, getResources().getColor(R.color.Black));
                    break;
                case R.styleable.SegoEditText_sego_header_text_size:
                    headerTextSize = array.getDimensionPixelSize(attr, 12);
                    break;
                case R.styleable.SegoEditText_sego_input_text_color:
                    inputTextColor = array.getColor(attr,getResources().getColor(R.color.Black));
                    break;
                case R.styleable.SegoEditText_sego_input_text_size:
                    inputTextSize = array.getDimensionPixelSize(attr,13);
                    break;
            }
        }
        array.recycle();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_sego_edit_text, this, true);
        rlRoot = findViewById(R.id.rl);
        et = findViewById(R.id.et);
        ivEnd = findViewById(R.id.ivEnd);
        tvHeader = findViewById(R.id.tvHeader);
        initViews();
        getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }


    private void initViews() {
        if(getLayoutTransition() == null){
            setLayoutTransition(new LayoutTransition());
        }
        setPasswordView(isPasswordArea);
        setHeaderText(headerText);
        setHeaderTextColor(headerTextColor);
        setHeaderTextSize(headerTextSize);
        setInputTextColor(inputTextColor);
        setInputTextSize(inputTextSize);
    }

    public void setInputTextSize(int inputTextSize) {
        et.setTextSize(inputTextSize);
    }

    public void setInputTextColor(int inputTextColor) {
        et.setTextColor(inputTextColor);
    }

    public void setHeaderTextSize(int headerTextSize) {
        tvHeader.setTextSize(headerTextSize);
    }

    public void setHeaderTextColor(int headerTextColor) {
        tvHeader.setTextColor(headerTextColor);
    }

    public void setHeaderText(String headerText) {
        tvHeader.setText(headerText);
    }

    public void setPasswordView(boolean isPasswordArea) {
        if(isPasswordArea){
            ivEnd.setVisibility(View.VISIBLE);
            ivEnd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePasswordVisibility();
                }
            });
            et.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivEnd.performClick();
        } else {
            ivEnd.setVisibility(View.GONE);
            et.setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }

    public void changePasswordVisibility() {
        if(et.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
            ivEnd.setImageResource(R.drawable.ic_visibility_off);
            et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else{
            ivEnd.setImageResource(R.drawable.ic_visibility);
            et.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    public void setError(String errorMessage) {
        et.setHint(errorMessage);
        et.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.et_error_background));
    }

    public void clearError() {
        et.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.et_background));
    }

    public String getText(){
        return et.getText().toString();
    }

    public void addTextChangedListener(TextWatcher watcher){
        et.addTextChangedListener(watcher);
    }

}
