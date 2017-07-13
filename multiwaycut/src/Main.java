import algorithms.*;
import cplex.MultiwayCutSolver;
import cplex.Simplex;
import datastructures.flownetwork.FlowNetwork;
import library.Out;
import library.StdOut;
import utility.*;

import java.util.LinkedList;

/**
 * Created by Bloch-Hansen on 2017-01-23.
 */
public class Main {

    private static final String CURRENT = "SR160V4E10T100C";
    private static final String OUT_NAME = "-999.txt";

    private static final int NUM_ALGORITHMS = 18;
    private static final int NUM_TRIALS = 100;
    private static final int NUM_REPETITIONS = 1;

    private static final int EARLY_EXIT = 100;

    private static final double EPSILON3T = 0.09;
    private static final double EPSILON5T = 0.25;
    private static final double EPSILON6T = 0.35;
    private static final double EPSILON7T = 0.5;
    private static final double EPSILON8T = 0.65;
    private static final double EPSILON10T = 1.0;
    private static final double EPSILON12T = 1.4;
    private static final double EPSILON16T = 2.5;
    private static final double EPSILON17T = 2.75;
    private static final double EPSILON20T = 4.0;
    private static final double EPSILON24T = 6.0;
    private static final double EPSILON30T = 9.0;
    private static final double EPSILON34T = 11.0;
    private static final double EPSILON40T = 16.0;
    private static final double EPSILON80T = 64.0;

    private static final double EPSILON = EPSILON10T;

    private static final double THRESHOLD = 1.3;

    public static volatile LinkedList<Integer> numCompleted = new LinkedList<>();

    public static boolean WINDOWS = true;

    private static final String SLASH = System.getProperty("file.separator");
    private static final String SHARCNETPREFIX = SLASH + "work" +
            SLASH + "ablochha" +
            SLASH + "thesis" +
            SLASH + "multiwaycut" +
            SLASH + "data";

    private static boolean checkFlag(TestObject[] tests) {

        for (int i = 8; i < NUM_ALGORITHMS; i++) {

            if (tests[i].getRatio() > THRESHOLD) {

                return true;

            } // end if

        } //end for

        return false;

    } // end checkFlag

