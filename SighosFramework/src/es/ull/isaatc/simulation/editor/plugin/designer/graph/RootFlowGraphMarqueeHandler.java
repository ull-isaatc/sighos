package es.ull.isaatc.simulation.editor.plugin.designer.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.SwingUtilities;

import org.jgraph.graph.BasicMarqueeHandler;

import org.jgraph.graph.Port;
import org.jgraph.graph.PortView;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.ExitCell;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.PackageCell;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.SingleCell;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.GroupSplitCell;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.CursorFactory;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.menu.Palette;
import es.ull.isaatc.simulation.editor.project.model.ExitFlow;
import es.ull.isaatc.simulation.editor.project.model.GroupFlow;
import es.ull.isaatc.simulation.editor.project.model.PackageFlow;
import es.ull.isaatc.simulation.editor.project.model.SingleFlow;

public class RootFlowGraphMarqueeHandler extends BasicMarqueeHandler {
	private RootFlowGraph graph;

	private PortView startPort, currentPort;

	private static final int PORT_BUFFER = 2;

	public RootFlowGraphMarqueeHandler(RootFlowGraph graph) {
		this.graph = graph;
		startPort = null;
		currentPort = null;
	}

	public boolean isForceMarqueeEvent(MouseEvent event) {
		return (Palette.getInstance().getSelected() != Palette.SELECTION || super
				.isForceMarqueeEvent(event));
	}

	public void mouseMoved(MouseEvent event) {
		switch (Palette.getInstance().getSelected()) {
		case Palette.FLOW_CONNECTION: {
			if (startPort == null) {
				graph.setCursor(CursorFactory
						.getCustomCursor(CursorFactory.FLOW_CONNECTION));
				PortView newPort = getSourcePortAt(event.getPoint());
				if (newPort == null) {
					hidePort(currentPort);
					currentPort = null;
				}
				if (newPort != null && generatesOutgoingFlows(newPort)) {
					if (newPort != currentPort) {
						hidePort(currentPort);
						currentPort = newPort;
						showPort(currentPort);
					}
				}
				event.consume();
			}
			break;
		}
		case Palette.SINGLE_FLOW: {
			graph.setCursor(CursorFactory
					.getCustomCursor(CursorFactory.SIGLE_FLOW));
			event.consume();
			break;
		}
		case Palette.PACKAGE_NODE: {
			graph.setCursor(CursorFactory
					.getCustomCursor(CursorFactory.PACKAGE_NODE));
			event.consume();
			break;
		}
		case Palette.DECISION_NODE: {
			graph.setCursor(CursorFactory
					.getCustomCursor(CursorFactory.DECISION_NODE));
			event.consume();
			break;
		}
		case Palette.SELECTION: {
			graph.setCursor(CursorFactory
					.getCustomCursor(CursorFactory.SELECTION));
			event.consume();
			break;
		}
		default: {
			super.mouseMoved(event);
			event.consume();
		}
		}
	}

	public void mousePressed(final MouseEvent event) {

		if (SwingUtilities.isRightMouseButton(event)) {
			super.mousePressed(event);
			return;
		}

		switch (Palette.getInstance().getSelected()) {
		case Palette.FLOW_CONNECTION: {
			if (currentPort != null && generatesOutgoingFlows(currentPort)) {
				setStartPoint(getNearestSnapPoint(graph.toScreen(currentPort
						.getLocation())));
				startPort = currentPort;
			}
			event.consume();
			break;
		}
		case Palette.SINGLE_FLOW: {
			SingleCell cell = graph.addSingleFlow(getNearestSnapPoint(event
					.getPoint()));
			cell.setFlow(new SingleFlow());
			graph.updateAutoSize(graph.getViewFor(cell));
			break;
		}
		case Palette.PACKAGE_NODE: {
			PackageCell cell = graph
					.addPackageFlow(getNearestSnapPoint(event.getPoint()));
			cell.setFlow(new PackageFlow());
			graph.updateAutoSize(graph.getViewFor(cell));
			break;
		}
		case Palette.FINAL_FLOW: {
			ExitCell cell = graph.addExitFlow(getNearestSnapPoint(event.getPoint()));
			cell.setFlow(new ExitFlow());
			break;
		}
		case Palette.SPLIT_FLOW: {
			GroupSplitCell cell = graph.addGroupSplitFlow(getNearestSnapPoint(event
					.getPoint()));
			cell.setFlow(new GroupFlow() {
				public Object getXML() {
					return null;
				}

				public List<ProblemTableItem> validate() {
					return null;
				}
			});
			break;
		}
		case Palette.DECISION_NODE: {
			graph.addDecisionFlow(getNearestSnapPoint(event.getPoint()));
			break;
		}
		case Palette.TYPE_NODE: {
			graph.addTypeFlow(getNearestSnapPoint(event.getPoint()));
			break;
		}
		default: {
			super.mousePressed(event);
			break;
		}
		}
	}

