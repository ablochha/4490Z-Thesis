package algorithms;

import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
import library.StdRandom;
import utility.MonteCarlo;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-05-31.
 */
public class SharmaVondrak4Mix implements MultiwayCutStrategy {

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

        double p1 = 0.31052;
        double p2 = 0.305782;
        double p3 = 0.015338;
        double p4 = 0.36836;
        double b = 6.0 / 11.0;
        double alg = StdRandom.uniform(0.0, 1.0);
        double rand = MonteCarlo.getPhi2();

        double cost;
        long start = System.nanoTime();

        CalinescuUtility.subdivision(flowNetwork, vertexLabels);

        if (alg <= p1) {

            cost = CalinescuUtility.roundBuchbinder(flowNetwork, vertexLabels);

        } //end if

        else if (alg <= p1 + p2) {

            cost = CalinescuUtility.roundCalinescu(flowNetwork, vertexLabels, CalinescuUtility.singleThreshold(CalinescuUtility.uniformPermutation(flowNetwork), rand)).index;

        } //end else if

        else if (alg <= p1 + p2 + p3) {

            cost = CalinescuUtility.roundCalinescu(flowNetwork, vertexLabels, CalinescuUtility.descendingThreshold(CalinescuUtility.uniformPermutation(flowNetwork), b)).index;

        } //end else if

        else {

            cost = CalinescuUtility.roundCalinescu(flowNetwork, vertexLabels, CalinescuUtility.independentThreshold(CalinescuUtility.uniformPermutation(flowNetwork), b)).index;

        } //end else

        time = System.nanoTime() - start;

        //StdOut.println("COST1: " + cost1 + ", COST2: " + (int)cost2.index + ", COST3: " + (int)cost3.index + ", COST4: " + (int)cost4.index);
        //return Math.min(Math.min(Math.min(cost1, (int)cost2.index), (int)cost3.index), (int)cost4.index);
        return cost;

    } //end round

    @Override
    public double computeMultiwayCut(FlowNetwork flowNetwork) {

        Map<Integer, double[]> vertexLabels = solver.getVertexLabels();

        StdOut.println("Sharma and Vondrak's 4 ingredient mixture");
        double cost = round(flowNetwork, vertexLabels);
        StdOut.println("4 Mix: The weight of the multiway cut: " + String.format("%.3f", cost));

        return cost;

    } //end computeMultiwayCut

} //end SharmaVondrak4Mix
