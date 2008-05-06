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
import org.apache.poi.hssf.util.HSSFColor;

import es.ull.isaatc.HUNSC.cirgen.AdmissionType;
import es.ull.isaatc.HUNSC.cirgen.GSExcelInputWrapper;
import es.ull.isaatc.HUNSC.cirgen.OperationTheatre;
import es.ull.isaatc.HUNSC.cirgen.PatientCategory;
import es.ull.isaatc.HUNSC.cirgen.PatientType;
import es.ull.isaatc.HUNSC.cirgen.util.ExcelTools;
import es.ull.isaatc.util.Statistics;


/**
 * A kind of experiment-attached listener controller, used for presenting the
 * summary of the results. It can be used to create an Excel workbook which 
 * contains the summary of the results from all the simulations performed.
 * @author Iván Castilla Rodríguez
 */
public class GSListenerControllerArray {
	private final static int DONE = 0;
	private final static String []ELEM_TIME_HEADERS = {"Categoría", "Ambulante", "Tipo", "Real", "Media", "Desv.", "Error rel."};
	private final static String []RES_USAGE_HEADERS = {"Quirófano", "Uso (real)", "Media", "Desv.", "Error rel."};
	private final static String []RES_AVAIL_HEADERS = {"Quirófano", "Disp. (real)", "Media", "Desv.", "Error rel."};
	private final static String []ELEM_WAIT_HEADERS = {"Resultado", "Media", "Desv."};

	private GSElementTypeTimeResults []timeList;
	private GSElementTypeWaitResults []waitList;
	private GSResourceStdUsageResults []resList;
	private CountDownLatch allCompleted;
	private int nExp;
	private HSSFWorkbook wb;
	private HSSFCellStyle headStyle;
	private HSSFCellStyle errorStyle;
	private HSSFCellStyle realStyle;
	private HSSFCellStyle diagStyle;
	private GSExcelInputWrapper input;
	
	public GSListenerControllerArray(GSExcelInputWrapper input) {
		this.input = input;
		this.nExp = input.getNExperiments();
		allCompleted = new CountDownLatch(nExp);
		timeList = new GSElementTypeTimeResults[nExp];
		waitList = new GSElementTypeWaitResults[nExp];
		resList = new GSResourceStdUsageResults[nExp];
		// create a new workbook
		wb = new HSSFWorkbook();
		headStyle = ExcelTools.getHeadStyle(wb);
		errorStyle = ExcelTools.getErrorStyle(wb);
		realStyle = wb.createCellStyle();
		realStyle.setFont(ExcelTools.getBoldFont(wb));
	    realStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
	    realStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    diagStyle = wb.createCellStyle();
	    diagStyle.setFillForegroundColor(HSSFColor.GOLD.index);
	    diagStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    diagStyle.setFont(ExcelTools.getBoldFont(wb));
	}
	
