package es.ull.isaatc.HUNSC.test;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * 
 */

/**
 * @author Iván
 *
 */
public class TestHSSF {

	public static HSSFWorkbook testValues() {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sh = wb.createSheet("prueba");
		HSSFRow row = sh.createRow(0);
		row.createCell((short)1).setCellValue(1.2);
	    row.createCell((short)2).setCellValue(new HSSFRichTextString("This is a string"));
	    row.createCell((short)3).setCellValue(true);
		return wb;
	}
	
	public static HSSFWorkbook testFormula() {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sh = wb.createSheet("prueba");
		HSSFRow row = sh.createRow(0);
		row.createCell((short)0).setCellFormula("COUNTIF(A2:A100;\">50\")");
		for (int i = 1; i < 100; i++) {
			row = sh.createRow(i);
			row.createCell((short)0).setCellValue(i);
		}
		return wb;
	}
	
	public static HSSFWorkbook testFreeze() {
		   HSSFWorkbook wb = new HSSFWorkbook();
		    HSSFSheet sheet1 = wb.createSheet("new sheet");
		    HSSFSheet sheet2 = wb.createSheet("second sheet");
		    HSSFSheet sheet3 = wb.createSheet("third sheet");
		    HSSFSheet sheet4 = wb.createSheet("fourth sheet");

		    // Freeze just one row
		    sheet1.createFreezePane( 0, 1, 0, 1 );
		    // Freeze just one column
		    sheet2.createFreezePane( 1, 0, 1, 0 );
		    // Freeze the columns and rows (forget about scrolling position of the lower right quadrant).
		    sheet3.createFreezePane( 2, 2 );
		    // Create a split with the lower left side being the active quadrant
		    sheet4.createSplitPane( 2000, 2000, 0, 0, HSSFSheet.PANE_LOWER_LEFT );
		    
		    return wb;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    FileOutputStream f;
		try {
			f = new FileOutputStream("C:\\testHSSF.xls");
		    testFormula().write(f);
		    f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
