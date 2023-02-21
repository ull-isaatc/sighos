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
import simkit.random.RandomIntegerSelector;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * @author Iván Castilla
 *
 */
public class VesselCreator extends TimeDrivenElementGenerator {
	private static final int N_VESSELS_PER_SPAWN = 1;
	/** Selector to choose the wares type */
	private final RandomIntegerSelector selector;
	/** Random number generator */
	private final RandomNumber rnd; 

	// TODO: Hacer que sea un único creador de barcos, que decida con cierta lógica de qué tipo, dónde y cuándo se genera el siguiente
	// Esto es así porque no sabemos exactamente cuándo llegan los barcos ni de dónde
	/**
	 * @param model
	 * @param nElem
	 * @param et
	 * @param flow
	 * @param cycle
	 */
	public VesselCreator(Simulation model, ElementType[] vesselSources, InitializerFlow flow, SimulationTimeFunction interarrivalTime, long firstArrivalTime) {
		super(model, N_VESSELS_PER_SPAWN, new SimulationPeriodicCycle(model.getTimeUnit(), firstArrivalTime, interarrivalTime, model.getEndTs()));
		for (int i = 0; i < vesselSources.length; i++) {
			add(new VesselGenerationInfo(vesselSources[i], flow, VesselSource.values()[i]));
		}
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
		return new Vessel(simul, wares, (VesselGenerationInfo) info);
	}
	
	public class VesselGenerationInfo extends StandardElementGenerationInfo {

		protected VesselGenerationInfo(ElementType et, InitializerFlow flow, VesselSource vType) {
			super(et, flow, Vessel.SIZE, vType.getInitialLocation(), vType.getProportion());
		}
		
	}
}