    public static void main(String[] args) {

        String inputFile;
        int inputTrials;
        int inputRepetitions;
        double inputEpsilon;

        if (args.length > 0) {

            inputFile = args[0];
            inputTrials = Integer.parseInt(args[1]);
            inputRepetitions = Integer.parseInt(args[2]);

            switch (Integer.parseInt(args[3])) {

                case 3:

                    inputEpsilon = EPSILON3T;
                    break;

                case 5:

                    inputEpsilon = EPSILON5T;
                    break;

                case 6:

                    inputEpsilon = EPSILON6T;
                    break;

                case 10:

                    inputEpsilon = EPSILON10T;
                    break;

                case 12:

                    inputEpsilon = EPSILON12T;
                    break;

                case 20:

                    inputEpsilon = EPSILON20T;
                    break;

                case 30:

                    inputEpsilon = EPSILON30T;
                    break;

                case 40:

                    inputEpsilon = EPSILON40T;
                    break;

                case 80:

                    inputEpsilon = EPSILON80T;
                    break;

                default:

                    throw new IllegalArgumentException("Unrecognized epsilon preset");

            } //end switch

        } //end if

        else {

            inputFile = CURRENT;
            inputTrials = NUM_TRIALS;
            inputRepetitions = NUM_REPETITIONS;
            inputEpsilon = EPSILON;

        } //end else

        TestObject[] tests = new TestObject[NUM_ALGORITHMS];

        PropertiesReader prop = new PropertiesReader();

        GraphFormatReader reader = new GraphFormatReader();
        ConnectedComponentSearcher searcher = new ConnectedComponentSearcher();

        MultiwayCutStrategy solver = new Simplex();
        MultiwayCutStrategy ih = new IsolationHeuristic();

        int repetitions;
        int trials = 0;
        int vertices = 0;
        int k = 0;
        int gapCount = 0;
        int gapCount2 = 0;
        int outputCount = 0;

        double ratio;
        double edgesSum = 0.0;

        boolean flag;

        tests[0] = new TestObject("Cplex", new MultiwayCutSolver(), numCompleted, WINDOWS);
        tests[1] = new TestObject("Simplex", solver, numCompleted, WINDOWS);
        tests[2] = new TestObject("Isolation Heuristic", ih, numCompleted, WINDOWS);
        tests[3] = new TestObject("Local Search (epsilon = " + inputEpsilon + ", OneEach)", new LocalSearch(), "ONEEACH", inputEpsilon, ih, numCompleted, WINDOWS);
        tests[4] = new TestObject("Local Search (epsilon = " + inputEpsilon + ", Clumps)", new LocalSearch(), "CLUMPS", inputEpsilon, ih, numCompleted, WINDOWS);
        tests[5] = new TestObject("Local Search (epsilon = " + inputEpsilon + ", Isolation Heuristic)", new LocalSearch(), "ISOLATION HEURISTIC", inputEpsilon, ih, numCompleted, WINDOWS);
        tests[6] = new TestObject("Local Search (epsilon = " + inputEpsilon + ", Random)", new LocalSearch(), "RANDOM", inputEpsilon, ih, numCompleted, WINDOWS);
        tests[7] = new TestObject("Local Search (epsilon = " + inputEpsilon + ", Random)", new LocalSearch(), "RANDOM", inputEpsilon, ih, numCompleted, WINDOWS);
        tests[8] = new TestObject("Calinescu", new Calinescu(), solver, numCompleted, WINDOWS);
        tests[9] = new TestObject("Exponential Clocks", new ExponentialClocks(), solver, numCompleted, WINDOWS);
        tests[10] = new TestObject("Buchbinder", new Buchbinder(), solver, numCompleted, WINDOWS);
        tests[11] = new TestObject("Single Threshold 1", new SingleThreshold1(), solver, numCompleted, WINDOWS);
        tests[12] = new TestObject("Single Threshold 2", new SingleThreshold2(), solver, numCompleted, WINDOWS);
        tests[13] = new TestObject("Descending Threshold 1", new DescendingThreshold1(), solver, numCompleted, WINDOWS);
        tests[14] = new TestObject("Descending Threshold 2", new DescendingThreshold2(), solver, numCompleted, WINDOWS);
        tests[15] = new TestObject("Independent Threshold", new IndependentThreshold(), solver, numCompleted, WINDOWS);
        tests[16] = new TestObject("3-Ingredient Mix", new SharmaVondrak3Mix(), solver, numCompleted, WINDOWS);
        tests[17] = new TestObject("4-Ingredient Mix", new SharmaVondrak4Mix(), solver, numCompleted, WINDOWS);

        while (trials < inputTrials) {

            repetitions = 0;
            numCompleted.clear();
            flag = false;
            trials++;

            StdOut.println("****************************************Trial " + trials + " of " + inputTrials + "****************************************");

            FlowNetwork largestConnectedComponent = searcher.getLargestConnectedComponent(reader.parse(prop.get(inputFile, WINDOWS), OUT_NAME));

            vertices = largestConnectedComponent.getNumVertices();
            edgesSum += largestConnectedComponent.getNumEdges();
            k = largestConnectedComponent.getK();

            tests[0].setGraph(new FlowNetwork(largestConnectedComponent), largestConnectedComponent.getNumVertices(), largestConnectedComponent.getNumEdges(), reader.getCapacity1(), reader.getCapacity2(), tests[0].getCost(), repetitions);
            tests[0].run();
            tests[1].setGraph(new FlowNetwork(largestConnectedComponent), largestConnectedComponent.getNumVertices(), largestConnectedComponent.getNumEdges(), reader.getCapacity1(), reader.getCapacity2(), tests[0].getCost(), repetitions);
            tests[1].run();
            tests[2].setGraph(new FlowNetwork(largestConnectedComponent), largestConnectedComponent.getNumVertices(), largestConnectedComponent.getNumEdges(), reader.getCapacity1(), reader.getCapacity2(), tests[0].getCost(), repetitions + 1);
            tests[2].run();

            if (tests[1].isFractional()) {

                gapCount++;

            } //end if

            if (tests[1].getCost() < tests[0].getCost()) {

                gapCount2++;

            } //end if

            while (repetitions < inputRepetitions) {

                repetitions++;
                StdOut.println("****************************************Repitition " + repetitions + " of " + inputRepetitions + "(" + trials + "/" + inputTrials + ")****************************************");

                for (int i = 3; i < NUM_ALGORITHMS; i++) {

                    if (repetitions == 1) {

                        tests[i].setGraph(new FlowNetwork(largestConnectedComponent), largestConnectedComponent.getNumVertices(), largestConnectedComponent.getNumEdges(), reader.getCapacity1(), reader.getCapacity2(), tests[0].getCost(), repetitions);
                        new Thread(tests[i]).start();

                    } //end if

                    else {

                        if (i >= 6) {

                            tests[i].setGraph(new FlowNetwork(largestConnectedComponent), largestConnectedComponent.getNumVertices(), largestConnectedComponent.getNumEdges(), reader.getCapacity1(), reader.getCapacity2(), tests[0].getCost(), repetitions);
                            //tests[i].run();   // Serial
                            new Thread(tests[i]).start();

                        } //end if

                    } //end else

                } //end for

                if (repetitions >= EARLY_EXIT && !flag) {

                    break;

                } //end if

                while (numCompleted.size() != NUM_ALGORITHMS) {

                } //end while

                StdOut.println("All threads completed the current graph");

            } //end while

            if (checkFlag(tests)) {

                tests[8].outputThresholdRatio(reader);
                tests[10].outputThresholdRatio(reader);
                tests[11].outputThresholdRatio(reader);
                tests[12].outputThresholdRatio(reader);

                outputCount++;

            } //end if

            tests[8].clearThresholdRatio(repetitions);
            tests[9].clearThresholdRatio(repetitions);
            tests[10].clearThresholdRatio(repetitions);
            tests[11].clearThresholdRatio(repetitions);
            tests[12].clearThresholdRatio(repetitions);
            tests[13].clearThresholdRatio(repetitions);
            tests[14].clearThresholdRatio(repetitions);
            tests[15].clearThresholdRatio(repetitions);
            tests[16].clearThresholdRatio(repetitions);
            tests[17].clearThresholdRatio(repetitions);

        } //end while

        // Update the test objects statistics
        for (int i = 2; i < NUM_ALGORITHMS; i++) {

            tests[i].calculate(inputTrials, inputRepetitions);

        } //end for

        StdOut.println("\nTrials: " + trials + ", Vertices: " + vertices + ", Edges: " + (int) (edgesSum / (double) trials) + ", K: " + k + ", Gap Count1: " + gapCount + ", Gap Count2: " + gapCount2 + ", Output Count: " + outputCount);

        // Output the test objects results
        for (int i = 2; i < NUM_ALGORITHMS; i++) {

            tests[i].output(reader);

        } //end for

        String prefix;

        if (WINDOWS) {

            prefix = "data";

        } //end if

        else {

            prefix = SHARCNETPREFIX;

        } //end else

        Out out = new Out(prefix + SLASH + "csv" + SLASH + inputFile + ".csv");
        out.println("Vertices," +
                "Edges," +
                "Terminals," +
                "Edge Density," +
                "Terminal Density," +
                "Isolation (avg)," +
                "Local Search(avg)," +
                "Calinescu (avg)," +
                "Buchbinder (avg)," +
                "Sharma and Vondrak (avg),");

        out.println(vertices + "," +
                (int) (edgesSum / (double) trials) + "," +
                k + "," +
                String.format("%.3f", ((int) (edgesSum / (double) trials)) / (double) vertices) + "," +
                String.format("%.3f", (double) k / (double) vertices) + "," +
                String.format("%.3f", tests[2].getRatioAvg()) + "," +
                String.format("%.3f", tests[4].getRatioAvg()) + "," +
                String.format("%.3f", tests[8].getRatioAvg()) + "," +
                String.format("%.3f", tests[10].getRatioAvg()) + "," +
                String.format("%.3f", tests[17].getRatioAvg()) + ",");

        out.println("Vertices," +
                "Edges," +
                "Terminals," +
                "Edge Density," +
                "Terminal Density," +
                "Isolation (max)," +
                "Local Search (max)," +
                "Calinescu (max)," +
                "Buchbinder (max)," +
                "Sharma and Vondrak (max)");

        out.println(vertices + "," +
                (int) (edgesSum / (double) trials) + "," +
                k + "," +
                String.format("%.3f", ((int) (edgesSum / (double) trials)) / (double) vertices) + "," +
                String.format("%.3f", (double) k / (double) vertices) + "," +
                String.format("%.3f", tests[2].getRatioMax()) + "," +
                String.format("%.3f", tests[4].getRatioMax()) + "," +
                String.format("%.3f", tests[8].getRatioMax()) + "," +
                String.format("%.3f",tests[10].getRatioMax()) + "," +
                String.format("%.3f", tests[17].getRatioMax()));

        out.println("Vertices," +
                "Edges," +
                "Terminals," +
                "Edge Density," +
                "Terminal Density," +
                "Isolation (std)," +
                "Local Search (std)," +
                "Calinescu (std)," +
                "Buchbinder (std)," +
                "Sharma and Vondrak (std)");

        out.println(vertices + "," +
                (int) (edgesSum / (double) trials) + "," +
                k + "," +
                String.format("%.3f", ((int) (edgesSum / (double) trials)) / (double) vertices) + "," +
                String.format("%.3f", (double) k / (double) vertices) + "," +
                String.format("%.3f", tests[2].getStd()) + "," +
                String.format("%.3f", tests[4].getStd()) + "," +
                String.format("%.3f", tests[8].getStd()) + "," +
                String.format("%.3f",tests[10].getStd()) + "," +
                String.format("%.3f", tests[17].getStd()));

        out.close();

    } //end main

} //end Main
