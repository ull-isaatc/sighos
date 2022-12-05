/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.inforeceiver;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.info.DiabetesPatientInfo;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

public class StructuredIncidenceByGroupAgeView extends Listener implements StructuredOutputListener {
	private final int intervalLength;
	private final int nIntervals;
	private final int minAge;
	private final int [] nDeaths;
	private final int[][] nChronic;
	private final int [][] nAcute;

	/**
	 * 
	 */
	public StructuredIncidenceByGroupAgeView(SecondOrderParamsRepository secParams, int intervalLength) {
		super("Structured Incidence view");
		this.intervalLength = intervalLength;
		this.nIntervals = (int) Math.ceil((BasicConfigParams.DEF_MAX_AGE - secParams.getMinAge()) / (double)intervalLength);
		this.minAge = Math.floorDiv(secParams.getMinAge(), intervalLength) * intervalLength;
		nDeaths = new int[nIntervals];
		nChronic = new int[secParams.getRegisteredComplicationStages().size()][nIntervals];
		nAcute = new int[DiabetesAcuteComplications.values().length][nIntervals];
		addGenerated(DiabetesPatientInfo.class);
		addEntrance(DiabetesPatientInfo.class);
	}

	public static String getStrHeader(String intervention, SecondOrderParamsRepository secParams, int intervalLength) {
		final int nIntervals = (int) Math.ceil((BasicConfigParams.DEF_MAX_AGE - secParams.getMinAge()) / (double)intervalLength);
		final int minAge = Math.floorDiv(secParams.getMinAge(), intervalLength) * intervalLength;
		final StringBuilder str = new StringBuilder();
		for (int i = 0; i < nIntervals; i++) {
			final String name = intervention + "_UPTO_" + (minAge + intervalLength * (i + 1));
			str.append("N_DEATH_" + name + "\t");
			for (DiabetesComplicationStage comp : secParams.getRegisteredComplicationStages()) {
				str.append("N_" + comp.name() + "_" + name + "\t");
			}
			for (DiabetesAcuteComplications comp : DiabetesAcuteComplications.values()) {
				str.append("N_" + comp.name() + "_" + name + "\t");
			}			
		}
		return str.toString();
	}
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		for (int year = 0; year < nIntervals; year++) {
			str.append(nDeaths[year]).append("\t");
			for (int j = 0; j < nChronic.length; j++) {
				str.append(nChronic[j][year]).append("\t");
			}
			for (int j = 0; j < nAcute.length; j++) {
				str.append(nAcute[j][year]).append("\t");
			}	
		}
		return str.toString();
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof DiabetesPatientInfo) {
			final DiabetesPatientInfo pInfo = (DiabetesPatientInfo) info;
			final DiabetesPatient pat = (DiabetesPatient)pInfo.getPatient();
			final int interval = (int)Math.ceil((pat.getAge() - minAge) / (double)intervalLength) - 1;
			switch(pInfo.getType()) {
				case COMPLICATION:
					nChronic[pInfo.getComplication().ordinal()][interval]++;
					break;
				case ACUTE_EVENT:
					nAcute[pInfo.getAcuteEvent().ordinal()][interval]++;
					break;
				case DEATH:
					nDeaths[interval]++;
					break;
				case START:
				default:
					break;
			}
		}
	}
}

