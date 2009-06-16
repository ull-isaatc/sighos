/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import es.ull.isaatc.util.ExcelTools;
import es.ull.isaatc.simulation.SimulationWeeklyPeriodicCycle;
import es.ull.isaatc.simulation.SimulationWeeklyPeriodicCycle.WeekDays;

/**
 * Clase para "envolver" el fichero java de entrada.
 * @author Iván Castilla Rodríguez
 */
public class GSExcelInputWrapper {
	/** Identificadores de las 3 hojas de Excel que se crean */
	enum ExcelSheets {CONFIG, SURGERIES, PATIENTS}
	/** El libro en el que se escribe */
	private HSSFWorkbook wb;
	/** Los quirófanos del hospital */
	private OperationTheatre []opTheatres;
	/** Los tipos de pacientes del hospital */
	private PatientCategory []patientCategories;

	public GSExcelInputWrapper(String filename) {
		POIFSFileSystem fs;
		try {
			fs = new POIFSFileSystem(new FileInputStream(filename));
			wb = new HSSFWorkbook(fs);
			setOpTheatres();
			setPatientCategories();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Devuelve el tipo de admisión (PROG vs EMER) correspondiente a una cadena de texto
	 * @param at Tipo de admisión
	 * @return el tipo de admisión (PROG vs EMER) correspondiente a una cadena de texto
	 */
	private AdmissionType str2AdmissionType(String at) {
		return (at.compareToIgnoreCase("P") == 0) ? AdmissionType.PROGRAMMED : AdmissionType.EMERGENCY;		
	}
	
	/**
	 * Devuelve el tipo de paciente (DC vs OR) correspondiente a una cadena de texto
	 * @param at Tipo de admisión
	 * @return el tipo de paciente (DC vs OR) correspondiente a una cadena de texto
	 */
	private PatientType str2PatientType(String pt) {
		return (pt.compareToIgnoreCase("S") == 0) ? PatientType.DC : PatientType.OR;
	}
	
	/**
	 * Crea las estructuras donde se almacenan las características de los quirófanos del hospital.
	 */
	private void setOpTheatres() {
		ArrayList<OperationTheatre> res = new ArrayList<OperationTheatre>();
		HSSFSheet s = wb.getSheetAt(ExcelSheets.SURGERIES.ordinal());
		Iterator<HSSFRow> rit = (Iterator<HSSFRow>)s.rowIterator();
		// Skip header
		rit.next();
		// Continue with the op. theatres
		OperationTheatre op = null;
		while (rit.hasNext()) {
			HSSFRow r = (HSSFRow)rit.next();
			// If it's a new op. theatre, creates the operation theatre with the basic information
			if ((r.getCell((short)0) != null) && !"".equals(ExcelTools.getString(r, (short)0).trim())) {
				op = new OperationTheatre(ExcelTools.getString(r, (short)1), 
						r.getCell((short)2).getNumericCellValue(), 
						r.getCell((short)3).getNumericCellValue());
				res.add(op);				
			}
			// Adds the timetable entry
			ArrayList<WeekDays> days = new ArrayList<WeekDays>();
			short col = 8;
			for (WeekDays day : SimulationWeeklyPeriodicCycle.ALLWEEK)
				if (ExcelTools.getString(r, (short)col++).compareToIgnoreCase("S") == 0)
					days.add(day);
			op.addTimeTableEntry(str2PatientType(ExcelTools.getString(r, (short)4)), str2AdmissionType(ExcelTools.getString(r, (short)5)), 
					r.getCell((short)6).getNumericCellValue() * 60.0, r.getCell((short)7).getNumericCellValue() * 60.0, EnumSet.copyOf(days));
		}
		opTheatres = new OperationTheatre[res.size()];
		opTheatres = res.toArray(opTheatres);
	}
	
	/**
	 * Crea las estructuras donde se almancenan los tipos de paciente que van al hospital 
	 */
	private void setPatientCategories() {
		ArrayList<PatientCategory> res = new ArrayList<PatientCategory>();
		HSSFSheet s = wb.getSheetAt(ExcelSheets.PATIENTS.ordinal());
		Iterator<HSSFRow> rit = (Iterator<HSSFRow>)s.rowIterator();
		// Skip header
		rit.next();
		// Continue with the patient types
		while (rit.hasNext()) {
			HSSFRow r = (HSSFRow)rit.next();
			if (ExcelTools.validCell(r, (short)0)) {
				res.add(new PatientCategory(r.getCell((short)0).toString(),
						str2AdmissionType(r.getCell((short)2).toString()),
						str2PatientType(r.getCell((short)1).toString()),						
						(int)r.getCell((short)3).getNumericCellValue(),
						r.getCell((short)4).getNumericCellValue(),
						r.getCell((short)5).toString(),
						r.getCell((short)6).getNumericCellValue(),
						r.getCell((short)7).getNumericCellValue()));
			}
		}
		patientCategories = new PatientCategory[res.size()];
		patientCategories = res.toArray(patientCategories);
	}
	
	/**
	 * Devuelve el total de días de observación del modelo
	 * @return El total de días de observación del modelo
	 */
	public long getObservedDays() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return (long)s.getRow(1).getCell((short)1).getNumericCellValue();		
	}
	
	/**
	 * Devuelve el número de experimentos que hay que realizar
	 * @return el número de experimentos que hay que realizar
	 */
	public int getNExperiments() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return (int)s.getRow(2).getCell((short)1).getNumericCellValue();		
	}

	/**
	 * Devuelve el total de días que hay que simular del modelo
	 * @return el total de días que hay que simular del modelo
	 */
	public long getSimulationDays() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return (long)s.getRow(3).getCell((short)1).getNumericCellValue();		
	}
	
	/**
	 * Devuelve la ruta a los ficheros de salida 
	 * @return La ruta a los ficheros de salida
	 */
	public String getOutputPath() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return s.getRow(4).getCell((short)1).getRichStringCellValue().getString();		
	}

	/**
	 * Devuelve el nombre base del fichero de salida
	 * @return El nombre base del fichero de salida
	 */
	public String getOutputFileName() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return s.getRow(5).getCell((short)1).getRichStringCellValue().getString();		
	}

	/**
	 * Devuelve los quirófanos del hospital
	 * @return the opTheatres
	 */
	public OperationTheatre[] getOpTheatres() {
		return opTheatres;
	}

	/**
	 * Devuelve los tipos de paciente que maneja el hospital 
	 * @return the patientCategories
	 */
	public PatientCategory[] getPatientCategories() {
		return patientCategories;
	}
	
}
