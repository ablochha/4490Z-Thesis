package datastructures.flownetwork;

import library.StdOut;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Bloch-Hansen on 2017-01-23.
 */
public class FlowVertex {

    private int id;
    private int excess;
    private int label;
    private int localSearchLabel;
    private int iteratorAugPath;
    private int predecessor;
    private LinkedList<FlowEdge> adjacencyList;
    private LinkedList<FlowEdge> resAdjacencyList;
    private FlowEdge backtrackEdge;
    private boolean deadEnd;
    private boolean backtrack;
    private boolean increasedLabel;

    public FlowVertex(int id) {

        this.id = id;
        this.excess = 0;
        this.localSearchLabel = -1;
        this.label = 0;
        this.iteratorAugPath = 0;
        this.predecessor = -10;
        this.deadEnd = false;
        this.backtrack = false;
        adjacencyList = new LinkedList<FlowEdge>();
        resAdjacencyList = new LinkedList<FlowEdge>();

    } //end Vertex

    public boolean addEdge(FlowVertex endVertex, int capacity) {

        if (!containsEdge(endVertex)) {

            adjacencyList.add(new FlowEdge(this, endVertex, capacity));
            return true;

        } //end if

        else {

            return false;

        } //end else

    } //end addEdge

    public boolean addResEdge(FlowVertex startVertex) {

        if (startVertex.containsEdge(this)) {

            resAdjacencyList.add(startVertex.getEdge(this));
            return true;

        } //end if

        else {

            return false;

        } //end else

    } //end addResEdge

    public boolean removeEdge(FlowVertex endVertex) {

        if (containsEdge(endVertex)) {

            FlowEdge oldEdge = getEdge(endVertex);

            if (oldEdge != null) {

                adjacencyList.remove(oldEdge);
                return true;

            } //end if

            else {

                return false;

            } //end else

        } //end if

        else {

            return false;

        } //end else

    } //end removeEdge

    public boolean removeResEdge(FlowVertex startVertex) {

        if (this.containsResEdge(startVertex)) {

            FlowEdge oldEdge = this.getResEdge(startVertex);

            if (oldEdge != null) {
                StdOut.println("Removed Res Id: " + this.id() + ", Id: " + startVertex.id());
                resAdjacencyList.remove(oldEdge);
                return true;

            } //end if

            else {

                return false;

            } //end else

        } //end if

        else {

            return false;

        } //end else

    } //end removeResEdge

    public boolean removeAllEdges() {

        ListIterator<FlowEdge> it = adjacencyList.listIterator();
        boolean success = true;

        while (it.hasNext()) {

            success = success && it.next().getEndVertex().removeResEdge(this);

        } //end while

        return success;

    } //end removeAllEdges

    public boolean removeAllResEdges() {

        ListIterator<FlowEdge> it = resAdjacencyList.listIterator();
        boolean success = true;

        while (it.hasNext()) {

            success = success && it.next().getStartVertex().removeEdge(this);

        } //end while

        return success;

    } //end removeAllResEdges

    public boolean containsEdge(int vertexId) {

        ListIterator<FlowEdge> it = adjacencyList.listIterator();

        while (it.hasNext()) {

            if (it.next().getEndVertex().id() == vertexId) {

                return true;

            } //end if

        } //end while

        return false;

    } //end containsEdge

    public boolean containsEdge(FlowVertex endVertex) {

        ListIterator<FlowEdge> it = adjacencyList.listIterator();

        while (it.hasNext()) {

            if (it.next().getEndVertex() == endVertex) {

                return true;

            } //end if

        } //end while

        return false;

    } //end containsEdge

    public boolean containsResEdge(FlowVertex startVertex) {

        ListIterator<FlowEdge> it = resAdjacencyList.listIterator();

        while (it.hasNext()) {

            if (it.next().getStartVertex() == startVertex) {

                return true;

            } //end if

        } //end while

        return false;

    } //end containsResEdge

