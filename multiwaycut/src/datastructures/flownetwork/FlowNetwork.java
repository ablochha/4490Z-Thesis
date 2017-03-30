package datastructures.flownetwork;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import library.StdOut;
import com.google.common.collect.TreeMultimap;
import java.awt.*;
import java.util.*;

/**
 * Created by Bloch-Hansen on 2017-01-23.
 */
public class FlowNetwork {

    private Graph graph;
    private LinkedList<Integer> terminals;
    private Map<Integer, Point> coordinates;

    private int k;
    private int sourceId;
    private int sinkId;

    public FlowNetwork() {

        this.sourceId = -1;
        this.sinkId = -1;
        this.k = 0;
        this.graph = new Graph();
        this.terminals = new LinkedList<>();
        this.coordinates = new LinkedHashMap<>();

    } //end FlowNetwork

    public FlowNetwork(LinkedList<FlowEdge> edges, LinkedList<Integer> terminals, int k) {

        this();
        this.k = k;

        ListIterator<FlowEdge> itEdges = edges.listIterator();

        while (itEdges.hasNext()) {

            FlowEdge edge = itEdges.next();

            this.addVertex(edge.getStartVertex().id());
            this.addVertex(edge.getEndVertex().id());
            this.addEdge(edge.getStartVertex().id(), edge.getEndVertex().id(), edge.getCapacity());

        } //end while

        this.setTerminals(terminals);

        ListIterator<Integer> itTerminals = terminals.listIterator();

        while (itTerminals.hasNext()) {

            int terminal = itTerminals.next();

            if (!this.containsVertex(terminal)) {

                throw new IllegalArgumentException("Terminal " + terminal + " not located in the largest connected component");

            } //end if

        } //end while

    } //end FlowNetwork

    public FlowNetwork(FlowNetwork flowNetwork) {

        this(flowNetwork.getEdges(), flowNetwork.getTerminals(), flowNetwork.getK());

    } //end FlowNetwork

    public void setK(int k) {

        this.k = k;

    } //end setK

    public int getK() {

        return this.k;

    } //end getK

    public void setTerminals(LinkedList<Integer> terminals) {

        this.terminals = terminals;

    } //end setTerminals

    public LinkedList<Integer> getTerminals() {

        return this.terminals;

    } //end getTerminals

    public boolean setCoordinates(int id, Point coordinates) {

        boolean found = false;

        for (Map.Entry<Integer, Point> entry : this.coordinates.entrySet()) {

            if (coordinates.equals(entry.getValue())) {

                found = true;

            } //end if

        } //end found

        if (!found) {

            this.coordinates.put(id, coordinates);
            return true;

        } //end if

        else {

            return false;

        } //end else

    } //end setCoordinates

