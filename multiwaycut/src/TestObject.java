import algorithms.MultiwayCutStrategy;
import datastructures.flownetwork.FlowNetwork;
import library.StdOut;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Bloch-Hansen on 2017-05-03.
 */
public class TestObject {

    private MultiwayCutStrategy strategy;

    private String name;

    private int trials;
    private int cost;

    private double ratio;
    private double ratioSum;
    private double ratioAvg;
    private double ratioMin;
    private double ratioMax;

    private double deviationSum;

    private long start;
    private long time;
    private long timeSum;

    private LinkedList<Double> deviation;

    public TestObject(int trials, String name, MultiwayCutStrategy strategy) {

        ratio = 0.0;
        ratioSum = 0.0;
        ratioAvg = 0.0;
        ratioMin = Double.MAX_VALUE;
        ratioMax = 0.0;

        timeSum = 0;

        deviation = new LinkedList<>();

        this.strategy = strategy;
        this.name = name;
        this.trials = trials;

    } //end TestObject

    public TestObject(int trials, String name, MultiwayCutStrategy strategy, double epsilon) {

        this(trials, name, strategy);
        this.strategy.setEpsilon(epsilon);

    } //end TestObject

    public TestObject(int trials, String name, MultiwayCutStrategy strategy, MultiwayCutStrategy solver) {

        this(trials, name, strategy);
        this.strategy.setSolver(solver);

    } //end TestObject

    public void run(FlowNetwork largestConnectedComponent) {

        start = System.nanoTime();
        cost = strategy.computeMultiwayCut(largestConnectedComponent);
        time = System.nanoTime() - start;
        timeSum += time;

    } //end run

    public void update(int optimal) {

        ratio = (double) cost / (double) optimal;
        deviation.add(ratio);
        ratioSum += ratio;

        if (ratio < ratioMin) {

            ratioMin = ratio;

        } //end if

        if (ratio > ratioMax) {

            ratioMax = ratio;

        } //end if

    } //end update

    public void calculate() {

        ratioAvg = ratioSum / trials;

        for (int i = 0; i < trials; i++) {

            deviationSum += Math.pow(Math.abs(deviation.get(i) - ratioAvg), 2);

        } //end for

    } //end calculate

    public int getCost() {

        return cost;

    } //end getCost

    public void output() {

        StdOut.println("\n" + name + "\n" +
                "Avg: " + String.format("%.3f", ratioAvg) +
                ", Max: " + String.format("%.3f", ratioMax) +
                ", Min: " + String.format("%.3f", ratioMin) +
                ", Std: " + String.format("%.3f", Math.sqrt(deviationSum / trials)) +
                ", Time: " + TimeUnit.MILLISECONDS.convert((timeSum / trials), TimeUnit.NANOSECONDS) + "ms");

    } //end output

} //end TestObject
