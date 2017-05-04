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
 * Created by Bloch-Hansen on 2017-04-25.
 */
public class Buchbinder implements MultiwayCutStrategy {

    private MultiwayCutStrategy solver;

    private LinkedList<Integer> uniformPermutation(FlowNetwork flowNetwork) {

        LinkedList<Integer> terminalOrder = new LinkedList<>();
        LinkedList<Integer> terminals = (LinkedList<Integer>)flowNetwork.getTerminals().clone();

        // Randomly pick a permutation of terminals from 1 to k-1
        while (terminals.size() > 1) {

            int pick = StdRandom.uniform(0, terminals.size() - 1);
            terminalOrder.add(terminals.remove(pick));

        } //end if

        terminalOrder.add(flowNetwork.getK() - 1);

        return terminalOrder;

    } //end uniformPermutation

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
                           LinkedList<FlowEdge> queue,
                           FlowEdge edge,
                           Map<Integer, double[]> vertexLabels) {

        vertexLabels.put(flowNetwork.getNumVertices(), newPoint(flowNetwork, edge, vertexLabels));

        FlowEdge uw = flowNetwork.addEdge(edge.getStartVertex().id(), flowNetwork.getNumVertices(), edge.getCapacity(), edge.getOriginal());
        FlowEdge wv = flowNetwork.addEdge(flowNetwork.getNumVertices() - 1, edge.getEndVertex().id(), edge.getCapacity(), edge.getOriginal());

        flowNetwork.removeEdge(edge.getStartVertex().id(), edge.getEndVertex().id());

        queue.add(wv);

    } //end subdivide

    private void checkCoordinates(FlowNetwork flowNetwork,
                                  LinkedList<FlowEdge> queue,
                                  Map<Integer, double[]> vertexLabels) {

        FlowEdge edge = queue.removeFirst();
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

            subdivide(flowNetwork, queue, edge, vertexLabels);

        } //end if

    } //end checkCoordinates

    private void subdivision(FlowNetwork flowNetwork,
                             Map<Integer, double[]> vertexLabels) {

        LinkedList<FlowEdge> queue = flowNetwork.getEdges();

        while (queue.size() > 0) {

            checkCoordinates(flowNetwork, queue, vertexLabels);

        } //end while

    } //end subdivision

    private Map<Integer, double[]> sort(FlowNetwork flowNetwork,
                                        Map<Integer, double[]> vertexLabels) {

        // For every edge, uv's coordinates must be sorted in non-increasing order
        for (Map.Entry<Integer, double[]> entry : vertexLabels.entrySet()) {



        } //end for

        return null;

    } //end sort

    private int round(FlowNetwork flowNetwork,
                      Map<Integer, double[]> vertexLabels) {

        Map<Integer, LinkedList<Integer>> partitions = new LinkedHashMap<>();
        Map<Integer, FlowVertex> vertices = flowNetwork.getVertices();
        LinkedList<Integer> terminalOrder;

        double alg = StdRandom.uniform(0, 229);

        // Initialize the partition list
        for (int i = 0; i < flowNetwork.getK(); i++) {

            partitions.put(i, new LinkedList<>());

        } //end for

        // Exponential clocks on the simplex
        if (alg < 229) {

            double clocks[] = new double[flowNetwork.getK()];

            // Generate the exponential random variables
            for (int i = 0; i < flowNetwork.getK(); i++) {

                clocks[i] = StdRandom.exp(1);

            } //end for

            // Scale each vertex using the exponential clocks
            for (Map.Entry<Integer, double[]> entry : vertexLabels.entrySet()) {

                double bestClock = Double.MAX_VALUE;
                double clock;
                int index = -1;

                // Find the smallest exponential clock
                for (int i = 0; i < flowNetwork.getK(); i++) {

                    clock = clocks[i] / entry.getValue()[i];

                    // Check if this clock is the winner
                    if (clock < bestClock) {

                        bestClock = clock;
                        index = i;

                    } //end if

                } //end for

                vertices.get(entry.getKey()).setCalinescu(index);

            } //end for

            return flowNetwork.calinescuCost();

        } //end if

        // Calinescu on the transformed simplex
        else {

            double rand = StdRandom.uniform(0.0, 1.0);

            terminalOrder = uniformPermutation(flowNetwork);

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

            return flowNetwork.calinescuCost();

        } //end else

    } //end round

    public void setSolver(MultiwayCutStrategy solver) {

        this.solver = solver;

    } //end setSolver

    @Override
    public int computeMultiwayCut(FlowNetwork flowNetwork) {

        Map<Integer, double[]> vertexLabels = new LinkedHashMap<>();
        Map<Integer, double[]> sortedVertexLabels = new LinkedHashMap<>();
        double[] edgeLabelSums = new double[flowNetwork.getNumEdges()];

        StdOut.println("Buchbinder");

        solver.computeMultiwayCut(flowNetwork, edgeLabelSums, vertexLabels);
        subdivision(flowNetwork, vertexLabels);
        //sortedVertexLabels = sort(flowNetwork, vertexLabels);
        //outputCoordinates(flowNetwork, vertexLabels, edgeLabelSums);

        return round(flowNetwork, vertexLabels);

    } //end computeMultiwayCut

} //end Buchbinder
