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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sh = wb.createSheet("prueba");
		HSSFRow row = sh.createRow(0);
		row.createCell((short)1).setCellValue(1.2);
	    row.createCell((short)2).setCellValue(new HSSFRichTextString("This is a string"));
	    row.createCell((short)3).setCellValue(true);
	    FileOutputStream f;
		try {
			f = new FileOutputStream("C:\\Users\\Iván\\Documents\\HC\\test.xls");
		    wb.write(f);
		    f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
