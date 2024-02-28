package com.allybros.superego.unit;

/**
 * Created by orcunkamiloglu on 11.08.2023
 */

public class Personality {
    private String type; //TODO: Change enum
    private String title;
    private String description;
    private String detail_url;
    private String primary_color;
    private String secondary_color;
    private String img_url;

    public Personality(String type, String title, String description, String detail_url, String primary_color, String secondary_color, String img_url) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.detail_url = detail_url;
        this.primary_color = primary_color;
        this.secondary_color = secondary_color;
        this.img_url = img_url;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetail_url() {
        return detail_url;
    }

    public void setDetail_url(String detail_url) {
        this.detail_url = detail_url;
    }

    public String getPrimary_color() {
        return primary_color;
    }

    public void setPrimary_color(String primary_color) {
        this.primary_color = primary_color;
    }

    public String getSecondary_color() {
        return secondary_color;
    }

    public void setSecondary_color(String secondary_color) {
        this.secondary_color = secondary_color;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
