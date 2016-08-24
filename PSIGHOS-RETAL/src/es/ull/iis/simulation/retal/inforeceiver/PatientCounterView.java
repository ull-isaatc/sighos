/**
 * 
 */
package es.ull.iis.simulation.retal.inforeceiver;

import java.util.EnumSet;
import java.util.TreeMap;

import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.info.PatientInfo;
import es.ull.iis.simulation.retal.params.CNVStage;

/**
 * @author Iván Castilla
 *
 */
public class PatientCounterView extends Listener {
	private int nPatients;
	private final int []nEARM = new int[2];
	private final int []nCNV = new int[2];
	private final int []nGA = new int[2];
	private final TreeMap<CNVStage, int[]> nCNVStage; 
	private int nDeaths;
	private final boolean detailed;
	
	/**
	 * @param simul
	 */
	public PatientCounterView(Simulation simul, boolean detailed) {
		super(simul, "Counter of patients");
		this.detailed = detailed;
		if (detailed) {
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
	public PatientCounterView(Simulation simul) {
		this(simul, false);		
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationEndInfo) {
			System.out.println("CREATED: " + nPatients);
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
			System.out.println("DEAD: " + nDeaths);
		}
		else if (info instanceof PatientInfo) {
			final PatientInfo p = (PatientInfo) info;
			final int eyeIndex = p.getEyeIndex();
			final Patient pat = (Patient)p.getPatient();
			switch(p.getType()) {
				case START:
					nPatients++; 
					break;
				case CHANGE_EYE_STATE:
					final EnumSet<EyeState> eye = pat.getEyeState(eyeIndex);
					if (eye.contains(EyeState.AMD_CNV)) {
						nCNV[eyeIndex]++;
					}
					else if (eye.contains(EyeState.AMD_GA)) {
						nGA[eyeIndex]++;
					}
					else if (eye.contains(EyeState.EARM)) {
						nEARM[eyeIndex]++;
					}
					break;
				case CHANGE_CNV_STAGE:
					if (detailed) {
						final CNVStage newStage = pat.getCurrentCNVStage(eyeIndex);
						nCNVStage.get(newStage)[eyeIndex]++;
					}
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
