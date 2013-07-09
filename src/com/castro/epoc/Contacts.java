package com.castro.epoc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Contacts fragment class. Initiates required objects and specifies functions
 * to navigate through the contacts list.
 */
public class Contacts extends Fragment implements PropertyChangeListener {

	private final float mActiveAlpha = 1f;
	private final float mDefaultAlpha = 0.5f;
	private View mFragmentView;
	private int sActiveContact = 0;
	private ProgressDialog mCallDialog;
	private ImageView[] mContactsPictures = new ImageView[5];
	private TextView[] mContactsNames = new TextView[5];
	private TextView mGyroText;
	private int[] mGyro = new int[2];
	private int mRecentTimeout = 0;
	private final int RECENT_TIMEOUT = 35;
	private final String[] mContactsList = { "Barack Obama", "Angelina Jolie",
			"Steve Jobs", "Tom Cruise", "Marilyn Monroe" };

	public Contacts() {
	}

	/**
	 * Changes the alpha of the contacts pictures and names according to the
	 * selection.
	 **/
	private void changeAlpha(int last) {
		mContactsPictures[sActiveContact].setAlpha(mActiveAlpha);
		mContactsNames[sActiveContact].setAlpha(mActiveAlpha);
		mContactsPictures[last].setAlpha(mDefaultAlpha);
		mContactsNames[last].setAlpha(mDefaultAlpha);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFragmentView = inflater.inflate(R.layout.activity_contacts, container,
				false);
		RelativeLayout layout = (RelativeLayout) mFragmentView
				.findViewById(R.id.contacts_relative2);
		for (int x = 0; x < 10; x++) {
			if (x < 5) {
				mContactsPictures[x] = (ImageView) layout.getChildAt(x);
			} else {
				mContactsNames[x - 5] = (TextView) layout.getChildAt(x);
			}
		}
		mGyroText = (TextView) mFragmentView.findViewById(R.id.gyrotext);
		return mFragmentView;
	}

	@Override
	public void onPause() {
		super.onPause();
		Connection.getInstance().removeChangeListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Connection.getInstance().getConnection()) {
			Connection.getInstance().addChangeListener(this);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		Connection.getInstance().removeChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName() == "gyro") {
			mGyro = (int[]) event.getNewValue();
			mGyroText.setText("x: " + mGyro[0] + " y: " + mGyro[1]);
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
			mCallDialog = ProgressDialog.show(mFragmentView.getContext(), "",
					"Calling " + mContactsList[sActiveContact] + "...");
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
}
