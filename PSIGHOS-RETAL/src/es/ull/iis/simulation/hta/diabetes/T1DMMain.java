/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import es.ull.iis.simulation.hta.diabetes.DCCT.DCCTSecondOrderParams;
import es.ull.iis.simulation.hta.diabetes.canada.CanadaSecondOrderParams;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.AcuteComplicationCounterListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.AnnualCostView;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.BudgetImpactView;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.CostListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.DiabetesPatientInfoView;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.ExperimentListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.HbA1cListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.IncidenceByGroupAgeView;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.IncidenceView;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.LYListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.PrevalenceView;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.QALYListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.TimeFreeOfComplicationsView;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention.DiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.Discount;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository.RepositoryInstance;
import es.ull.iis.simulation.hta.diabetes.params.StdDiscount;
import es.ull.iis.simulation.hta.diabetes.params.ZeroDiscount;
import es.ull.iis.simulation.hta.diabetes.submodels.SecondOrderAcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SecondOrderChronicComplicationSubmodel;

/**
 * Main class to launch simulation experiments
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMMain {
	private enum Outputs {
		INCIDENCE, 				// printing incidence of complications by age group
		PREVALENCE, 			// printing prevalence of complications by age group
		CUMM_INCIDENCE, 		// printing cummulated incidence of complications by time from start
		INDIVIDUAL_OUTCOMES, 	// printing the outcomes per patient
		BREAKDOWN_COST,			// printing breakdown of costs
		BI 						// printing the budget impact
	};
	private final EnumSet<Outputs> printOutputs;
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
	private final DiabetesPatientInfoView patientListener;
	private final PrintProgress progress;
	/** Enables parallel execution of simulations */
	private final boolean parallel;
	/** Disables most messages */
	private final boolean quiet;
	/** Time horizon for the simulation */
	private final int timeHorizon;
	private final ArrayList<ExperimentListener<?>> expListeners;
	private final ArrayList<ExperimentListener<?>> baseCaseExpListeners;
	
	
	public T1DMMain(PrintWriter out, SecondOrderParamsRepository secParams, int nRuns, int timeHorizon, Discount discountCost, Discount discountEffect, boolean parallel, boolean quiet, int singlePatientOutput, final EnumSet<Outputs> printOutputs) {
		super();
		this.printOutputs = printOutputs;
		this.timeHorizon = timeHorizon;
		this.out = out;
		this.discountCost = discountCost;
		this.discountEffect = discountEffect;
		this.interventions = secParams.getRegisteredInterventions();
		this.nRuns = nRuns;
		this.nPatients = secParams.getnPatients();
		this.secParams = secParams;
		this.parallel = parallel;
		this.quiet = quiet;
		if (singlePatientOutput != -1)
			patientListener = new DiabetesPatientInfoView(singlePatientOutput);
		else
			patientListener = null;
		progress = new PrintProgress((nRuns > N_PROGRESS) ? nRuns/N_PROGRESS : 1, nRuns + 1);
		this.expListeners = new ArrayList<>();
		this.baseCaseExpListeners = new ArrayList<>();
		if (printOutputs.contains(Outputs.BREAKDOWN_COST)) {
			expListeners.add(new AnnualCostView(nRuns, secParams, discountCost));
			baseCaseExpListeners.add(new AnnualCostView(1, secParams, discountCost));
		}
		if (printOutputs.contains(Outputs.CUMM_INCIDENCE)) {
			expListeners.add(new IncidenceView(nRuns, secParams, timeHorizon, true));
			baseCaseExpListeners.add(new IncidenceView(1, secParams, timeHorizon, true));
		}
		if (printOutputs.contains(Outputs.INCIDENCE)) {
			expListeners.add(new IncidenceByGroupAgeView(nRuns, secParams, 1, false));
			baseCaseExpListeners.add(new IncidenceByGroupAgeView(1, secParams, 1, false));
		}
		if (printOutputs.contains(Outputs.BI)) {
			baseCaseExpListeners.add(new BudgetImpactView(secParams, 10));
		}
	}

	private void addListeners(DiabetesSimulation simul) {
		if (printOutputs.contains(Outputs.PREVALENCE))
			simul.addInfoReceiver(new PrevalenceView(simul.getTimeUnit(), 
					PrevalenceView.buildAgesInterval(secParams.getMinAge(), BasicConfigParams.DEF_MAX_AGE, 1, true),
					secParams.getRegisteredComplicationStages()));
	}
	
	private String getStrHeader() {
		final StringBuilder str = new StringBuilder();
		str.append("SIM\t");
		for (int i = 0; i < interventions.size(); i++) {
			final String shortName = interventions.get(i).getShortName();
			str.append(HbA1cListener.getStrHeader(shortName));
			str.append(CostListener.getStrHeader(shortName));
			str.append(LYListener.getStrHeader(shortName));
			str.append(QALYListener.getStrHeader(shortName));
			str.append(AcuteComplicationCounterListener.getStrHeader(shortName));
		}
		str.append(TimeFreeOfComplicationsView.getStrHeader(false, interventions, secParams.getRegisteredComplicationStages()));
		str.append(secParams.getStrHeader());
		return str.toString();
	}
	
	private String print(DiabetesSimulation simul, HbA1cListener[] hba1cListeners, CostListener[] costListeners, LYListener[] lyListeners, QALYListener[] qalyListeners, AcuteComplicationCounterListener[] acuteListeners, TimeFreeOfComplicationsView timeFreeListener) {
		final StringBuilder str = new StringBuilder();
		str.append("" +  simul.getIdentifier() + "\t");
		for (int i = 0; i < interventions.size(); i++) {
			str.append(hba1cListeners[i]);
			str.append(costListeners[i]);
			str.append(lyListeners[i]);
			str.append(qalyListeners[i]);
			str.append(acuteListeners[i]);
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
		final HbA1cListener[] hba1cListeners = new HbA1cListener[nInterventions];
		final CostListener[] costListeners = new CostListener[nInterventions];
		final LYListener[] lyListeners = new LYListener[nInterventions];
		final QALYListener[] qalyListeners = new QALYListener[nInterventions];
		final AcuteComplicationCounterListener[] acuteListeners = new AcuteComplicationCounterListener[nInterventions];

		for (int i = 0; i < nInterventions; i++) {
			hba1cListeners[i] = new HbA1cListener(nPatients);
			costListeners[i] = new CostListener(secParams.getCostCalculator(common.getAnnualNoComplicationCost(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), discountCost, nPatients);
			lyListeners[i] = new LYListener(discountEffect, nPatients);
			qalyListeners[i] = new QALYListener(secParams.getUtilityCalculator(common.getNoComplicationDisutility(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), discountEffect, nPatients);
			acuteListeners[i] = new AcuteComplicationCounterListener(nPatients);
		}
		final DiabetesIntervention[] intInstances = secParams.getInterventions();
		DiabetesSimulation simul = new DiabetesSimulation(id, intInstances[0], nPatients, common, secParams.getPopulation(), timeHorizon);
		simul.addInfoReceiver(hba1cListeners[0]);
		simul.addInfoReceiver(costListeners[0]);
		simul.addInfoReceiver(lyListeners[0]);
		simul.addInfoReceiver(qalyListeners[0]);
		simul.addInfoReceiver(acuteListeners[0]);
		simul.addInfoReceiver(timeFreeListener);
		if (patientListener != null)
			simul.addInfoReceiver(patientListener);
		addListeners(simul);
		if (baseCase) {
			for (ExperimentListener<?> listener : baseCaseExpListeners) {
				listener.addListener(simul);
			}			
		}
		else {
			for (ExperimentListener<?> listener : expListeners) {
				listener.addListener(simul);
			}
		}
		simul.run();
		for (int i = 1; i < nInterventions; i++) {
			simul = new DiabetesSimulation(simul, intInstances[i]);
			simul.addInfoReceiver(hba1cListeners[i]);
			simul.addInfoReceiver(costListeners[i]);
			simul.addInfoReceiver(lyListeners[i]);
			simul.addInfoReceiver(qalyListeners[i]);
			simul.addInfoReceiver(acuteListeners[i]);
			simul.addInfoReceiver(timeFreeListener);
			if (patientListener != null)
				simul.addInfoReceiver(patientListener);
			addListeners(simul);
			if (baseCase) {
				for (ExperimentListener<?> listener : baseCaseExpListeners) {
					listener.addListener(simul);
				}			
			}
			else {
				for (ExperimentListener<?> listener : expListeners) {
					listener.addListener(simul);
				}
			}
			simul.run();				
		}
		if (printOutputs.contains(Outputs.INDIVIDUAL_OUTCOMES)) {
			System.out.print("Patient");
			for (int i = 0; i < nInterventions; i++) {
				final String shortName = interventions.get(i).getShortName();
				System.out.print("\tCost_" + shortName + "\tLE_" + shortName + "\tQALE_" + shortName);
			}
			System.out.println();
			for (int i = 0; i < nPatients; i++) {
				System.out.print(i);
				for (int j = 0; j < nInterventions; j++) {
					System.out.print("\t" + costListeners[j].getValues()[i] + "\t" + lyListeners[j].getValues()[i] + "\t" + qalyListeners[j].getValues()[i]);					
				}
				System.out.println();
			}
		}
		out.println(print(simul, hba1cListeners, costListeners, lyListeners, qalyListeners, acuteListeners, timeFreeListener));	
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
		for (ExperimentListener<?> listener : baseCaseExpListeners) {
			out.println(listener);
		}		
		progress.print();
		secParams.setBaseCase(false);
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
			}
			else {
				new ProblemExecutor(out, 1, 1).run();
			}
			for (ExperimentListener<?> listener : expListeners) {
				out.println(listener);
			}		
		}
		
		
        if (!quiet)
        	System.out.println("Execution time: " + ((System.currentTimeMillis() - t) / 1000) + " sec");       
        out.close();
	}

	public static void main(String[] args) {
		final Arguments args1 = new Arguments();
		try {
			JCommander jc = JCommander.newBuilder()
			  .addObject(args1)
			  .build();
			jc.parse(args);
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
	        BasicConfigParams.USE_SIMPLE_MODELS = args1.basic;
	        BasicConfigParams.USE_CHD_DEATH_MODEL = args1.dCHD;
	        BasicConfigParams.USE_REVIEW_UTILITIES = args1.altUtils;
	        BasicConfigParams.STUDY_YEAR = args1.year;

	        for (final Map.Entry<String, String> pInit : args1.initProportions.entrySet()) {
	        	BasicConfigParams.INIT_PROP.put(pInit.getKey(), Double.parseDouble(pInit.getValue()));
	        }
	        SecondOrderParamsRepository secParams;
	        switch(args1.population) {
	        	case 1: 
	        		secParams = new UnconsciousSecondOrderParams(args1.nPatients);
	        		break;
	        	case 2:
	        		secParams = new UncontrolledSecondOrderParams(args1.nPatients);
	        		break;
	        	case 3:
	        		secParams = new CanadaSecondOrderParams(args1.nPatients);
	        		break;
	        	case 4:
	        		secParams = new DCCTSecondOrderParams(args1.nPatients);
	        		break;
	        	case 5:
	        		secParams =new LySecondOrderParams(args1.nPatients);
	        		break;
	        	case 6:
	        		secParams = new SMILESecondOrderParams(args1.nPatients);
	        		break;
	        	case 7:
	        		secParams = new PROSITSecondOrderParams(args1.nPatients);
	        		break;
	        	default:
	        		secParams = new UnconsciousSecondOrderParams(args1.nPatients);
	        		break;
	        }
	        
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
		    	final int timeHorizon = (args1.timeHorizon == -1) ? BasicConfigParams.DEF_MAX_AGE - secParams.getMinAge() + 1 : args1.timeHorizon;
	    		final EnumSet<Outputs> printOutputs = EnumSet.noneOf(Outputs.class);
	    		if (args1.prevalence)
	    			printOutputs.add(Outputs.PREVALENCE);
	    		if (args1.incidence)
	    			printOutputs.add(Outputs.INCIDENCE);
	    		if (args1.cumm)
	    			printOutputs.add(Outputs.CUMM_INCIDENCE);
	    		if (args1.bi)
	    			printOutputs.add(Outputs.BI);
	    		if (args1.individualOutcomes)
	    			printOutputs.add(Outputs.INDIVIDUAL_OUTCOMES);
	    		if (args1.breakdownCost)
	    			printOutputs.add(Outputs.BREAKDOWN_COST);
	    		final Discount discountCost;
	    		final Discount discountEffect;
	    		if (args1.discount.size() == 0) {
	    			// The Canada experiment requires its own discount rate
	    			if (args1.population == 3) {
		    			discountCost = new StdDiscount(CanadaSecondOrderParams.DISCOUNT_RATE);
		    			discountEffect = new StdDiscount(CanadaSecondOrderParams.DISCOUNT_RATE);
	    			}
	    			// DCCT uses no discount
	    			else if (args1.population == 4) {
			    			discountCost = new ZeroDiscount();
			    			discountEffect = new ZeroDiscount();
		    			}
		    		// Use default discount
	    			else {
		    			discountCost = new StdDiscount(BasicConfigParams.DEF_DISCOUNT_RATE);
		    			discountEffect = new StdDiscount(BasicConfigParams.DEF_DISCOUNT_RATE);
	    			}
	    		}
	    		else if (args1.discount.size() == 1) {
	    			final double value = args1.discount.get(0);
	    			discountCost = (value == 0.0) ? new ZeroDiscount() : new StdDiscount(value);
	    			discountEffect = (value == 0.0) ? new ZeroDiscount() : new StdDiscount(value);
	    		}
	    		else {
	    			final double valueCost = args1.discount.get(0);
	    			final double valueEffect = args1.discount.get(1);
	    			discountCost = (valueCost == 0.0) ? new ZeroDiscount() : new StdDiscount(valueCost);
	    			discountEffect = (valueEffect == 0.0) ? new ZeroDiscount() : new StdDiscount(valueEffect);
	    		}
		        final T1DMMain experiment = new T1DMMain(out, secParams, args1.nRuns, timeHorizon, discountCost, discountEffect, args1.parallel, args1.quiet, args1.singlePatientOutput, printOutputs);
		        experiment.run();
	    	}
	    	else {
	    		System.err.println("Could not validate model");
	    		System.err.println(validity);
	    	}
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			ex.usage();
			System.exit(-1);
		} catch (NumberFormatException ex) {
			System.out.println(ex.getMessage());
			System.exit(-1);
		}
		

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
		@Parameter(names ={"--population", "-pop"}, description = "Selects an alternative scenario (1 for unconscious, 2 for uncontrolled, 3 for Canada, 4 for DCCT, 5 for Ly, 6 for SMILE, 7 for UKPDS)", order = 8)
		private int population = 1;
		@Parameter(names ={"--single_patient_output", "-es"}, description = "Enables printing the specified patient's output", order = 4)
		private int singlePatientOutput = -1;
		@Parameter(names = {"--discount", "-dr"}, variableArity = true, 
				description = "The discount rate to be applied. If more than one value is provided, the first one is used for costs, and the second for effects. Default value is " + BasicConfigParams.DEF_DISCOUNT_RATE, order = 7)
		public List<Double> discount = new ArrayList<>();
		@Parameter(names ={"--prevalence", "-ep"}, description = "Enables printing prevalence of complications by age group ", order = 9)
		private boolean prevalence = false;
		@Parameter(names ={"--incidence", "-ei"}, description = "Enables printing incidence of complications by age group ", order = 9)
		private boolean incidence = false;
		@Parameter(names ={"--cumincidence", "-ec"}, description = "Enables printing cummulated incidence of complications by time from start", order = 9)
		private boolean cumm = false;
		@Parameter(names ={"--outcomes", "-eo"}, description = "Enables printing individual outcomes", order = 9)
		private boolean individualOutcomes = false;
		@Parameter(names ={"--costs", "-ebc"}, description = "Enables printing breakdown of costs", order = 9)
		private boolean breakdownCost = false;
		@Parameter(names ={"--budget", "-ebi"}, description = "Enables printing budget impact", order = 9)
		private boolean bi = false;
		@Parameter(names ={"--parallel", "-p"}, description = "Enables parallel execution", order = 5)
		private boolean parallel = false;
		@Parameter(names ={"--quiet", "-q"}, description = "Quiet execution (does not print progress info)", order = 6)
		private boolean quiet = false;
		@Parameter(names ={"--basic", "-b"}, description = "Use basic progression models, instead of complex (only some complications has complex models)", order = 10)
		private boolean basic = BasicConfigParams.USE_SIMPLE_MODELS;
		@Parameter(names ={"--deathCHD", "-dchd"}, description = "Use basic progression models, instead of complex (only some complications has complex models)", order = 10)
		private boolean dCHD = BasicConfigParams.USE_CHD_DEATH_MODEL;
		@Parameter(names ={"--alt_utilities", "-au"}, description = "Enables using alternative utilities from the revision of Beaudet et al. 2014", order = 10)
		private boolean altUtils = BasicConfigParams.USE_REVIEW_UTILITIES;
		@Parameter(names ={"--year", "-y"}, description = "Modifies the year of the study (for cost updating))", order = 8)
		private int year = BasicConfigParams.STUDY_YEAR;
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
