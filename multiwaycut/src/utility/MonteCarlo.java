package utility;

import library.StdRandom;

/**
 * Created by Bloch-Hansen on 2017-05-24.
 */
public class MonteCarlo {

    public static double getPhi1() {

        double x;
        double y;
        double fx;

        double a = 1.9001367;
        double b = 0.464102;
        double c = 1.4250987;
        double d = 0.44093302;
        double ab = 1.86603172;

        while (true) {

            x = StdRandom.uniform(0.0, 1.0);
            y = StdRandom.uniform(0.0, ab);

            if (x <= b) {

                fx = a * x;

                if (y <= fx) {

                    return x;

                } //end if

            } //end if

            else {

                fx = (c * x) + d;

                if (y <= fx) {

                    return x;

                } //end if

            } //end else

        } //end while

    } //end getPhi1

    public static double getPhi2() {

        double x;
        double y;
        double fx;

        double p2 = 0.305782;

        while (true) {

            x = StdRandom.uniform(0.0, 1.0);
            y = StdRandom.uniform(0.0, 2.12);

            if (x <= 0.23) {

                fx = ((0.14957 * x) - (0.0478 * Math.pow(x, 2)) + (0.45 * Math.pow(x, 3)) / p2);

                if (y <= fx) {

                    return x;

                } //end if

            } //end if

            else if (x <= 6.0 / 11.0) {

                fx = ((-0.00484) + (0.1995 * x) - (0.1067 * Math.pow(x, 2)) + (0.158 * Math.pow(x, 3)) / p2);

                if (y <= fx) {

                    return x;

                } //end if

            } //end else if

            else if (x <= 0.61) {

                fx = ((0.47639) + (0.21685 * x) - (0.02388 * Math.pow(x, 2)) - (0.021 * Math.pow(x, 3)) / p2);

                if (y <= fx) {

                    return x;

                } //end if

            } //end else if

            else if (x <= 0.77) {

                fx = ((0.47368) + (0.2816 * x) - (0.18365 * Math.pow(x, 2)) + (0.079 * Math.pow(x, 3)) / p2);

                if (y <= fx) {

                    return x;

                } //end if

            } //end else if

            else {

                fx = ((0.32195) + (0.75 * x) - (0.6476 * Math.pow(x, 2)) + (0.2239 * Math.pow(x, 3)) / p2);

                if (y <= fx) {

                    return x;

                } //end if

            } //end else

        } //end while

    } //end getPhi2

} //end MonteCarlo
