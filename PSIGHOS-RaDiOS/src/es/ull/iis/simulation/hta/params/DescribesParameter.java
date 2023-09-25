/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.model.Describable;

/**
 * Defines methods that allow to describe and set common generic {@link SecondOrderParam second order parameters} within a {@link SecondOrderParamsRepository repository}.
 * In general, should be implemented by enum types. 
 * Describes the prefixes used to create specific  names and descriptions of parameters, and encapsulates the access to the collections within the repository to get 
 * parameters. Describes also the default value that can be used when a parameter is not defined within the repository.
 * @author Iván Castilla Rodríguez
 *
 */
public interface DescribesParameter extends Named {
	final static String SHORT_LINK = "_";
	final static String LONG_LINK = " ";

	/** 
	 * Returns a prefix used to create names of parameters. It should be formed by a short string and the {@link #SHORT_LINK}
	 * @return a prefix used to create names of parameters
	 */
	String getShortPrefix();
	
	/** 
	 * Returns a prefix used to create descriptions of parameters. It should be formed by a string and the {@link #LONG_LINK}
	 * @return a prefix used to create descriptions of parameters
	 */
	String getLongPrefix();
	
	/**
	 * Returns the default value to be used for this parameter when it has not being defined within the respository. 
	 * @return the default value to be used for this parameter when it has not being defined within the respository.
	 */
	double getParameterDefaultValue();
	
	/**
	 * Creates the final identifying name for a parameter, that should consist of the short prefix and the individual name of the parameter
	 * @param instance Named instance
	 * @return the final identifying name for a parameter
	 */
	default String getParameterName(Named instance) {
		return getParameterName(instance.name());
	}
	
	/**
	 * Creates the final description for a parameter, that should consist of the long prefix and the specific description of the parameter
	 * @param instance Instance with a specific description
	 * @return the final description for a parameter
	 */
	default String getParameterDescription(Describable instance) {
		return getParameterDescription(instance.getDescription());
	}
	
	/**
	 * Creates the final identifying name for a parameter, that should consist of the short prefix and the specific name of the parameter
	 * @param name Short name of the specific parameter
	 * @return the final identifying name for a parameter
	 */
	default String getParameterName(String name) {
		return getShortPrefix() + name;
	}	

	/**
	 * Creates the final description for a parameter, that should consist of the long prefix and the specific description of the parameter
	 * @param name Short name of the specific parameter
	 * @return the final description for a parameter
	 */
	default String getParameterDescription(String description) {
		return getLongPrefix() + description;
	}
	/**
	 * Returns the value of the parameter if it is registered in the repository; otherwise, should return a predefined default value
	 * @param secParams Parameter repository
	 * @param instance Named instance to be concatenated with the corresponding prefix
	 * @param simul Current simulation
	 * @return the value of the parameter if it is registered in the repository; otherwise, should return a predefined default value
	 */
	default double getValue(SecondOrderParamsRepository secParams, Named instance, DiseaseProgressionSimulation simul) {
		return getValue(secParams, instance.name(), simul);		
	}

	/**
	 * Returns the value of the parameter if it is registered in the repository; otherwise, should return a predefined default value
	 * @param secParams Parameter repository
	 * @param name The name of the parameter to be concatenated with the corresponding prefix 
	 * @param simul Current simulation
	 * @return the value of the parameter if it is registered in the repository; otherwise, should return a predefined default value
	 */
	double getValue(SecondOrderParamsRepository secParams, String name, DiseaseProgressionSimulation simul);
	
	/**
	 * Returns the value of the parameter if it is registered in the repository; otherwise, should return Double.NaN
	 * @param secParams Parameter repository
	 * @param instance Named instance to be concatenated with the corresponding prefix
	 * @param simul Current simulation
	 * @return the value of the parameter if it is registered in the repository; otherwise, should return Double.NaN
	 */
	default double getValueIfExists(SecondOrderParamsRepository secParams, Named instance, DiseaseProgressionSimulation simul) {
		return getValueIfExists(secParams, instance.name(), simul);		
	}
	
	/**
	 * Returns the value of the parameter if it is registered in the repository; otherwise, should return Double.NaN
	 * @param secParams Parameter repository
	 * @param name The name of the parameter to be concatenated with the corresponding prefix 
	 * @param simul Current simulation
	 * @return the value of the parameter if it is registered in the repository; otherwise, should return Double.NaN
	 */
	double getValueIfExists(SecondOrderParamsRepository secParams, String name, DiseaseProgressionSimulation simul);
	
	/**
	 * Creates a specific parameter name that involves a transition, e.g., probability of going from "from" to "to".
	 * @param from Source
	 * @param to Destination
	 * @return a specific parameter name that involves a transition
	 */
	static String getTransitionName(Named from, Named to) {
		return from.name() + "_" + to.name();
	}
	
	/**
	 * Creates a specific parameter description that involves a transition, e.g., probability of going from "from" to "to".
	 * @param from Source
	 * @param to Destination
	 * @return a specific parameter description that involves a transition
	 */
	static String getTransitionDescription(Describable from, Describable to) {
		return from.getDescription() + " to " + to.getDescription();
	}
	
	default String getParameterName(Named from, Named to) {
		return getParameterName(DescribesParameter.getTransitionName(from, to));
	}
}
