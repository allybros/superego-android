package com.allybros.superego.util;

import java.io.Serializable;

public class UserTraits implements Serializable {

    int traitNo,value;
    public UserTraits(int traitNo, int value) {
        this.traitNo = traitNo;
        this.value = value;
    }
}
