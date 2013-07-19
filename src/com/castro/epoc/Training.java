
package com.castro.epoc;

import static com.castro.epoc.Global.CHANNELS;
import static com.castro.epoc.Global.SAMPLES;

import java.util.ArrayList;
import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Training {

    public static void updateLda() {
        for (Events e : Events.values()) {
            if (e != Events.BASELINE) {
                e.setLda();
            }
        }
    }

    private Events mActiveEvent;

    // private double[] mTopValue = new double[CHANNELS];

    private double[] mBaselineAverage = new double[CHANNELS];

    private int mBaselineCounter = 0;

    // private double[] mCurrentValues;

    private double[] mLastValues;

    private double[] mInitialValues;

    private double[] mTempAmplitudes;

    private double[] mAmplitudes;

    private int[] mChannelsIndex;

    private boolean mIsRising = false;

    private int mRisingTimeout = 0;

    private int mRisingTimeoutMax = 4;

    private ArrayList<double[]> mBaselineList = new ArrayList<double[]>();

    public Training(Events event) {
        mActiveEvent = event;
        mChannelsIndex = event.getRelevant();
        mAmplitudes = new double[CHANNELS];
        ActionBarManager.setState("Recording");
        if (!event.getFile().exists()) {
            Files.createFile(event.getFile(), "root");
        }
    }

    // Saves each received value to a temporary list to be saved later.
    // While recording baseline values, averages each second worth of data.
    // For events data, only saves the highest recorded peak (amplitude).
    public void addValues(double[] values) {
        if (mActiveEvent == Events.BASELINE) {
            for (int x = 0; x < CHANNELS; x++) {
                mBaselineAverage[x] += values[x];
            }
            mBaselineCounter += 1;
            if (mBaselineCounter >= SAMPLES) {
                for (int y = 0; y < CHANNELS; y++) {
                    mBaselineAverage[y] /= mBaselineCounter;
                    mBaselineAverage[y] = Math.round(mBaselineAverage[y] * 100.0) / 100.0;
                }
                mBaselineList.add(mBaselineAverage);
                mBaselineCounter = 0;
                mBaselineAverage = new double[CHANNELS];
            }
        }

        else {
            findPeak(values);
        }
        // else if (values[mActiveEvent.getRelevant()[0]] >
        // mTopValue[mActiveEvent.getRelevant()[0]]) {
        // for (int x = 0; x < CHANNELS; x++) {
        // mTopValue[x] = values[x];
        // }
        // }
    }

    // Saves event related data to a file, creating the XML structure if needed.
    private void recordEvent() {
        final Document document = Files.getDoc(mActiveEvent.getFile());
        final Node root = document.getFirstChild();
        final Element recording = document.createElement("recording");
        double[] average = Files.getAverage(mActiveEvent.getFile());
        mAmplitudes = normalize(mAmplitudes, average);
        final double[] range = findRange();
        final Element max = (Element)document.getElementsByTagName("max").item(0);
        final Element min = (Element)document.getElementsByTagName("min").item(0);
        max.setTextContent(Double.toString(range[0]));
        min.setTextContent(Double.toString(range[1]));
        final String string = Arrays.toString(mAmplitudes).replaceAll("\\[|\\]", "");
        root.appendChild(recording);
        recording.setTextContent(string);
        Files.saveChanges(document, mActiveEvent.getFile());
    }

    // Approximates each value to a given average.
    private double[] normalize(double[] amplitudes, double[] average) {
        double[] temp = new double[amplitudes.length];
        if (average == null) {
            return amplitudes;
        }
        for (int x = 0; x < amplitudes.length; x++) {
            temp[x] = (amplitudes[x] + average[x]) / 2;
            temp[x] = Math.round(temp[x] * 100.0) / 100.0;
        }
        return temp;
    }

    // Saves temporary data to a file. Behaves differently for basline or events
    // data.
    public void recordValues() {
        if (mActiveEvent == Events.BASELINE) {
            Files.addValues(mActiveEvent.getFile(), mBaselineList);
        } else {
            if (mAmplitudes == null) {
                return;
            }
            recordEvent();
            mActiveEvent.setLda();
            mActiveEvent.setRanges();
            // mActiveEvent.refineLda();
        }
    }

    private double[] convert(double[] values) {
        double[] temp = new double[values.length];
        for (int x = 0; x < values.length; x++) {
            temp[x] = values[x];
        }
        return temp;
    }

    // Attempts to find a peak value for the given event's dominant channel.
    private void findPeak(double[] values) {

        if (values == null) {
            return;
        }

        if (mLastValues == null) {
            mLastValues = convert(values);
            return;
        }
        if (values[mChannelsIndex[0]] > mLastValues[mChannelsIndex[0]]) {
            if (!mIsRising) {
                mIsRising = true;
                mInitialValues = convert(mLastValues);
            }
            mRisingTimeout = mRisingTimeoutMax;
            mLastValues = convert(values);
            return;
        }

        if (!mIsRising) {
            mLastValues = convert(values);
            return;
        }

        if (mRisingTimeout > 0) {
            mRisingTimeout -= 1;
            return;
        }

        mTempAmplitudes = amplitudes(mLastValues, mInitialValues);
        if (mTempAmplitudes[mChannelsIndex[0]] > mAmplitudes[mChannelsIndex[0]]) {
            mAmplitudes = mTempAmplitudes;
        }
        mRisingTimeout = 0;
        mIsRising = false;
    }

    // Finds the ratio dominant/opposite channel for the active event.
    private double findRatio(double[] values) {
        double temp = Math.pow(values[mChannelsIndex[0]] / values[mChannelsIndex[1]], 2)
                * (values[mChannelsIndex[0]] - values[mChannelsIndex[1]]);
        return temp;
    }

    // Attempts to find the limits of possible values for the ratio
    // dominant/opposite channel, for the active event.
    private double[] findRange() {
        double[][] recordings = Files.getRecordings(mActiveEvent.getFile());
        if (recordings == null) {
            return null;
        }
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        for (int x = 0; x < recordings.length; x++) {
            double ratio = findRatio(recordings[x]);
            max = Math.max(max, ratio);
            min = Math.min(min, ratio);
        }
        double[] range = {
                max, min
        };
        return range;
    }

    // Calculates amplitudes for a given set of initial and final values.
    private double[] amplitudes(double[] last, double[] initial) {
        double[] temp = new double[initial.length];
        for (int x = 0; x < temp.length; x++) {
            temp[x] = last[x] - initial[x];
        }
        return temp;
    }
}
