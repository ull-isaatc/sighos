/**
 * 
 */
package es.ull.iis.simulation.hta;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import es.ull.iis.simulation.hta.inforeceiver.AnnualCostView;
import es.ull.iis.simulation.hta.inforeceiver.BudgetImpactView;
import es.ull.iis.simulation.hta.inforeceiver.CostListener;
import es.ull.iis.simulation.hta.inforeceiver.CumulativeIncidenceView;
import es.ull.iis.simulation.hta.inforeceiver.ExperimentListener;
import es.ull.iis.simulation.hta.inforeceiver.IncidenceView;
import es.ull.iis.simulation.hta.inforeceiver.PopulationAttributeListener;
import es.ull.iis.simulation.hta.inforeceiver.IndividualTime2ManifestationView;
import es.ull.iis.simulation.hta.inforeceiver.LYListener;
import es.ull.iis.simulation.hta.inforeceiver.PatientInfoView;
import es.ull.iis.simulation.hta.inforeceiver.PrevalenceView;
import es.ull.iis.simulation.hta.inforeceiver.QALYListener;
import es.ull.iis.simulation.hta.inforeceiver.ScreeningTestPerformanceView;
import es.ull.iis.simulation.hta.inforeceiver.TimeFreeOfComplicationsView;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.interventions.ScreeningIntervention;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation;

/**
 * @author Iván Castilla
 *
 */
public abstract class HTAExperiment {
	private final static String SEPARATOR = "################################################################################################################";
	/** How many replications have to be run to show a new progression percentage message */
	private static final int N_PROGRESS = 20;
	
	private final PrintProgress progress;
	/** Enables parallel execution of simulations */
	private final boolean parallel;
	/** Disables most messages */
	private final boolean quiet;
	private final Intervention[] interventions;
	/** Number of simulations to run */
	private final int nRuns;
	/** Number of patients to be generated during each simulation */
	private final int nPatients;
	/** Time horizon for the simulation */
	private final int timeHorizon;
	private final SecondOrderParamsRepository secParams;
	private final Discount discountCost;
	private final Discount discountEffect;
	
	private final EnumSet<Outputs> printOutputs;
	private static final String OUTPUTS_SUFIX = "_outputs";
	private final PrintWriter out;
	private final PrintWriter outListeners;
	private final PatientInfoView patientListener;
	private final ArrayList<ExperimentListener> expListeners;
	private final ArrayList<ExperimentListener> baseCaseExpListeners;

	public HTAExperiment(CommonArguments arguments, ByteArrayOutputStream simResult) throws MalformedSimulationModelException {
		BasicConfigParams.STUDY_YEAR = arguments.year;
		this.secParams = createRepository(arguments);
		final String validity = secParams.checkValidity();
		if (validity != null) {
			throw new MalformedSimulationModelException(validity);
		}

		this.interventions = secParams.getRegisteredInterventions();
		this.nRuns = secParams.getNRuns();
		this.nPatients = secParams.getNPatients();
		this.progress = new PrintProgress((nRuns > N_PROGRESS) ? nRuns / N_PROGRESS : 1, nRuns + 1);
		
		final Discount[] discounts = configureDiscounts (arguments.discount);
		this.discountCost = discounts[0];
		this.discountEffect = discounts[1];
		this.timeHorizon = (arguments.timeHorizon == -1) ? BasicConfigParams.DEF_MAX_AGE - secParams.getMinAge() + 1 : arguments.timeHorizon;
		this.parallel = arguments.parallel;
		this.quiet = arguments.quiet;
		this.patientListener = (arguments.singlePatientOutput != -1) ? new PatientInfoView(arguments.singlePatientOutput) : null;
		this.expListeners = new ArrayList<>();
		this.baseCaseExpListeners = new ArrayList<>();
		this.printOutputs = configurePrintOutputs(arguments.bi, arguments.individualOutcomes, arguments.breakdownCost);				
		if (printOutputs.contains(Outputs.BREAKDOWN_COST)) {
			expListeners.add(new AnnualCostView(nRuns, secParams, discountCost));
			baseCaseExpListeners.add(new AnnualCostView(1, secParams, discountCost));
		}
		if (printOutputs.contains(Outputs.BI)) {
			expListeners.add(new BudgetImpactView(nRuns, secParams, BasicConfigParams.DEF_BI_YEARS));
			baseCaseExpListeners.add(new BudgetImpactView(1, secParams, BasicConfigParams.DEF_BI_YEARS));
		}
		final ArrayList<EpidemiologicOutputFormat> toPrint = configureOutputFormats(arguments.epidem);
		final PrintWriter[] outputPrintWriters = configureOutputPrintWriters(arguments.outputFileName, simResult, printOutputs.size(), toPrint.size());
		this.out = outputPrintWriters[0];
		this.outListeners = outputPrintWriters[1];
		for (final EpidemiologicOutputFormat format : toPrint) {
			switch(format.getType()) {
			case CUMUL_INCIDENCE:
				expListeners.add(new CumulativeIncidenceView(nRuns, secParams, format.getInterval(), format.isAbsolute(), format.isByAge()));
				baseCaseExpListeners.add(new CumulativeIncidenceView(1, secParams, format.getInterval(), format.isAbsolute(), format.isByAge()));
				break;
			case INCIDENCE:
				expListeners.add(new IncidenceView(nRuns, secParams, format.getInterval(), format.isAbsolute(), format.isByAge()));
				baseCaseExpListeners.add(new IncidenceView(1, secParams, format.getInterval(), format.isAbsolute(), format.isByAge()));
				break;
			case PREVALENCE:
				expListeners.add(new PrevalenceView(nRuns, secParams, format.getInterval(), format.isAbsolute(), format.isByAge()));
				baseCaseExpListeners.add(new PrevalenceView(1, secParams, format.getInterval(), format.isAbsolute(), format.isByAge()));
				break;
			default:
				break;
			
			}
		}		

		secParams.registerAllSecondOrderParams();
	}

