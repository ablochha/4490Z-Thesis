package algorithms;

import datastructures.flownetwork.FlowNetwork;
import java.util.Map;

/**
 * Created by Bloch-Hansen on 2017-05-03.
 */
public interface MultiwayCutStrategy {

    default double computeMultiwayCut(FlowNetwork flowNetwork) {

        return 0.0;

    } //end computeMultiwayCut

    default void setEpsilon(double epsilon) {

    } //end setEpsilon

    default double getEpsilon() {

        return 0.0;

    } //end getEpsilon

    default void setSolver(MultiwayCutStrategy solver) {

    } //end setSolver

    default void setInitialLabeller(String initialLabeller) {

    } //end setInitialLabeller

    default void setIsolationHeuristic(MultiwayCutStrategy ih) {

    } //end setIsolationHeuristic

    default double getThreshold() {

        return 0.0;

    } //end getRadius

    default double getCalCost() {

        return 0.0;

    } //end getRadius

    default Map<Integer, Integer> getIsolationHeuristicLabelling() {

        return null;

    } //end getIsolationLabelling

    default Map<Integer, double[]> getVertexLabels() {

        return null;

    } //end getVertexLabels

    default boolean isFractional() {

        return false;

    } //end isFractional

    long getTime();

} //end MultiwayCutStrategy
