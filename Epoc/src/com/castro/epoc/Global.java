
package com.castro.epoc;

public final class Global {

    private Global() {
    }

    public static final int SAMPLES = 128;
    public static final int FILTER = SAMPLES * 2;
    public static final int FFTREP = 8;
    public static final int FFTSIZE = SAMPLES * FFTREP;
    public static final int SAMPLING = 1000 / SAMPLES;
    public static final int PACKET_SIZE = 32;
    public static final int Y_RANGE = 800;
    public static final int CHANNELS = 14;
    public static final int RECENT_TIMEOUT = 50;
    public static final int BANDS_MAX = 31;
    public static final int KEY_VIEW = 0;
    public static final int CONT_VIEW = 1;
    public static final int CALC_VIEW = 2;
    public static final int CONC_VIEW = 3;
    public static final int CONFIG_VIEW = 4;
    public static final String[] eventsList = {
            "Baseline", "Wink left", "Wink right",
            "Look left", "Look right"
    };
}