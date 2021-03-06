package datastructures.flownetwork;

import library.StdOut;
import utility.BreadthFirstSearch;
import utility.MinCutReachability;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-01-23.
 */
public class Graph {

    private static final String NEWLINE = System.getProperty("line.separator");

    private Map<Integer, FlowVertex> vertices;
    private LinkedList<FlowVertex> queue;
    private FlowVertex startVertex;
    private FlowVertex endVertex;

    public Graph() {

        this.vertices = new LinkedHashMap<>();

    } //end Graph

    public boolean addVertex(int id) {

        if (!vertices.containsKey(id)) {

            vertices.put(id, new FlowVertex(id));
            return true;

        } //end if

        else {

            return false;

        } //end else

    } //end addVertex

    public boolean removeVertex(int id) {

        if (vertices.containsKey(id)) {

            boolean success = true;
            success = success && vertices.get(id).removeAllEdges();
            success = success && vertices.get(id).removeAllResEdges();
            vertices.remove(id);
            return success;

        } //end if

        else {

            return false;

        } //end else

    } //end removeVertex

    public boolean containsVertex(int id) {

        return vertices.containsKey(id);

    } //end containsVertex

    public void setLocalSearchLabel(int id, int i) {

        vertices.get(id).setLocalSearchLabel(i);

    } //end setLocalSearchLabel

    public boolean addEdge(int vertexId1, int vertexId2, double capacity) {

        this.addVertex(vertexId1);
        this.addVertex(vertexId2);

        FlowVertex startVertex = vertices.get(vertexId1);
        FlowVertex endVertex = vertices.get(vertexId2);

        boolean success = startVertex.addEdge(endVertex, capacity);
        success = success && endVertex.addResEdge(startVertex);

        return success;

    } //end addEdge

    public FlowEdge addEdge(int vertexId1, int vertexId2, double capacity, FlowEdge edge) {

        this.addVertex(vertexId1);
        this.addVertex(vertexId2);

        FlowVertex startVertex = vertices.get(vertexId1);
        FlowVertex endVertex = vertices.get(vertexId2);

        FlowEdge retEdge = startVertex.addEdge(endVertex, capacity, edge);
        boolean success = endVertex.addResEdge(startVertex);

        return retEdge;

    } //end addEdge

    public boolean removeEdge(int vertexId1, int vertexId2) {

        FlowVertex startVertex = vertices.get(vertexId1);
        FlowVertex endVertex = vertices.get(vertexId2);

        if (startVertex != null && endVertex != null) {

            boolean success = startVertex.removeEdge(endVertex);

            if (success) {

                endVertex.removeResEdge(startVertex);
                return true;

            } //end if

            else {

                return false;

            } //end else

        } //end if

        else {

            return false;

        } //end else

    } //end removeEdge

    public boolean resetFlow() {

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            if (!entry.getValue().resetFlow()) {

                return false;

            } //end if

        } //end for

