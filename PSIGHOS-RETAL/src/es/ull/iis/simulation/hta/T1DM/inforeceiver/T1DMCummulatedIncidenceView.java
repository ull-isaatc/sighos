/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.inforeceiver;

import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.T1DM.T1DMComplicationStage;
import es.ull.iis.simulation.hta.T1DM.info.T1DMPatientInfo;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationTimeInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * @author Iván Castilla
 *
 */
public class T1DMCummulatedIncidenceView extends Listener {
	private final int nIntervals;
	private final int [] nDeaths;
	private final TreeMap<T1DMComplicationStage, int[]> nComplications;
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
	public T1DMCummulatedIncidenceView(int nIntervals, int nPatients, ArrayList<T1DMComplicationStage> availableStates) {
		super("Counter of patients");
		this.nIntervals = nIntervals;
		this.nPatients = nPatients;
		nDeaths = new int[nIntervals];
		nComplications = new TreeMap<>();
		for (T1DMComplicationStage st : availableStates)
			nComplications.put(st, new int[nIntervals]);
		nSevereHypo = new int[nIntervals];
		addGenerated(T1DMPatientInfo.class);
		addEntrance(T1DMPatientInfo.class);
		addEntrance(SimulationTimeInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationTimeInfo) {
			if (SimulationTimeInfo.Type.END.equals(((SimulationTimeInfo) info).getType())) {
				final StringBuilder strHead = new StringBuilder("AGE\tDEATH");
				for (T1DMComplicationStage comp : nComplications.keySet()) {
					strHead.append("\t").append(comp.name());
				}
				strHead.append("\t").append("HYPOG");
				System.out.println(strHead);
				double accDeaths = 0.0;
				double accHypos = 0.0;
				final TreeMap<T1DMComplicationStage, Integer> accComplications = new TreeMap<>();
				for (T1DMComplicationStage comp : nComplications.keySet())
					accComplications.put(comp,  0);
				for (int i = 0; i < nIntervals; i++) {
					accDeaths += nDeaths[i];
					accHypos += nSevereHypo[i];
					final StringBuilder str = new StringBuilder("" + i + "\t").append(accDeaths  / nPatients);
					for (T1DMComplicationStage comp : nComplications.keySet()) {
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
