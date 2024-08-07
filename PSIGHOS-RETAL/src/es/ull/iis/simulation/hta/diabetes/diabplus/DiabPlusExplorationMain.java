/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.diabplus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.InvalidPathException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
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
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams.Sex;
import es.ull.iis.simulation.hta.diabetes.params.Discount;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository.RepositoryInstance;
import es.ull.iis.simulation.hta.diabetes.params.StdDiscount;

/**
 * Main class to launch exploratory simulation experiments
 * For example:
 * - to create raw data for ML postprocessing: -q -n 5000 -r 0  -ex -po
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class DiabPlusExplorationMain {
	private static final String STR_FILENAME_ROOT = "\\res";
	private static final String STR_FILENAME_EXT = ".txt";
	private static final int DEF_MIN_AGE = 20;
	private static final int DEF_MAX_AGE = 50;
	private static final int DEF_INT_AGE = 5;
	private static final int DEF_MIN_ONSET_AGE = 5;
	private static final int DEF_MAX_ONSET_AGE = 30;
	private static final int DEF_INT_ONSET_AGE = 5;
	private static final double DEF_MIN_HBA1C = 6.0;
	private static final double DEF_MAX_HBA1C = 14.0;
	private static final double DEF_INT_HBA1C = 1.0;
	private static final double DEF_MIN_SHE = 0.0;
	private static final double DEF_MAX_SHE = 4.0;
	private static final double DEF_INT_SHE = 2.0;
	private static final int DEF_COMB1_INIT_MANIF = 3;
	private static final int DEF_COMB2_INIT_MANIF = 3;
	private static final int DEF_COMB3_INIT_MANIF = 3;
	/** How many replications have to be run to show a new progression percentage message */
	private static final int N_PROGRESS = 20;
	private final PrintWriter out;
	private final ArrayList<SecondOrderDiabetesIntervention> interventions;
	/** Number of patients to be generated during each simulation */
	private static int nPatients;
	private final SecondOrderParamsRepository secParams;
	private static Discount discountCost;
	private static Discount discountEffect;
	/** Number of simulations to run */
	private static int nRuns;
	/** Enables parallel execution of simulations */
	private static boolean parallel;
	/** Disables most messages */
	private static boolean quiet;
	/** Time horizon for the simulation */
	private static int timeHorizon;
	private final double initAge;
	private final double duration;
	private static int intervalLength; 
	private final PrintProgress progress;
	
	public DiabPlusExplorationMain(PrintWriter out, SecondOrderParamsRepository secParams, double initAge, double duration) {
		super();
		this.initAge = initAge;
		this.duration = duration;
		this.out = out;
		this.interventions = secParams.getRegisteredInterventions();
		this.secParams = secParams;
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
	
	private static void launchExperiments(PrintWriter out, Sex sex, double age, double duration, double hypoRate, ArrayList<Double> hba1cLevels, int combLevel) {
    	final DiabPlusStdPopulation population = new DiabPlusStdPopulation(sex, hba1cLevels.get(0), age, duration, hypoRate);
    	
        final DiabPlusExplorationSecondOrderRepository secParams = new DiabPlusExplorationSecondOrderRepository(nPatients, population, hba1cLevels);
		final TreeSet<DiabetesComplicationStage> initManifestations = DiabPlusExplorationSecondOrderRepository.getRndCollectionOfStages(combLevel); 
		
		// Hard-coded here, since the proper way of doing it is via BasicConfigParams, but only if the stages could be defined before the repository, and that's not possible
        for (final DiabetesComplicationStage stage : initManifestations) {
			secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getInitProbString(stage), "Initial proportion of " + stage.name(), "", 1.0));
        }
		final String msg = "EXPERIMENT FOR SEX=" + sex + "\tAGE=" + age + "\tDURATION=" + duration + "\tHYPO_RATE=" + hypoRate + "\tINIT_MANIF=" + DiabPlusExplorationSecondOrderRepository.print(initManifestations);
        if (!quiet)
        	System.out.println(msg);
        out.println(msg);
        
    	final String validity = secParams.checkValidity();
    	if (validity == null) {
    		final DiabPlusExplorationMain experiment = new DiabPlusExplorationMain(out, secParams, age, duration);
	        experiment.run();
    	}
    	else {
    		System.err.println("Could not validate model");
    		System.err.println(validity);
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
	        BasicConfigParams.N_RUNS = args1.nRuns;
	        BasicConfigParams.N_PATIENTS = args1.nPatients;

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
    		parallel = args1.parallel;
    		quiet = args1.quiet;
    		nRuns = args1.nRuns;
    		nPatients = args1.nPatients;
    		// Be careful, since the min age used here is disconnected from that defined in the study population
        	timeHorizon = (args1.timeHorizon == -1) ? BasicConfigParams.DEF_MAX_AGE - BasicConfigParams.DEF_MIN_AGE + 1 : args1.timeHorizon;
    		intervalLength = args1.interval;


    		// Checks the output: it must be a folder; otherwise, an InvalidPathException arise
			File dir = new File(args1.outputFileName);
			if (!dir.isDirectory()) {
				throw new InvalidPathException(args1.outputFileName, "Output must be a folder");
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
	        ArrayList<Double> sheRates = null;
	        if (args1.expSHEs.size() == 0)
	        	sheRates = generateExperimentalNumbers(DEF_MIN_SHE, DEF_MAX_SHE, DEF_INT_SHE);
	        else
	        	sheRates = generateExperimentalNumbers(args1.expSHEs.get(0), args1.expSHEs.get(1), args1.expSHEs.get(2));
	        ArrayList<Integer> initManif = new ArrayList<>();
	        if (args1.expInitManif.size() == 0) {
	        	initManif.add(DEF_COMB1_INIT_MANIF);
	        	initManif.add(DEF_COMB2_INIT_MANIF);
	        	initManif.add(DEF_COMB3_INIT_MANIF);
	        }
	        else {
	        	initManif.add(args1.expInitManif.get(0));
	        	initManif.add(args1.expInitManif.get(1));
	        	initManif.add(args1.expInitManif.get(2));
	        }
	    	
	        final String expId = new SimpleDateFormat("yyyyMMdd").format(new Date());
			final PrintWriter outSummary = new PrintWriter(new BufferedWriter(new FileWriter(args1.outputFileName + STR_FILENAME_ROOT + "_" + expId + "_SUMMARY" + STR_FILENAME_EXT)));
			outSummary.println(BasicConfigParams.printOptions());
			// Print available manifestations
			for (DiabetesComplicationStage stage : DiabPlusExplorationSecondOrderRepository.getChronicComplicationStages()) {
				outSummary.print(stage + "\t");
			}
			outSummary.println();
			for (DiabetesAcuteComplications acute : DiabPlusExplorationSecondOrderRepository.getAcuteComplications()) {
				outSummary.print(acute + "\t");
			}
			outSummary.println();
			for (double hba1cLevel : hba1cLevels) {
				outSummary.print(hba1cLevel + "\t");				
			}
			outSummary.println();
			long t = System.currentTimeMillis();
			int nExp = 0;
			for (Sex sex : Sex.values()) {
		    	for (final double age : ages) {
		    		for (final double onset : onsets) {
		    			// Only test with valid onset (when the onset age is lower or equal than the current age)
		    			if (age >= onset) {
		    				final double duration = age - onset;
		    				for (double hypoRate : sheRates) {
			    				final String fileName = args1.outputFileName + STR_FILENAME_ROOT + "_" + expId + "_" + sex + "_A" + age + "_D" + duration + "_H" + hypoRate + STR_FILENAME_EXT;
			    				final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
		    					// First launch the experiments for no initial manifestations
	    						launchExperiments(out, sex, age, duration, hypoRate, hba1cLevels, 0);
	    						nExp++;
	    						// then launches the experiments for each level of combination of initial manifestations
		    					for (int manifCombLevel = 1; manifCombLevel <= 3; manifCombLevel++) {
		    						for (int combId = 0; combId < initManif.get(manifCombLevel - 1); combId++) {
		    							launchExperiments(out, sex, age, duration, hypoRate, hba1cLevels, manifCombLevel);
			    						nExp++;
		    						}
					    		}
			    				out.close();
		    				}
		    			}
		    		}
		    	}
			}
        	final String msg = "NEXP=" +  nExp + "\tEXEC_TIME_SECONDS=" + ((System.currentTimeMillis() - t) / 1000);
        	if (!args1.quiet)
        		System.out.println(msg);
        	outSummary.println(msg);
	        outSummary.close();
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			ex.usage();
			System.exit(-1);
		} catch (NumberFormatException ex) {
			System.out.println(ex.getMessage());
			System.exit(-1);
		} catch(InvalidPathException ex) {
			System.out.println(ex.getMessage());
			System.exit(-1);
		} catch (IOException ex) {
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
		@Parameter(names ={"--output", "-o"}, description = "Name of the output folder", order = 1)
		private String outputFileName = System.getProperty("user.dir");
		@Parameter(names ={"--patients", "-n"}, description = "Number of patients to test", order = 2)
		private int nPatients = BasicConfigParams.N_PATIENTS;
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
		@Parameter(names = {"--exp_she", "-Es"}, arity = 3, 
				description = "The experimental severe hypoglycemia rates to test, expressed as MIN MAX GAP. Default values are: " + DEF_MIN_SHE + " " + DEF_MAX_SHE + " " + DEF_INT_SHE, order = 7)
		public List<Double> expSHEs = new ArrayList<>();
		@Parameter(names = {"--exp_initManif", "-Em"}, arity = 3, 
				description = "The experimental initial manifestations to test, expressed as COMB1 COMB2 COMB3, i.e. how many combinations of 1, 2 or 3 initial manifestations to test. Default values are: " + DEF_COMB1_INIT_MANIF + " " + DEF_COMB2_INIT_MANIF + " " + DEF_COMB3_INIT_MANIF, order = 7)
		public List<Integer> expInitManif = new ArrayList<>();
		
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
	}

	/**
	 * The executor of simulations. Each problem executor launches a set of simulation experiments
	 * @author Iv�n Castilla Rodr�guez
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
	 * @author Iv�n Castilla Rodr�guez
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
