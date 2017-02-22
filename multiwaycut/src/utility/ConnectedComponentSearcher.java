package utility;

import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowNetwork;
import datastructures.flownetwork.FlowVertex;
import library.StdOut;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-02-20.
 */
public class ConnectedComponentSearcher {

    public FlowNetwork getLargestConnectedComponent(FlowNetwork flowNetwork) {

        LinkedList<FlowVertex> bfs = new LinkedList<>();
        Map<Integer, Boolean> marked = new LinkedHashMap<>();
        Map<Integer, FlowVertex> vertices = flowNetwork.getVertices();

        LinkedList<FlowEdge> largest = new LinkedList<>();

        int largestComponent = 0;
        int currentComponent = 0;

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            marked.put(entry.getValue().id(), false);

        } //end for

        for (Map.Entry<Integer, FlowVertex> entry : vertices.entrySet()) {

            if (!marked.get(entry.getValue().id()).booleanValue()) {

                LinkedList<FlowEdge> edges = new LinkedList<>();

                bfs.add(entry.getValue());
                marked.put(entry.getValue().id(), true);
                currentComponent++;

                while (!bfs.isEmpty()) {

                    FlowVertex start = bfs.removeFirst();

                    for (FlowEdge edge : start.getAllEdges()) {

                        FlowVertex end = edge.getEndVertex();

                        if (!edges.contains(edge)) {

                            edges.add(edge);

                        } //end if

                        if (!marked.get(end.id()).booleanValue()) {

                            marked.put(end.id(), true);
                            bfs.add(end);
                            currentComponent++;

                        } //end if

                    } //end for

                    for (FlowEdge edge : start.getAllResEdges()) {

                        FlowVertex end = edge.getStartVertex();

                        if (!edges.contains(edge)) {

                            edges.add(edge);

                        } //end if

                        if (!marked.get(end.id()).booleanValue()) {

                            marked.put(end.id(), true);
                            bfs.add(end);
                            currentComponent++;

                        } //end if

                    } //end for

                } //end while

                if (currentComponent > largestComponent) {

                    largestComponent = currentComponent;
                    largest = edges;

                } //end if

                StdOut.println("Largest: " + largestComponent + ", Current: " + currentComponent);
                currentComponent = 0;

            } //end if

        } //end for

        if (largestComponent == flowNetwork.getNumVertices()) {

            return flowNetwork;

        } //end if

        else {

            return new FlowNetwork(largest, flowNetwork.getTerminals(), flowNetwork.getK());

        } //end else

    } //end getLargestConnectedComponent

} //end ConnectedComponentSearcher
