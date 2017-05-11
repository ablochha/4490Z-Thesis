package algorithms;

import datastructures.flownetwork.FlowNetwork;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-05-03.
 */
public interface MultiwayCutStrategy {

    default int computeMultiwayCut(FlowNetwork flowNetwork) {

        return 0;

    } //end computeMultiwayCut

    default int computeMultiwayCut(FlowNetwork flowNetwork, double[] edgesRelaxed, Map<Integer, double[]> verticesRelaxed) {

        return 0;

    } //end computeMultiwayCut

    default void setEpsilon(double epsilon) {

    } //end setEpsilon

    default void setSolver(MultiwayCutStrategy solver) {

    } //end setSolver

    long getTime();

} //end MultiwayCutStrategy
