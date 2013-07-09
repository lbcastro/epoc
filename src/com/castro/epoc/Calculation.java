package com.castro.epoc;

import static com.castro.epoc.Global.CHANNELS;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.castro.epoc.expressions.LookLeft;
import com.castro.epoc.expressions.LookRight;

public class Calculation extends Fragment implements PropertyChangeListener {

	private final int mCorrectColor = Color.rgb(131, 175, 155);
	private final int mWrongColor = Color.RED;
	private final int mDefaultColor = Color.WHITE;
	private View mFragmentView;
	private TextView mResultText;
	private TextView mLevelText;
	private TextView mFirstAnswerText;
	private TextView mSecondAnswerText;
	private double[] mCorrectedBuffer = new double[CHANNELS];
	private int mCorrectSide;
	private int mActiveLevel = 1;
	private boolean mCalculationActive = false;
	private boolean mCalculationInitiated = false;
	private int mLastResult = 0;
	private int mCalculationsCounter = 0;
	private boolean mEyesNavigationWasActive = false;
	private final static Handler handler = new Handler();
	private Runnable startCalculation = new Runnable() {

		@Override
		public void run() {
			newCalculation();
			if (mCalculationInitiated)
				handler.postDelayed(startCalculation, 4000);
		}
	};

	public Calculation() {
	}

	private void changeCenter(int i) {
		mResultText.setText(Integer.toString(i));
		mResultText.setTextColor(mDefaultColor);
	}

	private void changeFirst(String s) {
		mFirstAnswerText.setText(s);
	}

	private void changeSecond(String s) {
		mSecondAnswerText.setText(s);
	}

	public void checkSide(int side) {
		if (!mCalculationActive || !mCalculationInitiated)
			return;
		mResultText.setTextColor(side == mCorrectSide ? mCorrectColor
				: mWrongColor);
		mCalculationActive = false;
	}

	/**
	 * Calculates a true or a false division operation for a given result.
	 * 
	 * @param result
	 *            The resulting value of the operation
	 * @param correct
	 *            Specifies if the operation should be correct or false
	 * @return A string with the calculated operation
	 */
	private String findDiv(int result, boolean correct, int level) {
		Random random = new Random();
		int range = 20 * level;
		int first = getRange(random, range);
		int second;
		if (correct) {
			while (first % result != 0) {
				first = random.nextInt(range) + 1;
			}
			second = first / result;
		} else {
			;
			while (first / (second = random.nextInt(range) + 1) == result) {
				second = random.nextInt(range);
			}
		}
		return first + " / " + second;
	}

	/**
	 * Calculates a true or a false multiplication operation for a given result.
	 * 
	 * @param result
	 *            The resulting value of the operation
	 * @param correct
	 *            Specifies if the operation should be correct or false
	 * @return A string with the calculated operation
	 */
	private String findMult(int result, boolean correct, int level) {
		Random random = new Random();
		int range = 10 * level;
		int first = getRange(random, range);
		int second;
		if (correct) {
			while (first % result != 0 && result % first != 0) {
				first = random.nextInt(range) + 1;
			}
			second = (first % result == 0 ? first / result : result / first);
			if (first * second != result)
				return findMult(result, correct, level);
		} else {
			while (first * (second = random.nextInt(range)) == result)
				second = random.nextInt(range);
		}
		return first + " x " + second;
	}

	private String findSub(int result, boolean correct, int level) {
		Random random = new Random();
		int range = 20 * level;
		int first = random.nextInt(range) + 1;
		int second;
		if (correct) {
			while (result > first) {
				first = random.nextInt(range) + 1;
			}
			second = first - result;
		} else {
			while (first - (second = random.nextInt(range) + 1) == result) {
				second = random.nextInt(range) + 1;
			}
		}
		return first + " - " + Math.abs(second);
	}

