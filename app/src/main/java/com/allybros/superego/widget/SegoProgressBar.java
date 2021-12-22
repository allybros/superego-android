package com.allybros.superego.widget;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.allybros.superego.R;

public class SegoProgressBar extends ConstraintLayout {

    private String headerLabel;
    private String startLabel;
    private String endLabel;
    private int startPercent;
    private int endPercent;
    private Drawable progressBarBackground;
    private Drawable progressBackground;
    private Drawable containerBackground;

    private ConstraintLayout clContainer;

    public SegoProgressBar(Context context) {
        super(context);
        init();
    }

    public SegoProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initArgs(context, attrs);
    }

    public SegoProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initArgs(context, attrs);
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_sego_progress, this, true);
        clContainer = findViewById(R.id.clContainer);
        initViews();
        getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    private void initArgs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SegoMenuButton);
        int count = array.getIndexCount();
        for (int i= 0; i < count; i++){
            int attr = array.getIndex(i);
            switch (attr){
                case R.styleable.SegoProgressBar_sego_progress_header_label:
                    headerLabel = array.getString(attr);
                    break;
                case R.styleable.SegoProgressBar_sego_progress_start_label:
                    startLabel = array.getString(attr);
                    break;
                case R.styleable.SegoProgressBar_sego_progress_end_label:
                    endLabel = array.getString(attr);
                    break;
                case R.styleable.SegoProgressBar_sego_progress_start_percent:
                    startPercent = array.getInt(attr, 0);
                    break;
                case R.styleable.SegoProgressBar_sego_progress_end_percent:
                    endPercent = array.getInt(attr, 0);
                    break;
                case R.styleable.SegoProgressBar_sego_progress_bar_background:
                    progressBarBackground = array.getDrawable(attr);
                    break;
                case R.styleable.SegoProgressBar_sego_progress_progress_background:
                    progressBackground = array.getDrawable(attr);
                    break;
                case R.styleable.SegoProgressBar_sego_progress_bar_container_background:
                    containerBackground = array.getDrawable(attr);
                    break;
            }
        }
        array.recycle();
    }

    private void initViews() {

    }
}
