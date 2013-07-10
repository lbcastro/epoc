
package com.castro.epoc;

import static com.castro.epoc.Global.CHANNELS;

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

    private double[] mTopValue = new double[CHANNELS];

    private double[] mBaselineAverage = new double[CHANNELS];

    private int mBaselineCounter = 0;

    private ArrayList<double[]> mBaselineList = new ArrayList<double[]>();

    public Training(Events event) {
        mActiveEvent = event;
        ActionBarManager.setState("Recording");
        if (!event.getFile().exists()) {
            Files.createFile(event.getFile(), "root");
        }
    }

    public void addValues(double[] values) {
        if (mActiveEvent == Events.BASELINE) {
            for (int x = 0; x < CHANNELS; x++) {
                mBaselineAverage[x] += values[x];
            }
            mBaselineCounter += 1;
            if (mBaselineCounter >= 128) {
                for (int y = 0; y < CHANNELS; y++) {
                    mBaselineAverage[y] /= mBaselineCounter;
                    mBaselineAverage[y] = Math.round(mBaselineAverage[y] * 100.0) / 100.0;
                }
                mBaselineList.add(mBaselineAverage);
                mBaselineCounter = 0;
                mBaselineAverage = new double[CHANNELS];
            }
        } else if (values[mActiveEvent.getRelevant()[0] - 1] > mTopValue[mActiveEvent.getRelevant()[0] - 1]) {
            for (int x = 0; x < CHANNELS; x++) {
                mTopValue[x] = values[x];
            }
        }
    }

    private void recordEvent() {
        final Document document = Files.getDoc(mActiveEvent.getFile());
        final Node root = document.getFirstChild();
        final Element recording = document.createElement("recording");
        final String string = Arrays.toString(mTopValue).replaceAll("\\[|\\]", "");
        root.appendChild(recording);
        recording.setTextContent(string);
        Files.saveChanges(document, mActiveEvent.getFile());
    }

    public void recordValues() {
        if (mActiveEvent == Events.BASELINE) {
            Files.addValues(mActiveEvent.getFile(), mBaselineList);
        } else {
            recordEvent();
            mActiveEvent.setLda();
        }
    }
}
