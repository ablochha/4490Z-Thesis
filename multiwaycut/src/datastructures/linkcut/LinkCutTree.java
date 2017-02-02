package datastructures.linkcut;

/**
 * LinkCutTree.java
 *
 * Course: CS4490Z
 * Author: Andrew Bloch-Hansen
 *
 * This LinkCutTree implemention is based on the paper Self-Adjusting
 * Binary Search Trees by Sleator and Tarjan.
 *
 * This LinkCutTree represents a forest of splay trees. Individual trees
 * are connected in the forrest by a middle child from a parent node to the
 * root of the disjointed tree, and the splay rotations only effect the left
 * and right children.
 */
public class LinkCutTree implements LinkCutTreeInterface {

    /**
     * Finds and returns the root of the tree holding v.
     * @param v the node that we want to know the root of
     * @return the root node of that tree
     */
    public TreeNode findRoot(TreeNode v) {

        splay(v);
        TreeNode w = v;

        // Descend to the rightmost child to find the root
        while (w.getRight() != null) {

            w = w.getRight();

        } //end while

        splay(w);
        return w;

    } //end findRoot

    /**
     * Finds and returns the size of the tree holding v.
     * @param v the node that we want to know the size of
     * @return the size of that tree
     */
    public int findSize(TreeNode v) {

        return findRoot(v).getSize();

    } //findSize

    /**
     * Finds and returns the value of a node.
     * @param v the node that we want to know the value of
     * @return the value of that node
     */
    public int findValue(TreeNode v) {

        splay(v);
        return v.getValue();

    } //end findValue

    /**
     * Finds and returns the minimum value of a node and its descendants.
     * @param v the node that we want to know the min value of
     * @return the min value of that node
     */
    public TreeNode findMin(TreeNode v) {

        splay(v);
        TreeNode w = v.getMinValue();
        splay(w);
        return w;

    } //end findMin

    /**
     * Changes the value of a node.
     * @param v the node that we want to change the value of
     * @param x the new value of the node
     */
    public void changeValue(TreeNode v, int x) {

        splay(v);
        v.setValue(v.getValue() + x);

        // Update the left child's value accordingly
        if (v.getLeft() != null) {

            v.getLeft().setValue(v.getLeft().getValue() - x);

        } //end if

    } //end changeValue

    /**
     * Links the disjoint vertices v and w by making the middle child.
     * of w the root of the tree containing v.
     * @param v the root of the tree v
     * @param w a node from another tree
     */
    public void link(TreeNode v, TreeNode w) {

        splay(v);
        splay(w);
        v.setParent(w);

    } //end link

    /**
     * Deletes the edge between the vertex v and it's right child.
     * @param v the node that we want to cut
     */
    public void cut(TreeNode v) {

        splay(v);
        v.getRight().setValue(v.getRight().getValue() + v.getValue());
        v.getRight().setParent(null);
        v.setRight(null);

    } //end cut

    /**
     * Performs a series of rotations until a node becomes the root of the tree.
     * @param v the node that we want to rotate to the root
     */
    private void splay(TreeNode v) {

        // Splay the node v up to the root of the tree
        while (!v.isRoot()) {

            TreeNode w = v.getParent();

            // Only one rotation needed
            if (w.isRoot()) {

                // Zig
                if (w.getLeft() == v) {

                    rotateRight(v);

                } //end if

                // Zig
                else {

                    rotateLeft(v);

                } //end else

            } //end if

            // Multiple rotations needed
            else {

                TreeNode r = w.getParent();

                if (r.getLeft() == v) {

                    // Zig-Zig
                    if (w.getLeft() == v) {

                        rotateRight(w);
                        rotateRight(v);

                    } //end if

                    // Zig-Zag
                    else {

                        rotateLeft(w);
                        rotateRight(v);

                    } //end else

                } //end if

                else {

                    // Zig-Zig
                    if (w.getRight() == v) {

                        rotateLeft(w);
                        rotateLeft(v);

                    } //end if

                    // Zig-Zag
                    else {

                        rotateRight(v);
                        rotateLeft(v);

                    } //end else

                }//end else

            } //end else

        } //end while

        v.updateCount();

    } //end splay

    /**
     * Rotates the node v to the right and adjusts the node values.
     * @param v the node that we want to rotate to the right
     */
    private void rotateRight(TreeNode v) {

        TreeNode w = v.getParent();
        TreeNode r = w.getParent();

        // Stores the values before the rotations take place
        int vValue = v.getValue();
        int wValue = w.getValue();
        int aValue = v.getLeft().getValue();
        int bValue = v.getRight().getValue();
        int cValue = w.getRight().getValue();

        int aMin = v.getLeft().getMin();
        int bMin = v.getRight().getMin();
        int cMin = w.getRight().getMin();

        if ((w.setLeft(v.getRight())) != null) {

            w.getLeft().setParent(w);

        } //end if

        v.setRight(w);
        w.setParent(v);

        if ((v.setParent(v)) != null) {

            if (r.getLeft() == w) {

                r.setLeft(v);

            } //end if

            else if (r.getRight() == w) {

                r.setRight(v);

            } //end else if

        } //end if

        // Adjust the edge costs after the rotation
        v.setValue(vValue + wValue);
        w.setValue(0 - vValue);
        w.getLeft().setValue(vValue + bValue);
        w.setMin(Math.max(Math.max(0, bMin - w.getLeft().getValue()), cMin - cValue));
        v.setMin(Math.max(Math.max(0, aMin - aValue), w.getMin() - w.getValue()));

        w.updateCount();

    } //end rotateRight

    /**
     * Rotates the node v to the left and adjusts the node values.
     * @param v the node that we want to rotate to the left
     */
    private void rotateLeft(TreeNode v) {

        TreeNode w = v.getParent();
        TreeNode r = w.getParent();

        // Stores the values before the rotations take place
        int vValue = v.getValue();
        int wValue = w.getValue();
        int aValue = w.getLeft().getValue();
        int bValue = v.getLeft().getValue();
        int cValue = v.getRight().getValue();

        int aMin = w.getLeft().getMin();
        int bMin = v.getLeft().getMin();
        int cMin = v.getRight().getMin();

        if ((w.setRight(v.getLeft())) != null) {

            w.getRight().setParent(w);

        } //end if

        v.setLeft(w);
        w.setParent(v);

        if ((v.setParent(r)) != null) {

            if (r.getLeft() == w) {

                r.setRight(v);

            } //end if

            else if(r.getRight() == w) {

                r.setRight(v);

            } //end else if

        } //end if

        // Adjust the edge costs after the rotation
        v.setValue(vValue + wValue);
        w.setValue(0 - vValue);
        w.getRight().setValue(vValue + bValue);
        w.setMin(Math.max(Math.max(0, bMin - w.getRight().getValue()), cMin - cValue));
        v.setMin(Math.max(Math.max(0, aMin - aValue), w.getMin() - w.getValue()));

        w.updateCount();

    } //end rotateLeft

} //end LinkCutTree
