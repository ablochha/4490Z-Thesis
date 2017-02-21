package utility;

import datastructures.flownetwork.FlowNetwork;
import library.In;
import library.StdOut;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
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

            case "DIMACS":

                format.close();
                return parseDIMACS(filename);

            case "OTHER":

                format.close();
                return parseOther(filename);

            default:

                format.close();
                return null;

        } //end switch

    } //end parse

    private FlowNetwork parseDIMACS(String filename) {

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

        final Pattern e = Pattern.compile("e\\s+(\\d+)\\s+(\\d+)\\s*");
        final Matcher me = e.matcher("");

        for (String line = in.readLine(); line != null; line = in.readLine()) {

            me.reset(line);

            if (me.matches()) {

                int u = Integer.parseInt(me.group(1));
                int v = Integer.parseInt(me.group(2));

                flowNetwork.addVertex(u);
                flowNetwork.addVertex(v);
                flowNetwork.addEdge(u, v, 1);

            } //end if

        } //end for

        in.close();
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
