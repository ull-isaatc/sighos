/**
 * 
 */
package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

/**
 * @author Roberto Mu�oz
 *
 */
public abstract class PeriodicListener implements SimulationListener {
	/**	The interval of time between two consecutive storages. */
	protected double period;
	/** ... */
	protected double referenceTs;
	/** The number of periods contained in the simulation time. */
	protected int nPeriods = 1;
	/** The current period to store information. */
	protected int currentPeriod = 0;
	/** The simulation start timestamp. */
	protected double simStart;
	/** The simulation end timestamp. */
	protected double simEnd;
	/** The simulation. */
	protected Simulation simul;

	/**
	 * Creates a listener.
	 */
	public PeriodicListener() {	}
	
	/**
	 * Creates a listener with period <code>period</code>.
	 * @param period The interval of time between two consecutive storages.
	 */
	public PeriodicListener(double period, double referenceTs) {
		this.period = period;
		this.referenceTs = referenceTs;
	}
	
	/**
	 * Returns the interval of time between two consecutive storages.
	 * @return The interval of time between two consecutive storages.
	 */
	public double getPeriod() {
		return period;
	}

	/**
	 * Sets the interval of time between two consecutive strorages
	 * @param period The period to set.
	 */
	public void setPeriod(double period) {
		this.period = period;
	}
	
	/**
	 * @return the referenceTs
	 */
	public double getReferenceTs() {
		return referenceTs;
	}

	/**
	 * @param referenceTs the referenceTs to set
	 */
	public void setReferenceTs(double referenceTs) {
		this.referenceTs = referenceTs;
	}

	/**
	 * Returns the number of periods contained in the simulation time.
	 * @return The number of periods contained in the simulation time.
	 */
	public int getNumberOfPeriods() {
		return nPeriods;
	}

	/**
	 * Returns the simulation start timestamp.
	 * @return The simulation start timestamp.
	 */
	public double getSimStart() {
		return simStart;
	}

	/**
	 * Returns the simulation end timestamp.
	 * @return The simulation end timestamp.
	 */
	public double getSimEnd() {
		return simEnd;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.SimulationListener#infoEmited(es.ull.isaatc.simulation.info.SimulationObjectInfo)
	 */
	public void infoEmited(SimulationObjectInfo info) {
		while (info.getTs() >= ((currentPeriod + 1) * period) + simStart) {
			currentPeriod++;
			changeCurrentPeriod(info.getTs());
		}
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.SimulationListener#infoEmited(es.ull.isaatc.simulation.info.SimulationStartInfo)
	 */
	public void infoEmited(SimulationStartInfo info) {
		simul = info.getSimulation();
		simStart = simul.getStartTs();
		simEnd = simul.getEndTs();
		
		// get the number of periods contained in the simulation time
		double auxPeriods = ((simEnd - simStart) / period);
		if (auxPeriods > (int)auxPeriods)
			nPeriods = (int)auxPeriods + 1;
		else
			nPeriods = (int)auxPeriods;
		initializeStorages();
	}


	/**
	 * Initialize the structures where the information will be stored. 
	 */
	protected abstract void initializeStorages();
	
	/**
	 * Performs the required operations when the period changes. 
	 */
	protected abstract void changeCurrentPeriod(double ts);

}
