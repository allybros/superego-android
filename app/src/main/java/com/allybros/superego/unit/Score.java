package com.allybros.superego.unit;

public class Score {
    private int traitNo;
    private float value;

    public Score(int traitNo, float value) {
        this.traitNo = traitNo;
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
    public String getEmojiName(){
        Trait t = Trait.getTraitById(this.getTraitNo());
        if (t == null)
            return null;
        else if (this.getValue()>=0)
            return t.getPositiveEmojiName();
        else
            return t.getNegativeEmojiName();

    }

    /**
     * Get Trait name by trait number
     * @return String Associated Trait name
     */
    public String getTraitName(){
        Trait t = Trait.getTraitById(this.getTraitNo());
        if (t == null)
            return null;
        else if (this.getValue()>=0)
            return t.getPositiveName();
        else
            return t.getNegativeName();

    }
}
