package utility;

import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowVertex;

import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-05-21.
 */
public class MinCutReachability implements Reachability {

    public boolean isReachable(FlowEdge edge, FlowVertex start, FlowVertex end, Map<Integer, Boolean> marked) {

        return (edge.getCapacity() - edge.getFlow() > 0 && !marked.get(end.id())) || (edge.getCapacity() - edge.getFlow() == 0 && !marked.get(end.id()) && edge.getCapacity() > 0 && edge.getFrom() != start.id());

    } //end isReachable

} //end MinCutReachability
