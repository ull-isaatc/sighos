/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import es.ull.iis.simulation.hta.osdi.utils.OwlHelper;
import es.ull.iis.simulation.hta.progression.Development;
import es.ull.iis.simulation.hta.progression.Disease;

/**
 * @author Iván Castilla Rodríguez
 * @author David Prieto González
 */
public interface DevelopmentBuilder {

	public static Development getDevelopmentInstance(String developmentName, Disease disease) {
		final Development develop = new Development(developmentName, OwlHelper.getDataPropertyValue(developmentName, OSDiNames.DataProperty.HAS_DESCRIPTION.getDescription(), ""), disease);
		return develop;
	}
}
