
package com.castro.epoc.expressions;

import java.io.File;
import java.util.Arrays;

import com.castro.epoc.Events;
import com.castro.epoc.Files;
import com.castro.epoc.LDA;
import com.castro.epoc.Training;

public class LookLeft extends Look {

    private static LDA sData;

    private static File sFile;

    private static double[] sCurrentValues = new double[2];

    private static double[] sLastValues = new double[2];

    private static double[] sInitialValues = new double[2];

    private static double[] sAmplitudes = new double[2];

    private static int[] sChannelsIndex = new int[2];

    private static double[] sRatios = new double[2];

    private static boolean sRising = false;

    private static int sRisingTimeout = 0;

    private static int sRecentTimeout = 0;

    private static final int sRisingTimeoutMax = 3;

    private static final int sDominantChannel = 2;

    private static final int sOppositeChannel = 13;

    private static final int[] sRelevantChannels = {
            sDominantChannel, sOppositeChannel
    };

    private static double[] sLookLeftRanges = new double[2];

    public static boolean detect(double[] values) {
        if (!check(sData, sFile, values)) {
            return false;
        }
        // TODO: Refactor this method, make it return the amplitudes.
        sCurrentValues = separate(values, indexes(sRelevantChannels));
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
        if (!ratios(sAmplitudes)) {
            reset();
            return false;
        }
        // if (!test(sLastValues, sData)) {
        // reset();
        // return false;
        // }
        System.out.println("LOOKLEFT");
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
    private static boolean ratios(double[] amplitudes) {
        setRanges();
        System.out.println(Arrays.toString(amplitudes));
        // TODO: Adjust this condition.
        sRatios[0] = Math.pow(amplitudes[0] / amplitudes[1], 2) * (amplitudes[0] - amplitudes[1]);
        System.out.println(sRatios[0]);
        System.out.println(Arrays.toString(sLookLeftRanges));
        if (Double.isNaN(sRatios[0])) {
            return false;
        }
        // TODO: Test this section.
        if (sRatios[0] < sLookLeftRanges[1]) {
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
        sFile = Files.sdCard("lookleft");
        sData = train(sFile, indexes(sRelevantChannels));
    }

    public static void setRanges() {
        sLookLeftRanges = Files.getRanges(Files.sdCard("lookleft"));
    }
}
