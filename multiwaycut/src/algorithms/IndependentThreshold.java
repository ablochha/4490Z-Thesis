package algorithms;

import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
import library.StdRandom;
import utility.MonteCarlo;
import utility.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Bloch-Hansen on 2017-05-11.
 */
public class IndependentThreshold implements MultiwayCutStrategy{

    private MultiwayCutStrategy solver;
    private long time;

    public void setSolver(MultiwayCutStrategy solver) {

        this.solver = solver;

    } //end setSolver

    public long getTime() {

        return time;

    } //end getTime

    private double round(FlowNetwork flowNetwork,
                      Map<Integer, double[]> vertexLabels) {

        double b = 6.0 / 11.0;

        double cost;
        long start = System.nanoTime();

        CalinescuUtility.subdivision(flowNetwork, vertexLabels);
        cost = CalinescuUtility.roundCalinescu(flowNetwork, vertexLabels, CalinescuUtility.independentThreshold(CalinescuUtility.uniformPermutation(flowNetwork), b)).index;
        time = System.nanoTime() - start;

        return cost;

    } //end round

    @Override
    public double computeMultiwayCut(FlowNetwork flowNetwork) {

        Map<Integer, double[]> vertexLabels = solver.getVertexLabels();

        StdOut.println("Sharma and Vondrak's independent threshold");
        double cost = round(flowNetwork, vertexLabels);
        StdOut.println("Independent: The weight of the multiway cut: " + String.format("%.3f", cost));

        return cost;

    } //end computeMultiwayCut

} //end IndependentThreshold
