package es.ull.isaatc.simulation.info;

import java.util.HashMap;

import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationListener;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;
import es.ull.isaatc.util.OrderedList;

/**
 * Periodically stores the size of the activity queues.
 * 
 * @author Ivan Castilla
 * @author Roberto Muñoz
 */
public class ActivityQueueListener implements SimulationListener {
    /** The interval of time between two consecutive storages. */
    private double period;

    /** The number of periods contained in the simulation time. */
    private int nPeriods = 1;

    /** The current period to store information. */
    private int currentPeriod = 0;

    /** The simulation start timestamp. */
    private double simStart;

    /** The simulation end timestamp. */
    private double simEnd;

    /** The simulation CPU start time. */
    private long cpuStart;

    /** The simulation CPU end time. */
    private double cpuEnd;
    
    /** The size of the activity queues by period. */
    private HashMap<Integer, int[]> actQueues = new HashMap<Integer, int[]>();

    /** The number of activities performed by period. */
    private HashMap<Integer, int[]> actPerformed = new HashMap<Integer, int[]>();

    /** The number of elements finished by period. */
    private int elemFinish[];

    /** The simulation */
    Simulation simul;

    /**
     * Creates a listener
     */
    public ActivityQueueListener() {

    }

    /**
     * Returns the interval of time between two consecutive storages.
     * 
     * @return The interval of time between two consecutive storages.
     */
    public double getPeriod() {
	return period;
    }

    /**
     * Sets de interval of time between two consecutive storages.
     * 
     * @param period
     *                The interval of time between two consecutive storages.
     */
    public void setPeriod(double period) {
	this.period = period;
    }

    /**
     * Returns the number of periods contained in the simulation time.
     * 
     * @return The number of periods contained in the simulation time.
     */
    public int getNPeriods() {
	return nPeriods;
    }

    /**
     * Returns the size of the activity queues by period.
     * 
     * @return The size of the activity queues by period.
     */
    public HashMap<Integer, int[]> getActQueues() {
	return actQueues;
    }

    /*
     * (non-Javadoc)
     * 
     * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationComponentInfo)
     */
    public void infoEmited(SimulationObjectInfo info) {
	if (info instanceof ElementInfo) {
	    ElementInfo eInfo = (ElementInfo) info;
	    // New period
	    while (eInfo.getTs() >= ((currentPeriod + 1) * period) + simStart) {
		currentPeriod++;
		for (int[] queue : actQueues.values())
		    queue[currentPeriod] = queue[currentPeriod - 1];
		for (int[] queue : actPerformed.values())
		    queue[currentPeriod] = queue[currentPeriod - 1];
	    }
	    if (eInfo.getType() == ElementInfo.Type.REQACT)
		actQueues.get(eInfo.getValue())[currentPeriod]++;
	    else if (eInfo.getType() == ElementInfo.Type.STAACT)
		actQueues.get(eInfo.getValue())[currentPeriod]--;
	    else if (eInfo.getType() == ElementInfo.Type.ENDACT)
		actPerformed.get(eInfo.getValue())[currentPeriod]++;
	    else if (eInfo.getType() == ElementInfo.Type.FINISH)
		elemFinish[currentPeriod]++;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationStartInfo)
     */
    public void infoEmited(SimulationStartInfo info) {
	simul = info.getSimulation();
	simStart = simul.getStartTs();
	simEnd = simul.getEndTs();
	cpuStart = System.currentTimeMillis();
	// Creates the activity queues map
	double auxPeriods = ((simEnd - simStart) / period);
	if (auxPeriods > (int) auxPeriods)
	    nPeriods = (int) auxPeriods + 1;
	else
	    nPeriods = (int) auxPeriods;
	OrderedList<Activity> actList = simul.getActivityList();
	for (int i = 0; i < actList.size(); i++) {
	    actQueues.put(new Integer(actList.get(i).getIdentifier()),
		    new int[nPeriods]);
	    actPerformed.put(new Integer(actList.get(i).getIdentifier()),
		    new int[nPeriods]);
	}
	elemFinish = new int[nPeriods];
    }

    /*
     * (non-Javadoc)
     * 
     * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
     */
    public void infoEmited(SimulationEndInfo info) {
	cpuEnd = System.currentTimeMillis();
	for (int[] queue : actQueues.values()) {
	    for (int cont = currentPeriod + 1; cont < queue.length; cont++)
		queue[cont] = queue[cont - 1];
	}
	for (int[] queue : actPerformed.values()) {
	    for (int cont = currentPeriod + 1; cont < queue.length; cont++)
		queue[cont] = queue[cont - 1];
	}
	System.out.println(toString());
    }

    // Nothing to do
    public void infoEmited(TimeChangeInfo info) {

    }

    @Override
    public String toString() {
	StringBuffer str = new StringBuffer();
	str.append("Simulation Time :\t" + (cpuEnd - cpuStart));
	OrderedList<Activity> actList = simul.getActivityList();
	str.append("\nActivity Queues (PERIOD: " + period + ")\r\n");
	for (int i = 0; i < actList.size(); i++) {
	    str.append("A" + actList.get(i).getIdentifier() + ":");
	    for (int value : actQueues.get(Integer.valueOf(actList.get(i)
		    .getIdentifier())))
		str.append("\t" + value);
	    str.append("\r\n");
	}
	str.append("\nActivities Performed (PERIOD: " + period + ")\r\n");
	for (int i = 0; i < actList.size(); i++) {
	    str.append("A" + actList.get(i).getIdentifier() + ":");
	    for (int value : actPerformed.get(Integer.valueOf(actList.get(i)
		    .getIdentifier())))
		str.append("\t" + value);
	    str.append("\r\n");
	}
	str.append("\nElements Finished (PERIOD: " + period + ")\r\n");
	for (int i = 0; i < elemFinish.length; i++)
	    str.append("\t" + elemFinish[i]);
	str.append("\r\n");
	return str.toString();
    }
}
