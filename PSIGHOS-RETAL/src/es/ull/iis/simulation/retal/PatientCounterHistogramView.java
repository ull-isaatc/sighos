/**
 * 
 */
package es.ull.iis.simulation.retal;

import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.View;

/**
 * @author Iván Castilla
 *
 */
public class PatientCounterHistogramView extends View {
	private final int length;
	private final int nIntervals;
	private final int minAge;
	private final int maxAge;
	private final int [] nPatients;
	private final int [] nEARM1;
	private final int [] nCNV1;
	private final int [] nGA1;
	private final int [] nEARM2;
	private final int [] nCNV2;
	private final int [] nGA2;
	private final int [] nDeaths;
	
	/**
	 * @param simul
	 * @param length
	 */
	public PatientCounterHistogramView(Simulation simul, int minAge, int maxAge, int length) {
		super(simul, "Counter of patients");
		this.length = length;
		this.nIntervals = ((maxAge - minAge) / length) + 1;
		this.minAge = minAge;
		this.maxAge = maxAge;
		nPatients = new int[nIntervals];
		nEARM1 = new int[nIntervals];
		nCNV1 = new int[nIntervals];
		nGA1 = new int[nIntervals];
		nEARM2 = new int[nIntervals];
		nCNV2 = new int[nIntervals];
		nGA2 = new int[nIntervals];
		nDeaths = new int[nIntervals];
		addEntrance(PatientInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationEndInfo) {
			System.out.println("AGE\tBASE\tEARM 1E\tCNV 1E\tGA 1E\tEARM 2E\tCNV 2E\tGA 2E\tDEATH");
			for (int i = 0; i < nIntervals; i++)
				System.out.println((minAge + i * length) + "\t" + nPatients[i] + "\t" + nEARM1[i] + "\t" + nCNV1[i] + "\t" + nGA1[i] + "\t" + nEARM2[i] + "\t" + nCNV2[i] + "\t" + nGA2[i] + "\t" + nDeaths[i]);
		}
		else if (info instanceof PatientInfo) {
			int interval = (int)((((PatientInfo) info).getPatient().getAge() - minAge) / length);
			switch(((PatientInfo) info).getType()) {
				case START:
					nPatients[interval]++; 
					break;
				case EARM1:
					nEARM1[interval]++;
					break;
				case CNV1:
					nCNV1[interval]++;
					break;
				case GA1:
					nGA1[interval]++;
					break;
				case EARM2:
					nEARM2[interval]++;
					break;
				case CNV2:
					nCNV2[interval]++;
					break;
				case GA2:
					nGA2[interval]++;
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
