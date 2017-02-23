package utility;

import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowNetwork;
import library.In;
import library.Out;
import library.StdOut;
import library.StdRandom;

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

    public FlowNetwork parse(String filename) {

        In format = new In(filename);

        String text = format.readLine();

        switch(text) {

            case "DIMACS1":

                format.close();
                return parseDIMACS(filename, false);

            case "DIMACS2":

                format.close();
                return parseDIMACS(filename, true);

            case "OTHER":

                format.close();
                return parseOther(filename);

            default:

                format.close();
                throw new IllegalArgumentException("Unrecognized graph format");

        } //end switch

    } //end parse

    private FlowNetwork parseDIMACS(String filename, boolean edgeWeights) {

        In in = new In(filename);
        FlowNetwork flowNetwork = new FlowNetwork();

        int numVertices = -1;

        in.readLine();
        flowNetwork.setK(in.readInt());
        flowNetwork.setTerminals(readTerminals(in, flowNetwork.getK()));
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

                        flowNetwork.addEdge(u, v, StdRandom.uniform(500, 1000));
                        //flowNetwork.addEdge(u, v, 1);

                    } //end if

                    else {

                        flowNetwork.addEdge(u, v, StdRandom.uniform(1, 5));
                        //flowNetwork.addEdge(u, v, 1);

                    } //end else

                } //end else

            } //end if

        } //end for

        in.close();

        if (!edgeWeights) {

            Out out = new Out(filename.substring(0, filename.length() - 4) + "_edges.txt");

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

} //end GraphFormatReader
