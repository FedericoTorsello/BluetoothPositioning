package it.unibo.torsello.bluetoothpositioning.fragment;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.utils.ArduinoCommunicatorService;
import it.unibo.torsello.bluetoothpositioning.utils.ByteArray;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class ArduinoUSB implements MeasurementFrag.OnArduinoListener {

    private static final int ARDUINO_USB_VENDOR_ID = 0x2341;
    private static final int ARDUINO_UNO_USB_PRODUCT_ID = 0x01;
    private static final int ARDUINO_MEGA_2560_USB_PRODUCT_ID = 0x10;
    private static final int ARDUINO_MEGA_2560_R3_USB_PRODUCT_ID = 0x42;
    private static final int ARDUINO_UNO_R3_USB_PRODUCT_ID = 0x43;
    private static final int ARDUINO_MEGA_2560_ADK_R3_USB_PRODUCT_ID = 0x44;
    private static final int ARDUINO_MEGA_2560_ADK_USB_PRODUCT_ID = 0x3F;

    private final static String TAG = "ArduinoCommunicatorActivity";

    private Boolean mIsReceiving;
    private ArrayList<ByteArray> mTransferedDataList;
    private BroadcastReceiver mReceiver;
    private FragmentActivity fragActivity;

    public ArduinoUSB(FragmentActivity fragmentActivity) {
        this.fragActivity = fragmentActivity;
        mTransferedDataList = new ArrayList<ByteArray>();
        createReceiver();
        findDevice();
    }

    private void createReceiver() {
        mReceiver = new BroadcastReceiver() {

            private void handleTransferedData(Intent intent, boolean receiving) {
                if (mIsReceiving == null || mIsReceiving != receiving) {
                    mIsReceiving = receiving;
                    mTransferedDataList.add(new ByteArray());
                }

                final byte[] newTransferedData = intent.getByteArrayExtra(ArduinoCommunicatorService.DATA_EXTRA);

                ByteArray transferedData = mTransferedDataList.get(mTransferedDataList.size() - 1);
                transferedData.add(newTransferedData);
                mTransferedDataList.set(mTransferedDataList.size() - 1, transferedData);


//                if (textView.getText() != ""){
//                    try {
//                        Thread.sleep(20);
//                        textView.setText(transferedData.getVal());
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                } else{
//                    textView.setText("");
//                }

            }

            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                if (ArduinoCommunicatorService.DATA_RECEIVED_INTENT.equals(action)) {
                    handleTransferedData(intent, true);
                } else if (ArduinoCommunicatorService.DATA_SENT_INTERNAL_INTENT.equals(action)) {
                    handleTransferedData(intent, false);
                }
            }
        };
    }

    private void findDevice() {
        UsbManager usbManager = (UsbManager) fragActivity.getSystemService(Context.USB_SERVICE);
        UsbDevice usbDevice = null;

        FloatingActionButton fab = (FloatingActionButton) fragActivity.findViewById(R.id.fab);
        for (UsbDevice deviceIterator : usbManager.getDeviceList().values()) {
            // Arduino device found
            if (deviceIterator.getVendorId() == ARDUINO_USB_VENDOR_ID) {

                switch (deviceIterator.getProductId()) {
                    case ARDUINO_UNO_USB_PRODUCT_ID:
                        Snackbar.make(fab, "Arduino Uno " + fragActivity.getString(R.string.found), Snackbar.LENGTH_SHORT).show();
                        usbDevice = deviceIterator;
                        break;
                    case ARDUINO_MEGA_2560_USB_PRODUCT_ID:
                        Snackbar.make(fab, "Arduino Mega 2560 " + fragActivity.getString(R.string.found), Snackbar.LENGTH_SHORT).show();
                        usbDevice = deviceIterator;
                        break;
                    case ARDUINO_MEGA_2560_R3_USB_PRODUCT_ID:
                        Snackbar.make(fab, "Arduino Mega 2560 R3 " + fragActivity.getString(R.string.found), Snackbar.LENGTH_SHORT).show();
                        usbDevice = deviceIterator;
                        break;
                    case ARDUINO_UNO_R3_USB_PRODUCT_ID:
                        Snackbar.make(fab, "Arduino Uno R3 " + fragActivity.getString(R.string.found), Snackbar.LENGTH_SHORT).show();
                        usbDevice = deviceIterator;
                        break;
                    case ARDUINO_MEGA_2560_ADK_R3_USB_PRODUCT_ID:
                        Snackbar.make(fab, "Arduino Mega 2560 ADK R3 " + fragActivity.getString(R.string.found), Snackbar.LENGTH_SHORT).show();
                        usbDevice = deviceIterator;
                        break;
                    case ARDUINO_MEGA_2560_ADK_USB_PRODUCT_ID:
                        Snackbar.make(fab, "Arduino Mega 2560 ADK " + fragActivity.getString(R.string.found), Snackbar.LENGTH_SHORT).show();
                        usbDevice = deviceIterator;
                        break;
                }
            }
        }

        // No device found
        if (usbDevice == null) {
            Snackbar.make(fab, R.string.no_device_found, Snackbar.LENGTH_LONG).show();
        } else { // Device found
            Intent startIntent = new Intent(fragActivity, ArduinoCommunicatorService.class);
            PendingIntent pendingIntent = PendingIntent.getService(fragActivity, 0, startIntent, 0);
            usbManager.requestPermission(usbDevice, pendingIntent);
        }
    }

    @Override
    public BroadcastReceiver getBroadcastReceiver() {
        return mReceiver;
    }
}
