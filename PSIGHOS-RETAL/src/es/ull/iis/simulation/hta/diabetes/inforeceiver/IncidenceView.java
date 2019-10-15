/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.inforeceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesSimulation;
import es.ull.iis.simulation.hta.diabetes.Named;
import es.ull.iis.simulation.hta.diabetes.info.DiabetesPatientInfo;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.ExperimentListener.InnerListener;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

// FIXME: Prevalence not computing correctly when using %. Only works for absolute number of patients
public class IncidenceView implements ExperimentListener<IncidenceView.InnerIncidenceListenerInstance> {
	public enum Type {
		INCIDENCE("Incidence"),
		PREVALENCE("Prevalence"),
		CUMUL_INCIDENCE("Cumulative incidence");
		
		private final String description;
		private Type(String description) {
			this.description = description;
		}
		public String getDescription() {
			return description;
		}
	}
	private final int nExperiments;
	private final SecondOrderParamsRepository secParams;
	private final ArrayList<SecondOrderDiabetesIntervention> interventions;
	private final int nIntervals;
	private final double [][] nDeaths;
	private final HashMap<Named, double[][]> nDeathsByCause;
	private final double[][][] nChronic;
	private final double[][][] nMainChronic;
	private final double [][][] nAcute;
	private final int nPatients;
	private final Type type;
	private final boolean absolute;
	private final String format;

	/**
	 * 
	 */
	public IncidenceView(int nExperiments, SecondOrderParamsRepository secParams, int nIntervals, Type type, boolean absolute) {
		this.absolute = absolute;
		this.format = absolute ? "%.0f" : "%.2f";
		this.type = type;
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
		nDeathsByCause = new HashMap<>();
	}

