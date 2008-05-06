/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.listener;

import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import es.ull.isaatc.HUNSC.cirgen.*;
import es.ull.isaatc.HUNSC.cirgen.util.ExcelTools;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.listener.ElementTypeTimeListener;
import es.ull.isaatc.util.Statistics;

/**
 * @author Iván
 *
 */
public class GSElementTypeTimeListener extends ElementTypeTimeListener implements GSListener {
	private enum Columns {
		CATEGORY ("Categoría"),
		DAYCASE ("Ambulante"),
		TYPE ("Tipo"),
		TOTAL ("Total"),
		N_CREATED ("Creados"),
		ERROR_CREATED ("Error creados"),
		N_FINISHED ("Terminados"),
		ERROR_FINISHED ("Error terminados"),
		TIME ("Tiempo"),
		EXP_TIME ("Total tiempo"),
		ERROR_EXP_TIME ("Error tiempo");
		
		String name;
		private Columns(String name) {
			this.name= name;			
		}
	}

	private GSExcelInputWrapper input;
	private GSElementTypeTimeResults results = null;
	
	/**
	 * 
	 */
	public GSElementTypeTimeListener() {
		super();
	}
	
	public GSElementTypeTimeListener(GSExcelInputWrapper input) {
		super(TimeUnit.MINUTES.convert(input.getSimulationDays(), TimeUnit.DAYS));
		this.input = input;
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.listener.ElementTypeTimeListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
	 */
	@Override
	public void infoEmited(SimulationEndInfo info) {
		super.infoEmited(info);
		int []cElem = new int[input.getPatientCategories().length];
		int []fElem = new int[input.getPatientCategories().length];
		double []wElem = new double[input.getPatientCategories().length];
		for (PatientCategory pt : input.getPatientCategories()) {
			for (int value : getElementTypeTimes().get(pt.getIndex()).getCreatedElement())
				cElem[pt.getIndex()] += value;
			for (int value : getElementTypeTimes().get(pt.getIndex()).getFinishedElement())
				fElem[pt.getIndex()] += value;
			for (double value : getElementTypeTimes().get(pt.getIndex()).getWorkTime())
				wElem[pt.getIndex()] += value;			
		}
		results = new GSElementTypeTimeResults(cElem, fElem, wElem);
	}
	
	public void setResults(HSSFWorkbook wb) {
		// Define fonts
	    HSSFFont boldFont = ExcelTools.getBoldFont(wb);
	    
		// Define styles
	    HSSFCellStyle headStyle = ExcelTools.getHeadStyle(wb); 

	    HSSFCellStyle errorStyle = ExcelTools.getErrorStyle(wb);
	    
	    HSSFCellStyle diagStyle = wb.createCellStyle();
	    diagStyle.setFillForegroundColor(HSSFColor.GOLD.index);
	    diagStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    diagStyle.setFont(boldFont);

	    HSSFCellStyle theorStyle = wb.createCellStyle();
	    theorStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
	    theorStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		// create a new sheet
		HSSFSheet s = wb.createSheet("Pacientes");
		s.createFreezePane(1, 2);
		int rownum = 1;
		HSSFRow r = s.createRow(rownum++);
	    for (Columns col : Columns.values()) { 
			HSSFCell c = r.createCell((short)col.ordinal());
			c.setCellValue(new HSSFRichTextString(col.name));
			c.setCellStyle(headStyle);
	    }
		double totalT = 0.0;
		double totalN = 0.0;
		double totalC = 0.0;
		double totalF = 0.0;
		double totalW = 0.0;
		for (PatientCategory pt : input.getPatientCategories()) {
			r = s.createRow(rownum++);
			HSSFCell c = r.createCell((short)Columns.CATEGORY.ordinal());
			c.setCellValue(new HSSFRichTextString(pt.getName()));
			c.setCellStyle(diagStyle);
			
			String type = (pt.getPatientType() == PatientType.DC) ? "S":"N"; 
			r.createCell((short)Columns.DAYCASE.ordinal()).setCellValue(new HSSFRichTextString(type));
			r.getCell((short)Columns.DAYCASE.ordinal()).setCellStyle(diagStyle);
			type = (pt.getAdmissionType() == AdmissionType.PROGRAMMED) ? "P":"U"; 
			r.createCell((short)Columns.TYPE.ordinal()).setCellValue(new HSSFRichTextString(type));
			r.getCell((short)Columns.TYPE.ordinal()).setCellStyle(diagStyle);
			
			r.createCell((short)Columns.TOTAL.ordinal()).setCellValue(pt.getTotal());
			r.getCell((short)Columns.TOTAL.ordinal()).setCellStyle(theorStyle);
			totalN += pt.getTotal();
			r.createCell((short)Columns.TIME.ordinal()).setCellValue(pt.getTotalTime());
			r.getCell((short)Columns.TIME.ordinal()).setCellStyle(theorStyle);
			totalT += pt.getTotalTime();
			
			totalC += results.getCreatedElements()[pt.getIndex()];
			r.createCell((short)Columns.N_CREATED.ordinal()).setCellValue(results.getCreatedElements()[pt.getIndex()]);
			r.createCell((short)Columns.ERROR_CREATED.ordinal()).setCellValue(Statistics.relError100(pt.getTotal(), results.getCreatedElements()[pt.getIndex()]));
			r.getCell((short)Columns.ERROR_CREATED.ordinal()).setCellStyle(errorStyle);
			
			totalF += results.getFinishedElements()[pt.getIndex()];
			r.createCell((short)Columns.N_FINISHED.ordinal()).setCellValue(results.getFinishedElements()[pt.getIndex()]);
			r.createCell((short)Columns.ERROR_FINISHED.ordinal()).setCellValue(Statistics.relError100(pt.getTotal(), results.getFinishedElements()[pt.getIndex()]));
			r.getCell((short)Columns.ERROR_FINISHED.ordinal()).setCellStyle(errorStyle);

			totalW += results.getWorkTime()[pt.getIndex()];
			r.createCell((short)Columns.EXP_TIME.ordinal()).setCellValue(results.getWorkTime()[pt.getIndex()]);
			r.createCell((short)Columns.ERROR_EXP_TIME.ordinal()).setCellValue(Statistics.relError100(pt.getTotalTime(), results.getWorkTime()[pt.getIndex()]));
			r.getCell((short)Columns.ERROR_EXP_TIME.ordinal()).setCellStyle(errorStyle);
		}
		r = s.createRow(rownum++);
		r.createCell((short)Columns.CATEGORY.ordinal()).setCellValue(new HSSFRichTextString("TOTAL"));
		r.getCell((short)Columns.CATEGORY.ordinal()).setCellStyle(headStyle);
		r.createCell((short)Columns.TOTAL.ordinal()).setCellValue(totalN);
		r.createCell((short)Columns.TIME.ordinal()).setCellValue(totalT);
		r.createCell((short)Columns.N_CREATED.ordinal()).setCellValue(totalC);
		r.createCell((short)Columns.ERROR_CREATED.ordinal()).setCellValue(Statistics.relError100(totalN, totalC));
		r.getCell((short)Columns.ERROR_CREATED.ordinal()).setCellStyle(errorStyle);
		r.createCell((short)Columns.N_FINISHED.ordinal()).setCellValue(totalF);
		r.createCell((short)Columns.ERROR_FINISHED.ordinal()).setCellValue(Statistics.relError100(totalN, totalF));
		r.getCell((short)Columns.ERROR_FINISHED.ordinal()).setCellStyle(errorStyle);
		r.createCell((short)Columns.EXP_TIME.ordinal()).setCellValue(totalW);
		r.createCell((short)Columns.ERROR_EXP_TIME.ordinal()).setCellValue(Statistics.relError100(totalT, totalW));
		r.getCell((short)Columns.ERROR_EXP_TIME.ordinal()).setCellStyle(errorStyle);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.HUNSC.cirgen.listener.GSListener#getResults()
	 */
	public GSResult getResults() {
		return results;
	}
	
}
