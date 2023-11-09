/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import java.util.Collection;
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
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
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
public abstract class EpidemiologicView implements ExperimentListener {
	/** A brief text describing the listener */
	protected final String description;
	/** Number of experiments to be collected together */
	protected final int nExperiments;
	/** The original repository with the definition of the scenario */
	protected final SecondOrderParamsRepository secParams;
	/** A collection of the interventions being analyzed */
	protected final Intervention[] interventions;
	/** Number of time intervals the viewer uses to split the results */
	protected final int nIntervals;
	/** Results on the proportion of deaths by intervention and interval */
	protected final double [][] nDeaths;
	/** Results on the proportion of births (or patient spawns) by intervention and interval */
	protected final double [][] nBirths;	
	/** Results on the proportion of deaths by specific cause, intervention and interval */
	protected final HashMap<Named, double[][]> nDeathsByCause;
	/** Results on the proportion of patients with a specific manifestation by intervention and interval */
	protected final double[][][] nManifestation;
	/** Results on the proportion of patients with a specific disease by intervention and interval */
	protected final double[][][] nDisease;
	/** Total number of patients being simulated */
	protected final int nPatients;
	/** If true, shows number of patients; otherwise, shows ratios */
	protected final boolean absolute;
	/** If true, creates intervals depending on the current age of the patients; otherwise, creates intervals depending on the time from simulation start */
	protected final boolean byAge;
	/** The format for printing results */
	protected final String format;
	/** Minimum possible age for a patient */
	protected final int minAge;
	/** Length of the intervals (in years) */
	protected final int length;
	/** A flag to indicate whether the results have been processed after finishing the experiment */
	protected boolean resultsReady;

	/**
	 * Creates a epidemiologic viewer
	 * @param description A brief text describing the listener
	 * @param nExperiments Number of experiments to be collected together
	 * @param secParams The original repository with the definition of the scenario
	 * @param length Length of the intervals (in years)
	 * @param absolute If true, shows number of patients; otherwise, shows ratios
	 * @param byAge If true, creates intervals depending on the current age of the patients; otherwise, creates intervals depending on the time from simulation start
	 */
	public EpidemiologicView(String description, int nExperiments, SecondOrderParamsRepository secParams, int length, boolean absolute, boolean byAge) {
		this.description = description;
		this.absolute = absolute;
		this.byAge = byAge;
		this.format = (absolute && nExperiments == 1) ? "%.0f" : "%.3f";
		this.nExperiments = nExperiments;
		this.secParams = secParams;
		this.nPatients = secParams.getNPatients();
		this.minAge = secParams.getMinAge();
		this.length = length;
		this.nIntervals = ((BasicConfigParams.DEF_MAX_AGE - minAge) / length) + 1;
		this.interventions = secParams.getRegisteredInterventions();
		final int nInterventions = interventions.length;
		nDeaths = new double[nInterventions][nIntervals];
		nBirths = new double[nInterventions][nIntervals];
		nManifestation = new double[nInterventions][secParams.getRegisteredDiseaseProgressions().length][nIntervals];
		nDisease = new double[nInterventions][secParams.getRegisteredDiseases().length][nIntervals];
		nDeathsByCause = new HashMap<>();
		this.resultsReady = false;
	}

	@Override
	public void addListener(DiseaseProgressionSimulation simul) {
		simul.addInfoReceiver(new InnerListenerInstance());
	}
	
