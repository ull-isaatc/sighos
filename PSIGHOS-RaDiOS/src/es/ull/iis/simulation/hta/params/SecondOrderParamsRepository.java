/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.GenerateSecondOrderInstances;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.SecondOrderIntervention;
import es.ull.iis.simulation.hta.interventions.SecondOrderIntervention.Intervention;
import es.ull.iis.simulation.hta.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.populations.Population;
import es.ull.iis.simulation.hta.progression.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.progression.DeathSubmodel;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPair;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.SecondOrderAcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.progression.SecondOrderDeathSubmodel;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.util.Statistics;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * A repository to define the second order parameters for the simulation. It contains several "standard" collections 
 * of {@link SecondOrderParam parameters}, grouped by type: probability, {@link SecondOrderCostParam cost} and utility. It also contains a miscellaneous category (others) 
 * for all other parameters which do not fit in the former collections. To add a parameter:
 * <ol>
 * <li>Create a constant name</li>
 * <li>Create a method to return the parameter (or ensure that the parameter is added to one of the standard collections, which already defined access methods)</li>
 * <li>Remember to add the value of the parameter in the corresponding subclasses</li> 
 * </ol>
 * <p>
 * This repository also contains the acute and chronic complications defined for diabetes. Complications must be registered 
 * to be used within the simulation. To register a complication:
 * <ol>
 * <li>Use the {@link #registerComplication(ChronicComplication)} or the {@link #registerComplication(AcuteComplication)} method</li>
 * <li>Currently, only chronic complications allow for stages. Use the {@link #registerComplicationStages(Manifestation[])} method to add them</li>
 * </ol>
 * </p>
 * @author Iván Castilla Rodríguez
 */
public abstract class SecondOrderParamsRepository implements GenerateSecondOrderInstances {
	// Strings to define standard parameters
	/** String prefix for probability parameters */
	public static final String STR_PROBABILITY_PREFIX = "P_";
	/** String prefix for relative risk parameters */
	public static final String STR_RR_PREFIX = "RR_";
	/** String prefix for Increased Mortality Rate parameters */
	public static final String STR_IMR_PREFIX = "IMR_";
	/** String prefix for cost parameters */
	public static final String STR_COST_PREFIX = "C_";
	/** String prefix for transition (one-time) cost parameters */
	public static final String STR_TRANS_PREFIX = "TC_";
	/** String prefix for utility parameters */
	public static final String STR_UTILITY_PREFIX = "U_";
	/** String prefix for disutility parameters */
	public static final String STR_DISUTILITY_PREFIX = "DU_";
	/** String prefix for death parameters */
	public static final String STR_DEATH_PREFIX = "DEATH_";
	/** String prefix for initial parameters */
	public static final String STR_INIT_PREFIX = "INIT_";
	/** String for healthy individuals */
	public static final String STR_HEALTHY = "HEALTHY";

	/** A null relative risk, i.e., RR = 1.0 */
	public static final RRCalculator NO_RR = new StdComplicationRR(1.0);
	
	/** The collection of probability parameters */
	final protected TreeMap<String, SecondOrderParam> probabilityParams;
	/** The collection of cost parameters */
	final protected TreeMap<String, SecondOrderCostParam> costParams;
	/** The collection of utility parameters */
	final protected TreeMap<String, SecondOrderParam> utilParams;
	/** The collection of miscellaneous parameters */
	final protected TreeMap<String, SecondOrderParam> otherParams;
	/** A random number generator for first order parameter values */
	private static RandomNumber RNG_FIRST_ORDER = RandomNumberFactory.getInstance();
	/** The collection of defined manifestations */
	final protected ArrayList<Manifestation> registeredManifestations;
	/** The collection of defined diseases */
	final protected ArrayList<Disease> registeredDiseases;

	/** The death submodel to be used */
	protected SecondOrderDeathSubmodel registeredDeathSubmodel = null;
	/** The collection of interventions */
	final protected ArrayList<SecondOrderIntervention> registeredInterventions;
	// TODO: Change by scenarios: each parameter could be defined according to an scenario. This woulud require adding a factory to secondOrderParams and allowing a user to add several parameter settings
	/** Number of patients that should be generated */
	final protected int nPatients;
	/** The population */
	final private Population population;
	/** The number of simulations to run by using this repository */
	final private int nRuns;
	
	/**
	 * Creates a repository of second order parameters. By default, generates the base case values.
	 * @param nPatients Number of patients to create
	 * @param population The population used within this repository
	 */
	protected SecondOrderParamsRepository(final int nRuns, final int nPatients, final Population population) {
		this.population = population;
		this.probabilityParams = new TreeMap<>();
		this.costParams = new TreeMap<>();
		this.otherParams = new TreeMap<>();
		this.utilParams = new TreeMap<>();
		this.nPatients = nPatients;
		this.nRuns = nRuns;
		this.registeredManifestations = new ArrayList<>();
		this.registeredDiseases = new ArrayList<Disease>();
		this.registeredInterventions = new ArrayList<>();
	}

	/**
	 * Checks the model validity and returns a string with the missing components.
	 * @return null if everything is ok; a string with the missing components otherwise
	 */
	public String checkValidity() {
		final StringBuilder str = new StringBuilder();
		if (registeredDiseases.size() == 0)
			str.append("At least one disease must be defined").append(System.lineSeparator());
		if (registeredInterventions.size() == 0) {
			str.append("At least one intervention must be defined").append(System.lineSeparator());
		}
		if (registeredDeathSubmodel == null) {
			str.append("No death submodel defined").append(System.lineSeparator());
		}
		return (str.length() > 0) ? str.toString() : null;
	}
	
	/**
	 * Registers a new disease and its manifestations
	 * @param disease Disease
	 */
	public void registerDisease(Disease disease) {
		registeredDiseases.add(disease);
		for (Manifestation st : disease.getManifestations()) {
			st.setOrder(registeredManifestations.size());
			registeredManifestations.add(st);
		}
		disease.addSecondOrderParams(this);
	}
	
	/**
	 * Returns the registered diseases
	 * @return the registered diseases
	 */
	public Disease[] getRegisteredDiseases() {
		return (Disease[])registeredDiseases.toArray();
	}

	/**
	 * Returns the already registered complication stages
	 * @return The already registered complication stages
	 */
	public ArrayList<Manifestation> getRegisteredManifestations() {
		return registeredManifestations;
	}

	/**
	 * Registers a new intervention 
	 * @param intervention The description of an intervention
	 */
	public void registerIntervention(SecondOrderIntervention intervention) {
		registeredInterventions.add(intervention);
		intervention.addSecondOrderParams(this);
	}
	
	public ArrayList<SecondOrderIntervention> getRegisteredInterventions() {
		return registeredInterventions;
	}

	/**
	 * Registers the death submodel. Returns false if there was an already registered death submodel
	 * @param deathSubmodel Death submodel to be used
	 * @return false if there was an already registered death submodel; true otherwise
	 */
	public boolean registerDeathSubmodel(SecondOrderDeathSubmodel deathSubmodel) {
		if (registeredDeathSubmodel != null)
			return false;
		registeredDeathSubmodel = deathSubmodel;
		return true;
	}
	
	/**
	 * Returns the registered death submodel; null if no submodel was registered
	 * @return the registered death submodel; null if no submodel was registered
	 */
	public SecondOrderDeathSubmodel getRegisteredDeathSubmodel() {
		return registeredDeathSubmodel;
	}
	/**
	 * Returns the minimum age for patients within this repository, which is the minimum age of the population
	 * @return the minimum age for patients within this repository
	 */
	public int getMinAge() {
		return population.getMinAge();
	}

	/**
	 * Returns the number of interventions included in the simulation
	 * @return The number of interventions included in the simulation
	 */
	public final int getNInterventions() {
		return registeredInterventions.size();
	}

	public Population getPopulation() {
		return population;
	}

	/**
	 * @return the nRuns
	 */
	public int getnRuns() {
		return nRuns;
	}

	/**
	 * Generates n random numbers for each parameter in the repository
	 */
	@Override
	public void generate(SecondOrderParamsRepository secOrder) {
		for (SecondOrderParam param : probabilityParams.values())
			param.generate(null);
		for (SecondOrderParam param : costParams.values())
			param.generate(null);
		for (SecondOrderParam param : utilParams.values())
			param.generate(null);
		for (SecondOrderParam param : otherParams.values())
			param.generate(null);
	}
	/**
	 * Adds a probability parameter
	 * @param param Probability parameter
	 */
	public void addProbParam(SecondOrderParam param) {
		probabilityParams.put(param.getName(), param);
	}
	
	/**
	 * Adds a cost parameter
	 * @param param Cost parameter
	 */
	public void addCostParam(SecondOrderCostParam param) {
		costParams.put(param.getName(), param);
	}
	
	/**
	 * Adds a cost parameter associated to a complication or complication stage
	 * @param stage A complication or complication stage
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param year Year when the cost was originally estimated
	 * @param detValue Deterministic/expected value
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 */
	public void addCostParam(Named stage, String description, String source, int year, double detValue, RandomVariate rnd) {
		final String paramName = STR_COST_PREFIX + stage.name();
		addCostParam(new SecondOrderCostParam(paramName, description, source, year, detValue, rnd));
	}
	
	/**
	 * Adds a transition (or one-time) cost parameter associated to a complication or complication stage
	 * @param stage A complication or complication stage
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param year Year when the cost was originally estimated
	 * @param detValue Deterministic/expected value
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 */
	public void addTransitionCostParam(Named stage, String description, String source, int year, double detValue, RandomVariate rnd) {
		final String paramName = STR_TRANS_PREFIX + stage.name();
		addCostParam(new SecondOrderCostParam(paramName, description, source, year, detValue, rnd));
	}
	
	/**
	 * Adds a utility or disutility parameter
	 * @param param Utility parameter
	 */
	public void addUtilParam(SecondOrderParam param) {
		utilParams.put(param.getName(), param);
	}

	/**
	 * Adds a utility parameter
	 * @param stage A complication or complication stage
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 */
	public void addUtilityParam(Named stage, String description, String source, double detValue, RandomVariate rnd) {
		final String paramName = STR_UTILITY_PREFIX + stage.name();
		addUtilParam(new SecondOrderParam(paramName, description, source, detValue, rnd));
	}

	/**
	 * Adds a disutility parameter
	 * @param stage A complication or complication stage
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 */
	public void addDisutilityParam(Named stage, String description, String source, double detValue, RandomVariate rnd) {
		final String paramName = STR_DISUTILITY_PREFIX + stage.name();
		addUtilParam(new SecondOrderParam(paramName, description, source, detValue, rnd));
	}

	/**
	 * Adds a parameter that represents the increase mortality rate (IMR) associated to a complication or complication stage; 
	 * 1.0 if no additional risk is associated
	 * @param stage A complication or complication stage
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 */
	public void addIMRParam(Named stage, String description, String source, double detValue, RandomVariate rnd) {
		final String paramName = STR_IMR_PREFIX + stage.name();
		otherParams.put(paramName, new SecondOrderParam(paramName, description, source, detValue, rnd));
	}
	/**
	 * Adds a miscellaneous parameter
	 * @param param Miscellanous parameter
	 */
	public void addOtherParam(SecondOrderParam param) {
		otherParams.put(param.getName(), param);
	}

	/**
	 * Returns a value for a miscellaneous parameter
	 * @param name String identifier of the miscellaneous parameter
	 * @return A value for the specified miscellaneous parameter; {@link Double#NaN} in case the parameter is not defined
	 */
	public double getOtherParam(String name, int id) {
		final SecondOrderParam param = otherParams.get(name);
		return (param == null) ? Double.NaN : param.getValue(id); 
	}
	
	/**
	 * Returns a value for a cost parameter
	 * @param name String identifier of the cost parameter
	 * @return A value for the specified cost parameter; {@link Double#NaN} in case the parameter is not defined
	 */
	public double getCostParam(String name, int id) {
		final SecondOrderParam param = costParams.get(name);
		return (param == null) ? Double.NaN : param.getValue(id); 
	}
	
	/**
	 * Returns the probability from healthy to the specified complication or complication stage; 0.0 if not defined
	 * @param stage The destination complication or complication stage
	 * @return the probability from healthy to the specified complication or complication stage; 0.0 if not defined
	 */
	public double getProbability(Named stage, int id) {
		return getProbability(null, stage, id); 
	}

	/**
	 * Returns the probability from a complication to another; 0.0 if not defined
	 * @param fromStage The source complication or complication stage
	 * @param toStage The destination complication or complication stage
	 * @return the probability of developing a complication to another; 0.0 if not defined
	 */
	public double getProbability(Named fromStage, Named toStage, int id) {
		return getProbParam(getProbString(fromStage, toStage), id);
	}

	/**
	 * Returns a value for a probability parameter
	 * @param name String identifier of the probability parameter
	 * @return A value for the specified probability parameter; 0.0 in case the parameter is not defined
	 */	
	public double getProbParam(String name, int id) {
		final SecondOrderParam param = probabilityParams.get(name);
		return (param == null) ? 0.0 : param.getValue(id); 
	}

	/**
	 * Returns a value for the probability of starting with a complication
	 * @param stage Complication
	 * @return A value for the specified probability parameter; 0.0 in case the parameter is not defined
	 */	
	public double getInitProbParam(Named stage, int id) {
		final SecondOrderParam param = probabilityParams.get(getInitProbString(stage));
		return (param == null) ? 0.0 : param.getValue(id); 
	}

	/**
	 * Returns a value for the probability of dying from a manifestation
	 * @param stage Manifestation
	 * @return A value for the specified probability parameter; 0.0 in case the parameter is not defined
	 */	
	public double getDeathProbParam(Named stage, int id) {
		final SecondOrderParam param = probabilityParams.get(getDeathProbString(stage));
		return (param == null) ? 0.0 : param.getValue(id); 
	}

	/**
	 * Returns the increase mortality rate associated to a complication or complication stage; 1.0 if no additional risk is associated
	 * @param stage Complication or complication stage
	 * @return the increase mortality rate associated to a complication or complication stage; 1.0 if no additional risk is associated
	 */
	public double getIMR(Named stage, int id) {
		final SecondOrderParam param = otherParams.get(STR_IMR_PREFIX + stage.name());
		return (param == null) ? 1.0 : Math.max(1.0, param.getValue(id));		
	}
	
	/**
	 * Returns the cost for a complication or complication stage &ltannual cost, cost at incidence&gt; &lt0, 0&gt
	 * if not defined
	 * @param stage Complication or complication stage
	 * @return the cost for a complication or complication stage &ltannual cost, cost at incidence&gt; &lt0, 0&gt
	 * if not defined 
	 */
	public double[] getCostsForChronicComplication(Named stage, int id) {
		final double[] costs = new double[2];
		final SecondOrderParam annualCost = costParams.get(STR_COST_PREFIX + stage.name());
		final SecondOrderParam transCost = costParams.get(STR_TRANS_PREFIX + stage.name());
		costs[0] = (annualCost == null) ? 0.0 : annualCost.getValue(id);
		costs[1] = (transCost == null) ? 0.0 : transCost.getValue(id);
		return costs;
	}
	
	/**
	 * Returns the disutility for a complication or complication stage; 0.0 if not defined
	 * @param stage Complication or complication stage
	 * @return the disutility for a complication or complication stage; 0.0 if not defined 
	 */
	public double getDisutilityForChronicComplication(Named stage, int id) {
		final SecondOrderParam param = utilParams.get(STR_DISUTILITY_PREFIX + stage.name());
		return (param == null) ? 0.0 : param.getValue(id);		
	}
	
	/**
	 * Returns the random number generator for first order uncertainty
	 * @return the random number generator for first order uncertainty
	 */
	public static RandomNumber getRNG_FIRST_ORDER() {
		return RNG_FIRST_ORDER;
	}

	/**
	 * Changes the default random number generator for first order uncertainty
	 * @param rngFirstOrder New random number generator
	 */
	public static void setRNG_FIRST_ORDER(RandomNumber rngFirstOrder) {
		RNG_FIRST_ORDER = rngFirstOrder;
	}

	/**
	 * Returns the number of patients that will be generated during the simulation
	 * @return the number of patients that will be generated during the simulation
	 */
	public int getnPatients() {
		return nPatients;
	}

	private final DeathSubmodel getDeathSubmodel() {
		return (DeathSubmodel)registeredDeathSubmodel.getInstance(this);
	}
	/**
	 * Returns the interventions to be compared within the simulation
	 * @return The interventions to be compared within the simulation
	 */
	protected Intervention[] getInterventions() {
		final Intervention[] interventions = new Intervention[registeredInterventions.size()];
		for (int i = 0; i < registeredInterventions.size(); i++) {
			interventions[i] = registeredInterventions.get(i).getInstance(i, this);
		}
		return interventions;
	}

	/**
	 * Returns the list of first order instances of the chronic complication submodels
	 * @return the list of first order instances of the chronic complication submodels
	 */
	private final Disease[] getDiseaseInstances() {
		final Disease[] diseases = new Disease[registeredDiseases.size()];
		
		int cont = 0;
		for (Disease secDis : registeredDiseases) {
			diseases[cont++] = secDis.getInstance(this);
		}
		return diseases;
	}

	/**
	 * Returns the class that computes costs 
	 * @param cDNC Cost of diabetes with no complications
	 * @param submodels Submodels for chronic complications
	 * @param acuteSubmodels Submodels for acute complications
	 * @return the class that computes costs 
	 */
	public abstract CostCalculator getCostCalculator(double cDNC, Disease[] submodels, AcuteComplicationSubmodel[] acuteSubmodels);
	/**
	 * Returns the class that computes utilities 
	 * @param submodels Submodels for chronic complications
	 * @param acuteSubmodels Submodels for acute complications
	 * @return the class that computes utilities 
	 */
	public abstract UtilityCalculator getUtilityCalculator(Disease[] submodels, AcuteComplicationSubmodel[] acuteSubmodels);
	
	
	/**
	 * Builds a string that represents a probability of developing a complication from another
	 * @param from Initial complication
	 * @param to Final complication
	 * @return a string that represents a probability of developing a complication from another
	 */
	public static String getProbString(Named from, Named to) {
		final String fromName = (from == null) ? STR_HEALTHY : from.name();
		final String toName = "_" + to.name();
		return STR_PROBABILITY_PREFIX + fromName + toName;
	}
	
	/**
	 * Builds a string that represents a probability of starting with a complication
	 * @param to Final complication
	 * @return a string that represents a probability of starting with a complication
	 */
	public static String getInitProbString(Named to) {
		return STR_PROBABILITY_PREFIX + STR_INIT_PREFIX + to.name();
	}

	/**
	 * Builds a string that represents a probability of dying from a manifestation
	 * @param to Manifestation
	 * @return a string that represents a probability of dying from a manifestation
	 */
	public static String getDeathProbString(Named to) {
		return STR_PROBABILITY_PREFIX + STR_DEATH_PREFIX + to.name();
	}
	
	/**
	 * Creates a Gamma distribution to add uncertainty to a deterministic cost. Uses the {@link BasicConfigParams#DEF_SECOND_ORDER_VARIATION} 
	 * parameters to adjust the uncertainty
	 * @param detCost Deterministic cost
	 * @return a Gamma random distribution that represents the uncertainty around a cost
	 */
	public static RandomVariate getRandomVariateForCost(double detCost) {
		if (detCost == 0.0) {
			return RandomVariateFactory.getInstance("ConstantVariate", detCost);
		}
		final double costVariance2 = BasicConfigParams.DEF_SECOND_ORDER_VARIATION.COST * BasicConfigParams.DEF_SECOND_ORDER_VARIATION.COST;
		final double invCostVariance2 = 1 / costVariance2;
		return RandomVariateFactory.getInstance("GammaVariate", invCostVariance2, costVariance2 * detCost);
	}

	/**
	 * Creates a uniform distribution to add uncertainty to a deterministic probability. Uses the {@link BasicConfigParams#DEF_SECOND_ORDER_VARIATION} 
	 * parameters to adjust the uncertainty
	 * @param detProb Deterministic probability
	 * @return a uniform distribution that represents the uncertainty around a probability parameter
	 */
	public static RandomVariate getRandomVariateForProbability(double detProb) {
		if (detProb == 0.0) {
			return RandomVariateFactory.getInstance("ConstantVariate", detProb);
		}
		return RandomVariateFactory.getInstance("UniformVariate", detProb * (1-BasicConfigParams.DEF_SECOND_ORDER_VARIATION.PROBABILITY), detProb * (1+BasicConfigParams.DEF_SECOND_ORDER_VARIATION.PROBABILITY));
	}

	/**
	 * Generates a time to event based on annual risk. The time to event is absolute, i.e., can be used directly to schedule a new event. 
	 * @param pat A patient
	 * @param annualRisk Annual risk of the event)
	 * @param logRnd The natural log of a random number (0,1)
	 * @param rr Relative risk for the patient
	 * @return a time to event based on annual risk
	 */
	public static long getAnnualBasedTimeToEvent(Patient pat, double annualRisk, double logRnd, double rr) {
		final double time = Statistics.getAnnualBasedTimeToEvent(annualRisk, logRnd, rr);
	
		return (time >= (pat.getAgeAtDeath() - pat.getAge())) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
	}
	
	/**
	 * Generates a time to event based on annual rate. The time to event is absolute, i.e., can be used directly to schedule a new event. 
	 * @param pat A patient
	 * @param annualRate Annual rate of the event
	 * @param logRnd The natural log of a random number (0,1)
	 * @param irr Incidence rate ratio for the patient
	 * @return a time to event based on annual rate
	 */
	public static long getAnnualBasedTimeToEventFromRate(Patient pat, double annualRate, double logRnd, double irr) {
		final double time = Statistics.getAnnualBasedTimeToEventFromRate(annualRate, logRnd, irr);
	
		return (time >= (pat.getAgeAtDeath() - pat.getAge())) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
	}
	
	/**
	 * Creates a string that contains a tab separated list of the parameter names defined in this repository
	 * @return a string that contains a tab separated list of the parameter names defined in this repository
	 */
	public String getStrHeader() {
		StringBuilder str = new StringBuilder();
		for (SecondOrderParam param : probabilityParams.values())
			str.append(param.getName()).append("\t");
		for (SecondOrderParam param : costParams.values())
			str.append(param.getName()).append("\t");
		for (SecondOrderParam param : utilParams.values())
			str.append(param.getName()).append("\t");
		for (SecondOrderParam param : otherParams.values())
			str.append(param.getName()).append("\t");
		return str.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (SecondOrderParam param : probabilityParams.values())
			str.append(param.getLastGeneratedValue()).append("\t");
		for (SecondOrderParam param : costParams.values())
			str.append(param.getLastGeneratedValue()).append("\t");
		for (SecondOrderParam param : utilParams.values())
			str.append(param.getLastGeneratedValue()).append("\t");
		for (SecondOrderParam param : otherParams.values())
			str.append(param.getLastGeneratedValue()).append("\t");
		return str.toString();
	}

	/**
	 * Returns an instance of the repository that will be shared among the simulations for the different interventions.
	 * @return an instance of the repository that will be shared among the simulations for the different interventions.
	 */
	public RepositoryInstance getInstance() {
		return new RepositoryInstance();
	}
	
	/**
	 * A repository to handle the simulation values of second order parameters. At creation, draws a value for each second-order
	 * parameter, and then stores the value to be used during the simulation.
	 * TODO El cálculo de tiempo hasta complicación usa siempre el mismo número aleatorio para la misma complicación. Si aumenta el riesgo de esa
	 * complicación en un momento de la simulación, se recalcula el tiempo, pero empezando en el instante actual. Esto produce que no necesariamente se acorte
	 * el tiempo hasta evento en caso de un nuevo factor de riesgo. ¿debería reescalar de alguna manera el tiempo hasta evento en estos casos (¿proporcional al RR?)?
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public class RepositoryInstance {
		/** Chronic complication submodels included */
		private final Disease[] diseases;
		/** Death submodel */
		private final DeathSubmodel deathSubmodel; 
		/** Interventions being assessed */
		private final Intervention[] interventions;
		
		/**
		 * Creates a repository for first order simulations 
		 * @param secondOrder The second order repository that defines the second-order uncertainty on the parameters
		 */
		private RepositoryInstance() {
			diseases = SecondOrderParamsRepository.this.getDiseaseInstances();
			deathSubmodel = SecondOrderParamsRepository.this.getDeathSubmodel();
			interventions = SecondOrderParamsRepository.this.getInterventions();
		}

		/**
		 * Returns the complication stages related to the chronic complications
		 * @return the complication stages related to the chronic complications
		 */
		public ArrayList<Manifestation> getRegisteredComplicationStages() {
			return SecondOrderParamsRepository.this.getRegisteredManifestations();
		}

		/**
		 * Returns the diseases
		 * @return the diseases
		 */
		public Disease[] getDiseases() {
			return diseases;
		}
		
		/**
		 * Returns the interventions being assessed
		 * @return the interventions
		 */
		public Intervention[] getInterventions() {
			return interventions;
		}

		/**
		 * Returns the life expectancy of the patient
		 * @param pat A patient
		 * @return the life expectancy of the patient
		 */
		public long getTimeToDeath(Patient pat) {
			return deathSubmodel.getTimeToDeath(pat);
		}

		/**
		 * Restarts the parameters among interventions. Useful to reuse already computed values for a previous intervention and
		 * preserve common random numbers
		 */
		public void reset() {
			for (AcuteComplicationSubmodel acuteSubmodel : acuteCompSubmodels.values())
				acuteSubmodel.reset();
		}
	}
}
