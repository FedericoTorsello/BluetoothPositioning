package it.unibo.torsello.bluetoothpositioning.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by federico on 28/07/16.
 */
public class BeaconConstants {

    private enum Plain {ONE, TWO, THREE, FOUR, FIVE}

//    private static final Identifier ID_DEVICE_1 = Identifier.parse("C19BB0B9019E");//ice1
//    private static final Identifier ID_DEVICE_2 = Identifier.parse("D1BEE2E967A6");//ice2
//    private static final Identifier ID_DEVICE_3 = Identifier.parse("FA6B721EEB46");//mint1
//    private static final Identifier ID_DEVICE_4 = Identifier.parse("D98000B71678");//mint2
//    private static final Identifier ID_DEVICE_5 = Identifier.parse("DBF6F50C23BF");//bluebarry1
//    private static final Identifier ID_DEVICE_6 = Identifier.parse("E7E40EF6793F");//bleubarry2
//
//    public static final List<Identifier> BEACON_ADRESSES1;
//    static {
//        List<Identifier> deviceID = new ArrayList<>();
//        deviceID.add(ID_DEVICE_1);
//        deviceID.add(ID_DEVICE_2);
//        deviceID.add(ID_DEVICE_3);
//        BEACON_ADRESSES1 = Collections.unmodifiableList(deviceID);
//    }
//
//    public static final List<Identifier> BEACON_ADRESSES2;
//    static {
//        List<Identifier> deviceID = new ArrayList<>();
//        deviceID.add(ID_DEVICE_4);
//        deviceID.add(ID_DEVICE_5);
//        deviceID.add(ID_DEVICE_6);
//        BEACON_ADRESSES2 = Collections.unmodifiableList(deviceID);
//    }
//
//    public static final Region REGION_1 = new Region(Plain.ONE.name(), BEACON_ADRESSES1, null);
//    public static final Region REGION_2 = new Region(Plain.TWO.name(), BEACON_ADRESSES2, null);
//    public static final List<Region> REGIONS = Arrays.asList(REGION_1, REGION_2);

    public static final List<String> PLAIN_ONE;

    static {
        List<String> addresses = new ArrayList<>();
        addresses.add("C1:9B:B0:B9:01:9E"); //ice1
        addresses.add("D1:BE:E2:E9:67:A6"); //ice2
        addresses.add("FA:6B:72:1E:EB:46"); //mint1
        addresses.add("D9:80:00:B7:16:78"); //mint2
        addresses.add("DB:F6:F5:0C:23:BF"); //bluebarry1
        addresses.add("E7:E4:0E:F6:79:3F"); //bleubarry2
        PLAIN_ONE = Collections.unmodifiableList(addresses);
    }

}
