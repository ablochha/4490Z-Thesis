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

    private double radius;

    public void setSolver(MultiwayCutStrategy solver) {

        this.solver = solver;

    } //end setSolver

    public long getTime() {

        return time;

    } //end getTime

    @Override
    public double getRadius() {

        return radius;

    } //end getRadius

    /**
     * Computes a minimum multiway cut.
     */
    @Override
    public int computeMultiwayCut(FlowNetwork flowNetwork) {

        //Map<Integer, double[]> vertexLabels = new LinkedHashMap<>();
        Map<Integer, double[]> vertexLabels = solver.getVertexLabels();
        double[] edgeLabelSums = new double[flowNetwork.getNumEdges()];

        StdOut.println("Calinescu");

        //solver.computeMultiwayCut(flowNetwork, edgeLabelSums, vertexLabels);

        long start = System.nanoTime();
        //outputCoordinates(flowNetwork, vertexLabels, edgeLabelSums);
        CalinescuUtility.subdivision(flowNetwork, vertexLabels);
        Pair cost = CalinescuUtility.roundCalinescu(flowNetwork, vertexLabels, CalinescuUtility.singleThreshold(CalinescuUtility.binomialPermutation(flowNetwork), StdRandom.uniform(0.0, 1.0)));
        time = System.nanoTime() - start;
        radius = cost.value;

        StdOut.println("The weight of the multiway cut: " + (int)cost.index);

        return (int)cost.index;

    } //end computeMultiwayCut

} //end Calinescu
