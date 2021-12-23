package com.allybros.superego.widget;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.allybros.superego.R;
import com.allybros.superego.util.HelperMethods;

public class SegoProgressBar extends ConstraintLayout {

    private String headerLabel = "headerLabel";
    private String startLabel = "startLabel";
    private String endLabel = "endLabel";
    private int startPercent = 1;
    private int endPercent = 1;
    private Drawable progressBarBackground = getResources().getDrawable(R.drawable.shape_sego_progress_bar_background);
    private Drawable progressBackground = getResources().getDrawable(R.drawable.shape_sego_progress_background);;
    private Drawable containerBackground = getResources().getDrawable(R.drawable.sego_progress_container_background);

    private ConstraintLayout clContainer;
    private View vProgressBar;
    private View vProgress;
    private AppCompatTextView tvHeaderLabel;
    private AppCompatTextView tvStartLabel;
    private AppCompatTextView tvEndLabel;
    private AppCompatTextView tvStartPercent;
    private AppCompatTextView tvEndPercent;


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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_sego_progress, this, true);
        clContainer = findViewById(R.id.clContainer);
        vProgress = findViewById(R.id.vProgress);
        vProgressBar = findViewById(R.id.vProgressBar);
        tvEndPercent = findViewById(R.id.tvEndPercent);
        tvStartPercent = findViewById(R.id.tvStartPercent);
        tvStartLabel = findViewById(R.id.tvStartLabel);
        tvEndLabel = findViewById(R.id.tvEndLabel);
        tvHeaderLabel = findViewById(R.id.tvHeaderLabel);

        initViews();
        getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    private void initArgs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SegoProgressBar);
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
        if(getLayoutTransition() == null){
            setLayoutTransition(new LayoutTransition());
        }
        setHeaderLabel(headerLabel);
        setStartLabel(startLabel);
        setEndLabel(endLabel);
        setStartPercent(startPercent);
        setEndPercent(endPercent);
        setProgressBarBackground(progressBarBackground);
        setProgressBackground(progressBackground);
        setContainerBackground(containerBackground);
        setProgressView(startPercent, endPercent);
    }

    private void setProgressView(int startPercent, int endPercent) {
        if(startPercent > endPercent){
            HelperMethods.setConstraintConnection(
                    clContainer,
                    vProgress,
                    vProgressBar,
                    ConstraintSet.START,
                    ConstraintSet.START
            );
            setProgressWidth(startPercent);
        } else {
            HelperMethods.setConstraintConnection(
                    clContainer,
                    vProgress,
                    vProgressBar,
                    ConstraintSet.END,
                    ConstraintSet.END
            );
            setProgressWidth(endPercent);
        }
    }

    private void setProgressWidth(int percent) {
        int vProgressWidth = vProgressBar.getLayoutParams().width;
        vProgress.getLayoutParams().width = (int) Math.round((percent*vProgressWidth)/100);
        vProgress.requestLayout();
    }

    /**
     * Sets container background with drawable
     * @param containerBackground
     */
    private void setContainerBackground(Drawable containerBackground) {
        clContainer.setBackground(containerBackground);
    }

    /**
     * Sets progress background with drawable
     * @param progressBackground
     */
    private void setProgressBackground(Drawable progressBackground) {
        vProgress.setBackground(progressBackground);
    }

    /**
     * Sets progress bar background with drawable
     * @param progressBarBackground
     */
    private void setProgressBarBackground(Drawable progressBarBackground) {
        vProgressBar.setBackground(progressBarBackground);
    }

    private void setEndPercent(int endPercent) {
        tvEndPercent.setText("%\n"+endPercent);
    }

    private void setStartPercent(int startPercent) {
        tvStartPercent.setText("%\n"+startPercent);
    }

    private void setEndLabel(String endLabel) {
        tvEndLabel.setText(endLabel);
    }

    private void setStartLabel(String startLabel) {
        tvStartLabel.setText(startLabel);
    }

    private void setHeaderLabel(String headerLabel) {
        tvHeaderLabel.setText(headerLabel);
    }
}
