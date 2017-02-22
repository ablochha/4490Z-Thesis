package datastructures.flownetwork;

import library.StdOut;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-01-23.
 */
public class FlowNetwork {

    private Graph graph;
    private LinkedList<Integer> terminals;

    private int k;
    private int sourceId;
    private int sinkId;

    public FlowNetwork() {

        this.sourceId = -1;
        this.sinkId = -1;
        this.k = 0;
        this.graph = new Graph();
        this.terminals = new LinkedList<>();

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

            while (queueLength > 0) {
                //this.test();
                queueLength = graph.dischargeQueue();

            } //end while

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

    public void test() {

        graph.test();

    } //end test

} //end FlowNetwork
