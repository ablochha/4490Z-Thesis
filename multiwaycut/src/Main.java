import algorithms.*;
import cplex.MultiwayCutSolver;
import cplex.Simplex;
import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
import utility.*;

/**
 * Created by Bloch-Hansen on 2017-01-23.
 */
public class Main {

    private static final String CURRENT = "GE080";
    private static final String OUT_NAME = "-999.txt";

    private static final int NUM_ALGORITHMS = 13;
    private static final int NUM_TRIALS = 1000;
    private static final int NUM_REPITITIONS = 1;

    private static final int EARLY_EXIT = 5;

    private static final double EPSILON1 = 1000.0;
    private static final double EPSILON2 = 1.6;
    private static final double EPSILON3 = .525;
    private static final double EPSILON4 = .315;
    private static final double EPSILON5 = .16;

    private static final double THRESHOLD = 1.05;

    public static void main(String[] args) {

        TestObject[] tests = new TestObject[NUM_ALGORITHMS];

        PropertiesReader prop = new PropertiesReader();

        GraphFormatReader reader = new GraphFormatReader();
        ConnectedComponentSearcher searcher = new ConnectedComponentSearcher();

        MultiwayCutStrategy solver = new Simplex();
        MultiwayCutStrategy ih = new IsolationHeuristic();

        int repititions;
        int trials = 0;
        int vertices = 0;
        int k = 0;

        double ratio;
        double edgesSum = 0.0;

        boolean flag;

        tests[0] = new TestObject("Cplex", new MultiwayCutSolver());
        tests[1] = new TestObject("Simplex", solver);
        tests[2] = new TestObject("Isolation Heuristic", ih);
        tests[3] = new TestObject("Local Search (epsilon = " + EPSILON2 + ", OneEach)", new LocalSearch(), "ONEEACH", EPSILON2, ih);
        tests[4] = new TestObject("Local Search (epsilon = " + EPSILON2 + ", Clumps)", new LocalSearch(), "CLUMPS", EPSILON2, ih);
        tests[5] = new TestObject("Local Search (epsilon = " + EPSILON2 + ", Random)", new LocalSearch(), "RANDOM", EPSILON2, ih);
        tests[6] = new TestObject("Local Search (epsilon = " + EPSILON2 + ", Random)", new LocalSearch(), "RANDOM", EPSILON2, ih);
        tests[7] = new TestObject("Local Search (epsilon = " + EPSILON2 + ", Isolation Heuristic)", new LocalSearch(), "ISOLATION HEURISTIC", EPSILON2, ih);
        tests[8] = new TestObject("Calinescu", new Calinescu(), solver);
        tests[9] = new TestObject("Exponential Clocks", new ExponentialClocks(), solver);
        tests[10] = new TestObject("Buchbinder", new Buchbinder(), solver);
        tests[11] = new TestObject("Descending Threshold", new DescendingThreshold(), solver);
        tests[12] = new TestObject("Independent Threshold", new IndependentThreshold(), solver);

        while (trials < NUM_TRIALS) {

            repititions = 0;
            flag = false;
            trials++;

            StdOut.println("****************************************Trial " + trials + " of " + NUM_TRIALS + "****************************************");

            FlowNetwork largestConnectedComponent = searcher.getLargestConnectedComponent(reader.parse(prop.get(CURRENT), OUT_NAME));

            vertices = largestConnectedComponent.getNumVertices();
            edgesSum += largestConnectedComponent.getNumEdges();
            k = largestConnectedComponent.getK();

            tests[0].run(new FlowNetwork(largestConnectedComponent), largestConnectedComponent.getNumVertices(), largestConnectedComponent.getNumEdges(), reader.getCapacity1(), reader.getCapacity2());
            tests[1].run(new FlowNetwork(largestConnectedComponent), largestConnectedComponent.getNumVertices(), largestConnectedComponent.getNumEdges(), reader.getCapacity1(), reader.getCapacity2());

            while (repititions < NUM_REPITITIONS) {

                repititions++;
                StdOut.println("****************************************Repitition " + repititions + " of " + NUM_REPITITIONS + "(" + trials + "/" + NUM_TRIALS + ")****************************************");

                for (int i = 2; i < NUM_ALGORITHMS; i++) {

                    if (repititions == 1) {

                        tests[i].run(new FlowNetwork(largestConnectedComponent), largestConnectedComponent.getNumVertices(), largestConnectedComponent.getNumEdges(), reader.getCapacity1(), reader.getCapacity2());
                        ratio = tests[i].update(tests[0].getCost());

                        if (i >= 8 && ratio > THRESHOLD) {

                            flag = true;

                        } //end if

                    } //end if

                    else {

                        if (i >= 8) {

                            tests[i].run(new FlowNetwork(largestConnectedComponent), largestConnectedComponent.getNumVertices(), largestConnectedComponent.getNumEdges(), reader.getCapacity1(), reader.getCapacity2());
                            ratio = tests[i].update(tests[0].getCost());

                            if (ratio > THRESHOLD) {

                                flag = true;

                            } //end if

                        } //end if

                    } //end else

                } //end for

                if (repititions >= EARLY_EXIT && !flag) {

                    break;

                } //end if

            } //end while

            if (repititions >= EARLY_EXIT && flag) {

                tests[8].outputRadiusRatio(reader);
                tests[10].outputRadiusRatio(reader);
                tests[12].outputRadiusRatio(reader);

            } //end if

            tests[8].clearRadiusRatio();
            tests[10].clearRadiusRatio();
            tests[12].clearRadiusRatio();

        } //end while

        // Update the test objects statistics
        for (int i = 2; i < NUM_ALGORITHMS; i++) {

            tests[i].calculate();

        } //end for

        StdOut.println("\nTrials: " + trials + ", Vertices: " + vertices + ", Edges: " + (int) (edgesSum / trials) + ", K: " + k);

        // Output the test objects results
        for (int i = 2; i < NUM_ALGORITHMS; i++) {

            tests[i].output(reader);

        } //end for

    } //end main

} //end Main
