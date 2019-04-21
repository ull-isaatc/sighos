/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatientGenerator;
import es.ull.iis.simulation.hta.diabetes.Named;
import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.interventions.DiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.DeathSubmodel;
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
 * <li>Use the {@link #registerComplication(DiabetesChronicComplications)} or the {@link #registerComplication(DiabetesAcuteComplications)} method</li>
 * <li>Currently, only chronic complications allow for stages. Use the {@link #registerComplicationStages(DiabetesComplicationStage[])} method to add them</li>
 * </ol>
 * </p>
 * @author Iván Castilla Rodríguez
 */
public abstract class SecondOrderParamsRepository {
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
	
	/** String descriptor for diabetes with no complications */
	public static final String STR_NO_COMPLICATIONS = "DNC";
	/** String descriptor for the discount rate */
	public static final String STR_DISCOUNT_RATE = "DISCOUNT_RATE";

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
	/** If true, forces the discount rate to be zero, even if previously defined by assigning a value through the {@link #STR_DISCOUNT_RATE} parameter */
	private boolean discountZero = false;
	/** A random number generator for first order parameter values */
	final private RandomNumber rngFirstOrder;
	/** The collection of defined chronic complication stages */
	final protected ArrayList<DiabetesComplicationStage> registeredComplicationStages;
	/** The collection of defined chronic complications */
	final protected TreeSet<DiabetesChronicComplications> registeredChronicComplication;
	/** The collection of defined acute complications */
	final protected TreeSet<DiabetesAcuteComplications> registeredAcuteComplication;	
	/** True if base case parameters (expected values) should be used. False for second order simulations */
	protected boolean baseCase = true;
	/** Number of patients that should be generated */
	final protected int nPatients;
	/** Minimum age for patients within this repository */
	private int minAge = BasicConfigParams.DEF_MIN_AGE;
	final private ArrayList<DiabetesPatientGenerator.DiabetesPatientGenerationInfo> populations;
	
	/**
	 * Creates a repository of second order parameters. By default, generates the base case values.
	 * @param nPatients Number of patients to create
	 */
	protected SecondOrderParamsRepository(int nPatients) {
		populations = new ArrayList<>();
		probabilityParams = new TreeMap<>();
		costParams = new TreeMap<>();
		otherParams = new TreeMap<>();
		utilParams = new TreeMap<>();
		this.rngFirstOrder = RandomNumberFactory.getInstance();
		this.nPatients = nPatients;
		this.registeredComplicationStages = new ArrayList<>();
		this.registeredChronicComplication = new TreeSet<>();
		this.registeredAcuteComplication = new TreeSet<>();
	}

	/**
	 * Registers a collection of new chronic complication stages
	 * @param newStages Chronic complication stages
	 */
	public void registerComplicationStages(DiabetesComplicationStage[] newStages) {
		for (DiabetesComplicationStage st : newStages) {
			st.setOrder(registeredComplicationStages.size());
			registeredComplicationStages.add(st);
		}
	}
	
	/**
	 * Registers a new chronic complication 
	 * @param comp Chronic complication
	 */
	public void registerComplication(DiabetesChronicComplications comp) {
		registeredChronicComplication.add(comp);
	}
	
	/**
	 * Registers a new acute complication 
	 * @param comp Acute complication
	 */
	public void registerComplication(DiabetesAcuteComplications comp) {
		registeredAcuteComplication.add(comp);
	}
	
	/**
	 * Returns true if the specified chronic complication is already registered
	 * @param comp Chronic complication
	 * @return True if the specified chronic complication is already registered
	 */
	public boolean isRegistered(DiabetesChronicComplications comp) {
		return (registeredChronicComplication.contains(comp));
	}

	/**
	 * Returns true if the specified acute complication is already registered
	 * @param comp Acute complication
	 * @return True if the specified acute complication is already registered
	 */
	public boolean isRegistered(DiabetesAcuteComplications comp) {
		return (registeredAcuteComplication.contains(comp));
	}
	
	/**
	 * Returns the already registered complication stages
	 * @return The already registered complication stages
	 */
	public ArrayList<DiabetesComplicationStage> getRegisteredComplicationStages() {
		return registeredComplicationStages;
	}

	/**
	 * Returns the minimum age for patients within this repository
	 * @return the minimum age for patients within this repository
	 */
	public int getMinAge() {
		return minAge;
	}
	
	/**
	 * Returns the interventions to be compared within the simulation
	 * @return The interventions to be compared within the simulation
	 */
	public abstract DiabetesIntervention[] getInterventions();

	/**
	 * Returns the number of interventions included in the simulation
	 * @return The number of interventions included in the simulation
	 */
	public abstract int getNInterventions();

	public DiabetesPatientGenerator.DiabetesPatientGenerationInfo[] getPopulations() {
		return populations.toArray(new DiabetesPatientGenerator.DiabetesPatientGenerationInfo[populations.size()]);
	}
	
	protected void addPopulation(DiabetesPatientGenerator.DiabetesPatientGenerationInfo pop) {
		populations.add(pop);
		if (pop.getPopulation().getMinAge() < minAge)
			minAge = pop.getPopulation().getMinAge();
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
	 * Adds a utility parameter
	 * @param param Utility parameter
	 */
	public void addUtilParam(SecondOrderParam param) {
		utilParams.put(param.getName(), param);
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
	public double getOtherParam(String name) {
		final SecondOrderParam param = otherParams.get(name);
		return (param == null) ? Double.NaN : param.getValue(baseCase); 
	}
	
	/**
	 * Returns a value for a cost parameter
	 * @param name String identifier of the cost parameter
	 * @return A value for the specified cost parameter; {@link Double#NaN} in case the parameter is not defined
	 */
	public double getCostParam(String name) {
		final SecondOrderParam param = costParams.get(name);
		return (param == null) ? Double.NaN : param.getValue(baseCase); 
	}
	
	/**
	 * Returns the probability from healthy to the specified complication or complication stage; 0.0 if not defined
	 * @param stage The destination complication or complication stage
	 * @return the probability from healthy to the specified complication or complication stage; 0.0 if not defined
	 */
	public double getProbability(Named stage) {
		return getProbability(null, stage); 
	}

	/**
	 * Returns the probability from a complication to another; 0.0 if not defined
	 * @param fromStage The source complication or complication stage
	 * @param toStage The destination complication or complication stage
	 * @return the probability of developing a complication to another; 0.0 if not defined
	 */
	public double getProbability(Named fromStage, Named toStage) {
		return getProbParam(getProbString(fromStage, toStage));
	}

	/**
	 * Returns a value for a probability parameter
	 * @param name String identifier of the probability parameter
	 * @return A value for the specified probability parameter; 0.0 in case the parameter is not defined
	 */	
	public double getProbParam(String name) {
		final SecondOrderParam param = probabilityParams.get(name);
		return (param == null) ? 0.0 : param.getValue(baseCase); 
	}

	/**
	 * Returns the increase mortality rate associated to a complication or complication stage; 1.0 if no additional risk is associated
	 * @param stage Complication or complication stage
	 * @return the increase mortality rate associated to a complication or complication stage; 1.0 if no additional risk is associated
	 */
	public double getIMR(Named stage) {
		final SecondOrderParam param = otherParams.get(STR_IMR_PREFIX + stage.name());
		return (param == null) ? 1.0 : Math.max(1.0, param.getValue(baseCase));		
	}
	
	/**
	 * Returns the cost for a complication or complication stage &ltannual cost, cost at incidence&gt; &lt0, 0&gt
	 * if not defined
	 * @param stage Complication or complication stage
	 * @return the cost for a complication or complication stage &ltannual cost, cost at incidence&gt; &lt0, 0&gt
	 * if not defined 
	 */
	public double[] getCostsForChronicComplication(Named stage) {
		final double[] costs = new double[2];
		final SecondOrderParam annualCost = costParams.get(STR_COST_PREFIX + stage.name());
		final SecondOrderParam transCost = costParams.get(STR_TRANS_PREFIX + stage.name());
		costs[0] = (annualCost == null) ? 0.0 : annualCost.getValue(baseCase);
		costs[1] = (transCost == null) ? 0.0 : transCost.getValue(baseCase);
		return costs;
	}
	
	/**
	 * Returns the cost for an acute complication; 0.0 if not defined
	 * @param comp Acute complication
	 * @return the cost for an acute complication; 0.0 if not defined
	 */
	public double getCostForAcuteComplication(DiabetesAcuteComplications comp) {
		final SecondOrderParam param = costParams.get(STR_COST_PREFIX + comp.name());
		return (param == null) ? 0.0 : param.getValue(baseCase); 						
	}
	
	/**
	 * Returns the disutility for a complication or complication stage; 0.0 if not defined
	 * @param stage Complication or complication stage
	 * @return the disutility for a complication or complication stage; 0.0 if not defined 
	 */
	public double getDisutilityForChronicComplication(Named stage) {
		final SecondOrderParam param = utilParams.get(STR_DISUTILITY_PREFIX + stage.name());
		return (param == null) ? 0.0 : param.getValue(baseCase);		
	}
	
	/**
	 * Returns the disutility for an acute complication; 0.0 if not defined
	 * @param comp Acute complication
	 * @return the disutility for an acute complication; 0.0 if not defined
	 */
	public double getDisutilityForAcuteComplication(DiabetesAcuteComplications comp) {
		final SecondOrderParam param = utilParams.get(STR_DISUTILITY_PREFIX + comp.name());
		return (param == null) ? 0.0 : param.getValue(baseCase); 		
	}
	
	/**
	 * Returns true if the base case is active; false if the probabilistic analysis is active
	 * @return true if the base case is active; false if the probabilistic analysis is active
	 */
	public boolean isBaseCase() {
		return baseCase;
	}
	
	/**
	 * Returns the random number generator for first order parameter values
	 * @return the random number generator for first order parameter values
	 */
	public RandomNumber getRngFirstOrder() {
		return rngFirstOrder;
	}

	/**
	 * Returns the number of patients that will be generated during the simulation
	 * @return the number of patients that will be generated during the simulation
	 */
	public int getnPatients() {
		return nPatients;
	}

	/**
	 * Returns the increased mortality rate applied for patients with no complications; 1.0 if not defined
	 * @return the increased mortality rate applied for patients with no complications; 1.0 if not defined
	 */
	public double getNoComplicationIMR() {
		final SecondOrderParam param = otherParams.get(STR_IMR_PREFIX + STR_NO_COMPLICATIONS);
		return (param == null) ? 1.0 : param.getValue(baseCase); 		
	}
	
	/**
	 * Returns the annual cost applied to patients with no complications; 0.0 if not defined 
	 * @return the annual cost applied to patients with no complications; 0.0 if not defined
	 */
	public double getNoComplicationAnnualCost() {
		final SecondOrderParam param = costParams.get(STR_COST_PREFIX + STR_NO_COMPLICATIONS);
		return (param == null) ? 0.0 : param.getValue(baseCase); 		
	}
	
	/**
	 * Returns the disutility applied to patients with no complications; 0.0 if not defined
	 * @return the disutility applied to patients with no complications; 0.0 if not defined
	 */
	public double getNoComplicationDisutility() {
		final SecondOrderParam param = utilParams.get(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS);
		return (param == null) ? 0.0 : param.getValue(baseCase); 		
	}
	
	/**
	 * Returns the discount rate 
	 * @return the discount rate
	 */
	public double getDiscountRate() {
		if (discountZero)
			return 0.0;
		final SecondOrderParam param = (SecondOrderParam) otherParams.get(STR_DISCOUNT_RATE);
		return (param == null) ? 0.0 : param.getValue(baseCase); 						
	}
	
	/**
	 * Sets whether the second order parameters will use the base case value or not
	 * @param baseCase True if the second order parameters must use the expected value; false otherwise
	 */
	public void setBaseCase(boolean baseCase) {
		this.baseCase = baseCase;
	}

	/**
	 * Returns True if the discount rate is forced to be zero; false if uses the discount rate as defined in {@link #STR_DISCOUNT_RATE}
	 * @return True if the discount rate is forced to be zero; false if uses the discount rate as defined in {@link #STR_DISCOUNT_RATE}
	 */
	public boolean isDiscountZero() {
		return discountZero;
	}

	/**
	 * Sets whether the simulation will use a zero discount rate or the discount rate as defined in {@link #STR_DISCOUNT_RATE}
	 * @param discountZero If true, the simulation will apply a discount rate = 0.0; otherwise, the simulation will use the discount 
	 * rate as defined in {@link #STR_DISCOUNT_RATE}
	 */
	public void setDiscountZero(boolean discountZero) {
		this.discountZero = discountZero;
	}

	public abstract ChronicComplicationSubmodel[] getComplicationSubmodels();
	public abstract AcuteComplicationSubmodel[] getAcuteComplicationSubmodels();
	public abstract DeathSubmodel getDeathSubmodel();
	public abstract CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels);
	public abstract UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels);
	
	
	/**
	 * Builds a string that represents a probability of developing a complication from another
	 * @param from Initial complication
	 * @param to Final complication
	 * @return a string that represents a probability of developing a complication from another
	 */
	public static String getProbString(Named from, Named to) {
		final String fromName = (from == null) ? STR_NO_COMPLICATIONS : from.name();
		final String toName = "_" + to.name();
		return STR_PROBABILITY_PREFIX + fromName + toName;
	}

	/**
	 * Computes the standard deviation from 95% confidence intervals. Assumes that
	 * the confidence intervals are based in a normal distribution.
	 * @param ci Original 95% confidence intervals
	 * @return the standard deviation corresponding to the specified confidence intervals
	 */
	public static double sdFrom95CI(double[] ci) {
		return (ci[1] - ci[0])/(1.96*2);
	}
	
	/**
	 * Computes the alfa and beta parameters for a beta distribution from an average and
	 * a standard deviation.
	 * @param avg Original average of data 
	 * @param sd Original standard deviation of data
	 * @return the alfa and beta parameters for a beta distribution
	 */
	public static double[] betaParametersFromNormal(double avg, double sd) {
		final double alfa = (((1 - avg) / (sd*sd)) - (1 / avg)) *avg*avg;
		return new double[] {alfa, alfa * (1 / avg - 1)};
	}
	
	/**
	 * Computes the alfa and beta parameters for a gamma distribution from an average and
	 * a standard deviation.
	 * @param avg Original average of data 
	 * @param sd Original standard deviation of data
	 * @return the alfa and beta parameters for a beta distribution
	 */
	public static double[] gammaParametersFromNormal(double avg, double sd) {
		return new double[] {(avg / sd) * (avg / sd), sd * sd / avg};
	}
	
//	public static void main(String[] args) {
//		double avg = 27;
//		double sd = 7;
//		System.out.println(betaParametersFromNormal(avg, sd)[0] + "\t" + betaParametersFromNormal(avg, sd)[1]);
//	}
	/**
	 * Computes the alfa and beta parameters for a beta distribution from an average,
	 * a mode, and a maximum and minimum values.
	 * Important note: let's the output be [ALFA, BETA]. To use with RandomVariate: 
	 * final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", ALFA, BETA); 
	 * return RandomVariateFactory.getInstance("ScaledVariate", rnd, max - min, min);
	 * @param avg Original average of data 
	 * @param mode Most probable value within the interval (must be different from average)
	 * @param min Minimum value of the generated values
	 * @param max Maximum value of the generated values
	 * @return the alfa and beta parameters for a beta distribution
	 */
	public static double[] betaParametersFromEmpiricData(double avg, double mode, double min, double max) {
		final double alfa = ((avg-min)*(2*mode-min-max))/((mode-avg)*(max-min));
		return new double[] {alfa, ((max-avg)*alfa)/(avg-min)};
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

}
