import algorithms.LocalSearch;
import cplex.MultiwayCutSolver;
import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
import algorithms.IsolationHeuristic;
import utility.ConnectedComponentSearcher;
import utility.GraphFormatReader;

/**
 * Created by Bloch-Hansen on 2017-01-23.
 */
public class Main {

    private static final String SLASH = System.getProperty("file.separator");

    private static final String EXAMPLE1 = "data" + SLASH + "testnetwork1.txt";
    private static final String EXAMPLE2 = "data" + SLASH + "testnetwork2.txt";

    private static final String FIVEBYFIVE_1 = "data" + SLASH + "5x5_1.txt";
    private static final String FIVEBYFIVE_2 = "data" + SLASH + "5x5_2.txt";
    private static final String FIVEBYFIVE_3 = "data" + SLASH + "5x5_3.txt";
    private static final String FIVEBYFIVE_4 = "data" + SLASH + "5x5_4.txt";
    private static final String FIVEBYFIVE_5 = "data" + SLASH + "5x5_5.txt";
    private static final String FIVEBYFIVE_6 = "data" + SLASH + "5x5_6.txt";

    private static final String DIMACS_FRB30_15_1_1 = "data" + SLASH + "frb30-15" + SLASH + "frb30-15-1_1.txt";

    private static final String DIMACS_FRB59_26_1_1 = "data" + SLASH + "frb59-26-mis" + SLASH + "frb59-26-1_1.txt";
    private static final String DIMACS_FRB59_26_1_2 = "data" + SLASH + "frb59-26-mis" + SLASH + "frb59-26-1_2.txt";
    private static final String DIMACS_FRB59_26_1_3 = "data" + SLASH + "frb59-26-mis" + SLASH + "frb59-26-1_3.txt";
    private static final String DIMACS_FRB59_26_1_4 = "data" + SLASH + "frb59-26-mis" + SLASH + "frb59-26-1_4.txt";
    private static final String DIMACS_FRB59_26_1_5 = "data" + SLASH + "frb59-26-mis" + SLASH + "frb59-26-1_5.txt";
    private static final String DIMACS_FRB59_26_1_6 = "data" + SLASH + "frb59-26-mis" + SLASH + "frb59-26-1_6.txt";

    private static final String CURRENT = DIMACS_FRB59_26_1_1;

    public static void main(String[] args) {

        IsolationHeuristic isolationHeuristic = new IsolationHeuristic();
        LocalSearch localSearch = new LocalSearch();
        MultiwayCutSolver solver = new MultiwayCutSolver();
        GraphFormatReader reader = new GraphFormatReader();
        ConnectedComponentSearcher searcher = new ConnectedComponentSearcher();

        FlowNetwork largestConnectedComponent = searcher.getLargestConnectedComponent(reader.parse(CURRENT));

        int ihCost = isolationHeuristic.computeMultiwayCut(new FlowNetwork(largestConnectedComponent));
        int lsCost = localSearch.computeMultiwayCut(new FlowNetwork(largestConnectedComponent));
        int optimal = solver.computeMultiwayCut(new FlowNetwork(largestConnectedComponent));

        StdOut.println("Isolation Heuristic: " + ihCost +
                       ", Local Search (" + localSearch.iterations + " iterations): " + lsCost +
                       ", Cplex: " + optimal);

    } //end main

} //end Main
