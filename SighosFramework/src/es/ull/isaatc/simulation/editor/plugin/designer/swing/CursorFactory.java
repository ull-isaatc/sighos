package es.ull.isaatc.simulation.editor.plugin.designer.swing;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class CursorFactory {
	public static final int SELECTION = 0;

	public static final int FLOW_CONNECTION = 1;

	public static final int DECISION_NODE = 2;

	public static final int SIGLE_FLOW = 3;

	public static final int PACKAGE_NODE = 4;

	private static final Point topLeft = new Point(1, 1);

	private static final Cursor[] cursors = {
			buildCustomCursor("Selection32", topLeft, "Selection"),
			buildCustomCursor("FlowConnection32", topLeft, "FlowConnection"),
			buildCustomCursor("DecisionNode32", topLeft, "DecisionNode"),
			buildCustomCursor("SingleFlow32", topLeft, "SingleFlow"),
			buildCustomCursor("PackageNode32", topLeft, "PackageNode"), };

	public static Cursor getCustomCursor(int cursorType) {
		return cursors[cursorType];
	}

	private static Cursor buildCustomCursor(String cursorFileName,
			Point hotspot, String name) {
		Image image = ResourceLoader.getImageAsIcon(
				"/es/ull/isaatc/simulation/editor/plugin/designer/resources/cursors/"
						+ cursorFileName + ".gif").getImage();
		assert image != null : "Image is null!";
		return Toolkit.getDefaultToolkit().createCustomCursor(image, hotspot,
				name);
	}
}