	private String findSum(int result, boolean correct, int level) {
		Random random = new Random();
		int range = 10 * level;
		int first = random.nextInt(range) + 1;
		int second;
		if (correct) {
			if (result < first) {
				second = first;
				first = result - first;
			} else
				second = result - first;
		} else {
			while (first + (second = random.nextInt(range) + 1) == result) {
				second = random.nextInt(range) + 1;
			}
		}
		return first + " + " + second;
	}

	private int getRange(Random random, int range) {
		int min = (range / mActiveLevel) * (mActiveLevel - 1);
		int i = random.nextInt(range - min) + min + 1;
		return i;
	}

	// Defines all used textviews and their properties.
	private void initiateTextViews() {
		mResultText = (TextView) mFragmentView
				.findViewById(R.id.calculation_result);
		mLevelText = (TextView) mFragmentView
				.findViewById(R.id.calculation_level);
		mFirstAnswerText = (TextView) mFragmentView
				.findViewById(R.id.calculation_first);
		mSecondAnswerText = (TextView) mFragmentView
				.findViewById(R.id.calculation_second);
		mResultText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				toggleStart();
			}
		});
		mLevelText.setText("Level: " + mActiveLevel);
		mLevelText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				levelUp();
			}
		});
	}

	public boolean isActive() {
		return mCalculationInitiated;
	}

	private void levelUp() {
		if (mActiveLevel < 3)
			mActiveLevel += 1;
		else
			mActiveLevel = 1;
		mLevelText = (TextView) mFragmentView
				.findViewById(R.id.calculation_level);
		mLevelText.setText("Level: " + mActiveLevel);
	}

	private void newCalculation() {
		Random random = new Random();
		int result;
		do {
			result = getRange(random, 10 * mActiveLevel);
		} while (result == mLastResult);
		mLastResult = result;
		String[] operations = new String[2];
		for (int x = 0; x < 2; x++) {
			boolean b = (x == 0 ? true : false);
			int type = random.nextInt(4);
			switch (type) {
			case 0:
				operations[x] = findSum(result, b, mActiveLevel);
				break;
			case 1:
				operations[x] = findSub(result, b, mActiveLevel);
				break;
			case 2:
				operations[x] = findMult(result, b, mActiveLevel);
				break;
			case 3:
				operations[x] = findDiv(result, b, mActiveLevel);
				break;
			}
		}
		mCorrectSide = random.nextInt(2);
		changeFirst(operations[mCorrectSide]);
		changeSecond(operations[(mCorrectSide == 0 ? 1 : 0)]);
		changeCenter(result);
		if (!mCalculationActive)
			mCalculationActive = true;
		mCalculationsCounter += 1;
		if (mCalculationsCounter >= 10) {
			mCalculationsCounter = 0;
			levelUp();
			if (mActiveLevel > 3) {
				toggleStart();
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFragmentView = inflater.inflate(R.layout.activity_calculation,
				container, false);
		initiateTextViews();
		return mFragmentView;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName() == "update") {
			mCorrectedBuffer = (double[]) event.getNewValue();
			if (LookLeft.detect(mCorrectedBuffer)) {
				checkSide(0);
			}
			if (LookRight.detect(mCorrectedBuffer)) {
				checkSide(1);
			}
		}
	}

	public void setLevel(int level) {
		mActiveLevel = level;
	}

	private void toggleStart() {
		if (!mCalculationInitiated) {
			mEyesNavigationWasActive = EyesNavigation.getActive();
			EyesNavigation.setActive(false);
			mCalculationInitiated = true;
			handler.post(startCalculation);
		} else {
			mCalculationInitiated = false;
			EyesNavigation.setActive(mEyesNavigationWasActive);
			handler.removeCallbacks(startCalculation);
			mResultText.setText("Start");
			mResultText.setTextColor(Color.WHITE);
			mFirstAnswerText.setText("..........");
			mSecondAnswerText.setText("..........");
		}
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
}
