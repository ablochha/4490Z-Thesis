package utility;

import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowNetwork;
import datastructures.flownetwork.FlowVertex;
import library.In;
import library.Out;
import library.StdOut;
import library.StdRandom;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Bloch-Hansen on 2017-02-19.
 */
public class GraphFormatReader {

    private static final String DIMACS1 = "DIMACS1";
    private static final String DIMACS2 = "DIMACS2";
    private static final String DIMACS3 = "DIMACS3";
    private static final String CONCENTRIC = "CONCENTRIC";
    private static final String GEOGRAPHIC = "GEOGRAPHIC";
    private static final String OTHER = "OTHER";

    public FlowNetwork parse(String filename, String outName) {

        In format = new In(filename);

        String text = format.readLine();

        switch (text) {

            case DIMACS1:

                format.close();
                return parseDIMACS(filename,false, false, outName);

            case DIMACS2:

                format.close();
                return parseDIMACS(filename, true, false, outName);

            case DIMACS3:

                format.close();
                return parseDIMACS(filename, true, true, outName);

            case CONCENTRIC:

                format.close();
                return parseConcentric(filename, outName);

            case GEOGRAPHIC:

                format.close();
                return parseGeographic(filename, outName);

            case OTHER:

                format.close();
                return parseOther(filename);

            default:

                format.close();
                throw new IllegalArgumentException("Unrecognized graph format");

        } //end switch

    } //end parse

    private FlowNetwork parseDIMACS(String filename, boolean edgeWeights, boolean genTerminals, String outName) {

        In in = new In(filename);
        FlowNetwork flowNetwork = new FlowNetwork();

        int numVertices = -1;

        in.readLine();
        flowNetwork.setK(in.readInt());

        if (!genTerminals) {

            flowNetwork.setTerminals(readTerminals(in, flowNetwork.getK()));

        } //end if

        in.readLine();

        final Pattern p = Pattern.compile("p\\s+edge\\s+(\\d+)\\s+(\\d+)\\s*");
        final Matcher mp = p.matcher("");

        for (String line = in.readLine(); line != null; line = in.readLine()) {

            mp.reset(line);

            if (mp.matches()) {

                numVertices = Integer.parseInt(mp.group(1));
                break;

            } //end if

        } //end for

        if (numVertices < 0) {

            throw new IllegalArgumentException("Bad header line: " + filename);

        } //end if

        if (genTerminals) {

            flowNetwork.setTerminals(generateTerminals(flowNetwork.getK(), 1, numVertices));

        } //end if

        final Pattern e;

        if (edgeWeights) {

            e = Pattern.compile("e\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s*");

        } //end if

        else {

            e = Pattern.compile("e\\s+(\\d+)\\s+(\\d+)\\s*");

        } //end else

        final Matcher me = e.matcher("");

        for (String line = in.readLine(); line != null; line = in.readLine()) {

            me.reset(line);

            if (me.matches()) {

                int u = Integer.parseInt(me.group(1));
                int v = Integer.parseInt(me.group(2));

                flowNetwork.addVertex(u);
                flowNetwork.addVertex(v);

                if (edgeWeights) {

                    flowNetwork.addEdge(u, v, Integer.parseInt(me.group(3)));

                } //end if

                else {

                    if (flowNetwork.getTerminals().contains(u) | flowNetwork.getTerminals().contains(v)) {

                        flowNetwork.addEdge(u, v, StdRandom.uniform(15, 20));
                        //flowNetwork.addEdge(u, v, 1);

                    } //end if

                    else {

                        flowNetwork.addEdge(u, v, StdRandom.uniform(1, 2));
                        //flowNetwork.addEdge(u, v, 1);

                    } //end else

                } //end else

            } //end if

        } //end for

        in.close();

        if (!edgeWeights) {

            outputGraph(flowNetwork, filename, outName);

        } //end if

        return flowNetwork;

    } //end parseDIMACS

   private FlowNetwork parseOther(String filename) {

        In in = new In(filename);
        FlowNetwork flowNetwork = new FlowNetwork();

        int numVertices;
        int numEdges;

        in.readLine();
        flowNetwork.setK(in.readInt());
        flowNetwork.setTerminals(readTerminals(in, flowNetwork.getK()));

        numVertices = in.readInt();
        numEdges = in.readInt();

        if (numVertices < 0 || numEdges < 0) {

            throw new IllegalArgumentException("Choose a nonnegative number of vertices and edges");

        } //end if

        // Read each edge from the input
        for (int i = 0; i < numEdges; i++) {

            int u = in.readInt();
            int v = in.readInt();

            flowNetwork.addVertex(u);
            flowNetwork.addVertex(v);
            flowNetwork.addEdge(u, v, in.readInt());

        } //end for

        in.close();

        return flowNetwork;

    } //end parseOther

