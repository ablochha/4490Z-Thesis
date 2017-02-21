import algorithms.LocalSearch;
import cplex.MultiwayCutSolver;
import library.StdOut;
import algorithms.IsolationHeuristic;
import utility.ConnectedComponentSearcher;
import utility.GraphFormatReader;

/**
 * Created by Bloch-Hansen on 2017-01-23.
 */
public class Main {

    private static final String SLASH = System.getProperty("file.separator");

    public static final String EXAMPLE1 = "data" + SLASH + "testnetwork2.txt";

    public static final String FIVEBYFIVE_1 = "data" + SLASH + "5x5_1.txt";
    public static final String FIVEBYFIVE_2 = "data" + SLASH + "5x5_2.txt";
    public static final String FIVEBYFIVE_3 = "data" + SLASH + "5x5_3.txt";
    public static final String FIVEBYFIVE_4 = "data" + SLASH + "5x5_4.txt";
    public static final String FIVEBYFIVE_5 = "data" + SLASH + "5x5_5.txt";
    public static final String FIVEBYFIVE_6 = "data" + SLASH + "5x5_6.txt";

    public static final String DIMACS1 = "data" + SLASH + "frb59-26-mis" + SLASH + "frb59-26-1.mis";
    public static final String DIMACS2 = "data" + SLASH + "frb59-26-mis" + SLASH + "frb59-26-2.mis";
    public static final String DIMACS3 = "data" + SLASH + "frb59-26-mis" + SLASH + "frb59-26-3.mis";
    public static final String DIMACS4 = "data" + SLASH + "frb59-26-mis" + SLASH + "frb59-26-4.mis";
    public static final String DIMACS5 = "data" + SLASH + "frb59-26-mis" + SLASH + "frb59-26-5.mis";
    public static final String DIMACS6 = "data" + SLASH + "frb59-26-mis" + SLASH + "frb59-26-6.mis";

    public static final String CURRENT = EXAMPLE1;

    public static void main(String[] args) {

        IsolationHeuristic isolationHeuristic = new IsolationHeuristic();
        LocalSearch localSearch = new LocalSearch();
        MultiwayCutSolver solver = new MultiwayCutSolver();
        GraphFormatReader reader = new GraphFormatReader();
        //ConnectedComponentSearcher searcher = new ConnectedComponentSearcher();

        //searcher.getLargestConnectedComponent(reader.parse(CURRENT));

        int ihCost = isolationHeuristic.computeMultiwayCut(reader.parse(CURRENT));
        int lsCost = localSearch.computeMultiwayCut(reader.parse(CURRENT));
        int optimal = solver.computeMultiwayCut(reader.parse(CURRENT));

        StdOut.println("Isolation Heuristic: " + ihCost +
                       ", Local Search (" + localSearch.iterations + " iterations): " + lsCost +
                       ", Cplex: " + optimal);

    } //end main

} //end Main
