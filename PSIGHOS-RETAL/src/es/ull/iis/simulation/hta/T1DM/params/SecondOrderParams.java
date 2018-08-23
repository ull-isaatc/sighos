/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.T1DM.ComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.DeathSubmodel;
import es.ull.iis.simulation.hta.T1DM.Named;
import es.ull.iis.simulation.hta.T1DM.T1DMHealthState;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator.CombinationMethod;
import es.ull.iis.simulation.hta.params.SpanishIPCUpdate;
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
public abstract class SecondOrderParams {
	/** Default percentage for cost variations */
	private static final double DEF_PERCENT_COST_VARIATION = 0.2;
	// Descriptors for probabilities
	public static final String STR_PROBABILITY_PREFIX = "P_";
	public static final String STR_NO_COMPLICATIONS = "DNC";
	
	public static final String STR_P_HYPO = STR_PROBABILITY_PREFIX + "SEVERE_HYPO";
	public static final String STR_P_DEATH_HYPO = STR_PROBABILITY_PREFIX + "DEATH_SEVERE_HYPO";
	// Descriptors for other probabilities
	public static final String STR_P_MAN = STR_PROBABILITY_PREFIX + "MAN";
	// Descriptors for RRs
	public static final String STR_RR_PREFIX = "RR_";
	public static final String STR_RR_HYPO = STR_RR_PREFIX + "SEVERE_HYPO"; 
	public static final String STR_REF_HBA1C = "AVG REFERENCE HBA1C";

	// Descriptors for Increased Mortality Rates
	public static final String STR_IMR_PREFIX = "IMR_";
	// Descriptors for age parameters
	public static final String STR_AVG_BASELINE_AGE = "AVG BASELINE AGE";
	public static final String STR_AVG_BASELINE_HBA1C = "AVG BASELINE HBA1C";
	// Descriptors for misc parameters
	public static final String STR_DISCOUNT_RATE = "DISCOUNT_RATE";
	// Descriptors for cost parameters
	public static final String STR_COST_PREFIX = "C_";
	public static final String STR_COST_HYPO_EPISODE = STR_COST_PREFIX+ "SEVERE_HYPO_EPISODE";
	public static final String STR_TRANS_PREFIX = "TC_";

	// Descriptors for utilities
	public static final String STR_UTILITY_PREFIX = "U_";
	public static final String STR_DISUTILITY_PREFIX = "DU_";
	public static final String STR_U_GENERAL_POPULATION = STR_UTILITY_PREFIX + "GENERAL_POP";
	public static final String STR_DU_HYPO_EVENT = STR_DISUTILITY_PREFIX+ "SEVERE_HYPO_EPISODE";
	
	final protected TreeMap<String, SecondOrderParam> probabilityParams;
	final protected TreeMap<String, SecondOrderCostParam> costParams;
	final protected TreeMap<String, SecondOrderParam> utilParams;
	final protected TreeMap<String, SecondOrderParam> otherParams;
	protected CombinationMethod utilityCombinationMethod = CombinationMethod.ADD;
	private boolean canadaValidation = false;
	private boolean discountZero = false;
	final protected RandomNumber rngFirstOrder;
	final protected ArrayList<T1DMHealthState> availableHealthStates;

	
	/**
	 * True if base case parameters (expected values) should be used. False for second order simulations.
	 */
	private boolean baseCase = true;
	final protected int nPatients;
	
	/**
	 * @param baseCase True if base case parameters (expected values) should be used. False for second order simulations.
	 */
	protected SecondOrderParams(boolean baseCase, int nPatients) {
		this.baseCase = baseCase;
		probabilityParams = new TreeMap<>();
		costParams = new TreeMap<>();
		otherParams = new TreeMap<>();
		utilParams = new TreeMap<>();
		this.rngFirstOrder = RandomNumberFactory.getInstance();
		this.nPatients = nPatients;
		this.availableHealthStates = new ArrayList<>();
	}

