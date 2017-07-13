package algorithms;

import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
import library.StdRandom;
import utility.Pair;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-04-24.
 */
public class Calinescu implements MultiwayCutStrategy {

    private MultiwayCutStrategy solver;

    private long time;

    private double threshold;

    public void setSolver(MultiwayCutStrategy solver) {

        this.solver = solver;

    } //end setSolver

    public long getTime() {

        return time;

    } //end getTime

    @Override
    public double getThreshold() {

        return threshold;

    } //end getThreshold

    /**
     * Computes a minimum multiway cut.
     */
    @Override
    public double computeMultiwayCut(FlowNetwork flowNetwork) {

        Map<Integer, double[]> vertexLabels = solver.getVertexLabels();

        StdOut.println("Calinescu");
        long start = System.nanoTime();

        CalinescuUtility.subdivision(flowNetwork, vertexLabels);
        Pair cost = CalinescuUtility.roundCalinescu(flowNetwork, vertexLabels, CalinescuUtility.singleThreshold(CalinescuUtility.binomialPermutation(flowNetwork), StdRandom.uniform(0.0, 1.0)));
        time = System.nanoTime() - start;
        threshold = cost.value;

        StdOut.println("Calinescu: The weight of the multiway cut: " + String.format("%.3f", cost.index));
        return cost.index;

    } //end computeMultiwayCut

} //end Calinescu
