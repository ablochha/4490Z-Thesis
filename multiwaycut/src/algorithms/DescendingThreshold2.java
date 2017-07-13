package algorithms;

import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-05-31.
 */
public class DescendingThreshold2 implements MultiwayCutStrategy {

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
        cost = CalinescuUtility.roundCalinescu(flowNetwork, vertexLabels, CalinescuUtility.descendingThreshold(CalinescuUtility.uniformPermutation(flowNetwork), b)).index;
        time = System.nanoTime() - start;

        return cost;

    } //end round

    @Override
    public double computeMultiwayCut(FlowNetwork flowNetwork) {

        Map<Integer, double[]> vertexLabels = solver.getVertexLabels();

        StdOut.println("Sharma and Vondrak's descending threshold 2");
        double cost = round(flowNetwork, vertexLabels);
        StdOut.println("Descending 2: The weight of the multiway cut: " + String.format("%.3f", cost));

        return cost;

    } //end computeMultiwayCut

} //end DescendingThreshold2
