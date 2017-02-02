package algorithms;

import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowNetwork;
import datastructures.flownetwork.FlowVertex;
import datastructures.flownetwork.Graph;
import library.In;
import library.StdOut;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;
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
public class LocalSearch {

    /**
     * Reads the terminal vertices to be isolated.
     * @param in a text representation of the flow network
     * @param k the number of terminal vertices
     * @param terminals a list of the terminal vertices
     */
    private void readTerminals(In in, int k, LinkedList<Integer> terminals) {

        StdOut.println("k: " + k);

        // Store each of the terminal vertices id's
        for (int i = 0; i < k; i++) {

            terminals.add(in.readInt());
            StdOut.println("terminal " + (i+1) + ": " + terminals.get(i));

        } //end for

        StdOut.println();

    } //end readTerminals

    public Map<Integer, Integer> computeMinimumCostRelabel(FlowNetwork flowNetwork, int i, LinkedList<Integer> terminals) {

        FlowNetwork relabel = new FlowNetwork();
        Map<Integer, FlowVertex> vertices = flowNetwork.getVertices();
        Map<Integer, Integer> auxiliarySinkCapacities = new LinkedHashMap<>();
        LinkedList<FlowEdge> minCut;
        Map<Integer, Integer> relabelledVertices = new LinkedHashMap<>();

        int nextId = 0;
        int sourceId = -1;
        int sinkId = -1;

        // Add the original vertices
        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            relabel.addVertex(entry.getValue().id());
            relabel.setLocalSearchLabel(entry.getValue().id(), entry.getValue().getLocalSearchLabel());
            nextId++;

        } //end for

        // Add the auxiliary vertices and their edges
        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            for (FlowEdge edge : entry.getValue().getAllEdges()) {

                // Two vertices do have the same label
                if ((entry.getValue() == edge.getStartVertex()) && (edge.getStartVertex().getLocalSearchLabel() == edge.getEndVertex().getLocalSearchLabel())) {

                    relabel.addEdge(edge.getStartVertex().id(), edge.getEndVertex().id(), edge.getCapacity());

                } //end else if

                // Two vertices don't have the same label
                else if ((entry.getValue() == edge.getStartVertex()) && (edge.getStartVertex().getLocalSearchLabel() != edge.getEndVertex().getLocalSearchLabel())) {

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

            /*for (FlowEdge edge : entry.getValue().getAllResEdges()) {

                // Two vertices do have the same label
                if ((entry.getValue() == edge.getEndVertex()) && (edge.getStartVertex().getLocalSearchLabel() == edge.getEndVertex().getLocalSearchLabel())) {

                    relabel.addEdge(edge.getEndVertex().id(), edge.getStartVertex().id(), edge.getCapacity());

                } //end if

                // Two vertices don't have the same label
                else if ((entry.getValue() == edge.getEndVertex()) && (edge.getStartVertex().getLocalSearchLabel() != edge.getEndVertex().getLocalSearchLabel())) {

                    relabel.addVertex(nextId);
                    relabel.setLocalSearchLabel(nextId, -1);

                    auxiliarySinkCapacities.put(nextId, edge.getCapacity());

                    // Vertices with the current label being expanded
                    if (edge.getEndVertex().getLocalSearchLabel() == i) {

                        relabel.addEdge(edge.getEndVertex().id(), nextId, 0);

                    } //end if

                    // Vertices without the current label being expanded
                    else if (edge.getEndVertex().getLocalSearchLabel() != i) {

                        relabel.addEdge(edge.getEndVertex().id(), nextId, edge.getCapacity());

                    } //end else if

                    // Vertices with the current label being expanded
                    if (edge.getStartVertex().getLocalSearchLabel() == i) {

                        relabel.addEdge(nextId, edge.getStartVertex().id(), 0);

                    } //end if

                    // Vertices without the current label being expanded
                    else if (edge.getStartVertex().getLocalSearchLabel() != i) {

                        relabel.addEdge(nextId, edge.getStartVertex().id(), edge.getCapacity());

                    } //end else if

                    nextId++;

                } //end else if

            } //end for*/

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
//relabel.test();
        minCut = relabel.goldbergTarjan(sourceId, sinkId);
        for (int x = 0; x < minCut.size(); x++) {


            StdOut.println(minCut.get(x).edgeToString());
        } //end for
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
     * Computes a minimum multiway cut.
     * @param in a text representation of the flow network
     */
    public int computeMultiwayCut(In in) {

        // The number of terminal vertices, and the heaviest cut
        int k = in.readInt();
        int heavyIndex;
        int labelCost = 0;
        int newLabelCost = 0;
        int bestLabelCost = 0;

        Map<Integer, Integer> labelling;
        Map<Integer, Integer> newLabelling;
        Map<Integer, Integer> bestLabelling;

        // The minimum cuts for each iteration
        LinkedList<LinkedList<FlowEdge>> allMinCut = new LinkedList<>();
        LinkedList<FlowEdge> multiwayCut = new LinkedList<>();
        LinkedList<Integer> terminals = new LinkedList<>();
        LinkedList<Integer> cutWeights = new LinkedList<>();

        StdOut.println("Local Search");

        // Create the flow network from the text file
        readTerminals(in, k, terminals);
        int vertices = in.readInt();
        FlowNetwork flowNetwork = new FlowNetwork(in, vertices);
        in.close();

        labelling = flowNetwork.initialLocalSearchLabel(terminals);
        labelCost = flowNetwork.localSearchLabelCost();
        //flowNetwork.test();
        StdOut.println("The initial min cut cost is: " + labelCost);

        while (true) {

            bestLabelling = labelling;
            bestLabelCost = labelCost;

            for (int i = 0; i < k; i++) {

                newLabelling = computeMinimumCostRelabel(flowNetwork, i, terminals);
                flowNetwork.relabel(newLabelling);
                newLabelCost = flowNetwork.localSearchLabelCost();

                if (newLabelCost < labelCost) {

                   labelling = newLabelling;
                   labelCost = newLabelCost;

                } //end if

            } //end for

            if (labelCost >= bestLabelCost) {

                return bestLabelCost;

            } //end if

        } //end while

        /*initializeInfinityEdges(flowNetwork, vertices);
        computeMinimumCut(k, flowNetwork, terminals, vertices, allMinCut, cutWeights);
        heavyIndex = computeHeaviestCut(k, cutWeights);
        unionCuts(k, heavyIndex, allMinCut, multiwayCut);
        outputMultiwayCut(multiwayCut);*/

    } //end computeMultiwayCut

} //end LocalSearch
