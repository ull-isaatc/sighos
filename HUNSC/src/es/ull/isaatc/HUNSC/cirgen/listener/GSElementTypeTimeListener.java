/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.listener;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import es.ull.isaatc.HUNSC.cirgen.SimGS;
import es.ull.isaatc.HUNSC.cirgen.SimGS.OpTheatreType;
import es.ull.isaatc.HUNSC.cirgen.util.ExcelTools;
import es.ull.isaatc.simulation.listener.ElementTypeTimeListener;
import es.ull.isaatc.util.Statistics;

/**
 * @author Iv�n
 *
 */
public class GSElementTypeTimeListener extends ElementTypeTimeListener implements ToExcel {
	private enum Columns {
		DIAG ("Diagnostico"),
		N_NOAMB ("Total NOAMB"),
		N_CREATED_NOAMB ("Creados NOAMB"),
		ERROR_CREATED_NOAMB ("Error creados NOAMB"),
		N_FINISHED_NOAMB ("Terminados NOAMB"),
		ERROR_FINISHED_NOAMB ("Error terminados NOAMB"),
		T_NOAMB ("Tiempo NOAMB"),
		T_WORKING_NOAMB ("Total tiempo NOAMB"),
		ERROR_WORKING_NOAMB ("Error tiempo NOAMB"),
		N_AMB ("Total AMB"),
		N_CREATED_AMB ("Creados AMB"),
		ERROR_CREATED_AMB ("Error creados AMB"),		
		N_FINISHED_AMB ("Terminados AMB"),
		ERROR_FINISHED_AMB ("Error terminados AMB"),	
		T_AMB ("Tiempo AMB"),
		T_WORKING_AMB ("Total tiempo AMB"),
		ERROR_WORKING_AMB ("Error tiempo AMB");
		
		String name;
		private Columns(String name) {
			this.name= name;			
		}
	}
	
	/**
	 * 
	 */
	public GSElementTypeTimeListener() {
		super();
	}
	
