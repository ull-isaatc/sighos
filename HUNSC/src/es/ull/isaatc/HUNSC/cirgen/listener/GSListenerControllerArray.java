/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.listener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import es.ull.isaatc.HUNSC.cirgen.GSExcelInputWrapper;
import es.ull.isaatc.HUNSC.cirgen.OperationTheatreType;
import es.ull.isaatc.HUNSC.cirgen.OperationTheatre;
import es.ull.isaatc.HUNSC.cirgen.PatientType;
import es.ull.isaatc.HUNSC.cirgen.util.ExcelTools;
import es.ull.isaatc.simulation.listener.EventListener;
import es.ull.isaatc.util.Statistics;


/**
 * A kind of experiment-attached listener controller, used for presenting the
 * summary of the results. It can be used to create an Excel workbook which 
 * contains the summary of the results from all the simulations performed.
 * @author Iván Castilla Rodríguez
 */
public class GSListenerControllerArray {
	private final static int DONE = 0;
	private final static String []ELEM_TIME_HEADERS = {"Diagnóstico", "Real", "Media", "Desv."};
	private final static String []RES_USAGE_HEADERS = {"Quirófano", "Uso (real)", "Media", "Desv."};
	private final static String []RES_AVAIL_HEADERS = {"Quirófano", "Disp. (real)", "Media", "Desv."};
	private final static String []ELEM_WAIT_HEADERS = {"Resultado", "Media", "Desv."};

	private GSElementTypeTimeListener []timeList;
	private GSElementTypeWaitListener []waitList;
	private GSResourceStdUsageListener []resList;
	private GSListenerController []controllers;
	private CountDownLatch allCompleted;
	private int nExp;
	private HSSFWorkbook wb;
	private HSSFCellStyle headStyle;
	private GSExcelInputWrapper input;
	
	public GSListenerControllerArray(GSExcelInputWrapper input) {
		this.input = input;
		this.nExp = input.getNExperiments();
		allCompleted = new CountDownLatch(nExp);
		timeList = new GSElementTypeTimeListener[nExp];
		waitList = new GSElementTypeWaitListener[nExp];
		resList = new GSResourceStdUsageListener[nExp];
		controllers = new GSListenerController[nExp];
		for (int i = 0; i < nExp; i++) {
			timeList[i] = new GSElementTypeTimeListener(input);
			waitList[i] = new GSElementTypeWaitListener(input);
			resList[i] = new GSResourceStdUsageListener(input);
			controllers[i] = new GSListenerController(this, input.getOutputPath() + input.getOutputFileName() + (i + DONE) + ExcelTools.EXT, 
					new EventListener[] {timeList[i], waitList[i], resList[i]});
		}
		// create a new workbook
		wb = new HSSFWorkbook();
		headStyle = ExcelTools.getHeadStyle(wb);
	}
	
	public GSListenerController getController(int index) {
		return controllers[index];
	}

	private void writeHeader(String []headers, HSSFRow r) {
		int col = 0;
		for (; col < headers.length; col++) {
			HSSFCell c = r.createCell((short)col);
			c.setCellValue(new HSSFRichTextString(headers[col]));
			c.setCellStyle(headStyle);
		}
		for (; col < headers.length + nExp; col++) {
			HSSFCell c = r.createCell((short)col);
			c.setCellValue(new HSSFRichTextString("Exp" + (col - headers.length)));
			c.setCellStyle(headStyle);
		}		
	}
	
	private void writeStatistics(double []values, HSSFRow r, short column) {
		double mean = Statistics.average(values);
		r.createCell(column++).setCellValue(mean);
		r.createCell(column).setCellValue(Statistics.stdDev(values, mean));
		
	}
	
	private void writeSummary(HSSFRow r, double thValue, double []values) {
		short column = 0;
		r.createCell(column++).setCellValue(new HSSFRichTextString("Total"));
		r.createCell(column++).setCellValue(thValue);
		writeStatistics(values, r, column);
		column += 2;
		for (double val : values)
			r.createCell(column++).setCellValue(val);
			
	}
	
