/**
 * 
 */
package es.ull.isaatc.simulation.editor.plugin.designer.swing.util;

import javax.swing.JComboBox;

import es.ull.isaatc.simulation.xml.CommonFreq;

/**
 * @author Roberto
 *
 */
public class CommonFreqComboBox extends JComboBox {

	private static final long serialVersionUID = 1L;
	
	public CommonFreqComboBox() {
		super();
		CommonFreq values[] = CommonFreq.values();
		for (int i = 0; i < values.length; i++)
			addItem(values[i].value());
	}
}
