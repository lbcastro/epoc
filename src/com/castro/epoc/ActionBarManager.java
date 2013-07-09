
package com.castro.epoc;

import java.util.Locale;

import android.app.ActionBar;

public class ActionBarManager {

    private static ActionBar sActionBar;
    private static String sState;
    private static String sMessage;
    private static double sLoss = 0;
    private static String sUser;

    public static void setActionBar(ActionBar a) {
        sActionBar = a;
    }

    public static void setMessage(String s) {
        sMessage = s;
        update();
    }

    public static void setUser(String s) {
        sUser = s;
        update();
    }

    public static void clearMessage() {
        sMessage = null;
        update();
    }

    public static void setState(String s) {
        sState = s.toLowerCase(Locale.UK);
        update();
    }

    public static void setTitle(String s) {
        sActionBar.setTitle(s);
    }

    private static void update() {
        sActionBar.setSubtitle("Status: " + sState
                + (sUser != null ? "     Profile: " + sUser : "")
                + (Connection.getInstance().getConnection() ? "     Loss: " + sLoss + "%" : "") +
                (sMessage != null ? "     -     " + sMessage : ""));
    }

    public static void setLoss(double d) {
        sLoss = d;
        update();
    }
}
