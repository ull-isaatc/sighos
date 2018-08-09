/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.Arrays;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.Intervention;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class SecondOrderParams {
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
	// Descriptors for other probabilities
	public static final String STR_P_MAN = "P_MAN";
	// Descriptors for RRs
	public static final String STR_RR_PREFIX = "RR_";
	public static final String STR_RR_CHD = STR_RR_PREFIX + Complication.CHD.name(); 
	public static final String STR_RR_NEU = STR_RR_PREFIX + Complication.NEU.name(); 
	public static final String STR_RR_NPH = STR_RR_PREFIX + Complication.NPH.name(); 
	public static final String STR_RR_RET = STR_RR_PREFIX + Complication.RET.name(); 
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
	public static final String STR_INIT_AGE = "INIT_AGE";
	public static final String STR_YEARS_OF_EFFECT = "YEARS_OF_EFFECT";
	// Descriptors for misc parameters
	public static final String STR_DISCOUNT_RATE = "DISCOUNT_RATE";
	
	/** Not incremented relative risk for any intervention */
	public static final double[] NO_RR = {1.0, 1.0};
	
	public abstract class Param {
		protected final String name;
		protected final String description;
		protected final String source;
		
		public Param(String name, String description, String source) {
			this.name = name;
			this.description = description;
			this.source = source;
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
	}
	
	public class DoubleParam extends Param {
		private final double detValue;
		private final RandomVariate rnd;
		private double lastGeneratedValue;

		public DoubleParam(String name, String description, String source, double detValue, RandomVariate rnd) {
			super(name, description, source);
			this.detValue = detValue;
			this.rnd = rnd;
			lastGeneratedValue = Double.NaN;
		}
		
		public DoubleParam(String name, String description, String source, double detValue) {
			this(name, description, source, detValue, RandomVariateFactory.getInstance("ConstantVariate", detValue));
		}
		
		public double getValue() {
			lastGeneratedValue = baseCase ? detValue : rnd.generate();
			return lastGeneratedValue;
		}

		/**
		 * @return the generatedValues
		 */
		public double getLastGeneratedValue() {
			return lastGeneratedValue;
		}
	}
	
	final protected TreeMap<String, DoubleParam> probabilityParams;
	final protected TreeMap<String, Param> otherParams;
	protected Intervention[] interventions = null;
	private boolean canadaValidation = false;
	
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
		otherParams = new TreeMap<>();
	}

	/**
	 * @return the interventions
	 */
	public Intervention[] getInterventions() {
		return interventions;
	}

	/**
	 * @return the interventions
	 */
	public int getNInterventions() {
		return interventions.length;
	}

	public double getProbability(String prob) {
		final DoubleParam param = probabilityParams.get(prob);
		return (param == null) ? Double.NaN : param.getValue(); 
	}

	/**
	 * Returns the relative risk for each intervention. By default, sets the base intervention RR = 1.0.
	 * TODO: Currently only supports two interventions. If further interventions are used, fills all the 
	 * RRs with the parameter. To solve this, the RRParam class should be created.
	 * @param rr Text descriptor of the RR.
	 * @return Returns an array with the relative risk for each intervention
	 */
	public double[] getRR(Complication comp) {
		final DoubleParam param = probabilityParams.get(STR_RR_PREFIX + comp.name());
		if (param == null)
			return null;
		final double[] rrValues = new double[interventions.length];
		rrValues[0] = 1.0;
		for (int i = 1; i < interventions.length; i++) {
			rrValues[i] = param.getValue();
		}
		return rrValues;
	}
	
	public double[] getNoRR() {
		final double[] rrValues = new double[interventions.length];
		Arrays.fill(rrValues, 1.0);
		return rrValues;
	}
	public boolean isBaseCase() {
		return baseCase;
	}
	
	public double getIMR(Complication comp) {
		final DoubleParam param = probabilityParams.get(STR_IMR_PREFIX + comp.name());
		return (param == null) ? Double.NaN : param.getValue(); 
	}

	public double getIMRForDNC() {
		final DoubleParam param = probabilityParams.get(STR_IMR_DNC);
		return (param == null) ? Double.NaN : param.getValue(); 		
	}
	
	public double getInitAge() {
		final DoubleParam param = (DoubleParam) otherParams.get(STR_INIT_AGE);
		return (param == null) ? Double.NaN : param.getValue(); 				
	}
	
	public double getDiscountRate() {
		final DoubleParam param = (DoubleParam) otherParams.get(STR_DISCOUNT_RATE);
		return (param == null) ? 0.0 : param.getValue(); 						
	}
	/**
	 * Returns the duration of the effect for each intervention. By default, sets the base intervention duration to lifetime.
	 * TODO: Currently only supports two interventions. If further interventions are used, fills all the 
	 * durations with the parameter. To solve this, a new param class should be created.
	 * @param unit Simulation time unit
	 * @return Returns an array with the duration of the effect of each intervention, expressed in simulation units.
	 */
	public long [] getDurationOfEffect(TimeUnit unit) {
		final DoubleParam param = (DoubleParam) otherParams.get(STR_YEARS_OF_EFFECT);
		final long[] duration = new long[interventions.length];
		duration[0] = Long.MAX_VALUE;
		for (int i = 1; i < interventions.length; i++) {
			duration[i] = unit.convert(param.getValue(), TimeUnit.YEAR);
		}
		return duration;
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
	
	public String getStrHeader() {
		StringBuilder str = new StringBuilder();
		for (DoubleParam param : probabilityParams.values())
			str.append(param.getName()).append("\t");
		for (Param param : otherParams.values())
			str.append(param.getName()).append("\t");
		return str.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (DoubleParam param : probabilityParams.values())
			str.append(param.getLastGeneratedValue()).append("\t");
		for (Param param : otherParams.values())
			str.append(((DoubleParam)param).getValue()).append("\t");
		return str.toString();
	}
	// For testing only
//	public static void main(String[] args) {
//		int max = 10;
//		SecondOrderParams test1 = new SecondOrderParams(true);
//		for (int i = 0; i < max; i++)
//			System.out.print(test1.getPDNC_CHD() + "\t");
//		System.out.println();
//		test1 = new SecondOrderParams(false);
//		for (int i = 0; i < max; i++)
//			System.out.print(test1.getPDNC_CHD() + "\t");
//		System.out.println();
//		test1 = new SecondOrderParams(false);
//		for (int i = 0; i < max; i++)
//			System.out.print(test1.getPDNC_CHD() + "\t");
//		System.out.println();
//	}
}
