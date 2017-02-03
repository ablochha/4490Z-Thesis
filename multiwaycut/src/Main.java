import algorithms.LocalSearch;
import library.In;
import library.StdOut;
import algorithms.IsolationHeuristic;

/**
 * Created by Bloch-Hansen on 2017-01-23.
 */
public class Main {

    public static final String EXAMPLE1 = "data" + System.getProperty("file.separator") + "testnetwork1.txt";
    public static final String EXAMPLE2 = "data" + System.getProperty("file.separator") + "testnetwork2.txt";

    public static void main(String[] args) {

        IsolationHeuristic isolationHeuristic = new IsolationHeuristic();
        LocalSearch localSearch = new LocalSearch();

        int ihCost = 0;
        int lsCost = 0;

        // Run the example graph
        try {

            In in = new In(EXAMPLE2);
            ihCost = isolationHeuristic.computeMultiwayCut(in);

            in = new In(EXAMPLE2);
            lsCost = localSearch.computeMultiwayCut(in);

            StdOut.println("Isolation Heuristic: " + ihCost + ", Local Search (" + localSearch.iterations + " iterations): " + lsCost);

        } //end try

        // The file didn't work
        catch (Exception e) {

            StdOut.println("The graph failed, reason: " + e);

        } //end catch

    } //end main

} //end Main
