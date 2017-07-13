package utility;

import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowNetwork;
import datastructures.flownetwork.FlowVertex;
import datastructures.heappq.HeapPQ;
import datastructures.heappq.IntegerComparator;
import datastructures.heappq.Position;
import library.StdOut;
import library.StdRandom;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-02-20.
 */
public class ConnectedComponentSearcher {

    private static final double MARGIN = 0.25;

    public FlowNetwork getLargestConnectedComponent(FlowNetwork flowNetwork) {

        Map<Integer, FlowVertex> vertices = flowNetwork.getVertices();
        BreadthFirstSearch bfs = new BreadthFirstSearch(vertices, new DefaultReachability());
        Map<Integer, Boolean> marked = bfs.getMarked();
        LinkedList<FlowEdge> largest = new LinkedList<>();

        int largestComponent = 0;

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            if (!marked.get(entry.getValue().id()).booleanValue()) {

                bfs.search(entry.getValue().id(), false);

                if (bfs.getNodeCount() > largestComponent) {

                    largestComponent = bfs.getNodeCount();
                    largest = bfs.getEdges();

                } //end if

                //StdOut.println("Largest: " + largestComponent + ", Current: " +  bfs.getNodeCount());

            } //end if

        } //end for

        StdOut.println("Vertices: " + flowNetwork.getNumVertices() + ", Edges: " + flowNetwork.getNumEdges() + ", K: " + flowNetwork.getK());

        if (largestComponent == flowNetwork.getNumVertices()) {

            return flowNetwork;

        } //end if

