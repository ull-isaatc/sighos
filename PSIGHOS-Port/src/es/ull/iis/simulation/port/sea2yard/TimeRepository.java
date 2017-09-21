/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.Arrays;

import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TimeRepository {
	private static final RandomNumber rng = RandomNumberFactory.getInstance();
	final private long[] operationTime;
	final private long[] transportTime;
	final private long[][] moveTime;
	final private long currentSeed;
	final private int safetyDistance;

	/**
	 * 
	 */
	public TimeRepository(StowagePlan plan, double pError) {
		this(plan, pError, rng.getSeed());
	}
	/**
	 * 
	 * @param plan The stowage plan that defines the tasks to perform
	 * @param pError Percentage error for each time parameter of the simulation. If T is the constant duration of any activity 
	 * within the simulation, the effective time will be uniformly distributed in the interval (T-T*pError, T+T*pError). 
	 * If pError == 0.0, creates a deterministic simulation
	 * @param seed Random seed used to ensure that the random parameters of the simulation replicate those from a former simulation 
	 */
	public TimeRepository(StowagePlan plan, double pError, long seed) {
		currentSeed = seed;
		rng.setSeed(seed);
		operationTime = new long[plan.getNTasks()];
		transportTime = new long[plan.getNTasks()];
		safetyDistance = plan.getSafetyDistance();
		final int nActualBays = (1 + safetyDistance) * 2 + plan.getVessel().getNBays(); 
		moveTime = new long[plan.getNCranes()][nActualBays];
		if (pError == 0.0) {
			for (int taskId = 0; taskId < plan.getNTasks(); taskId++) {
				operationTime[taskId] = plan.getVessel().getContainerProcessingTime(taskId) * PortModel.T_OPERATION;
			}
			Arrays.fill(transportTime, PortModel.T_TRANSPORT);
			for (int craneId = 0; craneId < plan.getNCranes(); craneId++) {
				Arrays.fill(moveTime[craneId], PortModel.T_MOVE);
			}
		}
		else {
			for (int taskId = 0; taskId < plan.getNTasks(); taskId++) {
				operationTime[taskId] = getTimeWithError(pError, plan.getVessel().getContainerProcessingTime(taskId) * PortModel.T_OPERATION);
				transportTime[taskId] = getTimeWithError(pError, PortModel.T_TRANSPORT);
			}
			for (int craneId = 0; craneId < plan.getNCranes(); craneId++) {
				for (int bayId = 0; bayId < nActualBays; bayId++) {
					moveTime[craneId][bayId] = getTimeWithError(pError, PortModel.T_MOVE);
				}
			}
		}
	}

	/**
	 * Computes a time parameter by adding a uniform error. The error is specified as {@link #pError}
	 * @param constantTime
	 * @return
	 */
	private static synchronized long getTimeWithError(double pError, long constantTime) {
		if (pError == 0) {
			return constantTime;
		}
		else {
			double rnd = (rng.draw() - 0.5) * 2.0;
			return (long)(constantTime * (1 + rnd * pError));
		}
	}
	
	/**
	 * 
	 * @return the operationTime
	 */
	public long getOperationTime(int taskId) {
		return operationTime[taskId];
	}
	
	/**
	 * @return the transportTime
	 */
	public long getTransportTime(int taskId) {
		return transportTime[taskId];
	}
	
	/**
	 * @return the moveTime
	 */
	public long getMoveTime(int craneId, int bayId) {
		return moveTime[craneId][bayId + safetyDistance + 1];
	}

	public long getCurrentSeed() {
		return currentSeed;
	}

}
