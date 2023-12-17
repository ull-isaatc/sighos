package es.ull.iis.simulation.hta;

import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.params.DefinesParameters;
import es.ull.iis.simulation.hta.params.UsedParameter;
import es.ull.iis.simulation.hta.params.UsesParameters;

public abstract class HTAModelComponent implements NamedAndDescribed, DefinesParameters, UsesParameters {
    /** Short name of the model component */
	private final String name;
	/** Full description of the model component */
	private final String description;
    /** The model this component belongs to */
    protected final HTAModel model;
    /** A collection of names for parameters used by this model component */
    private final Map<UsedParameter, String> usedParameterNames;

    /**
     * Creates a model component
     * @param name Short name of the model component
     * @param description Full description of the model component
     */
    public HTAModelComponent(HTAModel model, String name, String description) {
        this.name = name;
        this.description = description;
        this.model = model;
        this.usedParameterNames = new TreeMap<>();
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

    @Override
    public void createParameters() {        
    }
	
	@Override
	public String toString() {
		return name;
    }

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
}