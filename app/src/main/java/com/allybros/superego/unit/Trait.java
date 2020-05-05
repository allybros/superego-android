package com.allybros.superego.unit;

import java.util.ArrayList;

public class Trait {
    private int traitNo;
    private String positiveName;
    private String negativeName;
    private String positiveEmojiName;
    private String negativeEmojiName;
    private static ArrayList<Trait> allTraits;

    public Trait(int traitNo,
                 String positiveName,
                 String negativeName,
                 String positiveEmojiName,
                 String negativeEmojiName) {
        this.traitNo = traitNo;
        this.positiveName = positiveName;
        this.negativeName = negativeName;
        this.positiveEmojiName = positiveEmojiName;
        this.negativeEmojiName = negativeEmojiName;
    }

    public int getTraitNo() {
        return traitNo;
    }

    public String getPositiveName() {
        return positiveName;
    }

    public String getNegativeName() {
        return negativeName;
    }

    public String getPositiveEmojiName() {
        return positiveEmojiName;
    }

    public String getNegativeEmojiName() {
        return negativeEmojiName;
    }

    public static void setAllTraits(ArrayList<Trait> allTraits) {
        Trait.allTraits = allTraits;
    }

    public static ArrayList<Trait> getAllTraits() {
        return allTraits;
    }

    /**
     * Get trait object by traitNo from all traits
     * @param traitNo int TraitNo
     * @return Trait|null New trait object or null when couldn't found a matching Trait
     */
    public static Trait getTraitById(int traitNo){
        for (Trait t: allTraits) {
            if (t.getTraitNo() == traitNo){
                //Create a new instance of matching trait
                return new Trait(
                  t.getTraitNo(),
                  t.getPositiveName(),
                  t.getNegativeName(),
                  t.getPositiveEmojiName(),
                  t.getNegativeEmojiName()
                );
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) return false;
        Trait trait = (Trait) o;
        return traitNo == trait.traitNo;
    }
}
