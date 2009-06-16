/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.view;

import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import es.ull.isaatc.HUNSC.cirgen.AdmissionType;
import es.ull.isaatc.HUNSC.cirgen.GSExcelInputWrapper;
import es.ull.isaatc.HUNSC.cirgen.PatientCategory;
import es.ull.isaatc.HUNSC.cirgen.PatientType;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.info.ElementActionInfo;
import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.inforeceiver.View;
import es.ull.isaatc.util.ExcelTools;
import es.ull.isaatc.util.Statistics;

/**
 * Recolecta y muestra como una hoja Excel los siguientes resultados:
 * - Número de pacientes creados
 * - Número de pacientes que han terminado su intervención
 * - Tiempo total que han estado los pacientes siendo intervenidos
 * La información se almacena y presenta por tipo de paciente.
 * @author Iván Castilla Rodríguez
 *
 */
public class GSElementTypeTimeView extends View implements GSView {
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

	private int []createdElem;
	private int []finishedElem;
	private double []workTimeElem;

	/** Hashmap that contains the element info */
	private HashMap<Integer, ElementInfoValue> elemHashMap = new HashMap<Integer, ElementInfoValue>();

	private GSExcelInputWrapper input;
	private GSElementTypeTimeResults results = null;
	
	public GSElementTypeTimeView(Simulation simul, GSExcelInputWrapper input) {
		super(simul, "ElementType Time");
		this.input = input;
		createdElem = new int[input.getPatientCategories().length];
		finishedElem = new int[input.getPatientCategories().length];
		workTimeElem = new double[input.getPatientCategories().length];
		addEntrance(ElementActionInfo.class);
		addEntrance(ElementInfo.class);
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		ElementInfoValue eInfoValue;
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo) info;
			switch (eInfo.getType()) {
				case START:
					eInfoValue = new ElementInfoValue(eInfo.getElem().getIdentifier(),
							eInfo.getElem().getElementType().getIdentifier(),eInfo.getTs().getValue());
					elemHashMap.put(eInfo.getElem().getIdentifier(), eInfoValue);
					createdElem[eInfo.getElem().getElementType().getIdentifier()]++;
					break;
				case FINISH:
					finishedElem[eInfo.getElem().getElementType().getIdentifier()]++;
					break;
			}
		}
		else if (info instanceof ElementActionInfo) {
			ElementActionInfo eInfo = (ElementActionInfo) info;
			switch (eInfo.getType()) {
				case STAACT:
					eInfoValue = elemHashMap.get(eInfo.getElem().getIdentifier());
					eInfoValue.startActivity(eInfo.getTs().getValue());
					break;
				case ENDACT:
					eInfoValue = elemHashMap.get(eInfo.getElem().getIdentifier());
					eInfoValue.endActivity(eInfo.getTs().getValue());
					workTimeElem[eInfo.getElem().getElementType().getIdentifier()] += eInfoValue.getWorkTime();
					break;
				case RESACT:
					eInfoValue = elemHashMap.get(eInfo.getElem().getIdentifier());
					eInfoValue.startActivity(eInfo.getTs().getValue());
					break;
				case INTACT:
					eInfoValue = elemHashMap.get(eInfo.getElem().getIdentifier());
					eInfoValue.endActivity(eInfo.getTs().getValue());
					workTimeElem[eInfo.getElem().getElementType().getIdentifier()] += eInfoValue.getWorkTime();
					break;
			}
		}
	}
	
	public void setResults(HSSFWorkbook wb) {
		results = new GSElementTypeTimeResults(createdElem, finishedElem, workTimeElem);
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
			
			totalC += createdElem[pt.getIndex()];
			r.createCell((short)Columns.N_CREATED.ordinal()).setCellValue(createdElem[pt.getIndex()]);
			r.createCell((short)Columns.ERROR_CREATED.ordinal()).setCellValue(Statistics.relError100(pt.getTotal(), createdElem[pt.getIndex()]));
			r.getCell((short)Columns.ERROR_CREATED.ordinal()).setCellStyle(errorStyle);
			
			totalF += finishedElem[pt.getIndex()];
			r.createCell((short)Columns.N_FINISHED.ordinal()).setCellValue(finishedElem[pt.getIndex()]);
			r.createCell((short)Columns.ERROR_FINISHED.ordinal()).setCellValue(Statistics.relError100(pt.getTotal(), finishedElem[pt.getIndex()]));
			r.getCell((short)Columns.ERROR_FINISHED.ordinal()).setCellStyle(errorStyle);

			totalW += workTimeElem[pt.getIndex()];
			r.createCell((short)Columns.EXP_TIME.ordinal()).setCellValue(workTimeElem[pt.getIndex()]);
			r.createCell((short)Columns.ERROR_EXP_TIME.ordinal()).setCellValue(Statistics.relError100(pt.getTotalTime(), workTimeElem[pt.getIndex()]));
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

	public class ElementInfoValue {
		int id;

		double startTs;

		double endTs;
		
		double currActStartTs;

		double workTime = 0;

		int typeId;

		/**
		 * @param id
		 * @param startTs
		 */
		public ElementInfoValue(int id, int typeId, double startTs) {
			super();
			this.id = id;
			this.typeId = typeId;
			this.startTs = startTs;
			this.endTs = Double.NaN;
		}

		/**
		 * @return the typeId
		 */
		public int getTypeId() {
			return typeId;
		}

		/**
		 * @return the startTs
		 */
		public double getStartTs() {
			return startTs;
		}

		/**
		 * @param endTs
		 *            the endTs to set
		 */
		public void setEndTs(double endTs) {
			this.endTs = endTs;
		}

		/**
		 * @return the endTs
		 */
		public double getEndTs() {
			return endTs;
		}

		/**
		 * @return true if the element has finished, false elsewhere
		 */
		public boolean hasFinished() {
			if (!Double.isNaN(endTs))
				return true;
			return false;
		}
		
		public void startActivity(double ts) {
			currActStartTs = ts;
		}
		
		public void endActivity(double ts) {
			workTime = ts - currActStartTs;
		}

		/**
		 * @return the workTime
		 */
		public double getWorkTime() {
			return workTime;
		}

		public String toString() {
			return "E[" + id + "/" + typeId + "]\tS: " + startTs + "\tE: " + endTs;
		}
	}
	
	public class ElementTypeTime { // Change to public

		/** the element type identifier */
		int typeId;

		/** array that contains the working time for the elements of this type */
		private double workTime;

		/** number of elements created */
		int createdElement;

		/** number of elements finished */
		int finishedElement;

		/** number of active elements */
		int activedElement;

		public ElementTypeTime(int typeId) {
			this.typeId = typeId;
		}

		/**
		 * @return the typeId
		 */
		public int getTypeId() {
			return typeId;
		}

		/**
		 * @return the createdElement
		 */
		public int getCreatedElement() {
			return createdElement;
		}

		/**
		 * @return the finishedElement
		 */
		public int getFinishedElement() {
			return finishedElement;
		}

		/**
		 * @return the workTime
		 */
		public double getWorkTime() {
			return workTime;
		}

		public void startElement() {
			createdElement++;
		}
		
		public void finishElement() {
			finishedElement++;
		}

		public void finishActivity(ElementInfoValue eInfoValue) {
			workTime += eInfoValue.getWorkTime();
		}

		public void addElement() {
			activedElement++;
		}

		/**
		 * Calculate the average indisposed time by period
		 */
		public void finishSimulation() {
//			for (int i = 0; i < nPeriods; i++)
//				workTime[i] = workTime[i] / (double) createdElement[i];
		}


		public String getCreatedString() {
			StringBuffer str = new StringBuffer();
			str.append("ET : " + typeId);
			str.append("\t" + createdElement + "\n");
			return str.toString();
		}

		public String getFinishedString() {
			StringBuffer str = new StringBuffer();
			str.append("ET : " + typeId);
			str.append("\t" + finishedElement + "\n");
			return str.toString();
		}
		
		public String getWorkingString() {
			StringBuffer str = new StringBuffer();
			str.append("ET : " + typeId);
			str.append("\t" + workTime + "\n");
			return str.toString();
		}
	}
	
}
