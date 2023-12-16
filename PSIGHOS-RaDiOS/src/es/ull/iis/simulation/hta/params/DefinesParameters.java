package es.ull.iis.simulation.hta.params;


public interface DefinesParameters {
    /**
     * Creates parameters that will be used by a model component. 
     * This method should be invoked from the constructor of the model component, and use
     * {@link #addParameter(Parameter)} to add the parameters to the collection. 
     */
    void createParameters();
}
