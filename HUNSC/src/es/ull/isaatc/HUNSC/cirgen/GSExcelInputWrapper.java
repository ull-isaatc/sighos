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

import es.ull.isaatc.HUNSC.cirgen.util.ExcelTools;
import es.ull.isaatc.util.WeeklyPeriodicCycle;
import es.ull.isaatc.util.WeeklyPeriodicCycle.WeekDays;

/**
 * @author Iván Castilla Rodríguez
 */
public class GSExcelInputWrapper {
	enum ExcelSheets {CONFIG, SURGERIES, PATIENTS}
	private HSSFWorkbook wb;
	private OperationTheatre []opTheatres;
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
	
	private AdmissionType str2AdmissionType(String at) {
		return (at.compareToIgnoreCase("P") == 0) ? AdmissionType.PROGRAMMED : AdmissionType.EMERGENCY;		
	}
	
	private PatientType str2PatientType(String pt) {
		return (pt.compareToIgnoreCase("S") == 0) ? PatientType.DC : PatientType.OR;
	}
	
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
			for (WeekDays day : WeeklyPeriodicCycle.ALLWEEK)
				if (ExcelTools.getString(r, (short)col++).compareToIgnoreCase("S") == 0)
					days.add(day);
			op.addTimeTableEntry(str2PatientType(ExcelTools.getString(r, (short)4)), str2AdmissionType(ExcelTools.getString(r, (short)5)), 
					r.getCell((short)6).getNumericCellValue() * 60.0, r.getCell((short)7).getNumericCellValue() * 60.0, EnumSet.copyOf(days));
		}
		opTheatres = new OperationTheatre[res.size()];
		opTheatres = res.toArray(opTheatres);
	}
	
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
	
	public long getObservedDays() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return (long)s.getRow(1).getCell((short)1).getNumericCellValue();		
	}
	
	public int getNExperiments() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return (int)s.getRow(2).getCell((short)1).getNumericCellValue();		
	}

	public long getSimulationDays() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return (long)s.getRow(3).getCell((short)1).getNumericCellValue();		
	}
	
	public String getOutputPath() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return s.getRow(4).getCell((short)1).getRichStringCellValue().getString();		
	}

	public String getOutputFileName() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return s.getRow(5).getCell((short)1).getRichStringCellValue().getString();		
	}

	/**
	 * @return the opTheatres
	 */
	public OperationTheatre[] getOpTheatres() {
		return opTheatres;
	}

	/**
	 * @return the patientCategories
	 */
	public PatientCategory[] getPatientCategories() {
		return patientCategories;
	}
	
}