	private void writeTimeListenerCreatedResults() {
		HSSFSheet s = wb.createSheet("Creados");
		int rownum = 0;

		for (OperationTheatreType type : OperationTheatreType.values()) {
			double totalExp[] = new double[timeList.length];
			int totalTh = 0;
			HSSFRow r = s.createRow(rownum++);
			r.createCell((short)0).setCellValue(new HSSFRichTextString(type.getName()));
			writeHeader(ELEM_TIME_HEADERS, s.createRow(rownum++));
			for (PatientType pt : input.getPatientTypes()) {
				short column = 0;
				double [] values = new double[timeList.length];
				r = s.createRow(rownum++);
				r.createCell(column++).setCellValue(new HSSFRichTextString(pt.getName()));
				r.createCell(column++).setCellValue(pt.getTotal(type));
				totalTh += pt.getTotal(type);
				column += 2;
				for (GSElementTypeTimeListener list : timeList) {
					int total = 0;
					if (pt.getTotal(type) > 0) {
						for (int value : list.getElementTypeTimes().get(pt.getIndex(type)).getCreatedElement()) {
							values[column - ELEM_TIME_HEADERS.length] = value;
							totalExp[column - ELEM_TIME_HEADERS.length] += value;
							total += value;
						}
					}
					r.createCell(column++).setCellValue(total);
				}
				writeStatistics(values, r, (short)2);
			}
			writeSummary(s.createRow(rownum++), totalTh, totalExp);
			
			rownum ++;
		}
	}
	
	private void writeTimeListenerFinishedResults() {
		HSSFSheet s = wb.createSheet("Terminados");
		int rownum = 0;

		for (OperationTheatreType type : OperationTheatreType.values()) {
			double totalExp[] = new double[timeList.length];
			int totalTh = 0;
			HSSFRow r = s.createRow(rownum++);
			r.createCell((short)0).setCellValue(new HSSFRichTextString(type.getName()));
			writeHeader(ELEM_TIME_HEADERS, s.createRow(rownum++));
			for (PatientType pt : input.getPatientTypes()) {
				short column = 0;
				double [] values = new double[timeList.length];
				r = s.createRow(rownum++);
				r.createCell(column++).setCellValue(new HSSFRichTextString(pt.getName()));
				r.createCell(column++).setCellValue(pt.getTotal(type));
				totalTh += pt.getTotal(type);
				column += 2;
				for (GSElementTypeTimeListener list : timeList) {
					int total = 0;
					if (pt.getTotal(type) > 0) {
						for (int value : list.getElementTypeTimes().get(pt.getIndex(type)).getFinishedElement()) {
							values[column - ELEM_TIME_HEADERS.length] = value;
							totalExp[column - ELEM_TIME_HEADERS.length] += value;
							total += value;
						}
					}
					r.createCell(column++).setCellValue(total);
				}
				writeStatistics(values, r, (short)2);
			}
			writeSummary(s.createRow(rownum++), totalTh, totalExp);
			
			rownum ++;
		}
	}
	
	private void writeTimeListenerWorkingResults() {
		HSSFSheet s = wb.createSheet("Tiempo paciente");
		int rownum = 0;


		for (OperationTheatreType type : OperationTheatreType.values()) {
			double totalExp[] = new double[timeList.length];
			double totalTh = 0;
			HSSFRow r = s.createRow(rownum++);
			r.createCell((short)0).setCellValue(new HSSFRichTextString(type.getName()));
			writeHeader(ELEM_TIME_HEADERS, s.createRow(rownum++));
			for (PatientType pt : input.getPatientTypes()) {
				short column = 0;
				double [] values = new double[timeList.length];
				r = s.createRow(rownum++);
				r.createCell(column++).setCellValue(new HSSFRichTextString(pt.getName()));
				r.createCell(column++).setCellValue(pt.getTotalTime(type));
				totalTh += pt.getTotalTime(type);
				column += 2;
				for (GSElementTypeTimeListener list : timeList) {
					double total = 0;
					if (pt.getTotal(type) > 0) {
						for (double value : list.getElementTypeTimes().get(pt.getIndex(type)).getWorkTime()) {
							values[column - ELEM_TIME_HEADERS.length] = value;
							totalExp[column - ELEM_TIME_HEADERS.length] += value;
							total += value;
						}
					}
					r.createCell(column++).setCellValue(total);
				}
				writeStatistics(values, r, (short)2);
			}
			writeSummary(s.createRow(rownum++), totalTh, totalExp);
			
			rownum ++;
		}
	}
	
