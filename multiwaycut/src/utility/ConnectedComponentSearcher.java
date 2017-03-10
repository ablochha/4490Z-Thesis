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

    public FlowNetwork getLargestConnectedComponent(FlowNetwork flowNetwork) {

        LinkedList<FlowVertex> bfs = new LinkedList<>();
        Map<Integer, Boolean> marked = new LinkedHashMap<>();
        Map<Integer, FlowVertex> vertices = flowNetwork.getVertices();

        LinkedList<FlowEdge> largest = new LinkedList<>();

        int largestComponent = 0;
        int currentComponent = 0;

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            marked.put(entry.getValue().id(), false);

        } //end for

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            if (!marked.get(entry.getValue().id()).booleanValue()) {

                LinkedList<FlowEdge> edges = new LinkedList<>();

                bfs.add(entry.getValue());
                marked.put(entry.getValue().id(), true);
                currentComponent++;

                while (!bfs.isEmpty()) {

                    FlowVertex start = bfs.removeFirst();

                    for (FlowEdge edge : start.getAllEdges()) {

                        FlowVertex end = edge.getEndVertex();

                        if (!edges.contains(edge)) {

                            edges.add(edge);

                        } //end if

                        if (!marked.get(end.id()).booleanValue()) {

                            marked.put(end.id(), true);
                            bfs.add(end);
                            currentComponent++;

                        } //end if

                    } //end for

                    for (FlowEdge edge : start.getAllResEdges()) {

                        FlowVertex end = edge.getStartVertex();

                        if (!edges.contains(edge)) {

                            edges.add(edge);

                        } //end if

                        if (!marked.get(end.id()).booleanValue()) {

                            marked.put(end.id(), true);
                            bfs.add(end);
                            currentComponent++;

                        } //end if

                    } //end for

                } //end while

                if (currentComponent > largestComponent) {

                    largestComponent = currentComponent;
                    largest = edges;

                } //end if

                StdOut.println("Largest: " + largestComponent + ", Current: " + currentComponent);
                currentComponent = 0;

            } //end if

        } //end for

        if (largestComponent == flowNetwork.getNumVertices()) {

            return flowNetwork;

        } //end if

        else {

            return new FlowNetwork(largest, flowNetwork.getTerminals(), flowNetwork.getK());

        } //end else

    } //end getLargestConnectedComponent

    public void connectComponents(FlowNetwork flowNetwork) {

        LinkedList<FlowVertex> bfs = new LinkedList<>();
        Map<Integer, Boolean> marked = new LinkedHashMap<>();
        Map<Integer, FlowVertex> vertices = flowNetwork.getVertices();

        FlowVertex connector = null;

        int current = 0;

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            marked.put(entry.getValue().id(), false);

        } //end for

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            if (!marked.get(entry.getValue().id()).booleanValue()) {

                if (current > 0) {

                    flowNetwork.addEdge(entry.getValue().id(), connector.id(), 0);

                } //end if

                connector = entry.getValue();

                bfs.add(entry.getValue());
                marked.put(entry.getValue().id(), true);

                while (!bfs.isEmpty()) {

                    FlowVertex start = bfs.removeFirst();

                    for (FlowEdge edge : start.getAllEdges()) {

                        FlowVertex end = edge.getEndVertex();

                        if (!marked.get(end.id()).booleanValue()) {

                            marked.put(end.id(), true);
                            bfs.add(end);

                        } //end if

                    } //end for

                    for (FlowEdge edge : start.getAllResEdges()) {

                        FlowVertex end = edge.getStartVertex();

                        if (!marked.get(end.id()).booleanValue()) {

                            marked.put(end.id(), true);
                            bfs.add(end);

                        } //end if

                    } //end for

                } //end while

                current++;

            } //end if

        } //end for

    } //end connectComponents

    public void concentricEdges(FlowNetwork flowNetwork) {

        int terminalDensity = flowNetwork.getNumVertices() / flowNetwork.getK();
        //int terminalDensity = 5;

        // Add additional edges based on proximity to terminals
        for (int i = 0; i < flowNetwork.getNumVertices(); i++) {

            for (int j = 0; j < additionalEdges(flowNetwork, i, terminalDensity); j++) {

                int end = StdRandom.uniform(0, flowNetwork.getNumVertices() - 1);

                // No self edges and no edges ending at a terminal
                while (end == i && !flowNetwork.getTerminals().contains(i)) {

                    end = StdRandom.uniform(0, flowNetwork.getNumVertices() - 1);

                } //end while

                flowNetwork.addEdge(i, end, 0);

            } //end for

        } //end for

    } //end concentricEdges

    public void concentricEdgeCapacities(FlowNetwork flowNetwork, double initialCapacity1, double initialCapacity2) {

        Map<Integer, FlowVertex> vertices = flowNetwork.getVertices();

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            for (FlowEdge edge : entry.getValue().getAllEdges()) {

                if (edge.getCapacity() == 0) {

                    edge.setCapacity(generateEdgeCapacity(flowNetwork, entry.getValue().id(), initialCapacity1, initialCapacity2));

                } //end if

            } //end for

            for (FlowEdge edge : entry.getValue().getAllResEdges()) {

                if (edge.getCapacity() == 0) {

                    edge.setCapacity(generateEdgeCapacity(flowNetwork, entry.getValue().id(), initialCapacity1, initialCapacity2));

                } //end if

            } //end for

        } //end for

    } //end concentricEdgeCapacities

    private int generateEdgeCapacity(FlowNetwork flowNetwork, int i, double initialCapacity1, double initialCapacity2) {

        int distance1 = distanceToClosestTerminal(flowNetwork, i, false);
        int distance2 = distanceToClosestTerminal(flowNetwork, i, true);
        int totalDistance = distance1 + distance2;

        if (distance1 == 0) {
            //StdOut.println("THIS IS MADNESS .00");
            return (int) StdRandom.uniform(initialCapacity1, initialCapacity2);

        } //end if

        double percent = (double) distance1 / (double) totalDistance;
        //StdOut.println("ONE: " + distance1 + ", TWO: " + distance2 + ", TOTAL: " + totalDistance + ", PERCENT: " + percent);

        if (percent <= 0.05) {
            //StdOut.println("THIS IS MADNESS .05");
            return (int) StdRandom.uniform((9.0 / 10.0) * initialCapacity1, (9.0 / 10.0) * initialCapacity2);

        } //end if

        else if (percent <= 0.10) {
            //StdOut.println("THIS IS MADNESS .10");
            return (int) StdRandom.uniform((8.0 / 10.0) * initialCapacity1, (8.0 / 10.0) * initialCapacity2);

        } //end else if

        else if (percent <= 0.15) {
            //StdOut.println("THIS IS MADNESS .15");
            return (int) StdRandom.uniform((7.0 / 10.0) * initialCapacity1, (7.0 / 10.0) * initialCapacity2);

        } //end else if

        else if (percent <= 0.20) {
            //StdOut.println("THIS IS MADNESS .20");
            return (int) StdRandom.uniform((6.0 / 10.0) * initialCapacity1, (6.0 / 10.0) * initialCapacity2);

        } //end else if

        else if (percent <= 0.25) {
            //StdOut.println("THIS IS MADNESS .25");
            return (int) StdRandom.uniform((5.0 / 10.0) * initialCapacity1, (5.0 / 10.0) * initialCapacity2);

        } //end else if

        else if (percent <= 0.30) {
            //StdOut.println("THIS IS MADNESS .30");
            return (int) StdRandom.uniform((4.0 / 10.0) * initialCapacity1, (4.0 / 10.0) * initialCapacity2);

        } //end else if
        else if (percent <= 0.35) {
            //StdOut.println("THIS IS MADNESS .35");
            return (int) StdRandom.uniform((3.0 / 10.0) * initialCapacity1, (3.0 / 10.0) * initialCapacity2);

        } //end else if

        else if (percent <= 0.40) {
            //StdOut.println("THIS IS MADNESS .40");
            return (int) StdRandom.uniform((2.0 / 10.0) * initialCapacity1, (2.0 / 10.0) * initialCapacity2);

        } //end else if

        else if (percent <= 0.45) {
            //StdOut.println("THIS IS MADNESS .45");
            return (int) StdRandom.uniform((1.0 / 10.0) * initialCapacity1, (1.0 / 10.0) * initialCapacity2);

        } //end else if

        else {
            //StdOut.println("THIS IS MADNESS .50");
            return (int) StdRandom.uniform((0.5 / 10.0) * initialCapacity1, (0.5 / 10.0) * initialCapacity2);

        } //end else

    } //end generateEdgeCapacity

    private int additionalEdges(FlowNetwork flowNetwork, int i, int terminalDensity) {

        int distance1 = distanceToClosestTerminal(flowNetwork, i, false);
        int distance2 = distanceToClosestTerminal(flowNetwork, i, true);
        int totalDistance = distance1 + distance2;

        if (distance1 == 0) {

            return terminalDensity;

        } //end if

        double percent = (double) distance1 / (double) totalDistance;

        if (percent <= 0.05) {

            return (int) (9.0 / 10.0) * terminalDensity;

        } //end if

        else if (percent <= 0.10) {

            return (int) (8.0 / 10.0) * terminalDensity;

        } //end else if

        else if (percent <= 0.15) {

            return (int) (7.0 / 10.0) * terminalDensity;

        } //end else if

        else if (percent <= 0.20) {

            return (int) (6.0 / 10.0) * terminalDensity;

        } //end else if
        else if (percent <= 0.25) {

            return (int) (5.0 / 10.0) * terminalDensity;

        } //end else if

        else if (percent <= 0.30) {

            return (int) (4.0 / 10.0) * terminalDensity;

        } //end else if

        else if (percent <= 0.35) {

            return (int) (3.0 / 10.0) * terminalDensity;

        } //end else if

        else if (percent <= 0.40) {

            return (int) (2.0 / 10.0) * terminalDensity;

        } //end else if

        else if (percent <= 0.45) {

            return (int) (1.0 / 10.0) * terminalDensity;

        } //end else if

        else {

            return 0;

        } //end else

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

} //end ConnectedComponentSearcher
