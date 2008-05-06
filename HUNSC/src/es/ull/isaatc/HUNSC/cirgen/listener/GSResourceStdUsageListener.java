/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.listener;

import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import es.ull.isaatc.HUNSC.cirgen.GSExcelInputWrapper;
import es.ull.isaatc.HUNSC.cirgen.OperationTheatre;
import es.ull.isaatc.HUNSC.cirgen.util.ExcelTools;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.listener.ResourceStdUsageListener;
import es.ull.isaatc.util.Statistics;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class GSResourceStdUsageListener extends ResourceStdUsageListener implements GSListener {
	private enum ResColumns {
		RES("Quirófano"),
		RUSA_TIME("T uso (real)"),
		EUSA_TIME("T uso"),
		ERROR_USA("Error T uso"),
		RAVA_TIME("T disp. (real)"),
		EAVA_TIME("T disp."),
		ERROR_AVA("Error T disp.");
		
		String name; 
		private ResColumns(String name) {
			this.name = name;
		}
	}
	
	private GSResourceStdUsageResults results = null;
	private GSExcelInputWrapper input;
	
	/**
	 * @param period
	 */
	public GSResourceStdUsageListener(GSExcelInputWrapper input) {
		super(TimeUnit.MINUTES.convert(input.getSimulationDays(), TimeUnit.DAYS));
		this.input = input;
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.listener.ResourceStdUsageListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
	 */
	@Override
	public void infoEmited(SimulationEndInfo info) {
		super.infoEmited(info);
	    OperationTheatre []opTheatres = input.getOpTheatres();
	    double []resUsageSummary = new double[opTheatres.length];
	    double []resAvailabilitySummary = new double[opTheatres.length];
	    for (OperationTheatre res : opTheatres) {
			double total = 0.0;
			for (double[] rt : getResUsage().get(res.getIndex()).getUsageTime().values()) {
				for (double val : rt)
					total += val; 
			}
			resUsageSummary[res.getIndex()] = total;
			total = 0.0;
			for (double[] rt : getResUsage().get(res.getIndex()).getAvalTime().values()) {
				for (double val : rt)
					total += val; 
			}
			resAvailabilitySummary[res.getIndex()] = total;	    	
	    }
	    results = new GSResourceStdUsageResults(resUsageSummary, resAvailabilitySummary);
	}
	
	public void setResults(HSSFWorkbook wb) {
		HSSFSheet s = wb.createSheet("Recursos");

		HSSFCellStyle headStyle = ExcelTools.getHeadStyle(wb);
		
	    HSSFCellStyle diagStyle = wb.createCellStyle();
	    diagStyle.setFillForegroundColor(HSSFColor.GOLD.index);
	    diagStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    diagStyle.setFont(ExcelTools.getBoldFont(wb));
	    
	    HSSFCellStyle errorStyle = ExcelTools.getErrorStyle(wb);

	    HSSFCellStyle theorStyle = wb.createCellStyle();
	    theorStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
	    theorStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		
		int rownum = 1;
		HSSFRow r = s.createRow(rownum++);
	    for (ResColumns col : ResColumns.values()) { 
			HSSFCell c = r.createCell((short)col.ordinal());
			c.setCellValue(new HSSFRichTextString(col.name));
			c.setCellStyle(headStyle);
	    }
	    OperationTheatre []opTheatres = input.getOpTheatres();
	    for (OperationTheatre res : opTheatres) {
			r = s.createRow(rownum++);
			r.createCell((short)ResColumns.RES.ordinal()).setCellValue(new HSSFRichTextString(res.getName()));
			r.getCell((short)ResColumns.RES.ordinal()).setCellStyle(diagStyle);
			r.createCell((short)ResColumns.RUSA_TIME.ordinal()).setCellValue(res.getRealUsage());
			r.getCell((short)ResColumns.RUSA_TIME.ordinal()).setCellStyle(theorStyle);
			r.createCell((short)ResColumns.RAVA_TIME.ordinal()).setCellValue(res.getRealAva());
			r.getCell((short)ResColumns.RAVA_TIME.ordinal()).setCellStyle(theorStyle);
			r.createCell((short)ResColumns.EUSA_TIME.ordinal()).setCellValue(results.getResUsageSummary()[res.getIndex()]);
			r.createCell((short)ResColumns.ERROR_USA.ordinal()).setCellValue(Statistics.relError100(res.getRealUsage(), results.getResUsageSummary()[res.getIndex()]));
			r.getCell((short)ResColumns.ERROR_USA.ordinal()).setCellStyle(errorStyle);
			r.createCell((short)ResColumns.EAVA_TIME.ordinal()).setCellValue(results.getResAvailabilitySummary()[res.getIndex()]);
			r.createCell((short)ResColumns.ERROR_AVA.ordinal()).setCellValue(Statistics.relError100(res.getRealAva(), results.getResAvailabilitySummary()[res.getIndex()]));
			r.getCell((short)ResColumns.ERROR_AVA.ordinal()).setCellStyle(errorStyle);
		}
	}

	/**
	 * @return the results
	 */
	public GSResult getResults() {
		return results;
	}
}
