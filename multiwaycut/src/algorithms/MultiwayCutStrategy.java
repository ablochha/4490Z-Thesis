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

    /*double computeMultiwayCut(FlowNetwork flowNetwork);

    void setEpsilon(double epsilon);

    double getEpsilon();

    void setSolver(MultiwayCutStrategy solver);

    void setInitialLabeller(String initialLabeller);

    void setIsolationHeuristic(MultiwayCutStrategy ih);

    double getThreshold();

    double getCalCost();

    Map<Integer, Integer> getIsolationHeuristicLabelling();

    Map<Integer, double[]> getVertexLabels();

    boolean isFractional();*/

} //end MultiwayCutStrategy
