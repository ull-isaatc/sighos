/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import java.util.HashMap;
import java.util.Locale;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * A viewer for epidemiologic evolution of the simulated patients. Shows information on prevalence, incidence or cumulative incidence; either relative or absolute (number of patients);
 * either by age or according to the time from the simulation start.
 * It can show a single result or aggregated results from various simulation experiments (by using the nExperiments parameter).
 * It shows separate results for each intervention.
 * It shows results for every acute complication, chronic complication and chronic complication stage. Is also shows general  and specific mortality.  
 * FIXME: Prevalence not computing correctly when using %. Only works for absolute number of patients 
 * @author Iván Castilla
 *
 */
public class EpidemiologicView implements ExperimentListener {
	/**
	 * Type of epidemiologic information
	 * @author Iván Castilla
	 */
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
	/** Number of experiments to be collected together */
	private final int nExperiments;
	/** The original repository with the definition of the scenario */
	private final SecondOrderParamsRepository secParams;
	/** A collection of the interventions being analyzed */
	private final Intervention[] interventions;
	/** Number of time intervals the viewer uses to split the results */
	private final int nIntervals;
	/** Results on the proportion of deaths by intervention and interval */
	private final double [][] nDeaths;
	/** Results on the proportion of alive patients by intervention and interval */
	private final double [][] nAlivePatients;	
	/** Results on the proportion of deaths by specific cause, intervention and interval */
	private final HashMap<Named, double[][]> nDeathsByCause;
	/** Results on the proportion of patients with a specific manifestation by intervention and interval */
	private final double[][][] nManifestation;
	/** Results on the proportion of patients with a specific disease by intervention and interval */
	private final double[][][] nDisease;
	/** Total number of patients being simulated */
	private final int nPatients;
	/** Type of epidemiologic information being collected */
	private final Type type;
	/** If true, shows number of patients; otherwise, shows ratios */
	private final boolean absolute;
	/** If true, creates intervals depending on the current age of the patients; otherwise, creates intervals depending on the time from simulation start */
	private final boolean byAge;
	/** The format for printing results */
	private final String format;
	/** Minimum possible age for a patient */
	private final int minAge;
	/** Length of the intervals (in years) */
	private final int length;

	/**
	 * Creates a epidemiologic viewer
	 * @param nExperiments Number of experiments to be collected together
	 * @param secParams The original repository with the definition of the scenario
	 * @param length Length of the intervals (in years)
	 * @param type Type of epidemiologic information being collected
	 * @param absolute If true, shows number of patients; otherwise, shows ratios
	 * @param byAge If true, creates intervals depending on the current age of the patients; otherwise, creates intervals depending on the time from simulation start
	 */
	public EpidemiologicView(int nExperiments, SecondOrderParamsRepository secParams, int length, Type type, boolean absolute, boolean byAge) {
		this.absolute = absolute;
		this.byAge = byAge;
		this.format = (absolute || nExperiments != 1) ? "%.0f" : "%.2f";
		this.type = type;
		this.nExperiments = nExperiments;
		this.secParams = secParams;
		this.nPatients = secParams.getnPatients();
		this.minAge = secParams.getMinAge();
		this.length = length;
		this.nIntervals = ((BasicConfigParams.DEF_MAX_AGE - minAge) / length) + 1;
		this.interventions = secParams.getRegisteredInterventions();
		final int nInterventions = interventions.length;
		nDeaths = new double[nInterventions][nIntervals];
		nAlivePatients = new double[nInterventions][nIntervals];
		nManifestation = new double[nInterventions][secParams.getRegisteredManifestations().length][nIntervals];
		nDisease = new double[nInterventions][secParams.getRegisteredDiseases().length][nIntervals];
		nDeathsByCause = new HashMap<>();
	}

