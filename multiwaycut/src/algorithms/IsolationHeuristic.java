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
     * Adds an extra edge with infinity capacity from every vertex to the sink.
     * @param flowNetwork the flow network
     */
    private void initializeInfinityEdges(FlowNetwork flowNetwork) {

        LinkedList<Integer> t = flowNetwork.getTerminals();

        int k = flowNetwork.getK();
        int n = flowNetwork.getNumVertices();

        // Add an edge from each vertex to the sink with an infinity edge
        for (int i = 0; i < k; i++) {

            flowNetwork.addEdge(t.get(i), n - 1, Integer.MAX_VALUE);

        } //end for

    } //end initializeInfinityEdges

    /**
     * Computes a minimum cut from each terminal vertex to the sink.
     * @param flowNetwork the flow network
     * @param allMinCut a list of all the minimum cuts
     * @param cutWeights a list of weights for the minimum cuts
     */
    private void computeMinimumCut(FlowNetwork flowNetwork,
                                   LinkedList<LinkedList<FlowEdge>> allMinCut,
                                   LinkedList<Integer> cutWeights) {

        LinkedList<Integer> t = flowNetwork.getTerminals();

        int k = flowNetwork.getK();
        int n = flowNetwork.getNumVertices();

        // Compute a minimum cut to isolate each of the k terminal vertices
        for (int i = 0; i < k; i++) {

            int sum = 0;

            // Remove the infinity edge for the current source vertex
            flowNetwork.removeEdge(t.get(i), n - 1);

            // Add back the infinity edge to the previous terminal vertex
            if (i > 0) {

                flowNetwork.addEdge(t.get(i - 1),n - 1, Integer.MAX_VALUE);

            } //end if

            // Compute the minimum cut for the specified terminal vertex, store the edges
            LinkedList<FlowEdge> minCut = flowNetwork.goldbergTarjan(t.get(i),n - 1);
            allMinCut.add(minCut);

            ListIterator<FlowEdge> it = minCut.listIterator();

            // Compute the weight of the cut
            while (it.hasNext()) {

                FlowEdge edge = it.next();
                sum += edge.getCapacity();

                //StdOut.println("Edge: " + edge.edgeToString());

            } //end while

            //StdOut.println("Min cut weight: " + sum);
            //StdOut.println();
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
    private void unionCuts(int k,
                           int heavyIndex,
                           LinkedList<LinkedList<FlowEdge>> allMinCut,
                           LinkedList<FlowEdge> multiwayCut) {

        // Compute the union of all cuts except the heaviest cut
        for (int i = 0; i < k; i++) {

            if (i != heavyIndex) {

                ListIterator<FlowEdge> it = allMinCut.get(i).listIterator();

                while (it.hasNext()) {

                    FlowEdge edge = it.next();

                    if (!multiwayCut.contains(edge)){

                        multiwayCut.add(edge);

                    } //end if

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

        //StdOut.println("Multiway Cut: ");

        for (int i = 0; i < multiwayCut.size(); i++) {

            //StdOut.println(multiwayCut.get(i).edgeToString());
            multiwayCutWeight += multiwayCut.get(i).getCapacity();

        } //end for

        StdOut.println("The weight of the multiway cut: " + multiwayCutWeight);
        return multiwayCutWeight;

    } //end outputMultiwayCut

    /**
     * Computes a minimum multiway cut.
     */
    public int computeMultiwayCut(FlowNetwork flowNetwork) {

        // The minimum cuts for each iteration
        LinkedList<LinkedList<FlowEdge>> allMinCut = new LinkedList<>();
        LinkedList<FlowEdge> multiwayCut = new LinkedList<>();
        LinkedList<Integer> cutWeights = new LinkedList<>();

        int heavyIndex;

        StdOut.println("Isolation Heuristic");

        flowNetwork.addVertex(flowNetwork.getNumVertices());
        initializeInfinityEdges(flowNetwork);
        computeMinimumCut(flowNetwork, allMinCut, cutWeights);
        heavyIndex = computeHeaviestCut(flowNetwork.getK(), cutWeights);
        unionCuts(flowNetwork.getK(), heavyIndex, allMinCut, multiwayCut);
        return outputMultiwayCut(multiwayCut);

    } //end computeMultiwayCut

} //end IsolationHeuristic
