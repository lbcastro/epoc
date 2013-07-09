
package com.castro.epoc;

public class Expression {

    protected static double ch1;
    protected static double ch2;
    protected static double ch3;
    protected static double ch4;
    protected static double ch5;
    protected static double ch6;
    protected static double ch7;
    protected static double ch8;
    protected static double ch9;
    protected static double ch10;
    protected static double ch11;
    protected static double ch12;
    protected static double ch13;
    protected static double ch14;
    protected static double[] channels;
    protected static double average;
    protected static Events event;

    // protected Expression(double[] v) {
    // average = calcAverage(v);
    // channels = v;
    // setChannels(v);
    // }

    /**
     * Sets a variable for each channel for better differentiation between the
     * channel number and its index.
     * 
     * @param values All the channels' values
     */
    protected static void setChannels(double[] values) {
        channels = values;
        ch1 = values[0];
        ch2 = values[1];
        ch3 = values[2];
        ch4 = values[3];
        ch5 = values[4];
        ch6 = values[5];
        ch7 = values[6];
        ch8 = values[7];
        ch9 = values[8];
        ch10 = values[9];
        ch11 = values[10];
        ch12 = values[11];
        ch13 = values[12];
        ch14 = values[13];
    }

    /**
     * Procedure to check for specific parameters before detecting an event.
     * Checks if there is LDA data, if there is pre-recorded data and if the
     * provided channels values are not null.
     * 
     * @param event The specified event
     * @param values All the channels values
     * @return True if all parameters are positive
     */
    protected static boolean checkEvent(Events event, double[] values) {
        if (event.data == null) {
            return false;
        }
        if (!event.file.exists())
            return false;
        if (values == null)
            return false;
        return true;
    }

    /**
     * Calculates the average for a specific set of values.
     * 
     * @param values Provided values
     * @return Average value
     */
    protected static double calcAverage(double[] values) {
        double total = 0;
        for (int x = 0; x < values.length; x++) {
            total += values[x];
        }
        return total / values.length;
    }

}
