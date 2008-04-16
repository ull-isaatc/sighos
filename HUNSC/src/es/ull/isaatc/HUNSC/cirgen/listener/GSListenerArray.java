/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.listener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import es.ull.isaatc.HUNSC.cirgen.SimGS;
import es.ull.isaatc.simulation.listener.EventListener;
import es.ull.isaatc.util.Statistics;


/**
 * @author Iván
 *
 */
public class GSListenerArray {
	private final static int FIXTIMECOLS = 4;
	private GSElementTypeTimeListener []timeList;
	private GSElementTypeWaitListener []waitList;
	private GSResourceStdUsageListener []resList;
	
	public GSListenerArray(int nExp, double period) {
		timeList = new GSElementTypeTimeListener[nExp];
		waitList = new GSElementTypeWaitListener[nExp];
		resList = new GSResourceStdUsageListener[nExp];
		for (int i = 0; i < nExp; i++) {
			timeList[i] = new GSElementTypeTimeListener(period);
			waitList[i] = new GSElementTypeWaitListener();
			resList[i] = new GSResourceStdUsageListener(period);
		}
	}
	
	public EventListener[] getListeners(int index) {
		EventListener[] res = new EventListener[3];
		res[0] = timeList[index];
		res[1] = resList[index];
		res[2] = waitList[index];
		return res; 
	}

	private void writeTimeListenerHeader(HSSFRow r) {
		r.createCell((short)0).setCellValue(new HSSFRichTextString("Diagnóstico"));
		r.createCell((short)1).setCellValue(new HSSFRichTextString("Real"));
		r.createCell((short)2).setCellValue(new HSSFRichTextString("Media"));
		r.createCell((short)3).setCellValue(new HSSFRichTextString("Desv."));
		for (short column = FIXTIMECOLS; column < timeList.length + FIXTIMECOLS; column++)
			r.createCell(column).setCellValue(new HSSFRichTextString("Exp" + (column - FIXTIMECOLS)));	
	}
	
	private void writeTimeListenerCreatedResults(HSSFWorkbook wb) {
		HSSFSheet s = wb.createSheet("Creados");
		int rownum = 0;
		HSSFRow r = s.createRow(rownum++);
		r.createCell((short)0).setCellValue(new HSSFRichTextString("No ambulantes"));
		r = s.createRow(rownum++);
		writeTimeListenerHeader(r);
		rownum = 2;
		for (SimGS.PatientType pt : SimGS.PatientType.values()) {
			short column = 0;
			int [] values = new int[timeList.length];
			r = s.createRow(rownum++);
			r.createCell(column++).setCellValue(new HSSFRichTextString(pt.getName()));
			r.createCell(column++).setCellValue(pt.getTotal() * pt.getPercOR());
			column += 2;
			for (GSElementTypeTimeListener list : timeList) {
				int total = 0;
				if (pt.getPercOR() > 0.0) {
					for (int value : list.getElementTypeTimes().get(pt.ordinal()).getCreatedElement()) {
						values[column - FIXTIMECOLS] = value;
						total += value;
					}
				}
				r.createCell(column++).setCellValue(total);
			}
			r.createCell((short)2).setCellValue(Statistics.average(values));
			r.createCell((short)3).setCellValue(Statistics.stdDev(values));
		}
		
		rownum ++;
		
		r = s.createRow(rownum++);
		r.createCell((short)0).setCellValue(new HSSFRichTextString("Ambulantes"));
		r = s.createRow(rownum++);
		writeTimeListenerHeader(r);
		for (SimGS.PatientType pt : SimGS.PatientType.values()) {
			short column = 0;
			int [] values = new int[timeList.length];
			r = s.createRow(rownum++);
			r.createCell(column++).setCellValue(new HSSFRichTextString(pt.getName()));
			r.createCell(column++).setCellValue(pt.getTotal() * (1 - pt.getPercOR()));
			column += 2;
			for (GSElementTypeTimeListener list : timeList) {
				int total = 0;
				if (pt.getPercOR() < 1.0) {
					for (int value : list.getElementTypeTimes().get(pt.ordinal() + SimGS.PatientType.values().length).getCreatedElement()) {
						values[column - FIXTIMECOLS] = value;
						total += value;
					}
				}
				r.createCell(column++).setCellValue(total);
			}
			r.createCell((short)2).setCellValue(Statistics.average(values));
			r.createCell((short)3).setCellValue(Statistics.stdDev(values));
		}		
	}
	
