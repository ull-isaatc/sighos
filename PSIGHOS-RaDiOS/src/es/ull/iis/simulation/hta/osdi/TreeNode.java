package es.ull.iis.simulation.hta.osdi;

import java.util.LinkedList;
import java.util.List;

public class TreeNode<T> {
	private T data;
	private TreeNode<T> parent;
	private List<TreeNode<T>> children;

	public TreeNode(T data) {
		this.data = data;
		this.parent = null;
		this.children = new LinkedList<TreeNode<T>>();
	}

	public TreeNode(T data, TreeNode<T> parent) {
		this.data = data;
		this.parent = parent;
		this.children = new LinkedList<TreeNode<T>>();
	}

	public TreeNode(T data, TreeNode<T> parent, List<TreeNode<T>> children) {
		this.data = data;
		this.parent = parent;
		this.children = children;
	}

	public TreeNode<T> addChild(T child) {
		TreeNode<T> childNode = new TreeNode<T>(child);
		childNode.parent = this;
		this.children.add(childNode);
		return childNode;
	}
	
	public T getData() {
		return data;
	}
	
	public TreeNode<T> getParent() {
		return parent;
	}

	public void setParent (TreeNode<T> parent) {
		this.parent = parent;
	}
	
	public List<TreeNode<T>> getChildren () {
		return children;
	}
	
	@Override
	public String toString() {
		return getData().toString();
	}
}