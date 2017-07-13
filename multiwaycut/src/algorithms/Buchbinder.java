package algorithms;

import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowNetwork;
import datastructures.flownetwork.FlowVertex;
import library.Matrix;
import library.StdOut;
import library.StdRandom;
import utility.ObjectCopy;
import utility.Pair;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Bloch-Hansen on 2017-04-25.
 */
public class Buchbinder implements MultiwayCutStrategy {

    private MultiwayCutStrategy solver;

    private long time;

    private double threshold;
    private double calCost;

    public void setSolver(MultiwayCutStrategy solver) {

        this.solver = solver;

    } //end setSolver

    public long getTime() {

        return time;

    } //end getTime

    @Override
    public double getThreshold() {

        return threshold;

    } //end getThreshold

    @Override
    public double getCalCost() {

        return calCost;

    } //end getCalCost

    private double[][] createA(FlowNetwork flowNetwork) {

        double[][] A = new double[flowNetwork.getK()][flowNetwork.getK()];

        for (int i = 0; i < flowNetwork.getK(); i++) {

            A[0][i] = 5.0 / 27.0;
            A[i][0] = 5.0 / 27.0;

        } //end for

        for (int i = 1; i < flowNetwork.getK(); i++) {

            A[1][i] = 5.0 / 108.0;
            A[i][1] = 5.0 / 108.0;

        } //end for

        for (int i = 2; i < flowNetwork.getK(); i++) {

            A[2][i] = 5.0 / 108.0;
            A[i][2] = 5.0 / 108.0;

        } //end for

        if (flowNetwork.getK() >= 1) {

            A[0][0] = 1.0;

        } //end if

        if (flowNetwork.getK() >= 2) {

            A[1][1] = 1.0 / 9.0;

        } //end if

        if (flowNetwork.getK() >= 3) {

            A[2][2] = 1.0 / 9.0;

        } //end if

        if (flowNetwork.getK() >= 4) {

            A[3][3] = (5.0 / 18.0) * -1.0;

        } //end if

        /*for (int i = 0; i < flowNetwork.getK(); i++) {

            for (int j = 0; j < flowNetwork.getK(); j++) {

                StdOut.print(A[i][j] + " ");

            } //end for

            StdOut.println();

        } //end for*/

        return A;

    } //end createA

    private Map<Integer, Pair[]> sort(Map<Integer, double[]> vertexLabels) {

        Map<Integer, Pair[]> sorted = new LinkedHashMap<>();

        // For every vertex, the coordinates must be sorted in non-increasing order
        for (Map.Entry<Integer, double[]> entry : vertexLabels.entrySet()) {

            Pair[] vector = new Pair[entry.getValue().length];

            // Store the original coordinate positions
            for (int i = 0; i < entry.getValue().length; i++) {

                vector[i] = new Pair(i, entry.getValue()[i]);

            } //end for

            Arrays.sort(vector);
            sorted.put(entry.getKey(), vector);

            /*StdOut.println("Sorted vertex " + entry.getKey());

            for (int i = 0; i < vector.length; i++) {

                StdOut.println(vector[i].index + ": " + vector[i].value);

            } //end for*/

        } //end for

        return sorted;

    } //end sort

    private void split(FlowNetwork flowNetwork, Map<Integer, Pair[]> vertexLabels) {

        // For every edge, if both vertices can't be sorted with respect to the same order, average i and j
        for (FlowEdge edge : flowNetwork.getEdges()) {

            int u = edge.getStartVertex().id();
            int v = edge.getEndVertex().id();

            int i = -1;
            int j = -1;

            Pair[] splitPoint = new Pair[flowNetwork.getK()];

            // Find the coordinates that are not in the same order
            for (int x = 0; x < flowNetwork.getK(); x++) {

                // The first differing coordinate is i
                if (i < 0 && j < 0 && vertexLabels.get(u)[x].index != vertexLabels.get(v)[x].index && vertexLabels.get(u)[x].value == vertexLabels.get(v)[x].value) {

                    i = x;

                } //end if

                // The second differing coordinate is j
                else if (i >= 0 && j < 0 && vertexLabels.get(u)[x].index != vertexLabels.get(v)[x].index && vertexLabels.get(u)[x].value == vertexLabels.get(v)[x].value) {

                    j = x;

                } //end else if

                // Copy the coordinate
                else {

                    splitPoint[x] = new Pair(vertexLabels.get(u)[x].index, vertexLabels.get(u)[x].value);

                } //end else

            } //end for

            if (i >= 0 && j >= 0) {

                splitPoint[i] = new Pair(vertexLabels.get(u)[i].index,(vertexLabels.get(u)[i].value + vertexLabels.get(u)[j].value) / 2.0);
                splitPoint[j] = new Pair(vertexLabels.get(u)[j].index,(vertexLabels.get(u)[i].value + vertexLabels.get(u)[j].value) / 2.0);

                vertexLabels.put(flowNetwork.getMaxVertexId() + 1, splitPoint);

                FlowEdge uw = flowNetwork.addEdge(edge.getStartVertex().id(), flowNetwork.getMaxVertexId() + 1, edge.getCapacity(), edge.getOriginal());
                FlowEdge wv = flowNetwork.addEdge(flowNetwork.getMaxVertexId(), edge.getEndVertex().id(), edge.getCapacity(), edge.getOriginal());

                flowNetwork.removeEdge(edge.getStartVertex().id(), edge.getEndVertex().id());
                flowNetwork.removeEdge(edge.getEndVertex().id(), edge.getStartVertex().id());

            } //end if

        } //end for

    } //end sort

