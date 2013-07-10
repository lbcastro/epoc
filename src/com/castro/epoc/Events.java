
package com.castro.epoc;

import java.io.File;

import com.castro.epoc.expressions.LookLeft;
import com.castro.epoc.expressions.LookRight;
import com.castro.epoc.expressions.WinkLeft;
import com.castro.epoc.expressions.WinkRight;

public enum Events {
    BASELINE(), WINKLEFT(), WINKRIGHT(), LOOKLEFT(), LOOKRIGHT();

    Events() {
    }

    public boolean detect(double[] v) {
        switch (this) {
            case WINKLEFT:
                return WinkLeft.detect(v);
            case WINKRIGHT:
                return WinkRight.detect(v);
            case LOOKLEFT:
                return LookLeft.detect(v);
            case LOOKRIGHT:
                return LookRight.detect(v);
            default:
                return false;
        }
    }

    public File getFile() {
        switch (this) {
            case BASELINE:
                return Files.sdCard("baseline");
            case WINKLEFT:
                return WinkLeft.getFile();
            case WINKRIGHT:
                return WinkRight.getFile();
            case LOOKLEFT:
                return LookLeft.getFile();
            case LOOKRIGHT:
                return LookRight.getFile();
            default:
                return null;
        }
    }

    public int[] getRelevant() {
        switch (this) {
            case WINKLEFT:
                return WinkLeft.getRelevant();
            case WINKRIGHT:
                return WinkRight.getRelevant();
            case LOOKLEFT:
                return LookLeft.getRelevant();
            case LOOKRIGHT:
                return LookRight.getRelevant();
            default:
                return null;
        }
    }

    public int getTimeout() {
        switch (this) {
            case WINKLEFT:
                return WinkLeft.getTimeout();
            case WINKRIGHT:
                return WinkRight.getTimeout();
            case LOOKLEFT:
                return LookLeft.getTimeout();
            case LOOKRIGHT:
                return LookRight.getTimeout();
            default:
                return 0;
        }
    }

    public void setLda() {
        switch (this) {
            case WINKLEFT:
                WinkLeft.setLda();
            case WINKRIGHT:
                WinkRight.setLda();
            case LOOKLEFT:
                LookLeft.setLda();
            case LOOKRIGHT:
                LookRight.setLda();
            default:
                return;
        }
    }
}
