package algorithms;

import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
import library.StdRandom;
import utility.MonteCarlo;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-05-31.
 */
public class SharmaVondrak3Mix implements MultiwayCutStrategy {

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

        double p1 = (6.0 + (5.0 * Math.pow(3.0, 1.0 / 2.0))) / 26.0;
        double p2 = (19.0 - (8.0 * Math.pow(3.0, 1.0 / 2.0))) / 13.0;
        double b = (2.0 * Math.pow(3.0, 1.0 / 2.0)) - 3.0;
        double alg = StdRandom.uniform(0.0, 1.0);
        double rand = MonteCarlo.getPhi1();

        double cost;
        long start = System.nanoTime();

        CalinescuUtility.subdivision(flowNetwork, vertexLabels);

        if (alg <= p1) {

            //StdOut.println("Clocks: " + alg + "less than " + p1);
            cost = CalinescuUtility.roundBuchbinder(flowNetwork, vertexLabels);

        } //end if

        else if (alg <= p1 + p2) {

            //StdOut.println("Single: " + alg + "less than " + (p1 + p2));
            cost = CalinescuUtility.roundCalinescu(flowNetwork, vertexLabels, CalinescuUtility.singleThreshold(CalinescuUtility.uniformPermutation(flowNetwork), rand)).index;

        } //end else if

        else {

            //StdOut.println("Descending: " + alg + "less than 1");
            cost = CalinescuUtility.roundCalinescu(flowNetwork, vertexLabels, CalinescuUtility.descendingThreshold(CalinescuUtility.uniformPermutation(flowNetwork), b)).index;

        } //end else

        time = System.nanoTime() - start;

        //StdOut.println("COST1: " + cost1 + ", COST2: " + (int)cost2.index + ", COST3: " + (int)cost3.index);
        //return Math.min(Math.min(cost1, (int)cost2.index), (int)cost3.index);
        return cost;

    } //end round

    @Override
    public double computeMultiwayCut(FlowNetwork flowNetwork) {

        Map<Integer, double[]> vertexLabels = solver.getVertexLabels();

        StdOut.println("Sharma and Vondrak's 3 ingredient mixture");
        double cost = round(flowNetwork, vertexLabels);
        StdOut.println("3 Mix: The weight of the multiway cut: " + String.format("%.3f", cost));

        return cost;

    } //end computeMultiwayCut

} //end SharmaVondrak3Mix
