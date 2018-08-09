/**
 * 
 */
package es.ull.iis.simulation.hta.retal.inforeceiver;

import java.io.PrintStream;

import es.ull.iis.simulation.hta.retal.RetalPatient;
import es.ull.iis.simulation.hta.retal.RETALSimulation;
import es.ull.iis.simulation.hta.retal.info.PatientInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * @author Iván Castilla
 *
 */
public class DiagnosticView extends Listener {
	private final PrintStream out = System.out;	
	private final int[] diagnosed = new int[RETALSimulation.DISEASES.values().length + 1];
	private final int[] tp = new int[RETALSimulation.DISEASES.values().length + 1];
	private final int[] fn = new int[RETALSimulation.DISEASES.values().length + 1];
	private int fp;
	private int tn;
	private int na;		// Not attending
	private static final RETALSimulation.DISEASES[] DISEASES = RETALSimulation.DISEASES.values();
	
	/**
	 * @param simul
	 * @param description
	 */
	public DiagnosticView() {
		super("Counter of diagnosis and screening results");
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationEndInfo) {
			out.println("TOTAL DIAGNOSED: " + diagnosed[DISEASES.length]);
			for (int i = 0; i < DISEASES.length; i++) {
				out.println("\t" + DISEASES[i] + ": " + diagnosed[i]);
			}
			out.println("TOTAL TP: " + tp[DISEASES.length]);
			for (int i = 0; i < DISEASES.length; i++) {
				out.println("\t" + DISEASES[i] + ": " + tp[i]);
			}
			out.println("TOTAL FN: " + fn[DISEASES.length]);
			for (int i = 0; i < DISEASES.length; i++) {
				out.println("\t" + DISEASES[i] + ": " + fn[i]);
			}
			out.println("TOTAL TN: " + tn);
			out.println("TOTAL FP: " + fp);
			out.println("TOTAL NA: " + na);
			out.println("TOTAL SCREENING: " + (na + tn + fp + tp[DISEASES.length] + fn[DISEASES.length]));			
		}
		else if (info instanceof PatientInfo) {
			final PatientInfo p = (PatientInfo) info;
			final RetalPatient pat = p.getPatient();
			if (p.getType() == PatientInfo.Type.DIAGNOSED) {
				for (int i = 0; i < DISEASES.length; i++) {
					if (pat.getAffectedBy().contains(DISEASES[i])) {
						diagnosed[i]++;
					}
				}
				diagnosed[DISEASES.length]++;
			}
			else if (p.getType() == PatientInfo.Type.SCREENED) {
				switch(p.getScrResult()) {
				case FN:
					for (int i = 0; i < DISEASES.length; i++) {
						if (pat.getAffectedBy().contains(DISEASES[i])) {
							fn[i]++;
						}
					}
					fn[DISEASES.length]++;					
					break;
				case FP:
					fp++;
					break;
				case TN:
					tn++;
					break;
				case NA:
					na++;
					break;
				case TP:
					for (int i = 0; i < DISEASES.length; i++) {
						if (pat.getAffectedBy().contains(DISEASES[i])) {
							tp[i]++;
						}
					}
					tp[DISEASES.length]++;					
					break;
				default:
					break;				
				}
			}
		}
	}

}
