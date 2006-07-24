package es.ull.isaatc.simulation.editor.plugin.designer.actions.palette;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import es.ull.isaatc.simulation.editor.plugin.designer.swing.menu.Palette;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

abstract class SighosPaletteAction extends AbstractAction {
	private int paletteIdentifier = Palette.SELECTION;

	public void actionPerformed(ActionEvent event) {
		Palette.getInstance().setSelected(getIdentifier());
	}

	public void setIdentifier(int identifier) {
		paletteIdentifier = identifier;
	}

	public int getIdentifier() {
		return paletteIdentifier;
	}

	protected ImageIcon getPaletteIconByName(String iconName) {
		return ResourceLoader
				.getImageAsIcon("/es/ull/isaatc/simulation/editor/resources/menuicons/palette/"
						+ iconName + "32.gif");
	}
}