        return true;

    } //end resetFlow

    public void resetFrom() {

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            entry.getValue().resetFrom();

        } //end for

    } //end resetFrom

    public boolean resetExcess(int startVertexId) {

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            entry.getValue().resetExcess();

            if (entry.getValue().id() == startVertexId) {

                entry.getValue().setExcess(-1);

            } //end if

        } //end for

        return true;

    } //end resetExcess

    public boolean initializeLabels(int startVertexId){

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            entry.getValue().resetLabel();

            if (entry.getValue().id() == startVertexId) {

                entry.getValue().setLabel(this.vertices.size());

            } //end if

        } //end for

        return true;

    } //end initializeLabels

    public void buildResidualGraph() {

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            entry.getValue().clearResAdjacencyList();
            entry.getValue().setDead(false);

        } //end for

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            entry.getValue().addEdgesToResGraph();

        } //end for

    } //end buildResidualGraph

    public int initialPush(int startVertexId, int endVertexId) {

        startVertex = vertices.get(startVertexId);
        endVertex = vertices.get(endVertexId);
        queue = new LinkedList<>();

        LinkedList<FlowEdge> startEdges = startVertex.getAllEdges();
        ListIterator<FlowEdge> it = startEdges.listIterator();

        while (it.hasNext()) {

            FlowVertex newVertex = it.next().pushFlowForward();

            if (newVertex != null && newVertex != startVertex && newVertex != endVertex) {

                queue.add(newVertex);

            } //end if

        } //end while

        startEdges = startVertex.getAllResEdges();
        it = startEdges.listIterator();

        while (it.hasNext()) {

            FlowVertex newVertex = it.next().pushResFlowForward();

            if (newVertex != null && newVertex != startVertex && newVertex != endVertex) {

                queue.add(newVertex);

            } //end if

        } //end while

        return queue.size();

    } //end initialPush

    public int dischargeQueue() {

        FlowVertex headVertex = queue.removeFirst();
        headVertex.resetEdge();

        while (headVertex.getExcess() > 0 && !headVertex.labelIncreased()) {
            //StdOut.println("HEAD VERTEX: " + headVertex.vertexToString());
            FlowVertex newVertex = headVertex.pushRelabel(startVertex.id());

            if (newVertex != null && newVertex != startVertex && newVertex != endVertex) {
                //StdOut.println("Added to queue: " + newVertex.vertexToString());
                queue.add(newVertex);

            } //end if

        } //end while

        if (headVertex.getExcess() > 0) {
            //StdOut.println("Added to queue22: " + headVertex.vertexToString() + " Source: " + startVertex.id() + ", Sink: " + endVertex.id());
            headVertex.resetIncreasedLabel();
            queue.add(headVertex);

        } //end if

        return queue.size();

    } //end dischargeQueue

    public int getOutFlow(int vertexId) {

        return vertices.get(vertexId).getOutFlow();

    } //end getOutFlow

    public int getInFlow(int vertexId) {

        return vertices.get(vertexId).getInFlow();

    } //end getInFlow

    public double getExcess(int vertexId) {

        return vertices.get(vertexId).getExcess();

    } //end getExcess

    public String graphToString() {

        StringBuilder s = new StringBuilder();

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            s.append(entry.getValue().vertexToString() + NEWLINE);

        } //end for

        return s.toString();

    } //end graphToString

    public LinkedList<FlowEdge> minCut(int sinkId, int sourceId) {

        BreadthFirstSearch bfs = new BreadthFirstSearch(vertices, new MinCutReachability());
        bfs.search(sourceId, false);

        Map<Integer, Boolean> marked = bfs.getMarked();
        LinkedList<FlowEdge> minCut = new LinkedList<>();

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            for (FlowEdge edge : entry.getValue().getAllEdges()) {

                if ((entry.getValue() == edge.getStartVertex()) && (marked.get(edge.getStartVertex().id())) &&
                        (!marked.get(edge.getEndVertex().id()))) {

                    //StdOut.println("ADDED EDGE: " + edge.edgeToString());
                    minCut.add(edge);

                } //end if

            } //end for

            for (FlowEdge edge : entry.getValue().getAllResEdges()) {

                if ((entry.getValue() == edge.getEndVertex()) && (marked.get(edge.getEndVertex().id())) &&
                        (!marked.get(edge.getStartVertex().id()))) {

                    //StdOut.println("ADDED EDGE: " + edge.edgeToString());
                    minCut.add(edge);

                } //end if

            } //end for

        } //end for

        return minCut;

    } //end minCut

    public Map<Integer, FlowVertex> getVertices() {

        return vertices;

    } //end getVertices

    public int getMaxVertexId() {

        int maxId = -1;

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            if (entry.getValue().id() > maxId) {

                maxId = entry.getValue().id();

            } //end if

        } //end for

        return maxId;

    } //end getMaxVertexId

    public void test() {

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            StdOut.println(entry.getValue().vertexToString());

        } //end for

    } //end test

} //end Graph
