package es.ull.iis.simulation.hta;

import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.params.DefinesParameters;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.ParameterTemplate;
import es.ull.iis.simulation.hta.params.UsesParameters;
import simkit.random.RandomVariate;

public abstract class HTAModelComponent implements NamedAndDescribed, DefinesParameters, UsesParameters {
    /** Short name of the model component */
	private final String name;
	/** Full description of the model component */
	private final String description;
    /** The model this component belongs to */
    protected final HTAModel model;
    /** A collection of names for parameters used by this model component */
    private final Map<ParameterTemplate, String> usedParameterNames;

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

    @Override
    public String getUsedParameterName(ParameterTemplate param) {
        return usedParameterNames.get(param);
    }

    @Override
    public void setUsedParameterName(ParameterTemplate param, String name) {
        usedParameterNames.put(param, name);
    }

    @Override
    public Map<ParameterTemplate, String> getUsedParameterNames() {
        return usedParameterNames;
    }

    @Override
    public void addUsedParameter(ParameterTemplate param) {
        setUsedParameterName(param, param.createName(this));
    }

    /**
     * Adds a constant parameter to this model component
     * @param template A description of the parameter, that should be one of the parameters in {@link #getUsedParameterName(ParameterTemplate)}
     * @param description The desctiption of the parameter to be added
     * @param source The source of the parameter to be added
     * @param year The year of the parameter to be added
     * @param detValue The deterministic value of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public boolean addParameter(ParameterTemplate template, String description, String source, int year, double detValue) {
        if (usedParameterNames.containsKey(template))
            return template.addParameter(model, getUsedParameterName(template), description, source, year, detValue);
        return false;
    }

    /**
     * Adds a second order parameter to this model component
     * @param template A description of the parameter, that should be one of the parameters in {@link #getUsedParameterName(ParameterTemplate)}
     * @param description The desctiption of the parameter to be added
     * @param source The source of the parameter to be added
     * @param year The year of the parameter to be added
     * @param detValue The deterministic value of the parameter to be added
     * @param rnd The (second-order) random variate of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public boolean addParameter(ParameterTemplate template, String description, String source, int year, double detValue, RandomVariate rnd) {
        if (usedParameterNames.containsKey(template))
            return template.addParameter(model, getUsedParameterName(template), description, source, year, detValue, rnd);
        return false;
    }

    /**
     * Adds a first order parameter to this model component
     * @param template A description of the parameter, that should be one of the parameters in {@link #getUsedParameterName(ParameterTemplate)}
     * @param description The desctiption of the parameter to be added
     * @param source The source of the parameter to be added
     * @param year The year of the parameter to be added
     * @param rnd The (first-order) random variate of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public boolean addParameter(ParameterTemplate template, String description, String source, int year, RandomVariate rnd) {
        if (usedParameterNames.containsKey(template))
            return template.addParameter(model, getUsedParameterName(template), description, source, year, rnd);
        return false;
    }

    /**
     * Adds a constant parameter to this model component
     * @param template A description of the parameter, that should be one of the parameters in {@link #getUsedParameterName(ParameterTemplate)}
     * @param description The desctiption of the parameter to be added
     * @param source The source of the parameter to be added
     * @param detValue The deterministic value of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public boolean addParameter(ParameterTemplate template, String description, String source, double detValue) {
        if (usedParameterNames.containsKey(template))
            return template.addParameter(model, getUsedParameterName(template), description, source, detValue);
        return false;
    }

    /**
     * Adds a second order parameter to this model component
     * @param template A description of the parameter, that should be one of the parameters in {@link #getUsedParameterName(ParameterTemplate)}
     * @param description The desctiption of the parameter to be added
     * @param source The source of the parameter to be added
     * @param detValue The deterministic value of the parameter to be added
     * @param rnd The (second-order) random variate of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public boolean addParameter(ParameterTemplate template, String description, String source, double detValue, RandomVariate rnd) {
        if (usedParameterNames.containsKey(template))
            return template.addParameter(model, getUsedParameterName(template), description, source, detValue, rnd);
        return false;
    }

    /**
     * Adds a first order parameter to this model component
     * @param template A description of the parameter, that should be one of the parameters in {@link #getUsedParameterName(ParameterTemplate)}
     * @param description The desctiption of the parameter to be added
     * @param source The source of the parameter to be added
     * @param rnd The (first-order) random variate of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public boolean addParameter(ParameterTemplate template, String description, String source, RandomVariate rnd) {
        if (usedParameterNames.containsKey(template))
            return template.addParameter(model, getUsedParameterName(template), description, source, rnd);
        return false;
    }

    /**
     * Adds a parameter to this model component
     * @param template A description of the parameter, that should be one of the parameters in {@link #getUsedParameterName(ParameterTemplate)}
     * @param param A parameter whose name will replace the default name for the template
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public boolean addParameter(ParameterTemplate template, Parameter param) {
        setUsedParameterName(template, param.name());
        return template.addParameter(model, param);
    }

}