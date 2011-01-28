package es.ull.isaatc.simulation.benchmark;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

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
public class TimesResultFormatter {
	public static int[]NPROCS = {1,2,4,8};
	public static int[]NTHREADS = {1, 2, 4, 8, 16, 32, 64, 128};
	public static int[]NACTS = {1, 2, 4, 8,16,32};
//	public static int[]NELEM = {4096};
//	public static int[]NTHREADS = {1, 2, 4, 8, 16, 32, 64, 128};
//	public static int[]NACTS = {1, 2, 4, 8, 16, 32};
	public static int[]NELEM = {4096, 8192, 16384};
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filename = "resul1.xls";
		String dir = "S:\\simulacion\\Benchmark\\Iris\\ubuntu\\";
		
		// create a new workbook
		HSSFWorkbook wb = new HSSFWorkbook();

		for (int nProc : NPROCS) {
			for (int nElem : NELEM) {
				HSSFSheet s = wb.createSheet("Resul " + nElem + " " + nProc + " PROC");
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
				HSSFRow rLp = s.createRow(rownum + 1 + NTHREADS.length);
				HSSFRow rEv = s.createRow(rownum + 3 + 2 * NTHREADS.length);
				HSSFRow rEvRaw = s.createRow(rownum + 5 + 3 * NTHREADS.length);
				short col = 1;
				for (int nAct : NACTS) {
					c = r.createCell(col);
					c.setCellValue(nAct);
					HSSFCell cLp = rLp.createCell(col);
					cLp.setCellValue(nAct);
					HSSFCell cEv = rEv.createCell(col);
					cEv.setCellValue(nAct);
					HSSFCell cEvRaw = rEvRaw.createCell(col);
					cEvRaw.setCellValue(nAct);
					col++;
				}
				for (int nTh : NTHREADS) {				
					r = s.createRow(rownum++);
					rLp = s.createRow(rownum + 1 + NTHREADS.length);
					rEv = s.createRow(rownum + 3 + 2 * NTHREADS.length);
					rEvRaw = s.createRow(rownum + 5 + 3 * NTHREADS.length);
					c = r.createCell((short)0);
					HSSFCell cLp = rLp.createCell((short)0);
					HSSFCell cEv = rEv.createCell((short)0);
					HSSFCell cEvRaw = rEvRaw.createCell((short)0);
					c.setCellValue(nTh);				
					cLp.setCellValue(nTh);				
					cEv.setCellValue(nTh);				
					cEvRaw.setCellValue(nTh);				
					for (int i = 0; i < NACTS.length; i++) {
						try {
							BufferedReader inFiles = new BufferedReader(new FileReader(dir + "p" + nProc + "a" + NACTS[i] + "th" + nTh + "" + nElem + ".txt"));
							StringTokenizer tok = new StringTokenizer(inFiles.readLine());
							c = r.createCell((short)(i+1));
							c.setCellValue(Double.parseDouble(tok.nextToken()));
							cLp = rLp.createCell((short)(i+1));
							cLp.setCellValue(Double.parseDouble(tok.nextToken()));
							cEv = rEv.createCell((short)(i+1));
							cEv.setCellValue(Double.parseDouble(tok.nextToken()));
							cEvRaw = rEvRaw.createCell((short)(i+1));
							cEvRaw.setCellValue(Double.parseDouble(tok.nextToken()));
							inFiles.close();
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
