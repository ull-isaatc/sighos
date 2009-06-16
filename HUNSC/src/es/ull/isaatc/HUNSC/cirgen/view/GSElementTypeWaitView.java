/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.view;

import java.util.HashMap;

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
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.info.ElementActionInfo;
import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.inforeceiver.View;
import es.ull.isaatc.util.ExcelTools;
import es.ull.isaatc.util.Statistics;

/**
 * Recolecta y muestra en una hoja de Excel la informaci�n referente a la espera de los pacientes hasta que
 * son atendidos.
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class GSElementTypeWaitView extends View implements GSView {
	public static final String []REFDAYS = {"Espera < 1 d�a", "Espera < 2 d�as",
		"Espera < 3 d�as", "Espera > 3 d�as"};
	private FirstWaitTime[][] firstWaitTime;
	private GSElementTypeWaitResults results = null;
	private GSExcelInputWrapper input;
	
	public GSElementTypeWaitView(Simulation simul, GSExcelInputWrapper input) {
		super(simul, "Elements wait");
		this.input = input;
		firstWaitTime = new FirstWaitTime[AdmissionType.values().length][PatientType.values().length];
		for (int i = 0; i < AdmissionType.values().length; i++)
			for (int j = 0; j < PatientType.values().length; j++)
				firstWaitTime[i][j] = new FirstWaitTime();
		addEntrance(ElementActionInfo.class);
		addEntrance(ElementInfo.class);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo) info;
			PatientCategory pc = input.getPatientCategories()[eInfo.getElem().getElementType().getIdentifier()];
			if (eInfo.getType() == ElementInfo.Type.START) 
				firstWaitTime[pc.getAdmissionType().ordinal()][pc.getPatientType().ordinal()].addStart(eInfo.getElem().getIdentifier(), eInfo.getTs().getValue());
		}
		else if (info instanceof ElementActionInfo) {
			ElementActionInfo eInfo = (ElementActionInfo) info;
			PatientCategory pc = input.getPatientCategories()[eInfo.getElem().getElementType().getIdentifier()];
			if (eInfo.getType() == ElementActionInfo.Type.STAACT) 
				firstWaitTime[pc.getAdmissionType().ordinal()][pc.getPatientType().ordinal()].addStartActivity(eInfo.getElem().getIdentifier(), eInfo.getTs().getValue());
		}
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.HUNSC.cirgen.listener.ToExcel#setResult(org.apache.poi.hssf.usermodel.HSSFWorkbook)
	 */
	public void setResults(HSSFWorkbook wb) {
		double [][] averages = new double[AdmissionType.values().length][PatientType.values().length];
		double [][] stddevs = new double[AdmissionType.values().length][PatientType.values().length];
		int [][][] waitingDays = new int[AdmissionType.values().length][PatientType.values().length][REFDAYS.length];
		
		double conv = new SimulationTime(SimulationTimeUnit.DAY, 1).convert(getSimul().getUnit()).getValue();
		for (int i = 0; i < AdmissionType.values().length; i++)
			for (int j = 0; j < PatientType.values().length; j++) {
				firstWaitTime[i][j].fixSimulationEnd(getSimul().getInternalEndTs());
				Double []values = firstWaitTime[i][j].getValues();
				for (double val : values) {
					int range = 0;
					for (; (range < REFDAYS.length - 1) && 
					//FIXME: La espera deber�a depender de si es un paciente de emergencias o normal
						(val >= /*TimeUnit.MINUTES.convert(input.getOpTheatreStartHour(), TimeUnit.HOURS) + 
							TimeUnit.MINUTES.convert(input.getOpTheatreAvailabilityHours(), TimeUnit.HOURS) +*/ 
							conv * (range + 1)); range++);
					waitingDays[i][j][range]++;					
				}
				averages[i][j] = Statistics.average(values); 
				stddevs[i][j] = Statistics.stdDev(values, averages[i][j]);			
			}
		results = new GSElementTypeWaitResults(averages, stddevs, waitingDays);			

		
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
		r.createCell(column).setCellValue(new HSSFRichTextString("Admisi�n"));
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

		// Detect highest number of elements�
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

	/**
	 * Guardar los tiempos de espera antes de realizar la primera intervenci�n de cada paciente. 
	 * @author Iv�n Castilla Rodr�guez
	 */
	public class FirstWaitTime {
		/** Estructura para guardar los tiempos de espera antes de realizar la primera actividad de cada elemento. */
		private HashMap<Integer, Double> value;
		
		public FirstWaitTime() {
			value = new HashMap<Integer, Double>();
		}
		
		/**
		 * Almacena el instante de tiempo en que el paciente llega al sistema. El tiempo se almacena con valor
		 * negativo para tener en cuenta que no es un tiempo absoluto. 
		 * @param elemId Identificador del paciente
		 * @param ts Tiempo de comienzo del paciente en el sistema
		 */
		public void addStart(int elemId, double ts) {
			value.put(elemId, -ts);			
		}
		
		/**
		 * Actualiza el tiempo de espera del paciente la primera vez que se invoca. Esto s�lo ocurre si el tiempo
		 * que hay almacenado es negativo.
		 * @param elemId Identificador del paciente
		 * @param ts Instante de tiempo en que el paciente comienza una actividad
		 */
		public void addStartActivity(int elemId, double ts) {
			if (value.get(elemId) < 0.0 || value.get(elemId) == -0.0)
				value.put(elemId, ts + value.get(elemId));			
		}
		
		/**
		 * Actualiza los tiempos de espera para aquellos pacientes que no hayan podido iniciar su primera 
		 * intervenci�n. Estos pacientes tienen tiempo de espera negativo. 
		 * @param endTs
		 */
		public void fixSimulationEnd(double endTs) {
			for (Integer key : value.keySet())
				if (value.get(key) < 0.0)
					value.put(key, endTs + value.get(key));
		}
		
		public Double[] getValues() {
			return value.values().toArray(new Double[1]);
		}
		
		/**
		 * Devuelve el n�mero de pacientes que se han almacenado
		 * @return El n�mero de pacientes que se han almacenado
		 */
		public int size() {
			return value.size();
		}
	}

}
