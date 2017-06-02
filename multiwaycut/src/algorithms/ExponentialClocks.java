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
    public double computeMultiwayCut(FlowNetwork flowNetwork) {

        Map<Integer, double[]> vertexLabels = solver.getVertexLabels();

        StdOut.println("Exponential Clocks");
        long start = System.nanoTime();

        CalinescuUtility.subdivision(flowNetwork, vertexLabels);
        double cost = CalinescuUtility.roundBuchbinder(flowNetwork, vertexLabels);
        time = System.nanoTime() - start;

        StdOut.println("The weight of the multiway cut: " + String.format("%.3f", cost));
        return cost;

    } //end computeMultiwayCut

} //end ExponentialClocks
