
package com.castro.epoc;

import static com.castro.epoc.Global.BANDS_MAX;
import static com.castro.epoc.Global.CHANNELS;
import static com.castro.epoc.Global.FFTREP;
import static com.castro.epoc.Global.FFTSIZE;
import static com.castro.epoc.Global.SAMPLES;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.complex.Complex;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewStyle;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class Oscillation extends Fragment implements PropertyChangeListener {
    private static class Frequency {
        private static List<Double> sEegList;

        private static List<Double[]> sFourierList = new ArrayList<Double[]>();

        private static Complex[] sFourierResult;

        private static double[] sFrequencies;

        private static double sImaginary;

        private static double[] sMagnitudes = new double[FFTSIZE];

        private static Complex[] sMagnitudesList;

        private static double[] sMultiplier = new double[FFTSIZE];

        private static double sReal;

        private static double[] sSignalArray;

        private static DoubleFFT_1D sTransf;

        private static double[] sWindow;

        private static double[] sWindowedMagnitudes;

        // Converts a specified value to the decibel scale.
        private static double calcDb(double value) {
            return 20 * Math.log10(Math.pow(value, 2));
        }

        // TODO: Select a single channel to display oscillation results.
        // TODO: Display all results divided according to the electrode's
        // position.

        /**
         * Calculates the frequencies for the specified array of data. Useful
         * when a large sample is used, converting the frequencies to integrals.
         * 
         * @param values Array with the calculated frequencies
         * @return Array with the usable frequencies (half the sample size)
         */
        private static double[] calcFreqs(double[] values) {
            sFrequencies = new double[SAMPLES / 2];
            for (int x = 0; x < sFrequencies.length; x++) {
                for (int y = (FFTREP * x) + 1; y < y + FFTREP; y++) {
                    if (y >= values.length / 2)
                        break;
                    sFrequencies[x] += values[y];
                }
                sFrequencies[x] = calcDb(sFrequencies[x]);
            }
            return sFrequencies;
        }

        // Defines Hann window's values.
        private static void calcHann() {
            for (int x = 0; x < FFTSIZE; x++) {
                sMultiplier[x] = 0.5 * (1 - Math.cos(2 * Math.PI * x / FFTSIZE));
            }
        }

        // Calculates magnitudes for the recorded data.
        private static double[] calcMagnitude() {
            // Performs FFT.
            sEegList = getList();
            sWindowedMagnitudes = setHann(sEegList);
            sMagnitudesList = fftForward(sWindowedMagnitudes);
            // Calculates the magnitude for the provided list using M =
            // sqrt(real^2
            // + imag^2).
            for (int x = 0; x < sMagnitudesList.length; x++) {
                sReal = Math.pow(sMagnitudesList[x].getReal(), 2);
                sImaginary = Math.pow(sMagnitudesList[x].getImaginary(), 2);
                sWindowedMagnitudes[x] = Math.sqrt(sReal + sImaginary);
            }
            return sWindowedMagnitudes;
        }

        /**
         * Performs forward FFT using a specified array of values.
         * 
         * @param values Double array of EEG data
         * @return Complex array with the FFT of the specified data
         */
        private static Complex[] fftForward(double[] values) {
            sTransf = new DoubleFFT_1D(values.length);
            sSignalArray = new double[values.length * 2];
            // Fills an array with the provided values.
            for (int i = 0; i < values.length; i++) {
                sSignalArray[2 * i] = values[i];
                sSignalArray[2 * i + 1] = 0.0;
            }
            // Performs forward FFT.
            sTransf.complexForward(sSignalArray);
            // Creates a complex array to store the values.
            sFourierResult = new Complex[values.length];
            // Stores the calculated FFT in the complex array.
            for (int i = 0; i < values.length; i++) {
                sFourierResult[i] = new Complex(sSignalArray[2 * i], sSignalArray[2 * i + 1]);
            }
            return sFourierResult;
        }

        private static List<Double[]> getFourierList() {
            return sFourierList;
        }

        private static double[] getFrequencies() {
            sMagnitudes = calcMagnitude();
            return calcFreqs(sMagnitudes);
        }

        // Returns a list of all recorded data.
        private static List<Double> getList() {
            List<Double> l;
            List<Double> l2;
            l = getList(0);
            for (int x = 1; x < 14; x++) {
                l2 = getList(x);
                for (int y = 0; y < l.size(); y++) {
                    l.set(y, l.get(y) + l2.get(y));
                }
            }
            return l;
        }

        // Gets the EEG data of a specific channel.
        private static List<Double> getList(int channel) {
            List<Double> fftTemp = new ArrayList<Double>();
            for (int x = 0; x < FFTSIZE; x++) {
                fftTemp.add(sFourierList.get(x)[channel]);
            }
            return fftTemp;
        }

        /**
         * Applies Hann window to a specified array of values.
         * 
         * @param raw Values to apply the window
         * @return Windowed values
         */
        private static double[] setHann(List<Double> raw) {
            sWindow = new double[FFTSIZE];
            for (int i = 0; i < raw.size(); i++) {
                sWindow[i] = sMultiplier[i] * raw.get(i);
            }
            return sWindow;
        }
    }

    private ArrayList<double[]> mAllRecordings = new ArrayList<double[]>();

    private double mAlpha;

    private GraphViewData[] mBandsData = new GraphViewData[BANDS_MAX];

    private final File mBandsFile = Files.sdCard("bands");

    private GraphViewSeries mBandsSeries;

    private double[] mBandsValues;

    private double mBeta;

    private double[] mCorrectedBuffer = new double[CHANNELS];

    private double mDelta;

    private View mFragmentView;

    private GraphView mGraphView;

    private boolean mRecording = false;

    private double mTheta;

    public Oscillation() {
    }

    /**
     * Adds a specified array of data to the recorded values list.
     * 
     * @param values Array of EEG data to be recorded
     */
    public void addList(double[] values) {
        Double[] temp = new Double[values.length];
        for (int x = 0; x < values.length; x++) {
            temp[x] = values[x];
        }
        Frequency.getFourierList().add(temp);
        if (getListSize() > FFTSIZE) {
            Frequency.getFourierList().clear();
        }
    }

    private void buildGraphic() {
        final LinearLayout graph = (LinearLayout)mFragmentView.findViewById(R.id.oscillation_graph);
        mGraphView = new BarGraphView(getActivity(), "");
        graph.addView(mGraphView);
        graph.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecording)
                    stop();
                else
                    start();
            }
        });
        initData();
    }

    // Connection connection;
    private int getListSize() {
        return Frequency.getFourierList().size();
    }

    /** Defines the concentration graphic specific parameters. */
    public void initData() {
        if (mGraphView == null)
            return;
        mBandsSeries = new GraphViewSeries("bands",
                new GraphViewStyle(Color.rgb(105, 210, 231), 5), mBandsData);
        for (int x = 0; x < BANDS_MAX; x++) {
            mBandsData[x] = new GraphViewData(x, 0.0);
        }
        mGraphView.addSeries(mBandsSeries);
        mGraphView.setManualYAxisBounds(0.0, 0.0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.activity_oscillation, container, false);
        buildGraphic();
        Frequency.calcHann();
        return mFragmentView;
    }

    @Override
    public void onPause() {
        super.onPause();
        Connection.getInstance().setDataListener(this, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Connection.getInstance().setDataListener(this, true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        Connection.getInstance().setDataListener(this, false);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName() == "levels") {
            mCorrectedBuffer = (double[])event.getNewValue();
            work(mCorrectedBuffer);
        }
    }

    /**
     * Stores a specified array of data on a temporary list so it can be later
     * averaged and saved to a file.
     * 
     * @param bands Double array with one sample of brain bands values
     */
    private void record(double[] values) {
        for (int y = 0; y < values.length; y++) {
            values[y] = Math.round(values[y] * 100.0) / 100.0;
        }
        mAllRecordings.add(values);
    }

    /** Initiates data recording. */
    private void start() {
        if (!mBandsFile.exists())
            Files.createFile(mBandsFile, "root");
        mRecording = true;
    }

    /** Stops data recording. */
    private void stop() {
        Files.addValues(mBandsFile, mAllRecordings);
        mRecording = false;
    }

    /**
     * Calculates brain bands values using FFT and then calculates pre-defined
     * brain states and changes the correspondent seekbars.
     * 
     * @param data GraphView data storage array
     * @param bands Brain bands values
     */
    public void work(double[] values) {
        addList(values);
        if (getListSize() % 64 == 0) {
            ((ProgressBar)mFragmentView.findViewById(R.id.oscillation_refresh))
                    .setProgress(getListSize() / 64);
        }
        if (getListSize() >= FFTSIZE) {
            mBandsValues = Frequency.getFrequencies();
            mBandsData = new GraphViewData[BANDS_MAX];
            double max = 0;
            double min = 1000;
            for (int x = 0; x < BANDS_MAX; x++) {
                if (mBandsValues[x] > max) {
                    max = mBandsValues[x];
                }
                if (mBandsValues[x] < min) {
                    min = mBandsValues[x];
                }
                mBandsData[x] = new GraphViewData(x, mBandsValues[x]);
                if (x < 4) {
                    mDelta += mBandsValues[x];
                } else if (x < 8) {
                    mTheta += mBandsValues[x];
                } else if (x < 13) {
                    mAlpha += mBandsValues[x];
                } else if (x < 30) {
                    mBeta += mBandsValues[x];
                }
            }
            mDelta /= 4;
            mTheta /= 4;
            mAlpha /= 5;
            mBeta /= 17;
            ((ProgressBar)mFragmentView.findViewById(R.id.oscillation_progress_1))
                    .setProgress((int)mDelta);
            ((ProgressBar)mFragmentView.findViewById(R.id.oscillation_progress_2))
                    .setProgress((int)mTheta);
            ((ProgressBar)mFragmentView.findViewById(R.id.oscillation_progress_3))
                    .setProgress((int)mAlpha);
            ((ProgressBar)mFragmentView.findViewById(R.id.oscillation_progress_4))
                    .setProgress((int)mBeta);
            mBandsSeries.resetData(mBandsData);
            mGraphView.setManualYAxisBounds(max * 1.1, min * 0.9);
            record(mBandsValues);
        }
    }
}
