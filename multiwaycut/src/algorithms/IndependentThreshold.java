package algorithms;

import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
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
        FlowNetwork flowNetwork3 = new FlowNetwork(flowNetwork);
        FlowNetwork flowNetwork4 = new FlowNetwork(flowNetwork);

        Map<Integer, double[]> vertexLabels2 = vertexLabels.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        Map<Integer, double[]> vertexLabels3 = vertexLabels.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        Map<Integer, double[]> vertexLabels4 = vertexLabels.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        CalinescuUtility.subdivision(flowNetwork, vertexLabels);
        int cost1 = CalinescuUtility.roundBuchbinder(flowNetwork, vertexLabels);

        CalinescuUtility.subdivision(flowNetwork2, vertexLabels2);
        Pair cost2 = CalinescuUtility.roundCalinescu(flowNetwork2, vertexLabels2, CalinescuUtility.singleThreshold(CalinescuUtility.uniformPermutation(flowNetwork2)));

        CalinescuUtility.subdivision(flowNetwork3, vertexLabels3);
        Pair cost3 = CalinescuUtility.roundCalinescu(flowNetwork3, vertexLabels3, CalinescuUtility.descendingThreshold(CalinescuUtility.uniformPermutation(flowNetwork3)));

        CalinescuUtility.subdivision(flowNetwork4, vertexLabels4);
        Pair cost4 = CalinescuUtility.roundCalinescu(flowNetwork4, vertexLabels4, CalinescuUtility.independentThreshold(CalinescuUtility.uniformPermutation(flowNetwork4)));

        radius = cost2.value;
        calCost = cost2.index;

        StdOut.println("COST1: " + cost1 + ", COST2: " + (int)cost2.index + ", COST3: " + (int)cost3.index + ", COST4: " + (int)cost4.index);
        return Math.min(Math.min(Math.min(cost1, (int)cost2.index), (int)cost3.index), (int)cost4.index);
        //return cost1;

    } //end round

    @Override
    public int computeMultiwayCut(FlowNetwork flowNetwork) {

        //Map<Integer, double[]> vertexLabels = new LinkedHashMap<>();
        Map<Integer, double[]> vertexLabels = solver.getVertexLabels();
        double[] edgeLabelSums = new double[flowNetwork.getNumEdges()];

        StdOut.println("Sharma and Vondrak's Independent Threshold");

        //solver.computeMultiwayCut(flowNetwork, edgeLabelSums, vertexLabels);

        long start = System.nanoTime();
        //outputCoordinates(flowNetwork, vertexLabels, edgeLabelSums);
        int cost = round(flowNetwork, vertexLabels);
        time = System.nanoTime() - start;

        StdOut.println("The weight of the multiway cut: " + cost);

        return cost;

    } //end computeMultiwayCut

} //end IndependentThreshold
