package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.NamedAndDescribed;
import es.ull.iis.simulation.hta.params.Parameter.ParameterType;
import simkit.random.RandomVariate;

public interface ParameterTemplate {
    /**
     * Returns a default description for a parameter
	 * @return the default description for a parameter
	 */
	public String getDefaultDescription();

	/**
     * Returns a default value for a parameter
	 * @return the default value for a parameter
	 */
	public double getDefaultValue();

	/**
     * Returns the type of the parameter
	 * @return the type of the parameter
	 */
	public ParameterType getType();

    /**
     * Returns the prefix to be used to create names for these parameters
     * @return the prefix to be used to create names for these parameters
     */
    public String getPrefix();

    /**
     * Creates a parameter name for a component with the specified name
     * @param name The original name of the component
     * @return a parameter name for a component with the specified name
     */
    public default String createName(String name) {
        return getPrefix() + name;
    }

    /**
     * Creates a parameter name for the specified component
     * @param component A model component with a name
     * @return a parameter name for the specified component
     */
    public default String createName(Named component) {
        return createName(component.name());
    }

    /**
     * Adds a constant parameter to the model
     * @param model The model to add the parameter to
     * @param name The name of the parameter to be added
     * @param description The desctiption of the parameter to be added
     * @param source The source of the parameter to be added
     * @param year The year of the parameter to be added
     * @param detValue The deterministic value of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public default boolean addToModel(HTAModel model, String name, String description, String source, int year, double detValue) {
        return model.addParameter(new ConstantNatureParameter(model, name, description, source, year, getType(), detValue));
    }

    /**
     * Adds a second order parameter to the model
     * @param model The model to add the parameter to
     * @param name The name of the parameter to be added
     * @param description The desctiption of the parameter to be added
     * @param source The source of the parameter to be added
     * @param year The year of the parameter to be added
     * @param detValue The deterministic value of the parameter to be added
     * @param rnd The (second-order) random variate of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public default boolean addToModel(HTAModel model, String name, String description, String source, int year, double detValue, RandomVariate rnd) {
        return model.addParameter(new SecondOrderNatureParameter(model, name, description, source, year, getType(), detValue, rnd));
    }

    /**
     * Adds a first order parameter to the model
     * @param model The model to add the parameter to
     * @param name The name of the parameter to be added
     * @param description The desctiption of the parameter to be added
     * @param source The source of the parameter to be added
     * @param year The year of the parameter to be added
     * @param rnd The (first-order) random variate of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public default boolean addToModel(HTAModel model, String name, String description, String source, int year, RandomVariate rnd) {
        return model.addParameter(new FirstOrderNatureParameter(model, name, description, source, year, getType(), rnd));
    }

    /**
     * Adds a constant parameter to the model
     * @param model The model to add the parameter to
     * @param name The name of the parameter to be added
     * @param description The desctiption of the parameter to be added
     * @param source The source of the parameter to be added
     * @param detValue The deterministic value of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public default boolean addToModel(HTAModel model, String name, String description, String source, double detValue) {
        return model.addParameter(new ConstantNatureParameter(model, name, description, source, getType(), detValue));
    }

    /**
     * Adds a second order parameter to the model
     * @param model The model to add the parameter to
     * @param name The name of the parameter to be added
     * @param description The desctiption of the parameter to be added
     * @param source The source of the parameter to be added
     * @param detValue The deterministic value of the parameter to be added
     * @param rnd The (second-order) random variate of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public default boolean addToModel(HTAModel model, String name, String description, String source, double detValue, RandomVariate rnd) {
        return model.addParameter(new SecondOrderNatureParameter(model, name, description, source, HTAModel.getStudyYear(), getType(), detValue, rnd));
    }

    /**
     * Adds a first order parameter to the model
     * @param model The model to add the parameter to
     * @param name The name of the parameter to be added
     * @param description The desctiption of the parameter to be added
     * @param source The source of the parameter to be added
     * @param rnd The (first-order) random variate of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public default boolean addToModel(HTAModel model, String name, String description, String source, RandomVariate rnd) {
        return model.addParameter(new FirstOrderNatureParameter(model, name, description, source, HTAModel.getStudyYear(), getType(), rnd));
    }

    /**
     * Adds a constant parameter to the model, whose name is based on the name of the component and the prefix defined in the template
     * @param model The model to add the parameter to
     * @param instance A component with name and description
     * @param source The source of the parameter to be added
     * @param year The year of the parameter to be added
     * @param detValue The deterministic value of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */    
    public default boolean addToModel(HTAModel model, NamedAndDescribed instance, String source, int year, double detValue) {
        return this.addToModel(model, createName(instance), getDefaultDescription() + " " + instance.getDescription(), source, year, detValue);
    }

