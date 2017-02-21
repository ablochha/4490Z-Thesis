package cplex;

import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowNetwork;
import ilog.concert.*;
import ilog.cplex.*;
import library.StdOut;

import java.util.LinkedList;

/**
 * Created by Bloch-Hansen on 2017-02-15.
 */
public class MultiwayCutSolver {

    private void populateByRow(IloMPModeler model,
                              IloNumVar[] edgeLabelSums,
                              FlowNetwork flowNetwork) throws IloException {

        LinkedList<FlowEdge> edges = flowNetwork.getEdges();
        LinkedList<Integer> terminals = flowNetwork.getTerminals();

        int n = flowNetwork.getNumVertices();
        int m = flowNetwork.getNumEdges();
        int k = flowNetwork.getK();
        int[] edgeCapacities = flowNetwork.getEdgeCapacities();

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

        // An edge can only bridge 0 or 2 partitions
        for (int i = 0; i < m; i++) {

            model.addEq(edgeLabelSums[i], model.sum(edgeLabels[i]));

        } //end for

        // Terminal vertices are locked into their partitions
        for (int i = 0; i < k; i++) {

            model.addEq(vertexLabels[terminals.get(i)][i], 1.0);

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

    } //end populateByRow

    /**
     * Computes a minimum multiway cut.
     */
    public int computeMultiwayCut(FlowNetwork flowNetwork) {

        int optimal = 0;

        StdOut.println("Cplex");

        // Try to optimize the model
        try {

            // Create the modeler/solver object
            IloCplex cplex = new IloCplex();
            cplex.setOut(null);

            IloNumVar[] edgeLabelSums = new IloNumVar[flowNetwork.getNumEdges()];
            populateByRow(cplex, edgeLabelSums, flowNetwork);

            // Examine the solution
            if (cplex.solve()) {

                //System.out.println();
                //System.out.println("Solution status = " + cplex.getStatus());
                //System.out.println();
                //System.out.println("Optimal = " + cplex.getObjValue());
                optimal = (int)cplex.getObjValue();
                StdOut.println("The weight of the multiway cut: " + optimal);

                // Display which edges form the optimal multiway cut
                for (int i = 0; i < flowNetwork.getNumEdges(); i++) {

                    //System.out.println("Edge " + i + " = " + cplex.getValue(edgeLabelSums[i]));

                } //end for

            } //end if

            cplex.end();

        } catch (IloException ex) {

            System.out.println("Concert Error: " + ex);

        } //end try-catch

        return optimal;

    } //end computeMultiwayCut

} //end MultiwayCutSolver
