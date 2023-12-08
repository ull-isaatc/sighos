package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.PrettyPrintable;
import es.ull.iis.simulation.hta.params.calculators.ParameterCalculator;

/**
 * A parameter that defines a value for each patient. It may define a fixed value (constant parameter), a different value per simulation (second-order uncertainty), 
 * and even different value per patient (heterogeneity or first-order uncertainty). These are the parameters that uses {@link SecondOrderParamsRepository}. 
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Parameter implements ParameterCalculator, Named, PrettyPrintable, Comparable<Parameter> {
	/** Common parameters repository */
	private final SecondOrderParamsRepository secParams;
	/** The characteristics that describe this parameter */
	private final ParameterDescription desc;
	/** Short name and identifier of the parameter */
	private final String name;

	/**
	 * Creates a second-order parameter
	 * @param secParams Common parameters repository
	 * @param name Short name and identifier of the parameter
	 */
	public Parameter(final SecondOrderParamsRepository secParams, String name, ParameterDescription desc) {
		this.secParams = secParams;
		this.name = name;
		this.desc = desc;
	}

	/**
	 * Creates a second-order parameter
	 * @param secParams Common parameters repository
	 * @param name Short name and identifier of the parameter
	 */
	public Parameter(final SecondOrderParamsRepository secParams, String name) {
		this.secParams = secParams;
		this.name = name;
		this.desc = new ParameterDescription();
	}
	
	public ParameterDescription getParameterDescription() {
		return desc;
	}

	/**
	 * Returns the short name and identifier of the parameter
	 * @return the short name and identifier of the parameter
	 */
	public String name() {
		return name;
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