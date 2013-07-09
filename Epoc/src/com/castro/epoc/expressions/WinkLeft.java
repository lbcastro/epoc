
package com.castro.epoc.expressions;

import java.io.File;

import com.castro.epoc.Files;
import com.castro.epoc.LDA;

public class WinkLeft extends Wink {

    private static LDA[] sData = new LDA[6];
    private static int[] sChannelsIndex = new int[4];
    private static double[] sAmplitudes = new double[4];
    private static double[] sCurrentValues = new double[4];
    private static double[] sInitialValues = new double[4];
    private static double[] sLastValues = new double[4];
    private static double[] sRatios = new double[4];
    private static boolean sRising = false;
    private static int sRisingTimeout = 0;
    private static int sRecentTimeout = 0;
    private static final File sFile = Files.sdCard("winkleft");
    private static final int sRisingTimeoutMax = 3;
    private static final int sDominantChannel = 2;
    private static final int sSecondaryChannel = 1;
    private static final int sTertiaryChannel = 4;
    private static final int sOppositeChannel = 13;
    private static final int[] sRelevantChannels = {
            sDominantChannel, sSecondaryChannel,
            sTertiaryChannel, sOppositeChannel
    };

    public static File getFile() {
        return sFile;
    }

    public static int[] getRelevant() {
        return sRelevantChannels;
    }

    public static int getTimeout() {
        return sRecentTimeout;
    }

    public static boolean detect(double[] values) {
        // Check for missing files, LDA data or input values.
        if (!check(sData, sFile, values))
            return false;
        // Set current values.
        sCurrentValues = separate(values, sChannelsIndex);
        // Check if the dominant value is rising. Returns false to detect peaks
        // only.
        if (sCurrentValues[0] > sLastValues[0]) {
            if (!sRising) {
                sRising = true;
                sInitialValues = sCurrentValues;
            }
            sRisingTimeout = sRisingTimeoutMax;
            sLastValues = sCurrentValues;
            return false;
        }
        // Checks if the dominant channel wasn't rising.
        if (!sRising) {
            reset();
            return false;
        }
        // Checks if the dominant channel stopped rising too recently.
        if (sRisingTimeout > 0) {
            sRisingTimeout -= 1;
            return false;
        }
        // Calculates amplitudes and checks if the dominant channel is the
        // highest.
        sAmplitudes = amplitudes(sLastValues, sInitialValues);
        // Calculates specific ratios and checks if the dominant channel's
        // amplitude is high enough.
        if (!ratios(sAmplitudes)) {
            reset();
            return false;
        }
        // Tests the detected values using LDA.
        if (!test(sRatios, sData)) {
            reset();
            return false;
        }
        // If the method reaches this point, detection is true.
        reset();
        return true;
    }

    private static void initial() {
        for (int x = 0; x < sRelevantChannels.length; x++) {
            sInitialValues[x] = 0;
        }
    }

    private static boolean ratios(double[] values) {
        double[] temp = {
                values[1], values[2], (values[1] + values[2]), values[3]
        };
        for (int x = 0; x < sRatios.length; x++) {
            sRatios[x] = values[0] / temp[x];
            if (values[0] < sRatios[x])
                return false;
        }
        return true;
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
}