    public double getProximity(int vertex) {

        double closestTerminalDistance = Double.MAX_VALUE;
        double secondClosetTerminalDistance = Double.MAX_VALUE;

        int count = 0;

        if (terminals.contains(vertex)) {

            getVertices().get(vertex).setProximity(0.0);
            return getVertices().get(vertex).getProximity();

        } //end if

        else {

            for (int i = 0; i < terminals.size(); i++) {

                double xDistance = Math.abs(this.coordinates.get(vertex).getX() - coordinates.get(terminals.get(i)).getX());
                double yDistance = Math.abs(this.coordinates.get(vertex).getY() - coordinates.get(terminals.get(i)).getY());
                double distance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));

                if (distance < closestTerminalDistance) {

                    if (count > 0) {

                        secondClosetTerminalDistance = closestTerminalDistance;

                    } //end if

                    closestTerminalDistance = distance;

                } //end if

                else if (distance < secondClosetTerminalDistance) {

                    secondClosetTerminalDistance = distance;

                } //end else if

                count++;

            } //end for

        } //end else

        //StdOut.println("CLOSEST: " + closestTerminalDistance + ", SECOND: " + secondClosetTerminalDistance);

        getVertices().get(vertex).setProximity(closestTerminalDistance / (closestTerminalDistance + secondClosetTerminalDistance));
        return getVertices().get(vertex).getProximity();

    } //end getProximity

    public void setSource(int sourceId) {

        if (sourceId >= 0) {

            if (sourceId != this.sinkId) {

                this.sourceId = sourceId;
                graph.addVertex(sourceId);

            } //end if

            else {

                this.sourceId = sourceId;
                graph.addVertex(sourceId);
                this.sinkId = -1;

            } //end else

        } //end if

        else {

            // must be positive number

        } //end else

    } //end setSource

    public int getSource() {

        return sourceId;

    } //end getSource

    public void setSink(int sinkId) {

        if (sinkId >= 0) {

            if (sinkId != this.sourceId) {

                this.sinkId = sinkId;
                graph.addVertex(sinkId);

            } //end if

            else {

                this.sinkId = sinkId;
                graph.addVertex(sinkId);
                this.sourceId = -1;

            } //end else

        } //end if

        else {

            // must be positive number

        } //end else

    } //end setSink

    public int getSink() {

        return sinkId;

    } //end getSink

    public void addVertex(int vertexId) {

        if (vertexId >= 0) {

            graph.addVertex(vertexId);

        } //end if

        else {

            // must be positive

        } //end else

    } //end addVertex

    public void removeVertex(int vertexId) {

        if (vertexId >= 0) {

            if (graph.containsVertex(vertexId)) {

                boolean success = graph.removeVertex(vertexId);

                if (success) {

                    if (vertexId == sourceId) {

                        sourceId = -1;

                    } //end if

                    if (vertexId == sinkId) {

                        sinkId = -1;

                    } //end if

                } //end if

                else {

                    // cannot be removed

                } //end else

            } //end if

            else {

                // graph no vertex

            } //end else

        } //end if

        else {

            // use positive

        } //end else

    } //end removeVertex

    public boolean containsVertex(int id) {

        return graph.containsVertex(id);

    } //end containsVertex

    public void setLocalSearchLabel(int id, int i) {

        graph.setLocalSearchLabel(id, i);

    } //end setLocalSearchLabel

    public void addEdge(int vertexId1,int vertexId2, int capacity) {

        if (vertexId1 >= 0 && vertexId2 >= 0 && capacity >= 0) {

            if (vertexId1 != vertexId2) {

                graph.addEdge(vertexId1, vertexId2, capacity);

            } //end if

            else {

                // must be different

            } //end else

        } //end if

        else if (vertexId1 < 0 || vertexId2 < 0) {

            // must be positive

        } //end else if

        else if (capacity < 0) {

            // must be more than zero

        } //end else if

        else {

            // failed

        } // end else

    } //end addEdge

    public void removeEdge(int vertexId1, int vertexId2) {

        if (vertexId1 >= 0 && vertexId2 >= 0) {

            graph.removeEdge(vertexId1, vertexId2);

        } //end if

        else {

            // must be positive

        } //end else

    } //end removeEdge

    public LinkedList<FlowEdge> goldbergTarjan(int s, int t) {

        setSource(s);
        setSink(t);
        //StdOut.println("GoldBerg-Tarjan(n^3) Source:" + getSource() + ", Sink: " + getSink());

        if (this.getSource() >= 0 && this.getSink() >= 0) {

            graph.resetFlow();
            graph.resetFrom();
            graph.buildResidualGraph();
            graph.resetExcess(sourceId);
            graph.initializeLabels(sourceId);

            int queueLength = graph.initialPush(sourceId, sinkId);
int debug = 0;
            while (queueLength > 0) {

                //if (debug % 50000 == 0)
                    //StdOut.println("THIS IS MADNESS: " + queueLength);
                queueLength = graph.dischargeQueue();
                //debug++;
            } //end while
            //System.out.println("Just about to cut the graph. Press \"ENTER\" to continue...");
            //Scanner scanner = new Scanner(System.in);
            //scanner.nextLine();
            //test();
            return graph.minCut(sinkId, sourceId);

        } //end if

        else {

            // no valid source and sink

        } //end else

        return null;

    } //end goldbergTarjan

    public Map<Integer, Integer> initialLocalSearchLabel(LinkedList<Integer> terminals) {

        return graph.initialLocalSearchLabel(terminals);

    } //end initialLocalSearchLabel

    public int localSearchLabelCost() {

        /*int [] labelCounts = new int[k];
        Arrays.fill(labelCounts, 0);

        for (Map.Entry<Integer, FlowVertex> entry : graph.getVertices().entrySet()) {

            labelCounts[entry.getValue().getLocalSearchLabel()]++;

        } //end for

        for (int i = 0; i < k; i++) {

            StdOut.println("Label " + i + " has " + labelCounts[i] + "vertices");

        } //end for*/

        return graph.localSearchLabelCost();

    } //end localSearchLabelCost

    public LinkedList<FlowEdge> localSearchMinCut() {

        return graph.localSearchMinCut();

    } //end localSearchMinCut

    public void relabel(Map<Integer, Integer> labelling) {

        graph.relabel(labelling);

    } //end relabel

    public Map<Integer, FlowVertex> getVertices() {

        return graph.getVertices();

    } //end getGraph

    public int getNumVertices() {

        return getVertices().size();

    } //end getNumVertices

    public int getMaxVertexId() {

        return graph.getMaxVertexId();

    } //end getMaxVertexId

    public LinkedList<FlowEdge> getEdges() {

        LinkedList<FlowEdge> edges = new LinkedList<>();

        for (Map.Entry<Integer, FlowVertex> entry : graph.getVertices().entrySet()) {

            for (FlowEdge edge : entry.getValue().getAllEdges()) {

                edges.add(edge);

            } //end for

        } //end for

        return edges;

    } //end getEdges

    public int[] getEdgeCapacities() {

        LinkedList<FlowEdge> edges = getEdges();
        int[] edgeCapacities = new int[edges.size()];
        int i = 0;

        ListIterator<FlowEdge> it = edges.listIterator();

        while (it.hasNext()) {

            edgeCapacities[i] = it.next().getCapacity();
            i++;

        } //end while

        return edgeCapacities;

    } //end getEdgeCapacities

    public int getNumEdges() {

        return getEdges().size();

    } //end getNumEdges

    public TreeMultimap<Double, Integer> getProximityList() {

        TreeMultimap<Double, Integer> proximities = TreeMultimap.create();

        for (Map.Entry<Integer, FlowVertex> entry : getVertices().entrySet()) {

            proximities.put(entry.getValue().getProximity(), entry.getKey());

        } //end for

        return proximities;

    } //end getProximityList

    public void test() {

        graph.test();

    } //end test

} //end FlowNetwork
