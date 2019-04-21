/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.inforeceiver;

import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.info.T1DMPatientInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * @author Iván Castilla
 *
 */
public class PatientCounterHistogramView extends Listener {
	private final int length;
	private final int nIntervals;
	private final int minAge;
	private final int [] nPatients;
	private final int [] nDeaths;
	private final TreeMap<DiabetesComplicationStage, int[]> nComplications;
	private final int [] nSevereHypo;

	/**
	 * 
	 * @param simul
	 * @param minAge
	 * @param maxAge
	 * @param length
	 * @param detailDeaths
	 */
	public PatientCounterHistogramView(int minAge, int maxAge, int length, ArrayList<DiabetesComplicationStage> availableStates) {
		super("Counter of patients");
		this.length = length;
		this.nIntervals = ((maxAge - minAge) / length) + 1;
		this.minAge = minAge;
		nPatients = new int[nIntervals];
		nDeaths = new int[nIntervals];
		nComplications = new TreeMap<>();
		for (DiabetesComplicationStage st : availableStates)
			nComplications.put(st, new int[nIntervals]);
		nSevereHypo = new int[nIntervals];
		addGenerated(T1DMPatientInfo.class);
		addEntrance(T1DMPatientInfo.class);
		addEntrance(SimulationStartStopInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartStopInfo) {
			if (SimulationStartStopInfo.Type.END.equals(((SimulationStartStopInfo) info).getType())) {
				final StringBuilder strHead = new StringBuilder("AGE\tBASE\tDEATH");
				for (DiabetesComplicationStage comp : nComplications.keySet()) {
					strHead.append("\t").append(comp.name());
				}
				strHead.append("\t").append("HYPOG");
				System.out.println(strHead);
				for (int i = 0; i < nIntervals; i++) {
					final StringBuilder str = new StringBuilder((minAge + i * length) + "\t" + nPatients[i] + "\t" + nDeaths[i]);
					for (int[] val : nComplications.values()) {
						str.append("\t").append(val[i]);
					}
					str.append("\t").append(nSevereHypo[i]);
					System.out.println(str);
				}				
			}
		}
		else if (info instanceof T1DMPatientInfo) {
			final T1DMPatientInfo pInfo = (T1DMPatientInfo) info;
			final DiabetesPatient pat = (DiabetesPatient)pInfo.getPatient();
			final int interval = (int)((pat.getAge() - minAge) / length);
			switch(pInfo.getType()) {
				case START:
					nPatients[interval]++; 
					break;
				case COMPLICATION:
					nComplications.get(pInfo.getComplication())[interval]++;
					break;
				case ACUTE_EVENT:
					nSevereHypo[interval]++;
					break;
				case DEATH:
					nDeaths[interval]++;
					break;
				default:
					break;
			}
		}
	}

}