	@Override
	public void addListener(DiabetesSimulation simul) {
		simul.addInfoReceiver(Type.PREVALENCE.equals(type) ? new InnerPrevalenceListenerInstance() : new InnerIncidenceListenerInstance());
	}

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder(type.getDescription());
		str.append(System.lineSeparator()).append("YEAR");
		for (int i = 0; i < interventions.size(); i++) {
			final String name = interventions.get(i).getShortName();
			str.append("\t" + name + "_DEATH");
			for (final Named cause : nDeathsByCause.keySet()) {
				str.append("\t" + name + "_DEATH_" + cause);				
			}
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
				str.append("\t").append(String.format(Locale.US, format, nDeaths[i][year] / nExperiments));
				for (final Named cause : nDeathsByCause.keySet()) {
					str.append("\t").append(String.format(Locale.US, format, nDeathsByCause.get(cause)[i][year] / nExperiments));
				}
				for (int j = 0; j < nMainChronic[i].length; j++) {
					str.append("\t").append(String.format(Locale.US, format, nMainChronic[i][j][year] / nExperiments));
				}
				for (int j = 0; j < nChronic[i].length; j++) {
					str.append("\t").append(String.format(Locale.US, format, nChronic[i][j][year] / nExperiments));
				}
				for (int j = 0; j < nAcute[i].length; j++) {
					str.append("\t").append(String.format(Locale.US, format, nAcute[i][j][year] / nExperiments));
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
	public class InnerIncidenceListenerInstance extends Listener implements InnerListener {
		private final int [] nDeaths;
		private final HashMap<Named, int[]> nDeathsByCause;
		private final int[][] nChronic;
		private final int[][] nMainChronic;
		private final boolean[][] patientMainChronic;
		private final int [][] nAcute;
		private final int n;

		/**
		 * 
		 */
		public InnerIncidenceListenerInstance() {
			super("Viewer for incidence");
			n = (absolute) ? 1 : nPatients;
			nDeaths = new int[nIntervals];
			nDeathsByCause = new HashMap<>();			
			nChronic = new int[secParams.getRegisteredComplicationStages().size()][nIntervals];
			nMainChronic = new int[DiabetesChronicComplications.values().length][nIntervals];
			patientMainChronic = new boolean[DiabetesChronicComplications.values().length][nPatients];
			nAcute = new int[DiabetesAcuteComplications.values().length][nIntervals];
			addGenerated(DiabetesPatientInfo.class);
			addEntrance(DiabetesPatientInfo.class);
			addEntrance(SimulationStartStopInfo.class);
		}

		@Override
		public void infoEmited(SimulationInfo info) {
			if (info instanceof SimulationStartStopInfo) {
				if (SimulationStartStopInfo.Type.END.equals(((SimulationStartStopInfo) info).getType())) {
					updateExperiment((DiabetesSimulation) info.getSimul());
				}
			}
			else if (info instanceof DiabetesPatientInfo) {
				final DiabetesPatientInfo pInfo = (DiabetesPatientInfo) info;
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
						final Named cause = pInfo.getCauseOfDeath();
						if (cause != null) {
							if (!nDeathsByCause.containsKey(cause)) {
								nDeathsByCause.put(cause, new int[nIntervals]);
							}
							nDeathsByCause.get(cause)[interval]++;
						}
						break;
					default:
						break;
				}
			}
		}

		@Override
		public synchronized void updateExperiment(DiabetesSimulation simul) {
			final int interventionId = simul.getIntervention().getIdentifier();
			for (final Named cause : nDeathsByCause.keySet()) {
				if (!IncidenceView.this.nDeathsByCause.containsKey(cause)) {
					IncidenceView.this.nDeathsByCause.put(cause, new double[interventions.size()][nIntervals]);
				}
			}
			switch(type) {
			case INCIDENCE:
				for (int i = 0; i < nIntervals; i++) {
					IncidenceView.this.nDeaths[interventionId][i] += nDeaths[i] / n;
					for (final Named cause : nDeathsByCause.keySet()) {
						IncidenceView.this.nDeathsByCause.get(cause)[interventionId][i] += nDeathsByCause.get(cause)[i] / n;
					}
					for (int j = 0; j < nAcute.length; j++) {
						IncidenceView.this.nAcute[interventionId][j][i] += nAcute[j][i] / n;
					}
					for (int j = 0; j < nMainChronic.length; j++) {
						IncidenceView.this.nMainChronic[interventionId][j][i] += nMainChronic[j][i] / n;
					}
					for (int j = 0; j < nChronic.length; j++) {
						IncidenceView.this.nChronic[interventionId][j][i] += nChronic[j][i] / n;
					}
				}			
				break;
			case CUMUL_INCIDENCE:
			case PREVALENCE:
			default:
				double accDeaths = 0.0;
				final HashMap<Named, Double> accDeathsByCause = new HashMap<>();
				for (final Named cause : nDeathsByCause.keySet()) {
					accDeathsByCause.put(cause, 0.0);
				}
				final double []accAcute = new double[nAcute.length];
				final double []accChronic = new double[nChronic.length];
				final double []accMainChronic = new double[nMainChronic.length];
				for (int i = 0; i < nIntervals; i++) {
					accDeaths += nDeaths[i];
					IncidenceView.this.nDeaths[interventionId][i] += accDeaths / n;
					for (final Named cause : nDeathsByCause.keySet()) {
						accDeathsByCause.put(cause, accDeathsByCause.get(cause) + nDeathsByCause.get(cause)[i]);
						IncidenceView.this.nDeathsByCause.get(cause)[interventionId][i] += accDeathsByCause.get(cause) / n;
					}
					for (int j = 0; j < nAcute.length; j++) {
						accAcute[j] += nAcute[j][i];
						IncidenceView.this.nAcute[interventionId][j][i] += accAcute[j] / n;
					}
					for (int j = 0; j < nMainChronic.length; j++) {
						accMainChronic[j] += nMainChronic[j][i];
						IncidenceView.this.nMainChronic[interventionId][j][i] += accMainChronic[j] / n;
					}
					for (int j = 0; j < nChronic.length; j++) {
						accChronic[j] += nChronic[j][i];
						IncidenceView.this.nChronic[interventionId][j][i] += accChronic[j] / n;
					}
				}
				break;
			}
		}

	}

	/**
	 * @author Iván Castilla
	 *
	 */
	public class InnerPrevalenceListenerInstance extends Listener implements InnerListener {
		private final HashMap<Named, int[]> nDeathsByCause;
		private final int [] nDeaths;
		private final int[][] nChronic;
		private final int[][] nMainChronic;
		private final boolean[][] patientMainChronic;

		/**
		 * 
		 * @param simul
		 * @param minAge
		 * @param maxAge
		 * @param length
		 * @param detailDeaths
		 */
		public InnerPrevalenceListenerInstance() {
			super("Viewer for prevalence");
			nDeaths = new int[nIntervals];
			nDeathsByCause = new HashMap<>();			
			nChronic = new int[secParams.getRegisteredComplicationStages().size()][nIntervals];
			nMainChronic = new int[DiabetesChronicComplications.values().length][nIntervals];
			patientMainChronic = new boolean[DiabetesChronicComplications.values().length][nPatients];
			addGenerated(DiabetesPatientInfo.class);
			addEntrance(DiabetesPatientInfo.class);
			addEntrance(SimulationStartStopInfo.class);
		}

		@Override
		public void infoEmited(SimulationInfo info) {
			if (info instanceof SimulationStartStopInfo) {
				if (SimulationStartStopInfo.Type.END.equals(((SimulationStartStopInfo) info).getType())) {
					updateExperiment((DiabetesSimulation) info.getSimul());
				}
			}
			else if (info instanceof DiabetesPatientInfo) {
				final DiabetesPatientInfo pInfo = (DiabetesPatientInfo) info;
				// TODO: Check if it works with dead at 100
				final int interval = (pInfo.getTs() == 0.0) ? 0 : (int)(pInfo.getTs() / BasicConfigParams.YEAR_CONVERSION) + 1;
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
					case DEATH:
						nDeaths[interval]++;
						final Named cause = pInfo.getCauseOfDeath();
						if (cause != null) {
							if (!nDeathsByCause.containsKey(cause)) {
								nDeathsByCause.put(cause, new int[nIntervals]);
							}
							nDeathsByCause.get(cause)[interval]++;
						}

						for (DiabetesComplicationStage stage : pInfo.getPatient().getDetailedState()) {
							nChronic[stage.ordinal()][interval]--;
						}
						for (DiabetesChronicComplications comp : pInfo.getPatient().getState()) {
							nMainChronic[comp.ordinal()][interval]--;
						}
						break;
					default:
						break;
				}
			}
		}

		@Override
		public synchronized void updateExperiment(DiabetesSimulation simul) {
			final int interventionId = simul.getIntervention().getIdentifier();
			double accDeaths = 0.0;
			final HashMap<Named, Double> accDeathsByCause = new HashMap<>();
			for (final Named cause : nDeathsByCause.keySet()) {
				accDeathsByCause.put(cause, 0.0);
				if (!IncidenceView.this.nDeathsByCause.containsKey(cause)) {
					IncidenceView.this.nDeathsByCause.put(cause, new double[interventions.size()][nIntervals]);
				}
			}
			final double []accChronic = new double[nChronic.length];
			final double []accMainChronic = new double[nMainChronic.length];
			for (int i = 0; i < nIntervals; i++) {
				accDeaths += nDeaths[i];
				final double coef = absolute ? 1.0 : (nPatients - accDeaths);
				IncidenceView.this.nDeaths[interventionId][i] += accDeaths / (absolute ? 1.0 : nPatients);
				for (final Named cause : nDeathsByCause.keySet()) {
					accDeathsByCause.put(cause, accDeathsByCause.get(cause) + nDeathsByCause.get(cause)[i]);
					IncidenceView.this.nDeathsByCause.get(cause)[interventionId][i] += accDeathsByCause.get(cause) / (absolute ? 1.0 : nPatients);
				}
				
				if (coef != 0) {
					for (int j = 0; j < nMainChronic.length; j++) {
						accMainChronic[j] += nMainChronic[j][i];
						IncidenceView.this.nMainChronic[interventionId][j][i] += accMainChronic[j] / coef;
					}
					for (int j = 0; j < nChronic.length; j++) {
						accChronic[j] += nChronic[j][i];
						IncidenceView.this.nChronic[interventionId][j][i] += accChronic[j] / coef;
					}
				}
			}
		}

	}
}

