/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.builders;

import es.ull.iis.simulation.hta.osdi.OSDiGenericRepository;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
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
	public static Development getDevelopmentInstance(OSDiGenericRepository secParams, String developmentName, Disease disease) {
		final Development develop = new Development(developmentName, OSDiWrapper.DataProperty.HAS_DESCRIPTION.getValue(developmentName, ""), disease);
		return develop;
	}
}
