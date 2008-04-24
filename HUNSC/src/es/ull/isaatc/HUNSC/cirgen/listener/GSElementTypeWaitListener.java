/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.listener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import es.ull.isaatc.HUNSC.cirgen.GSExcelInputWrapper;
import es.ull.isaatc.HUNSC.cirgen.OperationTheatreType;
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
public class GSElementTypeWaitListener implements ToExcel, SimulationObjectListener, SimulationListener {
	public static final String []REFDAYS = {"Espera < 1 día", "Espera < 2 días",
		"Espera < 3 días", "Espera > 3 días"};
	private HashMap<Integer, Double> firstWaitTimeOR;
	private HashMap<Integer, Double> firstWaitTimeDC;
	private double[] averages;
	private double[] stddevs;
	private int[][] waitingDays;
	private GSExcelInputWrapper input;
	
	public GSElementTypeWaitListener(GSExcelInputWrapper input) {
		this.input = input;
		firstWaitTimeDC = new HashMap<Integer, Double>();
		firstWaitTimeOR = new HashMap<Integer, Double>();
		averages = new double[OperationTheatreType.values().length];
		stddevs = new double[OperationTheatreType.values().length];
		waitingDays = new int[OperationTheatreType.values().length][REFDAYS.length];
	}

	public void infoEmited(SimulationEndInfo info) {
		for (Integer key : firstWaitTimeOR.keySet()) {
			if (firstWaitTimeOR.get(key) < 0.0)
				firstWaitTimeOR.put(key, info.getSimulation().getEndTs() + firstWaitTimeOR.get(key));
		}
		for (Integer key : firstWaitTimeDC.keySet()) {
			if (firstWaitTimeDC.get(key) < 0.0)
				firstWaitTimeDC.put(key, info.getSimulation().getEndTs() + firstWaitTimeDC.get(key));
		}
		
	}

	public void infoEmited(SimulationStartInfo info) {
	}

	public void infoEmited(SimulationObjectInfo info) {
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo) info;
			switch (eInfo.getType()) {
			case START:
				if (eInfo.getValue() % OperationTheatreType.values().length == OperationTheatreType.DC.ordinal())
					firstWaitTimeDC.put(eInfo.getIdentifier(), -eInfo.getTs());
				else
					firstWaitTimeOR.put(eInfo.getIdentifier(), -eInfo.getTs());
				break;
			case STAACT:
				Double val = firstWaitTimeOR.get(eInfo.getIdentifier());
				if (val != null) {
					if (val < 0.0 || val == -0.0)
						firstWaitTimeOR.put(eInfo.getIdentifier(), eInfo.getTs() + val);
				}
				else { 
					val = firstWaitTimeDC.get(eInfo.getIdentifier());
					if (val != null)
						if (val < 0.0 || val == -0.0)
							firstWaitTimeDC.put(eInfo.getIdentifier(), eInfo.getTs() + val);
				}
				break;
			}
		}
	}

	public String toString() {
		StringBuffer str = new StringBuffer();
		
		return str.toString();
	}

	private void setColumnResults(HSSFSheet s, int rownum, OperationTheatreType type) {

		HashMap<Integer, Double> firstWaitTime = getFirstWaitTime(type);
		short column = (short)(type.ordinal() + 1);
		s.getRow(1).createCell(column).setCellValue(new HSSFRichTextString(type.getName()));

		for (double val : firstWaitTime.values()) {
			HSSFRow r = s.getRow(rownum++);
			r.createCell(column).setCellValue(val);
			int range = 0;
			for (; (range < REFDAYS.length - 1) && 
				(val >= TimeUnit.MINUTES.convert(input.getOpTheatreStartHour(), TimeUnit.HOURS) + 
					TimeUnit.MINUTES.convert(input.getOpTheatreAvailabilityHours(), TimeUnit.HOURS) + 
					TimeUnit.MINUTES.convert(1, TimeUnit.DAYS) * range); range++);
			waitingDays[type.ordinal()][range]++;
		}
		Double []values = firstWaitTime.values().toArray(new Double[1]);
		averages[type.ordinal()] = Statistics.average(values); 
		s.getRow(2).createCell(column).setCellValue(averages[type.ordinal()]);
		stddevs[type.ordinal()] = Statistics.stdDev(values, averages[type.ordinal()]);
		s.getRow(3).createCell(column).setCellValue(stddevs[type.ordinal()]);
		for (int i = 0; i < REFDAYS.length; i++)
			s.getRow(i + 5).createCell(column).setCellValue(waitingDays[type.ordinal()][i]);			
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.HUNSC.cirgen.listener.ToExcel#setResult(org.apache.poi.hssf.usermodel.HSSFWorkbook)
	 */
	public void setResult(HSSFWorkbook wb) {
		HSSFSheet s = wb.createSheet("Espera de los pacientes");

		short column = 0;
		int rownum = 1;
		s.createRow(rownum++).createCell(column).setCellValue(new HSSFRichTextString("Paciente"));
		s.createRow(rownum++).createCell(column).setCellValue(new HSSFRichTextString("Media"));
		s.createRow(rownum++).createCell(column).setCellValue(new HSSFRichTextString("Desv."));
		rownum++;
		for (String day : REFDAYS)
			s.createRow(rownum++).createCell(column).setCellValue(new HSSFRichTextString(day));
		
		rownum++;

		// Detect highest number of elements
		int maxrow = Math.max(firstWaitTimeOR.values().size(), firstWaitTimeDC.values().size());
		
		// Create rows
		for (int i = rownum; i < maxrow + rownum; i++)
			s.createRow(i);
		
		for (OperationTheatreType type : OperationTheatreType.values())
			setColumnResults(s, rownum, type);

	}

	public HashMap<Integer, Double> getFirstWaitTime(OperationTheatreType type) {
		if (type == OperationTheatreType.OR)
			return firstWaitTimeOR;
		return firstWaitTimeDC;
	}
	
	/**
	 * @return the waitingDays
	 */
	public int[][] getWaitingDays() {
		return waitingDays;
	}

	/**
	 * @return the averages
	 */
	public double[] getAverages() {
		return averages;
	}

	/**
	 * @return the stddevs
	 */
	public double[] getStddevs() {
		return stddevs;
	}

}
