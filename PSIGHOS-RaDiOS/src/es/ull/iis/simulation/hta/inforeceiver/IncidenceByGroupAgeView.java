/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import java.util.ArrayList;
import java.util.Locale;

import es.ull.iis.simulation.hta.AcuteComplication;
import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.SecondOrderIntervention;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

public class IncidenceByGroupAgeView implements ExperimentListener {
	private final int nExperiments;
	private final SecondOrderParamsRepository secParams;
	private final ArrayList<SecondOrderIntervention> interventions;
	private final int length;
	private final int nIntervals;
	private final int minAge;
	private final double [][] nDeaths;
	private final double [][] nAlivePatients;
	private final double[][][] nChronic;
	private final double [][][] nAcute;
	private final boolean cummulative;

	/**
	 * 
	 */
	public IncidenceByGroupAgeView(int nExperiments, SecondOrderParamsRepository secParams, int length, boolean cummulative) {
		this.cummulative = cummulative;
		this.nExperiments = nExperiments;
		this.secParams = secParams;
		this.length = length;
		this.minAge = secParams.getMinAge();
		this.nIntervals = ((BasicConfigParams.DEF_MAX_AGE - minAge) / length) + 1;
		this.interventions = secParams.getRegisteredInterventions();
		final int nInterventions = interventions.size();
		nAlivePatients = new double[nInterventions][nIntervals];
		nDeaths = new double[nInterventions][nIntervals];
		nChronic = new double[nInterventions][secParams.getRegisteredManifestations().size()][nIntervals];
		nAcute = new double[nInterventions][AcuteComplication.values().length][nIntervals];
	}

	@Override
	public void addListener(DiseaseProgressionSimulation simul) {
		simul.addInfoReceiver(new InnerListenerInstance());
	}

	@Override
	public String toString() {
		final StringBuilder str = cummulative ? new StringBuilder("Cummulated incidence") : new StringBuilder("Incidence");
		str.append(System.lineSeparator()).append("AGE");
		for (int i = 0; i < interventions.size(); i++) {
			final String name = interventions.get(i).getShortName();
			str.append("\t" + name + "_N").append("\t" + name + "_DEATH");
			for (Manifestation comp : secParams.getRegisteredManifestations()) {
				str.append("\t" + name + "_").append(comp.name());
			}
			for (AcuteComplication comp : AcuteComplication.values()) {
				str.append("\t" + name + "_").append(comp.name());				
			}
		}
		str.append(System.lineSeparator());
		for (int year = 0; year < nIntervals; year++) {
			str.append(year + minAge);
			for (int i = 0; i < interventions.size(); i++) {
				str.append("\t").append(String.format(Locale.US, "%.2f", nAlivePatients[i][year] / nExperiments));
				str.append("\t").append(String.format(Locale.US, "%.2f", nDeaths[i][year] / nExperiments));
				for (int j = 0; j < nChronic[i].length; j++) {
					str.append("\t").append(String.format(Locale.US, "%.2f", nChronic[i][j][year] / nExperiments));
				}
				for (int j = 0; j < nAcute[i].length; j++) {
					str.append("\t").append(String.format(Locale.US, "%.2f", nAcute[i][j][year] / nExperiments));
				}	
			}
			str.append(System.lineSeparator());
		}
		return str.toString();
	}
	
	/**
	 * @author Iván Castilla
	 *
	 */
	public class InnerListenerInstance extends Listener implements InnerListener {
		private final int [] nAlivePatients;
		private final int [] nDeaths;
		private final int[][] nChronic;
		private final int [][] nAcute;

		/**
		 * 
		 * @param simul
		 * @param minAge
		 * @param maxAge
		 * @param length
		 * @param detailDeaths
		 */
		public InnerListenerInstance() {
			super("Counter of patients");
			nDeaths = new int[nIntervals];
			nAlivePatients = new int[nIntervals];
			nChronic = new int[secParams.getRegisteredManifestations().size()][nIntervals];
			nAcute = new int[AcuteComplication.values().length][nIntervals];
			addGenerated(PatientInfo.class);
			addEntrance(PatientInfo.class);
			addEntrance(SimulationStartStopInfo.class);
		}

		@Override
		public void infoEmited(SimulationInfo info) {
			if (info instanceof SimulationStartStopInfo) {
				if (SimulationStartStopInfo.Type.END.equals(((SimulationStartStopInfo) info).getType())) {
					updateExperiment((DiseaseProgressionSimulation) info.getSimul());
				}
			}
			else if (info instanceof PatientInfo) {
				final PatientInfo pInfo = (PatientInfo) info;
				final Patient pat = (Patient)pInfo.getPatient();
				final int interval = (int)((pat.getAge() - minAge) / length);
				switch(pInfo.getType()) {
					case START:
						nAlivePatients[interval]++;
						break;
					case COMPLICATION:
						nChronic[pInfo.getManifestation().ordinal()][interval]++;
						break;
					case ACUTE_EVENT:
						nAcute[pInfo.getAcuteEvent().getInternalId()][interval]++;
						break;
					case DEATH:
						nDeaths[interval]++;
						break;
					default:
						break;
				}
			}
		}

		@Override
		public synchronized void updateExperiment(DiseaseProgressionSimulation simul) {
			final int interventionId = simul.getIntervention().getIdentifier();
			if (cummulative) {
				double accDeaths = 0.0;
				double accPatients = 0.0;
				final double []accAcute = new double[nAcute.length];
				final double []accChronic = new double[nChronic.length];
				for (int i = 0; i < nIntervals; i++) {
					accPatients += nAlivePatients[i];
					accDeaths += nDeaths[i];
					IncidenceByGroupAgeView.this.nAlivePatients[interventionId][i] += accPatients;
					IncidenceByGroupAgeView.this.nDeaths[interventionId][i] += accDeaths;
					for (int j = 0; j < nAcute.length; j++) {
						accAcute[j] += nAcute[j][i];
						IncidenceByGroupAgeView.this.nAcute[interventionId][j][i] += accAcute[j];
					}
					for (int j = 0; j < nChronic.length; j++) {
						accChronic[j] += nChronic[j][i];
						IncidenceByGroupAgeView.this.nChronic[interventionId][j][i] += accChronic[j];
					}
				}			
			}
			else {
				for (int i = 0; i < nIntervals; i++) {
					IncidenceByGroupAgeView.this.nAlivePatients[interventionId][i] += nAlivePatients[i];
					IncidenceByGroupAgeView.this.nDeaths[interventionId][i] += nDeaths[i];
					for (int j = 0; j < nAcute.length; j++) {
						IncidenceByGroupAgeView.this.nAcute[interventionId][j][i] += nAcute[j][i];
					}
					for (int j = 0; j < nChronic.length; j++) {
						IncidenceByGroupAgeView.this.nChronic[interventionId][j][i] += nChronic[j][i];
					}
				}			
				
			}
		}

	}
}