	private void writeWaitListenerResults() {
		HSSFSheet s = wb.createSheet("Espera pacientes");
		
		int numrow = 0;
		for (OperationTheatreType type : OperationTheatreType.values()) {
			s.createRow(numrow++).createCell((short)0).setCellValue(new HSSFRichTextString(type.getName()));
			writeHeader(ELEM_WAIT_HEADERS, s.createRow(numrow++));			
			int rowaux = numrow;
			s.createRow(numrow++).createCell((short)0).setCellValue(new HSSFRichTextString("Media"));
			s.createRow(numrow++).createCell((short)0).setCellValue(new HSSFRichTextString("Desv."));
			numrow++;
			for (String day : GSElementTypeWaitListener.REFDAYS)
				s.createRow(numrow++).createCell((short)0).setCellValue(new HSSFRichTextString(day));
			double []meanValues = new double[waitList.length];
			double []devValues = new double[waitList.length];
			double [][]refDays = new double[GSElementTypeWaitListener.REFDAYS.length][waitList.length];
			for (short column = (short)ELEM_WAIT_HEADERS.length; column < waitList.length + ELEM_WAIT_HEADERS.length; column++) {
				numrow = rowaux;
				meanValues[column - ELEM_WAIT_HEADERS.length] = waitList[column - ELEM_WAIT_HEADERS.length].getAverages()[type.ordinal()];
				s.getRow(numrow++).createCell(column).setCellValue(meanValues[column - ELEM_WAIT_HEADERS.length]);
				devValues[column - ELEM_WAIT_HEADERS.length] = waitList[column - ELEM_WAIT_HEADERS.length].getStddevs()[type.ordinal()];
				s.getRow(numrow++).createCell(column).setCellValue(devValues[column - ELEM_WAIT_HEADERS.length]);
				numrow++;
				for (int i = 0; i < GSElementTypeWaitListener.REFDAYS.length; i++) {
					refDays[i][column - ELEM_WAIT_HEADERS.length] = waitList[column - ELEM_WAIT_HEADERS.length].getWaitingDays()[type.ordinal()][i];
					s.getRow(numrow++).createCell(column).setCellValue(refDays[i][column - ELEM_WAIT_HEADERS.length]);
				}
			}
			writeStatistics(meanValues, s.getRow(rowaux++), (short)1);
			writeStatistics(devValues, s.getRow(rowaux++), (short)1);
			rowaux++;
			for (int i = 0; i < GSElementTypeWaitListener.REFDAYS.length; i++)
				writeStatistics(refDays[i], s.getRow(rowaux++), (short)1);
			numrow++;
		}
	}

	private void writeResorceResults() {
		HSSFSheet s = wb.createSheet("Recursos");
		HSSFRow r;
		int rownum = 1;
		writeHeader(RES_USAGE_HEADERS, s.createRow(rownum++));
	    for (OperationTheatre res : input.getOpTheatres()) {
			r = s.createRow(res.getIndex() + rownum);
			r.createCell((short)0).setCellValue(new HSSFRichTextString(res.getName()));
			r.createCell((short)1).setCellValue(res.getRealUsage());
	    }
    	short column = (short)RES_USAGE_HEADERS.length;
    	double [][] values = new double [input.getOpTheatres().length][resList.length];
	    for (GSResourceStdUsageListener l : resList) {
	    	for (OperationTheatre res : input.getOpTheatres()) {
	    		r = s.getRow(res.getIndex() + rownum);
	    		values[res.getIndex()][column - RES_USAGE_HEADERS.length] = l.getResUsageSummary()[res.getIndex()];
	    		r.createCell(column).setCellValue(values[res.getIndex()][column - RES_USAGE_HEADERS.length]);
	    	}
	    	column++;
	    }
	    for (OperationTheatre res : input.getOpTheatres())
			writeStatistics(values[res.getIndex()], s.getRow(res.getIndex() + rownum), (short)2);
	    rownum += input.getOpTheatres().length + 1;
	    
		writeHeader(RES_AVAIL_HEADERS, s.createRow(rownum++));
	    for (OperationTheatre res : input.getOpTheatres()) {
			r = s.createRow(res.getIndex() + rownum);
			r.createCell((short)0).setCellValue(new HSSFRichTextString(res.getName()));
			r.createCell((short)1).setCellValue(res.getRealAva());
	    }
    	column = (short)RES_AVAIL_HEADERS.length;
	    for (GSResourceStdUsageListener l : resList) {
	    	for (OperationTheatre res : input.getOpTheatres()) {
	    		r = s.getRow(res.getIndex() + rownum);
	    		values[res.getIndex()][column - RES_AVAIL_HEADERS.length] = l.getResAvailabilitySummary()[res.getIndex()];
	    		r.createCell(column).setCellValue(values[res.getIndex()][column - RES_AVAIL_HEADERS.length]);
	    	}
	    	column++;
	    }	    
	    for (OperationTheatre res : input.getOpTheatres())
			writeStatistics(values[res.getIndex()], s.getRow(res.getIndex() + rownum), (short)2);
		
	}
	
	private void writeResourceUsageListenerResults() {
		writeResorceResults();
	}
	
	private void writeTimeListenerResults() {
		writeTimeListenerCreatedResults();
		writeTimeListenerFinishedResults();
		writeTimeListenerWorkingResults();
		writeWaitListenerResults();
		writeResourceUsageListenerResults();
	}
	
	public void notifyEnd() {
		allCompleted.countDown();
	}
	
	public void writeResults(String filename) {
		try {
			// ... and now the controllerArray is waiting for all the controllers to finish
			allCompleted.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		writeTimeListenerResults();
		
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
