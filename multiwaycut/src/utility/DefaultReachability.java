package utility;

import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowVertex;

import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-05-21.
 */
public class DefaultReachability implements Reachability {

    public boolean isReachable(FlowEdge edge, FlowVertex start, FlowVertex end, Map<Integer, Boolean> marked) {

        return !marked.get(end.id());

    } //end isReachable

} //end DefaultReachability
