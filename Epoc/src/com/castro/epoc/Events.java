
package com.castro.epoc;

import java.io.File;

import com.castro.epoc.expressions.LookLeft;
import com.castro.epoc.expressions.LookRight;
import com.castro.epoc.expressions.WinkLeft;
import com.castro.epoc.expressions.WinkRight;

public enum Events {
    BASELINE(),
    WINKLEFT(WinkLeft.getFile(), WinkLeft.getTimeout(), WinkLeft.getRelevant()),
    WINKRIGHT(WinkRight.getFile(), WinkRight.getTimeout(), WinkRight.getRelevant()),
    LOOKLEFT(LookLeft.getFile(), LookLeft.getTimeout(), LookLeft.getRelevant()),
    LOOKRIGHT(LookRight.getFile(), LookRight.getTimeout(), LookRight.getRelevant());

    public File file;
    int timeout;
    public LDA data;
    int[] relevant;

    // Baseline case
    Events() {
        this.file = Files.sdCard("baseline");
    }

    Events(File file, int timeout, int[] relevant) {
        this.file = file;
        this.timeout = timeout;
        this.relevant = relevant;
    }

    public boolean detect(double[] v) {
        if (this == WINKLEFT) {
            return WinkLeft.detect(v);
        }
        if (this == WINKRIGHT) {
            return WinkRight.detect(v);
        }
        if (this == LOOKLEFT) {
            return LookLeft.detect(v);
        }
        if (this == LOOKRIGHT) {
            return LookRight.detect(v);
        }
        return false;
    }

    public void setLda() {
        if (this == WINKLEFT) {
            WinkLeft.setLda();
        }
        if (this == WINKRIGHT) {
            WinkRight.setLda();
        }
        if (this == LOOKLEFT) {
            LookLeft.setLda();
        }
        if (this == LOOKRIGHT) {
            LookRight.setLda();
        }
    }
}
