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
import es.ull.isaatc.simulation.hospital.StdMedicalSubModel;
import es.ull.isaatc.util.WeeklyPeriodicCycle;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public final class StdMedicalServiceParameters2 extends ModelParameterMap {

	public StdMedicalServiceParameters2() {
		super(StdMedicalSubModel.Parameters.values().length);
		
		final TimeUnit unit = HospitalModelConfig.UNIT;
		put(StdMedicalSubModel.Parameters.NDOCTORS, 5);
		put(StdMedicalSubModel.Parameters.NBEDS, 5);
		put(StdMedicalSubModel.Parameters.PROB_ADM, 0.01);
		put(StdMedicalSubModel.Parameters.PROB_NUC_OP, 0.01);
		put(StdMedicalSubModel.Parameters.PROB_RAD_OP, 0.01);
		put(StdMedicalSubModel.Parameters.PROB_LAB_OP, 0.3);
		put(StdMedicalSubModel.Parameters.PROB_LABLAB_OP, 0.90);
		put(StdMedicalSubModel.Parameters.PROB_LABMIC_OP, 0.06);
		put(StdMedicalSubModel.Parameters.PROB_LABHAE_OP, 0.07);
		put(StdMedicalSubModel.Parameters.PROB_LABPAT_OP, 0.05);
		put(StdMedicalSubModel.Parameters.PROB_NUC_IP, 0.01);
		put(StdMedicalSubModel.Parameters.PROB_RAD_IP, 0.01);
		put(StdMedicalSubModel.Parameters.PROB_LAB_IP, 0.1);
		put(StdMedicalSubModel.Parameters.PROB_LABLAB_IP, 0.90);
		put(StdMedicalSubModel.Parameters.PROB_LABMIC_IP, 0.06);
		put(StdMedicalSubModel.Parameters.PROB_LABHAE_IP, 0.07);
		put(StdMedicalSubModel.Parameters.PROB_LABPAT_IP, 0.05);
		put(StdMedicalSubModel.Parameters.LENGTH_OP2ADM, HospitalModelConfig.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getMinute())); 
		put(StdMedicalSubModel.Parameters.LOS, HospitalModelConfig.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"TriangleVariate", TimeStamp.getHour(), new TimeStamp(TimeUnit.DAY, 7), new TimeStamp(TimeUnit.DAY, 2)));
		put(StdMedicalSubModel.Parameters.LENGTH_OP1, new SimulationTimeFunction(unit, "ConstantVariate", 12));
		put(StdMedicalSubModel.Parameters.LENGTH_OP2OP, 
				new SimulationTimeFunction(unit, "UniformVariate", new TimeStamp(TimeUnit.WEEK, 8), new TimeStamp(TimeUnit.WEEK, 12)));
		put(StdMedicalSubModel.Parameters.LENGTH_OP2, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		put(StdMedicalSubModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", 40));
		put(StdMedicalSubModel.Parameters.INTERARRIVAL, 
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, HospitalModelConfig.PATIENTARRIVAL, 0));
		put(StdMedicalSubModel.Parameters.ITERSUCC, 
				TimeFunctionFactory.getInstance("UniformVariate", 1, 5));
		put(StdMedicalSubModel.Parameters.PROB_1ST_APP, 0.2);
	}

}
