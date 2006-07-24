package es.ull.isaatc.simulation.editor.plugin.designer.swing.menu;

import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JButton;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;

public class SighosToolBarButton extends JButton {

	private static final long serialVersionUID = 1L;

	private static final Insets margin = new Insets(0, 0, 0, 0);

	public SighosToolBarButton(Action a) {
		super(a);
		setText(null);
		setMnemonic(0);
		setMargin(margin);
		setMaximumSize(getPreferredSize());
	}

	public Point getToolTipLocation(MouseEvent e) {
		return new Point(0, getSize().height);
	}

	public void setEnabled(boolean enabled) {
		if (getAction() instanceof TooltipTogglingWidget) {
			TooltipTogglingWidget action = (TooltipTogglingWidget) this
					.getAction();
			if (enabled) {
				setToolTipText(action.getEnabledTooltipText());
			} else {
				setToolTipText(action.getDisabledTooltipText());
			}
		}
		super.setEnabled(enabled);
	}
}
