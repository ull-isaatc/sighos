package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Simulation;

/**
 * Information related to the start, end and time advance during simulation. Collects the simulation timestamp, and the CPU time for the start and
 * end of the simulation.
 * @author Iv�n Castilla
 *
 */
public class SimulationTimeInfo extends AsynchronousInfo {
	/** The types of information related to simulation time */
	public enum Type implements InfoType {
		START	("SIMULATION STARTS"), 
		END		("SIMULATION ENDS"),
		TICK	("CLOCK ADVANCED");
		
		private final String description;
		
		Type (String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}		
		
	};
	/** The CPU time for START and END events; 0L for TICK events */
	final private long cpuTime;
	/** Type of information */
	final private Type type;

	/**
	 * Notifies the start or end of the simulation, or clock advance
	 * @param model Simulation model
	 * @param type Type of information
	 * @param ts Current simulation timestamp
	 */
	public SimulationTimeInfo(final Simulation model, final Type type, final long ts) {
		super(model, ts);
		this.type = type;
		if (!Type.TICK.equals(type))
			this.cpuTime = System.nanoTime();
		else
			this.cpuTime = 0L;
	}

	public long getCpuTime() {
		return cpuTime;
	}
	
	public Type getType() {
		return type;
	}
	
	public String toString() {
		return  simul.long2SimulationTime(getTs()) + "\t[SIM]\t" + type.getDescription();
	}
}
