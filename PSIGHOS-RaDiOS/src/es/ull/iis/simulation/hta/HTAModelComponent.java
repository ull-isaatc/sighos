package es.ull.iis.simulation.hta;

import java.util.EnumMap;

import es.ull.iis.simulation.hta.params.DefinesParameters;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.StandardParameter;

public abstract class HTAModelComponent implements NamedAndDescribed, DefinesParameters {
	/** The parameter names defined for each standard parameter that can be used by the disease */
	private final EnumMap<StandardParameter, String> alternativeStdParameterNames;
	/** Short name of the model component */
	private final String name;
	/** Full description of the model component */
	private final String description;
    /** The model this component belongs to */
    protected final HTAModel model;

    /**
     * Creates a model component
     * @param name Short name of the model component
     * @param description Full description of the model component
     */
    public HTAModelComponent(HTAModel model, String name, String description) {
		this.alternativeStdParameterNames = new EnumMap<>(StandardParameter.class);
        this.name = name;
        this.description = description;
        this.model = model;
    }

	@Override
	public String name() {
		return name;
	}

	/**
	 * Returns the description of the model component
	 * @return the description of the model component
	 */
	@Override
	public String getDescription() {
		return description;
	}

    /**
     * Returns the model this component belongs to
     * @return the model this component belongs to
     */
    public HTAModel getModel() {
        return model;
    }

	/**
	 * Sets the name of the {@link Parameter} that defines the value of a standard parameter
	 * @param stdParam The standard parameter
	 * @param paramName The name of parameter that defines the value of the standard parameter
	 * @return false if the standard parameter is already defined; true otherwise
	 */
	public boolean setAlternativeStandardParameterName(StandardParameter stdParam, String paramName) {
		// If the standard parameter is already defined, do not add it
		if (alternativeStdParameterNames.containsKey(stdParam))
			return false;
		alternativeStdParameterNames.put(stdParam, paramName);
		return true;
	}

	/**
	 * Returns the value of a standard parameter for a patient. If the parameter is not defined, returns its default value
	 * @param stdParam The type of standard parameter
	 * @param pat A patient
	 * @return the value of a standard parameter for a patient; its default value if not defined
	 */
	public double getStandardParameterValue(StandardParameter stdParam, Patient pat) {
		if (alternativeStdParameterNames.containsKey(stdParam))
			return model.getParameterValue(alternativeStdParameterNames.get(stdParam), pat);
        else
            return model.getParameterValue(stdParam.createName(this), stdParam.getDefaultValue(), pat);
	}

    @Override
    public void createParameters() {        
    }
	
	@Override
	public String toString() {
		return name;
    }

}