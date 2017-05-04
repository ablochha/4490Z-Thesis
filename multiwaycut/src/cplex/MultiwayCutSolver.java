package cplex;

import algorithms.MultiwayCutStrategy;
import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowNetwork;
import datastructures.flownetwork.FlowVertex;
import ilog.concert.*;
import ilog.cplex.*;
import library.StdOut;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-02-15.
 */
public class MultiwayCutSolver implements MultiwayCutStrategy{

    private void populateByRowInteger(IloMPModeler model,
                                    IloNumVar[] edgeLabelSums,
                                    FlowNetwork flowNetwork) throws IloException {

        LinkedList<FlowEdge> edges = flowNetwork.getEdges();
        LinkedList<Integer> terminals = flowNetwork.getTerminals();

        int n = flowNetwork.getNumVertices();
        int m = flowNetwork.getNumEdges();
        int k = flowNetwork.getK();
        int[] edgeCapacities = flowNetwork.getEdgeCapacities();

        Map<Integer, IloNumVar[]> vertexLabels = new LinkedHashMap<>();
        IloNumVar[][] edgeLabels = new IloNumVar[m][k];

        // Initialize the boolean labels for the vertices
        for (Map.Entry<Integer, FlowVertex> entry : flowNetwork.getVertices().entrySet()) {

            vertexLabels.put(entry.getValue().id(), model.boolVarArray(k));

        } //end for

        // Initialize the boolean labels for the edges
        for (int i = 0; i < m; i++) {

            edgeLabels[i] = model.boolVarArray(k);
            edgeLabelSums[i] = model.numVar(0.0, 2.0, IloNumVarType.Int);

        } //end for

        model.addMinimize(model.prod(0.5, model.scalProd(edgeCapacities, edgeLabelSums)));

        // A vertex can only be in a single partition
        for (Map.Entry<Integer, FlowVertex> entry : flowNetwork.getVertices().entrySet()) {

            model.addEq(model.sum(vertexLabels.get(entry.getValue().id())), 1.0);

        } //end for

        // An edge can only bridge 0 or 2 partitions
        for (int i = 0; i < m; i++) {

            model.addEq(edgeLabelSums[i], model.sum(edgeLabels[i]));

        } //end for

        // Terminal vertices are locked into their partitions
        for (int i = 0; i < k; i++) {

            model.addEq(vertexLabels.get(terminals.get(i))[i], 1.0);

        } //end for

        // Add the label inequalities
        for (int i = 0; i < m; i++) {

            FlowEdge edge = edges.get(i);

            // An edge's label for partition j must be greater or equal to the difference of its vertices
            for (int j = 0; j < k; j++) {

                // xu[i] - xv[i] <= ze[i]
                model.addLe(model.sum(
                        model.prod(1.0, vertexLabels.get(edge.getStartVertex().id())[j]),
                        model.prod(-1.0, vertexLabels.get(edge.getEndVertex().id())[j])),
                        edgeLabels[i][j]);

                // xv[i] - xu[i] <= ze[i]
                model.addLe(model.sum(
                        model.prod(1.0, vertexLabels.get(edge.getEndVertex().id())[j]),
                        model.prod(-1.0, vertexLabels.get(edge.getStartVertex().id())[j])),
                        edgeLabels[i][j]);

            } //end for

        } //end for

    } //end populateByRow