	protected String printHeader() {
		final StringBuilder str = new StringBuilder(description).append(absolute ? " ABS" : " REL").append(byAge ? " AGE" : "");
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
			for (DiseaseProgression comp : secParams.getRegisteredDiseaseProgressions()) {
				str.append("\t" + name + "_").append(comp.name());
			}
		}
		str.append(System.lineSeparator());
		return str.toString();
	}
	
	protected String print() {
		final StringBuilder str = new StringBuilder();
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
	
	@Override
	public String toString() {
		if (resultsReady) {
			return printHeader() + print();			
		}
		else {
			return "Epidemiologic listener: RESULTS NOT READY YET";
		}
	}

	/**
	 * Updates an individual simulation. This method is invoked every time a simulation finishes, and polishes/prepares the results according to the specific epidemiologic
	 * measure that is being collected. 
	 * @param simul Current simulation
	 * @param listener The inner listener attached to the current simulation
	 */
	public abstract void updateExperiment(DiseaseProgressionSimulation simul, InnerListenerInstance listener);
	
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
			nManifestation = new int[secParams.getRegisteredDiseaseProgressions().length][nIntervals];
			nEndManifestation = new int[secParams.getRegisteredDiseaseProgressions().length][nIntervals];
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
				final int interval = byAge ? (int)((pat.getAge() - minAge) / length) : (int)Math.ceil(pInfo.getTs() / BasicConfigParams.YEAR_CONVERSION);
				switch(pInfo.getType()) {
					case START:
						nBirths[interval]++;
						if (!patientDisease[pInfo.getPatient().getDisease().ordinal()][pInfo.getPatient().getIdentifier()]) {
							patientDisease[pInfo.getPatient().getDisease().ordinal()][pInfo.getPatient().getIdentifier()] = true;
							nDisease[pInfo.getPatient().getDisease().ordinal()][interval]++;
						}
						break;
					case START_MANIF:
						nManifestation[pInfo.getDiseaseProgression().ordinal()][interval]++;
						if (!patientDisease[pInfo.getDiseaseProgression().getDisease().ordinal()][pInfo.getPatient().getIdentifier()]) {
							patientDisease[pInfo.getDiseaseProgression().getDisease().ordinal()][pInfo.getPatient().getIdentifier()] = true;
							nDisease[pInfo.getDiseaseProgression().getDisease().ordinal()][interval]++;
						}
						break;
					case END_MANIF:
						if (interval == 0)
							pat.error("Manifestation cannot end at timestamp #0");
						else
							nEndManifestation[pInfo.getDiseaseProgression().ordinal()][interval]++;
						break;
					case DEATH:
						if (interval == 0)
							pat.error("Death cannot happen at timestamp #0");
						else {
							nDeaths[interval]++;
							final Named cause = pInfo.getCause();
							if (cause != null) {
								if (!nDeathsByCause.containsKey(cause)) {
									nDeathsByCause.put(cause, new int[nIntervals]);
								}
								nDeathsByCause.get(cause)[interval]++;
							}
							// Removes the disease and manifestations of the patient from the total account 
							for (DiseaseProgression manif : pat.getState())
								nEndManifestation[manif.ordinal()][interval]++;
							nEndDisease[pat.getDisease().ordinal()][interval]++;
						}
						break;
					default:
						break;
				}
			}
		}

		@Override
		public synchronized void updateExperiment(DiseaseProgressionSimulation simul) {
			for (final Named cause : nDeathsByCause.keySet()) {
				if (!EpidemiologicView.this.nDeathsByCause.containsKey(cause)) {
					EpidemiologicView.this.nDeathsByCause.put(cause, new double[interventions.length][nIntervals]);
				}
			}
			EpidemiologicView.this.updateExperiment(simul, this);
		}

		/**
		 * @return the nBirths
		 */
		public int[] getnBirths() {
			return nBirths;
		}

		/**
		 * @return the nDeaths
		 */
		public int[] getnDeaths() {
			return nDeaths;
		}

		/**
		 * @return the nDeathsByCause
		 */
		public int[] getnDeathsByCause(Named cause) {
			return nDeathsByCause.get(cause);
		}

		public Collection<Named> getCausesOfDeath() {
			return nDeathsByCause.keySet();
		}
		
		/**
		 * @return the nManifestation
		 */
		public int[][] getnManifestation() {
			return nManifestation;
		}

		/**
		 * @return the nEndManifestation
		 */
		public int[][] getnEndManifestation() {
			return nEndManifestation;
		}

		/**
		 * @return the nDisease
		 */
		public int[][] getnDisease() {
			return nDisease;
		}

		/**
		 * @return the nEndDisease
		 */
		public int[][] getnEndDisease() {
			return nEndDisease;
		}

	}

	/**
	 * Included just for checking whether it would be useful for new listeners...
	 * @param minAge
	 * @param maxAge
	 * @param gap
	 * @param fillToLifetime
	 * @return
	 */
	@Deprecated 
	public static double[][] buildAgesInterval(int minAge, int maxAge, int gap, boolean fillToLifetime) {
		int nGroups = (maxAge - minAge) / gap;
		if (fillToLifetime)
			nGroups++;
		final double[][] ageIntervals = new double[nGroups][2];
		for (int i = 0; i < nGroups; i++) {
			ageIntervals[i][0] = minAge + gap * i;
			ageIntervals[i][1] = minAge + gap * (i + 1);
		}
		if (fillToLifetime)
			ageIntervals[nGroups - 1][1] = BasicConfigParams.DEF_MAX_AGE;
		return ageIntervals;
	}
	// For testing
//	private static void printAgeIntervals(double[][] ageIntervals) {
//		for (double[] dd : ageIntervals) {
//			System.out.print(" [" + dd[0] + "," + dd[1] + "]");
//		}		
//		System.out.println();
//	}
//	public static void main(String[] args) {
//		double[][] intervals = buildAgesInterval(40, 80, 10, true);
//		printAgeIntervals(intervals);
//		intervals = buildAgesInterval(40, 80, 5, false);
//		printAgeIntervals(intervals);
//	}
}

