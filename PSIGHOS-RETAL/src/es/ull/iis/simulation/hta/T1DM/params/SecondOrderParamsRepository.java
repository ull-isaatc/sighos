/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.MainAcuteComplications;
import es.ull.iis.simulation.hta.T1DM.MainChronicComplications;
import es.ull.iis.simulation.hta.T1DM.Named;
import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.DeathSubmodel;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * To add a parameter:
 * 1. Create a constant name
 * 2. Create a method to return the parameter (or ensure that the parameter is added to one of the already accessible repositories, e.g., "probabilityParams")
 * 3. Remember to add the value of the parameter in the corresponding subclasses 
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class SecondOrderParamsRepository {
	// Descriptors for probabilities
	public static final String STR_PROBABILITY_PREFIX = "P_";
	public static final String STR_NO_COMPLICATIONS = "DNC";
	// Descriptors for other probabilities
	public static final String STR_P_MAN = STR_PROBABILITY_PREFIX + "MAN";
	// Descriptors for RRs
	public static final String STR_RR_PREFIX = "RR_";

	// Descriptors for Increased Mortality Rates
	public static final String STR_IMR_PREFIX = "IMR_";
	// Descriptors for misc parameters
	public static final String STR_DISCOUNT_RATE = "DISCOUNT_RATE";
	// Descriptors for cost parameters
	public static final String STR_COST_PREFIX = "C_";
	public static final String STR_TRANS_PREFIX = "TC_";

	// Descriptors for utilities
	public static final String STR_UTILITY_PREFIX = "U_";
	public static final String STR_DISUTILITY_PREFIX = "DU_";
	
	public static final ComplicationRR NO_RR = new StdComplicationRR(1.0);
	
	final protected TreeMap<String, SecondOrderParam> probabilityParams;
	final protected TreeMap<String, SecondOrderCostParam> costParams;
	final protected TreeMap<String, SecondOrderParam> utilParams;
	final protected TreeMap<String, SecondOrderParam> otherParams;
	private boolean discountZero = false;
	final private RandomNumber rngFirstOrder;
	final protected ArrayList<T1DMComorbidity> availableHealthStates;
	final protected TreeSet<MainChronicComplications> complicationRegistered;
	final protected TreeSet<MainAcuteComplications> acuteComplicationRegistered;

	
	/**
	 * True if base case parameters (expected values) should be used. False for second order simulations.
	 */
	protected boolean baseCase = true;
	final protected int nPatients;
	
	/**
	 * Creates a repository of second order parameters. By default, generates the base case values.
	 */
	protected SecondOrderParamsRepository() {
		probabilityParams = new TreeMap<>();
		costParams = new TreeMap<>();
		otherParams = new TreeMap<>();
		utilParams = new TreeMap<>();
		this.rngFirstOrder = RandomNumberFactory.getInstance();
		this.nPatients = BasicConfigParams.N_PATIENTS;
		this.availableHealthStates = new ArrayList<>();
		this.complicationRegistered = new TreeSet<>();
		this.acuteComplicationRegistered = new TreeSet<>();
	}

	public void registerHealthStates(T1DMComorbidity[] newStates) {
		for (T1DMComorbidity st : newStates) {
			st.setOrder(availableHealthStates.size());
			availableHealthStates.add(st);
		}
	}
	
	public void registerComplication(MainChronicComplications comp) {
		complicationRegistered.add(comp);
	}
	
	public void registerComplication(MainAcuteComplications comp) {
		acuteComplicationRegistered.add(comp);
	}
	
	public boolean isRegistered(MainChronicComplications comp) {
		return (complicationRegistered.contains(comp));
	}

	public boolean isRegistered(MainAcuteComplications comp) {
		return (acuteComplicationRegistered.contains(comp));
	}
	
	/**
	 * @return the available health states
	 */
	public ArrayList<T1DMComorbidity> getAvailableHealthStates() {
		return availableHealthStates;
	}

	/**
	 * Return the interventions to be used in the simulation
	 * @return the interventions
	 */
	public abstract T1DMMonitoringIntervention[] getInterventions();

	/**
	 * @return the interventions
	 */
	public abstract int getNInterventions();

	public void addProbParam(SecondOrderParam param) {
		probabilityParams.put(param.getName(), param);
	}
	
	public void addCostParam(SecondOrderCostParam param) {
		costParams.put(param.getName(), param);
	}
	
	public void addUtilParam(SecondOrderParam param) {
		utilParams.put(param.getName(), param);
	}
	
	public void addOtherParam(SecondOrderParam param) {
		otherParams.put(param.getName(), param);
	}
	
	public double getOtherParam(String name) {
		final SecondOrderParam param = otherParams.get(name);
		return (param == null) ? Double.NaN : param.getValue(baseCase); 
	}
	
	public double getCostParam(String name) {
		final SecondOrderParam param = costParams.get(name);
		return (param == null) ? Double.NaN : param.getValue(baseCase); 
	}
	
	/**
	 * Returns the probability from healthy to the specified state; 0.0 if not defined
	 * @param state The destination health state
	 * @return the probability from healthy to the specified state; 0.0 if not defined
	 */
	public double getProbability(Named state) {
		return getProbability(null, state); 
	}

	/**
	 * Returns the probability from a state to another; 0.0 if not defined
	 * @param state The destination health state
	 * @return the probability from a state to another; 0.0 if not defined
	 */
	public double getProbability(Named fromState, Named toState) {
		return getProbParam(getProbString(fromState, toState));
	}
	
	public double getProbParam(String name) {
		final SecondOrderParam param = probabilityParams.get(name);
		return (param == null) ? 0.0 : param.getValue(baseCase); 
	}

	/**
	 * Returns the increase mortality rate associated to a health state; 1.0 if no additional risk is associated
	 * @param state Helath state
	 * @return the increase mortality rate associated to a health state; 1.0 if no additional risk is associated
	 */
	public double getIMR(Named state) {
		final SecondOrderParam param = otherParams.get(STR_IMR_PREFIX + state.name());
		return (param == null) ? 1.0 : Math.max(1.0, param.getValue(baseCase));		
	}
	
	/**
	 * Returns the cost for a health state <annual cost, cost at incidence>
	 * @param state Health state
	 * @return the cost for a health state <annual cost, cost at incidence>
	 */
	public double[] getCostsForHealthState(Named state) {
		final double[] costs = new double[2];
		final SecondOrderParam annualCost = costParams.get(STR_COST_PREFIX + state.name());
		final SecondOrderParam transCost = costParams.get(STR_TRANS_PREFIX + state.name());
		costs[0] = (annualCost == null) ? 0.0 : annualCost.getValue(baseCase);
		costs[1] = (transCost == null) ? 0.0 : transCost.getValue(baseCase);
		return costs;
	}
	
	public double getCostForAcuteComplication(MainAcuteComplications comp) {
		final SecondOrderParam param = costParams.get(STR_COST_PREFIX + comp.name());
		return (param == null) ? 0.0 : param.getValue(baseCase); 						
	}
	
	public double getDisutilityForHealthState(Named state) {
		final SecondOrderParam param = utilParams.get(STR_DISUTILITY_PREFIX + state.name());
		return (param == null) ? 0.0 : param.getValue(baseCase);		
	}
	
	public double getDisutilityForAcuteComplication(MainAcuteComplications comp) {
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
	 * @return the rngFirstOrder
	 */
	public RandomNumber getRngFirstOrder() {
		return rngFirstOrder;
	}

	/**
	 * @return the nPatients
	 */
	public int getnPatients() {
		return nPatients;
	}

	public double getNoComplicationIMR() {
		final SecondOrderParam param = otherParams.get(STR_IMR_PREFIX + STR_NO_COMPLICATIONS);
		return (param == null) ? 1.0 : param.getValue(baseCase); 		
	}
	
	public double getPMan() {
		final SecondOrderParam param = (SecondOrderParam) otherParams.get(STR_P_MAN);
		return (param == null) ? 0.5 : param.getValue(baseCase); 				
	}
	
	public double getDiscountRate() {
		if (discountZero)
			return 0.0;
		final SecondOrderParam param = (SecondOrderParam) otherParams.get(STR_DISCOUNT_RATE);
		return (param == null) ? 0.0 : param.getValue(baseCase); 						
	}
	
	public double getAnnualNoComplicationCost() {
		final SecondOrderParam param = costParams.get(STR_COST_PREFIX + STR_NO_COMPLICATIONS);
		return (param == null) ? 0.0 : param.getValue(baseCase); 		
	}
	
	public double getNoComplicationDisutility() {
		final SecondOrderParam param = utilParams.get(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS);
		return (param == null) ? 0.0 : param.getValue(baseCase); 		
	}
	
	/**
	 * @param baseCase the baseCase to set
	 */
	public void setBaseCase(boolean baseCase) {
		this.baseCase = baseCase;
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
	
	public boolean isDiscountZero() {
		return discountZero;
	}

	public void setDiscountZero(boolean discountZero) {
		this.discountZero = discountZero;
	}

	public abstract RandomVariate getBaselineHBA1c();
	public abstract RandomVariate getBaselineAge();
	public abstract ChronicComplicationSubmodel[] getComplicationSubmodels();
	public abstract AcuteComplicationSubmodel[] getAcuteComplicationSubmodels();
	public abstract DeathSubmodel getDeathSubmodel();
	public abstract CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels);
	public abstract UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels);
	
	
	public static String getProbString(Named from, Named to) {
		final String fromName = (from == null) ? STR_NO_COMPLICATIONS : from.name();
		final String toName = "_" + to.name();
		return STR_PROBABILITY_PREFIX + fromName + toName;
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
	
	public static RandomVariate getRandomVariateForCost(double detCost) {
		if (detCost == 0.0) {
			return RandomVariateFactory.getInstance("ConstantVariate", detCost);
		}
		final double costVariance2 = BasicConfigParams.DEF_SECOND_ORDER_VARIATION.COST * BasicConfigParams.DEF_SECOND_ORDER_VARIATION.COST;
		final double invCostVariance2 = 1 / costVariance2;
		return RandomVariateFactory.getInstance("GammaVariate", invCostVariance2, costVariance2 * detCost);
	}

	public static RandomVariate getRandomVariateForProbability(double detProb) {
		if (detProb == 0.0) {
			return RandomVariateFactory.getInstance("ConstantVariate", detProb);
		}
		return RandomVariateFactory.getInstance("UniformVariate", detProb * (1-BasicConfigParams.DEF_SECOND_ORDER_VARIATION.PROBABILITY), detProb * (1+BasicConfigParams.DEF_SECOND_ORDER_VARIATION.PROBABILITY));
	}
	
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
