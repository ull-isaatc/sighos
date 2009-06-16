/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.view;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Vistas de la simulación de los servicios quirúrgicos.
 * @author Iván Castilla Rodríguez
 *
 */
public interface GSView {
	void setResults(HSSFWorkbook wb);
	GSResult getResults();
}
