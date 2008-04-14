/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.listener;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface ToExcel {
	void setResult(HSSFWorkbook wb);
}