	public GSElementTypeTimeListener(double period) {
		super(period);
	}
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("\nPatients created (PERIOD: " + period + ")\n");
		for (SimGS.PatientType pt : SimGS.PatientType.values()) {
			str.append(pt.getName() + "\t");
			if (pt.getProbability(OpTheatreType.OR) > 0.0)
				for (int value : getElementTypeTimes().get(pt.ordinal(OpTheatreType.OR)).getCreatedElement())
					str.append(value + "\t");
			else
				for (int i = 0; i < getElementTypeTimes().get(pt.ordinal(OpTheatreType.DC)).getNPeriods(); i++)
					str.append("0\t");
			if (pt.getProbability(OpTheatreType.DC) > 0.0)
				for (int value : getElementTypeTimes().get(pt.ordinal(OpTheatreType.DC)).getCreatedElement())
				str.append(value + "\t");
			else
				for (int i = 0; i < getElementTypeTimes().get(pt.ordinal(OpTheatreType.OR)).getNPeriods(); i++)
					str.append("0\t");
			str.append("\n");
		}
		str.append("\nPatients finished (PERIOD: " + period + ")\n");
		for (SimGS.PatientType pt : SimGS.PatientType.values()) {
			str.append(pt.getName() + "\t");
			if (pt.getProbability(OpTheatreType.OR) > 0.0)
				for (int value : getElementTypeTimes().get(pt.ordinal(OpTheatreType.OR)).getFinishedElement())
					str.append(value + "\t");
			else
				for (int i = 0; i < getElementTypeTimes().get(pt.ordinal(OpTheatreType.DC)).getNPeriods(); i++)
					str.append("0\t");
			if (pt.getProbability(OpTheatreType.DC) > 0.0)
				for (int value : getElementTypeTimes().get(pt.ordinal(OpTheatreType.DC)).getFinishedElement())
				str.append(value + "\t");
			else
				for (int i = 0; i < getElementTypeTimes().get(pt.ordinal(OpTheatreType.OR)).getNPeriods(); i++)
					str.append("0\t");
			str.append("\n");
		}
		str.append("\nPatients time (PERIOD: " + period + ")\n");
		for (SimGS.PatientType pt : SimGS.PatientType.values()) {
			str.append(pt.getName() + "\t");
			if (pt.getProbability(OpTheatreType.OR) > 0.0)
				for (double value : getElementTypeTimes().get(pt.ordinal(OpTheatreType.OR)).getWorkTime())
					str.append(value + "\t");
			else
				for (int i = 0; i < getElementTypeTimes().get(pt.ordinal(OpTheatreType.DC)).getNPeriods(); i++)
					str.append("0\t");
			if (pt.getProbability(OpTheatreType.DC) > 0.0)
				for (double value : getElementTypeTimes().get(pt.ordinal(OpTheatreType.DC)).getWorkTime())
				str.append(value + "\t");
			else
				for (int i = 0; i < getElementTypeTimes().get(pt.ordinal(OpTheatreType.OR)).getNPeriods(); i++)
					str.append("0\t");
			str.append("\n");
		}
		return str.toString();
	}

	public void setResult(HSSFWorkbook wb) {
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
		double totalTNOAMB = 0.0;
		double totalNNOAMB = 0.0;
		double totalTAMB = 0.0;
		double totalNAMB = 0.0;
		double totalCNOAMB = 0.0;
		double totalCAMB = 0.0;
		double totalFNOAMB = 0.0;
		double totalFAMB = 0.0;
		double totalWNOAMB = 0.0;
		double totalWAMB = 0.0;
		for (SimGS.PatientType pt : SimGS.PatientType.values()) {
			r = s.createRow(rownum++);
			HSSFCell c = r.createCell((short)Columns.DIAG.ordinal());
			c.setCellValue(new HSSFRichTextString(pt.getName()));
			c.setCellStyle(diagStyle);
			
			r.createCell((short)Columns.N_NOAMB.ordinal()).setCellValue(pt.getTotal(SimGS.OpTheatreType.OR));
			r.getCell((short)Columns.N_NOAMB.ordinal()).setCellStyle(theorStyle);
			totalNNOAMB += pt.getTotal(SimGS.OpTheatreType.OR);
			r.createCell((short)Columns.T_NOAMB.ordinal()).setCellValue(pt.getTotalTime(SimGS.OpTheatreType.OR));
			r.getCell((short)Columns.T_NOAMB.ordinal()).setCellStyle(theorStyle);
			totalTNOAMB += pt.getTotalTime(SimGS.OpTheatreType.OR);
			r.createCell((short)Columns.N_AMB.ordinal()).setCellValue(pt.getTotal(SimGS.OpTheatreType.DC));
			r.getCell((short)Columns.N_AMB.ordinal()).setCellStyle(theorStyle);
			totalNAMB += pt.getTotal(SimGS.OpTheatreType.DC);
			r.createCell((short)Columns.T_AMB.ordinal()).setCellValue(pt.getTotalTime(SimGS.OpTheatreType.DC));
			r.getCell((short)Columns.T_AMB.ordinal()).setCellStyle(theorStyle);
			totalTAMB += pt.getTotalTime(SimGS.OpTheatreType.DC);
			if (pt.getTotal(SimGS.OpTheatreType.OR) > 0) {
				int total = 0;
				for (int value : getElementTypeTimes().get(pt.ordinal()).getCreatedElement())
					total += value;
				totalCNOAMB += total;
				r.createCell((short)Columns.N_CREATED_NOAMB.ordinal()).setCellValue(total);
				r.createCell((short)Columns.ERROR_CREATED_NOAMB.ordinal()).setCellValue(Statistics.relError100(pt.getTotal(SimGS.OpTheatreType.OR), total));
				r.getCell((short)Columns.ERROR_CREATED_NOAMB.ordinal()).setCellStyle(errorStyle);
				
				total = 0;
				for (int value : getElementTypeTimes().get(pt.ordinal()).getFinishedElement())
					total += value;
				totalFNOAMB += total;
				r.createCell((short)Columns.N_FINISHED_NOAMB.ordinal()).setCellValue(total);
				r.createCell((short)Columns.ERROR_FINISHED_NOAMB.ordinal()).setCellValue(Statistics.relError100(pt.getTotal(SimGS.OpTheatreType.OR), total));
				r.getCell((short)Columns.ERROR_FINISHED_NOAMB.ordinal()).setCellStyle(errorStyle);

				double totalD = 0.0;
				for (double value : getElementTypeTimes().get(pt.ordinal()).getWorkTime())
					totalD += value;
				totalWNOAMB += totalD;
				r.createCell((short)Columns.T_WORKING_NOAMB.ordinal()).setCellValue(totalD);
				r.createCell((short)Columns.ERROR_WORKING_NOAMB.ordinal()).setCellValue(Statistics.relError100(pt.getTotalTime(SimGS.OpTheatreType.OR), totalD));
				r.getCell((short)Columns.ERROR_WORKING_NOAMB.ordinal()).setCellStyle(errorStyle);
			}
			if (pt.getTotal(SimGS.OpTheatreType.DC) > 0) {
				int total = 0;
				for (int value : getElementTypeTimes().get(pt.ordinal() + SimGS.PatientType.values().length).getCreatedElement())
					total += value;
				totalCAMB += total;
				r.createCell((short)Columns.N_CREATED_AMB.ordinal()).setCellValue(total);
				r.createCell((short)Columns.ERROR_CREATED_AMB.ordinal()).setCellValue(Statistics.relError100(pt.getTotal(SimGS.OpTheatreType.DC), total));
				r.getCell((short)Columns.ERROR_CREATED_AMB.ordinal()).setCellStyle(errorStyle);
				
				total = 0;
				for (int value : getElementTypeTimes().get(pt.ordinal() + SimGS.PatientType.values().length).getFinishedElement())
					total += value;
				totalFAMB += total;
				r.createCell((short)Columns.N_FINISHED_AMB.ordinal()).setCellValue(total);
				r.createCell((short)Columns.ERROR_FINISHED_AMB.ordinal()).setCellValue(Statistics.relError100(pt.getTotal(SimGS.OpTheatreType.DC), total));
				r.getCell((short)Columns.ERROR_FINISHED_AMB.ordinal()).setCellStyle(errorStyle);

				double totalD = 0.0;
				for (double value : getElementTypeTimes().get(pt.ordinal() + SimGS.PatientType.values().length).getWorkTime())
					totalD += value;
				totalWAMB += totalD;
				r.createCell((short)Columns.T_WORKING_AMB.ordinal()).setCellValue(totalD);
				r.createCell((short)Columns.ERROR_WORKING_AMB.ordinal()).setCellValue(Statistics.relError100(pt.getTotalTime(SimGS.OpTheatreType.DC), totalD));
				r.getCell((short)Columns.ERROR_WORKING_AMB.ordinal()).setCellStyle(errorStyle);
			}
		}
		r = s.createRow(rownum++);
		r.createCell((short)Columns.DIAG.ordinal()).setCellValue(new HSSFRichTextString("TOTAL"));
		r.getCell((short)Columns.DIAG.ordinal()).setCellStyle(headStyle);
		r.createCell((short)Columns.N_NOAMB.ordinal()).setCellValue(totalNNOAMB);
		r.createCell((short)Columns.T_NOAMB.ordinal()).setCellValue(totalTNOAMB);
		r.createCell((short)Columns.N_AMB.ordinal()).setCellValue(totalNAMB);
		r.createCell((short)Columns.T_AMB.ordinal()).setCellValue(totalTAMB);
		r.createCell((short)Columns.N_CREATED_NOAMB.ordinal()).setCellValue(totalCNOAMB);
		r.createCell((short)Columns.N_CREATED_AMB.ordinal()).setCellValue(totalCAMB);
		r.createCell((short)Columns.ERROR_CREATED_NOAMB.ordinal()).setCellValue(Statistics.relError100(totalNNOAMB, totalCNOAMB));
		r.getCell((short)Columns.ERROR_CREATED_NOAMB.ordinal()).setCellStyle(errorStyle);
		r.createCell((short)Columns.ERROR_CREATED_AMB.ordinal()).setCellValue(Statistics.relError100(totalNAMB, totalCAMB));
		r.getCell((short)Columns.ERROR_CREATED_AMB.ordinal()).setCellStyle(errorStyle);
		r.createCell((short)Columns.N_FINISHED_NOAMB.ordinal()).setCellValue(totalFNOAMB);
		r.createCell((short)Columns.N_FINISHED_AMB.ordinal()).setCellValue(totalFAMB);
		r.createCell((short)Columns.ERROR_FINISHED_NOAMB.ordinal()).setCellValue(Statistics.relError100(totalNNOAMB, totalFNOAMB));
		r.getCell((short)Columns.ERROR_FINISHED_NOAMB.ordinal()).setCellStyle(errorStyle);
		r.createCell((short)Columns.ERROR_FINISHED_AMB.ordinal()).setCellValue(Statistics.relError100(totalNAMB, totalFAMB));
		r.getCell((short)Columns.ERROR_FINISHED_AMB.ordinal()).setCellStyle(errorStyle);
		r.createCell((short)Columns.T_WORKING_NOAMB.ordinal()).setCellValue(totalWNOAMB);
		r.createCell((short)Columns.T_WORKING_AMB.ordinal()).setCellValue(totalWAMB);
		r.createCell((short)Columns.ERROR_WORKING_NOAMB.ordinal()).setCellValue(Statistics.relError100(totalTNOAMB, totalWNOAMB));
		r.getCell((short)Columns.ERROR_WORKING_NOAMB.ordinal()).setCellStyle(errorStyle);
		r.createCell((short)Columns.ERROR_WORKING_AMB.ordinal()).setCellValue(Statistics.relError100(totalTAMB, totalWAMB));
		r.getCell((short)Columns.ERROR_WORKING_AMB.ordinal()).setCellStyle(errorStyle);
	}
	
}
