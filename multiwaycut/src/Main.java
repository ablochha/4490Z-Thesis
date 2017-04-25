import algorithms.Calinescu;
import algorithms.LocalSearch;
import cplex.MultiwayCutSolver;
import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
import algorithms.IsolationHeuristic;
import utility.*;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Bloch-Hansen on 2017-01-23.
 */
public class Main {

    private static final String CURRENT = "GE080";
    private static final String OUT_NAME = "-999.txt";
    private static final int NUM_TRIALS = 100;

    private static final double EPSILON1 = 1.6;
    private static final double EPSILON2 = .525;
    private static final double EPSILON3 = .315;
    private static final double EPSILON4 = .16;
    private static final double EPSILON5 = .08;


    public static void main(String[] args) {

        PropertiesReader prop = new PropertiesReader();

        GraphFormatReader reader = new GraphFormatReader();
        ConnectedComponentSearcher searcher = new ConnectedComponentSearcher();

        IsolationHeuristic isolationHeuristic = new IsolationHeuristic();
        LocalSearch localSearch = new LocalSearch();
        Calinescu calinescu = new Calinescu();
        MultiwayCutSolver solver = new MultiwayCutSolver();

        int trials = 0;
        int vertices = 0;
        double edgesSum = 0.0;

        double ihratio = 0.0;
        double ihratioSum = 0.0;
        double ihratioAvg = 0.0;
        double ihratioMin = Double.MAX_VALUE;
        double ihratioMax = 0.0;

        double lsratio1 = 0.0;
        double lsratioSum1 = 0.0;
        double lsratioAvg1 = 0.0;
        double lsratioMin1 = Double.MAX_VALUE;
        double lsratioMax1 = 0.0;

        double lsratio2 = 0.0;
        double lsratioSum2 = 0.0;
        double lsratioAvg2 = 0.0;
        double lsratioMin2 = Double.MAX_VALUE;
        double lsratioMax2 = 0.0;

        double lsratio3 = 0.0;
        double lsratioSum3 = 0.0;
        double lsratioAvg3 = 0.0;
        double lsratioMin3 = Double.MAX_VALUE;
        double lsratioMax3 = 0.0;

        double lsratio4 = 0.0;
        double lsratioSum4 = 0.0;
        double lsratioAvg4 = 0.0;
        double lsratioMin4 = Double.MAX_VALUE;
        double lsratioMax4 = 0.0;

        double lsratio5 = 0.0;
        double lsratioSum5 = 0.0;
        double lsratioAvg5 = 0.0;
        double lsratioMin5 = Double.MAX_VALUE;
        double lsratioMax5 = 0.0;

        double calratio = 0.0;
        double calratioSum = 0.0;
        double calratioAvg = 0.0;
        double calratioMin = Double.MAX_VALUE;
        double calratioMax = 0.0;

        double ihDeviationSum = 0.0;
        double lsDeviationSum1 = 0.0;
        double lsDeviationSum2 = 0.0;
        double lsDeviationSum3 = 0.0;
        double lsDeviationSum4 = 0.0;
        double lsDeviationSum5 = 0.0;
        double calDeviationSum = 0.0;

        long ihStart;
        long ihTime;
        long ihTimeSum = 0;

        long lsStart1;
        long lsTime1;
        long lsTimeSum1 = 0;
        long lsStart2;
        long lsTime2;
        long lsTimeSum2 = 0;
        long lsStart3;
        long lsTime3;
        long lsTimeSum3 = 0;
        long lsStart4;
        long lsTime4;
        long lsTimeSum4 = 0;
        long lsStart5;
        long lsTime5;
        long lsTimeSum5 = 0;

        long calStart;
        long calTime;
        long calTimeSum = 0;

        LinkedList<Double> ihDeviation = new LinkedList<>();
        LinkedList<Double> lsDeviation1 = new LinkedList<>();
        LinkedList<Double> lsDeviation2 = new LinkedList<>();
        LinkedList<Double> lsDeviation3 = new LinkedList<>();
        LinkedList<Double> lsDeviation4 = new LinkedList<>();
        LinkedList<Double> lsDeviation5 = new LinkedList<>();
        LinkedList<Double> calDeviation = new LinkedList<>();

        while (trials < NUM_TRIALS) {

            FlowNetwork largestConnectedComponent = searcher.getLargestConnectedComponent(reader.parse(prop.get(CURRENT), OUT_NAME));

            vertices = largestConnectedComponent.getNumVertices();
            edgesSum += largestConnectedComponent.getNumEdges();

            ihStart = System.nanoTime();
            int ihCost = isolationHeuristic.computeMultiwayCut(new FlowNetwork(largestConnectedComponent));
            ihTime = System.nanoTime() - ihStart;
            ihTimeSum += ihTime;

            lsStart1 = System.nanoTime();
            int lsCost1 = localSearch.computeMultiwayCut(new FlowNetwork(largestConnectedComponent), EPSILON1);
            lsTime1 = System.nanoTime() - lsStart1;
            lsTimeSum1 += lsTime1;

            lsStart2 = System.nanoTime();
            int lsCost2 = localSearch.computeMultiwayCut(new FlowNetwork(largestConnectedComponent), EPSILON2);
            lsTime2 = System.nanoTime() - lsStart2;
            lsTimeSum2 += lsTime2;

            lsStart3 = System.nanoTime();
            int lsCost3 = localSearch.computeMultiwayCut(new FlowNetwork(largestConnectedComponent), EPSILON3);
            lsTime3 = System.nanoTime() - lsStart3;
            lsTimeSum3 += lsTime3;

            lsStart4 = System.nanoTime();
            int lsCost4 = localSearch.computeMultiwayCut(new FlowNetwork(largestConnectedComponent), EPSILON4);
            lsTime4 = System.nanoTime() - lsStart4;
            lsTimeSum4 += lsTime4;

            lsStart5 = System.nanoTime();
            int lsCost5 = localSearch.computeMultiwayCut(new FlowNetwork(largestConnectedComponent), EPSILON5);
            lsTime5 = System.nanoTime() - lsStart5;
            lsTimeSum5 += lsTime5;

            calStart = System.nanoTime();
            int calCost = calinescu.computeMultiwayCut(new FlowNetwork(largestConnectedComponent), solver);
            calTime = System.nanoTime() - calStart;
            calTimeSum += calTime;

            int optimal = solver.computeIntegerMultiwayCut(new FlowNetwork(largestConnectedComponent));

            /*StdOut.println("Trial: " + (trials + 1) +
                    ", Isolation Heuristic: " + ihCost + " (" + String.format("%.3f", (double) ihCost / (double) optimal) + ")" +
                    ", Local Search (epsilon: " + EPSILON1 + "): " + lsCost1 +  " (" + String.format("%.3f", (double) lsCost1 / (double) optimal) + ")" +
                    ", Local Search (epsilon: " + EPSILON2 + "): " + lsCost2 +  " (" + String.format("%.3f", (double) lsCost2 / (double) optimal) + ")" +
                    ", Local Search (epsilon: " + EPSILON3 + "): " + lsCost3 +  " (" + String.format("%.3f", (double) lsCost3 / (double) optimal) + ")" +
                    ", Local Search (epsilon: " + EPSILON4 + "): " + lsCost4 +  " (" + String.format("%.3f", (double) lsCost4 / (double) optimal) + ")" +
                    ", Local Search (epsilon: " + EPSILON5 + "): " + lsCost5 +  " (" + String.format("%.3f", (double) lsCost5 / (double) optimal) + ")" +
                    ", Cplex: " + optimal);*/

            ihratio = (double) ihCost / (double) optimal;
            lsratio1 = (double) lsCost1 / (double) optimal;
            lsratio2 = (double) lsCost2 / (double) optimal;
            lsratio3 = (double) lsCost3 / (double) optimal;
            lsratio4 = (double) lsCost4 / (double) optimal;
            lsratio5 = (double) lsCost5 / (double) optimal;
            calratio = (double) calCost / (double) optimal;

            ihDeviation.add(ihratio);
            lsDeviation1.add(lsratio1);
            lsDeviation2.add(lsratio2);
            lsDeviation3.add(lsratio3);
            lsDeviation4.add(lsratio4);
            lsDeviation5.add(lsratio5);
            calDeviation.add(calratio);

            ihratioSum += ihratio;
            lsratioSum1 += lsratio1;
            lsratioSum2 += lsratio2;
            lsratioSum3 += lsratio3;
            lsratioSum4 += lsratio4;
            lsratioSum5 += lsratio5;
            calratioSum += calratio;

            if (ihratio < ihratioMin) {

                ihratioMin = ihratio;

            } //end if

            if (lsratio1 < lsratioMin1) {

                lsratioMin1 = lsratio1;

            } //end if

            if (lsratio2 < lsratioMin2) {

                lsratioMin2 = lsratio2;

            } //end if

            if (lsratio3 < lsratioMin3) {

                lsratioMin3 = lsratio3;

            } //end if

            if (lsratio4 < lsratioMin4) {

                lsratioMin4 = lsratio4;

            } //end if

            if (lsratio5 < lsratioMin5) {

                lsratioMin5 = lsratio5;

            } //end if

            if (calratio < calratioMin) {

                calratioMin = calratio;

            } //end if

            if (ihratio > ihratioMax) {

                ihratioMax = ihratio;

            } //end if

            if (lsratio1 > lsratioMax1) {

                lsratioMax1 = lsratio1;

            } //end if

            if (lsratio2 > lsratioMax2) {

                lsratioMax2 = lsratio2;

            } //end if

            if (lsratio3 > lsratioMax3) {

                lsratioMax3 = lsratio3;

            } //end if

            if (lsratio4 > lsratioMax4) {

                lsratioMax4 = lsratio4;

            } //end if

            if (lsratio5 > lsratioMax5) {

                lsratioMax5 = lsratio5;

            } //end if

            if (calratio > calratioMax) {

                calratioMax = calratio;

            } //end if

            trials++;

        } //end while

        ihratioAvg = ihratioSum / trials;
        lsratioAvg1 = lsratioSum1 / trials;
        lsratioAvg2 = lsratioSum2 / trials;
        lsratioAvg3 = lsratioSum3 / trials;
        lsratioAvg4 = lsratioSum4 / trials;
        lsratioAvg5 = lsratioSum5 / trials;
        calratioAvg = calratioSum / trials;

        for (int i = 0; i < trials; i++) {

            ihDeviationSum += Math.pow(Math.abs(ihDeviation.get(i) - ihratioAvg), 2);
            lsDeviationSum1 += Math.pow(Math.abs(lsDeviation1.get(i) - lsratioAvg1), 2);
            lsDeviationSum2 += Math.pow(Math.abs(lsDeviation2.get(i) - lsratioAvg2), 2);
            lsDeviationSum3 += Math.pow(Math.abs(lsDeviation3.get(i) - lsratioAvg3), 2);
            lsDeviationSum4 += Math.pow(Math.abs(lsDeviation4.get(i) - lsratioAvg4), 2);
            lsDeviationSum5 += Math.pow(Math.abs(lsDeviation5.get(i) - lsratioAvg5), 2);
            calDeviationSum += Math.pow(Math.abs(calDeviation.get(i) - calratioAvg), 2);

        } //end for

        StdOut.println("Trials: " + trials +
                ", Vertices: " + vertices +
                ", Edges: " + (int) (edgesSum / trials) +
                "\nIH Avg: " + String.format("%.3f", ihratioAvg) +
                ", IH Max: " + String.format("%.3f", ihratioMax) +
                ", IH Min: " + String.format("%.3f", ihratioMin) +
                ", IH Std: " + String.format("%.3f", Math.sqrt(ihDeviationSum / trials)) +
                ", IH Time: " + TimeUnit.MILLISECONDS.convert((ihTimeSum / trials), TimeUnit.NANOSECONDS) + "ms" +
                "\nLS1 Avg: " + String.format("%.3f", lsratioAvg1) +
                ", LS1 Max: " + String.format("%.3f", lsratioMax1) +
                ", LS1 Min: " + String.format("%.3f", lsratioMin1) +
                ", LS1 Std: " + String.format("%.3f", Math.sqrt(lsDeviationSum1 / trials)) +
                ", LS1 Time: " + TimeUnit.MILLISECONDS.convert((lsTimeSum1 / trials), TimeUnit.NANOSECONDS) + "ms" +
                "\nLS2 Avg: " + String.format("%.3f", lsratioAvg2) +
                ", LS2 Max: " + String.format("%.3f", lsratioMax2) +
                ", LS2 Min: " + String.format("%.3f", lsratioMin2) +
                ", LS2 Std: " + String.format("%.3f", Math.sqrt(lsDeviationSum2 / trials)) +
                ", LS2 Time: " + TimeUnit.MILLISECONDS.convert((lsTimeSum2 / trials), TimeUnit.NANOSECONDS) + "ms" +
                "\nLS3 Avg: " + String.format("%.3f", lsratioAvg3) +
                ", LS3 Max: " + String.format("%.3f", lsratioMax3) +
                ", LS3 Min: " + String.format("%.3f", lsratioMin3) +
                ", LS3 Std: " + String.format("%.3f", Math.sqrt(lsDeviationSum3 / trials)) +
                ", LS3 Time: " + TimeUnit.MILLISECONDS.convert((lsTimeSum3 / trials), TimeUnit.NANOSECONDS) + "ms" +
                "\nLS4 Avg: " + String.format("%.3f", lsratioAvg4) +
                ", LS4 Max: " + String.format("%.3f", lsratioMax4) +
                ", LS4 Min: " + String.format("%.3f", lsratioMin4) +
                ", LS4 Std: " + String.format("%.3f", Math.sqrt(lsDeviationSum4 / trials)) +
                ", LS4 Time: " + TimeUnit.MILLISECONDS.convert((lsTimeSum4 / trials), TimeUnit.NANOSECONDS) + "ms" +
                "\nLS5 Avg: " + String.format("%.3f", lsratioAvg5) +
                ", LS5 Max: " + String.format("%.3f", lsratioMax5) +
                ", LS5 Min: " + String.format("%.3f", lsratioMin5) +
                ", LS5 Std: " + String.format("%.3f", Math.sqrt(lsDeviationSum5 / trials)) +
                ", LS5 Time: " + TimeUnit.MILLISECONDS.convert((lsTimeSum5 / trials), TimeUnit.NANOSECONDS) + "ms" +
                "\nCAL Avg: " + String.format("%.3f", calratioAvg) +
                ", CAL Max: " + String.format("%.3f", calratioMax) +
                ", CAL Min: " + String.format("%.3f", calratioMin) +
                ", CAL Std: " + String.format("%.3f", Math.sqrt(calDeviationSum / trials)) +
                ", CAL Time: " + TimeUnit.MILLISECONDS.convert((calTimeSum / trials), TimeUnit.NANOSECONDS) + "ms");

    } //end main

} //end Main
