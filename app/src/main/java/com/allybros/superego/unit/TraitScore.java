package com.allybros.superego.unit;

public class TraitScore {
    private final int traitNo;
    private final String name;
    private final String icon;
    private final float value;

    public TraitScore(int traitNo, String name, String icon, float value) {
        this.traitNo = traitNo;
        this.name = name;
        this.icon = icon;
        this.value = value;
    }

    public int getTraitNo() {
        return traitNo;
    }

    public float getValue() {
        return value;
    }

    /**
     * Get Emoji name by trait number
     * @return String Associated Emoji name
     */
    public String getIcon(){
        return this.icon;

    }

    /**
     * Get Trait name by trait number
     * @return String Associated Trait name
     */
    public String getName(){
        return this.name;

    }
}
