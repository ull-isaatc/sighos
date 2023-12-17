package es.ull.iis.simulation.hta.params;

import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.NamedAndDescribed;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.PrettyPrintable;

/**
 * A parameter that defines a value for each patient. It may define a fixed value (constant parameter), a different value per simulation (second-order uncertainty), 
 * and even different value per patient (heterogeneity or first-order uncertainty). These are the parameters that uses {@link SecondOrderParamsRepository}. 
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Parameter implements NamedAndDescribed, PrettyPrintable, Comparable<Parameter>, UsesParameters {
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
		/** The collection of utility parameters */
		DISUTILITY,
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
	public final static Parameter NO_RR = new ConstantNatureParameter("No RR", "Dummy Relative risk", "", ParameterType.RISK, 1.0);
	/** Short name and identifier of the parameter */
	private final String name;
	/** The type of the parameter */
	private final ParameterType type;
	/** Full description of the parameter */
	private final String description;
	/** The reference from which this parameter was estimated/taken */
	private final String source;
	/** Year when the parameter was originally estimated */
	private final int year;
    /** A collection of names for parameters used by this model component */
    private final Map<UsedParameter, String> usedParameterNames;

	/**
	 * Creates a parameter
	 * @param name Short name and identifier of the parameter. Must be unique within the simulation.
	 */
	public Parameter(String name, String description, String source, int year, ParameterType type) {
		this.name = name;
		this.description = description;
		this.source = source;
		this.year = year;
		this.type = type;
		if (!type.addParameter(this))
			throw new IllegalArgumentException("Parameter " + name + " already exists");
        this.usedParameterNames = new TreeMap<>();
	}

	/**
	 * Creates a parameter
	 * @param name Short name and identifier of the parameter. Must be unique within the simulation.
	 */
	public Parameter(String name, String description, String source, ParameterType type) {
		this(name, description, source, HTAModel.getStudyYear(), type);
	}

	/**
	 * Returns the short name and identifier of the parameter
	 * @return the short name and identifier of the parameter
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns the type of the parameter
	 * @return the type of the parameter
	 */
	public ParameterType getType() {
		return type;
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
	 * Returns the year when the parameter was originally estimated
	 * @return the year when the parameter was originally estimated
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Calculates and returns the value of a parameter for a patient at a specific simulation timestamp
	 * @param pat A patient
	 * @return the value of a parameter for a patient at a specific simulation timestamp
	 */
	public abstract double getValue(Patient pat);

    /**
     * Returns the default name of the specified parameter
     * @param param The parameter
     * @return The default name of the specified parameter
     */
    @Override
    public String getUsedParameterName(UsedParameter param) {
        return usedParameterNames.get(param);
    }

    /**
     * Sets the default name of the specified parameter
     * @param param The parameter
     * @param name The default name of the specified parameter
     */
    @Override
    public void setUsedParameterName(UsedParameter param, String name) {
        usedParameterNames.put(param, name);
    }

    /**
     * Returns the collection of default parameter names
     * @return the collection of default parameter names
     */
    @Override
    public Map<UsedParameter, String> getUsedParameterNames() {
        return usedParameterNames;
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
	
	/**
	 * Creates a string that contains a tab separated list of the parameter names defined in this repository
	 * @return a string that contains a tab separated list of the parameter names defined in this repository
	 */
	public static String getStrHeader() {
		StringBuilder str = new StringBuilder();
		for (ParameterType type : ParameterType.values()) {
			for (Parameter param : type.getParameters().values()) {
				if (param instanceof SecondOrderNatureParameter) {
					str.append(param.name()).append("\t");
				}
			}
		}
		return str.toString();
	}
	
	public static String prettyPrintAll(String linePrefix) {
		StringBuilder str = new StringBuilder();
		for (ParameterType type : ParameterType.values()) {
			for (Parameter param : type.getParameters().values()) {
				str.append(param.prettyPrint(linePrefix)).append("\n");
			}
		}
		return str.toString();
	}
	
	public static String print(int id) {
		StringBuilder str = new StringBuilder();
		for (ParameterType type : ParameterType.values()) {
			for (Parameter param : type.getParameters().values()) {
				if (param instanceof SecondOrderNatureParameter)
					str.append(((SecondOrderNatureParameter)param).getValue(id)).append("\t");
			}
		}
		return str.toString();
	}

}