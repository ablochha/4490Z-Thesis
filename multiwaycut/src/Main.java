import algorithms.LocalSearch;
import cplex.MultiwayCutSolver;
import library.In;
import library.StdOut;
import algorithms.IsolationHeuristic;

/**
 * Created by Bloch-Hansen on 2017-01-23.
 */
public class Main {

    public static final String EXAMPLE1 = "data" + System.getProperty("file.separator") + "testnetwork1.txt";
    public static final String EXAMPLE2 = "data" + System.getProperty("file.separator") + "testnetwork2.txt";
    public static final String FIVEBYFIVE_1 = "data" + System.getProperty("file.separator") + "5x5_1.txt";
    public static final String FIVEBYFIVE_2 = "data" + System.getProperty("file.separator") + "5x5_2.txt";
    public static final String FIVEBYFIVE_3 = "data" + System.getProperty("file.separator") + "5x5_3.txt";
    public static final String FIVEBYFIVE_4 = "data" + System.getProperty("file.separator") + "5x5_4.txt";
    public static final String FIVEBYFIVE_5 = "data" + System.getProperty("file.separator") + "5x5_5.txt";
    public static final String FIVEBYFIVE_6 = "data" + System.getProperty("file.separator") + "5x5_6.txt";

    public static final String CURRENT = FIVEBYFIVE_6;

    public static void main(String[] args) {

        IsolationHeuristic isolationHeuristic = new IsolationHeuristic();
        LocalSearch localSearch = new LocalSearch();
        MultiwayCutSolver solver = new MultiwayCutSolver();

        int ihCost = 0;
        int lsCost = 0;
        int optimal = 0;

        // Run the example graph
        try {

            In in = new In(CURRENT);
            ihCost = isolationHeuristic.computeMultiwayCut(in);

            in = new In(CURRENT);
            lsCost = localSearch.computeMultiwayCut(in);

            in = new In(CURRENT);
            optimal = solver.computeMultiwayCut(in);

            StdOut.println("Isolation Heuristic: " + ihCost + ", Local Search (" + localSearch.iterations + " iterations): " + lsCost + ", Cplex: " + optimal);

        } //end try

        // The file didn't work
        catch (Exception e) {

            StdOut.println("The graph failed, reason: " + e);

        } //end catch

    } //end main

} //end Main
