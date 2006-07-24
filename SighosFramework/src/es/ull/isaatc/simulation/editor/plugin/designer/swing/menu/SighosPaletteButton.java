package es.ull.isaatc.simulation.editor.plugin.designer.swing.menu;

import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JToggleButton;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;

public class SighosPaletteButton extends JToggleButton {

	private static final long serialVersionUID = 1L;

	private static final Insets margin = new Insets(0, 0, 0, 0);

	public SighosPaletteButton(Action a, int mnemonic) {
		super(a);
		setText(null);
		setMnemonic(mnemonic);
		setMargin(margin);
		setOpaque(false);
		setMaximumSize(getPreferredSize());
	}

	public Point getToolTipLocation(MouseEvent e) {
		return new Point(0, getSize().height);
	}

	public void setEnabled(boolean enabled) {
		TooltipTogglingWidget action = (TooltipTogglingWidget) this.getAction();
		if (enabled) {
			setToolTipText(action.getEnabledTooltipText());
		} else {
			setToolTipText(action.getDisabledTooltipText());
		}
		super.setEnabled(enabled);
	}
}
