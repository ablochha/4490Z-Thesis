import algorithms.MultiwayCutStrategy;
import datastructures.flownetwork.FlowNetwork;
import library.Out;
import library.StdOut;
import utility.GraphFormatReader;
import utility.Pair;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Bloch-Hansen on 2017-05-03.
 */
public class TestObject {

    private static final String SLASH = System.getProperty("file.separator");

    private FlowNetwork original;
    private FlowNetwork worstCase;

    private MultiwayCutStrategy strategy;

    private String name;

    private int cost;
    private int calCost;
    private int vertices;
    private int edges;
    private int count;

    private double initialCapacity1;
    private double initialCapacity2;

    private double max;

    private double radius;
    private double radiusSum;
    private double radiusAvg;
    private double radiusMin;
    private double radiusMax;

    private double ratio;
    private double ratioSum;
    private double ratioAvg;
    private double ratioMin;
    private double ratioMax;
    private double ratioCal;

    private double deviationSum;

    private long timeSum;

    private LinkedList<Double> deviation;
    private LinkedList<Pair> radiusRatio;

    public TestObject(String name, MultiwayCutStrategy strategy) {

        ratio = 0.0;
        ratioSum = 0.0;
        ratioAvg = 0.0;
        ratioMin = Double.MAX_VALUE;
        ratioMax = 0.0;
        ratioCal = 0.0;

        radius = 0.0;
        radiusSum = 0.0;
        radiusAvg = 0.0;
        radiusMin = Double.MAX_VALUE;
        radiusMax = 0.0;

        max = 0.0;

        timeSum = 0;
        count = 0;

        deviation = new LinkedList<>();
        radiusRatio = new LinkedList<>();

        this.strategy = strategy;
        this.name = name;

    } //end TestObject

    public TestObject(String name, MultiwayCutStrategy strategy, String initialLabeller, double epsilon, MultiwayCutStrategy ih) {

        this(name, strategy);
        this.strategy.setEpsilon(epsilon);
        this.strategy.setInitialLabeller(initialLabeller);
        this.strategy.setIsolationHeuristic(ih);

    } //end TestObject

    public TestObject(String name, MultiwayCutStrategy strategy, MultiwayCutStrategy solver) {

        this(name, strategy);
        this.strategy.setSolver(solver);

    } //end TestObject

    public void run(FlowNetwork largestConnectedComponent, int vertices, int edges, double initialCapacity1, double initialCapacity2) {

        this.vertices = vertices;
        this.edges = edges;
        this.initialCapacity1 = initialCapacity1;
        this.initialCapacity2 = initialCapacity2;

        original = new FlowNetwork(largestConnectedComponent);
        cost = strategy.computeMultiwayCut(largestConnectedComponent);
        timeSum += strategy.getTime();

        if (name.equals("Calinescu") || name.equals("Buchbinder") || name.equals("Independent Threshold")) {

            radius = strategy.getRadius();

        } //end if

        if (name.equals("Buchbinder") || name.equals("Independent Threshold")) {

            calCost = (int) strategy.getCalCost();

        } //end if

    } //end run

    public double update(int optimal) {

        ratio = (double) cost / (double) optimal;
        ratioCal = (double) calCost / (double) optimal;
        deviation.add(ratio);
        ratioSum += ratio;

        if (ratio < ratioMin) {

            ratioMin = ratio;

        } //end if

        if (ratio > ratioMax) {

            ratioMax = ratio;
            worstCase = original;

        } //end if

        if (name.equals("Calinescu") && ratio > max) {

            max = ratio;

        } //end if

        if ((name.equals("Buchbinder") || name.equals("Independent Threshold"))&& ratioCal > max) {

            max = ratioCal;

        } //end if

        if (name.equals("Calinescu") || name.equals("Buchbinder") || name.equals("Independent Threshold")) {

            radiusSum += radius;

            if (name.equals("Calinescu")) {

                radiusRatio.add(new Pair(ratio, radius));

            } //end if

            if (name.equals("Buchbinder") || name.equals("Independent Threshold")) {

                radiusRatio.add(new Pair(ratioCal, radius));

            } //end if

            if (radius < radiusMin) {

                radiusMin = radius;

            } //end if

            if (radius > radiusMax) {

                radiusMax = radius;

            } //end if

        } //end if

        return ratio;

    } //end update

    public void calculate() {

        ratioAvg = ratioSum / deviation.size();

        for (int i = 0; i < deviation.size(); i++) {

            deviationSum += Math.pow(Math.abs(deviation.get(i) - ratioAvg), 2);

        } //end for

        if (name.equals("Calinescu") || name.equals("Buchbinder") || name.equals("Independent Threshold")) {

            radiusAvg = radiusSum / deviation.size();

        } //end if

    } //end calculate

    public int getCost() {

        return cost;

    } //end getCost

    public void outputRadiusRatio(GraphFormatReader reader) {

        Collections.sort(radiusRatio, Collections.reverseOrder());
        count++;

        String outFile = "data" + SLASH + "radiusratio" + SLASH +
                        String.format("%.3f", max) + "_" +
                        vertices + "V" +
                        original.getK() + "K" +
                        edges + "E" +
                        "_" + initialCapacity1 + "_" + (initialCapacity2 - 1) +
                        name +
                        "_" + count+
                        ".csv";

        Out out = new Out(outFile);
        out.println("Radius,Ratio");

        for (int i = 0; i < radiusRatio.size(); i++) {

            out.println(String.format("%.3f", radiusRatio.get(i).value) + "," + String.format("%.3f", radiusRatio.get(i).index));

        } //end for

        out.close();

        reader.outputGraph(original, ".txt",
                "data" + SLASH + "worstcase" + SLASH + "radiusratio" + SLASH +
                        String.format("%.3f", max) + "_" +
                        vertices + "V" +
                        original.getK() + "K" +
                        edges + "E" +
                        "_" + initialCapacity1 + "_" + (initialCapacity2 - 1) +
                        "_" + count +
                        ".txt");

        radiusRatio.clear();
        max = 0.0;

    } //end outputRadiusRatio

    public void clearRadiusRatio() {

        radiusRatio.clear();
        max = 0.0;

    } //end clearRadiusRatio

    public void output(GraphFormatReader reader) {

        if (deviation.size() > 1) {

            reader.outputGraph(worstCase, ".txt", "data" + SLASH + "worstcase" + SLASH +
                    worstCase.getNumVertices() + "V" + worstCase.getNumEdges() + "E" +
                    name + "_" + String.format("%.3f", ratioMax) + ".txt");

        } //end if

        StdOut.println("\n" + name + "\n" +
                "Avg: " + String.format("%.3f", ratioAvg) +
                ", Max: " + String.format("%.3f", ratioMax) +
                ", Min: " + String.format("%.3f", ratioMin) +
                ", Std: " + String.format("%.3f", Math.sqrt(deviationSum / deviation.size())) +
                ", Time: " + TimeUnit.MILLISECONDS.convert((timeSum / deviation.size()), TimeUnit.NANOSECONDS) + "ms");

        if (name.equals("Calinescu") || name.equals("Buchbinder") || name.equals("Independent Threshold")) {

            StdOut.println("Radius Avg: " + String.format("%.3f", radiusAvg) +
                    ", Radius Max: " + String.format("%.3f", radiusMax) +
                    ", Radius Min: " + String.format("%.3f", radiusMin));

        } //end if

    } //end output

} //end TestObject
