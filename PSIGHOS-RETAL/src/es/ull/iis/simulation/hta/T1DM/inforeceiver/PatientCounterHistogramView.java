/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.inforeceiver;

import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.info.T1DMPatientInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * @author Iv�n Castilla
 *
 */
public class PatientCounterHistogramView extends Listener {
	private final int length;
	private final int nIntervals;
	private final int minAge;
	private final int [] nPatients;
	private final int [] nDeaths;
	private final TreeMap<T1DMComorbidity, int[]> nComplications;
	private final int [] nSevereHypo;

	/**
	 * 
	 * @param simul
	 * @param minAge
	 * @param maxAge
	 * @param length
	 * @param detailDeaths
	 */
	public PatientCounterHistogramView(int minAge, int maxAge, int length, ArrayList<T1DMComorbidity> availableStates) {
		super("Counter of patients");
		this.length = length;
		this.nIntervals = ((maxAge - minAge) / length) + 1;
		this.minAge = minAge;
		nPatients = new int[nIntervals];
		nDeaths = new int[nIntervals];
		nComplications = new TreeMap<>();
		for (T1DMComorbidity st : availableStates)
			nComplications.put(st, new int[nIntervals]);
		nSevereHypo = new int[nIntervals];
		addGenerated(T1DMPatientInfo.class);
		addEntrance(T1DMPatientInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationEndInfo) {
			final StringBuilder strHead = new StringBuilder("AGE\tBASE\tDEATH");
			for (T1DMComorbidity comp : nComplications.keySet()) {
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
		else if (info instanceof T1DMPatientInfo) {
			final T1DMPatientInfo pInfo = (T1DMPatientInfo) info;
			final T1DMPatient pat = (T1DMPatient)pInfo.getPatient();
			final int interval = (int)((pat.getAge() - minAge) / length);
			switch(pInfo.getType()) {
				case START:
					nPatients[interval]++; 
					break;
				case COMPLICATION:
					nComplications.get(pInfo.getComplication())[interval]++;
					break;
				case HYPO_EVENT:
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
