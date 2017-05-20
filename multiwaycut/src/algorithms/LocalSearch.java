package algorithms;

import datastructures.flownetwork.*;
import library.StdOut;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * LocalSearch.java
 *
 * Course: CS4490Z
 * Author: Andrew Bloch-Hansen
 *
 * The IsolationHeuristic approximation algorithm performs a series
 * of minimum cuts to isolate specific vertices one at a time to solve
 * the multiway cut problem. It computes k-1 cuts, discarding the heaviest cut.
 */
public class LocalSearch implements MultiwayCutStrategy {

    public int iterations = 0;

    private long time;

    private double epsilon = 0.0;

    private String initialLabeller;

    private MultiwayCutStrategy ih;

    private Map<Integer, Integer> computeMinimumCostRelabel(FlowNetwork flowNetwork,
                                                           LinkedList<Integer> terminals,
                                                           int i) {

        FlowNetwork relabel = new FlowNetwork();

        Map<Integer, FlowVertex> vertices = flowNetwork.getVertices();
        Map<Integer, Integer> auxiliarySinkCapacities = new LinkedHashMap<>();
        Map<Integer, Integer> relabelledVertices = new LinkedHashMap<>();

        LinkedList<FlowEdge> minCut;

        int nextId = flowNetwork.getMaxVertexId() + 1;
        int sourceId;
        int sinkId;

        // Add the original vertices
        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            relabel.addVertex(entry.getValue().id());
            relabel.setLocalSearchLabel(entry.getValue().id(), entry.getValue().getLocalSearchLabel());

        } //end for

