/**
 * 
 */
package es.ull.isaatc.simulation.editor.util;

import java.util.List;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;

/**
 * This interface provides the property for validating an object
 * @author Roberto Muñoz
 *
 */
public interface Validatory {

	/**
	 * Validates an object
	 * @return
	 */
	public List<ProblemTableItem> validate();
	
}
