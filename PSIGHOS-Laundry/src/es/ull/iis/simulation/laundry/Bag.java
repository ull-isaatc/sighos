/**
 * 
 */
package es.ull.iis.simulation.laundry;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.location.Location;
import es.ull.iis.simulation.model.location.Node;

/**
 * @author Iván Castilla
 *
 */
public class Bag extends Element {
	private Node dryer = null;
	private Node washingStage = null;
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
	 * @return the washingStage
	 */
	public Node getWashingStage() {
		return washingStage;
	}

	/**
	 * @param washingStage the washingStage to set
	 */
	public void setWashingStage(Node washingStage) {
		this.washingStage = washingStage;
	}

	/**
	 * @return the productType
	 */
	public ProductsType getProductType() {
		return productType;
	}

	/**
	 * @return the dryer
	 */
	public Node getDryer() {
		return dryer;
	}
	/**
	 * @param dryer the dryer to set
	 */
	public void setDryer(Node dryer) {
		this.dryer = dryer;
	}

}
