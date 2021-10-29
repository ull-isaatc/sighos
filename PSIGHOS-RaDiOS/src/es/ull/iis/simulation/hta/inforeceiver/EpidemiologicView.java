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
 * It shows results for every manifestation. Is also shows general  and specific mortality.  
 * FIXME: Must review computation of relative measures, since they should depend on the number of persons at risk 
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
	/** Results on the proportion of births (or patient spawns) by intervention and interval */
	private final double [][] nBirths;	
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
	/** A flag to indicate whether the results have been processed after finishing the experiment */
	private boolean resultsReady;
	private final EpidemiologicCalculator calc; 

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
		switch(type) {
		case CUMUL_INCIDENCE:
			calc = new CummIncidenceCalculator();
			break;
		case PREVALENCE:
			calc = new PrevalenceCalculator();
			break;
		case INCIDENCE:
		default:
			calc = new IncidenceCalculator();
			break;		
		}
		this.nExperiments = nExperiments;
		this.secParams = secParams;
		this.nPatients = secParams.getnPatients();
		this.minAge = secParams.getMinAge();
		this.length = length;
		this.nIntervals = ((BasicConfigParams.DEF_MAX_AGE - minAge) / length) + 1;
		this.interventions = secParams.getRegisteredInterventions();
		final int nInterventions = interventions.length;
		nDeaths = new double[nInterventions][nIntervals];
		nBirths = new double[nInterventions][nIntervals];
		nManifestation = new double[nInterventions][secParams.getRegisteredManifestations().length][nIntervals];
		nDisease = new double[nInterventions][secParams.getRegisteredDiseases().length][nIntervals];
		nDeathsByCause = new HashMap<>();
		this.resultsReady = false;
	}

	@Override
	public void addListener(DiseaseProgressionSimulation simul) {
		simul.addInfoReceiver(new InnerListenerInstance());
	}

	@Override
	public void notifyEndExperiments() {
		calc.notifyEndExperiments();
		resultsReady = true;
	}
	
	@Override
	public String toString() {
		if (resultsReady) {
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
						str.append("\t").append(String.format(Locale.US, format, nBirths[i][year]));					
					}
					str.append("\t").append(String.format(Locale.US, format, nDeaths[i][year]));
					for (final Named cause : nDeathsByCause.keySet()) {
						str.append("\t").append(String.format(Locale.US, format, nDeathsByCause.get(cause)[i][year]));
					}
					for (int j = 0; j < nDisease[i].length; j++) {
						str.append("\t").append(String.format(Locale.US, format, nDisease[i][j][year]));
					}
					for (int j = 0; j < nManifestation[i].length; j++) {
						str.append("\t").append(String.format(Locale.US, format, nManifestation[i][j][year]));
					}
				}
				str.append(System.lineSeparator());
			}
			return str.toString();
		}
		else {
			return "Epidemiologic listener: RESULTS NOT READY";
		}
	}
	
	/**
	 * A listener that collects incidence or prevalence from a single simulation 
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public class InnerListenerInstance extends Listener implements InnerListener {
		/** Results on the proportion of births (or spawns) of patients by time interval */
		private final int [] nBirths;
		/** Results on the proportion of deaths by time interval */
		private final int [] nDeaths;
		/** Results on the proportion of deaths by specific cause, and time interval */
		private final HashMap<Named, int[]> nDeathsByCause;
		/** Results on the proportion of patients with a specific manifestation by time interval */
		private final int[][] nManifestation;
		/** Results on the proportion of patients who finish suffering a specific manifestation by time interval */
		private final int[][] nEndManifestation;
		/** Results on the proportion of patients with a specific disease by time interval */
		private final int[][] nDisease;
		/** Results on the proportion of patients who finish suffering a specific disease by time interval */
		private final int[][] nEndDisease;
		/** For each disease and patient, true if a patient already has the disease */ 
		private final boolean[][] patientDisease;

		/**
		 * Creates a listener for incidence, associated to a simulation
		 */
		public InnerListenerInstance() {
			super("Viewer for incidence or prevalence");
			nDeaths = new int[nIntervals];
			nDeathsByCause = new HashMap<>();			
			nBirths = new int[nIntervals];
			nManifestation = new int[secParams.getRegisteredManifestations().length][nIntervals];
			nEndManifestation = new int[secParams.getRegisteredManifestations().length][nIntervals];
			nDisease = new int[secParams.getRegisteredDiseases().length][nIntervals];
			nEndDisease = new int[secParams.getRegisteredDiseases().length][nIntervals];
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
				final int interval = byAge ? (int)((pat.getAge() - minAge) / length) : (int)Math.ceil((pInfo.getTs() / BasicConfigParams.YEAR_CONVERSION));
				switch(pInfo.getType()) {
					case START:
						nBirths[interval]++;
						if (!patientDisease[pInfo.getPatient().getDisease().ordinal()][pInfo.getPatient().getIdentifier()]) {
							patientDisease[pInfo.getPatient().getDisease().ordinal()][pInfo.getPatient().getIdentifier()] = true;
							nDisease[pInfo.getPatient().getDisease().ordinal()][interval]++;
						}
						break;
					case START_MANIF:
						nManifestation[pInfo.getManifestation().ordinal()][interval]++;
						if (!patientDisease[pInfo.getManifestation().getDisease().ordinal()][pInfo.getPatient().getIdentifier()]) {
							patientDisease[pInfo.getManifestation().getDisease().ordinal()][pInfo.getPatient().getIdentifier()] = true;
							nDisease[pInfo.getManifestation().getDisease().ordinal()][interval]++;
						}
						break;
					case END_MANIF:
						nEndManifestation[pInfo.getManifestation().ordinal()][interval]++;
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
						// Removes the disease and manifestations of the patient from the total account 
						for (Manifestation manif : pat.getState())
							nEndManifestation[manif.ordinal()][interval]++;
						nEndDisease[pat.getDisease().ordinal()][interval]++;
						break;
					default:
						break;
				}
			}
		}

		@Override
		public synchronized void updateExperiment(DiseaseProgressionSimulation simul) {
			calc.updateExperiment(simul, this);
		}

	}
	
	private interface EpidemiologicCalculator {
		public void updateExperiment(DiseaseProgressionSimulation simul, InnerListenerInstance listener);
		public void notifyEndExperiments();
	}

	private class IncidenceCalculator implements EpidemiologicCalculator {

		@Override
		public void updateExperiment(DiseaseProgressionSimulation simul, InnerListenerInstance listener) {
			final int interventionId = simul.getIntervention().ordinal();
			for (final Named cause : listener.nDeathsByCause.keySet()) {
				if (!EpidemiologicView.this.nDeathsByCause.containsKey(cause)) {
					EpidemiologicView.this.nDeathsByCause.put(cause, new double[interventions.length][nIntervals]);
				}
			}
			for (int i = 0; i < nIntervals; i++) {
				EpidemiologicView.this.nBirths[interventionId][i] += listener.nBirths[i];
				EpidemiologicView.this.nDeaths[interventionId][i] += listener.nDeaths[i];
				for (final Named cause : listener.nDeathsByCause.keySet()) {
					EpidemiologicView.this.nDeathsByCause.get(cause)[interventionId][i] += listener.nDeathsByCause.get(cause)[i];
				}
				for (int j = 0; j < listener.nDisease.length; j++) {
					EpidemiologicView.this.nDisease[interventionId][j][i] += listener.nDisease[j][i];
				}
				for (int j = 0; j < listener.nManifestation.length; j++) {
					EpidemiologicView.this.nManifestation[interventionId][j][i] += listener.nManifestation[j][i];
				}
			}			
		}

		@Override
		public void notifyEndExperiments() {
			if (absolute) {
				for (int year = 0; year < nIntervals; year++) {
					for (int i = 0; i < interventions.length; i++) {
						nBirths[i][year] /= (double)nExperiments;					
						nDeaths[i][year] /= (double)nExperiments;
						for (final Named cause : nDeathsByCause.keySet()) {
							nDeathsByCause.get(cause)[i][year] /= (double)nExperiments;
						}
						for (int j = 0; j < nDisease[i].length; j++) {
							nDisease[i][j][year] /= (double)nExperiments;
						}
						for (int j = 0; j < nManifestation[i].length; j++) {
							nManifestation[i][j][year] /= (double)nExperiments;
						}
					}
				}
			}
			else {
				for (int i = 0; i < interventions.length; i++) {
					int year = 0;
					nBirths[i][year] /= (double)nExperiments;
					double alive = nBirths[i][year];
					System.out.println("" + year + "\t" + alive);
					nDeaths[i][year] /= ((double)nExperiments * alive);
					for (final Named cause : nDeathsByCause.keySet()) {
						nDeathsByCause.get(cause)[i][year] /= ((double)nExperiments * alive);
					}
					for (int j = 0; j < nDisease[i].length; j++) {
						nDisease[i][j][year] /= ((double)nExperiments * alive);
					}
					for (int j = 0; j < nManifestation[i].length; j++) {
						nManifestation[i][j][year] /= ((double)nExperiments * alive);
					}
					alive -= nDeaths[i][year];
					year++;
					for (; year < nIntervals; year++) {
						System.out.println("" + year + "\t" + alive);
						nBirths[i][year] /= (double)nExperiments;					
						for (final Named cause : nDeathsByCause.keySet()) {
							nDeathsByCause.get(cause)[i][year] /= ((double)nExperiments * alive);
						}
						for (int j = 0; j < nDisease[i].length; j++) {
							nDisease[i][j][year] /= ((double)nExperiments * alive);
						}
						for (int j = 0; j < nManifestation[i].length; j++) {
							nManifestation[i][j][year] /= ((double)nExperiments * alive);
						}
						alive += nBirths[i][year] - nDeaths[i][year];
						nDeaths[i][year] /= ((double)nExperiments * (alive - nBirths[i][year] + nDeaths[i][year]));
					}
				}
			}
		}
		
	}

	private class CummIncidenceCalculator implements EpidemiologicCalculator {

		@Override
		public void updateExperiment(DiseaseProgressionSimulation simul, InnerListenerInstance listener) {
			final int interventionId = simul.getIntervention().ordinal();
			for (final Named cause : listener.nDeathsByCause.keySet()) {
				if (!EpidemiologicView.this.nDeathsByCause.containsKey(cause)) {
					EpidemiologicView.this.nDeathsByCause.put(cause, new double[interventions.length][nIntervals]);
				}
			}
			for (int i = 0; i < nIntervals; i++) {
				EpidemiologicView.this.nBirths[interventionId][i] += listener.nBirths[i];
				EpidemiologicView.this.nDeaths[interventionId][i] += listener.nDeaths[i];
				for (final Named cause : listener.nDeathsByCause.keySet()) {
					EpidemiologicView.this.nDeathsByCause.get(cause)[interventionId][i] += listener.nDeathsByCause.get(cause)[i];
				}
				for (int j = 0; j < listener.nDisease.length; j++) {
					EpidemiologicView.this.nDisease[interventionId][j][i] += listener.nDisease[j][i];
				}
				for (int j = 0; j < listener.nManifestation.length; j++) {
					EpidemiologicView.this.nManifestation[interventionId][j][i] += listener.nManifestation[j][i];
				}
			}			
		}

		@Override
		public void notifyEndExperiments() {
			if (absolute) {
				for (int i = 0; i < interventions.length; i++) {
					int year = 0;
					nBirths[i][year] /= (double)nExperiments;					
					nDeaths[i][year] /= (double)nExperiments;
					for (final Named cause : nDeathsByCause.keySet()) {
						nDeathsByCause.get(cause)[i][year] /= (double)nExperiments;
					}
					for (int j = 0; j < nDisease[i].length; j++) {
						nDisease[i][j][year] /= (double)nExperiments;
					}
					for (int j = 0; j < nManifestation[i].length; j++) {
						nManifestation[i][j][year] /= (double)nExperiments;
					}
					year++;
					for (; year < nIntervals; year++) {
						nBirths[i][year] = nBirths[i][year - 1] + nBirths[i][year] / (double)nExperiments;					
						nDeaths[i][year] = nDeaths[i][year - 1] + nDeaths[i][year] / (double)nExperiments;
						for (final Named cause : nDeathsByCause.keySet()) {
							nDeathsByCause.get(cause)[i][year] = nDeathsByCause.get(cause)[i][year - 1] + nDeathsByCause.get(cause)[i][year] / (double)nExperiments;
						}
						for (int j = 0; j < nDisease[i].length; j++) {
							nDisease[i][j][year] = nDisease[i][j][year - 1] + nDisease[i][j][year] / (double)nExperiments;
						}
						for (int j = 0; j < nManifestation[i].length; j++) {
							nManifestation[i][j][year] = nManifestation[i][j][year - 1] + nManifestation[i][j][year] / (double)nExperiments;
						}
					}
				}
			}
			else {
				for (int i = 0; i < interventions.length; i++) {
					int year = 0;
					nBirths[i][year] /= (double)nExperiments;
					double alive = nBirths[i][year];
					nDeaths[i][year] /= ((double)nExperiments * alive);
					for (final Named cause : nDeathsByCause.keySet()) {
						nDeathsByCause.get(cause)[i][year] /= ((double)nExperiments * alive);
					}
					for (int j = 0; j < nDisease[i].length; j++) {
						nDisease[i][j][year] /= ((double)nExperiments * alive);
					}
					for (int j = 0; j < nManifestation[i].length; j++) {
						nManifestation[i][j][year] /= ((double)nExperiments * alive);
					}
					year++;
					for (; year < nIntervals; year++) {
						alive += nBirths[i][year] / (double)nExperiments;
						nBirths[i][year] = nBirths[i][year - 1] + nBirths[i][year] / ((double)nExperiments * alive);					
						nDeaths[i][year] = nDeaths[i][year - 1] + nDeaths[i][year] / ((double)nExperiments * alive);
						for (final Named cause : nDeathsByCause.keySet()) {
							nDeathsByCause.get(cause)[i][year] = nDeathsByCause.get(cause)[i][year - 1] + nDeathsByCause.get(cause)[i][year] / ((double)nExperiments * alive);
						}
						for (int j = 0; j < nDisease[i].length; j++) {
							nDisease[i][j][year] = nDisease[i][j][year - 1] + nDisease[i][j][year] / ((double)nExperiments * alive);
						}
						for (int j = 0; j < nManifestation[i].length; j++) {
							nManifestation[i][j][year] = nManifestation[i][j][year - 1] + nManifestation[i][j][year] / ((double)nExperiments * alive);
						}
					}
				}
			}
		}
		
	}

	private class PrevalenceCalculator implements EpidemiologicCalculator {

		@Override
		public void updateExperiment(DiseaseProgressionSimulation simul, InnerListenerInstance listener) {
			final int interventionId = simul.getIntervention().ordinal();
			for (final Named cause : listener.nDeathsByCause.keySet()) {
				if (!EpidemiologicView.this.nDeathsByCause.containsKey(cause)) {
					EpidemiologicView.this.nDeathsByCause.put(cause, new double[interventions.length][nIntervals]);
				}
			}
				// First process base time interval
				double accDeaths = listener.nDeaths[0];
				double accPatients = listener.nBirths[0];
				final double []accManifestation = new double[listener.nManifestation.length];
				final double []accDisease = new double[listener.nDisease.length];
				final HashMap<Named, Double> accDeathsByCause = new HashMap<>();
				for (final Named cause : listener.nDeathsByCause.keySet()) {
					accDeathsByCause.put(cause, (double)listener.nDeathsByCause.get(cause)[0]);
					EpidemiologicView.this.nDeathsByCause.get(cause)[interventionId][0] = accDeathsByCause.get(cause);
				}
				EpidemiologicView.this.nDeaths[interventionId][0] = accDeaths;
				EpidemiologicView.this.nBirths[interventionId][0] = accPatients;
				for (int j = 0; j < listener.nDisease.length; j++) {
					accDisease[j] = listener.nDisease[j][0];
					EpidemiologicView.this.nDisease[interventionId][j][0] = accDisease[j];
				}
				for (int j = 0; j < listener.nManifestation.length; j++) {
					accManifestation[j] = listener.nManifestation[j][0];
					EpidemiologicView.this.nManifestation[interventionId][j][0] = accManifestation[j];
				}
				// Now process the rest of time intervals
				for (int i = 1; i < nIntervals; i++) {
					accPatients += listener.nBirths[i];
					accDeaths += listener.nDeaths[i];
					EpidemiologicView.this.nDeaths[interventionId][i] += accDeaths;
					EpidemiologicView.this.nBirths[interventionId][i] += accPatients;
					for (final Named cause : listener.nDeathsByCause.keySet()) {
						accDeathsByCause.put(cause, accDeathsByCause.get(cause) + listener.nDeathsByCause.get(cause)[i]);
						EpidemiologicView.this.nDeathsByCause.get(cause)[interventionId][i] += accDeathsByCause.get(cause);
					}
					for (int j = 0; j < listener.nDisease.length; j++) {
						accDisease[j] += listener.nDisease[j][i] - listener.nEndDisease[j][i-1];
						EpidemiologicView.this.nDisease[interventionId][j][i] += accDisease[j];
					}
					for (int j = 0; j < listener.nManifestation.length; j++) {
						accManifestation[j] += listener.nManifestation[j][i] - listener.nEndManifestation[j][i-1];
						EpidemiologicView.this.nManifestation[interventionId][j][i] += accManifestation[j];
					}
				}
		}

		@Override
		public void notifyEndExperiments() {
			if (absolute) {
				for (int year = 0; year < nIntervals; year++) {
					for (int i = 0; i < interventions.length; i++) {
						nBirths[i][year] /= (double)nExperiments;					
						nDeaths[i][year] /= (double)nExperiments;
						for (final Named cause : nDeathsByCause.keySet()) {
							nDeathsByCause.get(cause)[i][year] /= (double)nExperiments;
						}
						for (int j = 0; j < nDisease[i].length; j++) {
							nDisease[i][j][year] /= (double)nExperiments;
						}
						for (int j = 0; j < nManifestation[i].length; j++) {
							nManifestation[i][j][year] /= (double)nExperiments;
						}
					}
				}
			}
		}
		
	}
}

