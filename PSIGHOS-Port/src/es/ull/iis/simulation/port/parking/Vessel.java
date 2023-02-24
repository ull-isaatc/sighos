/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.port.parking.VesselCreator.VesselGenerationInfo;

/**
 * @author Iván Castilla
 *
 */
public class Vessel extends Element {
	public static final int SIZE = 1;
	private final WaresType wares;
	private final double initLoad;
	private double currentLoad;
	/** The amount of load not yet assigned to the vessel */
	private double notAssignedLoad;

	/**
	 * @param simul
	 * @param elementType
	 * @param initialFlow
	 */
	public Vessel(Simulation simul, WaresType wares, VesselGenerationInfo info) {
		super(simul, "VESSEL", info);
		this.wares = wares;
		this.initLoad = wares.getTypicalVesselLoad().generate();
		this.currentLoad = initLoad;
	}

	public Vessel(Simulation simul, WaresType wares, ElementType elementType, InitializerFlow initialFlow, VesselSource source) {
		super(simul, "VESSEL", elementType, initialFlow, SIZE, source.getInitialLocation());
		this.wares = wares;
		this.initLoad = wares.getTypicalVesselLoad().generate();
		this.currentLoad = initLoad;
	}
	
	public WaresType getWares() {
		return wares;
	}

	public double getInitLoad() {
		return initLoad;
	}

	public double unload(double quantity) {
		final double actual = (quantity > currentLoad) ? currentLoad : quantity;
		currentLoad -= actual;
		return actual;
	}
	
	public boolean isEmpty() {
		return (currentLoad < 0.001);
	}
}
