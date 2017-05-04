import algorithms.*;
import cplex.MultiwayCutSolver;
import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
import utility.*;

/**
 * Created by Bloch-Hansen on 2017-01-23.
 */
public class Main {

    private static final String CURRENT = "GE080";
    private static final String OUT_NAME = "-999.txt";
    private static final int NUM_TRIALS = 10;

    private static final double EPSILON1 = 1000.0;
    private static final double EPSILON2 = 1.6;
    private static final double EPSILON3 = .525;
    private static final double EPSILON4 = .315;
    private static final double EPSILON5 = .16;

    public static void main(String[] args) {

        TestObject[] tests = new TestObject[9];

        PropertiesReader prop = new PropertiesReader();

        GraphFormatReader reader = new GraphFormatReader();
        ConnectedComponentSearcher searcher = new ConnectedComponentSearcher();

        int trials = 0;
        int vertices = 0;
        double edgesSum = 0.0;

        tests[0] = new TestObject(NUM_TRIALS, "Cplex", new MultiwayCutSolver());
        tests[1] = new TestObject(NUM_TRIALS, "Isolation Heuristic", new IsolationHeuristic());
        tests[2] = new TestObject(NUM_TRIALS, "Local Search (epsilon = " + EPSILON1 + ")", new LocalSearch(), EPSILON1);
        tests[3] = new TestObject(NUM_TRIALS, "Local Search (epsilon = " + EPSILON2 + ")", new LocalSearch(), EPSILON2);
        tests[4] = new TestObject(NUM_TRIALS, "Local Search (epsilon = " + EPSILON3 + ")", new LocalSearch(), EPSILON3);
        tests[5] = new TestObject(NUM_TRIALS, "Local Search (epsilon = " + EPSILON4 + ")", new LocalSearch(), EPSILON4);
        tests[6] = new TestObject(NUM_TRIALS, "Local Search (epsilon = " + EPSILON5 + ")", new LocalSearch(), EPSILON5);
        tests[7] = new TestObject(NUM_TRIALS, "Calinescu", new Calinescu(), new MultiwayCutSolver());
        tests[8] = new TestObject(NUM_TRIALS, "Buchbinder", new Buchbinder(), new MultiwayCutSolver());

        while (trials < NUM_TRIALS) {

            FlowNetwork largestConnectedComponent = searcher.getLargestConnectedComponent(reader.parse(prop.get(CURRENT), OUT_NAME));

            vertices = largestConnectedComponent.getNumVertices();
            edgesSum += largestConnectedComponent.getNumEdges();

            tests[0].run(largestConnectedComponent);

            for (int i = 1; i < tests.length; i++) {

                tests[i].run(largestConnectedComponent);
                tests[i].update(tests[0].getCost());

            } //end for

            trials++;

        } //end while

        // Update the test objects statistics
        for (int i = 1; i < tests.length; i++) {

            tests[i].calculate();

        } //end for

        StdOut.println("\nTrials: " + trials + ", Vertices: " + vertices + ", Edges: " + (int) (edgesSum / trials));

        // Output the test objects results
        for (int i = 1; i < tests.length; i++) {

            tests[i].output();

        } //end for

    } //end main

} //end Main
