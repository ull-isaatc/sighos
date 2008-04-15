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
import es.ull.isaatc.simulation.listener.ElementTypeTimeListener;
import es.ull.isaatc.util.Statistics;

/**
 * @author Iván
 *
 */
public class GSElementTypeTimeListener extends ElementTypeTimeListener implements ToExcel {
	private enum Columns {
		DIAG ("Diagnostico"),
		N_NOAMB ("Total NOAMB"),
		T_NOAMB ("Tiempo NOAMB"),
		N_AMB ("Total AMB"),
		T_AMB ("Tiempo AMB"),
		N_CREATED_NOAMB ("Creados NOAMB"),
		ERROR_CREATED_NOAMB ("Error creados NOAMB"),
		N_CREATED_AMB ("Creados AMB"),
		ERROR_CREATED_AMB ("Error creados AMB"),		
		N_FINISHED_NOAMB ("Terminados NOAMB"),
		ERROR_FINISHED_NOAMB ("Error terminados NOAMB"),
		N_FINISHED_AMB ("Terminados AMB"),
		ERROR_FINISHED_AMB ("Error terminados AMB"),	
		T_WORKING_NOAMB ("Total tiempo NOAMB"),
		ERROR_WORKING_NOAMB ("Error tiempo NOAMB"),
		T_WORKING_AMB ("Total tiempo AMB"),
		ERROR_WORKING_AMB ("Error tiempo NOAMB");
		
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
			if (pt.getPercOR() > 0.0)
				for (int value : getElementTypeTimes().get(pt.ordinal()).getCreatedElement())
					str.append(value + "\t");
			else
				for (int i = 0; i < getElementTypeTimes().get(pt.ordinal() + SimGS.PatientType.values().length).getNPeriods(); i++)
					str.append("0\t");
			if (pt.getPercOR() < 1.0)
				for (int value : getElementTypeTimes().get(pt.ordinal() + SimGS.PatientType.values().length).getCreatedElement())
				str.append(value + "\t");
			else
				for (int i = 0; i < getElementTypeTimes().get(pt.ordinal()).getNPeriods(); i++)
					str.append("0\t");
			str.append("\n");
		}
		str.append("\nPatients finished (PERIOD: " + period + ")\n");
		for (SimGS.PatientType pt : SimGS.PatientType.values()) {
			str.append(pt.getName() + "\t");
			if (pt.getPercOR() > 0.0)
				for (int value : getElementTypeTimes().get(pt.ordinal()).getFinishedElement())
					str.append(value + "\t");
			else
				for (int i = 0; i < getElementTypeTimes().get(pt.ordinal() + SimGS.PatientType.values().length).getNPeriods(); i++)
					str.append("0\t");
			if (pt.getPercOR() < 1.0)
				for (int value : getElementTypeTimes().get(pt.ordinal() + SimGS.PatientType.values().length).getFinishedElement())
				str.append(value + "\t");
			else
				for (int i = 0; i < getElementTypeTimes().get(pt.ordinal()).getNPeriods(); i++)
					str.append("0\t");
			str.append("\n");
		}
		str.append("\nPatients time (PERIOD: " + period + ")\n");
		for (SimGS.PatientType pt : SimGS.PatientType.values()) {
			str.append(pt.getName() + "\t");
			if (pt.getPercOR() > 0.0)
				for (double value : getElementTypeTimes().get(pt.ordinal()).getWorkTime())
					str.append(value + "\t");
			else
				for (int i = 0; i < getElementTypeTimes().get(pt.ordinal() + SimGS.PatientType.values().length).getNPeriods(); i++)
					str.append("0\t");
			if (pt.getPercOR() < 1.0)
				for (double value : getElementTypeTimes().get(pt.ordinal() + SimGS.PatientType.values().length).getWorkTime())
				str.append(value + "\t");
			else
				for (int i = 0; i < getElementTypeTimes().get(pt.ordinal()).getNPeriods(); i++)
					str.append("0\t");
			str.append("\n");
		}
		return str.toString();
	}

	public void setResult(HSSFWorkbook wb) {
		// Define fonts
	    HSSFFont boldFont = wb.createFont();
	    boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

	    HSSFFont redFont = wb.createFont();
	    redFont.setColor(HSSFColor.RED.index);
	    redFont.setItalic(true);

		// Define styles
	    HSSFCellStyle headStyle = wb.createCellStyle();
	    headStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
	    headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    headStyle.setWrapText(true);
	    headStyle.setFont(boldFont);

	    HSSFCellStyle diagStyle = wb.createCellStyle();
	    diagStyle.setFillForegroundColor(HSSFColor.GOLD.index);
	    diagStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    diagStyle.setFont(boldFont);

	    HSSFCellStyle theorStyle = wb.createCellStyle();
	    theorStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
	    theorStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

	    HSSFCellStyle errorStyle = wb.createCellStyle();
	    errorStyle.setWrapText(true);
	    errorStyle.setFont(redFont);
	    
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
			
			r.createCell((short)Columns.N_NOAMB.ordinal()).setCellValue(pt.getTotal() * pt.getPercOR());
			r.getCell((short)Columns.N_NOAMB.ordinal()).setCellStyle(theorStyle);
			totalNNOAMB += pt.getTotal() * pt.getPercOR();
			r.createCell((short)Columns.T_NOAMB.ordinal()).setCellValue((pt.getTotal() * pt.getPercOR()) * pt.getAvgOR());
			r.getCell((short)Columns.T_NOAMB.ordinal()).setCellStyle(theorStyle);
			totalTNOAMB += pt.getTotal() * pt.getPercOR() * pt.getAvgOR();
			r.createCell((short)Columns.N_AMB.ordinal()).setCellValue(pt.getTotal() * (1 - pt.getPercOR()));
			r.getCell((short)Columns.N_AMB.ordinal()).setCellStyle(theorStyle);
			totalNAMB += pt.getTotal() * (1 - pt.getPercOR());
			r.createCell((short)Columns.T_AMB.ordinal()).setCellValue((pt.getTotal() * (1 - pt.getPercOR())) * pt.getAvgDC());
			r.getCell((short)Columns.T_AMB.ordinal()).setCellStyle(theorStyle);
			totalTAMB += pt.getTotal() * (1 - pt.getPercOR()) * pt.getAvgDC();
			if (pt.getPercOR() > 0.0) {
				int total = 0;
				for (int value : getElementTypeTimes().get(pt.ordinal()).getCreatedElement())
					total += value;
				totalCNOAMB += total;
				r.createCell((short)Columns.N_CREATED_NOAMB.ordinal()).setCellValue(total);
				r.createCell((short)Columns.ERROR_CREATED_NOAMB.ordinal()).setCellValue(Statistics.relError100(pt.getTotal() * pt.getPercOR(), total));
				r.getCell((short)Columns.ERROR_CREATED_NOAMB.ordinal()).setCellStyle(errorStyle);
				
				total = 0;
				for (int value : getElementTypeTimes().get(pt.ordinal()).getFinishedElement())
					total += value;
				totalFNOAMB += total;
				r.createCell((short)Columns.N_FINISHED_NOAMB.ordinal()).setCellValue(total);
				r.createCell((short)Columns.ERROR_FINISHED_NOAMB.ordinal()).setCellValue(Statistics.relError100(pt.getTotal() * pt.getPercOR(), total));
				r.getCell((short)Columns.ERROR_FINISHED_NOAMB.ordinal()).setCellStyle(errorStyle);

				double totalD = 0.0;
				for (double value : getElementTypeTimes().get(pt.ordinal()).getWorkTime())
					totalD += value;
				totalWNOAMB += totalD;
				r.createCell((short)Columns.T_WORKING_NOAMB.ordinal()).setCellValue(totalD);
				r.createCell((short)Columns.ERROR_WORKING_NOAMB.ordinal()).setCellValue(Statistics.relError100(pt.getTotal() * pt.getPercOR() * pt.getAvgOR(), totalD));
				r.getCell((short)Columns.ERROR_WORKING_NOAMB.ordinal()).setCellStyle(errorStyle);
			}
			else
				r.createCell((short)Columns.N_CREATED_NOAMB.ordinal()).setCellValue(0);
			if (pt.getPercOR() < 1.0) {
				int total = 0;
				for (int value : getElementTypeTimes().get(pt.ordinal() + SimGS.PatientType.values().length).getCreatedElement())
					total += value;
				totalCAMB += total;
				r.createCell((short)Columns.N_CREATED_AMB.ordinal()).setCellValue(total);
				r.createCell((short)Columns.ERROR_CREATED_AMB.ordinal()).setCellValue(Statistics.relError100(pt.getTotal() * (1 - pt.getPercOR()), total));
				r.getCell((short)Columns.ERROR_CREATED_AMB.ordinal()).setCellStyle(errorStyle);
				
				total = 0;
				for (int value : getElementTypeTimes().get(pt.ordinal() + SimGS.PatientType.values().length).getFinishedElement())
					total += value;
				totalFAMB += total;
				r.createCell((short)Columns.N_FINISHED_AMB.ordinal()).setCellValue(total);
				r.createCell((short)Columns.ERROR_FINISHED_AMB.ordinal()).setCellValue(Statistics.relError100(pt.getTotal() * (1 - pt.getPercOR()), total));
				r.getCell((short)Columns.ERROR_FINISHED_AMB.ordinal()).setCellStyle(errorStyle);

				double totalD = 0.0;
				for (double value : getElementTypeTimes().get(pt.ordinal() + SimGS.PatientType.values().length).getWorkTime())
					totalD += value;
				totalWAMB += totalD;
				r.createCell((short)Columns.T_WORKING_AMB.ordinal()).setCellValue(totalD);
				r.createCell((short)Columns.ERROR_WORKING_AMB.ordinal()).setCellValue(Statistics.relError100(pt.getTotal() * (1 - pt.getPercOR()) * pt.getAvgDC(), totalD));
				r.getCell((short)Columns.ERROR_WORKING_AMB.ordinal()).setCellStyle(errorStyle);
			}
			else
				r.createCell((short)Columns.N_CREATED_AMB.ordinal()).setCellValue(0);
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
