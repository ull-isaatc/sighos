/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.listener;

import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import es.ull.isaatc.HUNSC.cirgen.GSExcelInputWrapper;
import es.ull.isaatc.HUNSC.cirgen.OperationTheatre;
import es.ull.isaatc.HUNSC.cirgen.util.ExcelTools;
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
	
	private double []resUsageSummary;
	private double []resAvailabilitySummary;
	private GSExcelInputWrapper input;
	
	/**
	 * @param period
	 */
	public GSResourceStdUsageListener(GSExcelInputWrapper input) {
		super(TimeUnit.MINUTES.convert(input.getSimulationDays(), TimeUnit.DAYS));
		this.input = input;
	}
	
	/**
	 * @return the resUsage
	 */
	public double[] getResUsageSummary() {
		return resUsageSummary;
	}

	/**
	 * @return the resAvailability
	 */
	public double[] getResAvailabilitySummary() {
		return resAvailabilitySummary;
	}

	public void setResult(HSSFWorkbook wb) {
		HSSFSheet s = wb.createSheet("Recursos");

		int rownum = 1;
		HSSFRow r = s.createRow(rownum++);
	    for (ResColumns col : ResColumns.values()) { 
			HSSFCell c = r.createCell((short)col.ordinal());
			c.setCellValue(new HSSFRichTextString(col.name));
			c.setCellStyle(ExcelTools.getHeadStyle(wb));
	    }
	    OperationTheatre []opTheatres = input.getOpTheatres();
	    resUsageSummary = new double[opTheatres.length];
	    resAvailabilitySummary = new double[opTheatres.length];
	    for (OperationTheatre res : opTheatres) {
			r = s.createRow(rownum++);
			r.createCell((short)ResColumns.RES.ordinal()).setCellValue(new HSSFRichTextString(res.getName()));
			r.createCell((short)ResColumns.RUSA_TIME.ordinal()).setCellValue(res.getRealUsage());
			r.createCell((short)ResColumns.RAVA_TIME.ordinal()).setCellValue(res.getRealAva());
			double total = 0.0;
			for (double[] rt : getResUsage().get(res.getIndex()).getUsageTime().values()) {
				for (double val : rt)
					total += val; 
			}
			resUsageSummary[res.getIndex()] = total;
			r.createCell((short)ResColumns.EUSA_TIME.ordinal()).setCellValue(total);
			r.createCell((short)ResColumns.ERROR_USA.ordinal()).setCellValue(Statistics.relError100(res.getRealUsage(), total));
			total = 0.0;
			for (double[] rt : getResUsage().get(res.getIndex()).getAvalTime().values()) {
				for (double val : rt)
					total += val; 
			}
			resAvailabilitySummary[res.getIndex()] = total;
			r.createCell((short)ResColumns.EAVA_TIME.ordinal()).setCellValue(total);
			r.createCell((short)ResColumns.ERROR_AVA.ordinal()).setCellValue(Statistics.relError100(res.getRealAva(), total));
		}
	}
}
