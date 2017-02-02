package datastructures.flownetwork;

import library.StdOut;

/**
 * FlowEdge.java
 * 
 * Course: CS4445
 * Author: Andrew Bloch-Hansen
 * 
 * This FlowEdge is based on the implementation in the textbook 'Algorithms, 4th Edition'
 * by Robert Sedgewick and Kevin Wayne. A FlowEdge is a directed, weighted edge. Each edge
 * has a 'flow' and a 'capacity' associated with it. The flow cannot exceed the capacity, 
 * and the incoming flow must equal the outgoing flow for all nodes except the first and last.
 *
 */
public class FlowEdge {

	private FlowVertex startVertex;
	private FlowVertex endVertex;
	private int capacity;
	private int flow;
	
	/**
	 * Declares a new FlowEdge with 0 flow

	 */
	public FlowEdge(FlowVertex startVertex, FlowVertex endVertex, int capacity) {
		
		// Make sure positive integers are used to identify vertices
		if (startVertex.id() < 0) {
			
			throw new IndexOutOfBoundsException("Choose positive integers for vertices");
			
		} //end if
		
		// Make sure positive integers are used to identify vertices
		if (endVertex.id() < 0) {
			
			throw new IndexOutOfBoundsException("Choose positive integers for vertices");
			
		} //end if
		
		// Make sure the edge capacity is positive
		if (!(capacity >= 0)) {
			
			throw new IllegalArgumentException("Choose positive number for edge capacity");
			
		} //end if
		
		// Initialize the FlowEdge with 0 flow
		this.startVertex = startVertex;
		this.endVertex = endVertex;
		this.capacity = capacity;
		this.flow = 0;
		
	} //end FlowEdge
	
	/**
	 * Returns the first vertex of an edge
	 * @return the first vertex
	 */
	public FlowVertex getStartVertex() {
		
		return startVertex;
		
	} //end from
	
	/**
	 * Returns the second vertex of an edge
	 * @return the second vertex
	 */
	public FlowVertex getEndVertex() {
		
		return endVertex;
		
	} //end to
	
	/**
	 * Returns the capacity of an edge
	 * @return the capacity
	 */
	public int getCapacity() {
		
		return capacity;
		
	} //end capacity
	
	/**
	 * Returns the flow of an edge
	 * @return the flow
	 */
	public int getFlow() {
		
		return flow;
		
	} //end flow

	public boolean setFlow(int flow) {

		if (flow > this.capacity || flow < 0) {

			return false;

		} //end if

		else {

			this.flow = flow;
			return true;

		} //end else

	} //end setFlow

	public FlowVertex pushFlowForward() {

		int previousExcess = endVertex.getExcess();
		int deltaFlow = 0;

		// The start vertex is the source
		if (this.getStartVertex().getExcess() == -1) {

			deltaFlow = capacity - flow;
			flow = capacity;

		} //end if

		// The start vertex isn't the source
		else {

			// Saturating push
			if (capacity - flow <= startVertex.getExcess()) {

				deltaFlow = capacity - flow;

			} //end if

			// Non-saturating push
			else {

				deltaFlow = startVertex.getExcess();
				startVertex.setPreviousEdge();

			} //end else

			flow += deltaFlow;
			startVertex.changeExcess(-deltaFlow);

		} //end else

		endVertex.changeExcess(deltaFlow);
		StdOut.println("Pushing " + deltaFlow + " Flow Forward: " + this.edgeToString());
		// Return a new active vertex
		if (previousExcess == 0 && deltaFlow > 0) {

			endVertex.setPredecessor(startVertex.id());
			endVertex.setDead(false);
			return endVertex;

		} //end if

		else {

			return null;

		} //end else

	} //end pushFlowForward

	public FlowVertex pushResFlowForward() {

		int previousExcess = startVertex.getExcess();
		int deltaFlow = 0;

		// The start vertex is the source
		if (this.getEndVertex().getExcess() == -1) {

			deltaFlow = capacity - flow;
			flow = capacity;

		} //end if

		// The start vertex isn't the source
		else {

			// Saturating push
			if (capacity - flow <= endVertex.getExcess()) {

				deltaFlow = capacity - flow;

			} //end if

			// Non-saturating push
			else {

				deltaFlow = endVertex.getExcess();
				endVertex.setPreviousEdge();

			} //end else

			flow += deltaFlow;
			endVertex.changeExcess(-deltaFlow);

		} //end else

		startVertex.changeExcess(deltaFlow);
		StdOut.println("Pushing " + deltaFlow + " Res Flow Forward: " + this.edgeToString());
		// Return a new active vertex
		if (previousExcess == 0 && deltaFlow > 0) {

			startVertex.setPredecessor(endVertex.id());
			startVertex.setDead(false);
			return startVertex;

		} //end if

		else {

			return null;

		} //end else

	} //end pushResFlowForward

	public FlowVertex pushFlowBackward() {

		int previousExcess = startVertex.getExcess();
		int deltaFlow = 0;

		// Saturating push
		if (flow <= endVertex.getExcess()) {

			deltaFlow = flow;

		} //end if

		// Non saturating-push
		else {

			deltaFlow = endVertex.getExcess();
			startVertex.setPreviousEdge();

		} //end else

		flow -= deltaFlow;

		// Adjust the excess of the start vertex
		if (startVertex.getExcess() >= 0) {

			startVertex.changeExcess(deltaFlow);

		} //end if

		endVertex.changeExcess(-deltaFlow);
		StdOut.println("Pushing " + deltaFlow + " Flow Backward: " + this.edgeToString());
		// Return a new active vertex
		if (previousExcess == 0 && deltaFlow > 0) {

			startVertex.setPredecessor(endVertex.id());
			startVertex.setDead(false);
			return startVertex;

		} //end if

		else {

			return null;

		} //end else

	} //end pushFlowBackward

	public FlowVertex pushResFlowBackward() {

		int previousExcess = endVertex.getExcess();
		int deltaFlow = 0;

		// Saturating push
		if (flow <= startVertex.getExcess()) {

			deltaFlow = flow;

		} //end if

		// Non saturating-push
		else {

			deltaFlow = startVertex.getExcess();
			endVertex.setPreviousEdge();

		} //end else

		flow -= deltaFlow;

		// Adjust the excess of the start vertex
		if (endVertex.getExcess() >= 0) {

			endVertex.changeExcess(deltaFlow);

		} //end if

		startVertex.changeExcess(-deltaFlow);
		StdOut.println("Pushing " + deltaFlow + " Res Flow Backward: " + this.edgeToString());
		// Return a new active vertex
		if (previousExcess == 0 && deltaFlow > 0) {

			endVertex.setPredecessor(startVertex.id());
			endVertex.setDead(false);
			return endVertex;

		} //end if

		else {

			return null;

		} //end else

	} //end pushResFlowBackward

	public String edgeToString() {

		return "(" + startVertex.id() + "," + endVertex.id() + ",c:" + capacity + ",f:" + flow + ")";

	} //end edgeToString
	
} //end FlowEdge