        else {

            return new FlowNetwork(largest, flowNetwork.getTerminals(), flowNetwork.getK());

        } //end else

    } //end getLargestConnectedComponent

    public void connectComponents(FlowNetwork flowNetwork) {

        Map<Integer, FlowVertex> vertices = flowNetwork.getVertices();
        BreadthFirstSearch bfs = new BreadthFirstSearch(vertices, new DefaultReachability());
        Map<Integer, Boolean> marked = bfs.getMarked();

        FlowVertex connector = null;

        int current = 0;

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            if (!marked.get(entry.getValue().id()).booleanValue()) {

                if (current > 0) {

                    flowNetwork.addEdge(entry.getValue().id(), connector.id(), 0);

                } //end if

                connector = entry.getValue();
                bfs.search(entry.getValue().id(), false);

                current++;

            } //end if

        } //end for

    } //end connectComponents

    public void concentricEdges(FlowNetwork flowNetwork, double terminalDensityMultiplier, String decay, String format) {

        int terminalDensity = (int) Math.ceil((terminalDensityMultiplier * ((double) flowNetwork.getNumVertices() / (double) flowNetwork.getK())));
        int densityMargin = (int) Math.ceil(MARGIN * terminalDensity);
        Map<Integer, FlowVertex> vertices = flowNetwork.getVertices();

        // Add additional edges based on proximity to terminals
        for (int i = 0; i < flowNetwork.getNumVertices(); i++) {

            ub(flowNetwork, i, terminalDensity, decay, format);
            int count = 0;

            //StdOut.println("THIS IS MADNESS: " + i + ", NUMEDGES: " + (vertices.get(i).getNumEdges() + ", ADDITIONAL: " + vertices.get(i).getAdditionalEdges()));
            while (!vertices.get(i).isSaturated()) {

                int end = StdRandom.uniform(0, flowNetwork.getNumVertices() - 1);

                // No self edges or don't exceed the density bounds
                while (end == i ||
                        vertices.get(end).getNumEdges() + 1 > ub(flowNetwork, end, terminalDensity, decay, format) + densityMargin ||
                        vertices.get(end).containsEdge(i) ||
                        vertices.get(end).containsResEdge(vertices.get(i))) {

                    //StdOut.println("I: " + i + ", END: " + end + ", ADDITIONAL: " + vertices.get(i).getAdditionalEdges());

                    end++;

                    if (end == flowNetwork.getNumVertices()) {

                        end = end % flowNetwork.getNumVertices();
                        count++;

                    } //end if

                    if (count >= 2) {

                        break;

                    } //end if

                } //end while

                if (count >= 2) {

                    break;

                } //end if

                flowNetwork.addEdge(i, end, 0);

            } //end while

        } //end for

    } //end concentricEdges

    public void concentricEdgeCapacities(FlowNetwork flowNetwork, double initialCapacity1, double initialCapacity2, String decay, String format) {

        Map<Integer, FlowVertex> vertices = flowNetwork.getVertices();
        //LinkedList<Integer> terminals = flowNetwork.getTerminals();

        for (Map.Entry<Double, Integer> entry : flowNetwork.getProximityList().entries()) {

            for (FlowEdge edge : vertices.get(entry.getValue()).getAllEdges()) {

                if (edge.getCapacity() == 0) {

                    edge.setCapacity(generateEdgeCapacity(flowNetwork, entry.getValue(), initialCapacity1, initialCapacity2, decay, format));

                } //end if

            } //end for

            for (FlowEdge edge : vertices.get(entry.getValue()).getAllResEdges()) {

                if (edge.getCapacity() == 0) {

                    edge.setCapacity(generateEdgeCapacity(flowNetwork, entry.getValue(), initialCapacity1, initialCapacity2, decay, format));

                } //end if

            } //end for

        } //end for

    } //end concentricEdgeCapacities

    private int generateEdgeCapacity(FlowNetwork flowNetwork, int i, double initialCapacity1, double initialCapacity2, String decay, String format) {

        double percent = flowNetwork.getVertices().get(i).getProximity();

        //StdOut.println("ONE: " + distance1 + ", TWO: " + distance2 + ", TOTAL: " + totalDistance + ", PERCENT: " + percent);

        switch (decay) {

            case "LINEAR":

                if (percent == 0.0) {

                    return lb(StdRandom.uniform(initialCapacity1, initialCapacity2));

                } //end if

                else if (percent <= 0.05) {
                    //StdOut.println("THIS IS MADNESS .05");
                    return lb(StdRandom.uniform((9.0 / 10.0) * initialCapacity1, (9.0 / 10.0) * initialCapacity2));

                } //end else if

                else if (percent <= 0.10) {
                    //StdOut.println("THIS IS MADNESS .10");
                    return lb(StdRandom.uniform((8.0 / 10.0) * initialCapacity1, (8.0 / 10.0) * initialCapacity2));

                } //end else if

                else if (percent <= 0.15) {
                    //StdOut.println("THIS IS MADNESS .15");
                    return lb(StdRandom.uniform((7.0 / 10.0) * initialCapacity1, (7.0 / 10.0) * initialCapacity2));

                } //end else if

                else if (percent <= 0.20) {
                    //StdOut.println("THIS IS MADNESS .20");
                    return lb(StdRandom.uniform((6.0 / 10.0) * initialCapacity1, (6.0 / 10.0) * initialCapacity2));

                } //end else if

                else if (percent <= 0.25) {
                    //StdOut.println("THIS IS MADNESS .25");
                    return lb(StdRandom.uniform((5.0 / 10.0) * initialCapacity1, (5.0 / 10.0) * initialCapacity2));

                } //end else if

                else if (percent <= 0.30) {
                    //StdOut.println("THIS IS MADNESS .30");
                    return lb(StdRandom.uniform((4.0 / 10.0) * initialCapacity1, (4.0 / 10.0) * initialCapacity2));

                } //end else if

                else if (percent <= 0.35) {
                    //StdOut.println("THIS IS MADNESS .35");
                    return lb(StdRandom.uniform((3.0 / 10.0) * initialCapacity1, (3.0 / 10.0) * initialCapacity2));

                } //end else if

                else if (percent <= 0.40) {
                    //StdOut.println("THIS IS MADNESS .40");
                    return lb(StdRandom.uniform((2.0 / 10.0) * initialCapacity1, (2.0 / 10.0) * initialCapacity2));

                } //end else if

                else if (percent <= 0.45) {
                    //StdOut.println("THIS IS MADNESS .45");
                    return lb(StdRandom.uniform((1.0 / 10.0) * initialCapacity1, (1.0 / 10.0) * initialCapacity2));

                } //end else if

                else {
                    //StdOut.println("THIS IS MADNESS .50");
                    return lb(StdRandom.uniform((0.5 / 10.0) * initialCapacity1, (0.5 / 10.0) * initialCapacity2));

                } //end else

            case "EXPONENTIAL":

                if (percent == 0.0) {

                    return lb(StdRandom.uniform(initialCapacity1, initialCapacity2));

                } //end if

                else if (percent <= 0.10) {
                    //StdOut.println("THIS IS MADNESS .10");
                    return lb(StdRandom.uniform((1.0 / 2.0) * initialCapacity1, (1.0 / 2.0) * initialCapacity2));

                } //end else if

                else if (percent <= 0.20) {
                    //StdOut.println("THIS IS MADNESS .20");
                    return lb(StdRandom.uniform((1.0 / 4.0) * initialCapacity1, (1.0 / 4.0) * initialCapacity2));

                } //end else if

                else if (percent <= 0.30) {
                    //StdOut.println("THIS IS MADNESS .30");
                    return lb(StdRandom.uniform((1.0 / 8.0) * initialCapacity1, (1.0 / 8.0) * initialCapacity2));

                } //end else if

                else if (percent <= 0.40) {
                    //StdOut.println("THIS IS MADNESS .40");
                    return lb(StdRandom.uniform((1.0 / 16.0) * initialCapacity1, (1.0 / 16.0) * initialCapacity2));

                } //end else if

                else {
                    //StdOut.println("THIS IS MADNESS .50");
                    return lb(StdRandom.uniform((1.0 / 32.0) * initialCapacity1, (1.0 / 32.0) * initialCapacity2));

                } //end else

            default:

                throw new IllegalArgumentException("Unrecognized decay function");

        } //end switch

    } //end generateEdgeCapacity

    private int additionalEdges(FlowNetwork flowNetwork, int i, int terminalDensity, String decay, String format) {

        int distance1;
        int distance2;
        int totalDistance;
        double percent;

        if (format.equals("CONCENTRIC")) {

            distance1 = distanceToClosestTerminal(flowNetwork, i, false);
            distance2 = distanceToClosestTerminal(flowNetwork, i, true);
            totalDistance = distance1 + distance2;

            if (distance1 == 0) {
                //StdOut.println("THIS IS MADNESS .00");
                percent = 0.0;
                flowNetwork.getVertices().get(i).setProximity(percent);

            } //end if

            else {

                percent = (double) distance1 / (double) totalDistance;
                flowNetwork.getVertices().get(i).setProximity(percent);

            } //end else

        } //end if

        else {

            percent = flowNetwork.getProximity(i);
            //StdOut.println("PERCENT: " + percent + ", i: " + i);

        } //end if

        switch (decay) {

            case "LINEAR":

                if (percent == 0.0) {

                    return terminalDensity;

                } //end if

                else if (percent <= 0.05) {

                    return (int) ((9.0 / 10.0) * terminalDensity);

                } //end else if

                else if (percent <= 0.10) {

                    return (int) ((8.0 / 10.0) * terminalDensity);

                } //end else if

                else if (percent <= 0.15) {

                    return (int) ((7.0 / 10.0) * terminalDensity);

                } //end else if

                else if (percent <= 0.20) {

                    return (int) ((6.0 / 10.0) * terminalDensity);

                } //end else if

                else if (percent <= 0.25) {

                    return (int) ((5.0 / 10.0) * terminalDensity);

                } //end else if

                else if (percent <= 0.30) {

                    return (int) ((4.0 / 10.0) * terminalDensity);

                } //end else if

                else if (percent <= 0.35) {

                    return (int) ((3.0 / 10.0) * terminalDensity);

                } //end else if

                else if (percent <= 0.40) {

                    return (int) ((2.0 / 10.0) * terminalDensity);

                } //end else if

                else if (percent <= 0.45) {

                    return (int) ((1.0 / 10.0) * terminalDensity);

                } //end else if

                else {

                    return 0;

                } //end else

            case "EXPONENTIAL":

                if (percent == 0.0) {

                    return terminalDensity;

                } //end if

                else if (percent <= 0.10) {

                    return (int) ((1.0 / 2.0) * terminalDensity);

                } //end else if

                else if (percent <= 0.20) {

                    return (int) ((1.0 / 4.0) * terminalDensity);

                } //end else if

                else if (percent <= 0.30) {

                    return (int) ((1.0 / 8.0) * terminalDensity);

                } //end else if

                else if (percent <= 0.40) {

                    return (int) ((1.0 / 16.0) * terminalDensity);

                } //end else if

                else {

                    return (int) ((1.0 / 32.0) * terminalDensity);

                } //end else

            default:

                throw new IllegalArgumentException("Unrecognized decay function");

        } //end switch

    } //end additionalEdges

    private int distanceToClosestTerminal(FlowNetwork flowNetwork, int nonTerminal, boolean ignoreClosest) {

        int[] distancesToTerminals = dijkstra(flowNetwork, nonTerminal);

        if (!ignoreClosest) {

            return distancesToTerminals[0];

        } //end if

        else {

            return distancesToTerminals[1];

        } //end else

    } //end distanceToClosestTerminal

    private int[] dijkstra(FlowNetwork flowNetwork, int nonTerminal) {

        int numVertices = flowNetwork.getNumVertices();
        int[] distTo = new int[numVertices];
        int[] distancesToTerminals = new int[numVertices];
        Position[] locator = new Position[numVertices];

        HeapPQ<Integer, Integer> pq = new HeapPQ<>(numVertices, new IntegerComparator());
        Map<Integer, FlowVertex> vertices = flowNetwork.getVertices();

        for (int v = 0; v < numVertices; v++) {

            if (v == nonTerminal) {

                distTo[v] = 0;

            } //end if

            else {

                distTo[v] = Integer.MAX_VALUE;

            } //end else

            locator[v] = pq.insert(distTo[v], v);

        } //end for

        while (!pq.isEmpty()) {

            int v = pq.removeMin();

            for (FlowEdge e : vertices.get(v).getAllEdges()) {

                relax(pq, e, distTo, locator, true);

            } //end for

            for (FlowEdge e : vertices.get(v).getAllResEdges()) {

                relax(pq, e, distTo, locator, false);

            } //end for

        } //end while

        Arrays.fill(distancesToTerminals, Integer.MAX_VALUE);

        for (int i = 0; i < flowNetwork.getTerminals().size(); i++) {

            distancesToTerminals[flowNetwork.getTerminals().get(i)] = distTo[flowNetwork.getTerminals().get(i)];

        } //end for

        Arrays.sort(distancesToTerminals);

        return distancesToTerminals;

    } //end distanceToTerminal

    private void relax(HeapPQ pq, FlowEdge e, int[] distTo, Position[] locator, boolean forwards) {

        int v;
        int w;

        if (forwards) {

            v = e.getStartVertex().id();
            w = e.getEndVertex().id();

        } //end if

        else {

            v = e.getEndVertex().id();
            w = e.getStartVertex().id();

        } //end else

        if (distTo[w] > distTo[v] + 1) {

            distTo[w] = distTo[v] + 1;
            pq.decreaseKey(locator[w], distTo[w]);

        } //end if

    } //end relax

    private int lb(double x) {

        if (x >= 1.00) {

            return (int) x;

        } //end if

        else {

            return 1;

        } //end else

    } //end lb

    private int ub(FlowNetwork flowNetwork, int id, int terminalDensity, String decay, String format) {

        FlowVertex vertex = flowNetwork.getVertices().get(id);

        if (vertex.getAdditionalEdges() < 0) {

            vertex.setAdditionalEdges(additionalEdges(flowNetwork, id, terminalDensity, decay, format));

        } //end if

        return vertex.getAdditionalEdges();

    } //end ub

} //end ConnectedComponentSearcher
