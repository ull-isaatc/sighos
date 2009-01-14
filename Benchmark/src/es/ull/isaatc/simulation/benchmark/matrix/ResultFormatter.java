package es.ull.isaatc.simulation.benchmark.matrix;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Creates an Excel file containing the formatted results from the outputs of the benchmarks 
 * done in the "iris" server. This class has been made ad-hoc so it's not specially redistributable 
 * nor scalable.<p>
 * The class needs an output filename and an input directory. The input directory is supposed 
 * to contain a set of files, corresponding to tests done with different configurations. The 
 * expected configurations are:
 * <ul>
 * <li>Number of active processors: 1, 2, 4, 8</li>   
 * <li>Number of threads in the pool: 1, 2, 4, 8, 16, 32, 64, 128</li>   
 * <li>Number of activities in the model: 1, 2, 4, 8, 16, 32</li>
 * </ul>
 * Normally, each directory corresponds to a number of elements: 2000, 8192 and 16384 are the 
 * expected number of elements used. If resource conflict is the test done, 128 and 256 are the 
 * elements used.
 *    
 * @author Iván Castilla Rodríguez
 *
 */
public class ResultFormatter {
	public static int[]NPROCS = {1, 2, 4, 8};
	public static int[]NTHREADS = {1, 2, 4, 8, 16, 32, 64, 128};
	public static int INITROW = 17;
	public static int NELEM = 512;
	public static int MAXVAL = 133;
	public static int NEXP = 10;
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filename = "c:\\resul1.xls";
		String dir = "S:\\simulacion\\Benchmark\\Iris\\m1_" + NELEM + "_" + MAXVAL + "\\";
		
		// create a new workbook
		HSSFWorkbook wb = new HSSFWorkbook();
		
		// create a new sheet
		HSSFSheet s = wb.createSheet("results");
		// Prepare resume box
		int rownum = 0;
		HSSFRow r = s.createRow(rownum++);			
		HSSFCell c = r.createCell((short)0);
		c.setCellValue(new HSSFRichTextString("Summary"));

		r = s.createRow(rownum++);
		c = r.createCell((short)0);
		c.setCellValue(new HSSFRichTextString("Threads"));
		c = r.createCell((short)1);
		c.setCellValue(new HSSFRichTextString("Processors"));

		r = s.createRow(rownum++);
		short col = 1;
		for (int nAct : NPROCS) {
			c = r.createCell((short)col++);
			c.setCellValue(nAct);
		}
		int resRow = rownum;
		for (int nTh : NTHREADS) {
			r = s.createRow(rownum++);
			c = r.createCell((short)0);
			c.setCellValue(nTh);				
		}
		
		rownum = INITROW;
		r = s.createRow(rownum++);			
		c = r.createCell((short)0);
		c.setCellValue(new HSSFRichTextString("Processors"));
		col = 1;
		for (int nAct : NPROCS) {
			c = r.createCell((short)col++);
			c.setCellValue(nAct);
		}
		r = s.createRow(rownum);			
		c = r.createCell((short)0);
		c.setCellValue(new HSSFRichTextString("Threads"));
		for (int nTh : NTHREADS) {				
			r = s.createRow(++rownum);
			c = r.createCell((short)0);
			c.setCellValue(nTh);
			BufferedReader[] inFiles = new BufferedReader[NPROCS.length];
			for (int i = 0; i < NPROCS.length; i++) {
				try {
					inFiles[i] = new BufferedReader(new FileReader(dir + "m" + NELEM + "_" + MAXVAL + "p" + NPROCS[i] + "th" + nTh + ".txt"));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			double [][] results = new double[NPROCS.length][NEXP];
			for (int j = 0; j < NEXP; j++) {
				for (int i = 0; i < NPROCS.length; i++) {
					c = r.createCell((short)(i+1));
					try {
						results[i][j] = Double.parseDouble(inFiles[i].readLine());
						c.setCellValue(results[i][j]);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				r = s.createRow(++rownum);
			}
			for (int i = 0; i < NPROCS.length; i++) {
				try {
					inFiles[i].close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				r = s.getRow(resRow);
				String form = "AVERAGE(" + (char)('B' + i) + (INITROW + 3 + (resRow - 3) * (NEXP + 1)) + ":" + (char)('B' + i) + (INITROW + 12 + (resRow - 3) * (NEXP + 1)) + ")";
				r.createCell((short)(i + 1)).setCellFormula(form);
			}
			resRow++;
		}
		try {
			FileOutputStream out = new FileOutputStream(filename);
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
