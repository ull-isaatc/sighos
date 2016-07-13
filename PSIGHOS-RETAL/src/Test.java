import es.ull.iis.simulation.core.ElementType;
import es.ull.iis.simulation.core.Resource;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.TimeDrivenActivity;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.SingleFlow;
import es.ull.iis.simulation.factory.SimulationFactory;
import es.ull.iis.simulation.factory.SimulationObjectFactory;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.retal.CalibratedOphtalmologicPatientData;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import es.ull.iis.function.TimeFunctionFactory;

/**
 * 
 */

class SimulTest {
	protected SimulationObjectFactory factory;
	public final static TimeStamp SIMSTART = TimeStamp.getZero();
	public final static TimeStamp SIMEND = TimeStamp.getDay();
	public final static TimeUnit SIMUNIT = TimeUnit.MINUTE; 
	public final static TimeStamp RESAVAILABLE = new TimeStamp(TimeUnit.HOUR, 7);
	public final static TimeStamp RESSTART = new TimeStamp(TimeUnit.HOUR, 8);
	public final static TimeStamp RESPERIOD = TimeStamp.getDay();
	public final static TimeStamp GENSTART = TimeStamp.getZero();
	public final static TimeStamp GENPERIOD = TimeStamp.getDay();
	
	SimulTest() {
		factory = SimulationFactory.getInstance(SimulationType.SEQUENTIAL, 1, "test", SIMUNIT, SIMSTART, SIMEND);
		ResourceType rt = factory.getResourceTypeInstance("Médico");
		Resource res = factory.getResourceInstance("Médico 1");
		res.addTimeTableEntry(new SimulationPeriodicCycle(SIMUNIT, RESSTART, new SimulationTimeFunction(SIMUNIT, "ConstantVariate", RESPERIOD), 0), RESAVAILABLE, rt);

        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt}, new int[] {1});
		TimeDrivenActivity act = null;
		act = factory.getTimeDrivenActivityInstance("Consulta");
    	act.addWorkGroup(new SimulationTimeFunction(SIMUNIT, "ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 10)), wg);
		
        SingleFlow root = (SingleFlow)factory.getFlowInstance("SingleFlow", act);
        
        ElementType et = factory.getElementTypeInstance("Paciente");
        et.putVar("coste", 0);
        
        factory.getTimeDrivenGeneratorInstance(
        		factory.getElementCreatorInstance(TimeFunctionFactory.getInstance("ConstantVariate", 10), et, root), 
        		new SimulationPeriodicCycle(SIMUNIT, GENSTART, new SimulationTimeFunction(SIMUNIT, "ConstantVariate", GENPERIOD), 0));
        
        factory.getSimulation().addInfoReceiver(new StdInfoView(factory.getSimulation()));
	}
	
	void run() {
		factory.getSimulation().run();
	}
}

class CalibrationTest {
	private final static int NPATIENTS = 8;
	private final static int arrayOfTimes[][] = {
		{20, Integer.MAX_VALUE, 21}, // Should take next EARM
		{20, 19, 31}, 
		{25, 26, Integer.MAX_VALUE},
		{40, Integer.MAX_VALUE, Integer.MAX_VALUE},
		{25, Integer.MAX_VALUE, Integer.MAX_VALUE},
		{40, Integer.MAX_VALUE, Integer.MAX_VALUE}, // Should work fine
		{20, Integer.MAX_VALUE, 10}, // Should work fine
		{20, 10, Integer.MAX_VALUE} // Should work fine
	};
	
	private final static int arrayOfTimes2[][] = {
			{20, 21, Integer.MAX_VALUE}, // Should take next EARM
			{20, 31, 19}, 
			{25, Integer.MAX_VALUE, 26},
			{40, Integer.MAX_VALUE, Integer.MAX_VALUE},
			{25, Integer.MAX_VALUE, Integer.MAX_VALUE},
			{40, Integer.MAX_VALUE, Integer.MAX_VALUE}, // Should work fine
			{20, 10, Integer.MAX_VALUE}, // Should work fine
			{20, Integer.MAX_VALUE, 10} // Should work fine
		};
		
	private final static int arrayOfTimes3[][] = {
			{10, Integer.MAX_VALUE, Integer.MAX_VALUE}, // Should work fine
			{20, 10, Integer.MAX_VALUE}, // Should work fine
			{20, Integer.MAX_VALUE, 10}, // Should work fine
			{20, 21, Integer.MAX_VALUE}, // Should take next EARM
			{20, 31, 19}, 
			{25, Integer.MAX_VALUE, 21},
			{40, Integer.MAX_VALUE, Integer.MAX_VALUE},
			{40, Integer.MAX_VALUE, Integer.MAX_VALUE}
		};
	
