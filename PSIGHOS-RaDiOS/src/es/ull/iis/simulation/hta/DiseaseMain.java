/**
 * 
 */
package es.ull.iis.simulation.hta;

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

import es.ull.iis.simulation.hta.inforeceiver.AnnualCostView;
import es.ull.iis.simulation.hta.inforeceiver.BudgetImpactView;
import es.ull.iis.simulation.hta.inforeceiver.CostListener;
import es.ull.iis.simulation.hta.inforeceiver.EpidemiologicView;
import es.ull.iis.simulation.hta.inforeceiver.ExperimentListener;
import es.ull.iis.simulation.hta.inforeceiver.LYListener;
import es.ull.iis.simulation.hta.inforeceiver.PatientInfoView;
import es.ull.iis.simulation.hta.inforeceiver.QALYListener;
import es.ull.iis.simulation.hta.inforeceiver.ScreeningTestPerformanceView;
import es.ull.iis.simulation.hta.inforeceiver.TimeFreeOfComplicationsView;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.interventions.ScreeningStrategy;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.StdDiscount;
import es.ull.iis.simulation.hta.params.ZeroDiscount;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.radios.RadiosRepository;
import es.ull.iis.simulation.hta.simpletest.TestSimpleRareDiseaseRepository;

/**
 * Main class to launch simulation experiments
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class DiseaseMain {
	private enum Outputs {
		INDIVIDUAL_OUTCOMES, 	// printing the outcomes per patient
		BREAKDOWN_COST,			// printing breakdown of costs
		BI 						// printing the budget impact
	};
	private static class EpidemiologicOutputFormat {
		private final EpidemiologicView.Type type;
		private final boolean absolute;
		private final boolean byAge;
		private final int interval;
		/**
		 * @param type
		 * @param absolute
		 * @param byAge
		 * @param interval
		 */
		private EpidemiologicOutputFormat(EpidemiologicView.Type type, boolean absolute, boolean byAge, int interval) {
			this.type = type;
			this.absolute = absolute;
			this.byAge = byAge;
			this.interval = interval;
		}
		
		public static EpidemiologicOutputFormat build(String format) {
			EpidemiologicView.Type type;
			switch(format.charAt(0)) {
			case 'i': type = EpidemiologicView.Type.INCIDENCE; break;
			case 'p': type = EpidemiologicView.Type.PREVALENCE; break; 
			case 'c': type = EpidemiologicView.Type.CUMUL_INCIDENCE; break;
			default: return null;
			}
			boolean absolute = false;
			if (format.length() > 1) {
				switch(format.charAt(1)) {
				case 'a': absolute = true; break; 
				case 'r': absolute = false; break;
				default: return null;
				}
			}
			boolean byAge = false;
			if (format.length() > 2) {
				switch(format.charAt(2)) {
				case 'a': byAge = true; break;
				case 't': byAge = false; break;
				default: return null;
				}
			}
			int interval = 1;
			if (format.length() > 3) {
				try {
					interval = Integer.parseInt(format.substring(3));
				} catch(NumberFormatException e) {
					return null;
				}
			}
			return new EpidemiologicOutputFormat(type, absolute, byAge, interval);
		}
		/**
		 * @return the type
		 */
		public EpidemiologicView.Type getType() {
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
	
	private final EnumSet<Outputs> printOutputs;
	/** How many replications have to be run to show a new progression percentage message */
	private static final int N_PROGRESS = 20;
	private static final String OUTPUTS_SUFIX = "_outputs"; 
	private final PrintWriter out;
	private final PrintWriter outListeners;
	private final Intervention[] interventions;
	/** Number of simulations to run */
	private final int nRuns;
	/** Number of patients to be generated during each simulation */
	private final int nPatients;
	private final SecondOrderParamsRepository secParams;
	private final Discount discountCost;
	private final Discount discountEffect;
	private final PatientInfoView patientListener;
	private final PrintProgress progress;
	/** Enables parallel execution of simulations */
	private final boolean parallel;
	/** Disables most messages */
	private final boolean quiet;
	/** Time horizon for the simulation */
	private final int timeHorizon;
	private final ArrayList<ExperimentListener> expListeners;
	private final ArrayList<ExperimentListener> baseCaseExpListeners;
	
	
	public DiseaseMain(PrintWriter out, PrintWriter outListeners, SecondOrderParamsRepository secParams, int timeHorizon, Discount discountCost, Discount discountEffect, boolean parallel, boolean quiet, int singlePatientOutput, final EnumSet<Outputs> printOutputs, final ArrayList<EpidemiologicOutputFormat> toPrint) {
		super();
		this.printOutputs = printOutputs;
		this.timeHorizon = timeHorizon;
		this.out = out;
		this.outListeners = outListeners;
		this.discountCost = discountCost;
		this.discountEffect = discountEffect;
		this.interventions = secParams.getRegisteredInterventions();
		this.nRuns = secParams.getnRuns();
		this.nPatients = secParams.getnPatients();
		this.secParams = secParams;
		this.parallel = parallel;
		this.quiet = quiet;
		if (singlePatientOutput != -1)
			patientListener = new PatientInfoView(singlePatientOutput);
		else
			patientListener = null;
		progress = new PrintProgress((nRuns > N_PROGRESS) ? nRuns/N_PROGRESS : 1, nRuns + 1);
		this.expListeners = new ArrayList<>();
		this.baseCaseExpListeners = new ArrayList<>();
		if (printOutputs.contains(Outputs.BREAKDOWN_COST)) {
			expListeners.add(new AnnualCostView(nRuns, secParams, discountCost));
			baseCaseExpListeners.add(new AnnualCostView(1, secParams, discountCost));
		}
		if (printOutputs.contains(Outputs.BI)) {
			baseCaseExpListeners.add(new BudgetImpactView(secParams, 10));
		}
		for (final EpidemiologicOutputFormat format : toPrint) {
			expListeners.add(new EpidemiologicView(nRuns, secParams, format.getInterval(), format.getType(), format.isAbsolute(), format.isByAge()));
			baseCaseExpListeners.add(new EpidemiologicView(1, secParams, format.getInterval(), format.getType(), format.isAbsolute(), format.isByAge()));
		}
	}
	
	private String getStrHeader() {
		final StringBuilder str = new StringBuilder();
		str.append("SIM\t");
		for (int i = 0; i < interventions.length; i++) {
			final String shortName = interventions[i].name();
			str.append(CostListener.getStrHeader(shortName));
			str.append(LYListener.getStrHeader(shortName));
			str.append(QALYListener.getStrHeader(shortName));
			if (interventions[i] instanceof ScreeningStrategy)
				str.append(ScreeningTestPerformanceView.getStrHeader(shortName));
		}
		str.append(TimeFreeOfComplicationsView.getStrHeader(false, interventions, secParams.getRegisteredManifestations(Manifestation.Type.CHRONIC)));
		str.append(secParams.getStrHeader());
		return str.toString();
	}
	
	private String print(DiseaseProgressionSimulation simul, CostListener[] costListeners, LYListener[] lyListeners, QALYListener[] qalyListeners, TimeFreeOfComplicationsView timeFreeListener, ScreeningTestPerformanceView[] screenListeners) {
		final StringBuilder str = new StringBuilder();
		str.append("" +  simul.getIdentifier() + "\t");
		for (int i = 0; i < interventions.length; i++) {
			str.append(costListeners[i]);
			str.append(lyListeners[i]);
			str.append(qalyListeners[i]);
			if (interventions[i] instanceof ScreeningStrategy)
				str.append(screenListeners[i]);
		}
		str.append(timeFreeListener).append(secParams.print(simul.getIdentifier()));
		return str.toString();
	}

	/**
	 * Runs the simulations for each intervention
	 * @param id Simulation identifier
	 * @param baseCase True if we are running the base case
	 */
	private void simulateInterventions(int id, boolean baseCase) {
		final int nInterventions = interventions.length;
		final TimeFreeOfComplicationsView timeFreeListener = new TimeFreeOfComplicationsView(secParams, false);
		final CostListener[] costListeners = new CostListener[nInterventions];
		final LYListener[] lyListeners = new LYListener[nInterventions];
		final QALYListener[] qalyListeners = new QALYListener[nInterventions];
		final ScreeningTestPerformanceView[] screenListeners = new ScreeningTestPerformanceView[nInterventions];

		for (int i = 0; i < nInterventions; i++) {
			costListeners[i] = new CostListener(secParams.getCostCalculator(), discountCost, nPatients);
			lyListeners[i] = new LYListener(discountEffect, nPatients);
			qalyListeners[i] = new QALYListener(secParams.getUtilityCalculator(), discountEffect, nPatients);
			screenListeners[i] = (interventions[i] instanceof ScreeningStrategy) ? new ScreeningTestPerformanceView(secParams) : null;
		}
		DiseaseProgressionSimulation simul = new DiseaseProgressionSimulation(id, interventions[0], secParams, timeHorizon);
		simul.addInfoReceiver(costListeners[0]);
		simul.addInfoReceiver(lyListeners[0]);
		simul.addInfoReceiver(qalyListeners[0]);
		simul.addInfoReceiver(timeFreeListener);
		if (interventions[0] instanceof ScreeningStrategy)
			simul.addInfoReceiver(screenListeners[0]);
		if (patientListener != null)
			simul.addInfoReceiver(patientListener);
		if (baseCase) {
			for (ExperimentListener listener : baseCaseExpListeners) {
				listener.addListener(simul);
			}			
		}
		else {
			for (ExperimentListener listener : expListeners) {
				listener.addListener(simul);
			}
		}
		simul.run();
		for (int i = 1; i < nInterventions; i++) {
			simul = new DiseaseProgressionSimulation(simul, interventions[i]);
			simul.addInfoReceiver(costListeners[i]);
			simul.addInfoReceiver(lyListeners[i]);
			simul.addInfoReceiver(qalyListeners[i]);
			simul.addInfoReceiver(timeFreeListener);
			if (interventions[i] instanceof ScreeningStrategy)
				simul.addInfoReceiver(screenListeners[i]);
			if (patientListener != null)
				simul.addInfoReceiver(patientListener);
			if (baseCase) {
				for (ExperimentListener listener : baseCaseExpListeners) {
					listener.addListener(simul);
				}			
			}
			else {
				for (ExperimentListener listener : expListeners) {
					listener.addListener(simul);
				}
			}
			simul.run();				
		}
		if (printOutputs.contains(Outputs.INDIVIDUAL_OUTCOMES)) {
			System.out.print("Patient");
			for (int i = 0; i < nInterventions; i++) {
				final String shortName = interventions[i].name();
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
		out.println(print(simul, costListeners, lyListeners, qalyListeners, timeFreeListener, screenListeners));	
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
			outListeners.println(BasicConfigParams.STR_SEP);
			outListeners.println("Base case");
			outListeners.println(BasicConfigParams.STR_SEP);
			for (ExperimentListener listener : baseCaseExpListeners) {
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
			}
			else {
				new ProblemExecutor(out, 1, 1).run();
			}
			if (expListeners.size() > 0) {
				outListeners.println(BasicConfigParams.STR_SEP);
				outListeners.println("PSA");
				outListeners.println(BasicConfigParams.STR_SEP);
				for (ExperimentListener listener : expListeners) {
					outListeners.println(listener);
				}		
			}
		}
		
		
        if (!quiet)
        	System.out.println("Execution time: " + ((System.currentTimeMillis() - t) / 1000) + " sec");       
        out.close();
        outListeners.close();
	}

	public static void main(String[] args) {
		final Arguments args1 = new Arguments();
		try {
			JCommander jc = JCommander.newBuilder()
			  .addObject(args1)
			  .build();
			jc.parse(args);
	        BasicConfigParams.STUDY_YEAR = args1.year;

	        for (final Map.Entry<String, String> pInit : args1.initProportions.entrySet()) {
	        	BasicConfigParams.INIT_PROP.put(pInit.getKey(), Double.parseDouble(pInit.getValue()));
	        }
	        
	        SecondOrderParamsRepository secParams = null;
	        switch (args1.population) {
	        case 1: 
	        	secParams = new RadiosRepository(args1.nRuns, args1.nPatients, "/home/davidpg/workspace/java/sighos-radios/PSIGHOS-RaDiOS/resources/radios-test_disease1.json", args1.timeHorizon); 
	        	break;
	        case 0: 
	        default:
	        	secParams = new TestSimpleRareDiseaseRepository(args1.nRuns, args1.nPatients); 
	        	break;
	        }	        	
	        
	    	final String validity = secParams.checkValidity();
	    	if (validity == null) {
		    	final int timeHorizon = (args1.timeHorizon == -1) ? BasicConfigParams.DEF_MAX_AGE - secParams.getMinAge() + 1 : args1.timeHorizon;
	    		final EnumSet<Outputs> printOutputs = EnumSet.noneOf(Outputs.class);
	    		if (args1.bi)
	    			printOutputs.add(Outputs.BI);
	    		if (args1.individualOutcomes)
	    			printOutputs.add(Outputs.INDIVIDUAL_OUTCOMES);
	    		if (args1.breakdownCost)
	    			printOutputs.add(Outputs.BREAKDOWN_COST);
	    		final Discount discountCost;
	    		final Discount discountEffect;
	    		if (args1.discount.size() == 0) {
		    		// Use default discount
	    			discountCost = new StdDiscount(BasicConfigParams.DEF_DISCOUNT_RATE);
	    			discountEffect = new StdDiscount(BasicConfigParams.DEF_DISCOUNT_RATE);
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
	    		final ArrayList<EpidemiologicOutputFormat> formats = new ArrayList<>();
	    		for (final String format : args1.epidem) {
	    			final EpidemiologicOutputFormat f = EpidemiologicOutputFormat.build(format);
	    			if (f != null)
	    				formats.add(f);
	    		}
	    		
	    		// Set outputs: different files for simulation outputs and for other outputs. If no file name is specified or an error arises, standard output is used
				PrintWriter out;
				PrintWriter outListeners;
		        if (args1.outputFileName == null) {
		        	out = new PrintWriter(System.out);
		        	outListeners = new PrintWriter(System.out);
		        }
		        else  {
		        	try {
		        		out = new PrintWriter(new BufferedWriter(new FileWriter(args1.outputFileName)));
					} catch (IOException e) {
						e.printStackTrace();
						out = new PrintWriter(System.out);
					}
		        	if (formats.size() > 0 || printOutputs.size() > 0) { 
			        	try {
			        		outListeners = new PrintWriter(new BufferedWriter(new FileWriter(args1.outputFileName + OUTPUTS_SUFIX)));
						} catch (IOException e) {
							e.printStackTrace();
							outListeners = new PrintWriter(System.out);
						}
		        	}
		        	else {
						outListeners = new PrintWriter(System.out);		        		
		        	}

		        }

	    		final DiseaseMain experiment = new DiseaseMain(out, outListeners, secParams, timeHorizon, discountCost, discountEffect, args1.parallel, args1.quiet, args1.singlePatientOutput, printOutputs, formats);
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
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
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
		@Parameter(names ={"--population", "-pop"}, description = "Selects an alternative scenario (0: Test; 1: RaDiOS)", order = 8)
		private int population = 0;
		@Parameter(names = {"--discount", "-dr"}, variableArity = true, 
				description = "The discount rate to be applied. If more than one value is provided, the first one is used for costs, and the second for effects. Default value is " + BasicConfigParams.DEF_DISCOUNT_RATE, order = 7)
		public List<Double> discount = new ArrayList<>();
		@Parameter(names ={"--single_patient_output", "-ps"}, description = "Enables printing the specified patient's output", order = 4)
		private int singlePatientOutput = -1;
		@Parameter(names ={"--epidem", "-ep"}, variableArity = true, description = "Enables printing epidemiologic results. Can receive several \"orders\". Each order consists of\r\n" +
		 "\t- The type of info to print {i: incidence, p:prevalence, c:cumulative incidence}\r" + 
		 "\t- An optional argument of whether to print absolute ('a') or relative ('r') results (Default: relative)\r" +
		 "\t- An optional argument of whether to print information by age ('a') or by time from start ('t') results (Default: time from start)\r" +
		 "\t- An optional number that indicates interval size (in years) (Default: 1)", order = 9)
		private List<String> epidem = new ArrayList<>();
		
		@Parameter(names ={"--outcomes", "-po"}, description = "Enables printing individual outcomes", order = 9)
		private boolean individualOutcomes = false;
		@Parameter(names ={"--costs", "-pbc"}, description = "Enables printing breakdown of costs", order = 9)
		private boolean breakdownCost = false;
		@Parameter(names ={"--budget", "-pbi"}, description = "Enables printing budget impact", order = 9)
		private boolean bi = false;
		@Parameter(names ={"--parallel", "-p"}, description = "Enables parallel execution", order = 5)
		private boolean parallel = false;
		@Parameter(names ={"--quiet", "-q"}, description = "Quiet execution (does not print progress info)", order = 6)
		private boolean quiet = false;
		@Parameter(names ={"--year", "-y"}, description = "Modifies the year of the study (for cost updating))", order = 8)
		private int year = BasicConfigParams.STUDY_YEAR;
		@DynamicParameter(names = {"--iniprop", "-I"}, description = "Initial proportion for complication stages")
		private Map<String, String> initProportions = new TreeMap<String, String>();
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
