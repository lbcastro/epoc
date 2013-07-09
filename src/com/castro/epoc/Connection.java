package com.castro.epoc;

import static com.castro.epoc.Global.CHANNELS;
import static com.castro.epoc.Global.SAMPLING;
import static com.castro.epoc.Global.PACKET_SIZE;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;

public class Connection {
    private static Connection instance = null;

    public static Connection getInstance() {
        if (instance == null) {
            synchronized (Connection.class) {
                Connection inst = instance;
                if (inst == null) {
                    synchronized (Connection.class) {
                        inst = new Connection();
                    }
                    instance = inst;
                }
            }
        }
        return instance;
    }

    // Initiates needed objects.
    private HashMap<String, UsbDevice> mDevicesList;
    private UsbDeviceConnection mDeviceConnection;
    private UsbInterface mInterface;
    private UsbEndpoint mEndpoint;
    private String mSerialNumber;
    private Handler mHandler = new Handler();
    private byte[] mBuffer = new byte[PACKET_SIZE];
    private byte[] mDecryptedBuffer;
    private double[] mCorrectedBuffer = new double[CHANNELS];
    private int[] mLevels;
    private int[] mGyro = new int[2];
    private boolean mConnected = false;
    private int mCounter = 0;
    private int mLossCounter = 0;
    private double mLossValue;
    private List<PropertyChangeListener> mListenerArray = new ArrayList<PropertyChangeListener>();
    private PropertyChangeListener mMainListener;
    private Runnable mUpdateDataRunnable = new Runnable() {
        @Override
        public void run() {
            updateData();
        }
    };

    protected Connection() {
    }

    public void addChangeListener(PropertyChangeListener newListener) {
        if (!mListenerArray.contains(newListener)) {
            mListenerArray.add(newListener);
        }
    }

    public void closeConnection() {
        if (mDeviceConnection != null) {
            mDeviceConnection.close();
            mConnected = false;
            mHandler.removeCallbacks(mUpdateDataRunnable);
        }
    }

    private UsbDeviceConnection defCon(UsbManager m, UsbDevice d) {
        UsbDeviceConnection c = m.openDevice(d);
        return c;
    }

    private UsbDevice defDevice(HashMap<String, UsbDevice> l, String n) {
        UsbDevice d = l.get(n);
        return d;
    }

    private HashMap<String, UsbDevice> defDevicesList(UsbManager m) {
        HashMap<String, UsbDevice> d = m.getDeviceList();
        return d;
    }

    // Getters and setters.
    public boolean getConnection() {
        return mConnected;
    }

    private void getPermission(UsbManager m, UsbDevice d, Context context) {
        PendingIntent p = PendingIntent.getBroadcast(context, 0, new Intent(
                "com.android.example.USB_PERMISSION"), 0);
        if (!m.hasPermission(d))
            m.requestPermission(d, p);
    }

    /**
     * Establish a connection to the EPOC. Through the android.hardware
     * interface, all attached devices are scanned, looking for a connected
     * EPOC. If one is found, data acquisition module is started. If not, random
     * generation of data starts.
     * 
     * @param manager
     * @param context
     * @return
     */
    private boolean initEpoc(UsbManager manager, Context context) {
        if (mConnected) {
            return true;
        }
        // Tries to find attached devices and stops if no devices are found.
        mDevicesList = defDevicesList(manager);
        if (mDevicesList.size() == 0) {
            return false;
        }
        if (mDeviceConnection != null)
            mDeviceConnection.close();
        // Loops through all available USB BUS until an EPOC headset is found.
        Iterator<UsbDevice> s = mDevicesList.values().iterator();
        UsbDevice device = null;
        for (int x = 0; x < mDevicesList.size(); x++) {
            String devName = s.next().getDeviceName();
            UsbDevice tempDevice = defDevice(mDevicesList, devName);
            // Stops if no device is attached to the current BUS.
            if (tempDevice == null) {
                continue;
            }
            /** If needed, get needed permissions. */
            while (!manager.hasPermission(tempDevice))
                getPermission(manager, tempDevice, context);
            // Try to establish a connection to the current device. Stops if the
            // connection is unsuccessful.
            if (mDeviceConnection != null) {
                mDeviceConnection.close();
            }
            mDeviceConnection = defCon(manager, tempDevice);
            if (mDeviceConnection == null) {
                continue;
            }
            // Retrieves the device's serial number. Stops if this number
            // doesn't have the right configuration.
            mSerialNumber = mDeviceConnection.getSerial();
            if (!mSerialNumber.startsWith("SN")) {
                continue;
            }
            // If the loop reaches this section, the correct device was found
            // and a connection was established, so the loop is terminated.
            device = tempDevice;
            break;
        }
        if (device == null)
            return false;
        mInterface = device.getInterface(1);
        mDeviceConnection.claimInterface(mInterface, true);
        mEndpoint = mInterface.getEndpoint(0);
        // Check if the headset is on.
        byte[] buffer = new byte[32];
        double detection = mDeviceConnection.bulkTransfer(mEndpoint, buffer,
                PACKET_SIZE, 1000);
        if (detection == -1.0)
            return false;
        // At this point, connection has been established.
        mConnected = true;
        ActionBarManager.setState("Online");
        for (PropertyChangeListener listener : mListenerArray) {
            listener.propertyChange(new PropertyChangeEvent(this, "connection",
                    0, mConnected));
        }
        mMainListener.propertyChange(new PropertyChangeEvent(this,
                "connection", 0, mConnected));
        Crypt.getInstance().initCipher(mSerialNumber);
        // TODO: Random data management.
        mHandler.removeCallbacks(mUpdateDataRunnable);
        mHandler.post(mUpdateDataRunnable);
        return true;
    }

