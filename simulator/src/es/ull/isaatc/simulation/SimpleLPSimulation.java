/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * A simulation with standard AM configuration which creates a LP per AM.
 * This class is not finished and should not be used.
 * @see StandardAMSimulation
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class SimpleLPSimulation extends StandardAMSimulation {

	public SimpleLPSimulation(String description) {
		super(description);
	}

	@Override
	protected void createLogicalProcesses() {
		logicalProcessList = new LogicalProcess[activityManagerList.size() + 1];
		for (int i = 0; i < activityManagerList.size(); i++)
			logicalProcessList[i] = new LogicalProcess(this, startTs, endTs);
		// Creo el �ltimo proceso l�gico, que servir� de "caj�n de sastre"
		logicalProcessList[activityManagerList.size()] = new LogicalProcess(this, startTs, endTs);
	}

}
