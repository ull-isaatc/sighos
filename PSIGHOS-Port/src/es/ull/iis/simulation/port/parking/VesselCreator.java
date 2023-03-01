/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.StandardElementGenerationInfo;
import es.ull.iis.simulation.model.TimeDrivenElementGenerator;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.location.Node;
import simkit.random.RandomIntegerSelector;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * @author Iv�n Castilla
 *
 */
public class VesselCreator extends TimeDrivenElementGenerator {
	private static final int N_VESSELS_PER_SPAWN = 1;
	/** Selector to choose the wares type */
	private final RandomIntegerSelector selector;
	/** Random number generator */
	private final RandomNumber rnd;
	private int vesselCounter = 0;

	// TODO: Hacer que sea un �nico creador de barcos, que decida con cierta l�gica de qu� tipo, d�nde y cu�ndo se genera el siguiente
	// Esto es as� porque no sabemos exactamente cu�ndo llegan los barcos ni de d�nde
	/**
	 * @param model
	 * @param nElem
	 * @param et
	 * @param flow
	 * @param cycle
	 */
	public VesselCreator(Simulation model, Node initialLocation, InitializerFlow flow, SimulationTimeFunction interarrivalTime, long firstArrivalTime) {
		super(model, N_VESSELS_PER_SPAWN, new SimulationPeriodicCycle(model.getTimeUnit(), firstArrivalTime, interarrivalTime, model.getEndTs()));
		add(new VesselGenerationInfo(new ElementType(model, "Vessel"), flow, initialLocation));
		final double[] freqs = new double[WaresType.values().length];
		for (int i = 0; i < freqs.length; i++) {
			freqs[i] = WaresType.values()[i].getProportion();
		}
		selector = new RandomIntegerSelector(freqs);
		rnd = RandomNumberFactory.getInstance();
	}
	
	@Override
	public EventSource createEventSource(int ind, StandardElementGenerationInfo info) {
		final WaresType wares = WaresType.values()[selector.generate(rnd.draw())];
		return new Vessel(simul, vesselCounter++, wares, (VesselGenerationInfo) info);
	}
	
	public class VesselGenerationInfo extends StandardElementGenerationInfo {

		protected VesselGenerationInfo(ElementType et, InitializerFlow flow, Node initialLocation) {
			super(et, flow, PortParkingModel.VESSEL_SIZE, initialLocation, 1.0);
		}
		
	}
}
