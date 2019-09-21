package com.allybros.superego.unit;

public class Trait {
    private int traitNo;
    private float value;
    private String positiveName;
    private String negativeName;
    private String positiveIconURL;
    private String negativeIconURL;

    public Trait(int traitNo, String positiveName, String negativeName, String positiveIconURL, String negativeIconURL) {
        this.traitNo = traitNo;
        this.positiveName = positiveName;
        this.negativeName = negativeName;
        this.positiveIconURL = positiveIconURL;
        this.negativeIconURL = negativeIconURL;
    }

    public Trait(int traitNo, float value) {
        this.traitNo = traitNo;
        this.value = value;
    }
    public int getTraitNo() {
        return traitNo;
    }

    public void setTraitNo(int traitNo) {
        this.traitNo = traitNo;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getPositiveName() {
        return positiveName;
    }

    public void setPositiveName(String positiveName) {
        this.positiveName = positiveName;
    }

    public String getNegativeName() {
        return negativeName;
    }

    public void setNegativeName(String negativeName) {
        this.negativeName = negativeName;
    }

    public String getPositiveIconURL() {
        return positiveIconURL;
    }

    public void setPositiveIconURL(String positiveIconURL) {
        this.positiveIconURL = positiveIconURL;
    }

    public String getNegativeIconURL() {
        return negativeIconURL;
    }

    public void setNegativeIconURL(String negativeIconURL) {
        this.negativeIconURL = negativeIconURL;
    }
}
