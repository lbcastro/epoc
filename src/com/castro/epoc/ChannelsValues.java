
package com.castro.epoc;

import android.graphics.Color;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewStyle;

public class ChannelsValues {

    public final static int[] F3_BITS = {
            10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7
    };;

    public final static int[] FC5_BITS = {
            28, 29, 30, 31, 16, 17, 18, 19, 20, 21, 22, 23, 8, 9
    };

    public final static int[] AF3_BITS = {
            46, 47, 32, 33, 34, 35, 36, 37, 38, 39, 24, 25, 26, 27
    };

    public final static int[] F7_BITS = {
            48, 49, 50, 51, 52, 53, 54, 55, 40, 41, 42, 43, 44, 45
    };

    public final static int[] T7_BITS = {
            66, 67, 68, 69, 70, 71, 56, 57, 58, 59, 60, 61, 62, 63
    };

    public final static int[] P7_BITS = {
            84, 85, 86, 87, 72, 73, 74, 75, 76, 77, 78, 79, 64, 65
    };

    public final static int[] O1_BITS = {
            102, 103, 88, 89, 90, 91, 92, 93, 94, 95, 80, 81, 82, 83
    };

    public final static int[] O2_BITS = {
            140, 141, 142, 143, 128, 129, 130, 131, 132, 133, 134, 135, 120, 121
    };

    public final static int[] P8_BITS = {
            158, 159, 144, 145, 146, 147, 148, 149, 150, 151, 136, 137, 138, 139
    };

    public final static int[] T8_BITS = {
            160, 161, 162, 163, 164, 165, 166, 167, 152, 153, 154, 155, 156, 157
    };

    public final static int[] F8_BITS = {
            178, 179, 180, 181, 182, 183, 168, 169, 170, 171, 172, 173, 174, 175
    };

    public final static int[] AF4_BITS = {
            196, 197, 198, 199, 184, 185, 186, 187, 188, 189, 190, 191, 176, 177
    };

    public final static int[] FC6_BITS = {
            214, 215, 200, 201, 202, 203, 204, 205, 206, 207, 192, 193, 194, 195
    };

    public final static int[] F4_BITS = {
            216, 217, 218, 219, 220, 221, 222, 223, 208, 209, 210, 211, 212, 213
    };

    public static double F3_AV = 8700.0;

    public static double FC5_AV = 8700.0;

    public static double AF3_AV = 8700.0;

    public static double F7_AV = 8700.0;

    public static double T7_AV = 8700.0;

    public static double P7_AV = 8700.0;

    public static double O1_AV = 8700.0;

    public static double O2_AV = 8700.0;

    public static double P8_AV = 8700.0;

    public static double T8_AV = 8700.0;

    public static double F8_AV = 8700.0;

    public static double AF4_AV = 8700.0;

    public static double FC6_AV = 8700.0;

    public static double F4_AV = 8700.0;

    public static GraphViewSeries F3_SERIES = null;

    public static GraphViewSeries FC5_SERIES = null;

    public static GraphViewSeries AF3_SERIES = null;

    public static GraphViewSeries F7_SERIES = null;

    public static GraphViewSeries T7_SERIES = null;

    public static GraphViewSeries P7_SERIES = null;

    public static GraphViewSeries O1_SERIES = null;

    public static GraphViewSeries O2_SERIES = null;

    public static GraphViewSeries P8_SERIES = null;

    public static GraphViewSeries T8_SERIES = null;

    public static GraphViewSeries F8_SERIES = null;

    public static GraphViewSeries AF4_SERIES = null;

    public static GraphViewSeries FC6_SERIES = null;

    public static GraphViewSeries F4_SERIES = null;

    public final static GraphViewStyle F3_COLOR = new GraphViewStyle(Color.rgb(78, 205, 196), 2);

    public final static GraphViewStyle FC5_COLOR = new GraphViewStyle(Color.rgb(199, 244, 100), 2);

    public final static GraphViewStyle AF3_COLOR = new GraphViewStyle(Color.rgb(255, 107, 107), 2);

    public final static GraphViewStyle F7_COLOR = new GraphViewStyle(Color.rgb(196, 77, 88), 2);

    public final static GraphViewStyle T7_COLOR = new GraphViewStyle(Color.rgb(85, 98, 112), 2);

    public final static GraphViewStyle P7_COLOR = new GraphViewStyle(Color.rgb(209, 242, 165), 2);

    public final static GraphViewStyle O1_COLOR = new GraphViewStyle(Color.rgb(239, 250, 180), 2);

    public final static GraphViewStyle O2_COLOR = new GraphViewStyle(Color.rgb(255, 196, 140), 2);

    public final static GraphViewStyle P8_COLOR = new GraphViewStyle(Color.rgb(255, 159, 128), 2);

    public final static GraphViewStyle T8_COLOR = new GraphViewStyle(Color.rgb(245, 105, 145), 2);

    public final static GraphViewStyle F8_COLOR = new GraphViewStyle(Color.rgb(0, 160, 176), 2);

    public final static GraphViewStyle AF4_COLOR = new GraphViewStyle(Color.rgb(237, 201, 81), 2);

    public final static GraphViewStyle FC6_COLOR = new GraphViewStyle(Color.rgb(235, 104, 65), 2);

    public final static GraphViewStyle F4_COLOR = new GraphViewStyle(Color.rgb(106, 74, 60), 2);

    public static boolean F3_ACTIVE = false;

    public static boolean FC5_ACTIVE = false;

    public static boolean AF3_ACTIVE = false;

    public static boolean F7_ACTIVE = true;

    public static boolean T7_ACTIVE = false;

    public static boolean P7_ACTIVE = false;

    public static boolean O1_ACTIVE = false;

    public static boolean O2_ACTIVE = false;

    public static boolean P8_ACTIVE = false;

    public static boolean T8_ACTIVE = false;

    public static boolean F8_ACTIVE = false;

    public static boolean AF4_ACTIVE = false;

    public static boolean FC6_ACTIVE = false;

    public static boolean F4_ACTIVE = false;

    public static int F3_QUAL = Color.WHITE;

    public static int FC5_QUAL = Color.WHITE;

    public static int AF3_QUAL = Color.WHITE;

    public static int F7_QUAL = Color.WHITE;

    public static int T7_QUAL = Color.WHITE;

    public static int P7_QUAL = Color.WHITE;

    public static int O1_QUAL = Color.WHITE;

    public static int O2_QUAL = Color.WHITE;

    public static int P8_QUAL = Color.WHITE;

    public static int T8_QUAL = Color.WHITE;

    public static int F8_QUAL = Color.WHITE;

    public static int AF4_QUAL = Color.WHITE;

    public static int FC6_QUAL = Color.WHITE;

    public static int F4_QUAL = Color.WHITE;

    public static GraphViewData[] AF3_DATA;

    public static GraphViewData[] F7_DATA;

    public static GraphViewData[] F3_DATA;

    public static GraphViewData[] FC5_DATA;

    public static GraphViewData[] T7_DATA;

    public static GraphViewData[] P7_DATA;

    public static GraphViewData[] O1_DATA;

    public static GraphViewData[] O2_DATA;

    public static GraphViewData[] P8_DATA;

    public static GraphViewData[] T8_DATA;

    public static GraphViewData[] FC6_DATA;

    public static GraphViewData[] F4_DATA;

    public static GraphViewData[] F8_DATA;

    public static GraphViewData[] AF4_DATA;

    private ChannelsValues() {
    }
}
