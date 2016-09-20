package it.unibo.torsello.bluetoothpositioning.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v4.app.FragmentActivity;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unibo.torsello.bluetoothpositioning.fragment.UsbMeasurementFragment;

/**
 * Created by federico on 17/09/16.
 */
public class UsbRawDataUtil {

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private UsbSerialPort port;
    private SerialInputOutputManager mSerialIoManager;
    private FragmentActivity fragmentActivity;
    private OnReceiveNewData onReceiveNewData;
    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    onReceiveNewData.getStatus("The I/O manager is stopped");
                }

                @Override
                public void onNewData(final byte[] data) {
                    if (fragmentActivity != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                fragmentActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        onReceiveNewData.getData(data);
                                    }
                                });
                            }
                        }).start();
                    }
                }
            };

    public UsbRawDataUtil(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;

        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) fragmentActivity.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

        if (!availableDrivers.isEmpty()) {

            // Open a connection to the first available driver.
            UsbSerialDriver driver = availableDrivers.get(0);

            if (manager.openDevice(driver.getDevice()) != null) {
                // Read some data! Most have just one port (port 0).
                port = driver.getPorts().get(0);
            } else {
                Intent startIntent = new Intent(fragmentActivity, fragmentActivity.getClass());
                PendingIntent pendingIntent = PendingIntent.getService(fragmentActivity, 0, startIntent, 0);
                manager.requestPermission(driver.getDevice(), pendingIntent);
            }
        }
    }

    public void setOnReceiveNewData(OnReceiveNewData onReceive) {
        this.onReceiveNewData = onReceive;
    }

    public void resume() {

        if (port != null) {

            final UsbManager usbManager = (UsbManager) fragmentActivity.getSystemService(Context.USB_SERVICE);
            UsbDeviceConnection connection = usbManager.openDevice(port.getDriver().getDevice());

            if (connection == null) {
                onReceiveNewData.getStatus("Opening device failed");
            } else {

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
                    onReceiveNewData.getStatus("Error opening device: " + e.getMessage());
                    try {
                        port.close();
                    } catch (IOException e2) {
                        // Ignore.
                    }
                    port = null;
                    return;
                }

                stopIoManager();
                startIoManager();
            }
        } else {
            onReceiveNewData.getStatus("USB device not connected");
        }
    }

    public void pause() {
        stopIoManager();
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
            onReceiveNewData.getStatus("The I/O manager is stopped");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (port != null) {
            onReceiveNewData.getStatus("The I/O manager is started");
            mSerialIoManager = new SerialInputOutputManager(port, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    public interface OnReceiveNewData {
        void getData(byte[] data);

        void getStatus(String status);

    }

}
