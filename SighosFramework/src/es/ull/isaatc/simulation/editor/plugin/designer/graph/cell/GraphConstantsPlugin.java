package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell;

import java.util.Map;

/**
 * A class very similar to GraphConstants (in jgraph) that allows to access some
 * extra cell graphical attributes required for SighosDesigner, this avoid to modify
 * JGraph.
 */
public class GraphConstantsPlugin {

	public static String ORIENTATION = "orientation";
	
	public static String HORIZONTAL = "0";
	public static String VERTICAL = "1";

	/**
	 * Sets the orientation of a cell
	 */
	public static final void setOrientation(Map map, String  value) {
		map.put(ORIENTATION, value);
	}

	/**
	 * Returns the orientation of a cell
	 */
	public static final String getOrientation(Map map) {
		Object val = map.get(ORIENTATION);
		if (val == null || val.toString().equals(""))
			return "";
		return map.get(ORIENTATION).toString();
	}
	
	/**
	 * Changes the orientation of a cell
	 */
	public static final void changeOrientation(Map map) {
		Object val = map.get(ORIENTATION);
		if (val == null || val.toString().equals(""))
			return;
		if (map.get(ORIENTATION).toString().equals(HORIZONTAL))
			map.put(ORIENTATION, VERTICAL);
		else
			map.put(ORIENTATION, HORIZONTAL);
	}	
	
}