    public FlowEdge getEdge(FlowVertex endVertex) {

        ListIterator<FlowEdge> it = adjacencyList.listIterator();

        while (it.hasNext()) {

            FlowEdge nextEdge = it.next();

            if (nextEdge.getEndVertex() == endVertex) {

                return nextEdge;

            } //end if

        } //end while

        return null;

    } //end getEdge

    public FlowEdge getResEdge(FlowVertex startVertex) {

        ListIterator<FlowEdge> it = resAdjacencyList.listIterator();

        while (it.hasNext()) {

            FlowEdge nextEdge = it.next();

            if (nextEdge.getStartVertex() == startVertex) {

                return nextEdge;

            } //end if

        } //end while

        return null;

    } //end getResEdge

    public LinkedList<FlowEdge> getAllEdges() {

        return adjacencyList;

    } //end getAllEdges

    public LinkedList<FlowEdge> getAllResEdges() {

        return resAdjacencyList;

    } //end getAllResEdges

    public void clearResAdjacencyList() {

        resAdjacencyList = new LinkedList<FlowEdge>();

    } //end clearResAdjacencyList

    public void addEdgesToResGraph() {

        ListIterator<FlowEdge> it = adjacencyList.listIterator();

        while (it.hasNext()) {

            FlowEdge nextEdge = it.next();
            nextEdge.getEndVertex().addResEdge(nextEdge);

        } //end while

    } //end addEdgesToResGraph

    public void addResEdge(FlowEdge resEdge) {

        resAdjacencyList.add(resEdge);

    } //end addResEdge

    public int id() {

        return id;

    } //end id

    public boolean resetFlow() {

        ListIterator<FlowEdge> it = adjacencyList.listIterator();

        while (it.hasNext()) {

            FlowEdge nextEdge = it.next();

            if (!nextEdge.setFlow(0)) {

                return false;

            } //end if

        } //end while

        return true;

    } //end resetFlow

    public int getOutFlow() {

        ListIterator<FlowEdge> it = adjacencyList.listIterator();
        int outFlow = 0;

        while (it.hasNext()) {

            FlowEdge nextEdge = it.next();
            outFlow += nextEdge.getFlow();

        } //end while

        return outFlow;

    } //end getOutFlow

    public int getInFlow() {

        ListIterator<FlowEdge> it = resAdjacencyList.listIterator();
        int inFlow = 0;

        while (it.hasNext()) {

            FlowEdge nextEdge = it.next();
            inFlow += nextEdge.getFlow();

        } //end while

        return inFlow;

    } //end getInFlow

    public void setLocalSearchLabel(int label) {

        this.localSearchLabel = label;

    } //end setLocalSearchLabel

    public int getLocalSearchLabel() {

        return this.localSearchLabel;

    } //getLocalSearchLabel

    public void setLabel(int label) {

        this.label = label;

    } //end setLabel

    public int getLabel() {

        return this.label;

    } //getLabel

    public void resetLabel() {

        this.setLabel(0);

    } //end resetLabel

    public void setExcess(int excess) {

        this.excess = excess;

    } //end setExcess

    public int getExcess() {

        return this.excess;

    } //end getExcess

    public void changeExcess(int deltaExcess) {

        this.excess += deltaExcess;
        this.increasedLabel = false;

    } //end changeExcess

    public void resetExcess() {

        this.setExcess(0);

    } //end resetExcess

    public void setPredecessor(int predecessor) {

        this.predecessor = predecessor;

    } //end setPredecessor

    public int getPredecessor() {

        return this.predecessor;

    } //end getPredecessor

    public void resetPredecessor() {

        this.predecessor = -10;

    } //end resetPredecessor

    public void setBacktrackEdge(FlowEdge backtrackEdge) {

        this.backtrackEdge = backtrackEdge;

    } //end setBacktrackEdge

    public FlowEdge getBacktrackEdge() {

        return this.backtrackEdge;

    } //end getBacktrackEdge

