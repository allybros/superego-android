package com.allybros.superego.widget;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.allybros.superego.R;
import com.allybros.superego.util.HelperMethods;

public class SegoProgressBar extends ConstraintLayout {

    private String headerLabel = "headerLabel";
    private String startLabel = "startLabel";
    private String endLabel = "endLabel";
    private int startPercent = 1;
    private int endPercent = 1;
    private Drawable containerBackground = getResources().getDrawable(R.drawable.sego_progress_container_background);
    @ColorInt
    private int progressColor = getResources().getColor(R.color.progressGreen);
    @ColorInt
    private int progressPassiveColor = getResources().getColor(R.color.progressPassive);
    private ConstraintLayout clContainer;
    private ImageView ivProgressBar;
    private ImageView ivProgress;
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
        ivProgress = findViewById(R.id.ivProgress);
        ivProgressBar = findViewById(R.id.ivProgressBar);
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
                case R.styleable.SegoProgressBar_sego_progress_bar_color:
                    progressPassiveColor = array.getColor(attr, getResources().getColor(R.color.progressPassive));
                    break;
                case R.styleable.SegoProgressBar_sego_progress_color:
                    progressColor = array.getColor(attr, getResources().getColor(R.color.progressGreen));
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
        validatePercents(startPercent, endPercent);
        setHeaderLabel(headerLabel);
        setStartLabel(startLabel);
        setEndLabel(endLabel);
        setStartPercent(startPercent);
        setEndPercent(endPercent);
        setProgressBarColor(progressPassiveColor);
        setProgressColor(progressColor);
        setContainerBackground(containerBackground);
        setProgressView(startPercent, endPercent);
        setProgressViewColors(startPercent, endPercent);

    }

    private void setProgressViewColors(int startPercent, int endPercent) {
        if(startPercent > endPercent){
            tvStartPercent.setTextColor(progressColor);
            tvStartLabel.setTextColor(progressColor);

            tvEndPercent.setTextColor(getResources().getColor(R.color.progressSmallPercentTextColor));
            tvEndLabel.setTextColor(getResources().getColor(R.color.progressSmallPercentTextColor));
        } else {
            tvEndPercent.setTextColor(progressColor);
            tvEndLabel.setTextColor(progressColor);

            tvStartPercent.setTextColor(getResources().getColor(R.color.progressSmallPercentTextColor));
            tvStartLabel.setTextColor(getResources().getColor(R.color.progressSmallPercentTextColor));
        }
        tvHeaderLabel.setTextColor(progressColor);
    }

    private void setProgressView(int startPercent, int endPercent) {
        if(startPercent > endPercent){
            HelperMethods.setConstraintConnectionHorizontal(
                    clContainer,
                    ivProgress,
                    ivProgressBar,
                    ConstraintSet.START,
                    ConstraintSet.START
            );
            setProgressWidth(startPercent);
        } else {
            HelperMethods.setConstraintConnectionHorizontal(
                    clContainer,
                    ivProgress,
                    ivProgressBar,
                    ConstraintSet.END,
                    ConstraintSet.END
            );
            setProgressWidth(endPercent);
        }
    }

    private void setProgressWidth(int percent) {
        int vProgressWidth = ivProgressBar.getLayoutParams().width;
        ivProgress.getLayoutParams().width = Math.round((percent*vProgressWidth)/100);
        ivProgress.requestLayout();
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
     * @param progressColor
     */
    @SuppressLint("ResourceType")
    private void setProgressColor(@ColorInt int progressColor) {
        Drawable background = ivProgress.getBackground();
        background.setTint(progressColor);
    }

    /**
     * Sets progress bar background with drawable
     * @param progressBarColor
     */
    private void setProgressBarColor(@ColorInt int progressBarColor) {
        Drawable background = ivProgressBar.getBackground();
        background.setTint(progressBarColor);
    }

    private void setEndPercent(int endPercent) {
        tvEndPercent.setText("%"+endPercent);
    }

    private void setStartPercent(int startPercent) {
        tvStartPercent.setText("%"+startPercent);
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

    public void setNewPercent(int newStartPercent, int newEndPercent){
        validatePercents(newStartPercent, newEndPercent);
        setStartPercent(startPercent);
        setEndPercent(endPercent);
        setProgressView(startPercent, endPercent);
        setProgressViewColors(startPercent, endPercent);
    }

    private void validatePercents(int startPercent, int endPercent) {
        if(startPercent+endPercent != 100){
            this.startPercent = 50;
            this.endPercent = 50;
        } else {
            this.startPercent = startPercent;
            this.endPercent = endPercent;
        }
    }
}
