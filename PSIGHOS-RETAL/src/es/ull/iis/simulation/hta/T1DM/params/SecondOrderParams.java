/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.TreeMap;

import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.params.UtilityParams.CombinationMethod;
import es.ull.iis.simulation.hta.params.SpanishIPCUpdate;
import simkit.random.DiscreteSelectorVariate;
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
	public static final int N_COMPLICATIONS = Complication.values().length;
	// Descriptors for probabilities
	public static final String STR_PROBABILITY_PREFIX = "P_";
	public static final String STR_P_DNC_RET = "P_DNC_RET";
	public static final String STR_P_DNC_NEU = "P_DNC_NEU";
	public static final String STR_P_DNC_NPH = "P_DNC_NPH";
	public static final String STR_P_DNC_CHD = "P_DNC_CHD";
	public static final String STR_P_NEU_CHD = "P_NEU_CHD";
	public static final String STR_P_NEU_LEA = "P_NEU_LEA";
	public static final String STR_P_NEU_NPH = "P_NEU_NPH";
	public static final String STR_P_NPH_CHD = "P_NPH_CHD";
	public static final String STR_P_NPH_ESRD = "P_NPH_ESRD";
	public static final String STR_P_RET_BLI = "P_RET_BLI";
	public static final String STR_P_RET_CHD = "P_RET_CHD";
	public static final String STR_P_HYPO = STR_PROBABILITY_PREFIX + "SEVERE_HYPO";
	public static final String STR_P_DEATH_HYPO = STR_PROBABILITY_PREFIX + "DEATH_SEVERE_HYPO";
	// Descriptors for other probabilities
	public static final String STR_P_MAN = STR_PROBABILITY_PREFIX + "MAN";
	public static final String STR_P_MI = STR_PROBABILITY_PREFIX + CHDComplication.MI;
	public static final String STR_P_ANGINA = STR_PROBABILITY_PREFIX + CHDComplication.ANGINA;
	public static final String STR_P_STROKE = STR_PROBABILITY_PREFIX + CHDComplication.STROKE;
	public static final String STR_P_HF = STR_PROBABILITY_PREFIX + CHDComplication.HF;
	// Descriptors for RRs
	public static final String STR_RR_PREFIX = "RR_";
	public static final String STR_RR_HYPO = STR_RR_PREFIX + "SEVERE_HYPO"; 
	public static final String STR_REF_HBA1C = "AVG REFERENCE HBA1C";

	// Descriptors for Increased Mortality Rates
	public static final String STR_IMR_PREFIX = "IMR_";
	public static final String STR_IMR_DNC = STR_IMR_PREFIX + "DNC";
	public static final String STR_IMR_RET = STR_IMR_PREFIX + Complication.RET.name();
	public static final String STR_IMR_NEU = STR_IMR_PREFIX + Complication.NEU.name();
	public static final String STR_IMR_NPH = STR_IMR_PREFIX + Complication.NPH.name();
	public static final String STR_IMR_CHD = STR_IMR_PREFIX + Complication.CHD.name();
	public static final String STR_IMR_ESRD = STR_IMR_PREFIX + Complication.ESRD.name();
	public static final String STR_IMR_BLI = STR_IMR_PREFIX + Complication.BLI.name();
	public static final String STR_IMR_LEA = STR_IMR_PREFIX + Complication.LEA.name();
	// Descriptors for age parameters
	public static final String STR_AVG_BASELINE_AGE = "AVG BASELINE AGE";
	public static final String STR_AVG_BASELINE_HBA1C = "AVG BASELINE HBA1C";
	// Descriptors for misc parameters
	public static final String STR_DISCOUNT_RATE = "DISCOUNT_RATE";
	// Descriptors for cost parameters
	public static final String STR_COST_PREFIX = "C_";
	public static final String STR_COST_HYPO_EPISODE = STR_COST_PREFIX+ "SEVERE_HYPO_EPISODE";
	public static final String STR_COST_DNC = STR_COST_PREFIX + "DNC"; 
	public static final String STR_COST_CHD = STR_COST_PREFIX + Complication.CHD.name();
	public static final String STR_COST_NEU = STR_COST_PREFIX + Complication.NEU.name();
	public static final String STR_COST_NPH = STR_COST_PREFIX + Complication.NPH.name();
	public static final String STR_COST_RET = STR_COST_PREFIX + Complication.RET.name();
	public static final String STR_COST_LEA = STR_COST_PREFIX + Complication.LEA.name();
	public static final String STR_COST_ESRD = STR_COST_PREFIX + Complication.ESRD.name();
	public static final String STR_COST_BLI = STR_COST_PREFIX + Complication.BLI.name();
	public static final String STR_TRANS_PREFIX = "TC_";
	public static final String STR_TRANS_COST_CHD = STR_TRANS_PREFIX + Complication.CHD.name();
	public static final String STR_TRANS_COST_NEU = STR_TRANS_PREFIX + Complication.NEU.name();
	public static final String STR_TRANS_COST_NPH = STR_TRANS_PREFIX + Complication.NPH.name();
	public static final String STR_TRANS_COST_RET = STR_TRANS_PREFIX + Complication.RET.name();
	public static final String STR_TRANS_COST_LEA = STR_TRANS_PREFIX + Complication.LEA.name();
	public static final String STR_TRANS_COST_ESRD = STR_TRANS_PREFIX + Complication.ESRD.name();
	public static final String STR_TRANS_COST_BLI = STR_TRANS_PREFIX + Complication.BLI.name();

	// Descriptors for utilities
	public static final String STR_UTILITY_PREFIX = "U_";
	public static final String STR_DISUTILITY_PREFIX = "DU_";
	public static final String STR_U_GENERAL_POPULATION = STR_UTILITY_PREFIX + "GENERAL_POP";
	public static final String STR_DU_HYPO_EVENT = STR_DISUTILITY_PREFIX+ "SEVERE_HYPO_EPISODE";
	public static final String STR_DU_DNC = STR_DISUTILITY_PREFIX + "DNC";	
	public static final String STR_DU_CHD = STR_DISUTILITY_PREFIX + Complication.CHD.name();
	public static final String STR_DU_NEU = STR_DISUTILITY_PREFIX + Complication.NEU.name();
	public static final String STR_DU_NPH = STR_DISUTILITY_PREFIX + Complication.NPH.name();
	public static final String STR_DU_RET = STR_DISUTILITY_PREFIX + Complication.RET.name();
	public static final String STR_DU_LEA = STR_DISUTILITY_PREFIX + Complication.LEA.name();
	public static final String STR_DU_ESRD = STR_DISUTILITY_PREFIX + Complication.ESRD.name();
	public static final String STR_DU_BLI = STR_DISUTILITY_PREFIX + Complication.BLI.name();
	
	final protected TreeMap<String, SecondOrderParam> probabilityParams;
	final protected TreeMap<String, SecondOrderCostParam> costParams;
	final protected TreeMap<String, SecondOrderParam> utilParams;
	final protected TreeMap<String, SecondOrderParam> otherParams;
	protected CombinationMethod utilityCombinationMethod = CombinationMethod.ADD;
	private boolean canadaValidation = false;
	private boolean discountZero = false;
	
	/**
	 * True if base case parameters (expected values) should be used. False for second order simulations.
	 */
	private boolean baseCase = true;
	
	/**
	 * @param baseCase True if base case parameters (expected values) should be used. False for second order simulations.
	 */
	protected SecondOrderParams(boolean baseCase) {
		this.baseCase = baseCase;
		probabilityParams = new TreeMap<>();
		costParams = new TreeMap<>();
		otherParams = new TreeMap<>();
		utilParams = new TreeMap<>();
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
	
	public double getProbability(String prob) {
		final SecondOrderParam param = probabilityParams.get(prob);
		return (param == null) ? Double.NaN : param.getValue(); 
	}

	public boolean isBaseCase() {
		return baseCase;
	}
	
	/**
	 * Return the increased mortality risks (IMRs) for each complication. IMRs must be, at least, 1.0. 
	 * @return
	 */
	public double[] getIMRs() {
		final double[] complicationsIMR = new double[N_COMPLICATIONS];
		for (Complication comp : Complication.values()) {
			final SecondOrderParam param = otherParams.get(STR_IMR_PREFIX + comp.name());
			complicationsIMR[comp.ordinal()] = (param == null) ? 1.0 : Math.max(1.0, param.getValue());
		}
		return complicationsIMR; 
	}

	public double getNoComplicationIMR() {
		final SecondOrderParam param = otherParams.get(STR_IMR_DNC);
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
	
	public double[] getAnnualComplicationCost() {
		double[] annualComplicationCosts = new double[N_COMPLICATIONS];
		for (Complication comp : Complication.values()) {
			final SecondOrderParam param = costParams.get(STR_COST_PREFIX + comp.name());
			annualComplicationCosts[comp.ordinal()] = (param == null) ? 0.0 : param.getValue();
		}
		return annualComplicationCosts;
	}
	
	public double[] getTransitionComplicationCost() {
		double[] costs = new double[N_COMPLICATIONS];
		for (Complication comp : Complication.values()) {
			final SecondOrderParam param = costParams.get(STR_TRANS_PREFIX + comp.name());
			costs[comp.ordinal()] = (param == null) ? 0.0 : param.getValue();
		}
		return costs;
	}
	
	public double getAnnualNoComplicationCost() {
		final SecondOrderParam param = costParams.get(STR_COST_DNC);
		return (param == null) ? 0.0 : param.getValue(); 		
	}
	
	/**
	 * Returns true if the CHD complications are detailed; false otherwise
	 * @return True if the CHD complications are detailed; false otherwise
	 */
	public boolean isDetailedCHD() {
		for (CHDComplication comp : CHDComplication.values()) {
			if (otherParams.get(STR_PROBABILITY_PREFIX + comp.name()) == null)
				return false;
		}
		return true;
	}
	
	public DiscreteSelectorVariate getRandomVariateForCHDComplications() {
		if (isDetailedCHD()) {
			final double [] coef = new double[CHDComplication.values().length];
			for (CHDComplication comp : CHDComplication.values()) {
				final SecondOrderParam param = otherParams.get(STR_PROBABILITY_PREFIX + comp.name());
				coef[comp.ordinal()] = param.getValue();
			}
			return (DiscreteSelectorVariate)RandomVariateFactory.getInstance("DiscreteSelectorVariate", coef);
		}
		return null;
	}
	
	public double[] getAnnualCHDComplicationCost() {
		if (isDetailedCHD()) {
			double[] annualComplicationCosts = new double[CHDComplication.values().length];
			for (CHDComplication comp : CHDComplication.values()) {
				final SecondOrderParam param = costParams.get(STR_COST_PREFIX + comp.name());
				annualComplicationCosts[comp.ordinal()] = (param == null) ? 0.0 : param.getValue();
			}
			return annualComplicationCosts;
		}
		return null;
	}
	
	public double[] getTransitionCHDComplicationCost() {
		if (isDetailedCHD()) {
			double[] costs = new double[CHDComplication.values().length];
			for (CHDComplication comp : CHDComplication.values()) {
				final SecondOrderParam param = costParams.get(STR_TRANS_PREFIX + comp.name());
				costs[comp.ordinal()] = (param == null) ? 0.0 : param.getValue();
			}
			return costs;
		}
		return null;
	}
	
	public double getGeneralPopulationUtility() {
		final SecondOrderParam param = utilParams.get(STR_U_GENERAL_POPULATION);
		return (param == null) ? 1.0 : param.getValue(); 		
	}
	
	public double getNoComplicationDisutility() {
		final SecondOrderParam param = utilParams.get(STR_DU_DNC);
		return (param == null) ? 0.0 : param.getValue(); 		
	}
	
	public double getHypoEventDisutility() {
		final SecondOrderParam param = utilParams.get(STR_DU_HYPO_EVENT);
		return (param == null) ? 0.0 : param.getValue(); 		
	}
	
	public double[] getComplicationDisutilities() {
		final double[] du = new double[N_COMPLICATIONS];
		for (Complication comp : Complication.values()) {
			final SecondOrderParam param = utilParams.get(STR_DISUTILITY_PREFIX + comp.name());
			du[comp.ordinal()] = (param == null) ? 0.0 : param.getValue();
		}
		return du;
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
	public abstract ComplicationRR[] getComplicationRRs();
	public abstract ComplicationRR getHypoRR();
	
	
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
