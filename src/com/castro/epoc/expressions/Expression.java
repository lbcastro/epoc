
package com.castro.epoc.expressions;

import java.io.File;

import com.castro.epoc.Events;
import com.castro.epoc.Files;

public class Expression {

    protected static double[] amplitudes(double[] last, double[] initial) {
        double[] temp = new double[initial.length];
        for (int x = 0; x < temp.length; x++) {
            temp[x] = last[x] - initial[x];
        }
        return temp;
    }

    protected static int[] indexes(int[] channels) {
        int[] temp = new int[channels.length];
        for (int x = 0; x < channels.length; x++) {
            temp[x] = channels[x] - 1;
        }
        return temp;
    }

    protected static double[][][] recordings(File file) {
        double[][][] temp = new double[2][][];
        temp[0] = Files.getRecordings(file);
        temp[1] = Files.getRecordings(Events.BASELINE.getFile());
        if (temp[0] == null || temp[1] == null) {
            return null;
        }
        return temp;
    }

    protected static double[] separate(double[] values) {
        double[] temp = new double[values.length];
        for (int x = 0; x < values.length; x++) {
            temp[x] = values[x];
        }
        return temp;
    }

    protected static double[] separate(double[] values, int[] index) {
        double[] temp = new double[index.length];
        for (int x = 0; x < index.length; x++) {
            temp[x] = values[index[x]];
        }
        return temp;
    }
}
