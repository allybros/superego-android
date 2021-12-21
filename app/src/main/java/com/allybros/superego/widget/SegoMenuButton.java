package com.allybros.superego.widget;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.allybros.superego.R;

public class SegoMenuButton extends LinearLayout {

    private ConstraintLayout clContainer;
    private TextView tvButton;
    private ImageView ivButton;
    private View vBottomDivider;
    private String buttonLabel;
    private Drawable background = getResources().getDrawable(R.drawable.selector_card);
    private Drawable icon = getResources().getDrawable(R.drawable.ic_pencil_purple);
    @ColorInt
    private int labelColor = getResources().getColor(R.color.segoPurple600);
    private OnClickListener onClickListener = null;
    private boolean isBottomDividerVisible;

    public SegoMenuButton(Context context) {
        super(context);
        init();
    }

    public SegoMenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initArgs(context, attrs);
    }

    public SegoMenuButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initArgs(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void initArgs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SegoMenuButton);
        int count = array.getIndexCount();
        for (int i= 0; i < count; i++){
            int attr = array.getIndex(i);
            switch (attr){
                case R.styleable.SegoMenuButton_sego_button_background:
                    background = array.getDrawable(attr);
                    break;
                case R.styleable.SegoMenuButton_sego_label_text:
                    buttonLabel = array.getString(attr);
                    break;
                case R.styleable.SegoMenuButton_sego_start_icon:
                    icon = array.getDrawable(attr);
                    break;
                case R.styleable.SegoMenuButton_sego_label_color:
                    labelColor = array.getColor(attr,getResources().getColor(R.color.segoPurple600));
                    break;
                case R.styleable.SegoMenuButton_sego_bottom_divider:
                    isBottomDividerVisible = array.getBoolean(attr, false);
                    break;
            }
        }
        array.recycle();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_sego_menu_button, this, true);
        clContainer = findViewById(R.id.clContainer);
        tvButton = findViewById(R.id.tvButton);
        ivButton = findViewById(R.id.ivButton);
        vBottomDivider = findViewById(R.id.vBottomDivider);
        initViews();
        getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }


    private void initViews() {
        if(getLayoutTransition() == null){
            setLayoutTransition(new LayoutTransition());
        }

        setButtonLabel(buttonLabel);
        setButtonBackground(background);
        setIcon(icon);
        setLabelColor(labelColor);
        setBottomDivider(isBottomDividerVisible);
    }

    private void setBottomDivider(boolean isBottomDividerVisible) {
        if (isBottomDividerVisible) vBottomDivider.setVisibility(VISIBLE);
        else vBottomDivider.setVisibility(GONE);
    }

    private void setLabelColor(int labelColor) {
        tvButton.setTextColor(labelColor);
    }

    private void setIcon(Drawable icon) {
        ivButton.setImageDrawable(icon);
    }

    /**
     * Set button background with selector.
     * @param background It's a selector.
     */
    private void setButtonBackground(Drawable background) {
        clContainer.setBackground(background);
    }

    private void setButtonLabel(String tvLabel) {
        tvButton.setText(tvLabel);
    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
        clContainer.setOnClickListener(this.onClickListener);
    }
}