	@Override
	public void addListener(DiseaseProgressionSimulation simul) {
		simul.addInfoReceiver(Type.PREVALENCE.equals(type) ? new InnerPrevalenceListenerInstance() : new InnerIncidenceListenerInstance());
	}

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder(type.getDescription()).append(absolute ? " ABS" : " REL").append(byAge ? " AGE" : "");
		str.append(System.lineSeparator()).append(byAge ? "AGE" : "YEAR");
		for (int i = 0; i < interventions.length; i++) {
			final String name = interventions[i].name();
			if (byAge)
				str.append("\t" + name + "_N");
			str.append("\t" + name + "_DEATH");
			for (final Named cause : nDeathsByCause.keySet()) {
				str.append("\t" + name + "_DEATH_" + cause);				
			}
			for (Disease dis : secParams.getRegisteredDiseases()) {
				str.append("\t" + name + "_").append(dis.name());
			}
			for (Manifestation comp : secParams.getRegisteredManifestations()) {
				str.append("\t" + name + "_").append(comp.name());
			}
		}
		str.append(System.lineSeparator());
		for (int year = 0; year < nIntervals; year++) {
			str.append(byAge ? (length * year) + minAge : (length *year));
			for (int i = 0; i < interventions.length; i++) {
				if (byAge) {
					str.append("\t").append(String.format(Locale.US, format, nAlivePatients[i][year] / (double)nExperiments));					
				}
				str.append("\t").append(String.format(Locale.US, format, nDeaths[i][year] / (double)nExperiments));
				for (final Named cause : nDeathsByCause.keySet()) {
					str.append("\t").append(String.format(Locale.US, format, nDeathsByCause.get(cause)[i][year] / (double)nExperiments));
				}
				for (int j = 0; j < nDisease[i].length; j++) {
					str.append("\t").append(String.format(Locale.US, format, nDisease[i][j][year] / (double)nExperiments));
				}
				for (int j = 0; j < nManifestation[i].length; j++) {
					str.append("\t").append(String.format(Locale.US, format, nManifestation[i][j][year] / (double)nExperiments));
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
		private final int [] nAlivePatients;
		private final int [] nDeaths;
		private final HashMap<Named, int[]> nDeathsByCause;
		private final int[][] nManifestation;
		private final int[][] nDisease;
		private final boolean[][] patientDisease;
		private final double n;

		/**
		 * 
		 */
		public InnerIncidenceListenerInstance() {
			super("Viewer for incidence");
			n = (absolute) ? 1 : nPatients;
			nDeaths = new int[nIntervals];
			nDeathsByCause = new HashMap<>();			
			nAlivePatients = new int[nIntervals];
			nManifestation = new int[secParams.getRegisteredManifestations().length][nIntervals];
			nDisease = new int[secParams.getRegisteredDiseases().length][nIntervals];
			patientDisease = new boolean[secParams.getRegisteredDiseases().length][nPatients];
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
				final int interval = byAge ? (int)((pat.getAge() - minAge) / length) : (int)(pInfo.getTs() / BasicConfigParams.YEAR_CONVERSION);
				switch(pInfo.getType()) {
					case START:
						nAlivePatients[interval]++;
						break;
					case MANIFESTATION:
						nManifestation[pInfo.getManifestation().ordinal()][interval]++;
						if (!patientDisease[pInfo.getManifestation().getDisease().ordinal()][pInfo.getPatient().getIdentifier()]) {
							patientDisease[pInfo.getManifestation().getDisease().ordinal()][pInfo.getPatient().getIdentifier()] = true;
							nDisease[pInfo.getManifestation().getDisease().ordinal()][interval]++;
						}
						break;
					case DEATH:
						nDeaths[interval]++;
						final Named cause = pInfo.getCause();
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
		public synchronized void updateExperiment(DiseaseProgressionSimulation simul) {
			final int interventionId = simul.getIntervention().ordinal();
			for (final Named cause : nDeathsByCause.keySet()) {
				if (!EpidemiologicView.this.nDeathsByCause.containsKey(cause)) {
					EpidemiologicView.this.nDeathsByCause.put(cause, new double[interventions.length][nIntervals]);
				}
			}
			if (Type.INCIDENCE.equals(type)) {
				for (int i = 0; i < nIntervals; i++) {
					EpidemiologicView.this.nAlivePatients[interventionId][i] += nAlivePatients[i] / n;
					EpidemiologicView.this.nDeaths[interventionId][i] += nDeaths[i] / n;
					for (final Named cause : nDeathsByCause.keySet()) {
						EpidemiologicView.this.nDeathsByCause.get(cause)[interventionId][i] += nDeathsByCause.get(cause)[i] / n;
					}
					for (int j = 0; j < nDisease.length; j++) {
						EpidemiologicView.this.nDisease[interventionId][j][i] += nDisease[j][i] / n;
					}
					for (int j = 0; j < nManifestation.length; j++) {
						EpidemiologicView.this.nManifestation[interventionId][j][i] += nManifestation[j][i] / n;
					}
				}			
			}
			else {
				double accDeaths = 0.0;
				final HashMap<Named, Double> accDeathsByCause = new HashMap<>();
				for (final Named cause : nDeathsByCause.keySet()) {
					accDeathsByCause.put(cause, 0.0);
				}
				double accPatients = 0.0;
				final double []accManifestation = new double[nManifestation.length];
				final double []accDisease = new double[nDisease.length];
				for (int i = 0; i < nIntervals; i++) {
					accPatients += nAlivePatients[i];
					accDeaths += nDeaths[i];
					EpidemiologicView.this.nDeaths[interventionId][i] += accDeaths / n;
					EpidemiologicView.this.nAlivePatients[interventionId][i] += accPatients / n;
					for (final Named cause : nDeathsByCause.keySet()) {
						accDeathsByCause.put(cause, accDeathsByCause.get(cause) + nDeathsByCause.get(cause)[i]);
						EpidemiologicView.this.nDeathsByCause.get(cause)[interventionId][i] += accDeathsByCause.get(cause) / n;
					}
					for (int j = 0; j < nDisease.length; j++) {
						accDisease[j] += nDisease[j][i];
						EpidemiologicView.this.nDisease[interventionId][j][i] += accDisease[j] / n;
					}
					for (int j = 0; j < nManifestation.length; j++) {
						accManifestation[j] += nManifestation[j][i];
						EpidemiologicView.this.nManifestation[interventionId][j][i] += accManifestation[j] / n;
					}
				}
				
			}
		}

	}

	/**
	 * @author Iván Castilla
	 *
	 */
	public class InnerPrevalenceListenerInstance extends Listener implements InnerListener {
		private final int [] nAlivePatients;
		private final HashMap<Named, int[]> nDeathsByCause;
		private final int [] nDeaths;
		private final int[][] nManifestation;
		private final int[][] nDisease;
		private final boolean[][] patientDisease;

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
			nAlivePatients = new int[nIntervals];
			nDeaths = new int[nIntervals];
			nDeathsByCause = new HashMap<>();			
			nManifestation = new int[secParams.getRegisteredManifestations().length][nIntervals];
			nDisease = new int[secParams.getRegisteredDiseases().length][nIntervals];
			patientDisease = new boolean[secParams.getRegisteredDiseases().length][nPatients];
			addEntrance(SimulationStartStopInfo.class);
		}

		private void updatePatientInfo(Patient pat) {
			final double initAge = pat.getInitAge(); 
			final double ageAtDeath = pat.getAge();

			for (final Manifestation manif : secParams.getRegisteredManifestations())
				pat.getTimeToManifestation(manif);
		}
		@Override
		public void infoEmited(SimulationInfo info) {
			if (info instanceof SimulationStartStopInfo) {
				if (SimulationStartStopInfo.Type.END.equals(((SimulationStartStopInfo) info).getType())) {
					final Patient[] patients = ((DiseaseProgressionSimulation)info.getSimul()).getGeneratedPatients();
					for (final Patient pat : patients)
						updatePatientInfo(pat);
					updateExperiment((DiseaseProgressionSimulation) info.getSimul());
				}
			}
		}

		@Override
		public synchronized void updateExperiment(DiseaseProgressionSimulation simul) {
			final int interventionId = simul.getIntervention().ordinal();
			double accDeaths = 0.0;
			final HashMap<Named, Double> accDeathsByCause = new HashMap<>();
			for (final Named cause : nDeathsByCause.keySet()) {
				accDeathsByCause.put(cause, 0.0);
				if (!EpidemiologicView.this.nDeathsByCause.containsKey(cause)) {
					EpidemiologicView.this.nDeathsByCause.put(cause, new double[interventions.length][nIntervals]);
				}
			}
			final double []accManifestation = new double[nManifestation.length];
			final double []accDisease = new double[nDisease.length];
			for (int i = 0; i < nIntervals; i++) {
				accDeaths += nDeaths[i];
				final double coef = absolute ? 1.0 : (nPatients - accDeaths);
				EpidemiologicView.this.nDeaths[interventionId][i] += accDeaths / (absolute ? 1.0 : nPatients);
				for (final Named cause : nDeathsByCause.keySet()) {
					accDeathsByCause.put(cause, accDeathsByCause.get(cause) + nDeathsByCause.get(cause)[i]);
					EpidemiologicView.this.nDeathsByCause.get(cause)[interventionId][i] += accDeathsByCause.get(cause) / (absolute ? 1.0 : nPatients);
				}
				
				if (coef != 0) {
					for (int j = 0; j < nDisease.length; j++) {
						accDisease[j] += nDisease[j][i];
						EpidemiologicView.this.nDisease[interventionId][j][i] += accDisease[j] / coef;
					}
					for (int j = 0; j < nManifestation.length; j++) {
						accManifestation[j] += nManifestation[j][i];
						EpidemiologicView.this.nManifestation[interventionId][j][i] += accManifestation[j] / coef;
					}
				}
			}
		}

	}


//	public class InnerPrevalenceListenerInstance extends Listener implements InnerListener {
//		private final HashMap<Named, int[]> nDeathsByCause;
//		private final int [] nDeaths;
//		private final int[][] nManifestation;
//		private final int[][] nDisease;
//		private final boolean[][] patientDisease;
//
//		/**
//		 * 
//		 * @param simul
//		 * @param minAge
//		 * @param maxAge
//		 * @param length
//		 * @param detailDeaths
//		 */
//		public InnerPrevalenceListenerInstance() {
//			super("Viewer for prevalence");
//			nDeaths = new int[nIntervals];
//			nDeathsByCause = new HashMap<>();			
//			nManifestation = new int[secParams.getRegisteredComplicationStages().size()][nIntervals];
//			nDisease = new int[ManifestationComplication.values().length][nIntervals];
//			patientDisease = new boolean[ManifestationComplication.values().length][nPatients];
//			addGenerated(PatientInfo.class);
//			addEntrance(PatientInfo.class);
//			addEntrance(SimulationStartStopInfo.class);
//		}
//
//		@Override
//		public void infoEmited(SimulationInfo info) {
//			if (info instanceof SimulationStartStopInfo) {
//				if (SimulationStartStopInfo.Type.END.equals(((SimulationStartStopInfo) info).getType())) {
//					updateExperiment((DiseaseProgressionSimulation) info.getSimul());
//				}
//			}
//			else if (info instanceof PatientInfo) {
//				final PatientInfo pInfo = (PatientInfo) info;
//				// TODO: Check if it works with dead at 100
//				final int interval = (pInfo.getTs() == 0.0) ? 0 : (int)(pInfo.getTs() / BasicConfigParams.YEAR_CONVERSION) + 1;
//				switch(pInfo.getType()) {
//					case START:
//						break;
//					case COMPLICATION:
//						nManifestation[pInfo.getComplication().ordinal()][interval]++;
//						if (!patientDisease[pInfo.getComplication().getComplication().ordinal()][pInfo.getPatient().getIdentifier()]) {
//							patientDisease[pInfo.getComplication().getComplication().ordinal()][pInfo.getPatient().getIdentifier()] = true;
//							nDisease[pInfo.getComplication().getComplication().ordinal()][interval]++;
//						}
//						break;
//					case DEATH:
//						nDeaths[interval]++;
//						final Named cause = pInfo.getCauseOfDeath();
//						if (cause != null) {
//							if (!nDeathsByCause.containsKey(cause)) {
//								nDeathsByCause.put(cause, new int[nIntervals]);
//							}
//							nDeathsByCause.get(cause)[interval]++;
//						}
//
//						for (Manifestation stage : pInfo.getPatient().getDetailedState()) {
//							nManifestation[stage.ordinal()][interval]--;
//						}
//						for (ManifestationComplication comp : pInfo.getPatient().getState()) {
//							nDisease[comp.ordinal()][interval]--;
//						}
//						break;
//					default:
//						break;
//				}
//			}
//		}
//
//		@Override
//		public synchronized void updateExperiment(DiseaseProgressionSimulation simul) {
//			final int interventionId = simul.getIntervention().getIdentifier();
//			double accDeaths = 0.0;
//			final HashMap<Named, Double> accDeathsByCause = new HashMap<>();
//			for (final Named cause : nDeathsByCause.keySet()) {
//				accDeathsByCause.put(cause, 0.0);
//				if (!EpidemiologicView.this.nDeathsByCause.containsKey(cause)) {
//					EpidemiologicView.this.nDeathsByCause.put(cause, new double[interventions.length][nIntervals]);
//				}
//			}
//			final double []accManifestation = new double[nManifestation.length];
//			final double []accDisease = new double[nDisease.length];
//			for (int i = 0; i < nIntervals; i++) {
//				accDeaths += nDeaths[i];
//				final double coef = absolute ? 1.0 : (nPatients - accDeaths);
//				EpidemiologicView.this.nDeaths[interventionId][i] += accDeaths / (absolute ? 1.0 : nPatients);
//				for (final Named cause : nDeathsByCause.keySet()) {
//					accDeathsByCause.put(cause, accDeathsByCause.get(cause) + nDeathsByCause.get(cause)[i]);
//					EpidemiologicView.this.nDeathsByCause.get(cause)[interventionId][i] += accDeathsByCause.get(cause) / (absolute ? 1.0 : nPatients);
//				}
//				
//				if (coef != 0) {
//					for (int j = 0; j < nDisease.length; j++) {
//						accDisease[j] += nDisease[j][i];
//						EpidemiologicView.this.nDisease[interventionId][j][i] += accDisease[j] / coef;
//					}
//					for (int j = 0; j < nManifestation.length; j++) {
//						accManifestation[j] += nManifestation[j][i];
//						EpidemiologicView.this.nManifestation[interventionId][j][i] += accManifestation[j] / coef;
//					}
//				}
//			}
//		}
//
//	}
}