	public void registerHealthStates(T1DMHealthState[] newStates) {
		for (T1DMHealthState st : newStates) {
			st.setOrder(availableHealthStates.size());
			availableHealthStates.add(st);
		}
	}
	
	/**
	 * @return the available health states
	 */
	public ArrayList<T1DMHealthState> getAvailableHealthStates() {
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
		return getProbability(getProbString(fromState, toState));
	}
	
	public double getProbability(String prob) {
		final SecondOrderParam param = probabilityParams.get(prob);
		return (param == null) ? 0.0 : param.getValue(); 
	}

	/**
	 * Returns the increase mortality rate associated to a health state; 1.0 if no additional risk is associated
	 * @param state Helath state
	 * @return the increase mortality rate associated to a health state; 1.0 if no additional risk is associated
	 */
	public double getIMR(Named state) {
		final SecondOrderParam param = otherParams.get(STR_IMR_PREFIX + state.name());
		return (param == null) ? 1.0 : Math.max(1.0, param.getValue());		
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
		costs[0] = (annualCost == null) ? 0.0 : annualCost.getValue();
		costs[1] = (transCost == null) ? 0.0 : transCost.getValue();
		return costs;
	}
	
	public double getDisutilityForHealthState(Named state) {
		final SecondOrderParam param = utilParams.get(STR_DISUTILITY_PREFIX + state.name());
		return (param == null) ? 0.0 : param.getValue();		
	}
	/**
	 * Returns true if the base case is active; false if the probabilistic analysis is active
	 * @return true if the base case is active; false if the probabilistic analysis is active
	 */
	public boolean isBaseCase() {
		return baseCase;
	}
	
	/**
	 * @return the nPatients
	 */
	public int getnPatients() {
		return nPatients;
	}

	public double getNoComplicationIMR() {
		final SecondOrderParam param = otherParams.get(STR_IMR_PREFIX + STR_NO_COMPLICATIONS);
		return (param == null) ? Double.NaN : param.getValue(); 		
	}
	
	public double getPMan() {
		final SecondOrderParam param = (SecondOrderParam) otherParams.get(STR_P_MAN);
		return (param == null) ? 0.5 : param.getValue(); 				
	}
	
	public double getDiscountRate() {
		if (discountZero)
			return 0.0;
		final SecondOrderParam param = (SecondOrderParam) otherParams.get(STR_DISCOUNT_RATE);
		return (param == null) ? 0.0 : param.getValue(); 						
	}
	
	public double getCostForSevereHypoglycemicEpisode() {
		final SecondOrderParam param = (SecondOrderParam) costParams.get(STR_COST_HYPO_EPISODE);
		return (param == null) ? 0.0 : param.getValue(); 						
	}
	
	public double getAnnualNoComplicationCost() {
		final SecondOrderParam param = costParams.get(STR_COST_PREFIX + STR_NO_COMPLICATIONS);
		return (param == null) ? 0.0 : param.getValue(); 		
	}
	
	public double getGeneralPopulationUtility() {
		final SecondOrderParam param = utilParams.get(STR_U_GENERAL_POPULATION);
		return (param == null) ? 1.0 : param.getValue(); 		
	}
	
	public double getNoComplicationDisutility() {
		final SecondOrderParam param = utilParams.get(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS);
		return (param == null) ? 0.0 : param.getValue(); 		
	}
	
	public double getHypoEventDisutility() {
		final SecondOrderParam param = utilParams.get(STR_DU_HYPO_EVENT);
		return (param == null) ? 0.0 : param.getValue(); 		
	}
	
