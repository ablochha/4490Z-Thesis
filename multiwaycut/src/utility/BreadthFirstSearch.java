package utility;

import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowVertex;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-05-21.
 */
public class BreadthFirstSearch {

    private Reachability reach;

    private Map<Integer, Boolean> marked;
    private Map<Integer, FlowVertex> vertices;
    private Map<Integer, Integer> labelling;

    private LinkedList<FlowVertex> bfs;
    private LinkedList<FlowEdge> edges;

    private int nodeCount;

    public BreadthFirstSearch(Map<Integer, FlowVertex> vertices, Reachability reach) {

        this.reach = reach;

        bfs = new LinkedList<>();
        marked = new LinkedHashMap<>();
        labelling = new LinkedHashMap<>();

        this.vertices = vertices;

        for (Map.Entry<Integer, FlowVertex> entry : this.vertices.entrySet()) {

            marked.put(entry.getValue().id(), false);

        } //end for

    } //end BreadthFirstSearch

    public BreadthFirstSearch(Map<Integer, FlowVertex> vertices, LinkedList<Integer> terminals, Reachability comp) {

        this(vertices, comp);

        for (int i = 0; i < terminals.size(); i++) {

            vertices.get(terminals.get(i)).setLocalSearchLabel(i);
            labelling.put(terminals.get(i), i);
            marked.put(terminals.get(i), true);
            bfs.add(vertices.get(terminals.get(i)));
            nodeCount++;

        } //end for

    } //end BreadthFirstSearch

    public void search(int i, boolean shuffle){

        nodeCount = 0;

        marked.put(i, true);
        bfs.add(vertices.get(i));
        nodeCount++;

        search(shuffle);

    } //end search

    public void search(boolean shuffle) {

        edges = new LinkedList<>();

        while (!bfs.isEmpty()) {

            if (shuffle) {

                Collections.shuffle(bfs);

            } //end if

            FlowVertex start = bfs.removeFirst();

            for (FlowEdge edge : start.getAllEdges()) {

                FlowVertex end = edge.getEndVertex();

                if (!edges.contains(edge)) {

                    edges.add(edge);

                } //end if

                if (reach.isReachable(edge, start, end, marked)) {

                    end.setLocalSearchLabel(start.getLocalSearchLabel());
                    labelling.put(end.id(), end.getLocalSearchLabel());
                    marked.put(end.id(), true);
                    bfs.add(end);
                    nodeCount++;
                    //StdOut.println("MARKED: " + end.vertexToString());

                } //end if

            } //end for

            for (FlowEdge edge : start.getAllResEdges()) {

                FlowVertex end = edge.getStartVertex();

                if (!edges.contains(edge)) {

                    edges.add(edge);

                } //end if

                if (reach.isReachable(edge, start, end, marked)) {

                    end.setLocalSearchLabel(start.getLocalSearchLabel());
                    labelling.put(end.id(), end.getLocalSearchLabel());
                    marked.put(end.id(), true);
                    bfs.add(end);
                    nodeCount++;
                    //StdOut.println("MARKED: " + end.vertexToString());

                } //end if

            } //end for

        } //end while

    } //end search

    public Map<Integer, Boolean> getMarked() {

        return marked;

    } //end getMarked

    public int getNodeCount() {

        return nodeCount;

    } //end getNodeCode

    public LinkedList<FlowEdge> getEdges() {

        return edges;

    } //end getEdges

    public Map<Integer, Integer> getLabelling() {

        return labelling;

    } //end getLabelling

} //end BreadthFirstSearch
