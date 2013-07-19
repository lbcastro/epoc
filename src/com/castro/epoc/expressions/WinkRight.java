
package com.castro.epoc.expressions;

import java.io.File;
import java.util.Arrays;

import com.castro.epoc.Files;
import com.castro.epoc.LDA;

public class WinkRight extends Wink {

    private static LDA[] sData = new LDA[6];

    private static final int c = 2;

    private static int[] sChannelsIndex = new int[c];

    private static double[] sAmplitudes = new double[c];

    private static double[] sCurrentValues = new double[c];

    private static double[] sInitialValues = new double[c];

    private static double[] sLastValues = new double[c];

    private static double[] sRatios = new double[c];

    private static boolean sRising = false;

    private static int sRisingTimeout = 0;

    private static int sRecentTimeout = 0;

    private static File sFile;

    private static final int sRisingTimeoutMax = 3;

    private static final int sDominantChannel = 13;

    private static final int sSecondaryChannel = 11;

    private static final int sTertiaryChannel = 14;

    private static final int sOppositeChannel = 2;

    private static double[] sRanges = new double[2];

    // private static final int[] sRelevantChannels = {
    // sDominantChannel, sSecondaryChannel, sTertiaryChannel, sOppositeChannel
    // };
    private static final int[] sRelevantChannels = {
            sDominantChannel, sOppositeChannel
    };

    /**
     * Right wink detection function. This function is fed with a list of values
     * every time a sample is acquired. It then checks for specific features in
     * order to determine if an event occurred.
     * 
     * @param v Double array with every channels' values.
     * @return Returns true if a right wink was detected.
     */
    public static boolean detect(double[] values) {
        // Check for missing files, LDA data or input values.
        if (!check(sData, sFile, values)) {
            return false;
        }
        // TODO: Refactor this method, make it return the amplitudes.
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
        if (sAmplitudes[1] < 1 && sAmplitudes[1] > -1) {
            reset();
            return false;
        }

        // Calculates specific ratios and checks if the dominant channel's
        // amplitude is high enough.
        if (!ratios(sAmplitudes)) {
            reset();
            return false;
        }
        // // Tests the detected values using LDA.
        // if (!test(sRatios, sData)) {
        // reset();
        // return false;
        // }
        // If the method reaches this point, detection is true.
        reset();

        return true;
    }

    public static File getFile() {
        return sFile;
    }

    public static int[] getRelevant() {
        return indexes(sRelevantChannels);
    }

    public static int getTimeout() {
        return sRecentTimeout;
    }

    private static void initial() {
        for (int x = 0; x < sRelevantChannels.length; x++) {
            sInitialValues[x] = 0;
        }
    }

    // TODO: Refactor this method to the expression class. Make it return the
    // ratio.
    private static boolean ratios(double[] values) {
         setRanges();
        sRatios[0] = Math.pow(values[0] / values[1], 2) * (values[0] - values[1]);
        if (Double.isNaN(sRatios[0])) {
            return false;
        }
        if (sRatios[0] < sRanges[1]) {
            return false;
        }
        return true;
    }

    private static void reset() {
        sRising = false;
        sRisingTimeout = 0;
        sRatios = new double[1];
        sLastValues = sCurrentValues;
        initial();
    }

    public static void setLda() {
        sChannelsIndex = indexes(sRelevantChannels);
        sFile = Files.sdCard("winkright");
        sData = train(sFile, sChannelsIndex);
    }

    public static void setRanges() {
        sRanges = Files.getRanges(Files.sdCard("winkright"));
    }

    // private static boolean ratios(double[] values) {
    // // double[] temp = {
    // // values[1], values[2], (values[1] + values[2]), values[3]
    // // };
    // double[] temp = {
    // values[1], values[2]
    // };
    // for (int x = 0; x < sRatios.length; x++) {
    // sRatios[x] = values[0] / temp[x];
    // if (values[0] < sRatios[x]) {
    // return false;
    // }
    // }
    // return true;
    // }
}
