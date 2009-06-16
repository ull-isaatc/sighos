/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.view;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import es.ull.isaatc.HUNSC.cirgen.GSExcelInputWrapper;
import es.ull.isaatc.HUNSC.cirgen.OperationTheatre;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.info.ResourceInfo;
import es.ull.isaatc.simulation.info.ResourceUsageInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.inforeceiver.View;
import es.ull.isaatc.util.ExcelTools;
import es.ull.isaatc.util.Statistics;

/**
 * Almacena y muestra los resultados correspondientes al uso y disponibilidad de los quirófanos.
 * @author Iván Castilla Rodríguez
 *
 */
public class GSResourceStdUsageView extends View implements GSView {
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
	
	private double []resUsage;
	private double []resAvailability;
	
	private GSResourceStdUsageResults results = null;
	private GSExcelInputWrapper input;
	
	/**
	 * @param period
	 */
	public GSResourceStdUsageView(Simulation simul, GSExcelInputWrapper input) {
		super(simul, "Resource Usage");
		this.input = input;
	    OperationTheatre []opTheatres = input.getOpTheatres();
	    resUsage = new double[opTheatres.length];
	    resAvailability = new double[opTheatres.length];
		addEntrance(ResourceInfo.class);
		addEntrance(ResourceUsageInfo.class);
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ResourceInfo) {
			ResourceInfo rInfo = (ResourceInfo)info;
			switch (rInfo.getType()) {
			case ROLON:
				resAvailability[rInfo.getRes().getIdentifier()] -= rInfo.getTs().getValue();
				break;
			case ROLOFF:
				resAvailability[rInfo.getRes().getIdentifier()] += rInfo.getTs().getValue();
				break;
			}
		}
		else if (info instanceof ResourceUsageInfo) {
			ResourceUsageInfo rInfo = (ResourceUsageInfo)info;
			switch (rInfo.getType()) {
			case CAUGHT:
				resUsage[rInfo.getRes().getIdentifier()] -= rInfo.getTs().getValue();
				break;
			case RELEASED:
				resUsage[rInfo.getRes().getIdentifier()] += rInfo.getTs().getValue();
				break;
			}
			
		}			
	}
	
	public void setResults(HSSFWorkbook wb) {
	    results = new GSResourceStdUsageResults(resUsage, resAvailability);

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
