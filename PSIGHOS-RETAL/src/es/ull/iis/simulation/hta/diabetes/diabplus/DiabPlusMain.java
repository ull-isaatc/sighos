/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.diabplus;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesSimulation;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.AcuteComplicationCounterListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.AnnualCostView;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.BudgetImpactView;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.CostListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.DiabetesPatientInfoView;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.EpidemiologicView;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.ExperimentListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.HbA1cListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.LYListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.QALYListener;
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
 * Main class to launch simulation experiments
 * @author Iván Castilla Rodríguez
 *
 */
public class DiabPlusMain {
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
	private final PrintWriter outJSON;	
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
	private final ArrayList<ExperimentListener> expListeners;
	private final ArrayList<ExperimentListener> baseCaseExpListeners;
	private final DiabPlusJSONWriter jsonWriter;
	
	public DiabPlusMain(PrintWriter out, PrintWriter outListeners, PrintWriter outJSON, SecondOrderParamsRepository secParams, int nRuns, int timeHorizon, Discount discountCost, Discount discountEffect, boolean parallel, boolean quiet, int singlePatientOutput, final EnumSet<Outputs> printOutputs, final ArrayList<EpidemiologicOutputFormat> toPrint) {
		super();
		this.printOutputs = printOutputs;
		this.timeHorizon = timeHorizon;
		this.out = out;
		this.outListeners = outListeners;
		this.outJSON = outJSON;
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
		if (printOutputs.contains(Outputs.BI)) {
			baseCaseExpListeners.add(new BudgetImpactView(secParams, 10));
		}
		for (final EpidemiologicOutputFormat format : toPrint) {
			expListeners.add(new EpidemiologicView(nRuns, secParams, format.getInterval(), format.getType(), format.isAbsolute(), format.isByAge()));
			baseCaseExpListeners.add(new EpidemiologicView(1, secParams, format.getInterval(), format.getType(), format.isAbsolute(), format.isByAge()));
		}
		this.jsonWriter = new DiabPlusJSONWriter(nRuns, interventions, secParams.getRegisteredComplicationStages());
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
			str.append(CostListener.getStrHeader(shortName + "0"));
			str.append(LYListener.getStrHeader(shortName + "0"));
			str.append(QALYListener.getStrHeader(shortName + "0"));
			str.append(AcuteComplicationCounterListener.getStrHeader(shortName));
		}
		str.append(TimeFreeOfComplicationsView.getStrHeader(false, interventions, secParams.getRegisteredComplicationStages()));
		str.append(secParams.getStrHeader());
		return str.toString();
	}
	
	private String print(DiabetesSimulation simul, HbA1cListener[] hba1cListeners, CostListener[] costListeners, LYListener[] lyListeners, QALYListener[] qalyListeners, CostListener[] costListeners0, LYListener[] lyListeners0, QALYListener[] qalyListeners0, AcuteComplicationCounterListener[] acuteListeners, TimeFreeOfComplicationsView timeFreeListener) {
		final StringBuilder str = new StringBuilder();
		str.append("" +  simul.getIdentifier() + "\t");
		for (int i = 0; i < interventions.size(); i++) {
			str.append(hba1cListeners[i]);
			str.append(costListeners[i]);
			str.append(lyListeners[i]);
			str.append(qalyListeners[i]);
			str.append(costListeners0[i]);
			str.append(lyListeners0[i]);
			str.append(qalyListeners0[i]);
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
		final CostListener[] costListeners0 = new CostListener[nInterventions];
		final LYListener[] lyListeners0 = new LYListener[nInterventions];
		final QALYListener[] qalyListeners0 = new QALYListener[nInterventions];
		final AcuteComplicationCounterListener[] acuteListeners = new AcuteComplicationCounterListener[nInterventions];

		for (int i = 0; i < nInterventions; i++) {
			hba1cListeners[i] = new HbA1cListener(nPatients);
			costListeners[i] = new CostListener(secParams.getCostCalculator(common.getAnnualNoComplicationCost(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), discountCost, nPatients);
			lyListeners[i] = new LYListener(discountEffect, nPatients);
			qalyListeners[i] = new QALYListener(secParams.getUtilityCalculator(common.getNoComplicationDisutility(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), discountEffect, nPatients);
			costListeners0[i] = new CostListener(secParams.getCostCalculator(common.getAnnualNoComplicationCost(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), Discount.zeroDiscount, nPatients);
			lyListeners0[i] = new LYListener(Discount.zeroDiscount, nPatients);
			qalyListeners0[i] = new QALYListener(secParams.getUtilityCalculator(common.getNoComplicationDisutility(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), Discount.zeroDiscount, nPatients);
			acuteListeners[i] = new AcuteComplicationCounterListener(nPatients);
		}
		final DiabetesIntervention[] intInstances = common.getInterventions();
		DiabetesSimulation simul = new DiabetesSimulation(id, intInstances[0], nPatients, common, secParams.getPopulation(), timeHorizon);
		simul.addInfoReceiver(hba1cListeners[0]);
		simul.addInfoReceiver(costListeners[0]);
		simul.addInfoReceiver(lyListeners[0]);
		simul.addInfoReceiver(qalyListeners[0]);
		simul.addInfoReceiver(costListeners0[0]);
		simul.addInfoReceiver(lyListeners0[0]);
		simul.addInfoReceiver(qalyListeners0[0]);
		simul.addInfoReceiver(acuteListeners[0]);
		simul.addInfoReceiver(timeFreeListener);
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
			simul = new DiabetesSimulation(simul, intInstances[i]);
			simul.addInfoReceiver(hba1cListeners[i]);
			simul.addInfoReceiver(costListeners[i]);
			simul.addInfoReceiver(lyListeners[i]);
			simul.addInfoReceiver(qalyListeners[i]);
			simul.addInfoReceiver(costListeners0[i]);
			simul.addInfoReceiver(lyListeners0[i]);
			simul.addInfoReceiver(qalyListeners0[i]);
			simul.addInfoReceiver(acuteListeners[i]);
			simul.addInfoReceiver(timeFreeListener);
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
		if (baseCase) {
			jsonWriter.notifyEndBaseCase(simul, hba1cListeners, costListeners, lyListeners, qalyListeners, costListeners0, lyListeners0, qalyListeners0, acuteListeners, timeFreeListener);
		}
		else {
			jsonWriter.notifyEndProbabilisticRun(simul, hba1cListeners, costListeners, lyListeners, qalyListeners, costListeners0, lyListeners0, qalyListeners0, acuteListeners, timeFreeListener);
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
		out.println(print(simul, hba1cListeners, costListeners, lyListeners, qalyListeners, costListeners0, lyListeners0, qalyListeners0, acuteListeners, timeFreeListener));
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
			if (expListeners.size() > 0) {
				outListeners.println(BasicConfigParams.STR_SEP);
				outListeners.println("PSA");
				outListeners.println(BasicConfigParams.STR_SEP);
				for (ExperimentListener listener : expListeners) {
					outListeners.println(listener);
				}		
			}
			// Update JSON with results from probabilistic experiments
	        jsonWriter.notifyEndProbabilisticExperiments();
		}
		
		
        if (!quiet)
        	System.out.println("Execution time: " + ((System.currentTimeMillis() - t) / 1000) + " sec");
        outJSON.println(jsonWriter.getJSON());
        out.close();
        outListeners.close();
        outJSON.close();
	}

	private static SecondOrderParamsRepository loadJSON(int nPatients, String jsonFile) {
		JSONObject json = null;
		try {
			String str = new String(Files.readAllBytes(Paths.get(jsonFile)));
			json = new JSONObject(str).getJSONObject("patient");
		} catch (IOException e) {
			e.printStackTrace();
		}
		final double hypoRate = json.getDouble("hypoRate");
		final double baseHbA1cLevel = json.getDouble("baseHbA1cLevel");
		final double objHbA1cLevel = json.getDouble("objHbA1cLevel");
		final double annualCost = json.getDouble("annualCost");
		final double age = json.getDouble("age");
		final double durationOfDiabetes = json.getDouble("durationOfDiabetes");
		final boolean man = json.getBoolean("man");
		final DiabPlusStdPopulation population = new DiabPlusStdPopulation(man, baseHbA1cLevel, age, durationOfDiabetes);
        return new DiabPlusSecondOrderRepository(nPatients, population, hypoRate, baseHbA1cLevel, objHbA1cLevel, annualCost);		
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

	        final SecondOrderParamsRepository secParams = loadJSON(args1.nPatients, args1.inputJSONFileName);
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
	    		if (args1.bi)
	    			printOutputs.add(Outputs.BI);
	    		if (args1.individualOutcomes)
	    			printOutputs.add(Outputs.INDIVIDUAL_OUTCOMES);
	    		if (args1.breakdownCost)
	    			printOutputs.add(Outputs.BREAKDOWN_COST);
	    		final Discount discountCost;
	    		final Discount discountEffect;
	    		if (args1.discount.size() == 0) {
		    		discountCost = new StdDiscount(BasicConfigParams.DEF_DISCOUNT_RATE);
		    		discountEffect = new StdDiscount(BasicConfigParams.DEF_DISCOUNT_RATE);
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
	    		final ArrayList<EpidemiologicOutputFormat> formats = new ArrayList<>();
	    		for (final String format : args1.epidem) {
	    			final EpidemiologicOutputFormat f = EpidemiologicOutputFormat.build(format);
	    			if (f != null)
	    				formats.add(f);
	    		}
	    		
	    		// Set outputs: different files for simulation outputs and for other outputs. If no file name is specified or an error arises, standard output is used
				PrintWriter out;
				PrintWriter outListeners;
				PrintWriter outJSON;
		        final String outputJSONFileName = (args1.outputFileName == null) ? "out.json" : args1.outputJSONFileName;
				try {
					outJSON = new PrintWriter(new BufferedWriter(new FileWriter(outputJSONFileName)));
				} catch (IOException e1) {
					outJSON = new PrintWriter(System.out);
					e1.printStackTrace();
				}
		        if (args1.outputFileName == null) {
		        	out = new PrintWriter(System.out);
		        	outListeners = out;
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

	    		final DiabPlusMain experiment = new DiabPlusMain(out, outListeners, outJSON, secParams, args1.nRuns, timeHorizon, discountCost, discountEffect, args1.parallel, args1.quiet, args1.singlePatientOutput, printOutputs, formats);
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
		@Parameter(description = "Name of the input JSON file name", order = 1)
		private String inputJSONFileName = System.getProperty("user.dir") + "\\src\\es\\ull\\iis\\simulation\\hta\\diabetes\\diabplus\\test_patient1.json";
		@Parameter(names ={"--outputjson", "-jout"}, description = "Name of the output JSON file name", order = 1)
		private String outputJSONFileName = null;
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
