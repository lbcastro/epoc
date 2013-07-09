
package com.castro.epoc.expressions;

import java.io.File;

import com.castro.epoc.Events;
import com.castro.epoc.LDA;

public class Wink extends Expression {

    protected static boolean check(LDA[] data, File file, double[] values) {
        if (data == null)
            return false;
        if (values == null)
            return false;
        if (!file.exists())
            return false;
        return true;
    }

    private static double[] getRatios(double[] values, int[] index) {
        double[] temp = {
                values[index[1]], values[index[2]], (values[index[1]] + values[index[2]]),
                values[index[3]]
        };
        double[] tempRatios = new double[index.length];
        for (int x = 0; x < index.length; x++) {
            tempRatios[x] = values[index[0]] / temp[x];
        }
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
                if (positives >= 3) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static double[][] testValues(double[] values) {
        double[][] temp = new double[6][2];
        temp[0][0] = values[0];
        temp[0][1] = values[1];
        temp[1][0] = values[0];
        temp[1][1] = values[2];
        temp[2][0] = values[0];
        temp[2][1] = values[3];
        temp[3][0] = values[1];
        temp[3][1] = values[2];
        temp[4][0] = values[1];
        temp[4][1] = values[3];
        temp[5][0] = values[2];
        temp[5][1] = values[3];
        return temp;
    }

    public static LDA[] train(File file, int[] index) {
        // Checks if the data files exist.
        if (!file.exists())
            return null;
        if (!Events.BASELINE.file.exists())
            return null;
        // Retrieves data from the files and checks if the data exists.
        final double[][][] data = recordings(file);
        if (data == null) {
            return null;
        }
        // Detects the length of the data retrieved and stops if the length is
        // 1. More than one dataset is needed to train LDA.
        final int eventLength = data[0].length;
        final int totalLength = data[0].length + data[1].length;
        if (eventLength <= 1 || totalLength <= 1) {
            return null;
        }
        // Defines objects to store LDA training data.
        double[][][] values = new double[6][totalLength][2];
        int[] classes = new int[totalLength];
        double[] ratios = new double[4];
        for (int x = 0; x < eventLength; x++) {
            ratios = getRatios(data[0][x], index);
            values[0][x][0] = ratios[0];
            values[0][x][1] = ratios[1];
            values[1][x][0] = ratios[0];
            values[1][x][1] = ratios[2];
            values[2][x][0] = ratios[0];
            values[2][x][1] = ratios[3];
            values[3][x][0] = ratios[1];
            values[3][x][1] = ratios[2];
            values[4][x][0] = ratios[1];
            values[4][x][1] = ratios[3];
            values[5][x][0] = ratios[2];
            values[5][x][1] = ratios[3];
            classes[x] = 1;
        }
        for (int x = eventLength; x < totalLength; x++) {
            int y = x - eventLength;
            ratios = getRatios(data[1][y], index);
            values[0][x][0] = ratios[0];
            values[0][x][1] = ratios[1];
            values[1][x][0] = ratios[0];
            values[1][x][1] = ratios[2];
            values[2][x][0] = ratios[0];
            values[2][x][1] = ratios[3];
            values[3][x][0] = ratios[1];
            values[3][x][1] = ratios[2];
            values[4][x][0] = ratios[1];
            values[4][x][1] = ratios[3];
            values[5][x][0] = ratios[2];
            values[5][x][1] = ratios[3];
            classes[x] = 2;
        }
        LDA[] temp = new LDA[6];
        for (int x = 0; x < temp.length; x++) {
            temp[x] = new LDA(values[x], classes, true);
        }
        return temp;
    }
}
