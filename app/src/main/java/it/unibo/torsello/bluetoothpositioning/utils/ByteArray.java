package it.unibo.torsello.bluetoothpositioning.utils;

import android.util.Log;
import android.widget.TextView;

import it.unibo.torsello.bluetoothpositioning.R;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class ByteArray {

    private byte[] mByteArray = new byte[1];
    private int mUsedLength;

    String val;

    public void add(byte[] newArray) {
        // Make sure we have enough space to store byte array.
        while (mUsedLength + newArray.length > mByteArray.length) {
            byte[] tmpArray = new byte[mByteArray.length * 2];
            System.arraycopy(mByteArray, 0, tmpArray, 0, mUsedLength);
            mByteArray = tmpArray;
        }

        // Add byte array.
        System.arraycopy(newArray, 0, mByteArray, mUsedLength, newArray.length);
        mUsedLength += newArray.length;

        val = new String(newArray);
    }

    public String getVal() {
        return val;
    }

    //    @Override
//    public String toString() {
////        StringBuilder hexStr = new StringBuilder();
////
////        for (int i = 0; i < mUsedLength; i++) {
////            if (Character.isLetterOrDigit(mByteArray[i])) {
////                hexStr.append(new String(new byte[]{mByteArray[i]}));
////            } else {
////                hexStr.append('\n');
////            }
////        }
////
////        return hexStr.toString();
//
////        return new String(mByteArray,0,mByteArray.length);
//        new String(new byte[] {}));
//
//    }
}