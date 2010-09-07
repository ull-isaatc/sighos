/**
 * 
 */
package es.ull.isaatc.simulation.hospital.scenarios;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.SimulationWeeklyPeriodicCycle;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.hospital.HospitalModelConfig;
import es.ull.isaatc.simulation.hospital.ModelParameterMap;
import es.ull.isaatc.simulation.hospital.StdSurgicalDptModel;
import es.ull.isaatc.util.WeeklyPeriodicCycle;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public final class StdSurgicalDepartmentParameters1 extends ModelParameterMap {

	public StdSurgicalDepartmentParameters1() {
		super(StdSurgicalDptModel.Parameters.values().length);
		
		final TimeUnit unit = HospitalModelConfig.UNIT;
		put(StdSurgicalDptModel.Parameters.NBEDS, 20);
		put(StdSurgicalDptModel.Parameters.NSBEDS, 3);
		put(StdSurgicalDptModel.Parameters.NSURGEONS, 4);
		put(StdSurgicalDptModel.Parameters.NSURGERIES, 4);
		put(StdSurgicalDptModel.Parameters.NDOCTORS, 7);
		put(StdSurgicalDptModel.Parameters.NSCRUBNURSES, 4);
		put(StdSurgicalDptModel.Parameters.NSURGERY_ASSIST, 2);		
		put(StdSurgicalDptModel.Parameters.PROB_NUC_OP, 0.1);
		put(StdSurgicalDptModel.Parameters.PROB_RAD_OP, 0.01);
		put(StdSurgicalDptModel.Parameters.PROB_LAB_OP, 0.95);
		put(StdSurgicalDptModel.Parameters.PROB_LABCENT_OP, 0.70);
		put(StdSurgicalDptModel.Parameters.PROB_LABLAB_OP, 0.90);
		put(StdSurgicalDptModel.Parameters.PROB_LABMIC_OP, 0.06);
		put(StdSurgicalDptModel.Parameters.PROB_LABHAE_OP, 0.1);
		put(StdSurgicalDptModel.Parameters.PROB_LABPAT_OP, 0.05);
		put(StdSurgicalDptModel.Parameters.PROB_NUC_IP, 0.01);
		put(StdSurgicalDptModel.Parameters.PROB_RAD_IP, 0.01);
		put(StdSurgicalDptModel.Parameters.PROB_LAB_IP, 0.5);
		put(StdSurgicalDptModel.Parameters.PROB_LABCENT_IP, 0.70);
		put(StdSurgicalDptModel.Parameters.PROB_LABLAB_IP, 0.90);
		put(StdSurgicalDptModel.Parameters.PROB_LABMIC_IP, 0.06);
		put(StdSurgicalDptModel.Parameters.PROB_LABHAE_IP, 0.1);
		put(StdSurgicalDptModel.Parameters.PROB_LABPAT_IP, 0.05);
		// First OP Appointments takes up to the next quarter
		put(StdSurgicalDptModel.Parameters.LENGTH_OP1, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 12))); 
		// Patients wait at least one day before being admitted after last appointment. Admissions happen at 18:00
		put(StdSurgicalDptModel.Parameters.LENGTH_OP2ADM, HospitalModelConfig.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getMinute()));
		// Next Appointment is at 8 am of several days later
		put(StdSurgicalDptModel.Parameters.LENGTH_OP2OP, 
				HospitalModelConfig.getNextHighFunction(unit, TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 8), 
						"UniformVariate", new TimeStamp(TimeUnit.WEEK, 8), new TimeStamp(TimeUnit.WEEK, 12)));
		// Subsequent OP Appointments always ends at 5 minute multiples
		put(StdSurgicalDptModel.Parameters.LENGTH_OP2, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 5))); 
		put(StdSurgicalDptModel.Parameters.PROB_ADM, 0.5);
		put(StdSurgicalDptModel.Parameters.LENGTH_SUR, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 90)));
		put(StdSurgicalDptModel.Parameters.LENGTH_SSUR, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 30)));
		put(StdSurgicalDptModel.Parameters.LENGTH_ASUR, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 20)));
		put(StdSurgicalDptModel.Parameters.LENGTH_POP, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 8)));
		put(StdSurgicalDptModel.Parameters.LENGTH_SUR2POP, 
				HospitalModelConfig.getNextHighFunction(unit, TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 8), 
						"ConstantVariate", new TimeStamp(TimeUnit.DAY, 20)));
		put(StdSurgicalDptModel.Parameters.LENGTH_ICU, 
				HospitalModelConfig.getScaledSimulationTimeFunction(unit, "TriangleVariate", 
				new TimeStamp(TimeUnit.HOUR, 2), new TimeStamp(TimeUnit.WEEK, 2), new TimeStamp(TimeUnit.DAY, 3)));
		put(StdSurgicalDptModel.Parameters.LENGTH_PACU, 
				HospitalModelConfig.getScaledSimulationTimeFunction(unit, "TriangleVariate", 
				new TimeStamp(TimeUnit.HOUR, 1), new TimeStamp(TimeUnit.DAY, 3), new TimeStamp(TimeUnit.HOUR, 7)));
		put(StdSurgicalDptModel.Parameters.LENGTH_SPACU, 
				HospitalModelConfig.getScaledSimulationTimeFunction(unit, "UniformVariate", 
				new TimeStamp(TimeUnit.HOUR, 1), new TimeStamp(TimeUnit.HOUR, 3)));
		put(StdSurgicalDptModel.Parameters.LENGTH_APACU, 
				HospitalModelConfig.getScaledSimulationTimeFunction(unit, "UniformVariate", 
				new TimeStamp(TimeUnit.MINUTE, 10), new TimeStamp(TimeUnit.MINUTE, 30)));
		// Patients wait at least one day after exiting, always at 12:00
		put(StdSurgicalDptModel.Parameters.LENGTH_SUR2EXIT, HospitalModelConfig.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"TriangleVariate", TimeStamp.getHour(), new TimeStamp(TimeUnit.DAY, 4), TimeStamp.getDay()));
		put(StdSurgicalDptModel.Parameters.LENGTH_SSUR2EXIT, HospitalModelConfig.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"UniformVariate", TimeStamp.getHour(), TimeStamp.getDay()));
		put(StdSurgicalDptModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", 30));
		put(StdSurgicalDptModel.Parameters.NSPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", 10));
		put(StdSurgicalDptModel.Parameters.NAPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", 10));
		final TimeStamp pArrival = HospitalModelConfig.PATIENTARRIVAL;
		put(StdSurgicalDptModel.Parameters.INTERARRIVAL, 
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, pArrival, 0));
		put(StdSurgicalDptModel.Parameters.SINTERARRIVAL, 
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, pArrival, 0));
		put(StdSurgicalDptModel.Parameters.AINTERARRIVAL, 
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, pArrival, 0));
		put(StdSurgicalDptModel.Parameters.ITERSUCC, 
				TimeFunctionFactory.getInstance("UniformVariate", 1, 5));
		put(StdSurgicalDptModel.Parameters.PROB_1ST_APP, 0.2);
		put(StdSurgicalDptModel.Parameters.HOURS_INTERIPTEST, 24);
	}

}
