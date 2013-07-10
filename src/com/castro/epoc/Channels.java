
package com.castro.epoc;

import static com.castro.epoc.ChannelsValues.AF3_ACTIVE;
import static com.castro.epoc.ChannelsValues.AF3_AV;
import static com.castro.epoc.ChannelsValues.AF3_BITS;
import static com.castro.epoc.ChannelsValues.AF3_COLOR;
import static com.castro.epoc.ChannelsValues.AF3_DATA;
import static com.castro.epoc.ChannelsValues.AF3_QUAL;
import static com.castro.epoc.ChannelsValues.AF3_SERIES;
import static com.castro.epoc.ChannelsValues.AF4_ACTIVE;
import static com.castro.epoc.ChannelsValues.AF4_AV;
import static com.castro.epoc.ChannelsValues.AF4_BITS;
import static com.castro.epoc.ChannelsValues.AF4_COLOR;
import static com.castro.epoc.ChannelsValues.AF4_DATA;
import static com.castro.epoc.ChannelsValues.AF4_QUAL;
import static com.castro.epoc.ChannelsValues.AF4_SERIES;
import static com.castro.epoc.ChannelsValues.F3_ACTIVE;
import static com.castro.epoc.ChannelsValues.F3_AV;
import static com.castro.epoc.ChannelsValues.F3_BITS;
import static com.castro.epoc.ChannelsValues.F3_COLOR;
import static com.castro.epoc.ChannelsValues.F3_DATA;
import static com.castro.epoc.ChannelsValues.F3_QUAL;
import static com.castro.epoc.ChannelsValues.F3_SERIES;
import static com.castro.epoc.ChannelsValues.F4_ACTIVE;
import static com.castro.epoc.ChannelsValues.F4_AV;
import static com.castro.epoc.ChannelsValues.F4_BITS;
import static com.castro.epoc.ChannelsValues.F4_COLOR;
import static com.castro.epoc.ChannelsValues.F4_DATA;
import static com.castro.epoc.ChannelsValues.F4_QUAL;
import static com.castro.epoc.ChannelsValues.F4_SERIES;
import static com.castro.epoc.ChannelsValues.F7_ACTIVE;
import static com.castro.epoc.ChannelsValues.F7_AV;
import static com.castro.epoc.ChannelsValues.F7_BITS;
import static com.castro.epoc.ChannelsValues.F7_COLOR;
import static com.castro.epoc.ChannelsValues.F7_DATA;
import static com.castro.epoc.ChannelsValues.F7_QUAL;
import static com.castro.epoc.ChannelsValues.F7_SERIES;
import static com.castro.epoc.ChannelsValues.F8_ACTIVE;
import static com.castro.epoc.ChannelsValues.F8_AV;
import static com.castro.epoc.ChannelsValues.F8_BITS;
import static com.castro.epoc.ChannelsValues.F8_COLOR;
import static com.castro.epoc.ChannelsValues.F8_DATA;
import static com.castro.epoc.ChannelsValues.F8_QUAL;
import static com.castro.epoc.ChannelsValues.F8_SERIES;
import static com.castro.epoc.ChannelsValues.FC5_ACTIVE;
import static com.castro.epoc.ChannelsValues.FC5_AV;
import static com.castro.epoc.ChannelsValues.FC5_BITS;
import static com.castro.epoc.ChannelsValues.FC5_COLOR;
import static com.castro.epoc.ChannelsValues.FC5_DATA;
import static com.castro.epoc.ChannelsValues.FC5_QUAL;
import static com.castro.epoc.ChannelsValues.FC5_SERIES;
import static com.castro.epoc.ChannelsValues.FC6_ACTIVE;
import static com.castro.epoc.ChannelsValues.FC6_AV;
import static com.castro.epoc.ChannelsValues.FC6_BITS;
import static com.castro.epoc.ChannelsValues.FC6_COLOR;
import static com.castro.epoc.ChannelsValues.FC6_DATA;
import static com.castro.epoc.ChannelsValues.FC6_QUAL;
import static com.castro.epoc.ChannelsValues.FC6_SERIES;
import static com.castro.epoc.ChannelsValues.O1_ACTIVE;
import static com.castro.epoc.ChannelsValues.O1_AV;
import static com.castro.epoc.ChannelsValues.O1_BITS;
import static com.castro.epoc.ChannelsValues.O1_COLOR;
import static com.castro.epoc.ChannelsValues.O1_DATA;
import static com.castro.epoc.ChannelsValues.O1_QUAL;
import static com.castro.epoc.ChannelsValues.O1_SERIES;
import static com.castro.epoc.ChannelsValues.O2_ACTIVE;
import static com.castro.epoc.ChannelsValues.O2_AV;
import static com.castro.epoc.ChannelsValues.O2_BITS;
import static com.castro.epoc.ChannelsValues.O2_COLOR;
import static com.castro.epoc.ChannelsValues.O2_DATA;
import static com.castro.epoc.ChannelsValues.O2_QUAL;
import static com.castro.epoc.ChannelsValues.O2_SERIES;
import static com.castro.epoc.ChannelsValues.P7_ACTIVE;
import static com.castro.epoc.ChannelsValues.P7_BITS;
import static com.castro.epoc.ChannelsValues.P7_COLOR;
import static com.castro.epoc.ChannelsValues.P7_DATA;
import static com.castro.epoc.ChannelsValues.P7_QUAL;
import static com.castro.epoc.ChannelsValues.P7_SERIES;
import static com.castro.epoc.ChannelsValues.P8_ACTIVE;
import static com.castro.epoc.ChannelsValues.P8_AV;
import static com.castro.epoc.ChannelsValues.P8_BITS;
import static com.castro.epoc.ChannelsValues.P8_COLOR;
import static com.castro.epoc.ChannelsValues.P8_DATA;
import static com.castro.epoc.ChannelsValues.P8_QUAL;
import static com.castro.epoc.ChannelsValues.P8_SERIES;
import static com.castro.epoc.ChannelsValues.T7_ACTIVE;
import static com.castro.epoc.ChannelsValues.T7_AV;
import static com.castro.epoc.ChannelsValues.T7_BITS;
import static com.castro.epoc.ChannelsValues.T7_COLOR;
import static com.castro.epoc.ChannelsValues.T7_DATA;
import static com.castro.epoc.ChannelsValues.T7_QUAL;
import static com.castro.epoc.ChannelsValues.T7_SERIES;
import static com.castro.epoc.ChannelsValues.T8_ACTIVE;
import static com.castro.epoc.ChannelsValues.T8_AV;
import static com.castro.epoc.ChannelsValues.T8_BITS;
import static com.castro.epoc.ChannelsValues.T8_COLOR;
import static com.castro.epoc.ChannelsValues.T8_DATA;
import static com.castro.epoc.ChannelsValues.T8_QUAL;
import static com.castro.epoc.ChannelsValues.T8_SERIES;
import static com.castro.epoc.Global.FILTER;

