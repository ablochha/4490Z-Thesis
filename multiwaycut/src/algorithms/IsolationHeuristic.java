package algorithms;

import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowNetwork;
import library.In;
import library.StdOut;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * IsolationHeuristic.java
 *
 * Course: CS4490Z
 * Author: Andrew Bloch-Hansen
 *
 * The IsolationHeuristic approximation algorithm performs a series
 * of minimum cuts to isolate specific vertices one at a time to solve
 * the multiway cut problem. It computes k-1 cuts, discarding the heaviest cut.
 */
public class IsolationHeuristic {

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

    /**
     * Adds an extra edge with infinity capacity from every vertex to the sink.
     * @param flowNetwork the flow network
     * @param vertices the number of vertices in the flow network
     */
    private void initializeInfinityEdges(FlowNetwork flowNetwork, int vertices, LinkedList<Integer> terminals) {

        // Add an edge from each vertex to the sink with an infinity edge
        for (int i = 0; i < terminals.size(); i++) {

            flowNetwork.addEdge(terminals.get(i), vertices, Integer.MAX_VALUE);

        } //end for

    } //end initializeInfinityEdges

    /**
     * Computes a minimum cut from each terminal vertex to the sink.
     * @param k the number of terminal vertices
     * @param flowNetwork the flow network
     * @param terminals a list of the terminal vertices
     * @param vertices the number of vertices in the flow network
     * @param allMinCut a list of all the minimum cuts
     * @param cutWeights a list of weights for the minimum cuts
     */
    private void computeMinimumCut(int k, FlowNetwork flowNetwork, LinkedList<Integer> terminals, int vertices,
                                   LinkedList<LinkedList<FlowEdge>> allMinCut, LinkedList<Integer> cutWeights) {

        // Compute a minimum cut to isolate each of the k terminal vertices
        for (int i = 0; i < k; i++) {

            int sum = 0;

            // Remove the infinity edge for the current source vertex
            flowNetwork.removeEdge(terminals.get(i), vertices);

            // Add back the infinity edge to the previous terminal vertex
            if (i > 0) {

                flowNetwork.addEdge(terminals.get(i - 1), vertices, Integer.MAX_VALUE);

            } //end if

            // Compute the minimum cut for the specified terminal vertex, store the edges
            LinkedList<FlowEdge> minCut = flowNetwork.goldbergTarjan(terminals.get(i), vertices);
            allMinCut.add(minCut);

            ListIterator<FlowEdge> it = minCut.listIterator();

            // Compute the weight of the cut
            while (it.hasNext()) {

                FlowEdge edge = it.next();
                sum += edge.getCapacity();
                StdOut.println("Edge: " + edge.edgeToString());

            } //end while

            StdOut.println("Min cut weight: " + sum);
            StdOut.println();
            cutWeights.add(sum);

        } //end for

    } //end computeMinimumCut

    /**
     * Computes a minimum cut from each terminal vertex to the sink.
     * @param k the number of terminal vertices
     * @param cutWeights a list of weights for the minimum cuts
     * @return the index of the heaviest cut
     */
    private int computeHeaviestCut(int k, LinkedList<Integer> cutWeights) {

        // The heaviest cut
        int heavyCut = 0;
        int heavyIndex = 0;

        // Compute the heaviest cut
        for (int i = 0; i < k; i++) {

            if (cutWeights.get(i) > heavyCut) {

                heavyCut = cutWeights.get(i);
                heavyIndex = i;

            } //end if

        } //end for

        return heavyIndex;

    } //end computeHeaviestCut

    /**
     * Computes a minimum cut from each terminal vertex to the sink.
     * @param k the number of terminal vertices
     * @param heavyIndex the index of the heaviest cut
     * @param allMinCut a list of all the minimum cuts
     * @param multiwayCut a list of edges in the multiway cut
     */
    private void unionCuts(int k, int heavyIndex, LinkedList<LinkedList<FlowEdge>> allMinCut, LinkedList<FlowEdge> multiwayCut) {

        // Compute the union of all cuts except the heaviest cut
        for (int i = 0; i < k; i++) {

            if (i != heavyIndex) {

                ListIterator<FlowEdge> it = allMinCut.get(i).listIterator();

                while (it.hasNext()) {

                    FlowEdge edge = it.next();
                    multiwayCut.add(edge);

                } //end while

            } //end if

        } //end for

    } //end unionCuts

    /**
     * Computes a minimum cut from each terminal vertex to the sink.
     * @param multiwayCut a list of edges in the multiway cut
     */
    private int outputMultiwayCut(LinkedList<FlowEdge> multiwayCut) {

        int multiwayCutWeight = 0;

        StdOut.println("Multiway Cut: ");

        for (int i = 0; i < multiwayCut.size(); i++) {

            StdOut.println(multiwayCut.get(i).edgeToString());
            multiwayCutWeight += multiwayCut.get(i).getCapacity();

        } //end for

        StdOut.println("The weight of the multiway cut: " + multiwayCutWeight);
        return multiwayCutWeight;

    } //end outputMultiwayCut

    /**
     * Computes a minimum multiway cut.
     * @param in a text representation of the flow network
     */
    public int computeMultiwayCut(In in) {

        // The number of terminal vertices, and the heaviest cut
        int k = in.readInt();
        int heavyIndex;

        // The minimum cuts for each iteration
        LinkedList<LinkedList<FlowEdge>> allMinCut = new LinkedList<>();
        LinkedList<FlowEdge> multiwayCut = new LinkedList<>();
        LinkedList<Integer> terminals = new LinkedList<>();
        LinkedList<Integer> cutWeights = new LinkedList<>();

        StdOut.println("Isolation Heuristic");

        // Create the flow network from the text file
        readTerminals(in, k, terminals);
        int vertices = in.readInt();
        FlowNetwork flowNetwork = new FlowNetwork(in, vertices);
        flowNetwork.addVertex(vertices);
        in.close();

        initializeInfinityEdges(flowNetwork, vertices, terminals);
        computeMinimumCut(k, flowNetwork, terminals, vertices, allMinCut, cutWeights);
        heavyIndex = computeHeaviestCut(k, cutWeights);
        unionCuts(k, heavyIndex, allMinCut, multiwayCut);
        return outputMultiwayCut(multiwayCut);

    } //end computeMultiwayCut

} //end IsolationHeuristic