    private Map<Integer, double[]> transform(FlowNetwork flowNetwork, Map<Integer, double[]> vertexLabels) {

        Map<Integer, double[]> transform = new LinkedHashMap<>();
        Map<Integer, Pair[]> sorted = sort(vertexLabels);
        split(flowNetwork, sorted);
        double[][] A = createA(flowNetwork);

        /*LinkedList<FlowEdge> edges = flowNetwork.getEdges();
        for (int i = 0; i < edges.size(); i++) {

            StdOut.println("Sorted edge (" + edges.get(i).getStartVertex().id() + "," + edges.get(i).getEndVertex().id() + ")");

            for (int j = 0; j < flowNetwork.getK(); j++) {

                StdOut.println(sorted.get(edges.get(i).getStartVertex().id())[j].index + ": " + sorted.get(edges.get(i).getStartVertex().id())[j].value +
                        ", " + sorted.get(edges.get(i).getEndVertex().id())[j].index + ": " + sorted.get(edges.get(i).getEndVertex().id())[j].value);

            }

        }*/

        // For every vector, create k new vectors
        for (Map.Entry<Integer, Pair[]> entry : sorted.entrySet()) {

            double[] transformedVector = new double[entry.getValue().length];
            double[] nonIncreasingVector = new double[entry.getValue().length];

            // Create k new vectors
            for (int i = 0; i < entry.getValue().length; i++) {

                double[][] array = new double[1][entry.getValue().length];

                // The first j coordinates are all coordinate i
                for (int j = 0; j <= i; j++) {

                    array[0][j] = entry.getValue()[i].value;

                } //end for

                // Coordinates j+1 to k are coordinates j
                for (int j = i+1; j < entry.getValue().length; j++) {

                    array[0][j] = entry.getValue()[j].value;

                } //end for

                double[][] temp = Matrix.multiply(array, A);
                //nonIncreasingVector[i] = Matrix.multiply(temp, Matrix.transpose(array))[0][0];
                transformedVector[(int)entry.getValue()[i].index] = Matrix.multiply(temp, Matrix.transpose(array))[0][0];

            } //end for

            /*double test = 0.0;
            for (int x = 0; x < entry.getValue().length; x++) {

                test += transformedVector[x];

            } //end for

            StdOut.println("Transformed vertex: " + entry.getKey());
            for (int x = 0; x < transformedVector.length; x++) {

                StdOut.println("Non-increasing Order: " + (int)entry.getValue()[x].index + ": " + String.format("%.3f", nonIncreasingVector[x]) + "\tOriginal Order: " + x + ": " + String.format("%.3f", transformedVector[x]));
                //StdOut.println("Coordinate " + x + ": " + transformedVector[x]);

            } //end for
            StdOut.println("The cost of the transformed vector is: " + test);*/

            transform.put(entry.getKey(), transformedVector);

        } //end for

        return transform;

    } //end sort

    private double round(FlowNetwork flowNetwork,
                      Map<Integer, double[]> vertexLabels) {

        FlowNetwork flowNetwork2 = new FlowNetwork(flowNetwork);
        //Map<Integer, double[]> vertexLabels2 = ObjectCopy.copyMapIntDoubleArray(vertexLabels);
        Map<Integer, double[]> vertexLabels2 = vertexLabels.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        int alg = StdRandom.uniform(1, 230);
        long start = System.nanoTime();

        double cost;

        if (alg < 122) {

            CalinescuUtility.subdivision(flowNetwork, vertexLabels);
            cost = CalinescuUtility.roundBuchbinder(flowNetwork, vertexLabels);

        } //end if

        else {

            CalinescuUtility.subdivision(flowNetwork, vertexLabels);
            vertexLabels = transform(flowNetwork, vertexLabels);
            cost = CalinescuUtility.roundCalinescu(flowNetwork, vertexLabels, CalinescuUtility.singleThreshold(CalinescuUtility.uniformPermutation(flowNetwork), StdRandom.uniform(0.0, 1.0))).index;

        } //end else

        time = System.nanoTime() - start;

        CalinescuUtility.subdivision(flowNetwork2, vertexLabels2);
        vertexLabels2 = transform(flowNetwork2, vertexLabels2);
        Pair cost2 = CalinescuUtility.roundCalinescu(flowNetwork2, vertexLabels2, CalinescuUtility.singleThreshold(CalinescuUtility.uniformPermutation(flowNetwork2), StdRandom.uniform(0.0, 1.0)));

        threshold = cost2.value;
        calCost = cost2.index;

        //StdOut.println("COST1: " + cost1 + ", COST2: " + (int)cost2.index);
        //return Math.min(cost1, (int)cost2.index);
        return cost;

    } //end round

    @Override
    public double computeMultiwayCut(FlowNetwork flowNetwork) {

        Map<Integer, double[]> vertexLabels = solver.getVertexLabels();

        StdOut.println("Buchbinder");
        double cost = round(flowNetwork, vertexLabels);
        StdOut.println("Buchbinder: The weight of the multiway cut: " + String.format("%.3f", cost));

        return cost;

    } //end computeMultiwayCut

} //end Buchbinder
