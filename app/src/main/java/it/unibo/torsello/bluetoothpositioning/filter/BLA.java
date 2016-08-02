//package it.unibo.torsello.bluetoothpositioning.filter;
//
///**
// * Created by federico on 31/07/16.
// */
//
////import org.altbeacon.beacon.distance.PathLossDistanceCalculator;
//import org.altbeacon.beacon.distance.DistanceCalculator;
//import org.altbeacon.beacon.logging.LogManager;
//
//public class BLA extends PathLossDistanceCalculator implements DistanceCalculator {
//    public static final String TAG = "BLA";
//    private double mReceiverRssiSlope;
//    private double mReceiverRssiOffset;
//
//    public BLA(double receiverRssiSlope, double receiverRssiOffset) {
//        super(receiverRssiSlope, receiverRssiOffset);
//        this.mReceiverRssiSlope = receiverRssiSlope;
//        this.mReceiverRssiOffset = receiverRssiOffset;
//    }
//
////    public BLA(double receiverRssiSlope, double receiverRssiOffset) {
////        this.mReceiverRssiSlope = receiverRssiSlope;
////        this.mReceiverRssiOffset = receiverRssiOffset;
////    }
//
//    public double calculateDistance(int txPower, double rssi) {
//        if (rssi == 0.0D) {
//            return -1.0D;
//        } else {
//            LogManager.d("BLA", "calculating distance based on mRssi of %s and txPower of %s", new Object[]{Double.valueOf(rssi), Integer.valueOf(txPower)});
//            double ratio = rssi * 1.0D / (double) txPower;
//            double distance;
//            if (ratio < 1.0D) {
//                distance = Math.pow(ratio, 10.0D);
//            } else {
//                double adjustment = this.mReceiverRssiSlope * rssi + this.mReceiverRssiOffset;
//                double adjustedRssi = rssi - adjustment;
//                System.out.println("Adjusting rssi by " + adjustment + " when rssi is " + rssi);
//                System.out.println("Adjusted rssi is now " + adjustedRssi);
//                distance = Math.pow(10.0D, (-adjustedRssi + (double) txPower) / 10.0D * 0.35D);
//            }
//
//            LogManager.d("BLA", "avg mRssi: %s distance: %s", new Object[]{Double.valueOf(rssi), Double.valueOf(distance)});
//            return distance;
//        }
//    }
//}
