package datastructures.linkcut;

/**
 * TreeNode.java
 *
 * Course: CS4490Z
 * Author: Andrew Bloch-Hansen
 *
 * This TreeNode represents a node in a LinkCutTree.
 *
 * This LinkCutTree represents a forest of splay trees. Individual trees
 * are connected in the forrest by a middle child from a parent node to the
 * root of the disjointed tree, and the splay rotations only effect the left
 * and right children.
 */
public class TreeNode {

    /**
     * Pointers to the children and parent.
     */
    private TreeNode left, right, parent;

    /**
     * Represents the cost of the parent edge.
     */
    private int value;

    /**
     * Represents the minimum cost of this node and any of its descendants.
     */
    private int min;

    /**
     * Keeps track of the size of each tree.
     */
    private int size, mySize;

    public TreeNode(int v) {

        left = right = parent = null;
        value = v;
        min = v;
        size = mySize = 1;

    } //end Node

    public TreeNode(TreeNode v) {

        left = v.getLeft();
        right = v.getRight();
        parent = v.getParent();
        value = v.getValue();
        min = v.getMin();
        size = v.getSize();

    } //end TreeNode

    /**
     * Determines if a node is the root of its subtree.
     * @return true if its the root, false otherwise
     */
    public boolean isRoot() {

        // If the parent is null or its a middle child
        return ((parent == null) || (parent.left != this && parent.right != this));

    } //end isRoot

    /**
     * Finds and returns the value of the node v.
     * @return the value of node v
     */
    public int getValue() {

        if (this.isRoot()) {

            return value;

        } //end if

        else {

            return value - parent.getValue();

        } //end else

    } //end getValue

    /**
     * Sets the value of a node.
     * @param x the new value of a node
     */
    public void setValue(int x) {

        value = x;

    } //end setValue

    /**
     * Finds and returns the min value of the tree holding this node.
     * @return the min value of the tree holding this node
     */
    public int getMin() {

        return value - this.getMinValue().getValue();

    } //end getMin

    /**
     * Sets the min value of the tree holding this node
     * @param x the min value of the tree holding this node
     */
    public void setMin(int x) {

        min = x;

    } //end setMin

    /**
     * Calculates the min value of a node and any of its descendants.
     * @return the node with the min value
     */
    public TreeNode getMinValue() {

        TreeNode minNode = this;
        minNode = preorder(this, minNode);
        return minNode;

    } //end getMinValue

    /**
     * Finds and returns the left child of a node.
     * @return the left child of a node
     */
    public TreeNode getLeft() {

        return left;

    } //end getLeft

    /**
     * Sets the left child of a node.
     * @param v the new left child
     */
    public TreeNode setLeft(TreeNode v) {

        left = v;
        return left;

    } //end setLeft

    /**
     * Finds and returns the right child of a node.
     * @return the right child of a node
     */
    public TreeNode getRight() {

        return right;

    } //end getRight

    /**
     * Sets the right child of a node.
     * @param v the new right child
     */
    public TreeNode setRight(TreeNode v) {

        right = v;
        return right;

    } //end setRight

    /**
     * Finds and returns the parent of a node.
     * @return the parent child of a node
     */
    public TreeNode getParent() {

        return parent;

    } //end getParent

    /**
     * Sets the parent of a node.
     * @param v the new parent node
     */
    public TreeNode setParent(TreeNode v) {

        parent = v;
        return parent;

    } //end setParent

    /**
     * Finds and returns the size of the current subtree.
     * @return the size of the current subtree
     */
    public int getSize() {

        return size;

    } //end getSize

    /**
     * Sets the size of the current subtree.
     * @param x the new size of the current subtree
     */
    public void setSize(int x) {

        size = x;

    } //end setSize

    /**
     * Traverses the tree in preorder to find the minimum node value.
     * @param v the current node to visit
     * @param minNode the node with the minimum value so far
     * @return the node with the minimum value found
     */
    private TreeNode preorder(TreeNode v, TreeNode minNode) {

        // Base case
        if (v == null) {

            return minNode;

        } //end if

        // Check if this node has a smaller value
        if (v.getValue() < minNode.getValue()) {

            minNode = v;

        } //end if

        minNode = preorder(v.getLeft(), minNode);
        minNode = preorder(v.getRight(), minNode);

        return minNode;

    } //end preorder

    /**
     * Updates the size of the current subtree.
     */
    public void updateCount() {

        size = mySize;

        if (left != null) {

            size += left.getSize();

        } //end if

        if (right != null) {

            size += right.getSize();

        } //end if

    } //end updateCount

} //end class TreeNode
