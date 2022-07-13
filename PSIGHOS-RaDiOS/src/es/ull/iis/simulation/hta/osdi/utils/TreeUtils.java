package es.ull.iis.simulation.hta.osdi.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;

public interface TreeUtils {
	/**
	 * @param node
	 * @param spaces
	 * @throws TranspilerException 
	 * @throws IOException 
	 */
	public static void preorden(TreeNode<NodeData> node, String spaces, ByteArrayOutputStream baos) throws TranspilerException, IOException {
		if (node == null) { return; }

		showTreeNode(node, spaces, baos);

		for (TreeNode<NodeData> n : node.getChildren()) {
			preorden(n, spaces + "   ", baos);
		}
	}

	/**
	 * @param node
	 * @param spaces
	 * @throws TranspilerException 
	 * @throws IOException 
	 */
	public static void inorden(TreeNode<NodeData> node, String spaces, ByteArrayOutputStream baos) throws TranspilerException, IOException {
		if (node == null) { return; }

		int c = 0;
		if (!node.getChildren().isEmpty()) {
			int d = node.getChildren().size() / 2;
			for (TreeNode<NodeData> n : node.getChildren()) {
				if (c < d) {
					inorden(n, spaces + Constants.CONSTANT_DEFAULT_SPACES_SHOW_NODE, baos);
					c++;
				} else {
					if (d == 0) { // Significa que el nodo tan solo tiene un hijo
						inorden(n, spaces + Constants.CONSTANT_DEFAULT_SPACES_SHOW_NODE, baos);
						showTreeNode(node, spaces, baos);
						c++;
					} else {
						if (c == d) {
							showTreeNode(node, spaces, baos);
						}
						inorden(n, spaces + Constants.CONSTANT_DEFAULT_SPACES_SHOW_NODE, baos);
						c++;
					}
				}
			}
		} else {
			showTreeNode(node, spaces, baos);
		}
	}

	public static void showTreeNode(TreeNode<NodeData> node, String spaces, ByteArrayOutputStream baos) throws TranspilerException, IOException {
		baos.write(String.format("%s%s\n", spaces, node.toString()).getBytes());
	}
	
	/**
	 * @param root
	 * @return
	 */
	public static List<TreeNode<NodeData>> getTreeNodeLeafs (TreeNode<NodeData> root) {
		List<TreeNode<NodeData>> result = new ArrayList<>();
		Deque<TreeNode<NodeData>> stack = new ArrayDeque<>();
		stack.push(root);
		while (!stack.isEmpty()) {
			TreeNode<NodeData> node = stack.pop();
			if (node.getChildren().isEmpty()) {
				result.add(node);
			} else {
				for (TreeNode<NodeData> child : node.getChildren()) {
					stack.push(child);
				}
			}
		}
		return result;	
	}
	
	/**
	 * @param manifestations
	 * @param lifeExpectancy
	 * @return
	 */
	public static List<String> transformManifestationsToListForOrder(List<TreeNode<NodeData>> manifestations, Double lifeExpectancy) {		
		List<String> result = new ArrayList<String>();
		Double value = null;
		for (TreeNode<NodeData> manifestation : manifestations) {
			StringBuilder sb = new StringBuilder();
			
			value = 0.0;
			if (manifestation.getData().getProperties().containsKey(Constants.DATAPROPERTY_ONSET_AGE)) {
				value = Double.valueOf(manifestation.getData().getProperties().get(Constants.DATAPROPERTY_ONSET_AGE).getValue());
			}
			sb.append(String.format(Locale.US, "%07.3f", value).replace(".", "")).append("_");

			value = lifeExpectancy;
			if (manifestation.getData().getProperties().containsKey(Constants.DATAPROPERTY_END_AGE)) {
				value = Double.valueOf(manifestation.getData().getProperties().get(Constants.DATAPROPERTY_END_AGE).getValue());
			}
			sb.append(String.format(Locale.US, "%07.3f", value).replace(".", "")).append(":::");
			
			sb.append(manifestation);
			result.add(sb.toString());
		}
		return result;
	}
}
