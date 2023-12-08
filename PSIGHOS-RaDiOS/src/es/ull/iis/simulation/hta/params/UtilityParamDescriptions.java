/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.NamedAndDescribed;
import es.ull.iis.simulation.hta.Patient;
import simkit.random.RandomVariate;

/**
 * @author Iván Castilla
 *
 */
public enum UtilityParamDescriptions implements DescribesParameter {
	BASE_UTILITY("BASE_U", "Base utility for", 1.0) {
		@Override
		public double forceValue(SecondOrderParamsRepository secParams, String name, Patient pat) {
			return getValue(secParams, name, pat);
		}
	},
	DISUTILITY("DU", "Disutility for", 0.0) {
		@Override
		public double forceValue(SecondOrderParamsRepository secParams, String name, Patient pat) {
			double value = getValueIfExists(secParams, name, pat);
			if (!Double.isNaN(value))
				return value;
			value = UTILITY.getValueIfExists(secParams, name, pat);
			if (!Double.isNaN(value)) {
				return BASE_UTILITY.getValue(secParams, secParams.getPopulation(), pat) - value;
			}
			return getParameterDefaultValue();
		}
	},
	ONE_TIME_DISUTILITY("TDU", "One-time disutility for", 0.0) {
		@Override
		public double forceValue(SecondOrderParamsRepository secParams, String name, Patient pat) {
			double value = getValueIfExists(secParams, name, pat);
			if (!Double.isNaN(value))
				return value;
			value = ONE_TIME_UTILITY.getValueIfExists(secParams, name, pat);
			if (!Double.isNaN(value)) {
				return BASE_UTILITY.getValue(secParams, secParams.getPopulation(), pat) - value;
			}
			return getParameterDefaultValue();
		}
	},
	ONE_TIME_UTILITY("TU", "One-time utility for", 1.0) {
		@Override
		public double forceValue(SecondOrderParamsRepository secParams, String name, Patient pat) {
			double value = getValueIfExists(secParams, name, pat);
			if (!Double.isNaN(value))
				return value;
			value = ONE_TIME_DISUTILITY.getValueIfExists(secParams, name, pat);
			if (!Double.isNaN(value)) {
				return BASE_UTILITY.getValue(secParams, secParams.getPopulation(), pat) - value;
			}
			return getParameterDefaultValue();
		}
	},
	UTILITY("U", "Utility for", 1.0) {
		@Override
		public double forceValue(SecondOrderParamsRepository secParams, String name, Patient pat) {
			double value = getValueIfExists(secParams, name, pat);
			if (!Double.isNaN(value))
				return value;
			value = DISUTILITY.getValue(secParams, name, pat);
			if (!Double.isNaN(value)) {
				return BASE_UTILITY.getValue(secParams, secParams.getPopulation(), pat) - value;
			}
			return getParameterDefaultValue();
		}
	};
	
	private final String shortPrefix;
	private final String longPrefix;
	private final double defaultValue;

	/**
	 * 
	 */
	private UtilityParamDescriptions(String shortPrefix, String longPrefix, double defaultValue) {
		this.shortPrefix = shortPrefix + SHORT_LINK;
		this.longPrefix = longPrefix + LONG_LINK;
		this.defaultValue = defaultValue;
	}
	
	@Override
	public String getShortPrefix() {
		return shortPrefix;
	}

	@Override
	public String getLongPrefix() {
		return longPrefix;
	}

	@Override
	public double getParameterDefaultValue() {
		return defaultValue;
	}

	public double forceValue(SecondOrderParamsRepository secParams, Named instance, Patient pat) {
		return forceValue(secParams, instance.name(), pat);
	}
	/**
	 * Forces the return type to utility/disutility, depending on the enum item, i.e. if it does not find the "native" value, looks for the 
	 * complementary one and uses the populations's base utility as a reference to compute the final value.
	 * @param secParams Repository
	 * @param name Name of the parameter
	 * @param simul Current simulation
	 * @return A utility/disutility value, according to the enum type.
	 */
	public abstract double forceValue(SecondOrderParamsRepository secParams, String name, Patient pat);
	
	public static double getDisutilityValue(SecondOrderParamsRepository secParams, String name, Patient pat, boolean oneTime) {
		final UtilityParamDescriptions paramDisutility = oneTime ? UtilityParamDescriptions.ONE_TIME_DISUTILITY : UtilityParamDescriptions.DISUTILITY; 
		final UtilityParamDescriptions paramUtility = oneTime ? UtilityParamDescriptions.ONE_TIME_UTILITY : UtilityParamDescriptions.UTILITY;
		
		// Uses the base disutility for the disease if available 
		double du = paramDisutility.getValueIfExists(secParams, name, pat);
		if (!Double.isNaN(du))
			return du;
		// If the disutility is not defined, looks for a utility
		du = paramUtility.getValueIfExists(secParams, name, pat);
		// If the utility is neither defined, uses 0.0
		if (Double.isNaN(du))
			return 0.0;
		// If it is defined as utility, computes the disutility by using the base utility as a reference
		return secParams.getPopulation().getBaseUtility(pat) - du;
	}
	
	public String addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed instance, String source, double detValue) {
		return this.addParameter(secParams, instance.name(), instance.getDescription(), source, detValue);
	}
	
	public String addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed from, NamedAndDescribed to, String source, double detValue) {
		return this.addParameter(secParams, DescribesParameter.getTransitionName(from, to), DescribesParameter.getTransitionDescription(from, to), source, detValue);
	}
	
	public String addParameter(SecondOrderParamsRepository secParams, String name, String description, String source, double detValue) {
		final ParameterDescription desc = new ParameterDescription(getParameterDescription(description), source);		
		return addParameter(secParams, name, desc, detValue, SecondOrderParamsRepository.ParameterType.UTILITY);
	}
	
	public String addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed instance, String source, double detValue, RandomVariate rnd) {
		return this.addParameter(secParams, instance.name(), instance.getDescription(), source, detValue, rnd);
	}
	
	public String addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed from, NamedAndDescribed to, String source, double detValue, RandomVariate rnd) {
		return this.addParameter(secParams, DescribesParameter.getTransitionName(from, to), DescribesParameter.getTransitionDescription(from, to), source, detValue, rnd);
	}
	
	public String addParameter(SecondOrderParamsRepository secParams, String name, String description, String source, double detValue, RandomVariate rnd) {
		final ParameterDescription desc = new ParameterDescription(getParameterDescription(description), source);		
		return addParameter(secParams, name, desc, detValue, rnd, SecondOrderParamsRepository.ParameterType.UTILITY);
	}
	
	public String addParameter(SecondOrderParamsRepository secParams, Parameter param) {
		return addParameter(secParams, param, SecondOrderParamsRepository.ParameterType.UTILITY);
	}
}
