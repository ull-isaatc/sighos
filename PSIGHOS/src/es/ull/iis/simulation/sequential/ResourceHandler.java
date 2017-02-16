/**
 * 
 */
package es.ull.iis.simulation.sequential;

import es.ull.iis.simulation.core.Identifiable;
import es.ull.iis.simulation.model.flow.ResourceHandlerFlow;

/**
 * @author Iv�n Castilla
 *
 */
public interface ResourceHandler extends Identifiable {

	ResourceHandlerFlow getModelResHandler();
}
