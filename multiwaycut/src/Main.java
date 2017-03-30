import algorithms.LocalSearch;
import cplex.MultiwayCutSolver;
import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
import algorithms.IsolationHeuristic;
import utility.*;

import java.util.LinkedList;

/**
 * Created by Bloch-Hansen on 2017-01-23.
 */
public class Main {

    private static final String CURRENT = "GL080";
    private static final String OUT_NAME = "-999.txt";
    private static final int NUM_TRIALS = 1000;

    public static void main(String[] args) {

        PropertiesReader prop = new PropertiesReader();

        GraphFormatReader reader = new GraphFormatReader();
        ConnectedComponentSearcher searcher = new ConnectedComponentSearcher();

        IsolationHeuristic isolationHeuristic = new IsolationHeuristic();
        LocalSearch localSearch = new LocalSearch();
        MultiwayCutSolver solver = new MultiwayCutSolver();

        int trials = 0;
        int vertices = 0;
        double edgesSum = 0.0;

        double ihratio = 0.0;
        double ihratioSum = 0.0;
        double ihratioAvg = 0.0;
        double ihratioMin = Double.MAX_VALUE;
        double ihratioMax = 0.0;

        double lsratio = 0.0;
        double lsratioSum = 0.0;
        double lsratioAvg = 0.0;
        double lsratioMin = Double.MAX_VALUE;
        double lsratioMax = 0.0;

        double ihDeviationSum = 0.0;
        double lsDeviationSum = 0.0;

        LinkedList<Double> ihDeviation = new LinkedList<>();
        LinkedList<Double> lsDeviation = new LinkedList<>();

        while (trials < NUM_TRIALS) {

            FlowNetwork largestConnectedComponent = searcher.getLargestConnectedComponent(reader.parse(prop.get(CURRENT), OUT_NAME));

            vertices = largestConnectedComponent.getNumVertices();
            edgesSum += largestConnectedComponent.getNumEdges();

            int ihCost = isolationHeuristic.computeMultiwayCut(new FlowNetwork(largestConnectedComponent));
            int lsCost = localSearch.computeMultiwayCut(new FlowNetwork(largestConnectedComponent));
            int optimal = solver.computeMultiwayCut(new FlowNetwork(largestConnectedComponent));

            StdOut.println("Trial: " + (trials + 1) +
                    ", Isolation Heuristic: " + ihCost + " (" + String.format("%.3f", (double) ihCost / (double) optimal) + ")" +
                    ", Local Search: " + lsCost +  " (" + String.format("%.3f", (double) lsCost / (double) optimal) + ")" +
                    ", Cplex: " + optimal);

            ihratio = (double) ihCost / (double) optimal;
            lsratio = (double) lsCost / (double) optimal;

            ihDeviation.add(ihratio);
            lsDeviation.add(lsratio);

            ihratioSum += ihratio;
            lsratioSum += lsratio;

            if (ihratio < ihratioMin) {

                ihratioMin = ihratio;

            } //end if

            if (lsratio < lsratioMin) {

                lsratioMin = lsratio;

            } //end if

            if (ihratio > ihratioMax) {

                ihratioMax = ihratio;

            } //end if

            if (lsratio > lsratioMax) {

                lsratioMax = lsratio;

            } //end if

            trials++;

        } //end while

        ihratioAvg = ihratioSum / trials;
        lsratioAvg = lsratioSum / trials;

        for (int i = 0; i < trials; i++) {

            ihDeviationSum += Math.pow(Math.abs(ihDeviation.get(i) - ihratioAvg), 2);
            lsDeviationSum += Math.pow(Math.abs(lsDeviation.get(i) - lsratioAvg), 2);

        } //end for

        StdOut.println("Trials: " + trials +
                ", Vertices: " + vertices +
                ", Edges: " + (int) (edgesSum / trials) +
                "\nIH Avg: " + String.format("%.3f", ihratioAvg) +
                ", IH Max: " + String.format("%.3f", ihratioMax) +
                ", IH Min: " + String.format("%.3f", ihratioMin) +
                ", IH Std: " + String.format("%.3f", Math.sqrt(ihDeviationSum / trials)) +
                "\nLS Avg: " + String.format("%.3f", lsratioAvg) +
                ", LS Max: " + String.format("%.3f", lsratioMax) +
                ", LS Min: " + String.format("%.3f", lsratioMin) +
                ", LS Std: " + String.format("%.3f", Math.sqrt(lsDeviationSum / trials)));

    } //end main

} //end Main
