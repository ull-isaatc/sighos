package es.ull.iis.simulation.hta.params;

import java.util.Arrays;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.PrettyPrintable;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.calculators.ConstantParameterCalculator;
import es.ull.iis.simulation.hta.params.calculators.ParameterCalculator;
import es.ull.iis.simulation.hta.params.calculators.SecondOrderParameterCalculator;
import es.ull.iis.simulation.hta.params.modifiers.ParameterModifier;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * A parameter that defines a value for each patient. It may define a fixed value (constant parameter), a different value per simulation (second-order uncertainty), 
 * and even different value per patient (heterogeneity or first-order uncertainty). These are the parameters that uses {@link SecondOrderParamsRepository}. 
 * @author Iván Castilla Rodríguez
 *
 */
public class Parameter implements Named, PrettyPrintable, Comparable<Parameter> {
	/** Common parameters repository */
	private final SecondOrderParamsRepository secParams;
	/** Short name and identifier of the parameter */
	private final String name;
	/** Full description of the parameter */
	private final String description;
	/** The reference from which this parameter was estimated/taken */
	private final String source;
	private final ParameterCalculator calc;	
	private final ParameterModifier[] modificationPerIntervention;

	/**
	 * Creates a second-order parameter
	 * @param secParams Common parameters repository
	 * @param name Short name and identifier of the parameter
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 */
	public Parameter(final SecondOrderParamsRepository secParams, String name, String description, String source, ParameterCalculator calc) {
		this.secParams = secParams;
		this.name = name;
		this.description = description;
		this.source = source;
		this.calc = calc;
		this.modificationPerIntervention = new ParameterModifier[secParams.getRegisteredInterventions().length];
		Arrays.fill(this.modificationPerIntervention, null);
	}

	public Parameter(final SecondOrderParamsRepository secParams, String name, String description, String source, double value) {
		this(secParams, name, description, source, new ConstantParameterCalculator(value));
	}

	/**
	 * 
	 */
	public Parameter(final SecondOrderParamsRepository secParams, String name, String description, String source, double detValue, RandomVariate rnd) {
		this(secParams, name, description, source, new SecondOrderParameterCalculator(secParams, detValue, rnd));
	}

	public Parameter(final SecondOrderParamsRepository secParams, String name, String description, String source, double detValue, String rndFunction, Object... params) {
		this(secParams, name, description, source, detValue, RandomVariateFactory.getInstance(rndFunction, params));		
	}
	
	public void addModifier(Intervention intervention, ParameterModifier modification) {
		modificationPerIntervention[intervention.ordinal()] = modification;
	}

	public ParameterCalculator getCalculator() {
		return calc;
	}
		
	/**
	 * Returns the value of the parameter for a specific patient, modified according to the intervention
	 * @param pat A patient
	 * @return The value of the parameter for a specific patient, modified according to the intervention
	 */
	
	public double getValue(Patient pat) {
		double value = calc.getValue(pat);
		if (modificationPerIntervention[pat.getnIntervention()] != null)
			return modificationPerIntervention[pat.getnIntervention()].getModifiedValue(pat, value);
		return value;
	}

	/**
	 * Returns the short name and identifier of the parameter
	 * @return the short name and identifier of the parameter
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns the full description of the parameter
	 * @return the full description of the parameter
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the reference from which this parameter was estimated/taken
	 * @return the reference from which this parameter was estimated/taken
	 */
	public String getSource() {
		return source;
	}

	public SecondOrderParamsRepository getRepository() {
		return secParams;
	}
	
	@Override
	public String prettyPrint(String linePrefix) {
		StringBuilder sb = new StringBuilder(linePrefix).append(name);
		return sb.toString();		
	}

	@Override
	public int compareTo(Parameter o) {
		return name.compareTo(o.name);
	}

}