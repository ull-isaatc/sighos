/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.listener;

import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import es.ull.isaatc.HUNSC.cirgen.SimGS;
import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.listener.SimulationListener;
import es.ull.isaatc.simulation.listener.SimulationObjectListener;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class GSElementTypeWaitListener implements ToExcel, SimulationObjectListener, SimulationListener {

	private HashMap<Integer, Double> firstWaitTimeOR;
	private HashMap<Integer, Double> firstWaitTimeDC;
	
	public GSElementTypeWaitListener() {
		firstWaitTimeDC = new HashMap<Integer, Double>();
		firstWaitTimeOR = new HashMap<Integer, Double>();
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
				if (eInfo.getValue() >= SimGS.PatientType.values().length)
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
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.HUNSC.cirgen.listener.ToExcel#setResult(org.apache.poi.hssf.usermodel.HSSFWorkbook)
	 */
	public void setResult(HSSFWorkbook wb) {
		HSSFSheet s = wb.createSheet("Espera de los pacientes");

		// Detect highest number of elements
		int maxrow = Math.max(firstWaitTimeOR.values().size(), firstWaitTimeDC.values().size());
		
		// Create rows
		for (int i = 0; i < maxrow + 5; i++)
			s.createRow(i);
		
		short column = 0;
		s.getRow(1).createCell(column).setCellValue(new HSSFRichTextString("Paciente"));
		s.getRow(2).createCell(column).setCellValue(new HSSFRichTextString("Media"));
		s.getRow(3).createCell(column).setCellValue(new HSSFRichTextString("Desv."));
		
		s.getRow(1).createCell(++column).setCellValue(new HSSFRichTextString(SimGS.OpTheatreType.OR.getName()));
		int rownum = 5;
		for (double val : firstWaitTimeOR.values()) {
			HSSFRow r = s.getRow(rownum++);
			r.createCell(column).setCellValue(val);				
		}
		s.getRow(2).createCell(column).setCellFormula("AVERAGE(B6:B"+ rownum + ")");
		s.getRow(3).createCell(column).setCellFormula("STDEV(B6:B"+ rownum + ")");
		
		s.getRow(1).createCell(++column).setCellValue(new HSSFRichTextString(SimGS.OpTheatreType.DC.getName()));
		rownum = 5;		
		for (double val : firstWaitTimeDC.values()) {
			HSSFRow r = s.getRow(rownum++);
			r.createCell(column).setCellValue(val);				
		}
		s.getRow(2).createCell(column).setCellFormula("AVERAGE(C6:C"+ rownum + ")");
		s.getRow(3).createCell(column).setCellFormula("STDEV(C6:C"+ rownum + ")");
	}

}