	protected static void testThis() {
		int countD = 0;
		int countE = 0;
		int countA = 0;
		
		final CalibratedOphtalmologicPatientData []calibratedData = new CalibratedOphtalmologicPatientData[NPATIENTS];
		final LinkedList<Integer> timesToAMD = new LinkedList<Integer>();
		final LinkedList<Integer> timesToEARM = new LinkedList<Integer>();
		for (int i = 0; i < NPATIENTS; i++) {
			final int timeToDeath = arrayOfTimes[countD++][0];
			int timeToAMD;
			int timeToEARM;
			// If there are no stored values in the queue, generate a new one
			if (timesToAMD.isEmpty()) {
				timeToAMD = arrayOfTimes[countA++][2];
			}
			// If there are stored values in the queue, I try with them in the first place
			else {
				final Iterator<Integer> iter = timesToAMD.iterator();
				do {
					timeToAMD = iter.next();
					if (timeToAMD < timeToDeath)
						iter.remove();
				} while (iter.hasNext() && timeToAMD >= timeToDeath);
				// If no valid event is found, generate a new one
				if (timeToAMD >= timeToDeath)
					timeToAMD = arrayOfTimes[countA++][2];
			}
			// Generate new times to event until we get a valid one
			while (timeToAMD != Integer.MAX_VALUE && timeToAMD >= timeToDeath) {
				timesToAMD.push(timeToAMD);
				timeToAMD = arrayOfTimes[countA++][2];
			}
			
			// If we obtained a valid time to AMD, we don't need time to EARM
			if (timeToAMD < timeToDeath) {
				timeToEARM = arrayOfTimes[countE++][1];
				// Generate new times to event until we get a valid one
				while (timeToEARM != Integer.MAX_VALUE) {
					timesToEARM.push(timeToEARM);
					timeToEARM = arrayOfTimes[countE++][1];
				}
			}
			else {
				// If there are no stored values in the queue, generate a new one
				if (timesToEARM.isEmpty()) {
					timeToEARM = arrayOfTimes[countE++][1];
				}
				// If there are stored values in the queue, I try with them in the first place
				else {
					final Iterator<Integer> iter = timesToEARM.iterator();
					do {
						timeToEARM = iter.next();
						if (timeToEARM < timeToDeath)
							iter.remove();
					} while (iter.hasNext() && timeToEARM >= timeToDeath);
					// If no valid event is found, generate a new one
					if (timeToEARM >= timeToDeath)
						timeToEARM = arrayOfTimes[countE++][1];
				}
				// Generate new times to event until we get a valid one
				while (timeToEARM != Integer.MAX_VALUE && timeToEARM >= timeToDeath) {
					timesToEARM.push(timeToEARM);
					timeToEARM = arrayOfTimes[countE++][1];
				}
			}
			calibratedData[i] = new CalibratedOphtalmologicPatientData(40, 1, timeToDeath, (timeToEARM == Integer.MAX_VALUE) ? Long.MAX_VALUE : timeToEARM, (timeToAMD == Integer.MAX_VALUE) ? Long.MAX_VALUE : timeToAMD);
			System.out.println((calibratedData[i].isCoherent() ? "PASS: " : "FAIL: ") + calibratedData[i]);
		}
		
//		for (int i = 0; i < NPATIENTS; i++) {
//			final int timeToDeath = arrayOfTimes[countD++][0]; 
//			int timeToAMD = timesToAMD.isEmpty() ? arrayOfTimes[countA++][2] : timesToAMD.pollLast();
//			int timeToEARM = timesToEARM.isEmpty() ? arrayOfTimes[countE++][1] : timesToEARM.pollLast();
//			// If both EARM and AMD generate a valid event, then the EARM event is "delayed" for a future patient
//			if (timeToEARM != Integer.MAX_VALUE && timeToAMD != Integer.MAX_VALUE) {
//				if (timeToAMD < timeToDeath) {
//					while (timeToEARM != Integer.MAX_VALUE) {
//						timesToEARM.push(timeToEARM);
//						timeToEARM = arrayOfTimes[countE++][1];
//					}
//				}
//				else if (timeToEARM < timeToDeath) {
//					while (timeToAMD != Integer.MAX_VALUE) {
//						timesToAMD.push(timeToAMD);
//						timeToAMD = arrayOfTimes[countA++][2];
//					}						
//				}
//				else {
//					// First try to generate a coherent time to AMD, while saving the previously generated values for future uses 
//					do {							
//						timesToAMD.push(timeToAMD);
//						timeToAMD = arrayOfTimes[countA++][2];
//					} while ((timeToAMD != Integer.MAX_VALUE) && (timeToAMD >= timeToDeath));
//					// If we don't need to care anymore for AMD, let's proceed with EARM
//					if (timeToAMD == Integer.MAX_VALUE) {
//						// Try to generate a coherent time to EARM, while saving the previously generated values for future uses 
//						do {
//							timesToEARM.push(timeToEARM);
//							timeToEARM = arrayOfTimes[countE++][1];
//						} while ((timeToEARM != Integer.MAX_VALUE) && (timeToEARM >= timeToDeath));
//					}
//					// Else, if a valid time to AMD was generated, we need an INFINITE time to EARM
//					else {
//						do {
//							timesToEARM.push(timeToEARM);
//							timeToEARM = arrayOfTimes[countE++][1];
//						} while (timeToEARM != Integer.MAX_VALUE);
//					}
//				}
//			}
//			// Check if only an AMD event is active
//			else if (timeToAMD != Integer.MAX_VALUE) {
//				while (timeToDeath <= timeToAMD && timeToAMD != Integer.MAX_VALUE) {
//					timesToAMD.push(timeToAMD);
//					timeToAMD = arrayOfTimes[countA++][2];
//				}
//			}
//			// Check if only an EARM event is active
//			else if (timeToEARM != Integer.MAX_VALUE) {
//				while (timeToDeath <= timeToEARM && timeToEARM != Integer.MAX_VALUE) {
//					timesToEARM.push(timeToEARM);
//					timeToEARM = arrayOfTimes[countE++][1];
//				}
//			}
//			calibratedData[i] = new CalibratedOphtalmologicPatientData(40, 1, timeToDeath, (timeToEARM == Integer.MAX_VALUE) ? Long.MAX_VALUE : timeToEARM, (timeToAMD == Integer.MAX_VALUE) ? Long.MAX_VALUE : timeToAMD);
//			System.out.println((calibratedData[i].isCoherent() ? "PASS: " : "FAIL: ") + calibratedData[i]);
//		}
	}
}
/**
 * @author icasrod
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		SimulTest sim = new SimulTest();
//		sim.run();
		CalibrationTest.testThis();
	}

}
