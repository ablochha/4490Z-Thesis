package cplex;

import algorithms.MultiwayCutStrategy;
import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowNetwork;
import datastructures.flownetwork.FlowVertex;
import ilog.concert.*;
import ilog.cplex.IloCplex;
import library.StdOut;
import utility.ObjectCopy;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Bloch-Hansen on 2017-05-14.
 */
public class Simplex implements MultiwayCutStrategy {

    private long time;
    private Map<Integer, double[]> vertexLabels;

    private void populateByRowLinear(IloMPModeler model,
                                     IloNumVar[] edgeLabelSums,
                                     Map<Integer, IloNumVar[]> vertexLabels,
                                     FlowNetwork flowNetwork) throws IloException {

        LinkedList<FlowEdge> edges = flowNetwork.getEdges();
        LinkedList<Integer> terminals = flowNetwork.getTerminals();

        int n = flowNetwork.getNumVertices();
        int m = flowNetwork.getNumEdges();
        int k = flowNetwork.getK();
        double[] edgeCapacities = flowNetwork.getEdgeCapacities();
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
    public double computeMultiwayCut(FlowNetwork flowNetwork) {

        double optimal = 0;

        //StdOut.println("Cplex");

        Map<Integer, double[]> verticesRelaxed = new LinkedHashMap<>();
        double[] edgesRelaxed = new double[flowNetwork.getNumEdges()];

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
                optimal = cplex.getObjValue();
                StdOut.println("Simplex: The weight of the multiway cut: " + String.format("%.3f", optimal));

                // Display which edges form the optimal multiway cut
                for (int i = 0; i < flowNetwork.getNumEdges(); i++) {

                    edgesRelaxed[i] = cplex.getValue(edgeLabelSums[i]);

                } //end for

                for (Map.Entry<Integer, FlowVertex> entry : flowNetwork.getVertices().entrySet()) {

                    double[] vertexVector = new double[flowNetwork.getK()];

                    for (int j = 0; j < flowNetwork.getK(); j++) {

                        vertexVector[j] = cplex.getValue(vertexLabels.get(entry.getKey())[j]);

                    } //end for

                    verticesRelaxed.put(entry.getKey(), vertexVector);

                } //end for

            } //end if

            cplex.end();

        } catch (IloException ex) {

            System.out.println("Concert Error: " + ex);

        } //end try-catch

        this.vertexLabels = verticesRelaxed;
        return optimal;

    } //end computeMultiwayCut

    public long getTime() {

        return time;

    } //end getTime

    @Override
    public Map<Integer, double[]> getVertexLabels() {

        //return ObjectCopy.copyMapIntDoubleArray(this.vertexLabels);
        return this.vertexLabels.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

    } //end getVertexLabels

    @Override
    public boolean isFractional() {

        for (Map.Entry<Integer, double[]> entry : vertexLabels.entrySet()) {

            for (int i = 0; i < entry.getValue().length; i++) {

                if (entry.getValue()[i] % 1 != 0) {

                    return true;

                } //end if

            } //end for

        } //end for

        return false;

    } //end isFractional

} //end Simplex
