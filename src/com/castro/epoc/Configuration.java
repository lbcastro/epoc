package com.castro.epoc;

import static com.castro.epoc.Global.CHANNELS;
import static com.castro.epoc.Global.SAMPLES;
import static com.castro.epoc.Global.Y_RANGE;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Locale;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewStyle;

public class Configuration extends Fragment implements PropertyChangeListener {

	private static class Quality {

		private static int sQualityColor;
		private static int sQualityCounter;
		// Bit index for sensors quality meter.
		private static final int[] sQualityBits = { 99, 100, 101, 102, 103,
				104, 105, 106, 107, 108, 109, 110, 111, 112 };

		/**
		 * Retrieves each sensor quality. Changes the color of the label
		 * according to the quality. The quality ranges from 0 to 540, being 0
		 * the worst value. Stops if the quality didn't change.
		 * 
		 * @param buffer
		 *            Decrypted buffer with a packet of data.
		 */
		private static int getColor(int buffer) {
			if (buffer < 81) {
				return Color.rgb(255, 61, 127);
			} else if (buffer < 221) {
				return Color.rgb(255, 158, 157);
			} else if (buffer < 314) {
				return Color.rgb(218, 216, 167);
			} else if (buffer < 407) {
				return Color.rgb(127, 199, 175);
			} else {
				return Color.rgb(63, 184, 175);
			}
		}

		// Returns the the channel index for contact quality measurements.
		private static int getIndex(int i) {
			switch (i) {
			case 14:
				return 10;
			case 15:
				return 11;
			case 64:
				return 0;
			case 65:
				return 1;
			case 66:
				return 2;
			case 67:
				return 3;
			case 68:
				return 4;
			case 69:
				return 5;
			case 70:
				return 6;
			case 71:
				return 7;
			case 72:
				return 8;
			case 73:
				return 9;
			case 74:
				return 10;
			case 75:
				return 11;
			case 76:
				return 12;
			case 77:
				return 13;
			default:
				return 15;
			}
		}

		// Refreshes the display of sensors quality based on stored information.
		private static void refreshAll(ViewGroup view) {
			for (Channels c : Channels.values()) {
				CheckBox checkBox = (CheckBox) view.getChildAt(c.ordinal());
				checkBox.setTextColor(c.getQuality());
			}
		}

		/**
		 * Gets the quality of a specified sensor and displays it using a color
		 * scale.
		 * 
		 * @param counter
		 *            Packet counter to extract a specific sensor
		 * @param buffer
		 *            Buffer with the data relative to the sensor quality
		 * @param view
		 *            The checkgroup's view object
		 */
		private static void updateSensor(int counter, byte[] buffer,
				ViewGroup view) {
			if (counter < 0) {
				return;
			}
			sQualityCounter = (counter < 14 ? counter : Quality
					.getIndex(counter));
			sQualityColor = Quality.getColor(Crypt.getInstance().getLevel(
					buffer, sQualityBits));
			if (sQualityCounter > 14) {
				return;
			}
			if (Channels.values()[sQualityCounter].getQuality() == sQualityColor) {
				return;
			}
			Channels.values()[sQualityCounter].setQuality(sQualityColor);
			final CheckBox checkBox = (CheckBox) view
					.getChildAt(sQualityCounter);
			checkBox.setTextColor(sQualityColor);
		}
	};

	private Training training;
	private static Events sActiveEvent = Events.BASELINE;
	private double[] mCorrectedBuffer = new double[CHANNELS];
	private boolean mTraining;
	private boolean mLoad = false;
	private ViewGroup mCheckView;
	private GraphView mGraphView;
	private double mCounter = 0;
	private LinearLayout mGraphLayout;
	private View mFragmentView;
	private GraphViewSeries mMarkerSeries;
	private GraphViewData[] mMarkerData = new GraphViewData[] {
			new GraphViewData(0.0, 0.0), new GraphViewData(0.0, 0.0) };

	public Configuration() {
	}

	/**
	 * Adds a data point to the configuration graphic. Stops if training mode is
	 * activated.
	 * 
	 * @param corrected
	 *            Data to be added
	 * @param c
	 *            The specific channel
	 */
	private void addData(double corrected, Channels c) {
		if (mTraining) {
			return;
		}
		if (c.getActive()) {
			c.getData()[(int) mCounter] = new GraphViewData(mCounter, corrected);
			c.getSeries().resetData(c.getData());
		}
	}

