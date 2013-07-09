
package com.castro.epoc.expressions;

import java.io.File;

import com.castro.epoc.Events;
import com.castro.epoc.LDA;

public class Look extends Expression {

    protected static boolean check(LDA data, File file, double[] values) {
        if (data == null)
            return false;
        if (values == null)
            return false;
        if (!file.exists())
            return false;
        return true;
    }

    protected static boolean test(double[] values, LDA data) {
        int result = data.predict(values);
        if (result != 1) {
            return false;
        }
        return true;
    }

    public static LDA train(File file, int[] index) {
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
        double[][] values = new double[totalLength][2];
        int[] classes = new int[totalLength];
        for (int x = 0; x < eventLength; x++) {
            values[x][0] = data[0][x][index[0]];
            values[x][1] = data[0][x][index[1]];
            classes[x] = 1;
        }
        for (int x = eventLength; x < totalLength; x++) {
            values[x][0] = data[1][x - eventLength][index[0]];
            values[x][1] = data[1][x - eventLength][index[1]];
            classes[x] = 2;
        }
        return new LDA(values, classes, true);
    }
}
