
package com.castro.epoc.expressions;

import java.io.File;

import com.castro.epoc.Files;
import com.castro.epoc.LDA;

public class LookLeft extends Look {

    private static LDA sData;
    private static double[] sCurrentValues = new double[2];
    private static double[] sLastValues = new double[2];
    private static double[] sInitialValues = new double[2];
    private static double[] sAmplitudes = new double[2];
    private static int[] sChannelsIndex = new int[2];
    private static boolean sRising = false;
    private static int sRisingTimeout = 0;
    private static int sRecentTimeout = 0;
    private static final File sFile = Files.sdCard("lookleft");
    private static final int sRisingTimeoutMax = 3;
    private static final int sDominantChannel = 2;
    private static final int sOppositeChannel = 13;
    private static final int[] sRelevantChannels = {
            sDominantChannel, sOppositeChannel
    };

    public static boolean detect(double[] values) {
        if (!check(sData, sFile, values)) {
            return false;
        }
        sCurrentValues = separate(values, sChannelsIndex);
        if (sCurrentValues[0] > sLastValues[0]) {
            if (!sRising) {
                sRising = true;
                sInitialValues = sCurrentValues;
            }
            sRisingTimeout = sRisingTimeoutMax;
            sLastValues = sCurrentValues;
            return false;
        }
        if (!sRising) {
            reset();
            return false;
        }
        if (sRisingTimeout > 0) {
            sRisingTimeout -= 1;
            return false;
        }
        sAmplitudes = amplitudes(sLastValues, sInitialValues);
        if (sAmplitudes[0] < sAmplitudes[1]) {
            reset();
            return false;
        }
        if (!test(sAmplitudes, sData)) {
            reset();
            return false;
        }
        reset();
        return true;
    }

    public static File getFile() {
        return sFile;
    }

    public static int[] getRelevant() {
        return sRelevantChannels;
    }

    public static int getTimeout() {
        return sRecentTimeout;
    }

    private static void initial() {
        for (int x = 0; x < sRelevantChannels.length; x++) {
            sInitialValues[x] = 0;
        }
    }

    private static void reset() {
        sRising = false;
        sRisingTimeout = 0;
        sLastValues = sCurrentValues;
        initial();
    }

    public static void setLda() {
        sChannelsIndex = indexes(sRelevantChannels);
        sData = train(sFile, sChannelsIndex);
    }
    //
    // public static void function(int i) {
    // // if (!Calculation.isActive() && Switcher.getEyes()) {
    // // int ii = i;
    // // if (ii <= 0) ii = 4;
    // // else ii -= 1;
    // // Switcher.getViewPager().setCurrentItem(ii);
    // // return;
    // // }
    // // else {
    // switch (i) {
    // case CALC_VIEW:
    // // Calculation.checkSide(0);
    // return;
    // }
    // // }
    // }
}
