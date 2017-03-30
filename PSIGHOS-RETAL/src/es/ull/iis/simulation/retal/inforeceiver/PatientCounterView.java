/**
 * 
 */
package es.ull.iis.simulation.retal.inforeceiver;

import java.util.EnumSet;
import java.util.TreeMap;

import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.retal.RETALSimulation;
import es.ull.iis.simulation.retal.info.PatientInfo;
import es.ull.iis.simulation.retal.params.CNVStage;

/**
 * @author Iván Castilla
 *
 */
public class PatientCounterView extends Listener {
	private final EnumSet<RETALSimulation.DISEASES> diseases;
	private int nPatients;
	private final int []nEARM = new int[2];
	private final int []nCNV = new int[2];
	private final int []nGA = new int[2];
	private final TreeMap<CNVStage, int[]> nCNVStage; 
	private final int []nNPDR = new int[2];
	private final int []nNonHRPDR = new int[2];
	private final int []nHRPDR = new int[2];
	private final int []nCSME = new int[2];
	private int nDiabetes;
	private int nDeaths;
	private final boolean detailed;
	
	/**
	 * @param simul
	 */
	public PatientCounterView(EnumSet<RETALSimulation.DISEASES> diseases, boolean detailed) {
		super("Counter of patients");
		this.diseases = EnumSet.copyOf(diseases);
		this.detailed = detailed;
		if (detailed && diseases.contains(RETALSimulation.DISEASES.ARMD)) {
			nCNVStage = new TreeMap<CNVStage, int[]>();
			for (CNVStage.Type t : CNVStage.Type.values()) {
				for (CNVStage.Position pos : CNVStage.Position.values()) {
					nCNVStage.put(new CNVStage(t, pos) , new int[2]);
				}
			}
		}
		else {
			nCNVStage = null;
		}
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	/**
	 * @param simul
	 */
	public PatientCounterView(EnumSet<RETALSimulation.DISEASES> diseases) {
		this(diseases, false);		
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationEndInfo) {
			System.out.println("CREATED: " + nPatients);
			if (diseases.contains(RETALSimulation.DISEASES.ARMD)) {
				System.out.println("DEVELOP EARM IN FIRST EYE: " + nEARM[0]);
				System.out.println("DEVELOP EARM IN FELLOW EYE: " + nEARM[1]);
				System.out.println("DEVELOP GA IN FIRST EYE: " + nGA[0]);
				System.out.println("DEVELOP GA IN FELLOW EYE: " + nGA[1]);
				System.out.println("DEVELOP CNV IN FIRST EYE: " + nCNV[0]);
				System.out.println("DEVELOP CNV IN FELLOW EYE: " + nCNV[1]);
				if (detailed) {
					int count1 = 0;
					int count2 = 0;
					for (CNVStage stage : CNVStage.ALL_STAGES) { 
						System.out.println("DEVELOP CNV (" + stage.getType() + ", " + stage.getPosition() + ") IN FIRST EYE: " + nCNVStage.get(stage)[0]);
						System.out.println("DEVELOP CNV (" + stage.getType() + ", " + stage.getPosition() + ") IN FELLOW EYE: " + nCNVStage.get(stage)[1]);
						count1 += nCNVStage.get(stage)[0];
						count2 += nCNVStage.get(stage)[1];
					}
					if (isDebugMode()) {
						System.out.println("TOTAL CHANGES CNV IN FIRST EYE: " + count1);
						System.out.println("TOTAL CHANGES CNV IN FELLOW EYE: " + count2);
					}
				}
			}
			if (diseases.contains(RETALSimulation.DISEASES.DR)) {
				System.out.println("DEVELOP DIABETES: " + nDiabetes);
				System.out.println("DEVELOP NPDR IN FIRST EYE: " + nNPDR[0]);
				System.out.println("DEVELOP NPDR IN FELLOW EYE: " + nNPDR[1]);
				System.out.println("DEVELOP NON HR PDR IN FIRST EYE: " + nNonHRPDR[0]);
				System.out.println("DEVELOP NON HR PDR IN FELLOW EYE: " + nNonHRPDR[1]);
				System.out.println("DEVELOP HR PDR IN FIRST EYE: " + nHRPDR[0]);
				System.out.println("DEVELOP HR PDR IN FELLOW EYE: " + nHRPDR[1]);
				System.out.println("DEVELOP CSME IN FIRST EYE: " + nCSME[0]);
				System.out.println("DEVELOP CSME IN FELLOW EYE: " + nCSME[1]);
				
			}
			System.out.println("DEAD: " + nDeaths);
		}
		else if (info instanceof PatientInfo) {
			final PatientInfo p = (PatientInfo) info;
			final int eyeIndex = p.getEyeIndex();
			switch(p.getType()) {
				case START:
					nPatients++; 
					break;
				case CHANGE_EYE_STATE:
					switch(p.getToState()) {
					case AMD_CNV:
						nCNV[eyeIndex]++;
						break;
					case AMD_GA:
						nGA[eyeIndex]++;
						break;
					case CSME:
						nCSME[eyeIndex]++;
						break;
					case EARM:
						nEARM[eyeIndex]++;
						break;
					case HEALTHY:
						break;
					case HR_PDR:
						nHRPDR[eyeIndex]++;
						break;
					case NON_HR_PDR:
						nNonHRPDR[eyeIndex]++;
						break;
					case NPDR:
						nNPDR[eyeIndex]++;
						break;
					default:
						break;
					
					}
					break;
				case CHANGE_CNV_STAGE:
					if (detailed && diseases.contains(RETALSimulation.DISEASES.ARMD)) {
						nCNVStage.get(p.getToCNVStage())[eyeIndex]++;
					}
					break;
				case DIABETES:
					nDiabetes++;
					break;
				case DEATH:
					nDeaths++;
					break;
				default:
					break;
			}
		}
	}

}