    /**
     * Checks if the connection is established. If not, attempts to connect to
     * the headset, returning the result.
     * 
     * @param manager
     * @param context
     * @return The connection state
     */
    public boolean isConnected(UsbManager manager, Context context) {
        if (mConnected) {
            return mConnected;
        }
        mConnected = initEpoc(manager, context);
        return mConnected;
    }

    public byte[] processData(byte[] b) {
        mDecryptedBuffer = null;
        // Decrypts the new data.
        try {
            mDecryptedBuffer = Crypt.getInstance().decrypt(b);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        if (mDecryptedBuffer[0] == mCounter) {
            return null;
        }
        if (mDecryptedBuffer[0] != mCounter + 1) {
            if (mDecryptedBuffer[0] < mCounter && mDecryptedBuffer[0] >= 0) {
                mLossCounter = mDecryptedBuffer[0];
            } else if (mDecryptedBuffer[0] >= 0) {
                mLossCounter += 1;
            }
            mCounter = mDecryptedBuffer[0];
        }
        if (mDecryptedBuffer[0] < 0) {
            Battery.setLevel(this, 0xFF & mDecryptedBuffer[0], mMainListener);
            mCounter = -1;
            mLossValue = Math.round((mLossCounter / 128.0) * 100.0);
            ActionBarManager.setLoss(mLossValue);
            mLossCounter = 0;
        } else {
            mCounter = mDecryptedBuffer[0];
        }
        return mDecryptedBuffer;
    }

    public void removeChangeListener(PropertyChangeListener oldListener) {
        if (mListenerArray.contains(oldListener)) {
            mListenerArray.remove(oldListener);
        }
    }

    public void setBuffer(byte[] b) {
        mBuffer = b;
    }

    public void setMainListener(PropertyChangeListener newListener) {
        mMainListener = newListener;
    }

    private void updateData() {
        mHandler.postDelayed(mUpdateDataRunnable, SAMPLING);
        if (mDeviceConnection == null) {
            mConnected = false;
            mMainListener.propertyChange(new PropertyChangeEvent(this,
                    "connection", 0, mConnected));
            return;
        }
        if (mDeviceConnection.bulkTransfer(mEndpoint, mBuffer, PACKET_SIZE,
                10000) < 0) {
            if (mDeviceConnection != null) {
                mDeviceConnection.close();
                mDeviceConnection = null;
            }
            if (mConnected) {
                mConnected = false;
                for (PropertyChangeListener listener : mListenerArray) {
                    listener.propertyChange(new PropertyChangeEvent(this,
                            "connection", 0, mConnected));
                }
            }
            return;
        }
        if (mBuffer == null) {
            return;
        }
        mBuffer = processData(mBuffer);
        mGyro[0] = mBuffer[29];
        mGyro[1] = mBuffer[30];
        mLevels = Crypt.getInstance().getLevels(mBuffer);
        for (Channels c : Channels.values()) {
            c.setAverage(mLevels[c.ordinal()]);
            mCorrectedBuffer[c.ordinal()] = Math
                    .round((mLevels[c.ordinal()] - c.getAverage()) * 100.0) / 100.0;
        }
        for (PropertyChangeListener listener : mListenerArray) {
            // TODO: Discriminate data to different classes.
            listener.propertyChange(new PropertyChangeEvent(this, "levels", 0,
                    mCorrectedBuffer));
            listener.propertyChange(new PropertyChangeEvent(this, "buffer",
                    mCounter, mBuffer));
            listener.propertyChange(new PropertyChangeEvent(this, "gyro", 0,
                    mGyro));
        }
    }
}
