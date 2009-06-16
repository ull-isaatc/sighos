/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.view;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Vistas de la simulaci�n de los servicios quir�rgicos.
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface GSView {
	void setResults(HSSFWorkbook wb);
	GSResult getResults();
}