	/**
	 * @param filename
	 * @param printOutputsSize
	 * @param formatsSize
	 * @return
	 */
	private static PrintWriter[] configureOutputPrintWriters (String filename, ByteArrayOutputStream os, Integer printOutputsSize, Integer formatsSize) {
		// Set outputs: different files for simulation outputs and for other outputs. If no file name is specified or an error arises, standard output is used
		PrintWriter[] result = new PrintWriter[2];
		if (filename == null && os == null) {
			result[0] = new PrintWriter(System.out);
			result[1] = result[0];
		} else if (filename != null) {
			try {
				result[0] = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
			} catch (IOException e) {
				e.printStackTrace();
				result[0] = new PrintWriter(System.out);
			}
			if (formatsSize > 0 || printOutputsSize > 0) {
				try {
					result[1] = new PrintWriter(new BufferedWriter(new FileWriter(filename + OUTPUTS_SUFIX)));
				} catch (IOException e) {
					e.printStackTrace();
					result[1] = new PrintWriter(System.out);
				}
			} else {
				result[1] = new PrintWriter(System.out);
			}
		} else if (os != null) {
			result[0] = new PrintWriter(os);
			result[1] = result[0];
		} 
		return result;
		
	}
	
	/**
	 * @param formatList
	 * @return
	 */
	private static ArrayList<EpidemiologicOutputFormat> configureOutputFormats(List<String> formatList) {
		final ArrayList<EpidemiologicOutputFormat> formats = new ArrayList<>();
		for (final String format : formatList) {
			final EpidemiologicOutputFormat f = EpidemiologicOutputFormat.build(format);
			if (f != null)
				formats.add(f);
		}
		return formats;
	}

	/**
	 * @param printBudgetImpact
	 * @param printIndividualOutcomes
	 * @param printBreakdownCost
	 * @return
	 */
	private static EnumSet<Outputs> configurePrintOutputs(boolean printBudgetImpact, boolean printIndividualOutcomes, boolean printBreakdownCost) {
		final EnumSet<Outputs> printOutputs = EnumSet.noneOf(Outputs.class);
		if (printBudgetImpact)
			printOutputs.add(Outputs.BI);
		if (printIndividualOutcomes)
			printOutputs.add(Outputs.INDIVIDUAL_OUTCOMES);
		if (printBreakdownCost)
			printOutputs.add(Outputs.BREAKDOWN_COST);
		return printOutputs;
	}

