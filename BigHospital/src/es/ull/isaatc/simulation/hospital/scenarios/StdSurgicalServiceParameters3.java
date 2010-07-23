/**
 * 
 */
package es.ull.isaatc.simulation.hospital.scenarios;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.SimulationWeeklyPeriodicCycle;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.hospital.HospitalModelConfig;
import es.ull.isaatc.simulation.hospital.ModelParameterMap;
import es.ull.isaatc.simulation.hospital.StdSurgicalSubModel;
import es.ull.isaatc.util.WeeklyPeriodicCycle;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public final class StdSurgicalServiceParameters3 extends ModelParameterMap {

	public StdSurgicalServiceParameters3() {
		super(StdSurgicalSubModel.Parameters.values().length);
		
		final TimeUnit unit = HospitalModelConfig.UNIT;

		put(StdSurgicalSubModel.Parameters.NBEDS, 20);
		put(StdSurgicalSubModel.Parameters.NSBEDS, 3);
		put(StdSurgicalSubModel.Parameters.NSURGEONS, 4);
		put(StdSurgicalSubModel.Parameters.NSURGERIES, 4);
		put(StdSurgicalSubModel.Parameters.NDOCTORS, 7);
		put(StdSurgicalSubModel.Parameters.NSCRUBNURSES, 4);
		put(StdSurgicalSubModel.Parameters.NSURGERY_ASSIST, 2);		
		put(StdSurgicalSubModel.Parameters.PROB_NUC_OP, 0.1);
		put(StdSurgicalSubModel.Parameters.PROB_RAD_OP, 0.01);
		put(StdSurgicalSubModel.Parameters.PROB_LAB_OP, 0.95);
		put(StdSurgicalSubModel.Parameters.PROB_LABCENT_OP, 0.70);
		put(StdSurgicalSubModel.Parameters.PROB_LABLAB_OP, 0.90);
		put(StdSurgicalSubModel.Parameters.PROB_LABMIC_OP, 0.06);
		put(StdSurgicalSubModel.Parameters.PROB_LABHAE_OP, 0.1);
		put(StdSurgicalSubModel.Parameters.PROB_LABPAT_OP, 0.05);
		put(StdSurgicalSubModel.Parameters.PROB_NUC_IP, 0.01);
		put(StdSurgicalSubModel.Parameters.PROB_RAD_IP, 0.01);
		put(StdSurgicalSubModel.Parameters.PROB_LAB_IP, 0.5);
		put(StdSurgicalSubModel.Parameters.PROB_LABCENT_IP, 0.70);
		put(StdSurgicalSubModel.Parameters.PROB_LABLAB_IP, 0.90);
		put(StdSurgicalSubModel.Parameters.PROB_LABMIC_IP, 0.06);
		put(StdSurgicalSubModel.Parameters.PROB_LABHAE_IP, 0.1);
		put(StdSurgicalSubModel.Parameters.PROB_LABPAT_IP, 0.05);
		put(StdSurgicalSubModel.Parameters.LENGTH_OP1, new SimulationTimeFunction(unit, "ConstantVariate", 12));
		// Patients wait at least one day before being admitted after last appointment. Admissions happen at 18:00
		put(StdSurgicalSubModel.Parameters.LENGTH_OP2ADM, HospitalModelConfig.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getMinute())); 
		put(StdSurgicalSubModel.Parameters.LENGTH_OP2OP, 
				new SimulationTimeFunction(unit, "UniformVariate", new TimeStamp(TimeUnit.WEEK, 8), new TimeStamp(TimeUnit.WEEK, 12)));
		put(StdSurgicalSubModel.Parameters.LENGTH_OP2, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		put(StdSurgicalSubModel.Parameters.PROB_ADM, 0.5);
		put(StdSurgicalSubModel.Parameters.LENGTH_SUR, new SimulationTimeFunction(unit, "ConstantVariate", 90));
		put(StdSurgicalSubModel.Parameters.LENGTH_SSUR, new SimulationTimeFunction(unit, "ConstantVariate", 30));
		put(StdSurgicalSubModel.Parameters.LENGTH_ASUR, new SimulationTimeFunction(unit, "ConstantVariate", 20));
		put(StdSurgicalSubModel.Parameters.LENGTH_POP, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		put(StdSurgicalSubModel.Parameters.LENGTH_SUR2POP, 
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 20)));
		put(StdSurgicalSubModel.Parameters.LENGTH_ICU, 
				new SimulationTimeFunction(unit, "TriangleVariate", 
				new TimeStamp(TimeUnit.HOUR, 2), new TimeStamp(TimeUnit.WEEK, 2), new TimeStamp(TimeUnit.DAY, 3)));
		put(StdSurgicalSubModel.Parameters.LENGTH_PACU, 
				new SimulationTimeFunction(unit, "TriangleVariate", 
				new TimeStamp(TimeUnit.HOUR, 1), new TimeStamp(TimeUnit.DAY, 3), new TimeStamp(TimeUnit.HOUR, 7)));
		put(StdSurgicalSubModel.Parameters.LENGTH_SPACU, 
				new SimulationTimeFunction(unit, "UniformVariate", 
				new TimeStamp(TimeUnit.HOUR, 1), new TimeStamp(TimeUnit.HOUR, 3)));
		put(StdSurgicalSubModel.Parameters.LENGTH_APACU, 
				new SimulationTimeFunction(unit, "UniformVariate", 
				new TimeStamp(TimeUnit.MINUTE, 10), new TimeStamp(TimeUnit.MINUTE, 30)));
		// Patients wait at least one day after exiting, always at 12:00
		put(StdSurgicalSubModel.Parameters.LENGTH_SUR2EXIT, HospitalModelConfig.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"TriangleVariate", TimeStamp.getHour(), new TimeStamp(TimeUnit.DAY, 4), TimeStamp.getDay()));
		put(StdSurgicalSubModel.Parameters.LENGTH_SSUR2EXIT, HospitalModelConfig.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"UniformVariate", TimeStamp.getHour(), TimeStamp.getDay()));
		put(StdSurgicalSubModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", 30));
		put(StdSurgicalSubModel.Parameters.NSPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", 5));
		put(StdSurgicalSubModel.Parameters.NAPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", 5));
		final TimeStamp pArrival = HospitalModelConfig.PATIENTARRIVAL;
		put(StdSurgicalSubModel.Parameters.INTERARRIVAL, 
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, pArrival, 0));
		put(StdSurgicalSubModel.Parameters.SINTERARRIVAL, 
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, pArrival, 0));
		put(StdSurgicalSubModel.Parameters.AINTERARRIVAL, 
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, pArrival, 0));
		put(StdSurgicalSubModel.Parameters.ITERSUCC, 
				TimeFunctionFactory.getInstance("UniformVariate", 1, 5));
		put(StdSurgicalSubModel.Parameters.PROB_1ST_APP, 0.2);
		put(StdSurgicalSubModel.Parameters.HOURS_INTERIPTEST, 12);
	}

}
