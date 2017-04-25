package algorithms;

import cplex.MultiwayCutSolver;
import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowNetwork;
import datastructures.flownetwork.FlowVertex;
import library.StdOut;
import library.StdRandom;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-04-24.
 */
public class Calinescu {

    private double[] newPoint(FlowNetwork flowNetwork, FlowEdge edge, Map<Integer, double[]> vertexLabels) {

        double[] newPoint = new double[flowNetwork.getK()];

        int terminal1 = -1;
        int terminal2 = -1;

        double difference1 = 0.0;
        double difference2 = 0.0;

        double alpha = 0.0;

        // Find two coordinates to change
        for (int i = 0; i < flowNetwork.getK(); i++) {

            // xu < xv
            if (vertexLabels.get(edge.getStartVertex().id())[i] < vertexLabels.get(edge.getEndVertex().id())[i] && terminal1 < 0) {

                terminal1 = i;
                difference1 = vertexLabels.get(edge.getEndVertex().id())[i] - vertexLabels.get(edge.getStartVertex().id())[i];

            } //end if

            // xu > xv
            if (vertexLabels.get(edge.getStartVertex().id())[i] > vertexLabels.get(edge.getEndVertex().id())[i] && terminal2 < 0) {

                terminal2 = i;
                difference2 = vertexLabels.get(edge.getStartVertex().id())[i] - vertexLabels.get(edge.getEndVertex().id())[i];

            } //end if

        } //end for

        // Alpha is the minimum of the differences
        if (difference1 < difference2) {

            alpha = difference1;

        } //end if

        else {

            alpha = difference2;

        } //end else

        for (int i = 0; i < flowNetwork.getK(); i++) {

            newPoint[i] = vertexLabels.get(edge.getStartVertex().id())[i];

            if (i == terminal1) {

                newPoint[i] = vertexLabels.get(edge.getStartVertex().id())[i] + alpha;

            } //end if

            if (i == terminal2) {

                newPoint[i] = vertexLabels.get(edge.getStartVertex().id())[i] - alpha;

            } //end if

        } //end for

        return newPoint;

    } //end newPoint

    private void subdivide(FlowNetwork flowNetwork,
                           FlowEdge edge,
                           Map<Integer, double[]> vertexLabels) {

        vertexLabels.put(flowNetwork.getNumVertices(), newPoint(flowNetwork, edge, vertexLabels));

        FlowEdge uw = flowNetwork.addEdge(edge.getStartVertex().id(), flowNetwork.getNumVertices(), edge.getCapacity(), edge.getOriginal());
        FlowEdge wv = flowNetwork.addEdge(flowNetwork.getNumVertices() - 1, edge.getEndVertex().id(), edge.getCapacity(), edge.getOriginal());

        flowNetwork.removeEdge(edge.getStartVertex().id(), edge.getEndVertex().id());

        checkCoordinates(flowNetwork, wv, vertexLabels);

    } //end subdivide

    private void checkCoordinates(FlowNetwork flowNetwork,
                                  FlowEdge edge,
                                  Map<Integer, double[]> vertexLabels) {

        int count = 0;

        // Check the coordinates
        for (int i = 0; i < flowNetwork.getK(); i++) {

            // The ith coordinate differs
            if (vertexLabels.get(edge.getStartVertex().id())[i] != vertexLabels.get(edge.getEndVertex().id())[i]) {

                count++;

            } //end if

        } //end for

        // Subdivision is required
        if (count > 2) {

            subdivide(flowNetwork, edge, vertexLabels);

        } //end if

    } //end checkCoordinates

    private void subdivision(FlowNetwork flowNetwork,
                             Map<Integer, double[]> vertexLabels) {

        LinkedList<FlowEdge> edges = flowNetwork.getEdges();
        ListIterator<FlowEdge> it = edges.listIterator();

        // Check edges to see that vectors differ by at most 2 coordinates
        while (it.hasNext()) {

            FlowEdge edge = it.next();
            checkCoordinates(flowNetwork, edge, vertexLabels);

        } //end while

    } //end subdivision

