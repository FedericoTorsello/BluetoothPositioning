package it.unibo.torsello.bluetoothpositioning.fragment;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.utils.ArduinoCommunicatorService;
import it.unibo.torsello.bluetoothpositioning.utils.ByteArray;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class ArduinoUSB {

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
    private FragmentActivity fragmentActivity;

    public ArduinoUSB(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        mTransferedDataList = new ArrayList<ByteArray>();
        createReceiver();
        createService();
        findDevice();
    }

    public ArrayList<ByteArray> getmTransferedDataList() {
        return mTransferedDataList;
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

    private void createService() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ArduinoCommunicatorService.DATA_RECEIVED_INTENT);
        filter.addAction(ArduinoCommunicatorService.DATA_SENT_INTERNAL_INTENT);
        fragmentActivity.registerReceiver(mReceiver, filter);
    }

    private void findDevice() {
        UsbManager usbManager = (UsbManager) fragmentActivity.getSystemService(Context.USB_SERVICE);
        UsbDevice usbDevice = null;

        for (UsbDevice deviceIterator : usbManager.getDeviceList().values()) {
            // Arduino device found
            if (deviceIterator.getVendorId() == ARDUINO_USB_VENDOR_ID) {
                switch (deviceIterator.getProductId()) {
                    case ARDUINO_UNO_USB_PRODUCT_ID:
                        Toast.makeText(fragmentActivity, "Arduino Uno " + fragmentActivity.getString(R.string.found), Toast.LENGTH_SHORT).show();
                        usbDevice = deviceIterator;
                        break;
                    case ARDUINO_MEGA_2560_USB_PRODUCT_ID:
                        Toast.makeText(fragmentActivity, "Arduino Mega 2560 " + fragmentActivity.getString(R.string.found), Toast.LENGTH_SHORT).show();
                        usbDevice = deviceIterator;
                        break;
                    case ARDUINO_MEGA_2560_R3_USB_PRODUCT_ID:
                        Toast.makeText(fragmentActivity, "Arduino Mega 2560 R3 " + fragmentActivity.getString(R.string.found), Toast.LENGTH_SHORT).show();
                        usbDevice = deviceIterator;
                        break;
                    case ARDUINO_UNO_R3_USB_PRODUCT_ID:
                        Toast.makeText(fragmentActivity, "Arduino Uno R3 " + fragmentActivity.getString(R.string.found), Toast.LENGTH_SHORT).show();
                        usbDevice = deviceIterator;
                        break;
                    case ARDUINO_MEGA_2560_ADK_R3_USB_PRODUCT_ID:
                        Toast.makeText(fragmentActivity, "Arduino Mega 2560 ADK R3 " + fragmentActivity.getString(R.string.found), Toast.LENGTH_SHORT).show();
                        usbDevice = deviceIterator;
                        break;
                    case ARDUINO_MEGA_2560_ADK_USB_PRODUCT_ID:
                        Toast.makeText(fragmentActivity, "Arduino Mega 2560 ADK " + fragmentActivity.getString(R.string.found), Toast.LENGTH_SHORT).show();
                        usbDevice = deviceIterator;
                        break;
                }
            }
        }

        // No device found
        if (usbDevice == null) {
            Toast.makeText(fragmentActivity, fragmentActivity.getString(R.string.no_device_found), Toast.LENGTH_LONG).show();
        } else { // Device found
            Intent startIntent = new Intent(fragmentActivity, ArduinoCommunicatorService.class);
            PendingIntent pendingIntent = PendingIntent.getService(fragmentActivity, 0, startIntent, 0);
            usbManager.requestPermission(usbDevice, pendingIntent);
        }
    }
}
