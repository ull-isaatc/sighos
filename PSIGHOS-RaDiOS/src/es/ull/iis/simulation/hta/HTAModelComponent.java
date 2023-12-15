package es.ull.iis.simulation.hta;

import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.params.DefinesParameters;
import es.ull.iis.simulation.hta.params.Parameter;

public abstract class HTAModelComponent implements NamedAndDescribed, DefinesParameters {
    private final Map<String, Parameter> parameters = new TreeMap<>();
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
        this.name = name;
        this.description = description;
        this.model = model;
        createParameters();
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
    public Map<String, Parameter> getParameters() {
        return parameters;
    }

    @Override
    public Parameter getParameter(String name) {
        return parameters.get(name);
    }

    @Override
    public boolean addParameter(Parameter param) {
        if (parameters.containsKey(param.name()))
            return false;
        parameters.put(param.name(), param);
        return true;
    }

    @Override
    public void createParameters() {        
    }
	
	@Override
	public String toString() {
		return name;
    }

}