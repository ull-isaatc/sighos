/**
 * 
 */
package es.ull.isaatc.simulation.editor.plugin.designer.graph.layouts;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphModel;

/**
 * @author Roberto
 * 
 */
public class LayoutManager {

//	private TreeLayoutAlgorithm layoutAlgorithm = new TreeLayoutAlgorithm();
	private SugiyamaLayoutAlgorithm layoutAlgorithm = new SugiyamaLayoutAlgorithm();
//	private SpringEmbeddedLayoutAlgorithm layoutAlgorithm = new SpringEmbeddedLayoutAlgorithm();

	private static LayoutManager INSTANCE = new LayoutManager();

	public static LayoutManager getInstance() {
		return INSTANCE;
	}

	public void applyLayout(final JGraph graph) {
		if (layoutAlgorithm == null)
			return;
		// Decouple the updating of the progress meter and
		// the running of the layout algorithm.
		final Timer updater = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				layoutAlgorithm.setAllowedToRun(true);
			}
		});
		// Fork the layout algorithm and leave UI dispatcher thread
		Thread t = new Thread("Layout Algorithm " + layoutAlgorithm.toString()) {
			public void run() {
				try {
					Object[] cells = DefaultGraphModel.getAll(graph.getModel());
					if (cells != null && cells.length > 0) {
						updater.start();
						JGraphLayoutAlgorithm.applyLayout(graph,
								layoutAlgorithm, cells, null);
					}
				} finally {
					updater.stop();
				}
			}
		};
		t.start();
	}
}