	/**
	 * @param discounts
	 * @return
	 */
	private static Discount[] configureDiscounts (List<Double> discounts) {
		Discount[] result = new Discount[2];
		if (discounts != null) {
			if (discounts.size() == 0) {
				// Use default discount
				result[0] = new Discount(BasicConfigParams.DEF_DISCOUNT_RATE);
				result[1] = new Discount(BasicConfigParams.DEF_DISCOUNT_RATE);
			} else if (discounts.size() == 1) {
				final double value = discounts.get(0);
				result[0] = (value == 0.0) ? Discount.ZERO_DISCOUNT : new Discount(value);
				result[1] = (value == 0.0) ? Discount.ZERO_DISCOUNT : new Discount(value);
			} else {
				final double valueCost = discounts.get(0);
				final double valueEffect = discounts.get(1);
				result[0] = (valueCost == 0.0) ? Discount.ZERO_DISCOUNT : new Discount(valueCost);
				result[1] = (valueEffect == 0.0) ? Discount.ZERO_DISCOUNT : new Discount(valueEffect);
			}
		}
		return result;
	}

	public abstract SecondOrderParamsRepository createRepository(CommonArguments arguments) throws MalformedSimulationModelException;
	
	public SecondOrderParamsRepository getRepository() {
		return secParams;
	}
	
