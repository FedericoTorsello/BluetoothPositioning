package it.unibo.torsello.bluetoothpositioning.logic;

import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by federico on 11/07/16.
 */
public class Plain1 implements Plain {

    @Override
    public void setDevice() {
        Map<String, MyDevice> deviceArrayMap = new ArrayMap<>();
        List<MyDevice> beacons = new ArrayList<>();

        beacons.add(new MyDevice("mint1", "fa6b721eeb46", "B9407F30-F5F8-466E-AFF9-25556B57FE6D:60230:29214"));
        beacons.add(new MyDevice("mint2", "d98000b71678", "B9407F30-F5F8-466E-AFF9-25556B57FE6D:5752:183"));
        beacons.add(new MyDevice("blueberry1", "dbf6f50c23bf", "B9407F30-F5F8-466E-AFF9-25556B57FE6D:9151:62732"));
        beacons.add(new MyDevice("blueberry2", "fa6b721eeb46", "B9407F30-F5F8-466E-AFF9-25556B57FE6D:31039:3830"));
        beacons.add(new MyDevice("ice1", "c19bb0b9019e", "B9407F30-F5F8-466E-AFF9-25556B57FE6D:414:45241"));
        beacons.add(new MyDevice("ice2", "d1bee2e967a6", "B9407F30-F5F8-466E-AFF9-25556B57FE6D:26534:58089"));
    }

    @Override
    public void setAllDevice(List<MyDevice> allDevice) {

    }

    @Override
    public List<MyDevice> getAllDevice() {
        return null;
    }
}
