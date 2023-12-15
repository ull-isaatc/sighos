package es.ull.iis.simulation.hta.params;

import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.PrettyPrintable;

/**
 * A parameter that defines a value for each patient. It may define a fixed value (constant parameter), a different value per simulation (second-order uncertainty), 
 * and even different value per patient (heterogeneity or first-order uncertainty). These are the parameters that uses {@link SecondOrderParamsRepository}. 
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Parameter implements Named, PrettyPrintable, Comparable<Parameter> {
	/** The different types of parameters */
	public enum ParameterType {
		/** The collection of attributes, i.e., parameters that define specific characteristics for each patient */
		ATTRIBUTE,
		/** The collection of risk parameters */
		RISK,
		/** The collection of cost parameters */
		COST,
		/** The collection of utility parameters */
		UTILITY,
		/** The collection of miscellaneous parameters */
		OTHER;
		final private Map<String, Parameter> parameters = new TreeMap<>();

		/**
		 * Returns the collection of parameters of this type
		 * @return the collection of parameters of this type
		 */
		public Map<String, Parameter> getParameters() {
			return parameters;
		}

		/**
		 * Returns the parameter with the given name, or null if it does not exist
		 * @param name The name of the parameter
		 * @return the parameter with the given name, or null if it does not exist
		 */
		public Parameter getParameter(String name) {
			return parameters.get(name);
		}

		/**
		 * Adds a parameter to the collection, unless a parameter with the same name already exists.
		 * @param param The parameter to be added
		 * @return true if the parameter was added, false otherwise
		 */
		public boolean addParameter(Parameter param) {
			if (parameters.containsKey(param.name()))
				return false;
			parameters.put(param.name(), param);
			return true;
		}
	}
	/** The characteristics that describe this parameter */
	private final ParameterDescription desc;
	/** Short name and identifier of the parameter */
	private final String name;
	/** The type of the parameter */
	private final ParameterType type;

	/**
	 * Creates a parameter
	 * @param name Short name and identifier of the parameter. Must be unique within the simulation.
	 */
	public Parameter(String name, ParameterDescription desc, ParameterType type) {
		this.name = name;
		this.desc = desc;
		this.type = type;
		if (!type.addParameter(this))
			throw new IllegalArgumentException("Parameter " + name + " already exists");
	}

	/**
	 * Creates a parameter
	 * @param name Short name and identifier of the parameter. Must be unique within the simulation.
	 */
	public Parameter(String name, ParameterType type) {
		this(name, new ParameterDescription(), type);
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

	/**
	 * Calculates and returns the value of a parameter for a patient at a specific simulation timestamp
	 * @param pat A patient
	 * @return the value of a parameter for a patient at a specific simulation timestamp
	 */
	public abstract double getValue(Patient pat);
	
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