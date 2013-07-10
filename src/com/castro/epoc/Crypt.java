
package com.castro.epoc;

import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {

    public static Crypt getInstance() {
        if (instance == null) {
            synchronized (Crypt.class) {
                Crypt inst = instance;
                if (inst == null) {
                    synchronized (Crypt.class) {
                        inst = new Crypt();
                    }
                    instance = inst;
                }
            }
        }
        return instance;
    }

    private Cipher mCipher;

    private int mCurrentLevel;

    private int mBitsFirst;

    private int mBitsSecond;

    private static Crypt instance = null;

    protected Crypt() {
    }

    /** Decrypts a specified array of bytes. */
    public byte[] decrypt(byte[] buffer) throws IllegalBlockSizeException, BadPaddingException {
        return mCipher.doFinal(buffer);
    }

    /**
     * Generate a cipher key from a specified serial number.
     * 
     * @param serial - provided serial number.
     * @return
     * @throws IOException
     */
    private SecretKeySpec getKey(String serial) {
        byte[] raw = serial.getBytes();
        byte[] bytes = new byte[16];
        assert raw.length == 16;
        bytes[0] = raw[15];
        bytes[1] = 0x00;
        bytes[2] = raw[14];
        bytes[3] = 0x54;
        bytes[4] = raw[13];
        bytes[5] = 0x10;
        bytes[6] = raw[12];
        bytes[7] = 0x42;
        bytes[8] = raw[15];
        bytes[9] = 0x00;
        bytes[10] = raw[14];
        bytes[11] = 0x48;
        bytes[12] = raw[13];
        bytes[13] = 0x00;
        bytes[14] = raw[12];
        bytes[15] = 0x50;
        return new SecretKeySpec(bytes, "AES");
    }

    /**
     * Get a specific channel's value from a specified buffer.
     * 
     * @param buffer - buffer with all channels' data.
     * @param index - bit index of the channel.
     * @return Value of the specified channel.
     */
    public int getLevel(byte[] buffer, int[] index) {
        mCurrentLevel = 0;
        mBitsFirst = 0;
        mBitsSecond = 0;
        if (buffer == null || index == null) {
            return mCurrentLevel;
        }
        // Simulate unsigned bytes.
        for (int i = 13; i >= 0; --i) {
            mCurrentLevel <<= 1;
            mBitsFirst = (index[i] >> 3) + 1;
            mBitsSecond = index[i] % 8;
            mCurrentLevel |= (buffer[mBitsFirst] >> mBitsSecond) & 1;
        }
        return mCurrentLevel;
    }

    /**
     * Retrieve all channels values from a specified buffer.
     * 
     * @param buffer Buffer with decrypted data.
     * @return All channels' values.
     */
    public int[] getLevels(byte[] buffer) {
        int[] levels = new int[14];
        for (Channels c : Channels.values()) {
            levels[c.ordinal()] = getLevel(buffer, c.getBits());
        }
        return levels;
    }

    /** Initiate cipher module using a stored serial number. */
    public void initCipher(String serialNumber) {
        try {
            mCipher = Cipher.getInstance("AES/ECB/NoPadding");
            SecretKeySpec key = getKey(serialNumber);
            mCipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            throw new IllegalStateException("no javax.crypto support");
        }
    }
}
