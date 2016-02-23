/**
 * 
 */
package es.ull.iis.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

/**
 * @author Iván
 *
 */
public class ExcelTools {
	public final static String EXT = ".xls"; 
	
	/**
	 * Creates an Excel font which is bold.
	 * @param wb Workbook where the font is used.
	 * @return A bold Excel font.
	 */
	public static HSSFFont getBoldFont(HSSFWorkbook wb) {
	    HSSFFont boldFont = wb.createFont();
	    boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		return boldFont;
	}

	/**
	 * Creates an Excel style to remark error cells.
	 * @param wb Workbook where the style is used.
	 * @return A red and italic font.
	 */
	public static HSSFCellStyle getErrorStyle(HSSFWorkbook wb) {
	    HSSFFont redFont = wb.createFont();
	    redFont.setColor(HSSFColor.RED.index);
	    redFont.setItalic(true);
	    HSSFCellStyle errorStyle = wb.createCellStyle();
	    errorStyle.setFont(redFont);
	    return errorStyle;
	}
	
	/**
	 * Creates an Excel style to remark header cells.
	 * @param wb Workbook where the style is used.
	 * @return A grey background and a bold font. 
	 */
	public static HSSFCellStyle getHeadStyle(HSSFWorkbook wb) {
	    HSSFCellStyle headStyle = wb.createCellStyle();
	    headStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
	    headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    headStyle.setWrapText(true);
	    headStyle.setFont(getBoldFont(wb));
	    return headStyle;
	}
	
	public static String getString(HSSFSheet s, int row, short column) {
		return s.getRow(row).getCell(column).getRichStringCellValue().getString();
	}

	public static String getString(HSSFRow r, short column) {
		String res = "";
		if (r.getCell(column).getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
			res = String.valueOf((int)r.getCell(column).getNumericCellValue());
		else
			res = r.getCell(column).getRichStringCellValue().getString(); 
		return res;
	}
	
	public static boolean validCell(HSSFRow r, short column) {
		if (r.getCell(column) == null)
			return false;
		if (r.getCell(column).getCellType() == HSSFCell.CELL_TYPE_BLANK)
			return false;
		return true;
	}
}
