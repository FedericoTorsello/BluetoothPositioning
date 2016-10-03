package it.unibo.torsello.bluetoothpositioning.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.activities.ApplicationActivity;
import it.unibo.torsello.bluetoothpositioning.observables.UsbMeasurementObservable;

/**
 * Created by federico on 02/10/16.
 */

public class UsbUtil {

    private UsbMeasurementObservable myUsbObservable;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private UsbSerialPort port;
    private SerialInputOutputManager mSerialIoManager;

    private ApplicationActivity applicationActivity;

    public UsbUtil(ApplicationActivity applicationActivity) {
        this.applicationActivity = applicationActivity;
        myUsbObservable = UsbMeasurementObservable.getInstance();
    }

    private ApplicationActivity getActivity() {
        return applicationActivity;
    }

    public void onPause() {
        stopIoManager();
        closePort();
    }

    public void onResume() {
        initializeUsb();
    }


    private void initializeUsb() {

        // Find all available drivers from attached devices.
        UsbManager usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
        if (!availableDrivers.isEmpty()) {

            // Open a connection to the first available driver.
            UsbSerialDriver driver = availableDrivers.get(0);

            if (usbManager.hasPermission(driver.getDevice())) {
                if (usbManager.openDevice(driver.getDevice()) != null) {
                    // Read some data! Most have just one port (port 0).
                    port = driver.getPorts().get(0);
                }
            } else {
                Intent startIntent = new Intent(getActivity(), getClass());
                PendingIntent pendingIntent =
                        PendingIntent.getService(getActivity(), 0, startIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                usbManager.requestPermission(driver.getDevice(), pendingIntent);
            }

            if (port != null) {

                UsbDeviceConnection connection = usbManager.openDevice(port.getDriver().getDevice());

                if (connection != null) {
                    try {
                        port.open(connection);
                        port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

//                    String details = "CD  - Carrier Detect" + port.getCD() + '\n' +
//                            "CTS - Clear To Send" + port.getCTS() + '\n' +
//                            "DSR - Data Set Ready" + port.getDSR() + '\n' +
//                            "DTR - Data Terminal Ready" + port.getDTR() + '\n' +
//                            "DSR - Data Set Ready" + port.getDSR() + '\n' +
//                            "RI  - Ring Indicator" + port.getRI() + '\n' +
//                            "RTS - Request To Send" + port.getRTS();

                    } catch (IOException e) {
                        myUsbObservable.notifyObservers(getActivity().getString(R.string.error_opening_device)
                                + " " + e.getMessage());
                        myUsbObservable.notifyObservers(false);
                        closePort();
                        return;
                    }

                    stopIoManager();
                    startIoManager();
                }
            }
        }
    }

    private void closePort() {
        if (port != null) {
            try {
                port.close();
            } catch (IOException e) {
                // Ignore.
            }
            port = null;
        }
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (port != null) {

            SerialInputOutputManager.Listener mListener =
                    new SerialInputOutputManager.Listener() {

                        @Override
                        public void onRunError(Exception e) {
                            myUsbObservable.notifyObservers(false);
                            myUsbObservable.notifyObservers(getActivity().getString(R.string.usb_device_not_connected));
                            myUsbObservable.notifyObservers(0D);
                        }

                        @Override
                        public void onNewData(final byte[] data) {
                            try {
                                myUsbObservable.notifyObservers(true);
                                myUsbObservable.notifyObservers(getActivity().getString(R.string.usb_device_connected));
                                myUsbObservable.notifyObservers(Double.valueOf(new String(data).trim()) / 100);
                            } catch (NumberFormatException nfe) {
                            }
                        }
                    };

            mSerialIoManager = new SerialInputOutputManager(port, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }
}
