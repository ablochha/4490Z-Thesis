package cplex;

import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowNetwork;
import ilog.concert.*;
import ilog.cplex.*;
import library.In;
import library.StdOut;

import java.util.LinkedList;

/**
 * Created by Bloch-Hansen on 2017-02-15.
 */
public class MultiwayCutSolver {

    /**
     * Reads the terminal vertices to be isolated.
     * @param in a text representation of the flow network
     * @param k the number of terminal vertices
     * @param terminals a list of the terminal vertices
     */
    private void readTerminals(In in, int k, LinkedList<Integer> terminals) {

        StdOut.println("k: " + k);

        // Store each of the terminal vertices id's
        for (int i = 0; i < k; i++) {

            terminals.add(in.readInt());
            StdOut.println("terminal " + (i+1) + ": " + terminals.get(i));

        } //end for

        //StdOut.println();

    } //end readTerminals

    static void populateByRow(IloMPModeler model, IloNumVar[] edgeLabelSums, int k, int n, int m,
                              LinkedList<FlowEdge> edges, int[] edgeCapacities,
                              LinkedList<Integer> terminals) throws IloException {

        IloNumVar[][] vertexLabels = new IloNumVar[n][k];
        IloNumVar[][] edgeLabels = new IloNumVar[m][k];

        // Initialize the boolean labels for the vertices
        for (int i = 0; i < n; i++) {

            vertexLabels[i] = model.boolVarArray(k);

        } //end for

        // Initialize the boolean labels for the edges
        for (int i = 0; i < m; i++) {

            edgeLabels[i] = model.boolVarArray(k);
            edgeLabelSums[i] = model.numVar(0.0, 2.0, IloNumVarType.Int);

        } //end for

        model.addMinimize(model.prod(0.5, model.scalProd(edgeCapacities, edgeLabelSums)));

        // A vertex can only be in a single partition
        for (int i = 0; i < n; i++) {

            model.addEq(model.sum(vertexLabels[i]), 1.0);

        } //end for

        // Store the number of partitions that an edge bridges
        for (int i = 0; i < m; i++) {

            model.addEq(edgeLabelSums[i], model.sum(edgeLabels[i]));

        } //end for

        // An edge can only bridge 0 or 2 partitions
        for (int i = 0; i < m; i++) {

            IloAdd(model, model.or(model.addEq(edgeLabelSums[i], 0), model.addEq(edgeLabelSums[i], 2)));

        } //end for

        // Add the label inequalities
        for (int i = 0; i < m; i++) {

            FlowEdge edge = edges.get(i);

            // An edge's label for partition j must be greater or equal to the difference of its vertices
            for (int j = 0; j < k; j++) {

                // xu[i] - xv[i] <= ze[i]
                model.addLe(model.sum(
                            model.prod(1.0, vertexLabels[edge.getStartVertex().id()][j]),
                            model.prod(-1.0, vertexLabels[edge.getEndVertex().id()][j])),
                            edgeLabels[i][j]);

                // xv[i] - xu[i] <= ze[i]
                model.addLe(model.sum(
                        model.prod(1.0, vertexLabels[edge.getEndVertex().id()][j]),
                        model.prod(-1.0, vertexLabels[edge.getStartVertex().id()][j])),
                        edgeLabels[i][j]);

            } //end for

        } //end for

        // Terminal vertices are locked into their partitions
        for (int i = 0; i < k; i++) {

            model.addEq(vertexLabels[terminals.get(i)][i], 1.0);

        } //end for

    } //end populateByRow

    /**
     * Computes a minimum multiway cut.
     * @param in a text representation of the flow network
     */
    public int computeMultiwayCut(In in) {

        FlowNetwork flowNetwork;

        LinkedList<FlowEdge> edges;
        LinkedList<Integer> terminals = new LinkedList<>();

        int k;
        int numVertices;
        int numEdges;
        int optimal = 0;
        int[] edgeCapacities;

        StdOut.println("Cplex");

        // The number of terminal vertices
        k = in.readInt();

        // Create the flow network from the text file
        readTerminals(in, k, terminals);
        numVertices = in.readInt();
        flowNetwork = new FlowNetwork(in, numVertices);
        in.close();

        // Read the data for the cplex model
        edges = flowNetwork.getEdges();
        numEdges = flowNetwork.numEdges();
        edgeCapacities = flowNetwork.getEdgeCapacities();

        // Try to optimize the model
        try {

            // Create the modeler/solver object
            IloCplex cplex = new IloCplex();
            IloNumVar[] edgeLabelSums = new IloNumVar[numEdges];
            populateByRow(cplex, edgeLabelSums, k, numVertices, numEdges, edges, edgeCapacities, terminals);

            // Examine the solution
            if (cplex.solve()) {

                System.out.println();
                System.out.println("Solution status = " + cplex.getStatus());
                System.out.println();
                System.out.println("Optimal = " + cplex.getObjValue());
                optimal = (int)cplex.getObjValue();

                // Display which edges form the optimal multiway cut
                for (int i = 0; i < numEdges; i++) {

                    System.out.println("Edge " + i + " = " + cplex.getValue(edgeLabelSums[i]));

                } //end for

                System.out.println();

            } //end if

            cplex.end();

        } catch (IloException ex) {

            System.out.println("Concert Error: " + ex);

        } //end try-catch

        return optimal;

    } //end computeMultiwayCut

} //end MultiwayCutSolver
