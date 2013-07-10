
package com.castro.epoc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import static com.castro.epoc.Global.CONT_PAGE;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Contacts extends Fragment implements PropertyChangeListener {
    private ProgressDialog mCallDialog;

    private TextView[] mContactsNames = new TextView[5];

    private ImageView[] mContactsPictures = new ImageView[5];

    private View mFragmentView;

    private TextView mGyroText;

    private final float mDefaultAlpha = 0.5f;

    private final float mActiveAlpha = 1f;

    private final int RECENT_TIMEOUT = 35;

    private int[] mGyro = new int[2];

    private int[] mLastGyro = new int[2];

    private int mRecentTimeout = 0;

    private int sActiveContact = 0;

    private boolean mActive = false;

    private final String[] mContactsList = {
            "Barack Obama", "Angelina Jolie", "Steve Jobs", "Tom Cruise", "Marilyn Monroe"
    };

    public Contacts() {
    }

    // Changes the alpha of the contacts pictures and names according to the
    // selection.
    private void changeAlpha(int last) {
        mContactsPictures[sActiveContact].setAlpha(mActiveAlpha);
        mContactsNames[sActiveContact].setAlpha(mActiveAlpha);
        mContactsPictures[last].setAlpha(mDefaultAlpha);
        mContactsNames[last].setAlpha(mDefaultAlpha);
    }

    private void disableGyro() {
        mActive = false;
        mGyroText.setText("Touch to enable");
    }

    private void enableGyro() {
        mActive = true;
        mGyroText.setText("Retrieving gyroscope data");
    }

    private void initContacts() {
        RelativeLayout layout = (RelativeLayout)mFragmentView.findViewById(R.id.contacts_relative2);
        for (int x = 0; x < 10; x++) {
            if (x < 5) {
                mContactsPictures[x] = (ImageView)layout.getChildAt(x);
            } else {
                mContactsNames[x - 5] = (TextView)layout.getChildAt(x);
            }
        }
    }

    private void initGyro() {
        mGyroText = (TextView)mFragmentView.findViewById(R.id.gyrotext);
        disableGyro();
        mGyroText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mActive) {
                    disableGyro();
                } else {
                    enableGyro();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.activity_contacts, container, false);
        initContacts();
        initGyro();
        return mFragmentView;
    }

    @Override
    public void onPause() {
        super.onPause();
        Connection.getInstance().setGyroListener(this, false);
        disableGyro();
    }

    @Override
    public void onResume() {
        super.onResume();
        Connection.getInstance().setGyroListener(this, true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        Connection.getInstance().setGyroListener(this, false);
        disableGyro();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName() == "gyro") {
            if (!mActive || ActiveFragment.get() != CONT_PAGE) {
                return;
            }
            mGyro = (int[])event.getNewValue();
            if (mGyro[0] != mLastGyro[0] || mGyro[1] != mLastGyro[1]) {
                mGyroText.setText("x: " + mGyro[0] + " y: " + mGyro[1]);
                mLastGyro[0] = mGyro[0];
                mLastGyro[1] = mGyro[1];
            }
            if (mRecentTimeout > 0) {
                mRecentTimeout -= 1;
                return;
            }
            if (mGyro[1] < 0) {
                selectCurrent();
                mRecentTimeout = RECENT_TIMEOUT;
            }
            if (mGyro[0] > 110 || mGyro[0] < 0) {
                selectPrevious();
                mRecentTimeout = RECENT_TIMEOUT;
            } else {
                if (mGyro[0] < 100 && mGyro[0] > 0) {
                    selectNext();
                    mRecentTimeout = RECENT_TIMEOUT;
                }
            }
        }
    }

    // Calls the selected contact.
    private void selectCurrent() {
        if (mCallDialog != null && mCallDialog.isShowing()) {
            mCallDialog.dismiss();
        } else
            mCallDialog = ProgressDialog.show(mFragmentView.getContext(), "", "Calling "
                    + mContactsList[sActiveContact] + "...");
    }

    // Selects the next contact on the list.
    private void selectNext() {
        sActiveContact += 1;
        if (sActiveContact > 4)
            sActiveContact = 0;
        final int last = (sActiveContact == 0 ? 4 : sActiveContact - 1);
        changeAlpha(last);
    }

    // Selects the previous contact on the list.
    private void selectPrevious() {
        sActiveContact -= 1;
        if (sActiveContact < 0)
            sActiveContact = 4;
        final int last = (sActiveContact == 4 ? 0 : sActiveContact + 1);
        changeAlpha(last);
    }
    // private void setChangeListener(boolean b) {
    // if (b) {
    // if (Connection.getInstance().getConnection()) {
    // Connection.getInstance().setGyroListener(this, b);
    // }
    // } else {
    // Connection.getInstance().setGyroListener(this, b);
    // }
    // }
}
