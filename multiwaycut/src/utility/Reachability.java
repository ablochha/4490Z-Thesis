package utility;

import datastructures.flownetwork.FlowEdge;
import datastructures.flownetwork.FlowVertex;

import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-05-21.
 */
public interface Reachability {

    public boolean isReachable(FlowEdge edge, FlowVertex start, FlowVertex end, Map<Integer, Boolean> marked);

} //end Reachability