    public void resetBacktrackEdge() {

        this.backtrackEdge = null;

    } //end resetBacktrackEdge

    public void resetIncreasedLabel() {

        this.increasedLabel = false;

    } //end resetIncreasedLabel

    public boolean labelIncreased() {

        return increasedLabel;

    } //end labelIncreased

    public boolean isDead() {

        return this.deadEnd;

    } //end isDead

    public void setDead(boolean isDead) {

        this.deadEnd = isDead;

    } //end setDead

    public boolean isBacktrack() {

        return this.backtrack;

    } //end isBacktrack

    public void setBacktrack(boolean backtrack) {

        this.backtrack = backtrack;

    } //end setBacktrack

    public void resetEdge() {

        this.iteratorAugPath = 0;

    } //end resetEdge

    public void relabelVertex() {

        int newLabel = Integer.MAX_VALUE;
        ListIterator<FlowEdge> it = adjacencyList.listIterator();

        // Check regular neighbors
        while (it.hasNext()) {

            FlowEdge nextEdge = it.next();

            // Residual capacity, can still push flow forwards
            if (nextEdge.getEndVertex().getLabel() + 1 < newLabel && nextEdge.getCapacity() - nextEdge.getFlow() > 0) {

                newLabel = nextEdge.getEndVertex().getLabel() + 1;

            } //end if

            // Flow that can be pushed backwards
            if (nextEdge.getEndVertex().getLabel() + 1 < newLabel && nextEdge.getFlow() > 0) {

                newLabel = nextEdge.getEndVertex().getLabel() + 1;

            } //end if

        } //end while

        it = resAdjacencyList.listIterator();

        // Check residual neighbors
        while (it.hasNext()) {

            FlowEdge nextEdge = it.next();

            // Residual capacity, can still push flow forwards
            if (nextEdge.getStartVertex().getLabel() + 1 < newLabel && nextEdge.getCapacity() - nextEdge.getFlow() > 0) {

                newLabel = nextEdge.getStartVertex().getLabel() + 1;

            } //end if

            // Flow that can be pushed backwards
            if (nextEdge.getStartVertex().getLabel() + 1 < newLabel && nextEdge.getFlow() > 0) {

                newLabel = nextEdge.getStartVertex().getLabel() + 1;

            } //end if

            this.label = newLabel;
            this.increasedLabel = true;

        } //end while

    } //end relabelVertex

