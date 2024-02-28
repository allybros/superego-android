package com.allybros.superego.unit;

public class AlertButton {
    private String type;
    private String text;
    private String action;
    public static final String ALERT_BUTTON_TYPE_PRIMARY = "primary";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

}
