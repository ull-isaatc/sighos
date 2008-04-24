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
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFCell;
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
	private PatientType []patientTypes;

	public GSExcelInputWrapper(String filename) {
		POIFSFileSystem fs;
		try {
			fs = new POIFSFileSystem(new FileInputStream(filename));
			wb = new HSSFWorkbook(fs);
			setOpTheatres();
			setPatientTypes();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void setOpTheatres() {
		ArrayList<OperationTheatre> res = new ArrayList<OperationTheatre>();
		HSSFSheet s = wb.getSheetAt(ExcelSheets.SURGERIES.ordinal());
		Iterator<HSSFRow> rit = (Iterator<HSSFRow>)s.rowIterator();
		// Skip header
		rit.next();
		// Continue with the op. theatres
		while (rit.hasNext()) {
			HSSFRow r = (HSSFRow)rit.next();
			OperationTheatreType t = 
				(ExcelTools.getString(r, (short)2).compareToIgnoreCase("S") == 0)? 
						OperationTheatreType.DC : OperationTheatreType.OR;
			ArrayList<WeekDays> days = new ArrayList<WeekDays>();
			short col = 5;
			for (WeekDays day : WeeklyPeriodicCycle.WEEKDAYS)
				if (ExcelTools.getString(r, (short)col++).compareToIgnoreCase("S") == 0)
					days.add(day);
			res.add(new OperationTheatre(ExcelTools.getString(r, (short)1), t, EnumSet.copyOf(days), 
					TimeUnit.MINUTES.convert(getOpTheatreStartHour(), TimeUnit.HOURS), 
					r.getCell((short)3).getNumericCellValue(), 
					r.getCell((short)4).getNumericCellValue()));
		}
		opTheatres = new OperationTheatre[res.size()];
		opTheatres = res.toArray(opTheatres);
	}
	
	private void setPatientTypes() {
		ArrayList<PatientType> res = new ArrayList<PatientType>();
		HSSFSheet s = wb.getSheetAt(ExcelSheets.PATIENTS.ordinal());
		Iterator<HSSFRow> rit = (Iterator<HSSFRow>)s.rowIterator();
		// Skip header
		rit.next();
		// Continue with the patient types
		while (rit.hasNext()) {
			HSSFRow r = (HSSFRow)rit.next();
			// The diagnosis can be stored as a number or a text
			String diag = "";
			if (r.getCell((short)0).getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
				diag = String.valueOf((int)r.getCell((short)0).getNumericCellValue());
			else
				diag = ExcelTools.getString(r, (short)0);
			res.add(new PatientType(diag, 
					(int)r.getCell((short)1).getNumericCellValue(),
					(int)r.getCell((short)4).getNumericCellValue(),
					r.getCell((short)2).getNumericCellValue(),
					r.getCell((short)3).getNumericCellValue(),
					r.getCell((short)5).getNumericCellValue(),
					r.getCell((short)6).getNumericCellValue()));
		}
		patientTypes = new PatientType[res.size()];
		patientTypes = res.toArray(patientTypes);
	}
	
	public long getObservedDays() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return (long)s.getRow(1).getCell((short)1).getNumericCellValue();		
	}
	
	public long getOpTheatreStartHour() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return (long)s.getRow(2).getCell((short)1).getNumericCellValue();
	}

	public long getOpTheatreAvailabilityHours() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return (long)s.getRow(3).getCell((short)1).getNumericCellValue();
	}
	
	public int getNExperiments() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return (int)s.getRow(4).getCell((short)1).getNumericCellValue();		
	}

	public long getSimulationDays() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return (long)s.getRow(5).getCell((short)1).getNumericCellValue();		
	}
	
	public String getOutputPath() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return s.getRow(6).getCell((short)1).getRichStringCellValue().getString();		
	}

	public String getOutputFileName() {
		HSSFSheet s = wb.getSheetAt(ExcelSheets.CONFIG.ordinal());
		return s.getRow(7).getCell((short)1).getRichStringCellValue().getString();		
	}

	/**
	 * @return the opTheatres
	 */
	public OperationTheatre[] getOpTheatres() {
		return opTheatres;
	}

	/**
	 * @return the patientTypes
	 */
	public PatientType[] getPatientTypes() {
		return patientTypes;
	}
	
}
