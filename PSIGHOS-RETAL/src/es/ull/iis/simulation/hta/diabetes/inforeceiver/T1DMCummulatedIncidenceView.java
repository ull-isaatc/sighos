/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.inforeceiver;

import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.info.T1DMPatientInfo;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * @author Iván Castilla
 *
 */
public class T1DMCummulatedIncidenceView extends Listener {
	private final int nIntervals;
	private final int [] nDeaths;
	private final TreeMap<DiabetesComplicationStage, int[]> nComplications;
	private final int [] nSevereHypo;
	private final int nPatients;

	/**
	 * 
	 * @param simul
	 * @param minAge
	 * @param maxAge
	 * @param length
	 * @param detailDeaths
	 */
	public T1DMCummulatedIncidenceView(int nIntervals, int nPatients, ArrayList<DiabetesComplicationStage> availableStates) {
		super("Counter of patients");
		this.nIntervals = nIntervals;
		this.nPatients = nPatients;
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
				final StringBuilder strHead = new StringBuilder("AGE\tDEATH");
				for (DiabetesComplicationStage comp : nComplications.keySet()) {
					strHead.append("\t").append(comp.name());
				}
				strHead.append("\t").append("HYPOG");
				System.out.println(strHead);
				double accDeaths = 0.0;
				double accHypos = 0.0;
				final TreeMap<DiabetesComplicationStage, Integer> accComplications = new TreeMap<>();
				for (DiabetesComplicationStage comp : nComplications.keySet())
					accComplications.put(comp,  0);
				for (int i = 0; i < nIntervals; i++) {
					accDeaths += nDeaths[i];
					accHypos += nSevereHypo[i];
					final StringBuilder str = new StringBuilder("" + i + "\t").append(accDeaths  / nPatients);
					for (DiabetesComplicationStage comp : nComplications.keySet()) {
						int accValue = accComplications.get(comp) + nComplications.get(comp)[i];
						accComplications.put(comp, accValue);
						str.append("\t").append((double)accValue / nPatients);					
					}
					str.append("\t").append(accHypos / nPatients);
					System.out.println(str);
				}
			}
		}
		else if (info instanceof T1DMPatientInfo) {
			final T1DMPatientInfo pInfo = (T1DMPatientInfo) info;
			final int interval = (int)(pInfo.getTs() / BasicConfigParams.YEAR_CONVERSION);
			switch(pInfo.getType()) {
				case START:
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