	/**
	 * Launches the simulations
	 */
	public void run() {
		long t = System.currentTimeMillis();
		if (!quiet)
			out.println(BasicConfigParams.printOptions());
		out.println(getStrHeader());
		simulateInterventions(0, true);
		if (baseCaseExpListeners.size() > 0) {
			outListeners.println(SEPARATOR);
			outListeners.println(BasicConfigParams.STR_SEP);
			outListeners.println("Base case");
			outListeners.println(BasicConfigParams.STR_SEP);
			for (ExperimentListener listener : baseCaseExpListeners) {
				listener.notifyEndExperiments();
				outListeners.println(listener);
			}
		}
		progress.print();
		if (nRuns > 0) {
			out.println(getStrHeader());
			if (parallel) {
				final int maxThreads = Runtime.getRuntime().availableProcessors();
				try {
					final Thread[] workers = new Thread[maxThreads];
					for (int i = 0; i < maxThreads; i++) {
						workers[i] = new Thread(new ProblemExecutor(out, i + 1, maxThreads));
						workers[i].start();
					}
					for (int i = 0; i < maxThreads; i++) {
						workers[i].join();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				new ProblemExecutor(out, 1, 1).run();
			}
			if (expListeners.size() > 0) {
				outListeners.println(SEPARATOR);
				outListeners.println(BasicConfigParams.STR_SEP);
				outListeners.println("PSA");
				outListeners.println(BasicConfigParams.STR_SEP);
				for (ExperimentListener listener : expListeners) {
					listener.notifyEndExperiments();
					outListeners.println(listener);
				}
			}
		}

		if (!quiet)
			System.out.println("Execution time: " + ((System.currentTimeMillis() - t) / 1000) + " sec");
		out.close();
		outListeners.close();
	}

	private String getStrHeader() {
		final StringBuilder str = new StringBuilder();
		str.append("SIM\tN\t");
		for (int i = 0; i < interventions.length; i++) {
			final String shortName = interventions[i].name();
			str.append(CostListener.getStrHeader(shortName));
			str.append(LYListener.getStrHeader(shortName));
			str.append(QALYListener.getStrHeader(shortName));
			str.append(PopulationAttributeListener.getStrHeader(shortName, secParams.getPopulation().getPatientAttributes()));
			if (interventions[i] instanceof ScreeningIntervention)
				str.append(ScreeningTestPerformanceView.getStrHeader(shortName));
		}
		str.append(TimeFreeOfComplicationsView.getStrHeader(false, secParams));
		str.append(secParams.getStrHeader());
		return str.toString();
	}

	private String print(DiseaseProgressionSimulation simul, CostListener[] costListeners, LYListener[] lyListeners, QALYListener[] qalyListeners, PopulationAttributeListener[] paramListeners, TimeFreeOfComplicationsView timeFreeListener,
			ScreeningTestPerformanceView[] screenListeners) {
		final StringBuilder str = new StringBuilder();
		str.append(simul.getIdentifier()).append("\t").append(nPatients).append("\t");
		for (int i = 0; i < interventions.length; i++) {
			str.append(costListeners[i]);
			str.append(lyListeners[i]);
			str.append(qalyListeners[i]);
			str.append(paramListeners[i]);
			if (interventions[i] instanceof ScreeningIntervention)
				str.append(screenListeners[i]);
		}
		str.append(timeFreeListener).append(secParams.print(simul.getIdentifier()));
		return str.toString();
	}

	/**
	 * Runs the simulations for each intervention
	 * 
	 * @param id       Simulation identifier
	 * @param baseCase True if we are running the base case
	 */
	private void simulateInterventions(int id, boolean baseCase) {
		final int nInterventions = interventions.length;
		DiseaseProgressionSimulation simul = new DiseaseProgressionSimulation(id, interventions[0], secParams, timeHorizon);
		
		final TimeFreeOfComplicationsView timeFreeListener = new TimeFreeOfComplicationsView(secParams, false);
		final PopulationAttributeListener[] paramListeners = new PopulationAttributeListener[nInterventions];
		final CostListener[] costListeners = new CostListener[nInterventions];
		final LYListener[] lyListeners = new LYListener[nInterventions];
		final QALYListener[] qalyListeners = new QALYListener[nInterventions];
		final ScreeningTestPerformanceView[] screenListeners = new ScreeningTestPerformanceView[nInterventions];

		for (int i = 0; i < nInterventions; i++) {
			costListeners[i] = new CostListener(discountCost, nPatients);
			paramListeners[i] = new PopulationAttributeListener(nPatients, secParams.getPopulation().getPatientAttributes());
			lyListeners[i] = new LYListener(discountEffect, nPatients);
			qalyListeners[i] = new QALYListener(secParams.getDisutilityCombinationMethod(), discountEffect, nPatients);
			screenListeners[i] = (interventions[i] instanceof ScreeningIntervention) ? new ScreeningTestPerformanceView(secParams) : null;
		}
		simul.addInfoReceiver(costListeners[0]);
		simul.addInfoReceiver(paramListeners[0]);
		simul.addInfoReceiver(lyListeners[0]);
		simul.addInfoReceiver(qalyListeners[0]);
		simul.addInfoReceiver(timeFreeListener);
		IndividualTime2ManifestationView indTimeToEventListener = null;
		if (printOutputs.contains(Outputs.INDIVIDUAL_OUTCOMES)) {
			indTimeToEventListener = new IndividualTime2ManifestationView(secParams);
			simul.addInfoReceiver(indTimeToEventListener);
		}
		if (interventions[0] instanceof ScreeningIntervention)
			simul.addInfoReceiver(screenListeners[0]);
		if (patientListener != null)
			simul.addInfoReceiver(patientListener);
		if (baseCase) {
			for (ExperimentListener listener : baseCaseExpListeners) {
				listener.addListener(simul);
			}
		} else {
			for (ExperimentListener listener : expListeners) {
				listener.addListener(simul);
			}
		}
		simul.run();
		for (int i = 1; i < nInterventions; i++) {
			simul = new DiseaseProgressionSimulation(simul, interventions[i]);
			simul.addInfoReceiver(costListeners[i]);
			simul.addInfoReceiver(paramListeners[i]);
			simul.addInfoReceiver(lyListeners[i]);
			simul.addInfoReceiver(qalyListeners[i]);
			simul.addInfoReceiver(timeFreeListener);
			if (printOutputs.contains(Outputs.INDIVIDUAL_OUTCOMES)) {
				simul.addInfoReceiver(indTimeToEventListener);
			}
			if (interventions[i] instanceof ScreeningIntervention)
				simul.addInfoReceiver(screenListeners[i]);
			if (patientListener != null)
				simul.addInfoReceiver(patientListener);
			if (baseCase) {
				for (ExperimentListener listener : baseCaseExpListeners) {
					listener.addListener(simul);
				}
			} else {
				for (ExperimentListener listener : expListeners) {
					listener.addListener(simul);
				}
			}
			simul.run();
		}
		if (printOutputs.contains(Outputs.INDIVIDUAL_OUTCOMES)) {
			final Manifestation[] availableChronicManifestations = secParams.getRegisteredManifestations(Manifestation.Type.CHRONIC);
			final Manifestation[] availableAcuteManifestations = secParams.getRegisteredManifestations(Manifestation.Type.ACUTE);
			System.out.print("PAT");
			for (int i = 0; i < nInterventions; i++) {
				final String shortName = "_" + interventions[i].name();
				System.out.print("\tCOST" + shortName + "\tLE" + shortName + "\tQALE" + shortName);
				for (Manifestation comp : availableChronicManifestations) {
					System.out.print("\tT_" + comp.name() + shortName);
				}			
				for (Manifestation comp : availableAcuteManifestations) {
					System.out.print("\tN_" + comp.name() + shortName);
				}			
			}
			System.out.println();
			final double[][][] timesTo = indTimeToEventListener.getTimes(); 
			final int[][][] nEvents = indTimeToEventListener.getNEvents(); 
			for (int i = 0; i < nPatients; i++) {
				System.out.print(i);
				for (int j = 0; j < nInterventions; j++) {
					System.out.print("\t" + costListeners[j].getValues()[i] + "\t" + lyListeners[j].getValues()[i] + "\t" + qalyListeners[j].getValues()[i]);
					for (int k = 0; k < availableChronicManifestations.length; k++) {
						System.out.print("\t" + timesTo[i][j][k]);
					}
					for (int k = 0; k < availableAcuteManifestations.length; k++) {
						System.out.print("\t" + nEvents[i][j][k]);
					}
				}
				System.out.println();
			}
		}
		out.println(print(simul, costListeners, lyListeners, qalyListeners, paramListeners, timeFreeListener, screenListeners));
	}

	public enum Outputs {
		INDIVIDUAL_OUTCOMES, // printing the outcomes per patient
		BREAKDOWN_COST, // printing breakdown of costs
		BI // printing the budget impact
	};

	public static class EpidemiologicOutputFormat {
		/**
		 * Type of epidemiologic information
		 * @author Iván Castilla
		 */
		public enum Type {
			INCIDENCE,
			PREVALENCE,
			CUMUL_INCIDENCE
		}
		private final Type type;
		private final boolean absolute;
		private final boolean byAge;
		private final int interval;

		/**
		 * @param type
		 * @param absolute
		 * @param byAge
		 * @param interval
		 */
		private EpidemiologicOutputFormat(Type type, boolean absolute, boolean byAge, int interval) {
			this.type = type;
			this.absolute = absolute;
			this.byAge = byAge;
			this.interval = interval;
		}

		public static EpidemiologicOutputFormat build(String format) {
			Type type;
			switch (format.charAt(0)) {
			case 'i':
				type = Type.INCIDENCE;
				break;
			case 'p':
				type = Type.PREVALENCE;
				break;
			case 'c':
				type = Type.CUMUL_INCIDENCE;
				break;
			default:
				return null;
			}
			boolean absolute = false;
			if (format.length() > 1) {
				switch (format.charAt(1)) {
				case 'a':
					absolute = true;
					break;
				case 'r':
					absolute = false;
					break;
				default:
					return null;
				}
			}
			boolean byAge = false;
			if (format.length() > 2) {
				switch (format.charAt(2)) {
				case 'a':
					byAge = true;
					break;
				case 't':
					byAge = false;
					break;
				default:
					return null;
				}
			}
			int interval = 1;
			if (format.length() > 3) {
				try {
					interval = Integer.parseInt(format.substring(3));
				} catch (NumberFormatException e) {
					return null;
				}
			}
			return new EpidemiologicOutputFormat(type, absolute, byAge, interval);
		}

		/**
		 * @return the type
		 */
		public Type getType() {
			return type;
		}

		/**
		 * @return the absolute
		 */
		public boolean isAbsolute() {
			return absolute;
		}

		/**
		 * @return the byAge
		 */
		public boolean isByAge() {
			return byAge;
		}

		/**
		 * @return the interval
		 */
		public int getInterval() {
			return interval;
		}
	}



	/**
	 * The executor of simulations. Each problem executor launches a set of simulation experiments
	 * 
	 * @author Iván Castilla Rodríguez
	 */
	private class ProblemExecutor implements Runnable {
		final private PrintWriter out;
		final private int id;
		final private int maxThreads;

		public ProblemExecutor(PrintWriter out, int id, int maxThreads) {
			this.out = out;
			this.id = id;
			this.maxThreads = maxThreads;
		}

		@Override
		public void run() {
			for (int sim = id; sim <= nRuns; sim += maxThreads) {
				simulateInterventions(sim, false);
				progress.print();
			}
			out.flush();
		}
	}

	/**
	 * A class to print the progression of the simulations
	 * 
	 * @author Iván Castilla Rodríguez
	 *
	 */
	private class PrintProgress {
		final private int totalSim;
		final private int gap;
		private AtomicInteger counter;

		public PrintProgress(int gap, int totalSim) {
			this.totalSim = totalSim;
			this.gap = gap;
			this.counter = new AtomicInteger();
		}

		public void print() {
			if (!quiet) {
				if (counter.incrementAndGet() % gap == 0)
					System.out.println("" + (counter.get() * 100 / totalSim) + "% finished");
			}
		}
	}
	
	public static class MalformedSimulationModelException extends Exception {
		private static final long serialVersionUID = 7167363294337270171L;

		public MalformedSimulationModelException(String s) {
			super("The model was incomplete or malformed: " + s);
		}
				
	}
}
