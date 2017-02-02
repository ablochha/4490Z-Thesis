package datastructures.linkcut;

/**
 * LinkCutTreeInterface.java
 *
 * Course: CS4490Z
 * Author: Andrew Bloch-Hansen
 *
 * This LinkCutTreeInterface is based on the paper A New
 * Approach to the Maximum-Flow Problem by Goldberg and Tarjan.
 */
public interface LinkCutTreeInterface {

	public TreeNode findRoot(TreeNode v);

	public int findSize(TreeNode v);

	public int findValue(TreeNode v);

	public TreeNode findMin(TreeNode v);

	public void changeValue(TreeNode v, int x);

	public void link(TreeNode v, TreeNode w);

	public void cut(TreeNode v);
  
} //end LinkCutTreeInterface
