
package com.castro.epoc.expressions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import com.castro.epoc.Events;
import com.castro.epoc.Files;
import com.castro.epoc.LDA;
import com.castro.epoc.Profiles;

public class Wink extends Expression {

    protected static boolean check(LDA[] data, File file, double[] values) {
        if (Profiles.getInstance().getActiveUser() == null) {
            return false;
        }
        if (data == null) {
            return false;
        }
        if (values == null) {
            return false;
        }
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    private static double[] getRatios(double[] values, int[] index) {
        // double[] temp = {
        // values[index[1]], values[index[2]], (values[index[1]] +
        // values[index[2]]),
        // values[index[3]]
        // };
        // double[] temp = {
        // values[index[0]], values[index[1]]
        // };
        double[] tempRatios = new double[index.length];
        // for (int x = 0; x < index.length; x++) {
        tempRatios[0] = Math.pow(values[index[0]] / values[index[1]], 2);
        // }
        return tempRatios;
    }

    protected static boolean test(double[] values, LDA[] data) {
        if (data == null) {
            return false;
        }
        double[][] temp = testValues(values);
        int positives = 0;
        for (int x = 0; x < temp.length; x++) {
            if (data[x].predict(temp[x]) == 1) {
                positives += 1;
                if (positives > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static double[][] testValues(double[] values) {
        double[][] temp = new double[1][2];
        temp[0][0] = values[0];
        temp[0][1] = values[1];
        // temp[1][0] = values[0];
        // temp[1][1] = values[2];
        // temp[2][0] = values[0];
        // temp[2][1] = values[3];
        // temp[3][0] = values[1];
        // temp[3][1] = values[2];
        // temp[4][0] = values[1];
        // temp[4][1] = values[3];
        // temp[5][0] = values[2];
        // temp[5][1] = values[3];
        return temp;
    }

    public static LDA[] train(File file, int[] index) {
        if (Profiles.getInstance().getActiveUser() == null) {
            return null;
        }
        // Checks if the data files exist.
        if (!file.exists()) {
            return null;
        }
        if (!Events.BASELINE.getFile().exists()) {
            return null;
        }
        // Retrieves data from the files and checks if the data exists.
        final double[][][] data = recordings(file);
        if (data == null) {
            return null;
        }
        // Detects the length of the data retrieved and stops if the length is
        // one or less. More than one dataset is needed to train LDA.
        final int eventLength = data[0].length;
        final int totalLength = data[0].length + data[1].length;
        if (eventLength <= 1 || totalLength <= 1) {
            return null;
        }
        // Defines objects to store LDA training data.
        double[][][] values = new double[1][totalLength][2];
        int[] classes = new int[totalLength];
        double[] ratios = new double[1];
        for (int x = 0; x < eventLength; x++) {
            ratios = getRatios(data[0][x], index);
            values[0][x][0] = ratios[0];
            values[0][x][1] = data[0][x][index[0]] - data[0][x][index[1]];
            // values[0][x][0] = ratios[0];
            // values[0][x][1] = ratios[1];
            // values[1][x][0] = ratios[0];
            // values[1][x][1] = ratios[2];
            // values[2][x][0] = ratios[0];
            // values[2][x][1] = ratios[3];
            // values[3][x][0] = ratios[1];
            // values[3][x][1] = ratios[2];
            // values[4][x][0] = ratios[1];
            // values[4][x][1] = ratios[3];
            // values[5][x][0] = ratios[2];
            // values[5][x][1] = ratios[3];
            classes[x] = 1;
        }
        for (int x = eventLength; x < totalLength; x++) {
            int y = x - eventLength;
            ratios = getRatios(data[1][y], index);
            // values[0][x][0] = ratios[0];
            // values[0][x][1] = ratios[1];
            // values[1][x][0] = ratios[0];
            // values[1][x][1] = ratios[2];
            // values[2][x][0] = ratios[0];
            // values[2][x][1] = ratios[3];
            // values[3][x][0] = ratios[1];
            // values[3][x][1] = ratios[2];
            // values[4][x][0] = ratios[1];
            // values[4][x][1] = ratios[3];
            // values[5][x][0] = ratios[2];
            // values[5][x][1] = ratios[3];
            values[0][x][0] = ratios[0];
            values[0][x][1] = data[1][y][index[0]] - data[1][y][index[1]];
            // values[1][x][0] = 0;
            // values[1][x][1] = 0;
            // values[2][x][0] = 0;
            // values[2][x][1] = 0;
            // values[3][x][0] = 0;
            // values[3][x][1] = 0;
            // values[4][x][0] = 0;
            // values[4][x][1] = 0;
            // values[5][x][0] = 0;
            // values[5][x][1] = 0;
            classes[x] = 2;
        }
        LDA[] temp = new LDA[1];
        for (int x = 0; x < temp.length; x++) {
            temp[x] = new LDA(values[x], classes, true);
        }
        return temp;
    }

    public static void refineRecordings(Events e) {
        double[][] recordings = Files.getRecordings(e.getFile());
        if (recordings.length <= 1) {
            return;
        }
        double[] average = Files.getAverage(e.getFile());
        if (average == null) {
            return;
        }
        double[][] baselineTemp = Files.getRecordings(Events.BASELINE.getFile());
        double[][][] temp = new double[2][baselineTemp.length + 1][];
        ArrayList<double[]> refined = new ArrayList<double[]>();
        temp[0][0] = average;
        temp[1] = baselineTemp;
        if (temp[0] == null || temp[1] == null) {
            return;
        }
        // final int eventLength = temp[0].length;
        // final int totalLength = temp[0].length + temp[1].length;
        // double[][][] values = new double[6][totalLength][2];
        // int[] classes = new int[totalLength];
        // double[] ratios = new double[4];
        // for (int x = 0; x < 1; x++) {
        // ratios = getRatios(temp[0][0], e.getRelevant());
        // values[0][x][0] = ratios[0];
        // values[0][x][1] = ratios[1];
        // values[1][x][0] = ratios[0];
        // values[1][x][1] = ratios[2];
        // values[2][x][0] = ratios[0];
        // values[2][x][1] = ratios[3];
        // values[3][x][0] = ratios[1];
        // values[3][x][1] = ratios[2];
        // values[4][x][0] = ratios[1];
        // values[4][x][1] = ratios[3];
        // values[5][x][0] = ratios[2];
        // values[5][x][1] = ratios[3];
        // classes[x] = 1;
        // }
        // for (int x = eventLength; x < totalLength; x++) {
        // int y = x - eventLength;
        // ratios = getRatios(temp[1][y], e.getRelevant());
        // values[0][x][0] = ratios[0];
        // values[0][x][1] = ratios[1];
        // values[1][x][0] = ratios[0];
        // values[1][x][1] = ratios[2];
        // values[2][x][0] = ratios[0];
        // values[2][x][1] = ratios[3];
        // values[3][x][0] = ratios[1];
        // values[3][x][1] = ratios[2];
        // values[4][x][0] = ratios[1];
        // values[4][x][1] = ratios[3];
        // values[5][x][0] = ratios[2];
        // values[5][x][1] = ratios[3];
        // classes[x] = 2;
        // }
        // LDA[] lda = new LDA[6];
        // for (int x = 0; x < lda.length; x++) {
        // lda[x] = new LDA(values[x], classes, true);
        // }
        // for (int x = 0; x < recordings.length; x++) {
        //
        // for (int y = 0; y < 14; y++) {
        // recordings[x][y] = (recordings[x][y] + average[y]) / 2;
        // }
        // refined.add(recordings[x]);
        // // if (test(recordings[x], lda)) {
        // // refined.add(recordings[x]);
        // // }
        // }
        refined.add(average);
        e.getFile().delete();
        Files.createFile(e.getFile(), "root");
        Files.addValues(e.getFile(), refined);
    }
}
