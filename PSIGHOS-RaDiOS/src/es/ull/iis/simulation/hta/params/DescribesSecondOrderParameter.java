/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.model.Describable;

/**
 * @author masbe
 *
 */
public interface DescribesSecondOrderParameter extends Named {
	final static String SHORT_LINK = "_";
	final static String LONG_LINK = " ";

	String getShortPrefix();
	String getLongPrefix();
	double getParameterDefaultValue();
	default String getParameterName(Named instance) {
		return getParameterName(instance.name());
	}
	default String getParameterDescription(Describable instance) {
		return getParameterDescription(instance.getDescription());
	}
	default String getParameterName(String name) {
		return getShortPrefix() + name;
	}	
	default String getParameterDescription(String description) {
		return getLongPrefix() + description;
	}
	default double getValue(SecondOrderParamsRepository secParams, Named instance, DiseaseProgressionSimulation simul) {
		return getValue(secParams, instance.name(), simul);		
	}
	double getValue(SecondOrderParamsRepository secParams, String name, DiseaseProgressionSimulation simul);
}
