package es.ull.iis.simulation.hta.params;

import java.util.Map;

import es.ull.iis.simulation.hta.Patient;

public interface UsesParameters {
    /**
     * Returns the default name of the specified parameter
     * @param param The parameter
     * @return The default name of the specified parameter
     */
    String getUsedParameterName(ParameterTemplate param);

    /**
     * Sets an alternative name for the specified parameter
     * @param param The parameter
     * @param name The alternative name of the specified parameter
     */
    void setUsedParameterName(ParameterTemplate param, String name);

    /**
     * Adds a parameter to the collection of default parameter names used by a component
     * @param param The parameter to be added
     */
    void addUsedParameter(ParameterTemplate param);

    /**
     * Returns the collection of default parameter names
     * @return the collection of default parameter names
     */
    Map<ParameterTemplate, String> getUsedParameterNames();

    /**
     * Returns the value of the specified parameter for the specified patient if the parameter is used by the component; returns Double.NaN otherwise
     * @param param The parameter
     * @param pat The patient
     * @return The value of the specified parameter for the specified patient if the parameter is used by the component; returns Double.NaN otherwise
     */
    default double getUsedParameterValue(ParameterTemplate param, Patient pat) {
        if (getUsedParameterNames().containsKey(param))
            return pat.getSimulation().getModel().getParameterValue(getUsedParameterName(param), param.getDefaultValue(), pat);
        return Double.NaN;
    }

}
