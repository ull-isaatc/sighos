/**
 * 
 */
package es.ull.iis.simulation.test;

import java.io.PrintStream;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartInfo;
import es.ull.iis.simulation.inforeceiver.View;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class BenchmarkListener extends View {

	long elemEvents = 0;
	long startEv = 0;
	long endEv = 0;
	long startActEv = 0;
	long reqActEv = 0;
	long endActEv = 0;
	
	long resEvents = 0;
	long concurrentEvents = 0;
	double lastEventTs = -1.0;
	long maxConcurrentEvents = 0;
	long cpuTime;
	PrintStream out;

	public BenchmarkListener(PrintStream out) {
		super("Bench");
		this.out = out;
		addEntrance(SimulationStartInfo.class);
		addEntrance(SimulationEndInfo.class);
		addEntrance(ElementInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(ResourceInfo.class);
	}

	public synchronized void infoEmited(SimulationInfo info) {
		if (info instanceof ElementInfo) {
			elemEvents++;
			if (((ElementInfo) info).getTs() == lastEventTs) {
				concurrentEvents++;
				if (concurrentEvents > maxConcurrentEvents)
					maxConcurrentEvents = concurrentEvents;
			}
			else {
				concurrentEvents = 0;
				lastEventTs = ((ElementInfo) info).getTs();
			}
			if (((ElementInfo) info).getType() == ElementInfo.Type.START)
				startEv++;
			else if (((ElementInfo) info).getType() == ElementInfo.Type.FINISH)
				endEv++;
		}
		else if (info instanceof ElementActionInfo) {
			elemEvents++;
			if (((ElementActionInfo) info).getTs() == lastEventTs) {
				concurrentEvents++;
				if (concurrentEvents > maxConcurrentEvents)
					maxConcurrentEvents = concurrentEvents;
			}
			else {
				concurrentEvents = 0;
				lastEventTs = ((ElementActionInfo) info).getTs();
			}
			if (((ElementActionInfo) info).getType() == ElementActionInfo.Type.REQ)
				reqActEv++;
			else if (((ElementActionInfo) info).getType() == ElementActionInfo.Type.START)
				startActEv++;
			else if (((ElementActionInfo) info).getType() == ElementActionInfo.Type.END)
				endActEv++;
			
		}
		else if (info instanceof ResourceInfo) {
			resEvents++;
			if (((ResourceInfo) info).getTs() == lastEventTs) {
				concurrentEvents++;
				if (concurrentEvents > maxConcurrentEvents)
					maxConcurrentEvents = concurrentEvents;
			}
			else {
				concurrentEvents = 0;
				lastEventTs = ((ResourceInfo) info).getTs();
			}
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
