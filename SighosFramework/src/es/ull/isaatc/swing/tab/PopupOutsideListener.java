/*
 * David Bismut, davidou@mageos.com
 * Intern, SETLabs, Infosys Technologies Ltd. May 2004 - Jul 2004
 * Ecole des Mines de Nantes, France
 */


package es.ull.isaatc.swing.tab;

import java.awt.event.MouseEvent;
import java.util.EventListener;

public interface PopupOutsideListener extends EventListener {
	public void popupOutsideOperation(MouseEvent e);
}
