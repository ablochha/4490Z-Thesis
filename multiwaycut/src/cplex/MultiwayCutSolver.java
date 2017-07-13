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
public class MultiwayCutSolver implements MultiwayCutStrategy {

    private long time;

    private void populateByRowInteger(IloMPModeler model,
                                    IloNumVar[] edgeLabelSums,
                                    FlowNetwork flowNetwork) throws IloException {

        LinkedList<FlowEdge> edges = flowNetwork.getEdges();
        LinkedList<Integer> terminals = flowNetwork.getTerminals();

        int n = flowNetwork.getNumVertices();
        int m = flowNetwork.getNumEdges();
        int k = flowNetwork.getK();
        double[] edgeCapacities = flowNetwork.getEdgeCapacities();

        Map<Integer, IloNumVar[]> vertexLabels = new LinkedHashMap<>();
        IloNumVar[][] edgeLabels = new IloNumVar[m][k];

        // Initialize the boolean labels for the vertices
        for (Map.Entry<Integer, FlowVertex> entry : flowNetwork.getVertices().entrySet()) {

            vertexLabels.put(entry.getValue().id(), model.boolVarArray(k));
            //vertexLabels.put(entry.getValue().id(), model.numVarArray(k, 0.0, 1.0, IloNumVarType.Float));

        } //end for

        // Initialize the boolean labels for the edges
        for (int i = 0; i < m; i++) {

            edgeLabels[i] = model.boolVarArray(k);
            //edgeLabels[i] = model.numVarArray(k, 0.0, 1.0, IloNumVarType.Float);
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

    /**
     * Computes a minimum multiway cut.
     */
    @Override
    public double computeMultiwayCut(FlowNetwork flowNetwork) {

        double optimal = 0;

        StdOut.println("Cplex");

        long start = System.nanoTime();

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
                optimal = cplex.getObjValue();
                StdOut.println("Cplex: The weight of the multiway cut: " + String.format("%.3f", optimal));

                // Display which edges form the optimal multiway cut
                for (int i = 0; i < flowNetwork.getNumEdges(); i++) {

                    //System.out.println("Edge " + i + " = " + cplex.getValue(edgeLabelSums[i]) + ", " + flowNetwork.getEdgeCapacities()[i]);

                } //end for

            } //end if

            cplex.end();

        } catch (IloException ex) {

            System.out.println("Concert Error: " + ex);

        } //end try-catch

        time = System.nanoTime() - start;

        return optimal;

    } //end computeIntegerMultiwayCut

    public long getTime() {

        return time;

    } //end getTime

} //end MultiwayCutSolver
