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
public class PatientCounterView extends View {
	private int nPatients;
	private int nEARM1;
	private int nCNV1;
	private int nGA1;
	private int nEARM2;
	private int nCNV2;
	private int nGA2;
	private int nDeaths;
	
	/**
	 * @param simul
	 */
	public PatientCounterView(Simulation simul) {
		super(simul, "Counter of patients");
		addEntrance(PatientInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationEndInfo) {
			System.out.println("CREATED: " + nPatients);
			System.out.println("DEVELOP EARM IN FIRST EYE: " + nEARM1);
			System.out.println("DEVELOP CNV IN FIRST EYE: " + nCNV1);
			System.out.println("DEVELOP GA IN FIRST EYE: " + nGA1);
			System.out.println("DEVELOP EARM IN FELLOW EYE: " + nEARM2);
			System.out.println("DEVELOP CNV IN FELLOW EYE: " + nCNV2);
			System.out.println("DEVELOP GA IN FELLOW EYE: " + nGA2);
			System.out.println("DEAD: " + nDeaths);
		}
		else if (info instanceof PatientInfo) {
			switch(((PatientInfo) info).getType()) {
				case START:
					nPatients++; 
					break;
				case EARM1:
					nEARM1++;
					break;
				case CNV1:
					nCNV1++;
					break;
				case GA1:
					nGA1++;
					break;
				case EARM2:
					nEARM2++;
					break;
				case CNV2:
					nCNV2++;
					break;
				case GA2:
					nGA2++;
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
