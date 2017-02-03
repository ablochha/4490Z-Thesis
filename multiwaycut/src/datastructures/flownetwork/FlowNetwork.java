package datastructures.flownetwork;

import library.In;
import library.StdOut;
import library.StdRandom;

import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-01-23.
 */
public class FlowNetwork {

    private static final String NEWLINE = System.getProperty("line.separator");

    private int sourceId;
    private int sinkId;
    private int maxFlow;

    private Graph graph;

    public FlowNetwork() {

        this.sourceId = -1;
        this.sinkId = -1;
        this.maxFlow = 0;
        this.graph = new Graph();

    } //end FlowNetwork

    /**
     * Declare a FlowNetwork with the number of vertices and edges given
     * @param vertices the number of vertices in the flow network
     * @param edges the number of edges in the flow network
     * @exception IllegalArgumentException if a negative number of edges is given
     */
    public FlowNetwork(int vertices, int edges) {

        // Call the previous constructor to initializes the vertices and adjacency list
        this();

        // Make sure the number of vertices isn't negative
        if (vertices < 0) {

            throw new IllegalArgumentException("Choose a nonnegative number of vertices");

        } //end if

        // Make sure the number of edges isn't negative
        if (edges < 0) {

            throw new IllegalArgumentException("Choose a nonnegative number of edges");

        } //end if

        // Initialize a random flow network by inserting randomly chosen edges
        for (int i = 0; i < edges; i++) {

            int u = StdRandom.uniform(vertices);
            int v = StdRandom.uniform(vertices);

            addVertex(u);
            addVertex(v);

            int capacity = StdRandom.uniform(100);
            addEdge(u, v, capacity);

        } //end for

    } //end FlowNetwork

    public FlowNetwork(In in, int vertices) {

        this();			// Initialize the vertices and adjacency list
        int edges = in.readInt();	// Read the number of edges

        // Make sure the number of vertices isn't negative
        if (vertices < 0) {

            throw new IllegalArgumentException("Choose a nonnegative number of vertices");

        } //end if

        // Make sure the number of edges isn't negative
        if (edges < 0) {

            throw new IllegalArgumentException("Choose a nonnegative number of edges");

        } //end if

        // Read each edge from the input
        for (int i = 0; i < edges; i++) {

            int u = in.readInt();
            int v = in.readInt();

            addVertex(u);
            addVertex(v);

            // Insert the edge
            int capacity = in.readInt();
            addEdge(u, v, capacity);

        } //end for

    } //end FlowNetwork

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
        maxFlow = 0;

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

    public void test() {

        graph.test();

    } //end test

} //end FlowNetwork
