package algorithms;

import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
import library.StdRandom;
import utility.MonteCarlo;
import utility.Pair;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Bloch-Hansen on 2017-05-11.
 */
public class DescendingThreshold implements MultiwayCutStrategy {

    private MultiwayCutStrategy solver;
    private long time;

    private double radius;
    private double calCost;

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

    @Override
    public double getCalCost() {

        return calCost;

    } //end getRadius

    private int round(FlowNetwork flowNetwork,
                      Map<Integer, double[]> vertexLabels) {

        FlowNetwork flowNetwork2 = new FlowNetwork(flowNetwork);
        Map<Integer, double[]> vertexLabels2 = vertexLabels.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        double alg = StdRandom.uniform(0.0, 1.0);
        double threshold = MonteCarlo.getPhi1();

        int cost;
        long start = System.nanoTime();

        CalinescuUtility.subdivision(flowNetwork, vertexLabels);

        if (alg <= (6.0 + (5.0 * Math.pow(3.0, 1.0 / 2.0))) / 26.0) {

            cost = CalinescuUtility.roundBuchbinder(flowNetwork, vertexLabels);

        } //end if

        else if (alg <= (19.0 - (8.0 * Math.pow(3.0, 1.0 / 2.0))) / 13.0) {

            cost = (int) CalinescuUtility.roundCalinescu(flowNetwork, vertexLabels, CalinescuUtility.singleThreshold(CalinescuUtility.uniformPermutation(flowNetwork), threshold)).index;

        } //end else if

        else {

            cost = (int) CalinescuUtility.roundCalinescu(flowNetwork, vertexLabels, CalinescuUtility.descendingThreshold(CalinescuUtility.uniformPermutation(flowNetwork))).index;

        } //end else

        time = System.nanoTime() - start;

        CalinescuUtility.subdivision(flowNetwork2, vertexLabels2);
        Pair cost2 = CalinescuUtility.roundCalinescu(flowNetwork2, vertexLabels2, CalinescuUtility.singleThreshold(CalinescuUtility.uniformPermutation(flowNetwork2), threshold));

        radius = cost2.value;
        calCost = cost2.index;

        //StdOut.println("COST1: " + cost1 + ", COST2: " + (int)cost2.index + ", COST3: " + (int)cost3.index);
        //return Math.min(Math.min(cost1, (int)cost2.index), (int)cost3.index);
        return cost;

    } //end round

    @Override
    public int computeMultiwayCut(FlowNetwork flowNetwork) {

        //Map<Integer, double[]> vertexLabels = new LinkedHashMap<>();
        Map<Integer, double[]> vertexLabels = solver.getVertexLabels();
        double[] edgeLabelSums = new double[flowNetwork.getNumEdges()];

        StdOut.println("Sharma and Vondrak's Descending Threshold");

        //solver.computeMultiwayCut(flowNetwork, edgeLabelSums, vertexLabels);

        //outputCoordinates(flowNetwork, vertexLabels, edgeLabelSums);
        int cost = round(flowNetwork, vertexLabels);

        StdOut.println("The weight of the multiway cut: " + cost);

        return cost;

    } //end computeMultiwayCut

} //end DescendingThreshold