	private void writeTimeListenerFinishedResults(HSSFWorkbook wb) {
		HSSFSheet s = wb.createSheet("Terminados");
		int rownum = 0;
		HSSFRow r = s.createRow(rownum++);
		r.createCell((short)0).setCellValue(new HSSFRichTextString("No ambulantes"));
		r = s.createRow(rownum++);
		writeTimeListenerHeader(r);
		rownum = 2;
		for (SimGS.PatientType pt : SimGS.PatientType.values()) {
			short column = 0;
			int [] values = new int[timeList.length];
			r = s.createRow(rownum++);
			r.createCell(column++).setCellValue(new HSSFRichTextString(pt.getName()));
			r.createCell(column++).setCellValue(pt.getTotal() * pt.getPercOR());
			column += 2;
			for (GSElementTypeTimeListener list : timeList) {
				int total = 0;
				if (pt.getPercOR() > 0.0) {
					for (int value : list.getElementTypeTimes().get(pt.ordinal()).getFinishedElement()) {
						values[column - FIXTIMECOLS] = value;
						total += value;
					}
				}
				r.createCell(column++).setCellValue(total);
			}
			r.createCell((short)2).setCellValue(Statistics.average(values));
			r.createCell((short)3).setCellValue(Statistics.stdDev(values));
		}
		
		rownum ++;
		
		r = s.createRow(rownum++);
		r.createCell((short)0).setCellValue(new HSSFRichTextString("Ambulantes"));
		r = s.createRow(rownum++);
		writeTimeListenerHeader(r);
		for (SimGS.PatientType pt : SimGS.PatientType.values()) {
			short column = 0;
			int [] values = new int[timeList.length];
			r = s.createRow(rownum++);
			r.createCell(column++).setCellValue(new HSSFRichTextString(pt.getName()));
			r.createCell(column++).setCellValue(pt.getTotal() * (1 - pt.getPercOR()));
			column += 2;
			for (GSElementTypeTimeListener list : timeList) {
				int total = 0;
				if (pt.getPercOR() < 1.0) {
					for (int value : list.getElementTypeTimes().get(pt.ordinal() + SimGS.PatientType.values().length).getFinishedElement()) {
						values[column - FIXTIMECOLS] = value;
						total += value;
					}
				}
				r.createCell(column++).setCellValue(total);
			}
			r.createCell((short)2).setCellValue(Statistics.average(values));
			r.createCell((short)3).setCellValue(Statistics.stdDev(values));
		}		
	}
	
	private void writeTimeListenerWorkingResults(HSSFWorkbook wb) {
		HSSFSheet s = wb.createSheet("Tiempo paciente");
		int rownum = 0;
		HSSFRow r = s.createRow(rownum++);
		r.createCell((short)0).setCellValue(new HSSFRichTextString("No ambulantes"));
		r = s.createRow(rownum++);
		writeTimeListenerHeader(r);
		rownum = 2;
		for (SimGS.PatientType pt : SimGS.PatientType.values()) {
			short column = 0;
			double [] values = new double[timeList.length];
			r = s.createRow(rownum++);
			r.createCell(column++).setCellValue(new HSSFRichTextString(pt.getName()));
			r.createCell(column++).setCellValue((pt.getTotal() * pt.getPercOR()) * pt.getAvgOR());
			column += 2;
			for (GSElementTypeTimeListener list : timeList) {
				double total = 0;
				if (pt.getPercOR() > 0.0) {
					for (double value : list.getElementTypeTimes().get(pt.ordinal()).getWorkTime()) {
						values[column - FIXTIMECOLS] = value;
						total += value;
					}
				}
				r.createCell(column++).setCellValue(total);
			}
			r.createCell((short)2).setCellValue(Statistics.average(values));
			r.createCell((short)3).setCellValue(Statistics.stdDev(values));
		}
		
		rownum ++;
		
		r = s.createRow(rownum++);
		r.createCell((short)0).setCellValue(new HSSFRichTextString("Ambulantes"));
		r = s.createRow(rownum++);
		writeTimeListenerHeader(r);
		for (SimGS.PatientType pt : SimGS.PatientType.values()) {
			short column = 0;
			double [] values = new double[timeList.length];
			r = s.createRow(rownum++);
			r.createCell(column++).setCellValue(new HSSFRichTextString(pt.getName()));
			r.createCell(column++).setCellValue((pt.getTotal() * (1 - pt.getPercOR())) * pt.getAvgDC());
			column += 2;
			for (GSElementTypeTimeListener list : timeList) {
				double total = 0;
				if (pt.getPercOR() < 1.0) {
					for (double value : list.getElementTypeTimes().get(pt.ordinal() + SimGS.PatientType.values().length).getWorkTime()) {
						values[column - FIXTIMECOLS] = value;
						total += value;
					}
				}
				r.createCell(column++).setCellValue(total);
			}
			r.createCell((short)2).setCellValue(Statistics.average(values));
			r.createCell((short)3).setCellValue(Statistics.stdDev(values));
		}		
	}
	
	private void writeTimeListenerResults(HSSFWorkbook wb) {
		writeTimeListenerCreatedResults(wb);
		writeTimeListenerFinishedResults(wb);
		writeTimeListenerWorkingResults(wb);
	}
	
	public void writeResults(String filename) {
		// create a new workbook
		HSSFWorkbook wb = new HSSFWorkbook();
		writeTimeListenerResults(wb);
		
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
