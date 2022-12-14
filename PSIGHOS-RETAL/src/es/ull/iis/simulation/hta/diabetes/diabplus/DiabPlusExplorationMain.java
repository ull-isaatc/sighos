/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.diabplus;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesSimulation;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.AcuteComplicationCounterListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.CostListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.HbA1cListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.LYListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.QALYListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.StructuredIncidenceByGroupAgeView;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.TimeFreeOfComplicationsView;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention.DiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.Discount;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository.RepositoryInstance;
import es.ull.iis.simulation.hta.diabetes.params.StdDiscount;
import es.ull.iis.simulation.hta.diabetes.submodels.SecondOrderAcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SecondOrderChronicComplicationSubmodel;

/**
 * Main class to launch exploratory simulation experiments
 * For example:
 * - to create raw data for ML postprocessing: -q -n 5000 -r 0  -ex -po
 * @author Iván Castilla Rodríguez
 *
 */
public class DiabPlusExplorationMain {
	private static final int DEF_MIN_AGE = 20;
	private static final int DEF_MAX_AGE = 50;
	private static final int DEF_INT_AGE = 5;
	private static final int DEF_MIN_ONSET_AGE = 5;
	private static final int DEF_MAX_ONSET_AGE = 30;
	private static final int DEF_INT_ONSET_AGE = 5;
	private static final double DEF_MIN_HBA1C = 6.0;
	private static final double DEF_MAX_HBA1C = 14.0;
	private static final double DEF_INT_HBA1C = 1.0;
	/** How many replications have to be run to show a new progression percentage message */
	private static final int N_PROGRESS = 20;
	private final PrintWriter out;
	private final ArrayList<SecondOrderDiabetesIntervention> interventions;
	/** Number of simulations to run */
	private final int nRuns;
	/** Number of patients to be generated during each simulation */
	private final int nPatients;
	private final SecondOrderParamsRepository secParams;
	private final Discount discountCost;
	private final Discount discountEffect;
	private final PrintProgress progress;
	/** Enables parallel execution of simulations */
	private final boolean parallel;
	/** Disables most messages */
	private final boolean quiet;
	/** Time horizon for the simulation */
	private final int timeHorizon;
	private final double initAge;
	private final double duration;
	private final int intervalLength; 
	
	public DiabPlusExplorationMain(PrintWriter out, SecondOrderParamsRepository secParams, int nRuns, int timeHorizon, int intervalLength, Discount discountCost, Discount discountEffect, boolean parallel, boolean quiet, double initAge, double duration) {
		super();
		this.initAge = initAge;
		this.duration = duration;
		this.timeHorizon = timeHorizon;
		this.intervalLength = intervalLength;
		this.out = out;
		this.discountCost = discountCost;
		this.discountEffect = discountEffect;
		this.interventions = secParams.getRegisteredInterventions();
		this.nRuns = nRuns;
		this.nPatients = secParams.getnPatients();
		this.secParams = secParams;
		this.parallel = parallel;
		this.quiet = quiet;
		progress = new PrintProgress((nRuns > N_PROGRESS) ? nRuns/N_PROGRESS : 1, nRuns + 1);
	}
	
	/**
	 * Creates a string with the header required to interpret the output of the {@link #print(DiabetesSimulation, HbA1cListener[], CostListener[], LYListener[], QALYListener[], CostListener[], LYListener[], QALYListener[], AcuteComplicationCounterListener[], TimeFreeOfComplicationsView) print} method.
	 * @return A string with tab-separated headers for the outputs of the simulations
	 */
	private String getStrHeader() {
		final StringBuilder str = new StringBuilder();
		str.append("SIM\tAGE\tDURATION\t");
		for (int i = 0; i < interventions.size(); i++) {
			final String shortName = interventions.get(i).getShortName();
			str.append(CostListener.getStrHeader(shortName));
			str.append(LYListener.getStrHeader(shortName));
			str.append(QALYListener.getStrHeader(shortName));
			str.append(AcuteComplicationCounterListener.getStrHeader(shortName));
			str.append(StructuredIncidenceByGroupAgeView.getStrHeader(shortName, secParams, intervalLength));
		}
		str.append(TimeFreeOfComplicationsView.getStrHeader(false, interventions, secParams.getRegisteredComplicationStages()));
		str.append(secParams.getStrHeader());
		return str.toString();
	}
	