	public GSListenerController getController(int index) {
		return new GSListenerController(this, input.getOutputPath() + input.getOutputFileName() + (index + DONE) + ExcelTools.EXT, 
				new GSListenerArray(index, new GSElementTypeTimeListener(input), new GSElementTypeWaitListener(input), new GSResourceStdUsageListener(input)));
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
	
	private short writeStatistics(String []titles, double thValue, double []values, HSSFRow r) {
		double mean = Statistics.average(values);
		short column = 0;
		for (String title : titles) {
			r.createCell(column).setCellValue(new HSSFRichTextString(title));
			r.getCell(column++).setCellStyle(diagStyle);
		}
		r.createCell(column).setCellValue(thValue);
		r.getCell(column++).setCellStyle(realStyle);
		r.createCell(column++).setCellValue(mean);
		r.createCell(column++).setCellValue(Statistics.stdDev(values, mean));
		if (thValue > 0.0)
			r.createCell(column).setCellValue(Statistics.relError100(thValue, mean));
		else
			r.createCell(column).setCellValue(0);
		r.getCell(column++).setCellStyle(errorStyle);
		return column;
	}
	
	private short writeStatistics(PatientCategory pc, double thValue, double []values, HSSFRow r) {
		String pType = (pc.getPatientType() == PatientType.DC) ? "S":"N"; 
		String aType = (pc.getAdmissionType() == AdmissionType.PROGRAMMED) ? "P":"U"; 
		return writeStatistics(new String[] {pc.getName(), pType, aType}, thValue, values, r);
	}
	
	private void writeSummary(HSSFRow r, double thValue, double []values) {
		short column = writeStatistics(new String[] {"Total", "", ""}, thValue, values, r);
		for (double val : values)
			r.createCell(column++).setCellValue(val);
			
	}
	
	private void writeTimeListenerCreatedResults() {
		HSSFSheet s = wb.createSheet("Creados");
		int rownum = 0;

		try {
		double totalExp[] = new double[timeList.length];
		int totalTh = 0;
		HSSFRow r = s.createRow(rownum++);
		writeHeader(ELEM_TIME_HEADERS, s.createRow(rownum++));
		for (PatientCategory pt : input.getPatientCategories()) {
			short column = (short)ELEM_TIME_HEADERS.length;
			double [] values = new double[timeList.length];
			r = s.createRow(rownum++);
			totalTh += pt.getTotal();
			for (GSElementTypeTimeResults list : timeList) {
				int total = 0;
				if (pt.getTotal() > 0) {
					values[column - ELEM_TIME_HEADERS.length] = list.getCreatedElements()[pt.getIndex()];
					totalExp[column - ELEM_TIME_HEADERS.length] += list.getCreatedElements()[pt.getIndex()];
					total = list.getCreatedElements()[pt.getIndex()];
				}
				r.createCell(column++).setCellValue(total);
			}
			writeStatistics(pt, pt.getTotal(), values, r);
		}
		writeSummary(s.createRow(rownum++), totalTh, totalExp);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	private void writeTimeListenerFinishedResults() {
		HSSFSheet s = wb.createSheet("Terminados");
		int rownum = 0;

		double totalExp[] = new double[timeList.length];
		int totalTh = 0;
		HSSFRow r = s.createRow(rownum++);
		writeHeader(ELEM_TIME_HEADERS, s.createRow(rownum++));
		for (PatientCategory pt : input.getPatientCategories()) {
			short column = (short)ELEM_TIME_HEADERS.length;
			double [] values = new double[timeList.length];
			r = s.createRow(rownum++);
			totalTh += pt.getTotal();
			for (GSElementTypeTimeResults list : timeList) {
				int total = 0;
				if (pt.getTotal() > 0) {
					values[column - ELEM_TIME_HEADERS.length] = list.getFinishedElements()[pt.getIndex()];
					totalExp[column - ELEM_TIME_HEADERS.length] += list.getFinishedElements()[pt.getIndex()];
					total = list.getFinishedElements()[pt.getIndex()];
				}
				r.createCell(column++).setCellValue(total);
			}
			writeStatistics(pt, pt.getTotal(), values, r);
		}
		writeSummary(s.createRow(rownum++), totalTh, totalExp);
	}
	
	private void writeTimeListenerWorkingResults() {
		HSSFSheet s = wb.createSheet("Tiempo paciente");
		int rownum = 0;

		double totalExp[] = new double[timeList.length];
		double totalTh = 0;
		HSSFRow r = s.createRow(rownum++);
		writeHeader(ELEM_TIME_HEADERS, s.createRow(rownum++));
		for (PatientCategory pt : input.getPatientCategories()) {
			short column = (short)ELEM_TIME_HEADERS.length;
			double [] values = new double[timeList.length];
			r = s.createRow(rownum++);
			totalTh += pt.getTotalTime();
			for (GSElementTypeTimeResults list : timeList) {
				double total = 0;
				if (pt.getTotal() > 0) {
					values[column - ELEM_TIME_HEADERS.length] = list.getWorkTime()[pt.getIndex()];
					totalExp[column - ELEM_TIME_HEADERS.length] += list.getWorkTime()[pt.getIndex()];
					total = list.getWorkTime()[pt.getIndex()];
				}
				r.createCell(column++).setCellValue(total);
			}
			writeStatistics(pt, pt.getTotalTime(), values, r);
		}
		writeSummary(s.createRow(rownum++), totalTh, totalExp);
	}
	
	private void writeWaitListenerResults() {
		HSSFSheet s = wb.createSheet("Espera pacientes");
		
		int numrow = 0;
		for (AdmissionType adm : AdmissionType.values())
			for (PatientType type : PatientType.values()) {
				HSSFRow r = s.createRow(numrow++);
				r.createCell((short)0).setCellValue(new HSSFRichTextString(adm.getName()));
				r.getCell((short)0).setCellStyle(realStyle);
				r.createCell((short)1).setCellValue(new HSSFRichTextString(type.getName()));
				r.getCell((short)1).setCellStyle(realStyle);
				
				writeHeader(ELEM_WAIT_HEADERS, s.createRow(numrow++));			
				int rowaux = numrow;
				r = s.createRow(numrow++);
				r.createCell((short)0).setCellValue(new HSSFRichTextString("Media"));
				r.getCell((short)0).setCellStyle(diagStyle);
				r = s.createRow(numrow++);
				r.createCell((short)0).setCellValue(new HSSFRichTextString("Desv."));
				r.getCell((short)0).setCellStyle(diagStyle);
				numrow++;
				for (String day : GSElementTypeWaitListener.REFDAYS) {
					r = s.createRow(numrow++);
					r.createCell((short)0).setCellValue(new HSSFRichTextString(day));
					r.getCell((short)0).setCellStyle(diagStyle);					
				}
				
				double []meanValues = new double[waitList.length];
				double []devValues = new double[waitList.length];
				double [][]refDays = new double[GSElementTypeWaitListener.REFDAYS.length][waitList.length];
				for (short column = (short)ELEM_WAIT_HEADERS.length; column < waitList.length + ELEM_WAIT_HEADERS.length; column++) {
					numrow = rowaux;
					meanValues[column - ELEM_WAIT_HEADERS.length] = waitList[column - ELEM_WAIT_HEADERS.length].getAverages()[adm.ordinal()][type.ordinal()];
					s.getRow(numrow++).createCell(column).setCellValue(meanValues[column - ELEM_WAIT_HEADERS.length]);
					devValues[column - ELEM_WAIT_HEADERS.length] = waitList[column - ELEM_WAIT_HEADERS.length].getStddevs()[adm.ordinal()][type.ordinal()];
					s.getRow(numrow++).createCell(column).setCellValue(devValues[column - ELEM_WAIT_HEADERS.length]);
					numrow++;
					for (int i = 0; i < GSElementTypeWaitListener.REFDAYS.length; i++) {
						refDays[i][column - ELEM_WAIT_HEADERS.length] = waitList[column - ELEM_WAIT_HEADERS.length].getWaitingDays()[adm.ordinal()][type.ordinal()][i];
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

    	double [][] values = new double [input.getOpTheatres().length][resList.length];
	    for (OperationTheatre res : input.getOpTheatres()) {
			r = s.createRow(rownum++);
	    	for (int i = 0; i < resList.length; i++) {
	    		values[res.getIndex()][i] = resList[i].getResUsageSummary()[res.getIndex()];
	    		r.createCell((short)(RES_USAGE_HEADERS.length + i)).setCellValue(values[res.getIndex()][i]);	    		
	    	}	    		
			writeStatistics(new String [] {res.getName()}, res.getRealUsage(), values[res.getIndex()], r);
	    }
	    
	    rownum++;
	    
		writeHeader(RES_AVAIL_HEADERS, s.createRow(rownum++));

	    for (OperationTheatre res : input.getOpTheatres()) {
			r = s.createRow(rownum++);
	    	for (int i = 0; i < resList.length; i++) {
	    		values[res.getIndex()][i] = resList[i].getResAvailabilitySummary()[res.getIndex()];
	    		r.createCell((short)(RES_AVAIL_HEADERS.length + i)).setCellValue(values[res.getIndex()][i]);	    		
	    	}	    		
			writeStatistics(new String[] {res.getName()}, res.getRealAva(), values[res.getIndex()], r);
	    }
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
	
	public void notifyEnd(GSListenerArray res) {
		timeList[res.getIndex()] = (GSElementTypeTimeResults) res.getTimeListener().getResults();
		waitList[res.getIndex()] = (GSElementTypeWaitResults) res.getWaitListener().getResults();
		resList[res.getIndex()] = (GSResourceStdUsageResults) res.getResListener().getResults();
		
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
