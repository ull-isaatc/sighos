/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.listener;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import es.ull.isaatc.HUNSC.cirgen.SimGS;
import es.ull.isaatc.simulation.listener.ResourceStdUsageListener;
import es.ull.isaatc.util.Statistics;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class GSResourceStdUsageListener extends ResourceStdUsageListener implements ToExcel {
	private enum ResColumns {
		RES("Quirófano"),
		RUSA_TIME("T uso (real)"),
		RAVA_TIME("T disp. (real)"),
		EUSA_TIME("T uso"),
		ERROR_USA("Error T uso"),
		EAVA_TIME("T disp."),
		ERROR_AVA("Error T disp.");
		
		String name; 
		private ResColumns(String name) {
			this.name = name;
		}
	}
	
	private enum RoleColumns {
		ROLE("Tipo de quirófano"),
		USA_TIME("T uso"),
		AVA_TIME("T disp.");
		
		String name; 
		private RoleColumns(String name) {
			this.name = name;
		}		
	}
	SimGS simul;

	/**
	 * @param simul
	 */
	public GSResourceStdUsageListener(SimGS simul) {
		this.simul = simul;
	}

	/**
	 * @param period
	 * @param simul
	 */
	public GSResourceStdUsageListener(double period, SimGS simul) {
		super(period);
		this.simul = simul;
	}

	public void setResult(HSSFWorkbook wb) {
		HSSFSheet s = wb.createSheet("Recursos");

		int rownum = 1;
		HSSFRow r = s.createRow(rownum++);
	    HSSFCellStyle style = wb.createCellStyle();
	    style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
	    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    style.setWrapText(true);
	    for (ResColumns col : ResColumns.values()) { 
			HSSFCell c = r.createCell((short)col.ordinal());
			c.setCellValue(new HSSFRichTextString(col.name));
			c.setCellStyle(style);
	    }
	    for (SimGS.OpTheatre res : SimGS.OpTheatre.values()) {
			r = s.createRow(rownum++);
			r.createCell((short)ResColumns.RES.ordinal()).setCellValue(new HSSFRichTextString(res.getName()));
			r.createCell((short)ResColumns.RUSA_TIME.ordinal()).setCellValue(res.getRealUsage());
			r.createCell((short)ResColumns.RAVA_TIME.ordinal()).setCellValue(res.getRealAva());
			double total = 0.0;
			for (double[] rt : getResUsage().get(res.ordinal()).getUsageTime().values()) {
				for (double val : rt)
					total += val; 
			}
			r.createCell((short)ResColumns.EUSA_TIME.ordinal()).setCellValue(total);
			r.createCell((short)ResColumns.ERROR_USA.ordinal()).setCellValue(Statistics.relError100(res.getRealUsage(), total));
			total = 0.0;
			for (double[] rt : getResUsage().get(res.ordinal()).getAvalTime().values()) {
				for (double val : rt)
					total += val; 
			}
			r.createCell((short)ResColumns.EAVA_TIME.ordinal()).setCellValue(total);
			r.createCell((short)ResColumns.ERROR_AVA.ordinal()).setCellValue(Statistics.relError100(res.getRealAva(), total));
		}

		rownum++;
		r = s.createRow(rownum++);
	    for (RoleColumns col : RoleColumns.values()) { 
			HSSFCell c = r.createCell((short)col.ordinal());
			c.setCellValue(new HSSFRichTextString(col.name));
			c.setCellStyle(style);
	    }

		for (int rtId : getAvalTime().keySet()) {
			r = s.createRow(rownum++);
			r.createCell((short)RoleColumns.ROLE.ordinal()).setCellValue(new HSSFRichTextString(simul.getResourceType(rtId).getDescription()));
			double total = 0.0;
			for (double val : getRolTime().get(rtId))
				total += val;
			r.createCell((short)RoleColumns.USA_TIME.ordinal()).setCellValue(total);
			total = 0.0;
			for (double val : getAvalTime().get(rtId))
				total += val;
			r.createCell((short)RoleColumns.AVA_TIME.ordinal()).setCellValue(total);
		}
	    
	}
}