	/**
	 * Prints a detailed output for each simulation, aggregating the results for all the individuals
	 * @param simul Current simulation
	 * @param costListeners Results in terms of costs
	 * @param lyListeners Results in terms of life expectancy
	 * @param qalyListeners Results in terms of quality-adjusted life expectancy
	 * @param acuteListeners Results in terms of acute complications
	 * @param timeFreeListener Results in terms of time until a chronic manifestation appears
	 * @return
	 */
	private String print(DiabetesSimulation simul, CostListener[] costListeners, LYListener[] lyListeners, QALYListener[] qalyListeners, AcuteComplicationCounterListener[] acuteListeners,  StructuredIncidenceByGroupAgeView[] aggrIncidenceListeners, TimeFreeOfComplicationsView timeFreeListener) {
		final StringBuilder str = new StringBuilder();
		str.append("" +  simul.getIdentifier() + "\t");
		str.append("" + initAge + "\t" + duration + "\t");
		for (int i = 0; i < interventions.size(); i++) {
			str.append(costListeners[i]);
			str.append(lyListeners[i]);
			str.append(qalyListeners[i]);
			str.append(acuteListeners[i]);
			str.append(aggrIncidenceListeners[i]);
		}
		str.append(timeFreeListener).append(secParams);
		return str.toString();
	}
	