    /**
     * Adds a second order parameter to the model, whose name is based on the name of the component and the prefix defined in the template
     * @param model The model to add the parameter to
     * @param instance A component with name and description
     * @param source The source of the parameter to be added
     * @param year The year of the parameter to be added
     * @param detValue The deterministic value of the parameter to be added
     * @param rnd The (second-order) random variate of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public default boolean addToModel(HTAModel model, NamedAndDescribed instance, String source, int year, double detValue, RandomVariate rnd) {
        return this.addToModel(model, createName(instance), getDefaultDescription() + " " + instance.getDescription(), source, year, detValue, rnd);
    }

    /**
     * Adds a first order parameter to the model, whose name is based on the name of the component and the prefix defined in the template
     * @param model The model to add the parameter to
     * @param instance A component with name and description
     * @param source The source of the parameter to be added
     * @param year The year of the parameter to be added
     * @param rnd The (first-order) random variate of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public default boolean addToModel(HTAModel model, NamedAndDescribed instance, String source, int year, RandomVariate rnd) {
		return this.addToModel(model, createName(instance), getDefaultDescription() + " " + instance.getDescription(), source, year, rnd);
    }

    /**
     * Adds a constant parameter to the model, whose name is based on the name of the component and the prefix defined in the template
     * @param model The model to add the parameter to
     * @param instance A component with name and description
     * @param source The source of the parameter to be added
     * @param detValue The deterministic value of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */    
    public default boolean addToModel(HTAModel model, NamedAndDescribed instance, String source, double detValue) {
		return this.addToModel(model, createName(instance), getDefaultDescription() + " " + instance.getDescription(), source, detValue);
    }

    /**
     * Adds a second order parameter to the model, whose name is based on the name of the component and the prefix defined in the template
     * @param model The model to add the parameter to
     * @param instance A component with name and description
     * @param source The source of the parameter to be added
     * @param detValue The deterministic value of the parameter to be added
     * @param rnd The (second-order) random variate of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public default boolean addToModel(HTAModel model, NamedAndDescribed instance, String source, double detValue, RandomVariate rnd) {
		return this.addToModel(model, createName(instance), getDefaultDescription() + " " + instance.getDescription(), source, detValue, rnd);
    }

    /**
     * Adds a first order parameter to the model, whose name is based on the name of the component and the prefix defined in the template
     * @param model The model to add the parameter to
     * @param instance A component with name and description
     * @param source The source of the parameter to be added
     * @param rnd The (first-order) random variate of the parameter to be added
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public default boolean addToModel(HTAModel model, NamedAndDescribed instance, String source, RandomVariate rnd) {
		return this.addToModel(model, createName(instance), getDefaultDescription() + " " + instance.getDescription(), source, rnd);
    }

    /**
     * Adds a parameter to the model. Its name must start with the defined prefix and its type must be the one defined for this template
     * @param model The model to add the parameter to
     * @param param A parameter
     * @return <code>true</code> if the parameter was added, <code>false</code> otherwise
     */
    public default boolean addToModel(HTAModel model, Parameter param) {
        if (param.getType() != getType())
            return false;
        // if (!param.name().startsWith(getPrefix()))
        //     return false;
		return model.addParameter(param);
    }
}
