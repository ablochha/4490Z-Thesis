import algorithms.LocalSearch;
import cplex.MultiwayCutSolver;
import datastructures.flownetwork.FlowNetwork;
import library.StdOut;
import algorithms.IsolationHeuristic;
import utility.ConnectedComponentSearcher;
import utility.GraphFormatReader;
import utility.PropertiesReader;

/**
 * Created by Bloch-Hansen on 2017-01-23.
 */
public class Main {

    private static final String CURRENT = "I080_345";

    public static void main(String[] args) {

        PropertiesReader prop = new PropertiesReader();

        GraphFormatReader reader = new GraphFormatReader();
        ConnectedComponentSearcher searcher = new ConnectedComponentSearcher();

        IsolationHeuristic isolationHeuristic = new IsolationHeuristic();
        LocalSearch localSearch = new LocalSearch();
        MultiwayCutSolver solver = new MultiwayCutSolver();

        FlowNetwork largestConnectedComponent = searcher.getLargestConnectedComponent(reader.parse(prop.get(CURRENT)));

        int ihCost = isolationHeuristic.computeMultiwayCut(new FlowNetwork(largestConnectedComponent));
        int lsCost = localSearch.computeMultiwayCut(new FlowNetwork(largestConnectedComponent));
        int optimal = solver.computeMultiwayCut(new FlowNetwork(largestConnectedComponent));

        StdOut.println("Isolation Heuristic: " + ihCost +
                       ", Local Search (" + localSearch.iterations + " iterations): " + lsCost +
                       ", Cplex: " + optimal);

    } //end main

} //end Main
