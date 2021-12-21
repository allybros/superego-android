package com.allybros.superego.widget;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.allybros.superego.R;

public class SegoCheckBox extends LinearLayout {

    private TextView tvLabel;
    private CheckBox checkBox;
    private String labelText;
    private Drawable checkBoxBackground = getResources().getDrawable(R.drawable.selector_check_box);
    private Drawable checkBoxErrorBackground = getResources().getDrawable(R.drawable.checkbox_error);
    private OnClickListener onClickListener = null;

    public SegoCheckBox(Context context) {
        super(context);
        init();
    }

    public SegoCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        initArgs(context, attrs);
    }

    public SegoCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initArgs(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void initArgs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SegoCheckBox);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.SegoCheckBox_sego_box_drawable:
                    checkBoxBackground = array.getDrawable(attr);
                    break;
                case R.styleable.SegoCheckBox_sego_box_error_drawable:
                    checkBoxErrorBackground = array.getDrawable(attr);
                    break;
                case R.styleable.SegoCheckBox_sego_checkbox_text:
                    labelText = array.getString(attr);
                    break;
            }
        }
        array.recycle();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_sego_check_box, this, true);
        tvLabel = findViewById(R.id.tvLabel);
        checkBox = findViewById(R.id.checkbox);
        initViews();
        getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }


    private void initViews() {
        if (getLayoutTransition() == null) {
            setLayoutTransition(new LayoutTransition());
        }

        setCheckBoxStyle(checkBoxBackground);
        setCheckBoxLabelText(labelText);
    }

    private void setCheckBoxStyle(Drawable checkBoxBackground) {
        checkBox.setBackground(checkBoxBackground);
    }

    private void setCheckBoxLabelText(String labelText) {
        tvLabel.setText(labelText);
    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
        checkBox.setOnClickListener(this.onClickListener);
    }

    public TextView getLabelTextView(){
        return tvLabel;
    }

    public Boolean isChecked(){
        return checkBox.isChecked();
    }

    public void clearError(){
        checkBox.setBackground(checkBoxBackground);
    }

    public void setError(){
        checkBox.setBackground(checkBoxErrorBackground);
    }

    public void setEnabled(boolean value){
        checkBox.setEnabled(value);
    }
}
