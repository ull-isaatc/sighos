package es.ull.iis.simulation.hta.params;

import java.util.Map;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.Parameter.ParameterType;

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
     * Registers a parameter as a default parameter used by a component
     * @param param The parameter to be added
     */
    void registerUsedParameter(ParameterTemplate param);

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

    /**
     * Returns the value or a utility or disutility parameter. Uses a pair of utility and disutility parameters. If the first one is defined, returns its value;
     * otherwise, converts the second one into the first one by using the population base utility. 
     * @param firstUtilityOption An utility or a disutility parameter
     * @param secondUtilityOption Another parameter that must be a utility if firstUtilityOption is a disutility and viceversa
     * @param pat A patient
     * @return the value or a utility or disutility parameter.
     */
    default double forceUtilityParameterValue(ParameterTemplate firstUtilityOption, ParameterTemplate secondUtilityOption, Patient pat) {
        // Checks whether the parameters are defined as used by the component
        if (!getUsedParameterNames().containsKey(firstUtilityOption))
            return Double.NaN;
        if (!getUsedParameterNames().containsKey(secondUtilityOption))
            return Double.NaN;
        // Checks that a correct combination of parameters is used
        if (firstUtilityOption.getType() == ParameterType.UTILITY)
            if (secondUtilityOption.getType() != ParameterType.DISUTILITY)
                throw new IllegalArgumentException("If first option is a utility parameter, second option must be a disutility parameter");
        else if (firstUtilityOption.getType() == ParameterType.DISUTILITY)
            if (secondUtilityOption.getType() != ParameterType.UTILITY)
                throw new IllegalArgumentException("If first option is a disutility parameter, second option must be a utility parameter");
        else
            throw new IllegalArgumentException("First option must be a utility or disutility parameter");
        // Returns the value of the first parameter if it is defined; otherwise, returns the value of the second parameter
		double value = pat.getSimulation().getModel().getParameterValue(getUsedParameterName(firstUtilityOption), pat);
		if (Double.isNaN(value)) {
			value = pat.getSimulation().getModel().getParameterValue(getUsedParameterName(secondUtilityOption), pat);
			if (Double.isNaN(value))
				value = firstUtilityOption.getDefaultValue();
			else {
				value = pat.getSimulation().getModel().getPopulation().getBaseUtility(pat) - value;
			}
		}
		return value;
    }
}
