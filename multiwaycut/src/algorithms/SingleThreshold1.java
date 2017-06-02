package algorithms;

import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
import utility.MonteCarlo;
import utility.Pair;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-05-31.
 */
public class SingleThreshold1 implements MultiwayCutStrategy {

    private MultiwayCutStrategy solver;
    private long time;

    private double threshold;
    private double calCost;

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

    @Override
    public double getCalCost() {

        return calCost;

    } //end getCalCost

    private double round(FlowNetwork flowNetwork,
                         Map<Integer, double[]> vertexLabels) {

        double rand = MonteCarlo.getPhi1();
        long start = System.nanoTime();

        CalinescuUtility.subdivision(flowNetwork, vertexLabels);
        Pair cost = CalinescuUtility.roundCalinescu(flowNetwork, vertexLabels, CalinescuUtility.singleThreshold(CalinescuUtility.uniformPermutation(flowNetwork), rand));
        time = System.nanoTime() - start;

        threshold = cost.value;
        calCost = cost.index;

        return cost.index;

    } //end round

    @Override
    public double computeMultiwayCut(FlowNetwork flowNetwork) {

        Map<Integer, double[]> vertexLabels = solver.getVertexLabels();

        StdOut.println("Sharma and Vondrak's single threshold 1");
        double cost = round(flowNetwork, vertexLabels);
        StdOut.println("The weight of the multiway cut: " + String.format("%.3f", cost));

        return cost;

    } //end computeMultiwayCut

} //end SingleThreshold1