    private void populateByRowLinear(IloMPModeler model,
                                    IloNumVar[] edgeLabelSums,
                                    Map<Integer, IloNumVar[]> vertexLabels,
                                    FlowNetwork flowNetwork) throws IloException {

        LinkedList<FlowEdge> edges = flowNetwork.getEdges();
        LinkedList<Integer> terminals = flowNetwork.getTerminals();

        int n = flowNetwork.getNumVertices();
        int m = flowNetwork.getNumEdges();
        int k = flowNetwork.getK();
        int[] edgeCapacities = flowNetwork.getEdgeCapacities();
        IloNumExpr[] distances = new IloNumExpr[m];

        // Initialize the linear labels for the vertices
        for (Map.Entry<Integer, FlowVertex> entry : flowNetwork.getVertices().entrySet()) {

            vertexLabels.put(entry.getValue().id(), model.numVarArray(k, 0.0, 1.0, IloNumVarType.Float));

        } //end for

        // Initialize the linear labels for the edges
        for (int i = 0; i < m; i++) {

            edgeLabelSums[i] = model.numVar(0.0, 2.0, IloNumVarType.Float);

        } //end for

        model.addMinimize(model.prod(0.5, model.scalProd(edgeCapacities, edgeLabelSums)));

        // A vertex can only be in a single partition
        for (Map.Entry<Integer, FlowVertex> entry : flowNetwork.getVertices().entrySet()) {

            model.addEq(model.sum(vertexLabels.get(entry.getValue().id())), 1.0);

        } //end for

        // Terminal vertices are locked into their partitions
        for (int i = 0; i < k; i++) {

            model.addEq(vertexLabels.get(terminals.get(i))[i], 1.0);

        } //end for

        // Define the distance metric
        for (int i = 0; i < m; i++) {

            FlowEdge edge = edges.get(i);
            distances[i] = model.numExpr();

            for (int j = 0; j < k; j++) {

                distances[i] = model.sum(distances[i], model.abs(model.diff(vertexLabels.get(edge.getStartVertex().id())[j],
                                                                            vertexLabels.get(edge.getEndVertex().id())[j])));

            } //end for

            model.addEq(edgeLabelSums[i], distances[i]);

        } //end for

    } //end populateByRowLinear

    /**
     * Computes a minimum multiway cut.
     */
    @Override
    public int computeMultiwayCut(FlowNetwork flowNetwork) {

        int optimal = 0;

        StdOut.println("Cplex");

        // Try to optimize the model
        try {

            // Create the modeler/solver object
            IloCplex cplex = new IloCplex();
            cplex.setOut(null);
            cplex.setParam(IloCplex.IntParam.NodeFileInd, 2);
            cplex.setParam(IloCplex.BooleanParam.MemoryEmphasis, true);
            cplex.setParam(IloCplex.IntParam.Threads, 4);
            cplex.setParam(IloCplex.IntParam.RootAlg, 4);
            //cplex.setParam(IloCplex.IntParam.Cliques, 3);

            IloNumVar[] edgeLabelSums = new IloNumVar[flowNetwork.getNumEdges()];
            populateByRowInteger(cplex, edgeLabelSums, flowNetwork);

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

    } //end computeIntegerMultiwayCut

    /**
     * Computes a minimum multiway cut.
     */
    @Override
    public int computeMultiwayCut(FlowNetwork flowNetwork,
                                  double[] edgesRelaxed,
                                  Map<Integer, double[]> verticesRelaxed) {

        int optimal = 0;

        //StdOut.println("Cplex");

        // Try to optimize the model
        try {

            // Create the modeler/solver object
            IloCplex cplex = new IloCplex();
            cplex.setOut(null);
            cplex.setParam(IloCplex.IntParam.NodeFileInd, 2);
            cplex.setParam(IloCplex.BooleanParam.MemoryEmphasis, true);
            cplex.setParam(IloCplex.IntParam.Threads, 4);
            cplex.setParam(IloCplex.IntParam.RootAlg, 0);
            //cplex.setParam(IloCplex.IntParam.Cliques, 3);

            IloNumVar[] edgeLabelSums = new IloNumVar[flowNetwork.getNumEdges()];
            Map<Integer, IloNumVar[]> vertexLabels = new LinkedHashMap<>();
            populateByRowLinear(cplex, edgeLabelSums, vertexLabels, flowNetwork);

            // Examine the solution
            if (cplex.solve()) {

                //System.out.println();
                //System.out.println("Solution status = " + cplex.getStatus());
                //System.out.println();
                //System.out.println("Optimal = " + cplex.getObjValue());
                optimal = (int)cplex.getObjValue();
                //StdOut.println("The weight of the multiway cut: " + optimal);

                // Display which edges form the optimal multiway cut
                for (int i = 0; i < flowNetwork.getNumEdges(); i++) {

                    edgesRelaxed[i] = cplex.getValue(edgeLabelSums[i]);

                } //end for

                for (int i = 0; i < flowNetwork.getNumVertices(); i++) {

                    double[] vertexVector = new double[flowNetwork.getK()];

                    for (int j = 0; j < flowNetwork.getK(); j++) {

                        vertexVector[j] = cplex.getValue(vertexLabels.get(i)[j]);

                    } //end for

                    verticesRelaxed.put(i, vertexVector);

                } //end for

            } //end if

            cplex.end();

        } catch (IloException ex) {

            System.out.println("Concert Error: " + ex);

        } //end try-catch

        return optimal;

    } //end computeLinearMultiwayCut

} //end MultiwayCutSolver
