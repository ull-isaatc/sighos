/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import es.ull.iis.simulation.hta.Intervention;
import es.ull.iis.simulation.hta.T1DM.canada.CanadaSecondOrderParams;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.AnnualCostView;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.CostListener;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.LYListener;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.PatientCounterHistogramView;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.QALYListener;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.T1DMPatientInfoView;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.T1DMPatientPrevalenceView;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.T1DMTimeFreeOfComplicationsView;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.CommonParams;
import es.ull.iis.simulation.hta.T1DM.params.DCCTSecondOrderParams;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.T1DM.params.UnconsciousSecondOrderParams;
import es.ull.iis.simulation.hta.T1DM.params.UncontrolledSecondOrderParams;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class T1DMMain {
	private static final int GAP = 50;
	private static final int DEF_N_RUNS = 100;
	private static final int DEF_N_PATIENTS = 5000;
	private final PrintWriter out;
	private final T1DMMonitoringIntervention[] interventions;
	private final int nRuns;
	private final int nPatients;
	private final SecondOrderParamsRepository secParams;
	private final T1DMPatientInfoView patientListener;
	private final PrintProgress progress;
	private final boolean parallel;
	private final boolean quiet;
	private final boolean printIncidence;
	private final boolean printPrevalence;
	private final boolean printBI;
	
	public T1DMMain(PrintWriter out, int nRuns, int nPatients, SecondOrderParamsRepository secParams, boolean parallel, boolean quiet, int singlePatientOutput, boolean printIncidence, boolean printPrevalence, boolean printBI) {
		super();
		this.out = out;
		this.interventions = secParams.getInterventions();
		this.nRuns = nRuns;
		this.nPatients = nPatients;
		this.secParams = secParams;
		this.parallel = parallel;
		this.quiet = quiet;
		if (singlePatientOutput != -1)
			patientListener = new T1DMPatientInfoView(singlePatientOutput);
		else
			patientListener = null;
		this.printIncidence = printIncidence;
		this.printPrevalence = printPrevalence;
		this.printBI = printBI;
		progress = new PrintProgress(GAP, nRuns + 1);
	}

	private void addListeners(T1DMSimulation simul) {
		if (printIncidence)
			simul.addInfoReceiver(new PatientCounterHistogramView(BasicConfigParams.MIN_AGE, BasicConfigParams.MAX_AGE, 1, secParams.getAvailableHealthStates()));
		if (printPrevalence)
			simul.addInfoReceiver(new T1DMPatientPrevalenceView(simul.getTimeUnit(), 
					T1DMPatientPrevalenceView.buildAgesInterval(BasicConfigParams.MIN_AGE, BasicConfigParams.MAX_AGE, 1, true),
					secParams.getAvailableHealthStates()));
	}
	
	private String getStrHeader() {
		final StringBuilder str = new StringBuilder();
		final Intervention[] interventions = secParams.getInterventions();
		str.append("SIM\tNPATIENTS\t");
		for (int i = 0; i < interventions.length; i++) {
			str.append(CostListener.getStrHeader(interventions[i].getShortName()));
			str.append(LYListener.getStrHeader(interventions[i].getShortName()));
			str.append(QALYListener.getStrHeader(interventions[i].getShortName()));
		}
		str.append(T1DMTimeFreeOfComplicationsView.getStrHeader(false, interventions, secParams.getAvailableHealthStates()));
		str.append(secParams.getStrHeader());
		return str.toString();
	}
	
	private String print(T1DMSimulation simul, CostListener[] costListeners, LYListener[] lyListeners, QALYListener[] qalyListeners, T1DMTimeFreeOfComplicationsView timeFreeListener) {
		final StringBuilder str = new StringBuilder();
		final Intervention[] interventions = secParams.getInterventions();
		str.append("" +  simul.getIdentifier() + "\t" + nPatients + "\t");
		for (int i = 0; i < interventions.length; i++) {
			str.append(costListeners[i]);
			str.append(lyListeners[i]);
			str.append(qalyListeners[i]);
		}
		str.append(timeFreeListener).append(secParams);
		return str.toString();
	}

	private void simulateInterventions(int id, boolean baseCase) {
		final T1DMTimeFreeOfComplicationsView timeFreeListener = new T1DMTimeFreeOfComplicationsView(nPatients, interventions.length, false, secParams.getAvailableHealthStates());
		final CostListener[] costListeners = new CostListener[interventions.length];
		final LYListener[] lyListeners = new LYListener[interventions.length];
		final QALYListener[] qalyListeners = new QALYListener[interventions.length];
		AnnualCostView[] budgetImpactListener = null;
		if (printBI)
			budgetImpactListener = new AnnualCostView[interventions.length];
		for (int i = 0; i < interventions.length; i++) {
			costListeners[i] = new CostListener(secParams.getCostCalculator(), secParams.getDiscountRate(), nPatients);
			lyListeners[i] = new LYListener(secParams.getDiscountRate(), nPatients);
			qalyListeners[i] = new QALYListener(secParams.getUtilityCalculator(), secParams.getDiscountRate(), nPatients);
			if (printBI)
				budgetImpactListener[i] = new AnnualCostView(secParams.getCostCalculator(), nPatients, BasicConfigParams.MIN_AGE, BasicConfigParams.MAX_AGE);
		}
		T1DMSimulation simul = new T1DMSimulation(id, baseCase, interventions[0], nPatients, new CommonParams(secParams));
		simul.addInfoReceiver(costListeners[0]);
		simul.addInfoReceiver(lyListeners[0]);
		simul.addInfoReceiver(qalyListeners[0]);
		simul.addInfoReceiver(timeFreeListener);
		if (patientListener != null)
			simul.addInfoReceiver(patientListener);
		if (printBI)
			simul.addInfoReceiver(budgetImpactListener[0]);
		addListeners(simul);
		simul.run();
		for (int i = 1; i < interventions.length; i++) {
			simul = new T1DMSimulation(simul, interventions[i]);
			simul.addInfoReceiver(costListeners[i]);
			simul.addInfoReceiver(lyListeners[i]);
			simul.addInfoReceiver(qalyListeners[i]);
			simul.addInfoReceiver(timeFreeListener);
			if (patientListener != null)
				simul.addInfoReceiver(patientListener);
			if (printBI)
				simul.addInfoReceiver(budgetImpactListener[i]);
			addListeners(simul);
			simul.run();				
		}
		if (printBI) {
			System.out.println("Annual costs (for budget impact)");
			final double[][] costs = new double[interventions.length][];
			for (int i = 0; i < interventions.length; i++) {
				System.out.print(interventions[i].getShortName() + "\t");
				costs[i] = budgetImpactListener[i].getAnnualCosts(); 
			}
			System.out.println();
			for (int i = 0; i < BasicConfigParams.MAX_AGE - BasicConfigParams.MIN_AGE; i++) {
				for (int j = 0; j < interventions.length; j++) {
					System.out.print(costs[j][i] + "\t");
				}
				System.out.println();
			}
		}
		out.println(print(simul, costListeners, lyListeners, qalyListeners, timeFreeListener));	
	}
	
	public void run() {
		long t = System.currentTimeMillis(); 
		out.println(getStrHeader());
		simulateInterventions(0, true);
		progress.print();
		secParams.setBaseCase(false);
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
		
		
        out.close();
        if (!quiet)
        	System.out.println("Execution time: " + ((System.currentTimeMillis() - t) / 1000) + " sec");       
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
	        final int nPatients = args1.nPatients;
	        SecondOrderParamsRepository secParams;
	        if (args1.validation == 1) {
	        	secParams = new CanadaSecondOrderParams(true, nPatients);
	        }
	        else if (args1.validation == 2) {
	        	secParams = new DCCTSecondOrderParams(true, nPatients, args1.basic);
	        }
	        else if (args1.population == 2) {
	        	secParams = new UncontrolledSecondOrderParams(true, nPatients, args1.basic);
	        }
	        else {
	        	secParams = new UnconsciousSecondOrderParams(true, nPatients, args1.basic);
	        }
	        final int singlePatientOutput = args1.singlePatientOutput;
	        final int nRuns = args1.nRuns;
	    	if (args1.noDiscount)
	    		secParams.setDiscountZero(true);
	        
	        final T1DMMain experiment = new T1DMMain(out, nRuns, nPatients, secParams, args1.parallel, args1.quiet, singlePatientOutput, args1.incidence, args1.prevalence, args1.bi);
	        experiment.run();
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			ex.usage();
			System.exit(-1);
		}

	}
	
	private static class Arguments {
		@Parameter(names ={"--output", "-o"}, description = "Output file name", order = 1)
		private String outputFileName = null;
		@Parameter(names ={"--patients", "-n"}, description = "Number of patients to test", order = 2)
		private int nPatients = DEF_N_PATIENTS;
		@Parameter(names ={"--runs", "-r"}, description = "Number of probabilistic runs", order = 3)
		private int nRuns = DEF_N_RUNS;
		@Parameter(names ={"--validation", "-v"}, description = "Enables validation scenarios (1 for Canada, 2 for DCCT)", order = 8)
		private int validation = 0;
		@Parameter(names ={"--population", "-pop"}, description = "Selects an alternative population (1 for unconscious, 2 for uncontrolled)", order = 8)
		private int population = 1;
		@Parameter(names ={"--single_patient_output", "-es"}, description = "Enables single patient output", order = 4)
		private int singlePatientOutput = -1;
		@Parameter(names ={"--nodiscount", "-nd"}, description = "Uses rate discount = 0%", order = 7)
		private boolean noDiscount = false;
		@Parameter(names ={"--prevalence", "-ep"}, description = "Enables printing prevalence of complications by age group ", order = 9)
		private boolean prevalence = false;
		@Parameter(names ={"--incidence", "-ei"}, description = "Enables printing incidence of complications by age group ", order = 9)
		private boolean incidence = false;
		@Parameter(names ={"--budget", "-ebi"}, description = "Enables printing budget impact", order = 9)
		private boolean bi = false;
		
		@Parameter(names ={"--parallel", "-p"}, description = "Enables parallel execution", order = 5)
		private boolean parallel = false;
		@Parameter(names ={"--quiet", "-q"}, description = "Quiet execution (does not print progress info)", order = 6)
		private boolean quiet = false;
		@Parameter(names ={"--basic", "-b"}, description = "Use basic progression models, instead of complex (only some complications has complex models)", order = 10)
		private boolean basic = false;
	}
	
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
