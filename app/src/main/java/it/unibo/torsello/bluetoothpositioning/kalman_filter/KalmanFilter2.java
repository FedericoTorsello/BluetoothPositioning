package it.unibo.torsello.bluetoothpositioning.kalman_filter;

import it.unibo.torsello.bluetoothpositioning.constants.KFilterConstansts;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class KalmanFilter2 {

    private int N;
    private double R, Q;
    double[] xhat, xhat_prime, p, p_prime, k;

    private static KalmanFilter2 ourInstance = new KalmanFilter2();

    public static KalmanFilter2 getInstance() {
        return ourInstance;
    }

    private KalmanFilter2() {
        // Number of measurements
        N = KFilterConstansts.NUMBER_OF_MEASUREMENTS;
        //	R = StatUtils.populationVariance(observations);

        // Estimation variance
        R = KFilterConstansts.ESTIMATION_VARIANCE;

        // Process variance
        Q = KFilterConstansts.PROCESS_NOISE;

        // estimated true value (posteri)
        xhat = new double[N + 1];

        // estimated true value (priori)
        xhat_prime = new double[N + 1];

        // estimated error (posteri)
        p = new double[N + 1];

        // estimated error (priori)
        p_prime = new double[N + 1];

        // kalman gain
        k = new double[N + 1];
    }

    public double filter(double observations) {

        // measurements with mean = .5, sigma = .1;
        double z = observations;

        // Initial guesses
        xhat[0] = observations;
        p[0] = observations; // oppure 1?? o 0.001?

        //	System.out.println(xhat.length);
        //	System.out.println(observations.length);

        double cur_ave = 0;

        for (int i = 1; i <= N; i++) {
            // time update
            xhat_prime[i] = xhat[i - 1];
            p_prime[i] = p[i - 1] + Q;

            // measurement update
            k[i] = p_prime[i] / (p_prime[i] + R);
            xhat[i] = xhat_prime[i] + k[i] * (z - xhat_prime[i]);
            p[i] = (1 - k[i]) * p_prime[i];

            // calculate running average
            cur_ave = (cur_ave * (i - 1) + z) / ((double) i);

//            String a = String.format(Locale.getDefault(), "miao  %04f;%04f;%04f", z, xhat[i], cur_ave);
//            Log.i("test", a);
        }

        return xhat[N];
    }
}