/**
 * 
 */
package es.ull.isaatc.simulation.hospital.view;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.ElementInfo;
import es.ull.isaatc.simulation.common.info.ResourceInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.info.SimulationStartInfo;
import es.ull.isaatc.simulation.common.info.TimeChangeInfo;
import es.ull.isaatc.simulation.common.inforeceiver.View;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ActionsCounterView extends View {

	final private AtomicInteger elemEvents = new AtomicInteger(0);
	final private AtomicInteger startEv = new AtomicInteger(0);
	final private AtomicInteger endEv = new AtomicInteger(0);
	final private AtomicInteger startActEv = new AtomicInteger(0);
	final private AtomicInteger reqActEv = new AtomicInteger(0);
	final private AtomicInteger endActEv = new AtomicInteger(0);
	final private AtomicInteger resEvents = new AtomicInteger(0);
	
	final private AtomicInteger concurrentEvents = new AtomicInteger(0);
	int maxConcurrentEvents = 0;
	long cpuTime;
	PrintStream out;

	public ActionsCounterView(Simulation simul, PrintStream out) {
		super(simul, "Bench");
		this.out = out;
		addEntrance(SimulationStartInfo.class);
		addEntrance(SimulationEndInfo.class);
		addEntrance(ElementInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(ResourceInfo.class);
		addEntrance(TimeChangeInfo.class);
	}

	public synchronized void infoEmited(SimulationInfo info) {
		// This method assumes that TimeChangeInfo is emmited safely
		if (info instanceof TimeChangeInfo) {
			if (concurrentEvents.get() > maxConcurrentEvents)
				maxConcurrentEvents = concurrentEvents.get();
			concurrentEvents.set(0);			
		}
		if (info instanceof ElementInfo) {
			elemEvents.incrementAndGet();
			concurrentEvents.incrementAndGet();
			if (((ElementInfo) info).getType() == ElementInfo.Type.START)
				startEv.incrementAndGet();
			else if (((ElementInfo) info).getType() == ElementInfo.Type.FINISH)
				endEv.incrementAndGet();
		}
		else if (info instanceof ElementActionInfo) {
			elemEvents.incrementAndGet();
			concurrentEvents.incrementAndGet();
			if (((ElementActionInfo) info).getType() == ElementActionInfo.Type.REQACT)
				reqActEv.incrementAndGet();
			else if (((ElementActionInfo) info).getType() == ElementActionInfo.Type.STAACT)
				startActEv.incrementAndGet();
			else if (((ElementActionInfo) info).getType() == ElementActionInfo.Type.ENDACT)
				endActEv.incrementAndGet();
			
		}
		else if (info instanceof ResourceInfo) {
			resEvents.incrementAndGet();
			concurrentEvents.incrementAndGet();
		}
		else if (info instanceof SimulationStartInfo) {
			cpuTime = ((SimulationStartInfo)info).getCpuTime();
		}
		else if (info instanceof SimulationEndInfo) {
			cpuTime = (((SimulationEndInfo)info).getCpuTime() - cpuTime) / 1000000;
			out.println("T:\t" + cpuTime + " ms\tElem Events:\t" + elemEvents + "\tRes Events:\t" + resEvents + "\nMax. concurrent Events:\t" + maxConcurrentEvents);
			out.println("STA:\t" + startEv + "\tEND:\t" + endEv + "\tREQ:\t" + reqActEv + "\tSAC\t" + startActEv + "\tEAC\t" + endActEv);
		}
	}
}
