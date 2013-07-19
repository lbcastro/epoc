
package com.castro.epoc;

import static com.castro.epoc.Global.CHANNELS;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.castro.epoc.expressions.WinkLeft;
import com.castro.epoc.expressions.WinkRight;

public class Keyboard extends Fragment implements PropertyChangeListener {
    private View mFragmentView;

    private TextView[] mTextViewArray = new TextView[27];

    private int mCurrentRow = 2;

    private int mTotalRows = 3;

    private int mTotalColumns = 9;

    private int[] mRowNumber = new int[9];

    private int[] mColumnNumber = new int[3];

    private int mCurrentColumn = 8;

    private boolean mColumnSelected = false;

    private boolean mRowSelected = false;

    private int mKeySelected;

    private int mColorRegular = Color.WHITE;

    private int mColorTemporary = Color.rgb(69, 173, 168);

    private int mColorSelected = Color.rgb(241, 212, 175);

    private int mColorFinal = Color.rgb(224, 142, 121);

    private boolean mStarted = false;

    private boolean mDeleteLast = false;

    private double[] mCorrectedBuffer = new double[CHANNELS];

    private static int sLeftCount = 0;

    private static int sRightCount = 0;

    public Keyboard() {
    }

    // Changes the color of the specified column and resets the color of the
    // previous column. Also stores the index of the current column.
    private void changeColumn(int column, int color) {
        for (int x = 0; x < mTotalRows; x++) {
            int current = column + (x * mTotalColumns);
            for (int y = 0; y < mTotalColumns; y++) {
                if (mRowNumber[y] == current) {
                    if (color != mColorRegular) {
                        mTextViewArray[current].setTextColor(mColorFinal);
                    } else if (!mColumnSelected) {
                        mTextViewArray[current].setTextColor(mColorSelected);
                    }
                }
            }
            mColumnNumber[x] = current;
        }
    }

    // Changes the color of the specified row and resets the color of the
    // previous row. Also stores the index of the current row.
    private void changeRow(int row, int color) {
        for (int x = 0; x < mTotalColumns; x++) {
            mTextViewArray[x + (row * mTotalColumns)].setTextColor(color);
            mRowNumber[x] = x + (row * mTotalColumns);
        }
    }

    // Hovers the next column.
    private void nextColumn() {
        mCurrentColumn += 1;
        if (mCurrentColumn > mTotalColumns - 1) {
            mCurrentColumn = 0;
        }
        changeColumn(mCurrentColumn, mColorTemporary);
        changeColumn((mCurrentColumn == 0 ? mTotalColumns - 1 : mCurrentColumn - 1), mColorRegular);
    }

    // Hovers the next row.
    private void nextRow() {
        mCurrentRow += 1;
        if (mCurrentRow > mTotalRows - 1) {
            mCurrentRow = 0;
        }
        changeRow(mCurrentRow, mColorTemporary);
        changeRow((mCurrentRow == 0 ? mTotalRows - 1 : mCurrentRow - 1), mColorRegular);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.activity_keyboard, container, false);
        RelativeLayout layout = (RelativeLayout)mFragmentView.findViewById(R.id.keyboard_relative2);
        for (int x = 0; x < 27; x++) {
            mTextViewArray[x] = (TextView)layout.getChildAt(x);
        }
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
        if (mStarted) {
            refreshColors();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Connection.getInstance().setDataListener(this, false);
    }

    private void refreshColors() {
        mCurrentRow -= 1;
        mCurrentColumn -= 1;
        nextRow();
        nextColumn();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName() == "levels") {
            mCorrectedBuffer = (double[])event.getNewValue();
            if (WinkLeft.detect(mCorrectedBuffer)) {
                ToastManager.create("WINKLEFT " + sLeftCount, getActivity());
                System.out.println("WINKLEFT - " + sLeftCount);
                sLeftCount += 1;
                selectCurrent();
            }
            if (WinkRight.detect(mCorrectedBuffer)) {
                ToastManager.create("WINKRIGHT " + sRightCount, getActivity());
                System.out.println("WINKRIGHT - " + sRightCount);
                sRightCount += 1;
                selectNext();
            }
        }
    }

    /**
     * When nothing is selected, enables deletion. If hovering a row, selects
     * it. If hovering a column, selects the current character. Triggers
     * typeSelection().
     */
    private void selectCurrent() {
        if (!mStarted) {
            if (!mRowSelected && !mColumnSelected) {
                mDeleteLast = true;
                typeSelection();
            } else {
                return;
            }
        } else if (!mRowSelected) {
            mRowSelected = true;
            changeRow(mCurrentRow, mColorSelected);
            mStarted = false;
        } else {
            mColumnSelected = true;
            changeRow(mCurrentRow, mColorRegular);
            changeColumn(mCurrentColumn, mColorRegular);
            typeSelection();
            mRowSelected = false;
            mColumnSelected = false;
        }
    }

    /**
     * Hovers the next row if nothing is selected (nextRow()). Hovers the next
     * column if a row is selected (nextColumn()).
     */
    private void selectNext() {
        if (!mStarted) {
            mStarted = true;
        }
        if (!mRowSelected) {
            nextRow();
        } else if (!mColumnSelected) {
            nextColumn();
        }
    }

    // Types the currently selected character into the activity text box. If
    // deletion is activated, deletes the last typed character.
    private void typeSelection() {
        EditText mEditText = (EditText)mFragmentView.findViewById(R.id.keyboard_edittext);
        if (mDeleteLast) {
            String string = mEditText.getText().toString();
            if (string.length() > 0) {
                mEditText.setText(string.substring(0, string.length() - 1));
            }
            mDeleteLast = false;
        } else {
            mKeySelected = mCurrentColumn + mCurrentRow * mTotalColumns;
            if (mKeySelected == 26) {
                mEditText.append(" ");
            } else {
                mEditText.append(mTextViewArray[mKeySelected].getText());
            }
        }
        mRowSelected = false;
        mColumnSelected = false;
        mStarted = false;
        mCurrentRow = 2;
        mCurrentColumn = 8;
    }
}
