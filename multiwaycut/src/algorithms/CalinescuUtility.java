package algorithms;

import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowNetwork;
import datastructures.flownetwork.FlowVertex;
import library.StdOut;
import library.StdRandom;
import utility.Pair;

import java.util.*;

/**
 * Created by Bloch-Hansen on 2017-05-11.
 */
public class CalinescuUtility {

    public static LinkedList<Integer> binomialPermutation(FlowNetwork flowNetwork) {

        LinkedList<Integer> terminalOrder;
        int permutation = StdRandom.uniform(1, 3);

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

        return terminalOrder;

    } //end binomialPermutation

    public static LinkedList<Integer> uniformPermutation(FlowNetwork flowNetwork) {

        LinkedList<Integer> terminalOrder = new LinkedList<>();
        LinkedList<Integer> terminals = (LinkedList<Integer>)flowNetwork.getTerminals().clone();

        // Randomly pick a permutation of terminals from 1 to k
        while (terminals.size() > 0) {

            int pick = StdRandom.uniform(0, terminals.size());
            terminalOrder.add(terminals.remove(pick));

        } //end if

        //terminalOrder.add(flowNetwork.getK() - 1);

        return terminalOrder;

    } //end uniformPermutation

    private static int getIndex(LinkedList<Integer> terminals, int terminal) {

        int index = -1;

        for (int i = 0; i < terminals.size(); i++) {

            if (terminals.get(i) == terminal) {

                index = i;

            } //end if

        } //end for

        return index;

    } //end getIndex

    public static LinkedList<Pair> singleThreshold(LinkedList<Integer> terminalOrder, double rand) {

        LinkedList<Pair> order = new LinkedList<>();

        for (int i = 0; i < terminalOrder.size(); i++) {

            order.add(new Pair(terminalOrder.get(i), rand));

        } //end for

        return order;

    } //end singleThreshold

    public static LinkedList<Pair> descendingThreshold(LinkedList<Integer> terminalOrder, double b) {

        LinkedList<Pair> order = new LinkedList<>();

        for (int i = 0; i < terminalOrder.size(); i++) {

            order.add(new Pair(terminalOrder.get(i), StdRandom.uniform(0.0, b)));

        } //end for

        Collections.sort(order, Collections.reverseOrder());

        return order;

    } //end descendingThreshold

    public static LinkedList<Pair> independentThreshold(LinkedList<Integer> terminalOrder, double b) {

        LinkedList<Pair> order = new LinkedList<>();

        for (int i = 0; i < terminalOrder.size(); i++) {

            order.add(new Pair(terminalOrder.get(i), StdRandom.uniform(0.0, b)));

        } //end for

        return order;

    } //end descendingThreshold

    public static void subdivision(FlowNetwork flowNetwork,
                             Map<Integer, double[]> vertexLabels) {

        LinkedList<FlowEdge> queue = flowNetwork.getEdges();

        while (queue.size() > 0) {

            //StdOut.println("SIZE: " + queue.size());
            checkCoordinates(flowNetwork, queue, vertexLabels);

        } //end while

        /*queue = flowNetwork.getEdges();

        for (int i = 0; i < queue.size(); i++) {

            StdOut.println("Unsorted edge (" + queue.get(i).getStartVertex().id() + "," + queue.get(i).getEndVertex().id() + ")");
            int count = 0;

            for (int j = 0; j < flowNetwork.getK(); j++) {

                StdOut.println(j + " " + vertexLabels.get(queue.get(i).getStartVertex().id())[j] + " " + vertexLabels.get(queue.get(i).getEndVertex().id())[j]);

                // The ith coordinate differs
                //if (vertexLabels.get(queue.get(i).getStartVertex().id())[j] != vertexLabels.get(queue.get(i).getEndVertex().id())[j]) {
                    if (!String.format("%.3f", vertexLabels.get(queue.get(i).getStartVertex().id())[j]).equals(String.format("%.3f", vertexLabels.get(queue.get(i).getEndVertex().id())[j]))) {

                    count++;

                } //end if

            } //end for

            if (count > 2) {

                throw new IllegalArgumentException("WRONG");

            } //end if

        } //end for*/

    } //end subdivision

    public static void outputCoordinates(FlowNetwork flowNetwork,
                                   Map<Integer, double[]> vertexLabels,
                                   double[] edgeLabelSums) {

        for (int i = 0; i < edgeLabelSums.length; i++) {

            StdOut.println("Relaxed Edge: " + String.format("%.2f", edgeLabelSums[i]));

        } //end for

        for (Map.Entry<Integer, FlowVertex> entry : flowNetwork.getVertices().entrySet()) {

            StdOut.print("Relaxed Vertex " + entry.getKey() + ": ");

            for (int j = 0; j < flowNetwork.getK(); j++) {

                StdOut.print(String.format("%.2f", vertexLabels.get(entry.getKey())[j]) + ", ");

            } //end for

            StdOut.println("");

        } //end for

    } //end outputCoordinates