	private Point2D getNearestSnapPoint(Point2D point) {
		return graph.toScreen(graph.snap(point));
	}

	public void mouseDragged(MouseEvent event) {
		switch (Palette.getInstance().getSelected()) {
		case Palette.FLOW_CONNECTION: {
			if (!event.isConsumed() && startPort != null) {
				hideConnector();
				setCurrentPoint(graph.snap(event.getPoint()));
				showConnector();
				PortView thisPort = getSourcePortAt(event.getPoint());
				if (thisPort != null && thisPort != startPort
						&& connectionAllowable(startPort, thisPort)
						&& acceptsIncommingFlows(thisPort)) {
					hidePort(currentPort);
					currentPort = thisPort;
					showPort(currentPort);
				}
				if (thisPort == null) {
					hidePort(currentPort);
					currentPort = null;
				}
				event.consume();
			}
			break;
		}
		default: {
			super.mouseDragged(event);
			break;
		}
		}
	}

	public void mouseReleased(MouseEvent e) {
		switch (Palette.getInstance().getSelected()) {
		case Palette.FLOW_CONNECTION: {
			connectElementsOrIgnoreFlow();
			e.consume();
			break;
		}
		case Palette.SELECTION: {
			super.mouseReleased(e);
			break;
		}
		default: {
			e.consume();
			break;
		}
		}
	}

	public void connectElementsOrIgnoreFlow() {
		if (currentPort != null && startPort != null
				&& connectionAllowable(startPort, currentPort)
				&& acceptsIncommingFlows(currentPort)) {
			graph.connect((Port) startPort.getCell(), (Port) currentPort
					.getCell());
		}
		hideConnector();
		startPort = currentPort = null;
		setStartPoint(null);
		setCurrentPoint(null);
	}

	public PortView getSourcePortAt(Point point) {
		final Point2D tmp = graph.fromScreen(point);
		return graph.getPortViewAt(tmp.getX(), tmp.getY());
	}

	private void hideConnector() {
		paintConnector(Color.black, graph.getBackground(), graph.getGraphics());
	}

	private void showConnector() {
		paintConnector(graph.getBackground(), Color.black, graph.getGraphics());
	}

	protected void paintConnector(Color fg, Color bg, Graphics g) {
		g.setColor(fg);
		g.setXORMode(bg);
		if (startPort != null && getStartPoint() != null
				&& getCurrentPoint() != null)
			g.drawLine((int) getStartPoint().getX(), (int) getStartPoint()
					.getY(), (int) getCurrentPoint().getX(),
					(int) getCurrentPoint().getY());
	}

	protected void hidePort(PortView thisPort) {
		final Graphics g = graph.getGraphics();
		g.setColor(graph.getBackground());
		g.setXORMode(graph.getMarqueeColor());
		showPort(thisPort);
	}

	protected void showPort(PortView thisPort) {
		if (thisPort != null) {
			Rectangle2D portBounds = thisPort.getBounds();

			Rectangle2D.Double portViewbox = new Rectangle2D.Double(portBounds
					.getX()
					- PORT_BUFFER / 2, portBounds.getY() - PORT_BUFFER / 2,
					portBounds.getWidth() + (2 * PORT_BUFFER), portBounds
							.getHeight()
							+ (2 * PORT_BUFFER));

			graph.getUI().paintCell(graph.getGraphics(), thisPort, portViewbox,
					true);
		}
	}

	public PortView getPortViewAt(int x, int y) {
		PortView port = graph.getPortViewAt(x, y);
		return port;
	}

	private boolean acceptsIncommingFlows(PortView portView) {
		// TODO : hacer este y el siguiente
		// CellView parentView = portView.getParentView();
		// YAWLPort yawlPort = (YAWLPort) portView.getCell();
		// YAWLCell vertex = (YAWLCell) parentView.getCell();
		// return yawlPort.acceptsIncomingFlows()
		// && vertex.acceptsIncommingFlows()
		// && graph.acceptsIncommingFlows(vertex);
		return true;
	}

	private boolean generatesOutgoingFlows(PortView portView) {
		// CellView parentView = portView.getParentView();
		// YAWLPort yawlPort = (YAWLPort) portView.getCell();
		// YAWLCell vertex = (YAWLCell) parentView.getCell();
		// return yawlPort.generatesOutgoingFlows()
		// && vertex.generatesOutgoingFlows()
		// && graph.generatesOutgoingFlows(vertex);
		return true;
	}

	private boolean connectionAllowable(PortView source, PortView target) {
		return graph.connectionAllowable((Port) source.getCell(), (Port) target
				.getCell());
	}
}