    private void outputCoordinates(FlowNetwork flowNetwork,
                                   Map<Integer, double[]> vertexLabels,
                                   double[] edgeLabelSums) {

        for (int i = 0; i < edgeLabelSums.length; i++) {

            StdOut.println("Relaxed Edge: " + String.format("%.2f", edgeLabelSums[i]));

        } //end for

        for (int i = 0; i < flowNetwork.getNumVertices(); i++) {

            StdOut.print("Relaxed Vertex: ");

            for (int j = 0; j < flowNetwork.getK(); j++) {

                StdOut.print(String.format("%.2f", vertexLabels.get(i)[j]) + ", ");

            } //end for

            StdOut.println("");

        } //end for

    } //end outputCoordinates

    private int round(FlowNetwork flowNetwork,
                      Map<Integer, LinkedList<Integer>> partitions,
                      Map<Integer, double[]> vertexLabels) {

        Map<Integer, FlowVertex> vertices = flowNetwork.getVertices();
        LinkedList<Integer> terminalOrder;

        int permutation = StdRandom.uniform(1, 3);
        double rand = StdRandom.uniform(0.0, 1.0);

        // Initialize the partition list
        for (int i = 0; i < flowNetwork.getK(); i++) {

            partitions.put(i, new LinkedList<>());

        } //end for

        // Regular terminal ordering
        if (permutation == 1) {

            terminalOrder = flowNetwork.getTerminals();

        } //end if

        // Reverse terminal ordering
        else {

            terminalOrder = new LinkedList<>();

            for (int i = flowNetwork.getK() - 2; i >= 0; i--) {

                terminalOrder.add(flowNetwork.getTerminals().get(i));

            } //end for

            terminalOrder.add(flowNetwork.getTerminals().get(flowNetwork.getK() - 1));

        } //end else

        // Set all the vertices to the kth list
        for (Map.Entry<Integer, double[]> entry : vertexLabels.entrySet()) {

            partitions.get(flowNetwork.getK() - 1).add(entry.getKey());
            vertices.get(entry.getKey()).setCalinescu(flowNetwork.getK() - 1);

        } //end for

        // Group vertices within a sphere of radius rand with a terminal
        for (int i = 0; i < flowNetwork.getK() - 1; i++) {

            ListIterator<Integer> it = partitions.get(flowNetwork.getK() - 1).listIterator();

            // Loop through all of the vertices remaining in the kth list
            while (it.hasNext()) {

                int vertex = it.next();
                double distance = 0.0;

                // Calculate the distance from the terminal to the vertex
                for (int j = 0; j < flowNetwork.getK(); j++) {

                    distance += Math.abs(vertexLabels.get(terminalOrder.get(i))[j] - vertexLabels.get(vertex)[j]);

                } //end for

                distance /= 2.0;

                // The distance is within the sphere of radius rand
                if (distance <= rand) {

                    it.remove();
                    partitions.get(i).add(vertex);
                    vertices.get(vertex).setCalinescu(i);

                } //end if

            } //end while

        } //end for

        ListIterator<FlowEdge> it = flowNetwork.getEdges().listIterator();
        LinkedList<FlowEdge> multiwayCut = new LinkedList<>();

        int multiwayCutCost = 0;

        while (it.hasNext()) {

            FlowEdge edge = it.next();

            if (edge.getStartVertex().getCalinescu() != edge.getEndVertex().getCalinescu()
                    && !multiwayCut.contains(edge)
                    && !multiwayCut.contains(edge.getOriginal())) {

                multiwayCut.add(edge.getOriginal());
                multiwayCutCost += edge.getOriginal().getCapacity();

            } //end if

        } //end while

        StdOut.println("The weight of the multiway cut: " + multiwayCutCost);
        return multiwayCutCost;

    } //end round

    /**
     * Computes a minimum multiway cut.
     */
    public int computeMultiwayCut(FlowNetwork flowNetwork, MultiwayCutSolver solver) {

        Map<Integer, double[]> vertexLabels = new LinkedHashMap<>();
        Map<Integer, LinkedList<Integer>> partitions = new LinkedHashMap<>();
        double[] edgeLabelSums = new double[flowNetwork.getNumEdges()];

        StdOut.println("Calinescu");

        solver.computeLinearMultiwayCut(flowNetwork, edgeLabelSums, vertexLabels);
        //outputCoordinates(flowNetwork, vertexLabels, edgeLabelSums);
        subdivision(flowNetwork, vertexLabels);

        return round(flowNetwork, partitions, vertexLabels);

    } //end computeMultiwayCut

} //end Calinescu
