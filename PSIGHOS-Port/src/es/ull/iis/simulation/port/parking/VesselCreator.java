/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.StandardElementGenerationInfo;
import es.ull.iis.simulation.model.TimeDrivenElementGenerator;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.location.Node;
import es.ull.iis.simulation.port.parking.TransshipmentOrder.OperationType;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * @author Iván Castilla
 *
 */
public class VesselCreator extends TimeDrivenElementGenerator {
	private static final int N_VESSELS_PER_SPAWN = 1;
	/** Random number generator */
	private final RandomNumber rnd;
	private int vesselCounter = 0;
	private final ArrayList<PredefinedVessel> predefinedVessels;

	/**
	 * @param model
	 * @param nElem
	 * @param et
	 * @param flow
	 * @param cycle
	 */
	public VesselCreator(Simulation model, InitializerFlow flow, SimulationTimeFunction interarrivalTime, long firstArrivalTime, String fileName) {
		super(model, N_VESSELS_PER_SPAWN, new SimulationPeriodicCycle(model.getTimeUnit(), firstArrivalTime, interarrivalTime, model.getEndTs()));
		predefinedVessels = new ArrayList<>();
		BufferedReader buffer;
		try {
			buffer = new BufferedReader(new FileReader(fileName));
			// Read header;
			String line = buffer.readLine();
			while((line = buffer.readLine()) != null)
				predefinedVessels.add(new PredefinedVessel(line.split(";")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		add(new VesselGenerationInfo(new ElementType(model, "Vessel"), flow, Locations.VESSEL_SRC.getNode()));
		rnd = RandomNumberFactory.getInstance();
	}
	
	@Override
	public EventSource createEventSource(int ind, StandardElementGenerationInfo info) {
		final PredefinedVessel data = predefinedVessels.get((int) (rnd.draw() * predefinedVessels.size()));
		return new Vessel(simul, vesselCounter++, data.getOrders(), (VesselGenerationInfo) info);
	}
	
	public class VesselGenerationInfo extends StandardElementGenerationInfo {

		protected VesselGenerationInfo(ElementType et, InitializerFlow flow, Node initialLocation) {
			super(et, flow, PortParkingModel.VESSEL_SIZE, initialLocation, 1.0);
		}
		
	}
	
	private class PredefinedVessel {
		private final String scaleId;
		private final ArrayList<VesselTransshipmentOrder> orders;
		
		/**
		 * Creates a vessel from the information stored in an array of strings
		 * @param items An array of Strings that contains the scale id in the first position; the load orders and the unload orders. 
		 * Both load and unload orders are in the same order as in the {@link WaresType} class.
		 */
		public PredefinedVessel(String []items) {
			scaleId = items[0];
			orders = new ArrayList<>();
			for (WaresType wares : WaresType.values()) {
				final int index = wares.ordinal();
				// Load order
				double tones = Double.parseDouble(items[index + 1]);
				if (tones > 0.0)
					orders.add(new VesselTransshipmentOrder(OperationType.LOAD, wares, tones));
				tones = Double.parseDouble(items[index + WaresType.values().length + 1]);
				if (tones > 0.0)
					orders.add(new VesselTransshipmentOrder(OperationType.UNLOAD, wares, tones));
			}
		}

		/**
		 * @return the scaleId
		 */
		public String getScaleId() {
			return scaleId;
		}

		/**
		 * @return the orders
		 */
		public ArrayList<VesselTransshipmentOrder> getOrders() {
			return orders;
		}
	}
}
