package es.ull.isaatc.simulation.editor.plugin.designer.swing.menu;

import java.awt.Dimension;

import javax.swing.JToolBar;

abstract class SighosToolBar extends JToolBar {

	private static final Dimension spacer = new Dimension(11, 16);

	public SighosToolBar(String title) {
		super(title);
		initialize();
		setMaximumSize(getPreferredSize());
	}

	protected abstract void initialize();

	public void addSeparator() {
		super.addSeparator(spacer);
	}
}
