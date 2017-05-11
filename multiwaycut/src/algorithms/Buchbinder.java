package algorithms;

import datastructures.flownetwork.FlowNetwork;
import library.Matrix;
import library.StdOut;
import utility.Pair;
import java.util.*;

/**
 * Created by Bloch-Hansen on 2017-04-25.
 */
public class Buchbinder implements MultiwayCutStrategy {

    private MultiwayCutStrategy solver;

    private long time;

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

        } //end for

        return sorted;

    } //end sort

    private Map<Integer, double[]> transform(FlowNetwork flowNetwork, Map<Integer, double[]> vertexLabels) {

        Map<Integer, double[]> transform = new LinkedHashMap<>();
        Map<Integer, Pair[]> sorted = sort(vertexLabels);
        double[][] A = createA(flowNetwork);

        // For every vector, create k new vectors
        for (Map.Entry<Integer, Pair[]> entry : sorted.entrySet()) {

            double[] transformedVector = new double[entry.getValue().length];

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
                transformedVector[entry.getValue()[i].index] = Matrix.multiply(A, Matrix.transpose(array))[0][0];

            } //end for

            double test = 0.0;
            for (int x = 0; x < entry.getValue().length; x++) {

                test += transformedVector[x];

            } //end for
            StdOut.println("THE COST OF THE TRANSFORMED VECTOR IS: " + test);
            transform.put(entry.getKey(), transformedVector);

        } //end for

        return transform;

    } //end sort

    private int round(FlowNetwork flowNetwork,
                      Map<Integer, double[]> vertexLabels) {

        int cost1 = CalinescuUtility.roundBuchbinder(new FlowNetwork(flowNetwork), vertexLabels);
        vertexLabels = transform(flowNetwork, vertexLabels);
        int cost2 = CalinescuUtility.roundCalinescu(new FlowNetwork(flowNetwork), vertexLabels, CalinescuUtility.uniformPermutation(flowNetwork));

        StdOut.println("COST1: " + cost1 + ", COST2: " + cost2);
        return Math.min(cost1, cost2);

    } //end round

    public void setSolver(MultiwayCutStrategy solver) {

        this.solver = solver;

    } //end setSolver

    public long getTime() {

        return time;

    } //end getTime

    @Override
    public int computeMultiwayCut(FlowNetwork flowNetwork) {

        Map<Integer, double[]> vertexLabels = new LinkedHashMap<>();
        double[] edgeLabelSums = new double[flowNetwork.getNumEdges()];

        StdOut.println("Buchbinder");

        solver.computeMultiwayCut(flowNetwork, edgeLabelSums, vertexLabels);

        long start = System.nanoTime();
        CalinescuUtility.subdivision(flowNetwork, vertexLabels);
        //outputCoordinates(flowNetwork, vertexLabels, edgeLabelSums);
        int cost = round(flowNetwork, vertexLabels);
        time = System.nanoTime() - start;

        StdOut.println("The weight of the multiway cut: " + cost);

        return cost;

    } //end computeMultiwayCut

} //end Buchbinder
