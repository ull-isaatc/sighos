/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.NamedAndDescribed;
import simkit.random.RandomVariate;

/**
 * Common cost-like parameters defined within the repository
 * @author Iv�n Castilla Rodr�guez
 *
 */
public enum CostParamDescriptions implements DescribesParameter {
	/** Cost that is applied in a yearly fashion */
	ANNUAL_COST("AC", "Annual cost for", 0.0),
	/** Generic cost. May be different depending on the context */
	COST("C", "Cost for", 0.0),
	/** Assumed to be a one-time cost applied upon diagnosis */
	DIAGNOSIS_COST("C_DIAG", "Diagnosis cost for", 0.0),
	/** Cost that is applied in a yearly fashion to perform the folow-up of some manifestation or disease */
	FOLLOW_UP_COST("C_FOLLOW", "Annual cost of the follow-up for", 0.0),
	/** A cost that is applied just once */
	ONE_TIME_COST("TC", "One-time cost for", 0.0),
	/** Cost that is applied in a yearly fashion to treat some manifestation or disease */
	TREATMENT_COST("C_TREAT", "Annual cost of the treatment for", 0.0),
	/** A unit cost, generally used to calculate more complex costs, and that can be multiplied by a factor of time of any other measure */
	UNIT_COST("C_UNIT", "Unit cost for", 0.0);

	private final String shortPrefix;
	private final String longPrefix;
	private final double defaultValue;

	/**
	 * 
	 */
	private CostParamDescriptions(String shortPrefix, String longPrefix, double defaultValue) {
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
	
	@Override
	public double getValue(SecondOrderParamsRepository secParams, String name, DiseaseProgressionSimulation simul) {
		return secParams.getCostParam(getParameterName(name), defaultValue, simul);
	}

	@Override
	public double getValueIfExists(SecondOrderParamsRepository secParams, String name, DiseaseProgressionSimulation simul) {
		return secParams.getCostParam(getParameterName(name), simul);
	}

	/**
	 * Adds a cost parameter to the repository with only deterministic value. The name and description of the parameter are filled according to the enum item.
	 * @param secParams Common parameters repository
	 * @param instance The item this cost is associated to. Must implement {@link NamedAndDescribed}
	 * @param source The reference from which this parameter was estimated/taken
	 * @param year Year when the cost was originally estimated
	 * @param detValue Deterministic (constant) value of the parameter
	 */
	public void addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed instance, String source, int year, double detValue) {
		this.addParameter(secParams, instance.name(), instance.getDescription(), source, year, detValue);
	}

	/**
	 * Adds a cost parameter to the repository with only deterministic value. The name of the parameter if filled according to the enum item; the description is 
	 * defined ad hoc
	 * @param secParams Common parameters repository
	 * @param instance The item this cost is associated to. Must implement {@link Named}
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param year Year when the cost was originally estimated
	 * @param detValue Deterministic (constant) value of the parameter
	 */
	public void addParameter(SecondOrderParamsRepository secParams, Named instance, String description, String source, int year, double detValue) {
		secParams.addCostParam(new SecondOrderCostParam(secParams, getParameterName(instance.name()), description, source, this, year, detValue));
	}
	
	/**
	 * Adds a cost parameter to the repository with only deterministic value. The name and description of the parameter are filled according to the enum item.
	 * @param secParams Common parameters repository
	 * @param name Name of the parameter to be appended after the predefined prefix
	 * @param description Full description of the parameter to be appended after the predefined prefix
	 * @param source The reference from which this parameter was estimated/taken
	 * @param year Year when the cost was originally estimated
	 * @param detValue Deterministic (constant) value of the parameter
	 */
	public void addParameter(SecondOrderParamsRepository secParams, String name, String description, String source, int year, double detValue) {
		secParams.addCostParam(new SecondOrderCostParam(secParams, getParameterName(name), getParameterDescription(description), source, this, year, detValue));
	}

	/**
	 * Adds a cost parameter to the repository with both deterministic and probabilistic values. The name and description of the parameter are filled according to the enum item.
	 * @param secParams Common parameters repository
	 * @param instance The item this cost is associated to. Must implement {@link NamedAndDescribed}
	 * @param source The reference from which this parameter was estimated/taken
	 * @param year Year when the cost was originally estimated
	 * @param detValue Deterministic (constant) value of the parameter
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 */
	public void addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed instance, String source, int year, double detValue, RandomVariate rnd) {
		this.addParameter(secParams, instance.name(), instance.getDescription(), source, year, detValue, rnd);
	}

	/**
	 * Adds a cost parameter to the repository with both deterministic and probabilistic values. The name of the parameter if filled according to the enum item; the description is 
	 * defined ad hoc
	 * @param secParams Common parameters repository
	 * @param instance The item this cost is associated to. Must implement {@link Named}
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param year Year when the cost was originally estimated
	 * @param detValue Deterministic (constant) value of the parameter
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 */
	public void addParameter(SecondOrderParamsRepository secParams, Named instance, String description, String source, int year, double detValue, RandomVariate rnd) {
		secParams.addCostParam(new SecondOrderCostParam(secParams, getParameterName(instance.name()), description, source, this, year, detValue, rnd));
	}
	
	/**
	 * Adds a cost parameter to the repository with both deterministic and probabilistic values. The name and description of the parameter are filled according to the enum item.
	 * @param secParams Common parameters repository
	 * @param name Name of the parameter to be appended after the predefined prefix
	 * @param description Full description of the parameter to be appended after the predefined prefix
	 * @param source The reference from which this parameter was estimated/taken
	 * @param year Year when the cost was originally estimated
	 * @param detValue Deterministic (constant) value of the parameter
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 */
	public void addParameter(SecondOrderParamsRepository secParams, String name, String description, String source, int year, double detValue, RandomVariate rnd) {
		secParams.addCostParam(new SecondOrderCostParam(secParams, getParameterName(name), getParameterDescription(description), source, this, year, detValue, rnd));
	}
	
}
