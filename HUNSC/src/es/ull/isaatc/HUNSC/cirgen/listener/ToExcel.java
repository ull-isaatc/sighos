/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.listener;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface ToExcel {
	void setResult(HSSFWorkbook wb);
}
