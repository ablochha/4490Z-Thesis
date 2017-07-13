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
public class TestObject implements Runnable {

    private static final String SLASH = System.getProperty("file.separator");
    private static final String SHARCNETPREFIX = SLASH + "work" +
                                                SLASH + "ablochha" +
                                                SLASH + "thesis" +
                                                SLASH + "multiwaycut" +
                                                SLASH + "data";

    private FlowNetwork original;
    private FlowNetwork workingCopy;
    private FlowNetwork worstCase;

    private MultiwayCutStrategy strategy;

    private String name;
    private String prefix;

    private double cost;
    private double calCost;
    private double optimal;

    private int vertices;
    private int edges;
    private int count;
    private int repetitions;

    private double initialCapacity1;
    private double initialCapacity2;

    private double max;

    private double threshold;
    private double thresholdSum;
    private double thresholdAvg;
    private double thresholdMin;
    private double thresholdMax;

    private double ratio;
    private double ratioSum;
    private double ratioAvg;
    private double ratioMin;
    private double ratioMax;
    private double ratioCal;

    private double repRatioAvg;
    private double repRatioSum;
    private double repRatioMin;
    private double repRatioMax;
    private double repThresholdAvg;

    private double deviationSum;

    private long time;
    private long timeSum;

    private LinkedList<Double> deviation;
    private LinkedList<Pair> thresholdRatio;
    private LinkedList<Integer> numCompleted;

    public TestObject(String name, MultiwayCutStrategy strategy, LinkedList<Integer> numCompleted, boolean windows) {

        ratio = 0.0;
        ratioSum = 0.0;
        ratioAvg = 0.0;
        ratioMin = Double.MAX_VALUE;
        ratioMax = 0.0;
        ratioCal = 0.0;

        repRatioAvg = 0.0;
        repRatioSum = 0.0;
        repRatioMin = Double.MAX_VALUE;
        repRatioMax = 0.0;
        repThresholdAvg = 0.0;

        threshold = 0.0;
        thresholdSum = 0.0;
        thresholdAvg = 0.0;
        thresholdMin = Double.MAX_VALUE;
        thresholdMax = 0.0;

        max = 0.0;

        timeSum = 0;
        count = 0;

        deviation = new LinkedList<>();
        thresholdRatio = new LinkedList<>();
        this.numCompleted = numCompleted;

        this.strategy = strategy;
        this.name = name;

        if (windows) {

            prefix = "data";

        } //end if

        else {

            prefix = SHARCNETPREFIX;

        } //end else

    } //end TestObject

    public TestObject(String name, MultiwayCutStrategy strategy, String initialLabeller, double epsilon, MultiwayCutStrategy ih, LinkedList<Integer> numCompleted, boolean windows) {

        this(name, strategy, numCompleted, windows);
        this.strategy.setEpsilon(epsilon);
        this.strategy.setInitialLabeller(initialLabeller);
        this.strategy.setIsolationHeuristic(ih);

    } //end TestObject

    public TestObject(String name, MultiwayCutStrategy strategy, MultiwayCutStrategy solver, LinkedList<Integer> numCompleted, boolean windows) {

        this(name, strategy, numCompleted, windows);
        this.strategy.setSolver(solver);

    } //end TestObject

    public void setGraph(FlowNetwork largestConnectedComponent, int vertices, int edges, double initialCapacity1, double initialCapacity2, double optimal, int repetitions) {

        this.vertices = vertices;
        this.edges = edges;
        this.initialCapacity1 = initialCapacity1;
        this.initialCapacity2 = initialCapacity2;
        this.original = new FlowNetwork(largestConnectedComponent);
        this.workingCopy = largestConnectedComponent;
        this.optimal = optimal;
        this.repetitions = repetitions;

    } //end setGraph

    public void run() {

        cost = strategy.computeMultiwayCut(workingCopy);
        time = strategy.getTime();

        if (name.equals("Calinescu") || name.equals("Buchbinder") || name.equals("Single Threshold 1") || name.equals("Single Threshold 2")) {

            threshold = strategy.getThreshold();

        } //end if

        if (name.equals("Buchbinder") || name.equals("Single Threshold 1") || name.equals("Single Threshold 2")) {

            calCost = strategy.getCalCost();

        } //end if

        update();

        synchronized (numCompleted) {

            numCompleted.add(1);

        } //end synchronized

    } //end run

    public double update() {

        ratio = cost / optimal;
        ratioCal = calCost / optimal;

        if (repetitions == 1) {

            deviation.add(ratio);
            ratioSum += ratio;
            repRatioSum += ratio;
            timeSum += time;

            if (ratio < ratioMin) {

                ratioMin = ratio;

            } //end if

            if (ratio > ratioMax) {

                ratioMax = ratio;
                worstCase = original;

            } //end if

            if (ratio < repRatioMin) {

                repRatioMin = ratio;

            } //end if

            if (ratio > repRatioMax) {

                repRatioMax = ratio;

            } //end if

        } //end if

        else {

            repRatioSum += ratio;

            if (ratio < repRatioMin) {

                repRatioMin = ratio;

            } //end if

            if (ratio > repRatioMax) {

                repRatioMax = ratio;
                worstCase = original;

            } //end if

        } //end else

        if (name.equals("Calinescu") && ratio > max) {

            max = ratio;

        } //end if

        if (name.equals("Buchbinder") || name.equals("Single Threshold 1") || name.equals("Single Threshold 2") && ratioCal > max) {

            max = ratioCal;

        } //end if

        if (name.equals("Calinescu") || name.equals("Buchbinder") || name.equals("Single Threshold 1") || name.equals("Single Threshold 2")) {

            repThresholdAvg += threshold;

            if (name.equals("Calinescu")) {

                thresholdRatio.add(new Pair(ratio, threshold));

            } //end if

            if (name.equals("Buchbinder") || name.equals("Single Threshold 1") || name.equals("Single Threshold 2")) {

                thresholdRatio.add(new Pair(ratioCal, threshold));

            } //end if

            if (threshold < thresholdMin) {

                thresholdMin = threshold;

            } //end if

            if (threshold > thresholdMax) {

                thresholdMax = threshold;

            } //end if

        } //end if

        return ratio;

    } //end update

