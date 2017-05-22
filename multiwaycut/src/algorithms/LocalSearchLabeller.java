package algorithms;

import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowNetwork;
import datastructures.flownetwork.FlowVertex;
import library.StdOut;
import utility.BreadthFirstSearch;
import utility.DefaultReachability;
import utility.MinCutReachability;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-05-19.
 */
public class LocalSearchLabeller {

    private FlowNetwork flowNetwork;

    private MultiwayCutStrategy ih;
    private String initialLabeller;

    private Map<Integer, FlowVertex> vertices;
    private Map<Integer, Integer> initialLabelling;

    private LinkedList<Integer> terminals;

    public LocalSearchLabeller(FlowNetwork flowNetwork, String initialLabeller, MultiwayCutStrategy ih) {

        this.flowNetwork = flowNetwork;
        this.initialLabeller = initialLabeller;
        this.ih = ih;

        vertices = flowNetwork.getVertices();
        terminals = flowNetwork.getTerminals();

        initialLabelling = new LinkedHashMap<>();

    } //end LocalSearchLabeller

    public void relabel(Map<Integer, Integer> labelling) {

        for (Map.Entry<Integer, Integer> entry : labelling.entrySet()) {

            vertices.get(entry.getKey()).setLocalSearchLabel(entry.getValue());

        } //end for

    } //end relabel

    public Map<Integer, Integer> initialLocalSearchLabel() {

        switch (initialLabeller) {

            case "ONEEACH":

                return oneEach();

            case "CLUMPS":

                return clumps();

            case "RANDOM":

                return random();

            case "ISOLATION HEURISTIC":

                return isolationHeuristic(ih);

            default:

                throw new IllegalArgumentException("Unrecognized initial labelling");

        } //end switch

    } //end initialLocalSearchLabel

    public int localSearchLabelCost() {

        int cost = 0;

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            for (FlowEdge edge : entry.getValue().getAllEdges()) {

                if ((entry.getValue() == edge.getStartVertex()) && (edge.getStartVertex().getLocalSearchLabel() != edge.getEndVertex().getLocalSearchLabel())) {

                    cost += edge.getCapacity();

                } //end if

            } //end for

        } //end for

        return cost;

    } //end localSearchLabelCost

    public LinkedList<FlowEdge> localSearchMinCut() {

        LinkedList<FlowEdge> minCut = new LinkedList<>();

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            for (FlowEdge edge : entry.getValue().getAllEdges()) {

                if ((entry.getValue() == edge.getStartVertex()) && (edge.getStartVertex().getLocalSearchLabel() != edge.getEndVertex().getLocalSearchLabel())) {

                    minCut.add(edge);

                } //end if

            } //end for

        } //end for

        return minCut;

    } //end localSearchMinCut

    public Map<Integer, Integer> getIsolationHeuristicLabelling(FlowNetwork ihFlowNetwork, LinkedList<FlowEdge> multiwayCut) {

        Map<Integer, FlowVertex> ihVertices = ihFlowNetwork.getVertices();
        BreadthFirstSearch bfs = new BreadthFirstSearch(ihVertices, ihFlowNetwork.getTerminals(), new MinCutReachability());
        Map<Integer, Boolean> ihMarked = bfs.getMarked();
        Map<Integer, Integer> ihLabelling = bfs.getLabelling();

        for (int i = 0; i < multiwayCut.size(); i++) {

            ihFlowNetwork.removeEdge(multiwayCut.get(i).getStartVertex().id(), multiwayCut.get(i).getEndVertex().id());
            ihFlowNetwork.removeEdge(multiwayCut.get(i).getEndVertex().id(), multiwayCut.get(i).getStartVertex().id());

        } //end for

        bfs.search(false);

        // Add the multiway cut back in
        for (int i = 0; i < multiwayCut.size(); i++) {

            ihFlowNetwork.addEdge(multiwayCut.get(i).getStartVertex().id(), multiwayCut.get(i).getEndVertex().id(), multiwayCut.get(i).getCapacity());

        } //end for

        // Assign island vertices to a neighbor
        for (int i = 0; i < ihFlowNetwork.getNumVertices(); i++) {

            if (!ihMarked.get(i).booleanValue() && ihVertices.get(i).getLabelledNeighbor() >= 0) {

                FlowVertex vertex = ihVertices.get(ihVertices.get(i).getLabelledNeighbor());

                ihVertices.get(i).setLocalSearchLabel(vertex.getLocalSearchLabel());
                ihLabelling.put(i, vertex.getLocalSearchLabel());

                bfs.search(i, false);

            } //end if

        } //end for

        return ihLabelling;

    } //end getIsolationHeuristicLabelling

    private Map<Integer, Integer> oneEach() {

        for (int i = 0; i < terminals.size(); i++) {

            vertices.get(terminals.get(i)).setLocalSearchLabel(i);
            initialLabelling.put(terminals.get(i), i);

        } //end for

        for (int i = 0; i < flowNetwork.getNumVertices(); i++) {

            if (!terminals.contains(i)) {

                vertices.get(i).setLocalSearchLabel(terminals.get(flowNetwork.getK() - 1));
                initialLabelling.put(i, terminals.get(flowNetwork.getK() - 1));

            } //end if

        } //end for

        // TODO
        // This prolly won't work
        // Instead, start with the last terminal and BFS add everything
        // Then 2nd last terminal BFS add everything

        return initialLabelling;

    } //end oneEach

    private Map<Integer, Integer> clumps() {

        BreadthFirstSearch bfs = new BreadthFirstSearch(vertices, terminals, new DefaultReachability());
        bfs.search(false);

        return bfs.getLabelling();

    } //end oneEach

    private Map<Integer, Integer> random() {

        BreadthFirstSearch bfs = new BreadthFirstSearch(vertices, terminals, new DefaultReachability());
        bfs.search(true);

        return bfs.getLabelling();

    } //end oneEach

    private Map<Integer, Integer> isolationHeuristic(MultiwayCutStrategy ih) {

        relabel(ih.getIsolationHeuristicLabelling());
        return ih.getIsolationHeuristicLabelling();

    } //end oneEach

} //end LocalSearchLabeller
