package es.ull.isaatc.simulation.benchmark;
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
public class NewResultFormatter {
//	public static int[]NPROCS = {1, 2, 4, 8};
	public static int[]NTHREADS = {1, 2, 4, 8, 16, 32, 64, 128};
	public static int[]NACTS = {1, 2, 4, 8, 16, 32};
	public static int[]NELEM = {4096, 8192, 16384};
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filename = "resul1.xls";
		String dir = "S:\\simulacion\\Benchmark\\Iris ubuntu\\200 iter 8 proc\\";
		
		// create a new workbook
		HSSFWorkbook wb = new HSSFWorkbook();

		for (int nElem : NELEM) {
			HSSFSheet s = wb.createSheet("Resul " + nElem);
			// Prepare resume box
			int rownum = 0;
			HSSFRow r = s.createRow(rownum++);			
			HSSFCell c = r.createCell((short)0);
			c.setCellValue(new HSSFRichTextString("Summary"));
	
			r = s.createRow(rownum++);
			c = r.createCell((short)0);
			c.setCellValue(new HSSFRichTextString("Threads"));
			c = r.createCell((short)1);
			c.setCellValue(new HSSFRichTextString("Activities"));
			
			r = s.createRow(rownum++);
			short col = 1;
			for (int nAct : NACTS) {
				c = r.createCell((short)col++);
				c.setCellValue(nAct);
			}
			for (int nTh : NTHREADS) {				
				r = s.createRow(rownum++);
				c = r.createCell((short)0);
				c.setCellValue(nTh);				
				BufferedReader[] inFiles = new BufferedReader[NACTS.length];
				for (int i = 0; i < NACTS.length; i++) {
					try {
						inFiles[i] = new BufferedReader(new FileReader(dir + "pa" + NACTS[i] + "th" + nTh + "" + nElem + ".txt"));
						c = r.createCell((short)(i+1));
						c.setCellValue(Double.parseDouble(inFiles[i].readLine()));
						inFiles[i].close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		try {
			FileOutputStream out = new FileOutputStream(dir + filename);
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
