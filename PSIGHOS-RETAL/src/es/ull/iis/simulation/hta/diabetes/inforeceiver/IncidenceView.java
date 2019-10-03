/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.inforeceiver;

import java.util.ArrayList;
import java.util.Locale;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesSimulation;
import es.ull.iis.simulation.hta.diabetes.info.T1DMPatientInfo;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.ExperimentListener.InnerListener;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

public class IncidenceView implements ExperimentListener<IncidenceView.InnerListenerInstance> {
	private final int nExperiments;
	private final SecondOrderParamsRepository secParams;
	private final ArrayList<SecondOrderDiabetesIntervention> interventions;
	private final int nIntervals;
	private final double [][] nDeaths;
	private final double[][][] nChronic;
	private final double[][][] nMainChronic;
	private final double [][][] nAcute;
	private final int nPatients;
	private final boolean cummulative;

	/**
	 * 
	 */
	public IncidenceView(int nExperiments, SecondOrderParamsRepository secParams, int nIntervals, boolean cummulative) {
		this.cummulative = cummulative;
		this.nExperiments = nExperiments;
		this.secParams = secParams;
		this.nIntervals = nIntervals;
		this.nPatients = secParams.getnPatients();
		this.interventions = secParams.getRegisteredInterventions();
		final int nInterventions = interventions.size();
		nDeaths = new double[nInterventions][nIntervals];
		nChronic = new double[nInterventions][secParams.getRegisteredComplicationStages().size()][nIntervals];
		nMainChronic = new double[nInterventions][DiabetesChronicComplications.values().length][nIntervals];
		nAcute = new double[nInterventions][DiabetesAcuteComplications.values().length][nIntervals];
	}

	@Override
	public void addListener(DiabetesSimulation simul) {
		simul.addInfoReceiver(new InnerListenerInstance());
	}

	@Override
	public String toString() {
		final StringBuilder str = cummulative ? new StringBuilder("Cummulated incidence") : new StringBuilder("Incidence");
		str.append(System.lineSeparator()).append("YEAR");
		for (int i = 0; i < interventions.size(); i++) {
			final String name = interventions.get(i).getShortName();
			str.append("\t" + name + "_DEATH");
			for (DiabetesChronicComplications comp : DiabetesChronicComplications.values()) {
				str.append("\t" + name + "_").append(comp.name());
			}
			for (DiabetesComplicationStage comp : secParams.getRegisteredComplicationStages()) {
				str.append("\t" + name + "_").append(comp.name());
			}
			for (DiabetesAcuteComplications comp : DiabetesAcuteComplications.values()) {
				str.append("\t" + name + "_").append(comp.name());				
			}
		}
		str.append(System.lineSeparator());
		for (int year = 0; year < nIntervals; year++) {
			str.append(year);
			for (int i = 0; i < interventions.size(); i++) {
				str.append("\t").append(String.format(Locale.US, "%.2f", nDeaths[i][year] / nExperiments));
				for (int j = 0; j < nMainChronic[i].length; j++) {
					str.append("\t").append(String.format(Locale.US, "%.2f", nMainChronic[i][j][year] / nExperiments));
				}
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
		private final int [] nDeaths;
		private final int[][] nChronic;
		private final int[][] nMainChronic;
		private final boolean[][] patientMainChronic;
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
			nChronic = new int[secParams.getRegisteredComplicationStages().size()][nIntervals];
			nMainChronic = new int[DiabetesChronicComplications.values().length][nIntervals];
			patientMainChronic = new boolean[DiabetesChronicComplications.values().length][nPatients];
			nAcute = new int[DiabetesAcuteComplications.values().length][nIntervals];
			addGenerated(T1DMPatientInfo.class);
			addEntrance(T1DMPatientInfo.class);
			addEntrance(SimulationStartStopInfo.class);
		}

		@Override
		public void infoEmited(SimulationInfo info) {
			if (info instanceof SimulationStartStopInfo) {
				if (SimulationStartStopInfo.Type.END.equals(((SimulationStartStopInfo) info).getType())) {
					updateExperiment((DiabetesSimulation) info.getSimul());
				}
			}
			else if (info instanceof T1DMPatientInfo) {
				final T1DMPatientInfo pInfo = (T1DMPatientInfo) info;
				final int interval = (int)(pInfo.getTs() / BasicConfigParams.YEAR_CONVERSION);
				switch(pInfo.getType()) {
					case START:
						break;
					case COMPLICATION:
						nChronic[pInfo.getComplication().ordinal()][interval]++;
						if (!patientMainChronic[pInfo.getComplication().getComplication().ordinal()][pInfo.getPatient().getIdentifier()]) {
							patientMainChronic[pInfo.getComplication().getComplication().ordinal()][pInfo.getPatient().getIdentifier()] = true;
							nMainChronic[pInfo.getComplication().getComplication().ordinal()][interval]++;
						}
						break;
					case ACUTE_EVENT:
						nAcute[pInfo.getAcuteEvent().ordinal()][interval]++;
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
		public synchronized void updateExperiment(DiabetesSimulation simul) {
			final int interventionId = simul.getIntervention().getIdentifier();
			if (cummulative) {
				double accDeaths = 0.0;
				final double []accAcute = new double[nAcute.length];
				final double []accChronic = new double[nChronic.length];
				final double []accMainChronic = new double[nMainChronic.length];
				for (int i = 0; i < nIntervals; i++) {
					accDeaths += nDeaths[i];
					IncidenceView.this.nDeaths[interventionId][i] += accDeaths / nPatients;
					for (int j = 0; j < nAcute.length; j++) {
						accAcute[j] += nAcute[j][i];
						IncidenceView.this.nAcute[interventionId][j][i] += accAcute[j] / nPatients;
					}
					for (int j = 0; j < nMainChronic.length; j++) {
						accMainChronic[j] += nMainChronic[j][i];
						IncidenceView.this.nMainChronic[interventionId][j][i] += accMainChronic[j] / nPatients;
					}
					for (int j = 0; j < nChronic.length; j++) {
						accChronic[j] += nChronic[j][i];
						IncidenceView.this.nChronic[interventionId][j][i] += accChronic[j] / nPatients;
					}
				}			
			}
			else {
				for (int i = 0; i < nIntervals; i++) {
					IncidenceView.this.nDeaths[interventionId][i] += nDeaths[i] / nPatients;
					for (int j = 0; j < nAcute.length; j++) {
						IncidenceView.this.nAcute[interventionId][j][i] += nAcute[j][i] / nPatients;
					}
					for (int j = 0; j < nMainChronic.length; j++) {
						IncidenceView.this.nMainChronic[interventionId][j][i] += nMainChronic[j][i] / nPatients;
					}
					for (int j = 0; j < nChronic.length; j++) {
						IncidenceView.this.nChronic[interventionId][j][i] += nChronic[j][i] / nPatients;
					}
				}			
				
			}
		}

	}
}