    private FlowNetwork parseConcentric(String filename, String outName) {

        In in = new In(filename);
        FlowNetwork flowNetwork = new FlowNetwork();
        ConnectedComponentSearcher searcher = new ConnectedComponentSearcher();

        String decay;

        int numVertices;
        int initialDensity;

        double terminalDensityMultiplier;
        double initialCapacity1;
        double initialCapacity2;

        in.readLine();
        flowNetwork.setK(in.readInt());
        numVertices = in.readInt();
        initialDensity = in.readInt();
        terminalDensityMultiplier = in.readDouble();
        initialCapacity1 = in.readDouble();
        initialCapacity2 = in.readDouble();
        in.readLine();
        decay = in.readString();
        flowNetwork.setTerminals(generateTerminals(flowNetwork.getK(), 0, numVertices));

        in.close();

        if (numVertices < 0) {

            throw new IllegalArgumentException("Choose a nonnegative number of vertices and edges");

        } //end if

        // Add the vertices
        for (int i = 0; i < numVertices; i++) {

            flowNetwork.addVertex(i);

        } //end for

        // Add edges per vertex
        for (int i = 0; i < numVertices; i++) {

            // Each vertex gets initialDensity edges to start with
            for (int j = 0; j < initialDensity; j++) {

                int end = StdRandom.uniform(0, numVertices - 1);

                // No self edges
                while (end == i) {

                    end = StdRandom.uniform(0, numVertices - 1);

                } //end while

                flowNetwork.addEdge(i, end, 0);

            } //end for

        } //end for

        searcher.connectComponents(flowNetwork);
        searcher.concentricEdges(flowNetwork, terminalDensityMultiplier, decay, CONCENTRIC);
        searcher.concentricEdgeCapacities(flowNetwork, initialCapacity1, initialCapacity2, decay, CONCENTRIC);

        outputGraph(flowNetwork, filename, outName);

        return flowNetwork;

    } //end parseConcentric

    private FlowNetwork parseGeographic(String filename, String outName) {

        In in = new In(filename);
        FlowNetwork flowNetwork = new FlowNetwork();
        ConnectedComponentSearcher searcher = new ConnectedComponentSearcher();

        String decay;

        int numVertices;

        double terminalDensityMultiplier;
        double initialCapacity1;
        double initialCapacity2;
        double xModifier;
        double yModifier;

        in.readLine();
        flowNetwork.setK(in.readInt());
        numVertices = in.readInt();
        terminalDensityMultiplier = in.readDouble();
        xModifier = in.readDouble();
        yModifier = in.readDouble();
        initialCapacity1 = in.readDouble();
        initialCapacity2 = in.readDouble();
        in.readLine();
        decay = in.readString();
        flowNetwork.setTerminals(generateTerminals(flowNetwork.getK(), 0, numVertices));

        in.close();

        if (numVertices < 0) {

            throw new IllegalArgumentException("Choose a nonnegative number of vertices and edges");

        } //end if

        // Add the vertices
        for (int i = 0; i < numVertices; i++) {

            flowNetwork.addVertex(i);

            int x = StdRandom.uniform(0, (int) (xModifier * numVertices - 1));
            int y = StdRandom.uniform(0, (int) (yModifier * numVertices - 1));

            boolean success = flowNetwork.setCoordinates(i, new Point(x, y));

            while (!success) {

                x = StdRandom.uniform(0, (int) (xModifier * numVertices - 1));
                y = StdRandom.uniform(0, (int) (yModifier * numVertices - 1));

                success = flowNetwork.setCoordinates(i, new Point(x, y));

            } //end while

        } //end for

        searcher.concentricEdges(flowNetwork, terminalDensityMultiplier, decay, GEOGRAPHIC);
        searcher.connectComponents(flowNetwork);
        searcher.concentricEdgeCapacities(flowNetwork, initialCapacity1, initialCapacity2, decay, GEOGRAPHIC);

        outputGraph(flowNetwork, filename, outName);

        return flowNetwork;

    } //end parseGeographic

    /**
     * Reads the terminal vertices to be isolated.
     * @param in a text representation of the flow network
     * @param k the number of terminal vertices
     */
    private LinkedList<Integer> readTerminals(In in, int k) {

        LinkedList<Integer> terminals = new LinkedList<>();

        //StdOut.println("k: " + k);

        // Store each of the terminal vertices id's
        for (int i = 0; i < k; i++) {

            terminals.add(in.readInt());
            //StdOut.println("terminal " + (i+1) + ": " + terminals.get(i));

        } //end for

        //StdOut.println();

        return terminals;

    } //end readTerminals

    private LinkedList<Integer> generateTerminals(int k, int start, int end) {

        LinkedList<Integer> terminals = new LinkedList<>();

        for (int i = 0; i < k; i++) {

            int terminal = StdRandom.uniform(start, end - 1);

            while (terminals.contains(terminal)) {

                terminal = StdRandom.uniform(start, end - 1);

            } //end while

            terminals.add(terminal);

        } //end for

        return terminals;

    } //end generateTerminals

    private void outputGraph(FlowNetwork flowNetwork, String filename, String outName){

        Out out = new Out(filename.substring(0, filename.length() - 4) + outName);

        out.println("DIMACS2");
        out.print(flowNetwork.getK());

        ListIterator<Integer> itTerminals = flowNetwork.getTerminals().listIterator();

        while (itTerminals.hasNext()) {

            out.print(" " + itTerminals.next());

        } //end while

        out.println();
        out.println("p edge " + flowNetwork.getNumVertices() + " " + flowNetwork.getNumEdges());

        ListIterator<FlowEdge> itEdges = flowNetwork.getEdges().listIterator();

        while (itEdges.hasNext()) {

            FlowEdge edge = itEdges.next();

            out.println("e " + edge.getStartVertex().id() + " " + edge.getEndVertex().id() + " " + edge.getCapacity());

        } //end while

        out.close();

    } //end outputGraph

} //end GraphFormatReader
