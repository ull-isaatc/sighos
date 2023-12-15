package es.ull.iis.simulation.hta.params;

import java.util.Map;

public interface DefinesParameters {
    /**
     * Creates parameters that will be used by a model component. 
     * This method should be invoked from the constructor of the model component, and use
     * {@link #addParameter(Parameter)} to add the parameters to the collection. 
     */
    void createParameters();
    
    /**
     * Returns the collection of parameters created for a model component
     * @return the collection of parameters created for a model component
     */
    Map<String, Parameter> getParameters();

    /**
     * Returns the parameter with the given name, or null if it does not exist
     * @param name The name of the parameter
     * @return the parameter with the given name, or null if it does not exist
     */
    Parameter getParameter(String name);

    /**
     * Adds a parameter to the collection, unless a parameter with the same name already exists.
     * @param param The parameter to be added
     * @return true if the parameter was added, false otherwise
     */
    boolean addParameter(Parameter param);
}
