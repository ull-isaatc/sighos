/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.Experiment;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;
import es.ull.isaatc.simulation.hospital.view.ActivityQueueFileView;
import es.ull.isaatc.util.Output;

class BigHospitalExperiment extends Experiment {
	private static final int NEXP = 1;
	private static final TimeUnit unit = TimeUnit.MINUTE;
	private static final SimulationFactory.SimulationType simType = SimulationType.GROUPEDX;
	private static final TimeStamp warmup = new TimeStamp(TimeUnit.MONTH, 1);
	private static final TimeStamp endTs = new TimeStamp(TimeUnit.MONTH, 6);
//	private static final TimeStamp endTs = new TimeStamp(TimeUnit.DAY, 1);
	private static final int threads = 4;
	private int debug = 1;

	public BigHospitalExperiment() {
		super("Big Hospital Experiments", NEXP);
	}
	
	private void createModel(SimulationObjectFactory factory) {
		final Simulation simul = factory.getSimulation();
		final TimeUnit unit = simul.getTimeUnit();
		
		int lastId = 0;
		
		// Central services
		ModelParameterMap centralParams = new ModelParameterMap(CentralServicesSubModel.Parameters.values().length);
		centralParams.put(CentralServicesSubModel.Parameters.RESLAB, 2);
		centralParams.put(CentralServicesSubModel.Parameters.RESCAR, 2);
		centralParams.put(CentralServicesSubModel.Parameters.RESHAE, 2);
		centralParams.put(CentralServicesSubModel.Parameters.RESRAD, 2);
		lastId = CentralServicesSubModel.createModel(factory, lastId, centralParams);
		// Surgical common services
		ModelParameterMap surParams = new ModelParameterMap(SurgicalSubModel.Parameters.values().length);
		surParams.put(SurgicalSubModel.Parameters.NBEDS_ICU, 15);
		surParams.put(SurgicalSubModel.Parameters.NBEDS_PACU, 10);
		surParams.put(SurgicalSubModel.Parameters.NANAESTHETISTS, 4);
		lastId = SurgicalSubModel.createModel(factory, lastId, surParams);
		
		// Gynaegology
		ModelParameterMap gynParams = new ModelParameterMap(StdSurgicalSubModel.Parameters.values().length);
		gynParams.put(StdSurgicalSubModel.Parameters.NBEDS, 15);
		gynParams.put(StdSurgicalSubModel.Parameters.NSURGEONS, 4);
		gynParams.put(StdSurgicalSubModel.Parameters.NSURGERIES, 4);
		gynParams.put(StdSurgicalSubModel.Parameters.NDOCTORS, 5);
		gynParams.put(StdSurgicalSubModel.Parameters.NSCRUBNURSES, 4);
		gynParams.put(StdSurgicalSubModel.Parameters.NSURGERY_ASSIST, 2);		
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP1, new SimulationTimeFunction(unit, "ConstantVariate", 12));
		// Patients wait at least one day before being admitted after last appointment. Admissions happen at 18:00
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2SUR, HospitalModelTools.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getDay())); 
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2OP, new SimulationTimeFunction(unit, "UniformVariate", new TimeStamp(TimeUnit.WEEK, 8), new TimeStamp(TimeUnit.WEEK, 12)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR, new SimulationTimeFunction(unit, "ConstantVariate", 30));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_POP, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR2POP, new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 20)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_ICU, new SimulationTimeFunction(unit, "TriangleVariate", 
				new TimeStamp(TimeUnit.HOUR, 2), new TimeStamp(TimeUnit.WEEK, 2), new TimeStamp(TimeUnit.DAY, 3)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_PACU, new SimulationTimeFunction(unit, "TriangleVariate", 
				new TimeStamp(TimeUnit.HOUR, 1), new TimeStamp(TimeUnit.DAY, 3), new TimeStamp(TimeUnit.HOUR, 7)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR2EXIT, HospitalModelTools.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"TriangleVariate", TimeStamp.getHour(), new TimeStamp(TimeUnit.DAY, 3), TimeStamp.getDay()));
		gynParams.put(StdSurgicalSubModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", 50));
		gynParams.put(StdSurgicalSubModel.Parameters.INTERARRIVAL, SimulationPeriodicCycle.newDailyCycle(unit, new TimeStamp(TimeUnit.MINUTE, 479)));
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_1ST_APP, 0.2);
		lastId = StdSurgicalSubModel.createModel(factory, lastId, "GYN", gynParams);
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "Big Hospital", unit, TimeStamp.getZero(), endTs);
		createModel(factory);
		
		Simulation simul = factory.getSimulation();		
		simul.setNThreads(threads);
		simul.addInfoReceiver(new ActivityQueueFileView(simul, "C:\\queue.txt", TimeStamp.getWeek()));
		if (debug == 1)
			simul.addInfoReceiver(new StdInfoView(simul));
		else if (debug == 2)
			simul.setOutput(new Output(true));
		return simul;
	}

	@Override
	public void start() {
		for (int i = 0; i < nExperiments; i++)
			getSimulation(i).run();
		end();		
	}
	
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class BigHospital {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new BigHospitalExperiment().start();
	}

}
