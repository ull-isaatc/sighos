package es.ull.isaatc.simulation.editor.plugin.designer.swing.menu;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import es.ull.isaatc.simulation.editor.plugin.designer.actions.graph.SighosExistingGraphAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.palette.*;

public class Palette extends SighosToolBar implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	public static final int SINGLE_FLOW = 0;

	public static final int PACKAGE_NODE = 1;

	public static final int FINAL_FLOW = 2;

	public static final int SPLIT_FLOW = 3;

//	public static final int JOIN_FLOW = 4;
	
	public static final int DECISION_NODE = 4;

//	public static final int DECISION_JOIN_NODE = 6;
	
	public static final int TYPE_NODE = 5;
	
//	public static final int TYPE_JOIN_NODE = 8;

	public static final int FLOW_CONNECTION = 6;

	public static final int SELECTION = 7;

	private static final SighosExistingGraphAction existingGraph = new SighosExistingGraphAction();

	private static int selectedItem = SELECTION;

	private boolean enabledState = true;

	private static SighosPaletteButton[] buttons = {
			new SighosPaletteButton(new SingleFlowAction(), KeyEvent.VK_1),
			new SighosPaletteButton(new PackageFlowAction(), KeyEvent.VK_2),
			new SighosPaletteButton(new FinalFlowAction(), KeyEvent.VK_3),
			new SighosPaletteButton(new GroupSplitFlowAction(), KeyEvent.VK_4),
//			new SighosPaletteButton(new JoinFlowAction(), KeyEvent.VK_5),
			new SighosPaletteButton(new DecisionFlowAction(), KeyEvent.VK_5),
//			new SighosPaletteButton(new DecisionJoinFlowAction(), KeyEvent.VK_7),
			new SighosPaletteButton(new TypeFlowAction(), KeyEvent.VK_6),
//			new SighosPaletteButton(new TypeJoinFlowAction(), KeyEvent.VK_9),
			new SighosPaletteButton(new FlowConnectionAction(), KeyEvent.VK_C),
			new SighosPaletteButton(new SelectionAction(), KeyEvent.VK_S) };

	private static final Palette INSTANCE = new Palette();

	public static Palette getInstance() {
		return INSTANCE;
	}

	private Palette() {
		super("SihosDesigner Palette");
		existingGraph.addPropertyChangeListener(this);
	}

	protected void initialize() {
		setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		setMargin(new Insets(0, 0, 0, 0));
		add(getButtons());
	}

	private JPanel getButtons() {
		JPanel buttonPanel = new JPanel(new GridLayout(buttons.length, 0, 0, 0));
		for (int i = 0; i < buttons.length; i++) {
			buttonPanel.add(buttons[i]);
		}
		return buttonPanel;
	}

	public void setSelected(int item) {
		buttons[selectedItem].setSelected(false);
		selectedItem = item;
		buttons[selectedItem].setSelected(true);
	}

	public int getSelected() {
		return selectedItem;
	}

	public void setEnabled(boolean state) {
		if (enabledState == state) {
			return;
		}
		setVisible(false);
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setEnabled(state);
		}
		setVisible(true);
		enabledState = state;
	}

	public void propertyChange(PropertyChangeEvent ev) {
		if (ev.getPropertyName().equals("enabled")) {
			setEnabled((Boolean) ev.getNewValue());
		}
	}
}
