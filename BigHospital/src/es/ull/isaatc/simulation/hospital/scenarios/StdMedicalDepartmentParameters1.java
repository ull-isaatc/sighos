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
import es.ull.isaatc.simulation.hospital.StdMedicalDptModel;
import es.ull.isaatc.util.WeeklyPeriodicCycle;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public final class StdMedicalDepartmentParameters1 extends ModelParameterMap {

	public StdMedicalDepartmentParameters1() {
		super(StdMedicalDptModel.Parameters.values().length);
		
		final TimeUnit unit = HospitalModelConfig.UNIT;
		put(StdMedicalDptModel.Parameters.NDOCTORS, 5);
		put(StdMedicalDptModel.Parameters.NBEDS, 5);
		put(StdMedicalDptModel.Parameters.PROB_ADM, 0.01);
		put(StdMedicalDptModel.Parameters.PROB_NUC_OP, 0.05);
		put(StdMedicalDptModel.Parameters.PROB_RAD_OP, 0.1);
		put(StdMedicalDptModel.Parameters.PROB_LAB_OP, 0.95);
		put(StdMedicalDptModel.Parameters.PROB_LABCENT_OP, 0.70);
		put(StdMedicalDptModel.Parameters.PROB_LABLAB_OP, 0.90);
		put(StdMedicalDptModel.Parameters.PROB_LABMIC_OP, 0.06);
		put(StdMedicalDptModel.Parameters.PROB_LABHAE_OP, 0.07);
		put(StdMedicalDptModel.Parameters.PROB_LABPAT_OP, 0.05);
		put(StdMedicalDptModel.Parameters.PROB_NUC_IP, 0.01);
		put(StdMedicalDptModel.Parameters.PROB_RAD_IP, 0.01);
		put(StdMedicalDptModel.Parameters.PROB_LAB_IP, 0.5);
		put(StdMedicalDptModel.Parameters.PROB_LABCENT_IP, 0.70);
		put(StdMedicalDptModel.Parameters.PROB_LABLAB_IP, 0.90);
		put(StdMedicalDptModel.Parameters.PROB_LABMIC_IP, 0.06);
		put(StdMedicalDptModel.Parameters.PROB_LABHAE_IP, 0.07);
		put(StdMedicalDptModel.Parameters.PROB_LABPAT_IP, 0.05);
		put(StdMedicalDptModel.Parameters.LENGTH_OP2ADM, HospitalModelConfig.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getMinute())); 
		put(StdMedicalDptModel.Parameters.LOS, HospitalModelConfig.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"TriangleVariate", TimeStamp.getHour(), new TimeStamp(TimeUnit.DAY, 7), new TimeStamp(TimeUnit.DAY, 2)));
		put(StdMedicalDptModel.Parameters.LENGTH_OP1, new SimulationTimeFunction(unit, "ConstantVariate", 12));
		put(StdMedicalDptModel.Parameters.LENGTH_OP2OP, 
				new SimulationTimeFunction(unit, "UniformVariate", new TimeStamp(TimeUnit.WEEK, 8), new TimeStamp(TimeUnit.WEEK, 12)));
		put(StdMedicalDptModel.Parameters.LENGTH_OP2, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		put(StdMedicalDptModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", 25));
		put(StdMedicalDptModel.Parameters.NCPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", 5));
		put(StdMedicalDptModel.Parameters.INTERARRIVAL, 
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, HospitalModelConfig.PATIENTARRIVAL, 0));
		put(StdMedicalDptModel.Parameters.ITERSUCC, 
				TimeFunctionFactory.getInstance("UniformVariate", 1, 5));
		put(StdMedicalDptModel.Parameters.PROB_1ST_APP, 0.2);
		put(StdMedicalDptModel.Parameters.HOURS_INTERIPTEST, 24);
	}

}
