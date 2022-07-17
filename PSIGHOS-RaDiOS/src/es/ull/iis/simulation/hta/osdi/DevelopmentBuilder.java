/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import es.ull.iis.simulation.hta.osdi.utils.OwlHelper;
import es.ull.iis.simulation.hta.progression.Development;
import es.ull.iis.simulation.hta.progression.Disease;

/**
 * Allows the creation of a {@link Development} based on the information stored in the ontology
 * @author Iván Castilla Rodríguez
 * @author David Prieto González
 */
public interface DevelopmentBuilder {

	/**
	 * Creates a {@link Development} based on the information stored in the ontology
	 * @param developmentName Name of the Development as defined in the ontology
	 * @param disease {@link Disease} this {@link Development} is related to
	 * @return a {@link Development} based on the information stored in the ontology
	 */
	public static Development getDevelopmentInstance(String developmentName, Disease disease) {
		final Development develop = new Development(developmentName, OwlHelper.getDataPropertyValue(developmentName, OSDiNames.DataProperty.HAS_DESCRIPTION.getDescription(), ""), disease);
		return develop;
	}
}
