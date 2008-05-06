/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.listener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import es.ull.isaatc.HUNSC.cirgen.AdmissionType;
import es.ull.isaatc.HUNSC.cirgen.GSExcelInputWrapper;
import es.ull.isaatc.HUNSC.cirgen.PatientCategory;
import es.ull.isaatc.HUNSC.cirgen.PatientType;
import es.ull.isaatc.HUNSC.cirgen.util.ExcelTools;
import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.listener.SimulationListener;
import es.ull.isaatc.simulation.listener.SimulationObjectListener;
import es.ull.isaatc.util.Statistics;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class GSElementTypeWaitListener implements GSListener, SimulationObjectListener, SimulationListener {
	public static final String []REFDAYS = {"Espera < 1 día", "Espera < 2 días",
		"Espera < 3 días", "Espera > 3 días"};
	private FirstWaitTime[][] firstWaitTime;
	private GSElementTypeWaitResults results = null;
	private GSExcelInputWrapper input;
	
	public GSElementTypeWaitListener(GSExcelInputWrapper input) {
		this.input = input;
		firstWaitTime = new FirstWaitTime[AdmissionType.values().length][PatientType.values().length];
		for (int i = 0; i < AdmissionType.values().length; i++)
			for (int j = 0; j < PatientType.values().length; j++)
				firstWaitTime[i][j] = new FirstWaitTime();
	}

	public void infoEmited(SimulationEndInfo info) {
		double [][] averages = new double[AdmissionType.values().length][PatientType.values().length];
		double [][] stddevs = new double[AdmissionType.values().length][PatientType.values().length];
		int [][][] waitingDays = new int[AdmissionType.values().length][PatientType.values().length][REFDAYS.length];
		
		for (int i = 0; i < AdmissionType.values().length; i++)
			for (int j = 0; j < PatientType.values().length; j++) {
				firstWaitTime[i][j].fixSimulationEnd(info.getSimulation().getEndTs());
				Double []values = firstWaitTime[i][j].getValues();
				for (double val : values) {
					int range = 0;
					for (; (range < REFDAYS.length - 1) && 
					//FIXME: La espera debería depender de si es un paciente de emergencias o normal
						(val >= /*TimeUnit.MINUTES.convert(input.getOpTheatreStartHour(), TimeUnit.HOURS) + 
							TimeUnit.MINUTES.convert(input.getOpTheatreAvailabilityHours(), TimeUnit.HOURS) +*/ 
							TimeUnit.MINUTES.convert(1, TimeUnit.DAYS) * (range + 1)); range++);
					waitingDays[i][j][range]++;					
				}
				averages[i][j] = Statistics.average(values); 
				stddevs[i][j] = Statistics.stdDev(values, averages[i][j]);			
			}
		results = new GSElementTypeWaitResults(averages, stddevs, waitingDays);
	}

	public void infoEmited(SimulationStartInfo info) {
	}

	public void infoEmited(SimulationObjectInfo info) {
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo) info;
			PatientCategory pc = input.getPatientCategories()[eInfo.getValue()];
			switch (eInfo.getType()) {
			case START:
				firstWaitTime[pc.getAdmissionType().ordinal()][pc.getPatientType().ordinal()].addStart(eInfo.getIdentifier(), eInfo.getTs());
				break;
			case STAACT:
				firstWaitTime[pc.getAdmissionType().ordinal()][pc.getPatientType().ordinal()].addStartActivity(eInfo.getIdentifier(), eInfo.getTs());
				break;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.HUNSC.cirgen.listener.ToExcel#setResult(org.apache.poi.hssf.usermodel.HSSFWorkbook)
	 */
	public void setResults(HSSFWorkbook wb) {
		HSSFSheet s = wb.createSheet("Espera de los pacientes");

		// Define styles
	    HSSFCellStyle headStyle = ExcelTools.getHeadStyle(wb);
	    
	    HSSFCellStyle diagStyle = wb.createCellStyle();
	    diagStyle.setFillForegroundColor(HSSFColor.GOLD.index);
	    diagStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    diagStyle.setFont(ExcelTools.getBoldFont(wb));
	    
		short column = 0;
		int rownum = 1;
		HSSFRow r = s.createRow(rownum++);
		r.createCell(column).setCellValue(new HSSFRichTextString("Admisión"));
		r.getCell(column).setCellStyle(headStyle);
		r = s.createRow(rownum++);
		r.createCell(column).setCellValue(new HSSFRichTextString("Tipo"));
		r.getCell(column).setCellStyle(headStyle);
		r = s.createRow(rownum++);
		r.createCell(column).setCellValue(new HSSFRichTextString("Media"));
		r.getCell(column).setCellStyle(headStyle);
		r = s.createRow(rownum++);
		r.createCell(column).setCellValue(new HSSFRichTextString("Desv."));
		r.getCell(column).setCellStyle(headStyle);
		rownum++;
		for (String day : REFDAYS) {
			r = s.createRow(rownum++);
			r.createCell(column).setCellValue(new HSSFRichTextString(day));
			r.getCell(column).setCellStyle(headStyle);			
		}
		
		rownum++;

		// Detect highest number of elements´
		int maxrow = 0;
		for (int i = 0; i < AdmissionType.values().length; i++)
			for (int j = 0; j < PatientType.values().length; j++)
				maxrow = Math.max(firstWaitTime[i][j].size(), maxrow);
		
		// Create rows
		for (int i = rownum; i < maxrow + rownum; i++)
			s.createRow(i);
		
		for (AdmissionType adm : AdmissionType.values())
			for (PatientType type : PatientType.values()) {
				column++;
				rownum = 1;
				r = s.getRow(rownum++);
				r.createCell(column).setCellValue(new HSSFRichTextString(adm.getName()));
				r.getCell(column).setCellStyle(diagStyle);
				r = s.getRow(rownum++);
				r.createCell(column).setCellValue(new HSSFRichTextString(type.getName()));
				r.getCell(column).setCellStyle(diagStyle);

				s.getRow(rownum++).createCell(column).setCellValue(results.getAverages()[adm.ordinal()][type.ordinal()]);
				s.getRow(rownum++).createCell(column).setCellValue(results.getStddevs()[adm.ordinal()][type.ordinal()]);
				rownum++;
				for (int i = 0; i < REFDAYS.length; i++)
					s.getRow(rownum++).createCell(column).setCellValue(results.getWaitingDays()[adm.ordinal()][type.ordinal()][i]);			
				
				rownum++;
				for (double val : firstWaitTime[adm.ordinal()][type.ordinal()].getValues()) {
					s.getRow(rownum++).createCell(column).setCellValue(val);
				}
			}
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.HUNSC.cirgen.listener.GSListener#getResults()
	 */
	public GSResult getResults() {
		return results;
	}

	public class FirstWaitTime {
		private HashMap<Integer, Double> value;
		
		public FirstWaitTime() {
			value = new HashMap<Integer, Double>();
		}
		
		public void addStart(int elemId, double ts) {
			value.put(elemId, -ts);			
		}
		
		public void addStartActivity(int elemId, double ts) {
			if (value.get(elemId) < 0.0 || value.get(elemId) == -0.0)
				value.put(elemId, ts + value.get(elemId));			
		}
		
		public void fixSimulationEnd(double endTs) {
			for (Integer key : value.keySet())
				if (value.get(key) < 0.0)
					value.put(key, endTs + value.get(key));
		}
		
		public Double[] getValues() {
			return value.values().toArray(new Double[1]);
		}
		
		public int size() {
			return value.size();
		}
	}
}
