package it.unibo.torsello.bluetoothpositioning.constants;

import android.support.v4.util.ArrayMap;

import java.util.Collections;
import java.util.Map;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.models.Device;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceConstants {

//    private enum Plain {ONE, TWO, THREE, FOUR, FIVE}

//    private static final Identifier ID_DEVICE_1 = Identifier.parse("C19BB0B9019E");//ice1
//    private static final Identifier ID_DEVICE_2 = Identifier.parse("D1BEE2E967A6");//ice2
//    private static final Identifier ID_DEVICE_3 = Identifier.parse("FA6B721EEB46");//mint1
//    private static final Identifier ID_DEVICE_4 = Identifier.parse("D98000B71678");//mint2
//    private static final Identifier ID_DEVICE_5 = Identifier.parse("DBF6F50C23BF");//blueberry1
//    private static final Identifier ID_DEVICE_6 = Identifier.parse("E7E40EF6793F");//blueberry2
//
//    public static final List<Identifier> BEACON_ADRESSES1;
//    static {
//        List<Identifier> deviceID = new ArrayList<>();
//        deviceID.add(ID_DEVICE_1);
//        deviceID.add(ID_DEVICE_2);
//        deviceID.add(ID_DEVICE_3);
//        deviceID.add(ID_DEVICE_4);
//        deviceID.add(ID_DEVICE_5);
//        deviceID.add(ID_DEVICE_6);
//        BEACON_ADRESSES1 = Collections.unmodifiableList(deviceID);
//    }
//
//    public static final Region REGION_1 = new Region(Plain.ONE.name(), BEACON_ADRESSES1, null);
//    public static final List<Region> REGIONS = Arrays.asList(REGION_1, REGION_2);

//    public static final List<String> PLAIN_ONE;
//    static {
//        List<String> addresses = new ArrayList<>();
//        addresses.add("C1:9B:B0:B9:01:9E"); //ice1
//        addresses.add("D1:BE:E2:E9:67:A6"); //ice2
//        addresses.add("FA:6B:72:1E:EB:46"); //mint1
//        addresses.add("D9:80:00:B7:16:78"); //mint2
//        addresses.add("DB:F6:F5:0C:23:BF"); //blueberry1
//        addresses.add("E7:E4:0E:F6:79:3F"); //blueberry2
//        PLAIN_ONE = Collections.unmodifiableList(addresses);
//    }

    private static final String DEVICE_1 = "C1:9B:B0:B9:01:9E"; //ice1
    private static final String DEVICE_2 = "D1:BE:E2:E9:67:A6"; //ice2
    private static final String DEVICE_3 = "FA:6B:72:1E:EB:46"; //mint1
    private static final String DEVICE_4 = "D9:80:00:B7:16:78"; //mint2
    private static final String DEVICE_5 = "DB:F6:F5:0C:23:BF"; //blueberry1
    private static final String DEVICE_6 = "E7:E4:0E:F6:79:3F"; //blueberry2

    // Beacons
    public static final Map<String, Device> DEVICE_MAP;

    static {
        Map<String, Device> map = new ArrayMap<>();
        map.put(DEVICE_1, new Device(1, DEVICE_1, "Ice1", R.string.icy_marshmallow, R.drawable.beacon_icy));
        map.put(DEVICE_2, new Device(2, DEVICE_2, "Ice2", R.string.icy_marshmallow, R.drawable.beacon_icy));

        map.put(DEVICE_5, new Device(3, DEVICE_5, "Blueberry1", R.string.blueberry_pie, R.drawable.beacon_blueberry));
        map.put(DEVICE_6, new Device(4, DEVICE_6, "Blueberry2", R.string.blueberry_pie, R.drawable.beacon_blueberry));

        map.put(DEVICE_3, new Device(5, DEVICE_3, "Mint1", R.string.mint_cocktail, R.drawable.beacon_mint));
        map.put(DEVICE_4, new Device(6, DEVICE_4, "Mint2", R.string.mint_cocktail, R.drawable.beacon_mint));
        DEVICE_MAP = Collections.unmodifiableMap(map);
    }

}
