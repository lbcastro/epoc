
package com.castro.epoc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.view.Menu;

public class Battery {

    private Battery() {
    }

    private static int sLevel = 0;
    private static int sPercentage;
    private static boolean sUpdated = false;

    public static boolean isUpdated() {
        return sUpdated;
    }

    public static int getLevel() {
        return sLevel;
    }

    private static int getDrawable(int level) {
        switch (level) {
            case 0:
                return R.drawable.b0;
            case 1:
                return R.drawable.b1;
            case 2:
                return R.drawable.b2;
            case 3:
                return R.drawable.b3;
            case 4:
                return R.drawable.b4;
            default:
                return R.drawable.b0;
        }
    }

    /**
     * Finds the battery level from a specified byte value.
     * 
     * @param battery Battery level byte
     * @return Battery level on a 0-100 scale
     */
    private static int calculatePercentage(int battery) {
        if (battery >= 248)
            return 100;
        switch (battery) {
            case 247:
                return 99;
            case 246:
                return 97;
            case 245:
                return 93;
            case 244:
                return 89;
            case 243:
                return 85;
            case 242:
                return 82;
            case 241:
                return 77;
            case 240:
                return 72;
            case 239:
                return 66;
            case 238:
                return 62;
            case 237:
                return 55;
            case 236:
                return 46;
            case 235:
                return 32;
            case 234:
                return 20;
            case 233:
                return 12;
            case 232:
                return 6;
            case 231:
                return 4;
            case 230:
                return 3;
            case 229:
                return 2;
            case 228:
            case 227:
            case 226:
                return 1;
            default:
                return 0;
        }
    }

    /**
     * Changes the battery icon according to its level.
     * 
     * @param battery Battery level
     */
    public static void setLevel(Object source, int encryptedLevel,
            PropertyChangeListener mainListener) {
        sPercentage = calculatePercentage(encryptedLevel);
        sUpdated = false;
        if (sPercentage >= 75 && sLevel != 4) {
            sLevel = 4;
            sUpdated = true;
        }
        else if (sPercentage >= 50 && sLevel != 3) {
            sLevel = 3;
            sUpdated = true;
        }
        else if (sPercentage >= 25 && sLevel != 2) {
            sLevel = 2;
            sUpdated = true;
        }
        else if (sPercentage >= 5 && sLevel != 1) {
            sLevel = 1;
            sUpdated = true;
        }
        else if (sLevel != 0) {
            sLevel = 0;
            sUpdated = true;
        }
        if (sUpdated) {
            mainListener.propertyChange(new PropertyChangeEvent(source, "battery", null, sLevel));
        }
    }

    public static void changeDrawable(Menu menu) {
        if (menu == null) return;
        menu.findItem(R.id.action_battery).setIcon(getDrawable(sLevel));
    }
}
