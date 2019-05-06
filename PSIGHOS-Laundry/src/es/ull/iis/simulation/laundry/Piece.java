/**
 * 
 */
package es.ull.iis.simulation.laundry;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.location.Location;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Piece extends Element {
	private final ProductsType productType;
	/**
	 * @param simul
	 * @param type
	 * @param initialFlow
	 * @param size
	 * @param initLocation
	 */
	public Piece(Simulation simul, ProductsType type, InitializerFlow initialFlow, int size,
			Location initLocation) {
		super(simul, type.getElementType(), initialFlow, size, initLocation);
		this.productType = type;
	}
	
	/**
	 * @return the productType
	 */
	public ProductsType getProductType() {
		return productType;
	}
}
