package algorithms;

import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-05-07.
 */
public class ExponentialClocks implements MultiwayCutStrategy {

    private MultiwayCutStrategy solver;

    private long time;

    public void setSolver(MultiwayCutStrategy solver) {

        this.solver = solver;

    } //end setSolver

    public long getTime() {

        return time;

    } //end getTime

    @Override
    public int computeMultiwayCut(FlowNetwork flowNetwork) {

        Map<Integer, double[]> vertexLabels = new LinkedHashMap<>();

        double[] edgeLabelSums = new double[flowNetwork.getNumEdges()];

        StdOut.println("Exponential Clocks");

        solver.computeMultiwayCut(flowNetwork, edgeLabelSums, vertexLabels);

        long start = System.nanoTime();
        CalinescuUtility.subdivision(flowNetwork, vertexLabels);
        //outputCoordinates(flowNetwork, vertexLabels, edgeLabelSums);
        int cost = CalinescuUtility.roundBuchbinder(flowNetwork, vertexLabels);
        time = System.nanoTime() - start;

        StdOut.println("The weight of the multiway cut: " + cost);

        return cost;

    } //end computeMultiwayCut

} //end ExponentialClocks
