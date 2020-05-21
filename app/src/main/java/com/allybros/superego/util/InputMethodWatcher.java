package com.allybros.superego.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

/**
 * Detects input method changes and  sets listener for particular events.
 * @author umutalacam
 */
public class InputMethodWatcher {
    private View rootView;
    private KeyboardStatusListener softKeyboardShownListener;
    private boolean isKeyboardShown = false;

    public InputMethodWatcher(final View rootView) {
        this.rootView = rootView;

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                //TODO: Check on different devices
                if (heightDiff > 400) {
                    if (!isKeyboardShown && softKeyboardShownListener != null) {
                        softKeyboardShownListener.onShown();
                    }
                    Log.d("InputMethodWatcher", "Keyboard is shown");
                    isKeyboardShown = true;
                } else if (isKeyboardShown) {
                    if (softKeyboardShownListener!=null){
                        softKeyboardShownListener.onHidden();
                    }
                    Log.d("InputMethodWatcher", "Keyboard is down");
                    isKeyboardShown = false;
                }
            }
        });
    }

    /**
     * Set listener which listens to virtual keyboard is shown or not.
     * @param keyboardStatusListener A SoftKeyboardShownListener.
     */
    public void setKeyboardStatusListener(KeyboardStatusListener keyboardStatusListener) {
        this.softKeyboardShownListener = keyboardStatusListener;
    }

    /**
     * Shows the software keyboard
     */
    public void showSoftKeyboard() {
        rootView.setFocusable(true);
        if (rootView.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) rootView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(rootView, InputMethodManager.SHOW_IMPLICIT);
        }
        rootView.setFocusable(false);
    }

    /**
     * Hides the software keyboard.
     */
    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) rootView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        rootView.setFocusable(true);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        rootView.setFocusable(false);
    }

    public boolean isKeyboardShown() {
        return isKeyboardShown;
    }

    /**
     * Interface for SoftKeyboardStatusListeners
     */
    public interface KeyboardStatusListener {
        /**
         * Invoked when software keyboard is shown.
         */
        void onShown();

        void onHidden();
    }
}