	/**
	 * Runs the simulations for each intervention
	 * @param id Simulation identifier
	 * @param baseCase True if we are running the base case
	 */
	private void simulateInterventions(int id, boolean baseCase) {
		final RepositoryInstance common = secParams.getInstance();
		final int nInterventions = interventions.size();
		final TimeFreeOfComplicationsView timeFreeListener = new TimeFreeOfComplicationsView(nPatients, nInterventions, false, secParams.getRegisteredComplicationStages());
		final CostListener[] costListeners = new CostListener[nInterventions];
		final LYListener[] lyListeners = new LYListener[nInterventions];
		final QALYListener[] qalyListeners = new QALYListener[nInterventions];
		final AcuteComplicationCounterListener[] acuteListeners = new AcuteComplicationCounterListener[nInterventions];
		final StructuredIncidenceByGroupAgeView[] aggrIncidenceListeners = new StructuredIncidenceByGroupAgeView[nInterventions];

		for (int i = 0; i < nInterventions; i++) {
			costListeners[i] = new CostListener(secParams.getCostCalculator(common.getAnnualNoComplicationCost(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), discountCost, nPatients);
			lyListeners[i] = new LYListener(discountEffect, nPatients);
			qalyListeners[i] = new QALYListener(secParams.getUtilityCalculator(common.getNoComplicationDisutility(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), discountEffect, nPatients);
			acuteListeners[i] = new AcuteComplicationCounterListener(nPatients);
			aggrIncidenceListeners[i] = new StructuredIncidenceByGroupAgeView(secParams, intervalLength);
		}
		final DiabetesIntervention[] intInstances = common.getInterventions();
		DiabetesSimulation simul = new DiabetesSimulation(id, intInstances[0], nPatients, common, secParams.getPopulation(), timeHorizon);
		simul.addInfoReceiver(costListeners[0]);
		simul.addInfoReceiver(lyListeners[0]);
		simul.addInfoReceiver(qalyListeners[0]);
		simul.addInfoReceiver(acuteListeners[0]);
		simul.addInfoReceiver(aggrIncidenceListeners[0]);
		simul.addInfoReceiver(timeFreeListener);
		simul.run();
		for (int i = 1; i < nInterventions; i++) {
			simul = new DiabetesSimulation(simul, intInstances[i]);
			simul.addInfoReceiver(costListeners[i]);
			simul.addInfoReceiver(lyListeners[i]);
			simul.addInfoReceiver(qalyListeners[i]);
			simul.addInfoReceiver(acuteListeners[i]);
			simul.addInfoReceiver(aggrIncidenceListeners[i]);
			simul.addInfoReceiver(timeFreeListener);
			simul.run();
		}
		out.println(print(simul, costListeners, lyListeners, qalyListeners, acuteListeners, aggrIncidenceListeners, timeFreeListener));
	}
	
	/**
	 * Launches the simulations
	 */
	public void run() {
		out.println(getStrHeader());
		
		simulateInterventions(0, true);
		progress.print();
		secParams.setBaseCase(false);
		if (nRuns > 0) {
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
			}
			else {
				new ProblemExecutor(out, 1, 1).run();
			}
		}
	}
	
	public static void main(String[] args) {
		final Arguments args1 = new Arguments();
		try {
			JCommander jc = JCommander.newBuilder()
			  .addObject(args1)
			  .build();
			jc.parse(args);
	        BasicConfigParams.USE_SIMPLE_MODELS = args1.basic;
	        BasicConfigParams.USE_REVIEW_UTILITIES = args1.altUtils;
	        BasicConfigParams.STUDY_YEAR = args1.year;

	        for (final Map.Entry<String, String> pInit : args1.initProportions.entrySet()) {
	        	BasicConfigParams.INIT_PROP.put(pInit.getKey(), Double.parseDouble(pInit.getValue()));
	        }

    		final Discount discountCost;
    		final Discount discountEffect;
    		// Zero discount by default
    		if (args1.discount.size() == 0) {
	    		discountCost = Discount.zeroDiscount;
	    		discountEffect = Discount.zeroDiscount;
    		}
    		else if (args1.discount.size() == 1) {
    			final double value = args1.discount.get(0);
    			discountCost = (value == 0.0) ? Discount.zeroDiscount : new StdDiscount(value);
    			discountEffect = (value == 0.0) ? Discount.zeroDiscount : new StdDiscount(value);
    		}
    		else {
    			final double valueCost = args1.discount.get(0);
    			final double valueEffect = args1.discount.get(1);
    			discountCost = (valueCost == 0.0) ? Discount.zeroDiscount : new StdDiscount(valueCost);
    			discountEffect = (valueEffect == 0.0) ? Discount.zeroDiscount : new StdDiscount(valueEffect);
    		}
    		
    		// Set outputs: different files for simulation outputs and for other outputs. If no file name is specified or an error arises, standard output is used
			PrintWriter out;
	        if (args1.outputFileName == null) {
	        	out = new PrintWriter(System.out);
	        }
	        else  {
	        	try {
	        		out = new PrintWriter(new BufferedWriter(new FileWriter(args1.outputFileName)));
				} catch (IOException e) {
					e.printStackTrace();
					out = new PrintWriter(System.out);
				}

	        }
	    	
	        ArrayList<Double> onsets = null;
	        if (args1.expOnsetAges.size() == 0)
	        	onsets = generateExperimentalNumbers(DEF_MIN_ONSET_AGE, DEF_MAX_ONSET_AGE, DEF_INT_ONSET_AGE);
	        else
	        	onsets = generateExperimentalNumbers(args1.expOnsetAges.get(0), args1.expOnsetAges.get(1), args1.expOnsetAges.get(2));
	        ArrayList<Double> ages = null;
	        if (args1.expAges.size() == 0)
	        	ages = generateExperimentalNumbers(DEF_MIN_AGE, DEF_MAX_AGE, DEF_INT_AGE);
	        else
	        	ages = generateExperimentalNumbers(args1.expAges.get(0), args1.expAges.get(1), args1.expAges.get(2));
	        ArrayList<Double> hba1cLevels = null;
	        if (args1.expHbA1c.size() == 0)
	        	hba1cLevels = generateExperimentalNumbers(DEF_MIN_HBA1C, DEF_MAX_HBA1C, DEF_INT_HBA1C);
	        else
	        	hba1cLevels = generateExperimentalNumbers(args1.expHbA1c.get(0), args1.expHbA1c.get(1), args1.expHbA1c.get(2));
	    	final boolean man = true;
	    	final double hypoRate = 0.1;
	    	
			if (!args1.quiet)
				out.println(BasicConfigParams.printOptions());
	        
			long t = System.currentTimeMillis();	    	
	    	for (final double age : ages) {
	    		for (final double onset : onsets) {
	    			// Only test with valid onset (when the onset age is lower or equal than the current age)
	    			if (age >= onset) {
	    				final double duration = age - onset;
		    	        if (!args1.quiet)
		    	        	System.out.println("Experiment for age=" + age + "-duration=" + duration);
		    	    	final DiabPlusStdPopulation population = new DiabPlusStdPopulation(man, hba1cLevels.get(0), age, duration, hypoRate);
		    	        final SecondOrderParamsRepository secParams = new DiabPlusExplorationSecondOrderRepository(args1.nPatients, population, hba1cLevels);
		    	    	final int timeHorizon = (args1.timeHorizon == -1) ? BasicConfigParams.DEF_MAX_AGE - secParams.getMinAge() + 1 : args1.timeHorizon;
		    	    	final String validity = secParams.checkValidity();
		    	    	final SecondOrderChronicComplicationSubmodel[] chronicSubmodels = secParams.getRegisteredChronicComplications();
		    	    	final SecondOrderAcuteComplicationSubmodel[] acuteSubmodels = secParams.getRegisteredAcuteComplications();
		    	    	for (final String compName : args1.disable) {
		    	    		boolean found = false;
		    	    		for (final DiabetesChronicComplications comp : DiabetesChronicComplications.values()) {
		    	    			if (comp.name().equals(compName)) {
		    	    				found = true;
		    	    				chronicSubmodels[comp.ordinal()].disable();
		    	    			}
		    	    		}
		    	    		if (!found) {
		    		    		for (final DiabetesAcuteComplications comp : DiabetesAcuteComplications.values()) {
		    		    			if (comp.name().equals(compName)) {
		    		    				found = true;
		    		    				acuteSubmodels[comp.ordinal()].disable();
		    		    			}
		    		    		}
		    	    		}
		    	    		if (!found) {
		    	    			throw new ParameterException("Error using the disable submodel option: could not find complication \"" + compName + "\".");
		    	    		}
		    	    	}
		    	    	if (validity == null) {
		    	    		final DiabPlusExplorationMain experiment = new DiabPlusExplorationMain(out, secParams, args1.nRuns, timeHorizon, args1.interval, discountCost, discountEffect, args1.parallel, args1.quiet, age, duration);
		    		        experiment.run();
		    	    	}
		    	    	else {
		    	    		System.err.println("Could not validate model");
		    	    		System.err.println(validity);
		    	    	}	    			
		    		}
	    		}
	    	}
			
	        if (!args1.quiet)
	        	System.out.println("Execution time: " + ((System.currentTimeMillis() - t) / 1000) + " sec");
	        out.close();
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			ex.usage();
			System.exit(-1);
		} catch (NumberFormatException ex) {
			System.out.println(ex.getMessage());
			System.exit(-1);
		}
	}
	
	private static ArrayList<Double> generateExperimentalNumbers(double min, double max, double gap) {
		final ArrayList<Double> list = new ArrayList<>();
		double number = min;
		while (number <= max) {
			list.add(number);
			number += gap;
		}
		return list;
	}
	
	private static class Arguments {
		@Parameter(names ={"--output", "-o"}, description = "Name of the output file name", order = 1)
		private String outputFileName = null;
		@Parameter(names ={"--patients", "-n"}, description = "Number of patients to test", order = 2)
		private int nPatients = BasicConfigParams.DEF_N_PATIENTS;
		@Parameter(names ={"--runs", "-r"}, description = "Number of probabilistic runs", order = 3)
		private int nRuns = BasicConfigParams.N_RUNS;
		@Parameter(names ={"--horizon", "-h"}, description = "Time horizon for the simulation (years)", order = 3)
		private int timeHorizon = -1;
		@Parameter(names = {"--discount", "-dr"}, variableArity = true, 
				description = "The discount rate to be applied. If more than one value is provided, the first one is used for costs, and the second for effects. Default value is " + BasicConfigParams.DEF_DISCOUNT_RATE, order = 7)
		public List<Double> discount = new ArrayList<>();
		@Parameter(names ={"--interval", "-i"}, description = "Interval for collecting incidence results (in years)", order = 3)
		private int interval = 10;
		@Parameter(names = {"--exp_ages", "-Ea"}, arity = 3, 
				description = "The experimental ages to test, expressed as MIN MAX GAP. Default values are: " + DEF_MIN_AGE + " " + DEF_MAX_AGE + " " + DEF_INT_AGE, order = 7)
		public List<Double> expAges = new ArrayList<>();
		@Parameter(names = {"--exp_onsets", "-Eo"}, arity = 3, 
				description = "The experimental onset age of diabetes to test, expressed as MIN MAX GAP. Default values are: " + DEF_MIN_ONSET_AGE + " " + DEF_MAX_ONSET_AGE + " " + DEF_INT_ONSET_AGE, order = 7)
		public List<Double> expOnsetAges = new ArrayList<>();
		@Parameter(names = {"--exp_hba1c", "-Eh"}, arity = 3, 
				description = "The experimental starting HbA1c level to test, expressed as MIN MAX GAP. Default values are: " + DEF_MIN_HBA1C + " " + DEF_MAX_HBA1C + " " + DEF_INT_HBA1C, order = 7)
		public List<Double> expHbA1c = new ArrayList<>();
		
		@Parameter(names ={"--parallel", "-p"}, description = "Enables parallel execution", order = 5)
		private boolean parallel = false;
		@Parameter(names ={"--quiet", "-q"}, description = "Quiet execution (does not print progress info)", order = 6)
		private boolean quiet = false;
		@Parameter(names ={"--basic", "-b"}, description = "Use basic progression models, instead of complex (only some complications has complex models)", order = 10)
		private boolean basic = BasicConfigParams.USE_SIMPLE_MODELS;
		@Parameter(names ={"--alt_utilities", "-au"}, description = "Enables using alternative utilities from the revision of Beaudet et al. 2014", order = 10)
		private boolean altUtils = BasicConfigParams.USE_REVIEW_UTILITIES;
		@Parameter(names ={"--year", "-y"}, description = "Modifies the year of the study (for cost updating))", order = 8)
		private int year = Calendar.getInstance().get(Calendar.YEAR);
		@DynamicParameter(names = {"--iniprop", "-I"}, description = "Initial proportion for complication stages")
		private Map<String, String> initProportions = new TreeMap<String, String>();
		@Parameter(names = {"--disable", "-D"}, description = "Disable a complication: any of CHD, NEU, NPH, RET, SHE", variableArity = true)
		private List<String> disable = new ArrayList<String>();
	}

	/**
	 * The executor of simulations. Each problem executor launches a set of simulation experiments
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
}