	// Calls all the methods relative to building the configuration graphic.
	private void buildGraphic() {
		initGraphic();
		initGraphData();
		initButtons();
		initSpinner();
		initCheck();
		initMarker();
	}

	// Sets the enabled status of the buttons that depend on the connection
	// state.
	private void changeConnectedButtons(boolean b) {
		mFragmentView.findViewById(R.id.configuration_toggle).setEnabled(b);
		final ViewGroup checkView = (ViewGroup) mFragmentView
				.findViewById(R.id.configuration_check);
		for (int x = 0; x < 14; x++) {
			CheckBox checkBox = (CheckBox) checkView.getChildAt(x);
			checkBox.setEnabled(b);
			if (!b)
				checkBox.setTextColor(Color.WHITE);
		}
	}

	// Sets the enabled status of the buttons that depend on the existence of
	// recorded data.
	private void changeDataButtons(boolean b) {
		mFragmentView.findViewById(R.id.configuration_button_load)
				.setEnabled(b);
		mFragmentView.findViewById(R.id.configuration_button_clear).setEnabled(
				b);
	}

	/**
	 * Creates a toast with the provided text string.
	 * 
	 * @param text
	 *            Text to be displayed
	 */
	public void createToast(String text) {
		Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
		toast.show();
	}

	// Initiates all the configuration module buttons.
	private void initButtons() {
		final ToggleButton toggleButton = (ToggleButton) mFragmentView
				.findViewById(R.id.configuration_toggle);
		final Button clearButton = (Button) mFragmentView
				.findViewById(R.id.configuration_button_clear);
		final Button loadButton = (Button) mFragmentView
				.findViewById(R.id.configuration_button_load);
		// Button to toggle recording mode.
		toggleButton
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (!isChecked) {
							ActionBarManager.setState("Online");
							clearButton.setEnabled(true);
							training.recordValues();
							mTraining = false;
						} else {
							ActionBarManager.setState("Training");
							createToast("Visualization stopped to improve performance");
							clearButton.setEnabled(false);
							training = new Training(sActiveEvent);
							mTraining = true;
						}
					}
				});
		// Button to clear existing recorded data for the active event.
		clearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Files.resetData(sActiveEvent.file);
				createToast(sActiveEvent.name() + ": Recorded data cleared");
				changeDataButtons(sActiveEvent.file.exists());
			}
		});
		clearButton.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Files.resetData();
				createToast("All recorded data cleared");
				changeDataButtons(sActiveEvent.file.exists());
				return true;
			}
		});
		// Button to load previously recorded data.
		loadButton.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				if (Connection.getInstance().getConnection()) {
					mLoad = false;
					resetGraphData();
				}
				return true;
			}
		});
		loadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				loadData();
				return;
			}
		});
	}

	// Initiates the checkgroup to select the displayed channels.
	private void initCheck() {
		mCheckView = (ViewGroup) mFragmentView
				.findViewById(R.id.configuration_check);
		for (final Channels c : Channels.values()) {
			CheckBox checkBox = (CheckBox) mCheckView.getChildAt(c.ordinal());
			if (c.getActive())
				checkBox.setChecked(true);
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						c.setActive(true);
						mGraphView.addSeries(c.getSeries());
						mGraphView.invalidate();
					} else {
						c.setActive(false);
						mGraphView.removeSeries(c.getSeries());
						mGraphView.invalidate();
					}
				}
			});
		}
	}

	// Adapts the GraphView to the real data acquisition. Y_RANGE indicates the
	// positive maximum range value.
	public void initGraphData() {
		for (Channels c : Channels.values()) {
			c.setData(new GraphViewData[128]);
			c.setSeries(new GraphViewSeries(c.name(), c.getStyle(), c.getData()));
			for (int x = 0; x < 128; x++) {
				c.getData()[x] = new GraphViewData(x, 0.0);
			}
			if (c.getActive()) {
				mGraphView.addSeries(c.getSeries());
			}
		}
		mGraphView.setViewPort(0.0, 128.0);
		mGraphView.setManualYAxisBounds(Y_RANGE, -Y_RANGE);
		initMarker();
	}

	private void initGraphic() {
		mGraphLayout = (LinearLayout) mFragmentView
				.findViewById(R.id.configuration_graph);
		mGraphView = new LineGraphView(getActivity(), "") {
			@Override
			protected String formatLabel(double value, boolean isValueX) {
				if (isValueX) {
					return "";
				} else
					return super.formatLabel(value, isValueX);
			}
		};
		mGraphView.setShowLegend(true);
		mGraphView.setLegendAlign(LegendAlign.BOTTOM);
		mGraphView.setLegendWidth(80.0f);
		mGraphView.setOrientation(0);

		mGraphView.setScrollable(true);
		mGraphView.setScalable(true);
		mGraphLayout.addView(mGraphView);
	}

	// Initiates the marker series for the configuration graph.
	private void initMarker() {
		if (mMarkerSeries == null) {
			mMarkerSeries = new GraphViewSeries("Realtime", new GraphViewStyle(
					Color.DKGRAY, 1), mMarkerData);
		} else {
			mGraphView.removeSeries(mMarkerSeries);
		}
		mGraphView.addSeries(mMarkerSeries);
	}

	// Initiates the spinner to select the active event.
	private void initSpinner() {
		final Spinner spinner = (Spinner) mFragmentView
				.findViewById(R.id.configuration_spinner);
		String[] eventsArray = new String[Events.values().length];
		for (Events e : Events.values()) {
			eventsArray[e.ordinal()] = e.name();
		}
		SpinnerAdapter spinnerAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, eventsArray);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				sActiveEvent = Events.values()[position];
				changeDataButtons(sActiveEvent.file.exists());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void loadData() {
		// TODO: Stop random data.
		mLoad = true;
		changeConnectedButtons(true);
		removeMarker();
		final File f = Files.sdCard(sActiveEvent.name().toLowerCase(
				Locale.getDefault()));
		if (!f.exists()) {
			createToast("Requested file does not exist");
			return;
		}
		double[][] values = Files.getRecordings(f);
		double max = 0;
		double min = 0;
		mGraphView.setViewPort(0.0, values.length);
		for (Channels c : Channels.values()) {
			c.setData(new GraphViewData[values.length]);
			for (int x = 0; x < values.length; x++) {
				c.getData()[x] = new GraphViewData(x, values[x][c.ordinal()]);
				max = (values[x][c.ordinal()] > max ? values[x][c.ordinal()]
						: max);
				min = (values[x][c.ordinal()] < min ? values[x][c.ordinal()]
						: min);
			}
			c.getSeries().resetData(c.getData());
		}
		mGraphView.setManualYAxisBounds(max, min);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFragmentView = inflater.inflate(R.layout.activity_configuration,
				container, false);
		buildGraphic();
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
			changeConnectedButtons(true);
		} else {
			changeConnectedButtons(false);
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
		if (event.getPropertyName() == "levels") {
			if (mLoad) {
				return;
			}
			mCorrectedBuffer = (double[]) event.getNewValue();
			for (Channels c : Channels.values()) {
				addData(mCorrectedBuffer[c.ordinal()], c);
			}
			if (mTraining) {
				training.addValues(mCorrectedBuffer);
			}
			setMarker();
			mCounter += 1;
			if (mCounter >= SAMPLES) {
				mCounter = 0;
			}
		} else if (event.getPropertyName() == "buffer") {
			Quality.updateSensor((Integer) event.getOldValue(),
					(byte[]) event.getNewValue(), mCheckView);
		} else if (event.getPropertyName() == "connection") {
			if ((Boolean) event.getNewValue()) {
				Quality.refreshAll(mCheckView);
				changeConnectedButtons(true);
			} else {
				changeConnectedButtons(false);
			}
		}
	}

	private void removeMarker() {
		if (mMarkerSeries != null)
			mGraphView.removeSeries(mMarkerSeries);
	}

	private void resetGraphData() {
		for (Channels c : Channels.values()) {
			c.setData(new GraphViewData[128]);
			for (int x = 0; x < 128; x++) {
				c.getData()[x] = new GraphViewData(x, 0.0);
			}
			c.getSeries().resetData(c.getData());
		}
		mGraphView.setViewPort(0.0, 128.0);
		mGraphView.setManualYAxisBounds(Y_RANGE, -Y_RANGE);
		mGraphView.invalidate();
		initMarker();
	}

	private void setMarker() {
		if (mTraining) {
			return;
		}
		mMarkerData[0] = new GraphViewData(mCounter, Y_RANGE);
		mMarkerData[1] = new GraphViewData(mCounter, -Y_RANGE);
		mMarkerSeries.resetData(mMarkerData);
	}
}