    public FlowVertex pushRelabel() {

        FlowVertex newActiveVertex;
        FlowEdge pushEdge = this.getNextEdge();

        // Skip the backtrack unless its a dead end
        if (pushEdge != null && this == pushEdge.getStartVertex() && this.getPredecessor() == pushEdge.getEndVertex().id() && !this.isDead()) {

            this.setBacktrackEdge(pushEdge);
            pushEdge = this.getNextEdge();

        } //end if

        // Skip the backtrack unless its a dead end
        else if (pushEdge != null && this == pushEdge.getEndVertex() && this.getPredecessor() == pushEdge.getStartVertex().id() && !this.isDead()) {

            this.setBacktrackEdge(pushEdge);
            pushEdge = this.getNextEdge();

        } //end else if

        // There is not another edge to check
        if (pushEdge == null) {

            this.relabelVertex();
            this.setDead(false);
            this.setBacktrack(false);
            newActiveVertex = null;

        } //end if

        // There is another edge to check
        else {

            // This is a normal edge
            if (this == pushEdge.getStartVertex()) {

                // There is some flow on the edge, we might be pushing forward or backwards
                if (pushEdge.getStartVertex().getLabel() == pushEdge.getEndVertex().getLabel() + 1 && pushEdge.getCapacity() > pushEdge.getFlow()) {

                    // We are adding flow to an unsaturated edge
                    if (!this.isBacktrack()) {

                        StdOut.println("Start Vertex: " + pushEdge.getStartVertex().vertexToString());
                        StdOut.println("End Vertex: " + pushEdge.getEndVertex().vertexToString());
                        newActiveVertex = pushEdge.pushFlowForward();

                    } //end if

                    // We are removing flow from an unsaturated edge
                    else {

                        StdOut.println("Start Vertex: " + pushEdge.getStartVertex().vertexToString());
                        StdOut.println("End Vertex: " + pushEdge.getEndVertex().vertexToString());
                        newActiveVertex = pushEdge.pushResFlowBackward();

                    } //end else
                    //newActiveVertex = pushEdge.pushFlowForward();

                } //end if

                // The edge has capacity 0, so this will return null
                else if (pushEdge.getStartVertex().getLabel() == pushEdge.getEndVertex().getLabel() + 1 && pushEdge.getCapacity() == 0) {

                    newActiveVertex = pushEdge.pushFlowForward();

                } //end else if

                // The edge is saturated, so we are pushing flow backward
                else if (pushEdge.getStartVertex().getLabel() == pushEdge.getEndVertex().getLabel() + 1 && pushEdge.getCapacity() == pushEdge.getFlow()) {

                    newActiveVertex = pushEdge.pushResFlowBackward();

                } //end else if

                // The edge has no flow but positive capacity
                else if (pushEdge.getStartVertex().getLabel() == pushEdge.getEndVertex().getLabel() + 1 && 0 == pushEdge.getFlow()) {

                    newActiveVertex = pushEdge.pushFlowForward();

                } //end else if

                // This is the last edge, and no pushing operations are available
                else if (this.isDead()) {

                    this.relabelVertex();
                    this.setDead(false);
                    this.setBacktrack(false);
                    newActiveVertex = null;

                } //end else if

                else {

                    /*StdOut.println("START: " + pushEdge.getStartVertex().getLabel());
                    StdOut.println("END: " + pushEdge.getEndVertex().getLabel());
                    StdOut.println("VERTEXS: " + pushEdge.getStartVertex().vertexToString());
                    StdOut.println("VERTEXE: " + pushEdge.getEndVertex().vertexToString());*/
                    newActiveVertex = null;

                } //end else

            } //end if

            // This is a residual edge
            else {

                // The next edge is a residual edge, and its not saturated
                if (pushEdge.getEndVertex().getLabel() == pushEdge.getStartVertex().getLabel() + 1 && 0 < pushEdge.getFlow() && pushEdge.getFlow() != pushEdge.getCapacity()) {

                    // We are adding flow to an unsaturated edge
                    if (!this.isBacktrack()) {

                        StdOut.println("Start Vertex: " + pushEdge.getEndVertex().vertexToString());
                        StdOut.println("End Vertex: " + pushEdge.getStartVertex().vertexToString());
                        newActiveVertex = pushEdge.pushResFlowForward();

                    } //end if

                    // We are removing flow from an unsaturated edge
                    else {

                        StdOut.println("Start Vertex: " + pushEdge.getEndVertex().vertexToString());
                        StdOut.println("End Vertex: " + pushEdge.getStartVertex().vertexToString());
                        newActiveVertex = pushEdge.pushFlowBackward();

                    } //end else

                } //end if

                //The next edge is a residual edge, and it has no capacity
                else if (pushEdge.getEndVertex().getLabel() == pushEdge.getStartVertex().getLabel() + 1 && 0 == pushEdge.getCapacity()) {

                    newActiveVertex = pushEdge.pushResFlowForward();

                } //end else if

                // The next edge is a residual edge, and it is saturated
                else if (pushEdge.getEndVertex().getLabel() == pushEdge.getStartVertex().getLabel() + 1 && pushEdge.getFlow() == pushEdge.getCapacity()) {
StdOut.println(this.vertexToString());
                    newActiveVertex = pushEdge.pushFlowBackward();

                } //end else if

                // The next edge is a residual edge, and it currently has no flow but positive capacity
                else if (pushEdge.getEndVertex().getLabel() == pushEdge.getStartVertex().getLabel() + 1 && 0 == pushEdge.getFlow()) {
                    StdOut.println(this.vertexToString());
                    newActiveVertex = pushEdge.pushResFlowForward();

                } //end else if

                // This is the last edge, and no pushing operations are available
                else if (this.isDead()) {

                    this.relabelVertex();
                    this.setDead(false);
                    this.setBacktrack(false);
                    newActiveVertex = null;

                } //end else if

                else {

                    newActiveVertex = null;

                } //end else

            } //end else

        } //end else

        return newActiveVertex;

    } //end pushRelabel

