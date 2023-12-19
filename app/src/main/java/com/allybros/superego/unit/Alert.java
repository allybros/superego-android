package com.allybros.superego.unit;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Alert {
    private String type;
    private String title;
    private String message;
    @SerializedName("img_url")
    private String imageUrl;
    private List<AlertButton> buttons = new ArrayList<>();

    public static final String TYPE_DISMISSIBLE = "alert_dismissible";
    public static final String TYPE_NON_DISMISSIBLE = "alert_non_dismissible";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<AlertButton> getButtons() {
        return buttons;
    }

    public void setButtons(List<AlertButton> buttons) {
        this.buttons = buttons;
    }

}