    public static Pair roundCalinescu(FlowNetwork flowNetwork,
                                      Map<Integer, double[]> vertexLabels,
                                      LinkedList<Pair> terminalOrder) {

        Map<Integer, LinkedList<Integer>> partitions = new LinkedHashMap<>();
        Map<Integer, FlowVertex> vertices = flowNetwork.getVertices();

        // Initialize the partition list
        for (int i = 0; i < flowNetwork.getK(); i++) {

            partitions.put(i, new LinkedList<>());
            partitions.get(i).add((int)terminalOrder.get(i).index);
            vertices.get((int)terminalOrder.get(i).index).setCalinescu(i);

        } //end for

        // Set all the vertices to the kth list
        for (Map.Entry<Integer, double[]> entry : vertexLabels.entrySet()) {

            if (!flowNetwork.getTerminals().contains(entry.getKey())) {

                partitions.get(flowNetwork.getK() - 1).add(entry.getKey());
                vertices.get(entry.getKey()).setCalinescu(flowNetwork.getK() - 1);

            } //end if

        } //end for

        // Group vertices within a sphere of radius rand with a terminal
        for (int i = 0; i < flowNetwork.getK() - 1; i++) {

            ListIterator<Integer> it = partitions.get(flowNetwork.getK() - 1).listIterator();

            // Loop through all of the vertices remaining in the kth list
            while (it.hasNext()) {

                //int vertex = partition.get(i);
                int vertex = it.next();

                // The distance is within the sphere of radius rand
                if (vertexLabels.get(vertex)[getIndex(flowNetwork.getTerminals(), (int)terminalOrder.get(i).index)] >= terminalOrder.get(i).value) {

                    it.remove();
                    partitions.get(i).add(vertex);
                    vertices.get(vertex).setCalinescu(i);

                } //end if

            } //end while

        } //end for

        return new Pair(calinescuCost(flowNetwork.getEdges()), terminalOrder.get(0).value);

    } //end round

    public static double roundBuchbinder(FlowNetwork flowNetwork,
                                      Map<Integer, double[]> vertexLabels) {

        Map<Integer, FlowVertex> vertices = flowNetwork.getVertices();

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
                //StdOut.println("Vertex " + entry.getKey() + ": Clock " + i + " (" + clocks[i] + ") divided by " + entry.getValue()[i] + " = " + clock);

                // Check if this clock is the winner
                if (clock < bestClock) {

                    bestClock = clock;
                    index = i;

                } //end if

            } //end for

            //StdOut.println("Vertex " + entry.getKey() + " assigned to label " + index);
            vertices.get(entry.getKey()).setCalinescu(index);

        } //end for

        return calinescuCost(flowNetwork.getEdges());

    } //end round

    private static double[] newPoint(FlowNetwork flowNetwork, FlowEdge edge, Map<Integer, double[]> vertexLabels) {

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

    private static void subdivide(FlowNetwork flowNetwork,
                                  LinkedList<FlowEdge> queue,
                                  FlowEdge edge,
                                  Map<Integer, double[]> vertexLabels) {

        vertexLabels.put(flowNetwork.getNumVertices(), newPoint(flowNetwork, edge, vertexLabels));

        FlowEdge uw = flowNetwork.addEdge(edge.getStartVertex().id(), flowNetwork.getNumVertices(), edge.getCapacity(), edge.getOriginal());
        FlowEdge wv = flowNetwork.addEdge(flowNetwork.getNumVertices() - 1, edge.getEndVertex().id(), edge.getCapacity(), edge.getOriginal());

        flowNetwork.removeEdge(edge.getStartVertex().id(), edge.getEndVertex().id());
        flowNetwork.removeEdge(edge.getEndVertex().id(), edge.getStartVertex().id());

        queue.add(wv);

    } //end subdivide

    private static void checkCoordinates(FlowNetwork flowNetwork,
                                         LinkedList<FlowEdge> queue,
                                         Map<Integer, double[]> vertexLabels) {

        FlowEdge edge = queue.removeFirst();
        int count = 0;

        // Check the coordinates
        for (int i = 0; i < flowNetwork.getK(); i++) {

            // The ith coordinate differs

            if (Math.floor(vertexLabels.get(edge.getStartVertex().id())[i] * 100) / 1000 != Math.floor(vertexLabels.get(edge.getEndVertex().id())[i] * 100) / 1000) {
                //if (!String.format("%.3f", vertexLabels.get(edge.getStartVertex().id())[i]).equals(String.format("%.3f", vertexLabels.get(edge.getEndVertex().id())[i]))) {
                count++;

            } //end if

        } //end for

        // Subdivision is required
        if (count > 2) {

            //StdOut.println("The edge (" + edge.getStartVertex().id() + ", " + edge.getEndVertex().id() + ") needs to be subdivided");
            subdivide(flowNetwork, queue, edge, vertexLabels);

        } //end if

    } //end checkCoordinates

    public static double calinescuCost(LinkedList<FlowEdge> edges) {

        ListIterator<FlowEdge> it = edges.listIterator();
        LinkedList<FlowEdge> multiwayCut = new LinkedList<>();

        double multiwayCutCost = 0.0;

        while (it.hasNext()) {

            FlowEdge edge = it.next();

            if (edge.getStartVertex().getCalinescu() != edge.getEndVertex().getCalinescu()
                    && !multiwayCut.contains(edge)
                    && !multiwayCut.contains(edge.getOriginal())) {

                multiwayCut.add(edge.getOriginal());
                multiwayCutCost += edge.getOriginal().getCapacity();
                //StdOut.println("Edge: " + edge.getOriginal().edgeToString() + ", Capacity: " + edge.getCapacity() + ", Sum: " + multiwayCutCost);

            } //end if

        } //end while

        return multiwayCutCost;

    } //end calinescuCost

} //end CalinescuUtility