    public FlowEdge getNextEdge() {

        // There are no more edges
        if (this.isDead()) {

            return null;

        } //end if

        // There is only the backtrack edge
        else if (this.isBacktrack()) {

            this.setDead(true);
            return this.getBacktrackEdge();

        } //end else if

        // There are still edges to check
        else {

            if (iteratorAugPath >= 0 && iteratorAugPath < adjacencyList.size()) {

                iteratorAugPath++;

                if (iteratorAugPath == adjacencyList.size() && resAdjacencyList.size() == 0) {

                    //this.setDead(true);
                    this.setBacktrack(true);

                } //end if

                return adjacencyList.get(iteratorAugPath-1);

            } //end if

            else if (iteratorAugPath == adjacencyList.size()) {

                iteratorAugPath = -1;

                if (1 == resAdjacencyList.size()) {

                    //this.setDead(true);
                    this.setBacktrack(true);

                } //end if

                if (0 == resAdjacencyList.size()) {

                    //this.setDead(true);
                    this.setBacktrack(true);
                    return null;

                } //end if

                return resAdjacencyList.get(-1 * iteratorAugPath - 1);

            } //end else if

            else if (-1 * iteratorAugPath < resAdjacencyList.size() - 1) {

                iteratorAugPath--;
                return resAdjacencyList.get(-1 * iteratorAugPath - 1);

            } //end else if

            else {

                iteratorAugPath--;
                //this.setDead(true);
                this.setBacktrack(true);
                return resAdjacencyList.get(-1 * iteratorAugPath - 1);

            } //end else

        } //end else

    } //end getNextEdge

    public void setPreviousEdge() {

        if (!this.isDead()) {

            if (iteratorAugPath > 0 && iteratorAugPath <= adjacencyList.size()) {

                iteratorAugPath--;

            } //end if

            else if (iteratorAugPath == -1) {

                iteratorAugPath = adjacencyList.size();

            } //end else if

            else if (iteratorAugPath < -1) {

                iteratorAugPath++;

            } //end else if

            else {

                // Do nothing

            } //end else

        } //end if

        else if (this.isBacktrack()){

            this.setDead(false);
            this.setBacktrack(false);

            if (resAdjacencyList.size() > 0) {

                iteratorAugPath = -1 * resAdjacencyList.size() - 1;

            } //end if

            else if (adjacencyList.size() > 0){

                iteratorAugPath = adjacencyList.size() - 1;

            } //end else

        } //end else

    } //end setPreviousEdge

    public String vertexToString() {

        StringBuilder s = new StringBuilder();
        s.append("Vertex " + id + " (label " + this.label + ", lsLabel " + this.localSearchLabel + ", pred " + this.predecessor + ", aug " + iteratorAugPath + "):  ");

        ListIterator<FlowEdge> it = adjacencyList.listIterator();

        while (it.hasNext()) {

            FlowEdge nextEdge = it.next();

            if (nextEdge.getEndVertex() != null) {

                s.append(nextEdge.edgeToString() + "  ");

            } //end if

        } //end while

        it = resAdjacencyList.listIterator();

        while (it.hasNext()) {

            FlowEdge nextEdge = it.next();

            if (nextEdge.getStartVertex() != null) {

                s.append(nextEdge.edgeToString() + "  ");

            } //end if

        } //end while

        return s.toString();

    } //end vertexToString

} //end FlowVertex