import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewStyle;

public enum Channels {
    AF3(AF3_BITS, AF3_AV, AF3_SERIES, AF3_COLOR, AF3_ACTIVE, AF3_QUAL, AF3_DATA), F7(F7_BITS,
            F7_AV, F7_SERIES, F7_COLOR, F7_ACTIVE, F7_QUAL, F7_DATA), F3(F3_BITS, F3_AV, F3_SERIES,
            F3_COLOR, F3_ACTIVE, F3_QUAL, F3_DATA), FC5(FC5_BITS, FC5_AV, FC5_SERIES, FC5_COLOR,
            FC5_ACTIVE, FC5_QUAL, FC5_DATA), T7(T7_BITS, T7_AV, T7_SERIES, T7_COLOR, T7_ACTIVE,
            T7_QUAL, T7_DATA), P7(P7_BITS, T7_AV, P7_SERIES, P7_COLOR, P7_ACTIVE, P7_QUAL, P7_DATA), O1(
            O1_BITS, O1_AV, O1_SERIES, O1_COLOR, O1_ACTIVE, O1_QUAL, O1_DATA), O2(O2_BITS, O2_AV,
            O2_SERIES, O2_COLOR, O2_ACTIVE, O2_QUAL, O2_DATA), P8(P8_BITS, P8_AV, P8_SERIES,
            P8_COLOR, P8_ACTIVE, P8_QUAL, P8_DATA), T8(T8_BITS, T8_AV, T8_SERIES, T8_COLOR,
            T8_ACTIVE, T8_QUAL, T8_DATA), FC6(FC6_BITS, FC6_AV, FC6_SERIES, FC6_COLOR, FC6_ACTIVE,
            FC6_QUAL, FC6_DATA), F4(F4_BITS, F4_AV, F4_SERIES, F4_COLOR, F4_ACTIVE, F4_QUAL,
            F4_DATA), F8(F8_BITS, F8_AV, F8_SERIES, F8_COLOR, F8_ACTIVE, F8_QUAL, F8_DATA), AF4(
            AF4_BITS, AF4_AV, AF4_SERIES, AF4_COLOR, AF4_ACTIVE, AF4_QUAL, AF4_DATA);

    private final int[] mBits;

    private double mAverage;

    private GraphViewSeries mSeries;

    private final GraphViewStyle mStyle;

    private boolean mActive;

    private int mQuality;

    private GraphViewData[] mData;

    Channels(int[] bits, double average, GraphViewSeries series, GraphViewStyle color,
            boolean active, int qual, GraphViewData[] data) {
        this.mBits = bits;
        this.mAverage = average;
        this.mSeries = series;
        this.mStyle = color;
        this.mActive = active;
        this.mQuality = qual;
        this.mData = data;
    }

    public boolean getActive() {
        return this.mActive;
    }

    public double getAverage() {
        return this.mAverage;
    }

    public int[] getBits() {
        return this.mBits;
    }

    public GraphViewData[] getData() {
        return this.mData;
    }

    public int getQuality() {
        return this.mQuality;
    }

    public GraphViewSeries getSeries() {
        return this.mSeries;
    }

    public GraphViewStyle getStyle() {
        return this.mStyle;
    }

    public void setActive(boolean b) {
        mActive = b;
    }

    public void setAverage(double a) {
        mAverage = ((mAverage * (FILTER - 1)) + a) / FILTER;
    }

    public void setData(GraphViewData[] d) {
        this.mData = d;
    }

    public void setQuality(int c) {
        mQuality = c;
    }

    public void setSeries(GraphViewSeries s) {
        mSeries = s;
    }
}