        // Add the auxiliary vertices and their edges
        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            for (FlowEdge edge : entry.getValue().getAllEdges()) {

                // Two vertices do have the same label
                if ((entry.getValue() == edge.getStartVertex()) &&
                    (edge.getStartVertex().getLocalSearchLabel() == edge.getEndVertex().getLocalSearchLabel())) {

                    relabel.addEdge(edge.getStartVertex().id(), edge.getEndVertex().id(), edge.getCapacity());

                } //end else if

                // Two vertices don't have the same label
                else if ((entry.getValue() == edge.getStartVertex()) &&
                        (edge.getStartVertex().getLocalSearchLabel() != edge.getEndVertex().getLocalSearchLabel())) {

                    relabel.addVertex(nextId);
                    relabel.setLocalSearchLabel(nextId, -1);

                    auxiliarySinkCapacities.put(nextId, edge.getCapacity());

                    // Vertices with the current label being expanded
                    if (edge.getStartVertex().getLocalSearchLabel() == i) {

                        relabel.addEdge(edge.getStartVertex().id(), nextId, 0);

                    } //end if

                    // Vertices without the current label being expanded
                    else if (edge.getStartVertex().getLocalSearchLabel() != i) {

                        relabel.addEdge(edge.getStartVertex().id(), nextId, edge.getCapacity());

                    } //end else if

                    // Vertices with the current label being expanded
                    if (edge.getEndVertex().getLocalSearchLabel() == i) {

                        relabel.addEdge(nextId, edge.getEndVertex().id(), 0);

                    } //end if

                    // Vertices without the current label being expanded
                    else if (edge.getEndVertex().getLocalSearchLabel() != i) {

                        relabel.addEdge(nextId, edge.getEndVertex().id(), edge.getCapacity());

                    } //end else if

                    nextId++;

                } //end if

            } //end for

        } //end for

        sourceId = nextId;
        sinkId = nextId + 1;
        relabel.addVertex(sourceId);
        relabel.setLocalSearchLabel(sourceId, -2);
        relabel.addVertex(sinkId);
        relabel.setLocalSearchLabel(sinkId, -3);

        // Add the rest of the edges
        for (Map.Entry<Integer, FlowVertex> entry : relabel.getVertices().entrySet()) {

            // Terminal vertices
            if (terminals.contains(entry.getValue().id())) {

                // The terminal has the same label as the label we are expanding
                if (entry.getValue().getLocalSearchLabel() == i) {

                    relabel.addEdge(sourceId, entry.getValue().id(), 0);
                    relabel.addEdge(entry.getValue().id(), sinkId, Integer.MAX_VALUE);

                } //end if

                // The terminal does not have the same label as the label we are expanding
                else if (entry.getValue().getLocalSearchLabel() != i) {

                    relabel.addEdge(sourceId, entry.getValue().id(), Integer.MAX_VALUE);
                    relabel.addEdge(entry.getValue().id(), sinkId, 0);

                } //end else if

            } //end for

            // Original vertices
            else if (entry.getValue().getLocalSearchLabel() >= 0) {

                relabel.addEdge(sourceId, entry.getValue().id(), 0);

                // Vertex isn't the same label as what we are expanding
                if (entry.getValue().getLocalSearchLabel() != i) {

                    relabel.addEdge(entry.getValue().id(), sinkId, 0);

                } //end if

                // Vertex is the same label as what we are expanding
                else if (entry.getValue().getLocalSearchLabel() == i) {

                    relabel.addEdge(entry.getValue().id(), sinkId, Integer.MAX_VALUE);

                } //end else if

            } //end else if

            // Auxiliary vertices
            else if (entry.getValue().getLocalSearchLabel() == -1) {

                relabel.addEdge(entry.getValue().id(), sinkId, auxiliarySinkCapacities.get(entry.getValue().id()));

            } //end else

        } //end for

        minCut = relabel.goldbergTarjan(sourceId, sinkId);
        FlowVertex source = relabel.getVertices().get(sourceId);

        // Relabel vertices who's edge to the source is in the cut
        for (FlowEdge edge : source.getAllEdges()) {

            // The vertex is relabelled
            if (minCut.contains(edge)) {

                // Don't relabel terminal vertices
                if (!terminals.contains(edge.getEndVertex().id())) {

                    edge.getEndVertex().setLocalSearchLabel(i);

                } //end if

            } //end if

        } //end for

        for (Map.Entry<Integer, FlowVertex> entry : relabel.getVertices().entrySet()) {

            if (entry.getValue().getLocalSearchLabel() >= 0) {

                relabelledVertices.put(entry.getValue().id(), entry.getValue().getLocalSearchLabel());

            } //end if

        } //end for

        return relabelledVertices;

    } //end computeMinimumCostRelabel

    /**
     * Computes a minimum cut from each terminal vertex to the sink.
     * @param multiwayCut a list of edges in the multiway cut
     */
    private int outputMultiwayCut(LinkedList<FlowEdge> multiwayCut) {

        int multiwayCutWeight = 0;

        //StdOut.println("Multiway Cut: ");

        for (int i = 0; i < multiwayCut.size(); i++) {

            //StdOut.println(multiwayCut.get(i).edgeToString());
            multiwayCutWeight += multiwayCut.get(i).getCapacity();

        } //end for

        StdOut.println("The weight of the multiway cut: " + multiwayCutWeight);
        return multiwayCutWeight;

    } //end outputMultiwayCut

    @Override
    public void setEpsilon(double epsilon){

        this.epsilon = epsilon;

    } //end setEpsilon

    @Override
    public void setInitialLabeller(String initialLabeller) {

        this.initialLabeller = initialLabeller;

    } //end setInitialLabeller

    @Override
    public void setIsolationHeuristic(MultiwayCutStrategy ih) {

        this.ih = ih;

    } //end setIsolationHeuristic

    @Override
    public long getTime() {

        return time;

    } //end getTime

    /**
     * Computes a minimum multiway cut.
     */
    @Override
    public int computeMultiwayCut(FlowNetwork flowNetwork) {

        LocalSearchLabeller labeller = new LocalSearchLabeller(flowNetwork, initialLabeller, ih);

        Map<Integer, Integer> labelling;
        Map<Integer, Integer> newLabelling;
        Map<Integer, Integer> bestLabelling;

        LinkedList<FlowEdge> multiwayCut;

        int labelCost;
        int newLabelCost;
        int bestLabelCost;

        long start = System.nanoTime();

        labelling = labeller.initialLocalSearchLabel();
        labelCost = labeller.localSearchLabelCost();

        StdOut.println("Local Search (epsilon: " + String.format("%.3f", (1 - (1.0/epsilon) / Math.pow(flowNetwork.getK(), 2))) + ")");
        //StdOut.println("The initial min cut cost is: " + labelCost);

        // Loop until no significant improved solutions are found
        while (true) {

            bestLabelCost = labelCost;

            // Do a relabel operation for each label
            for (int i = 0; i < flowNetwork.getK(); i++) {

                iterations++;

                newLabelling = computeMinimumCostRelabel(flowNetwork, flowNetwork.getTerminals(), i);
                labeller.relabel(newLabelling);
                newLabelCost = labeller.localSearchLabelCost();
                //StdOut.println("Min cut weight: " + newLabelCost);

                // The new labelling is better than the labellings in this iteration
                if (newLabelCost < labelCost) {

                   labelling = newLabelling;
                   labelCost = newLabelCost;

                } //end if

            } //end for

            //StdOut.println("Cost to beat: " + (1 - (1.0/epsilon) / Math.pow(flowNetwork.getK(), 2)) * bestLabelCost);
            //StdOut.println("Percent: " + (1 - (1.0/epsilon) / Math.pow(flowNetwork.getK(), 2)));
            if (labelCost >= (1 - (1.0/epsilon) / Math.pow(flowNetwork.getK(), 2)) * bestLabelCost) {

                bestLabelling = labelling;
                bestLabelCost = labelCost;
                labeller.relabel(bestLabelling);
                break;

            } //end if

        } //end while

        multiwayCut = labeller.localSearchMinCut();
        outputMultiwayCut(multiwayCut);

        time = System.nanoTime() - start;

        return bestLabelCost;

    } //end computeMultiwayCut

} //end LocalSearch
