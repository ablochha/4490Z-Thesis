package algorithms;

import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-04-24.
 */
public class Calinescu implements MultiwayCutStrategy {

    private MultiwayCutStrategy solver;

    private long time;

    public void setSolver(MultiwayCutStrategy solver) {

        this.solver = solver;

    } //end setSolver

    public long getTime() {

        return time;

    } //end getTime

    /**
     * Computes a minimum multiway cut.
     */
    @Override
    public int computeMultiwayCut(FlowNetwork flowNetwork) {

        Map<Integer, double[]> vertexLabels = new LinkedHashMap<>();
        double[] edgeLabelSums = new double[flowNetwork.getNumEdges()];

        StdOut.println("Calinescu");

        solver.computeMultiwayCut(flowNetwork, edgeLabelSums, vertexLabels);

        long start = System.nanoTime();
        //outputCoordinates(flowNetwork, vertexLabels, edgeLabelSums);
        CalinescuUtility.subdivision(flowNetwork, vertexLabels);
        int cost = CalinescuUtility.roundCalinescu(flowNetwork, vertexLabels, CalinescuUtility.binomialPermutation(flowNetwork));
        time = System.nanoTime() - start;

        StdOut.println("The weight of the multiway cut: " + cost);

        return cost;

    } //end computeMultiwayCut

} //end Calinescu