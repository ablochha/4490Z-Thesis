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

            //StdOut.println("Vertex: " + entry.getKey() + ", Before: " + vertices.get(entry.getKey()).getLocalSearchLabel() + ", After: " + entry.getValue());
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

    public double localSearchLabelCost() {

        double cost = 0;

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            for (FlowEdge edge : entry.getValue().getAllEdges()) {

                if ((entry.getValue() == edge.getStartVertex()) && (edge.getStartVertex().getLocalSearchLabel() != edge.getEndVertex().getLocalSearchLabel())) {

                    //StdOut.println(edge.edgeToString());
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

    public Map<Integer, Integer> getIsolationHeuristicLabelling(FlowNetwork ihFlowNetwork, LinkedList<FlowEdge> multiwayCut, int heavyIndex) {

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
        for (Map.Entry<Integer, FlowVertex> entry : ihFlowNetwork.getVertices().entrySet()) {

            if (!ihMarked.get(entry.getKey())) {

                //StdOut.println("Heavy Index: " + heavyIndex);
                entry.getValue().setLocalSearchLabel(heavyIndex);
                ihLabelling.put(entry.getKey(), heavyIndex);

            } //end if

        } //end for

        return ihLabelling;

    } //end getIsolationHeuristicLabelling

    private Map<Integer, Integer> oneEach() {

        for (int i = 0; i < terminals.size(); i++) {

            vertices.get(terminals.get(i)).setLocalSearchLabel(i);
            initialLabelling.put(terminals.get(i), i);

        } //end for

        for (Map.Entry<Integer, FlowVertex> entry : flowNetwork.getVertices().entrySet()) {

            if (!terminals.contains(entry.getKey())) {

                entry.getValue().setLocalSearchLabel(flowNetwork.getK() - 1);
                initialLabelling.put(entry.getKey(), flowNetwork.getK() - 1);

            } //end if

        } //end for

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
