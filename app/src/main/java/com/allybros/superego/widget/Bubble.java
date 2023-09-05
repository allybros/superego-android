package com.allybros.superego.widget;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.allybros.superego.R;

import java.sql.Ref;

/**
 * Created by orcun on 5.09.2023
 */


public final class Bubble {
    private PopupWindow tipWindow;
    private View contentView;
    private LayoutInflater inflater;
    private String text;
    private Context context;
    private Activity activity;
    private int background;
    private int anchorTopStyle;
    private int anchorDownStyle;
    private int animationStyle;

    public final void showBubble(final View anchor) {
        PopupWindow var10000 = this.tipWindow;
        if (var10000 != null) {
            var10000.setHeight(-2);
        }

        var10000 = this.tipWindow;
        if (var10000 != null) {
            var10000.setWidth(-2);
        }

        var10000 = this.tipWindow;
        if (var10000 != null) {
            var10000.setOutsideTouchable(true);
        }

        var10000 = this.tipWindow;
        if (var10000 != null) {
            var10000.setTouchable(true);
        }

        var10000 = this.tipWindow;
        if (var10000 != null) {
            var10000.setFocusable(true);
        }

        var10000 = this.tipWindow;
        if (var10000 != null) {
            var10000.setBackgroundDrawable((Drawable)(new BitmapDrawable()));
        }

        var10000 = this.tipWindow;
        if (var10000 != null) {
            var10000.setContentView(this.contentView);
        }

        int[] screenPos = new int[2];
        anchor.getLocationOnScreen(screenPos);
        final Rect anchorRect = new Rect(screenPos[0], screenPos[1], screenPos[0] + anchor.getWidth(), screenPos[1] + anchor.getHeight());
        this.contentView.measure(-2, -2);
        ((LinearLayout)this.contentView.findViewById(R.id.root)).measure(-2, -2);
        final int[] positionX = {anchorRect.centerX()};
        final int[] positionY = {anchorRect.centerY()};
        LinearLayout var6 = (LinearLayout)this.contentView.findViewById(R.id.root);
        var6.getViewTreeObserver().addOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener)(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                LinearLayout var10000 = (LinearLayout)Bubble.this.contentView.findViewById(R.id.root);
                var10000.getViewTreeObserver().removeOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener)this);
                int[] screenPopup = new int[2];
                ((LinearLayout)Bubble.this.contentView.findViewById(R.id.root)).getLocationOnScreen(screenPopup);
                int var10002 = screenPopup[0];
                int var10003 = screenPopup[1];
                int var10004 = screenPopup[0];
                LinearLayout var10005 = (LinearLayout)Bubble.this.contentView.findViewById(R.id.root);
                var10004 += var10005.getWidth();
                int var17 = screenPopup[1];
                LinearLayout var10006 = (LinearLayout)Bubble.this.contentView.findViewById(R.id.root);
                Rect rootRect = new Rect(var10002, var10003, var10004, var17 + var10006.getHeight());
                var10000 = (LinearLayout)Bubble.this.contentView.findViewById(R.id.root);
                int contentViewWidth = var10000.getMeasuredWidth();
                var10000 = (LinearLayout)Bubble.this.contentView.findViewById(R.id.root);
                int contentViewHeight = var10000.getMeasuredHeight();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                Activity var11 = Bubble.this.activity;
                if (var11 != null) {
                    WindowManager var12 = var11.getWindowManager();
                    if (var12 != null) {
                        Display var13 = var12.getDefaultDisplay();
                        if (var13 != null) {
                            var13.getMetrics(displayMetrics);
                        }
                    }
                }

                int rootWidthx = displayMetrics.widthPixels;
                int rootHeightx = displayMetrics.heightPixels;
                int margin = 0;
                if (positionX[0] + contentViewWidth > rootWidthx) {
                    margin += 12;
                }

                margin += positionX[0] - rootRect.left - 36;
                if (margin < 36) {
                    margin += 48;
                } else if (margin > contentViewWidth - 96) {
                    margin = contentViewWidth - 96;
                }

                Bubble var14;
                ImageView var10001;
                if (contentViewHeight + anchorRect.bottom < rootHeightx) {
                    positionX[0] = anchorRect.centerX() - 24;
                    positionY[0] = anchorRect.centerY() + 48;
                    var14 = Bubble.this;
                    var10001 = (ImageView)Bubble.this.contentView.findViewById(R.id.ivAnchorUp);
                    var14.setMarginStart((View)var10001, margin);
                    var14 = Bubble.this;
                    var10001 = (ImageView)Bubble.this.contentView.findViewById(R.id.ivAnchorUp);
                    var14.visible((View)var10001);
                } else {
                    positionX[0] = anchorRect.centerX() - 24;
                    positionY[0] = anchorRect.centerY() - (contentViewHeight + anchorRect.height()) - 12;
                    var14 = Bubble.this;
                    var10001 = (ImageView)Bubble.this.contentView.findViewById(R.id.ivAnchorDown);
                    var14.setMarginStart((View)var10001, margin);
                    var14 = Bubble.this;
                    var10001 = (ImageView)Bubble.this.contentView.findViewById(R.id.ivAnchorDown);
                    var14.visible((View)var10001);
                }

                Bubble.this.dismissBubble();
                Bubble.this.visible(Bubble.this.contentView);
                TextView var15 = (TextView)Bubble.this.contentView.findViewById(R.id.tvCaption);
                var15.setText((CharSequence)Bubble.this.text);
                Bubble.this.setBackground();
                PopupWindow var16 = Bubble.this.tipWindow;
                if (var16 != null) {
                    var16.showAtLocation(anchor, 0, positionX[0] - 48, positionY[0]);
                }

            }
        }));
        TextView var7 = (TextView)this.contentView.findViewById(R.id.tvCaption);
        var7.setText((CharSequence)this.text);
        this.invisible(this.contentView);
        var10000 = this.tipWindow;
        if (var10000 != null) {
            var10000.showAtLocation(anchor, 0, positionX[0] - 36, positionY[0]);
        }

    }

    public final boolean isBubbleShown() {
        return this.tipWindow != null && this.tipWindow.isShowing();
    }

    public final void dismissBubble() {
        if (this.tipWindow != null && this.tipWindow.isShowing()) {
            this.tipWindow.dismiss();
        }

    }

    private final void setMarginStart(View view, int marginStart) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.LayoutParams var10000 = view.getLayoutParams();
            if (var10000 == null) {
                throw new NullPointerException("null cannot be cast to non-null type android.widget.LinearLayout.LayoutParams");
            }

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)var10000;
            params.setMargins(marginStart, 0, 0, 0);
            view.setLayoutParams((ViewGroup.LayoutParams)params);
            view.requestLayout();
        }

    }

    private final void setBackground() {
        LinearLayout var10000 = (LinearLayout)this.contentView.findViewById(R.id.llBackground);
        Context var10001 = this.context;
        Context var1;
        boolean var3;
        Drawable var5;
        Drawable var8;
        if (var10001 != null) {
            var1 = var10001;
            LinearLayout var4 = var10000;
            var3 = false;
            var5 = ContextCompat.getDrawable(var1, this.background);
            var10000 = var4;
            var8 = var5;
        } else {
            var8 = null;
        }

        var10000.setBackground(var8);
        ImageView var7 = (ImageView)this.contentView.findViewById(R.id.ivAnchorUp);
        var10001 = this.context;
        ImageView var6;
        if (var10001 != null) {
            var1 = var10001;
            var6 = var7;
            var3 = false;
            var5 = ContextCompat.getDrawable(var1, this.anchorTopStyle);
            var7 = var6;
            var8 = var5;
        } else {
            var8 = null;
        }

        var7.setImageDrawable(var8);
        var7 = (ImageView)this.contentView.findViewById(R.id.ivAnchorDown);
        var10001 = this.context;
        if (var10001 != null) {
            var1 = var10001;
            var6 = var7;
            var3 = false;
            var5 = ContextCompat.getDrawable(var1, this.anchorDownStyle);
            var7 = var6;
            var8 = var5;
        } else {
            var8 = null;
        }

        var7.setImageDrawable(var8);
    }

    private final void invisible(View $this$invisible) {
        $this$invisible.setVisibility(View.INVISIBLE);
    }

    private final void visible(View $this$visible) {
        $this$visible.setVisibility(View.VISIBLE);
    }

    public Bubble(String text, Activity activity, @Nullable Integer background, @Nullable Integer anchorTopStyle, @Nullable Integer anchorDownStyle, @Nullable Integer animationStyle) {
        this.text = "";
        this.animationStyle = R.style.BubbleAnimation;
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.tipWindow = new PopupWindow(this.context);
        Context var10001 = this.context;
        Object var7 = var10001 != null ? var10001.getSystemService(Context.LAYOUT_INFLATER_SERVICE) : null;
        if (var7 == null) {
            throw new NullPointerException("null cannot be cast to non-null type android.view.LayoutInflater");
        } else {
            this.inflater = (LayoutInflater)var7;
            View var8 = this.inflater.inflate(R.layout.widget_switch_button_popup, (ViewGroup)null);
            this.contentView = var8;
            this.text = text;
            this.background = background != null ? background : R.drawable.bg_spinner_popup;
            this.anchorTopStyle = anchorTopStyle != null ? anchorTopStyle : R.drawable.nav_up;
            this.anchorDownStyle = anchorDownStyle != null ? anchorDownStyle : R.drawable.nav_down;
            this.animationStyle = animationStyle != null ? animationStyle : R.style.BubbleAnimation;
            this.tipWindow.setAnimationStyle(this.animationStyle);
        }
    }

    // $FF: synthetic method
    public static final void access$setActivity$p(Bubble $this, Activity var1) {
        $this.activity = var1;
    }

    // $FF: synthetic method
    public static final void access$setText$p(Bubble $this, String var1) {
        $this.text = var1;
    }
}

