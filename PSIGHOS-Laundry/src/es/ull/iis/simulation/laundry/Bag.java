/**
 * 
 */
package es.ull.iis.simulation.laundry;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.location.Location;

/**
 * @author Iván Castilla
 *
 */
public class Bag extends Element {
	private final ProductsType productType;
	/**
	 * @param simul
	 * @param elementType
	 * @param initialFlow
	 * @param size
	 * @param initLocation
	 */
	public Bag(Simulation simul, ProductsType type, InitializerFlow initialFlow, int size,
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
