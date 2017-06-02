package algorithms;

import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-05-31.
 */
public class DescendingThreshold1 implements MultiwayCutStrategy{

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

        double b = (2.0 * Math.pow(3.0, 1.0 / 2.0)) - 3.0;

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

        StdOut.println("Sharma and Vondrak's descending threshold 1");
        double cost = round(flowNetwork, vertexLabels);
        StdOut.println("The weight of the multiway cut: " + String.format("%.3f", cost));

        return cost;

    } //end computeMultiwayCut

} //end DescendingThreshold1
