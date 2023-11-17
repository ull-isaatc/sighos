package es.ull.iis.simulation.hta.params;

import java.util.Arrays;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.PrettyPrintable;
import es.ull.iis.simulation.hta.interventions.Intervention;

/**
 * A parameter that defines a value for each patient. It may define a fixed value (constant parameter), a different value per simulation (second-order uncertainty), 
 * and even different value per patient (heterogeneity or first-order uncertainty). These are the parameters that uses {@link SecondOrderParamsRepository}. 
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Parameter implements PrettyPrintable, Comparable<Parameter> {
	/** Common parameters repository */
	private final SecondOrderParamsRepository secParams;
	/** Short name and identifier of the parameter */
	private final String name;
	/** Full description of the parameter */
	private final String description;
	/** The reference from which this parameter was estimated/taken */
	private final String source;
	private final DescribesParameter type;
	private final ParameterModifier[] modificationPerIntervention;

	/**
	 * Creates a second-order parameter
	 * @param secParams Common parameters repository
	 * @param name Short name and identifier of the parameter
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 */
	public Parameter(final SecondOrderParamsRepository secParams, DescribesParameter type, String name, String description, String source) {
		this.secParams = secParams;
		this.name = name;
		this.description = description;
		this.source = source;
		this.type = type;
		this.modificationPerIntervention = new ParameterModifier[secParams.getRegisteredInterventions().length];
		Arrays.fill(this.modificationPerIntervention, null);
	}
	
	public void addModifier(Intervention intervention, ParameterModifier modification) {
		modificationPerIntervention[intervention.ordinal()] = modification;
	}

	public abstract double calculateValue(Patient pat);
	
	/**
	 * Returns the value of the parameter for a specific patient, modified according to the intervention
	 * @param pat A patient
	 * @return The value of the parameter for a specific patient, modified according to the intervention
	 */
	
	public double getValue(Patient pat) {
		double value = calculateValue(pat);
		if (modificationPerIntervention[pat.getnIntervention()] != null)
			return modificationPerIntervention[pat.getnIntervention()].getModifiedValue(pat, value);
		return value;
	}

	/**
	 * Returns the short name and identifier of the parameter
	 * @return the short name and identifier of the parameter
	 */
	public String getName() {
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

	/**
	 * Returns the type of parameter. Affects its temporal behavior, among other issues
	 * @return the type of parameter
	 */
	public DescribesParameter getType() {
		return type;
	}

	public SecondOrderParamsRepository getRepository() {
		return secParams;
	}
	
	@Override
	public String prettyPrint(String linePrefix) {
		StringBuilder sb = new StringBuilder(linePrefix).append(name);
		sb.append(PrettyPrintable.SEPARATOR).append(type);
		return sb.toString();		
	}

	@Override
	public int compareTo(Parameter o) {
		return name.compareTo(o.name);
	}

}