	public CombinationMethod getUtilityCombinationMethod() {
		return utilityCombinationMethod;
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
	
	/**
	 * True if the modifications for the canadian model should be activated
	 * @return True if the modifications for the canadian model should be activated
	 */
	public boolean isCanadaValidation() {
		return canadaValidation;
	}
	
	/**
	 * Sets this set of second order parameters to activate the Canada model variations
	 */
	public void setCanadaValidation() {
		this.canadaValidation = true;
	}

	public boolean isDiscountZero() {
		return discountZero;
	}

	public void setDiscountZero(boolean discountZero) {
		this.discountZero = discountZero;
	}

	public abstract RandomVariate getBaselineHBA1c();
	public abstract RandomVariate getBaselineAge();
	public abstract RandomVariate getWeeklySensorUsage();
	public abstract ComplicationRR getHypoRR();
	public abstract ComplicationSubmodel[] getComplicationSubmodels();
	public abstract DeathSubmodel getDeathSubmodel();
	public abstract CostCalculator getCostCalculator();
	public abstract UtilityCalculator getUtilityCalculator();
	
	
	public String getProbString(Named from, Named to) {
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
		double alfa = (((1 - avg) / (sd*sd)) - (1 / avg)) *avg*avg;
		return new double[] {alfa, alfa * (1 / avg - 1)};
	}
	
	public static RandomVariate getRandomVariateForCost(double detCost) {
		if (detCost == 0.0) {
			return RandomVariateFactory.getInstance("ConstantVariate", detCost);
		}
		final double costVariance2 = DEF_PERCENT_COST_VARIATION * DEF_PERCENT_COST_VARIATION;
		final double invCostVariance2 = 1 / costVariance2;
		return RandomVariateFactory.getInstance("GammaVariate", invCostVariance2, costVariance2 * detCost);
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
			str.append(param.getValue()).append("\t");
		for (SecondOrderParam param : utilParams.values())
			str.append(param.getValue()).append("\t");
		for (SecondOrderParam param : otherParams.values())
			str.append(param.getValue()).append("\t");
		return str.toString();
	}
	// For testing only
//	public static void main(String[] args) {
//		SecondOrderParams par = new SecondOrderParams(true) {
//		};
//		SecondOrderCostParam cost = par.new SecondOrderCostParam("MI", "MI", "", 2003, 19277.0); 
//		System.out.println(cost.getValue());
//	}
	public class SecondOrderParam {
		private final String name;
		private final String description;
		private final String source;
		private final double detValue;
		private final RandomVariate rnd;
		private double lastGeneratedValue;

		public SecondOrderParam(String name, String description, String source, double detValue, RandomVariate rnd) {
			this.name = name;
			this.description = description;
			this.source = source;
			this.detValue = detValue;
			this.rnd = rnd;
			lastGeneratedValue = Double.NaN;
		}
		
		public SecondOrderParam(String name, String description, String source, double detValue) {
			this(name, description, source, detValue, RandomVariateFactory.getInstance("ConstantVariate", detValue));
		}
		
		public double getValue() {
			lastGeneratedValue = baseCase ? detValue : rnd.generate();
			return lastGeneratedValue;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @return the source
		 */
		public String getSource() {
			return source;
		}

		/**
		 * @return the generatedValues
		 */
		public double getLastGeneratedValue() {
			return lastGeneratedValue;
		}
	}
	
	/**
	 * A special kind of second order parameter that updates the values according to the Spanish IPC.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public class SecondOrderCostParam extends SecondOrderParam {
		/** Year of the cost */
		final private int year;

		public SecondOrderCostParam(String name, String description, String source, int year, double detValue) {
			super(name, description, source, detValue);
			this.year = year;
		}
		
		public SecondOrderCostParam(String name, String description, String source, int year, double detValue, RandomVariate rnd) {
			super(name, description, source, detValue, rnd);
			this.year = year;
		}

		@Override
		public double getValue() {
			return SpanishIPCUpdate.updateCost(super.getValue(), year, BasicConfigParams.STUDY_YEAR);
		}
		/**
		 * @return the year
		 */
		public int getYear() {
			return year;
		}
	}

}