    public void calculate(int numTrials, int numRepititions) {

        ratioAvg = ratioSum / (double) numTrials;
        repRatioAvg = repRatioSum / (double) (numTrials * numRepititions);
        //StdOut.println("THIS IS AVG: " + ratioAvg + " THIS IS SUM: " + ratioSum);

        for (int i = 0; i < deviation.size(); i++) {

            deviationSum += Math.pow(Math.abs(deviation.get(i) - ratioAvg), 2);

        } //end for

        if (name.equals("Calinescu") || name.equals("Buchbinder") || name.equals("Single Threshold 1") || name.equals("Single Threshold 2")) {

            thresholdAvg = thresholdSum / deviation.size();

        } //end if

    } //end calculate

    public double getCost() {

        return cost;

    } //end getCost

    public boolean isFractional() {

        return strategy.isFractional();

    } //end isFractional

    public double getRatio() {

        return ratio;

    } //end getRatio

    public void outputThresholdRatio(GraphFormatReader reader) {

        Collections.sort(thresholdRatio, Collections.reverseOrder());
        count++;

        String outFile = prefix + SLASH + "thresholdratio" + SLASH +
                        String.format("%.3f", max) + "_" +
                        vertices + "V" +
                        original.getK() + "K" +
                        edges + "E" +
                        "_" + initialCapacity1 + "_" + (initialCapacity2 - 1) +
                        name +
                        "_" + count+
                        ".csv";

        Out out = new Out(outFile);
        out.println("Threshold,Ratio");

        for (int i = 0; i < thresholdRatio.size(); i++) {

            out.println(String.format("%.3f", thresholdRatio.get(i).value) + "," + String.format("%.3f", thresholdRatio.get(i).index));

        } //end for

        out.close();

        reader.outputGraph(original, ".txt",
                "data" + SLASH + "worstcase" + SLASH + "thresholdratio" + SLASH +
                        String.format("%.3f", max) + "_" +
                        vertices + "V" +
                        original.getK() + "K" +
                        edges + "E" +
                        "_" + initialCapacity1 + "_" + (initialCapacity2 - 1) +
                        name +
                        "_" + count +
                        ".txt");

        thresholdRatio.clear();
        max = 0.0;

    } //end outputThresholdRatio

    public void clearThresholdRatio(int repetitions) {

        thresholdSum += repThresholdAvg / (double) repetitions;
        max = 0.0;
        repThresholdAvg = 0.0;
        thresholdRatio.clear();

    } //end clearThresholdRatio

    public double getRatioAvg() {

        return ratioAvg;

    } //end getRatioAvg

    public double getRatioMax() {

        return ratioMax;

    } //end getRatioMax

    public double getStd() {

        return Math.sqrt(deviationSum / deviation.size());

    } //end getStd

    public void output(GraphFormatReader reader) {

        /*if (deviation.size() > 1) {

            reader.outputGraph(worstCase, ".txt", prefix + SLASH + "worstcase" + SLASH +
                    worstCase.getNumVertices() + "V" + worstCase.getNumEdges() + "E" +
                    name + "_" + String.format("%.3f", ratioMax) + ".txt");

        } //end if*/

        StdOut.println("\n" + name + "\n" +
                "Avg: " + String.format("%.3f", ratioAvg) +
                ", Max: " + String.format("%.3f", ratioMax) +
                ", Min: " + String.format("%.3f", ratioMin) +
                ", Std: " + String.format("%.3f", Math.sqrt(deviationSum / deviation.size())) +
                ", Time: " + TimeUnit.MILLISECONDS.convert((timeSum / deviation.size()), TimeUnit.NANOSECONDS) + "ms");

        if (name.equals("Calinescu") || name.equals("Buchbinder") || name.equals("Single Threshold 1") || name.equals("Single Threshold 2")) {

            StdOut.println("Threshold Avg: " + String.format("%.3f", thresholdAvg) +
                    ", Threshold Max: " + String.format("%.3f", thresholdMax) +
                    ", Threshold Min: " + String.format("%.3f", thresholdMin));
            StdOut.println("Full Avg: " + String.format("%.3f", repRatioAvg) +
                    ", Full Max: " + String.format("%.3f", repRatioMax) +
                    ", Full Min: " + String.format("%.3f", repRatioMin));

        } //end if

        if (name.equals("Local Search (epsilon = " + this.strategy.getEpsilon() + ", Random)") ||
                name.equals("Exponential Clocks") ||
                name.equals("Descending Threshold 1") ||
                name.equals("Descending Threshold 2") ||
                name.equals("Independent Threshold") ||
                name.equals("3-Ingredient Mix") ||
                name.equals("4-Ingredient Mix")) {

            StdOut.println("Full Avg: " + String.format("%.3f", repRatioAvg) +
                    ", Full Max: " + String.format("%.3f", repRatioMax) +
                    ", Full Min: " + String.format("%.3f", repRatioMin));

        } //end if

    } //end output

} //end TestObject
