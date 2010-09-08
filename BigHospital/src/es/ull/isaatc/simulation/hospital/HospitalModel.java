package es.ull.isaatc.simulation.hospital;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.ElementCreator;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.SimulationWeeklyPeriodicCycle;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.hospital.scenarios.StdCentralLabParameters;
import es.ull.isaatc.simulation.hospital.scenarios.StdCentralServicesParameters;
import es.ull.isaatc.simulation.hospital.scenarios.StdMedicalDepartmentParameters1;
import es.ull.isaatc.simulation.hospital.scenarios.StdMedicalDepartmentParameters2;
import es.ull.isaatc.simulation.hospital.scenarios.StdMedicalDepartmentParameters3;
import es.ull.isaatc.simulation.hospital.scenarios.StdSurgicalParameters;
import es.ull.isaatc.simulation.hospital.scenarios.StdSurgicalDepartmentParameters1;
import es.ull.isaatc.simulation.hospital.scenarios.StdSurgicalDepartmentParameters2;
import es.ull.isaatc.simulation.hospital.scenarios.StdSurgicalDepartmentParameters3;
import es.ull.isaatc.util.WeeklyPeriodicCycle;

/**
 *  The main class to create a model of a hospital. Several modules defined in different classes are used.
 *  
 * @author Iván Castilla Rodríguez
 */
public final class HospitalModel {

	/**
	 * Creates a model of a hospital
	 * @param factory A factory for simulation components
	 * @param scale The finest grain of the simulation time (in minutes). No process lasts less than this value.
	 * @param nDepartments The amount of departments created for each predefined type 
	 */
	public static void createModel(SimulationObjectFactory factory, TimeStamp scale, int []nDepartments) {
		HospitalModelConfig.setScale(scale);
		
		// Central services
		ModelParameterMap centralParams = new StdCentralServicesParameters();
		CentralServicesModel.createModel(factory, centralParams);
		// Central Lab
		ModelParameterMap centralLabParams = new StdCentralLabParameters();
		CentralLabModel.createModel(factory, centralLabParams);
		// Surgical common units
		ModelParameterMap surParams = new StdSurgicalParameters();
		SurgicalDptSharedModel.createModel(factory, surParams);
		
		int count = 0;
		// Like-Gynaegology departments
		ModelParameterMap gynParams = new StdSurgicalDepartmentParameters1();
		for (int i = 1; i <= nDepartments[count]; i++)
			StdSurgicalDptModel.createModel(factory, "GYN" + i, gynParams);
		count++;

		// Like-Traumatology
		ModelParameterMap traParams = new StdSurgicalDepartmentParameters2();
		for (int i = 1; i <= nDepartments[count]; i++)
			StdSurgicalDptModel.createModel(factory, "TRA" + i, traParams);
		count++;

		// Like-Nephrology
		ModelParameterMap nepParams = new StdSurgicalDepartmentParameters3();
		for (int i = 1; i <= nDepartments[count]; i++)
			StdSurgicalDptModel.createModel(factory, "NEP" + i, nepParams);
		count++;
		
		// Like-Rheumatology
		ModelParameterMap rheParams = new StdMedicalDepartmentParameters1();
		for (int i = 1; i <= nDepartments[count]; i++)
			StdMedicalDptModel.createModel(factory, "RHE" + i, rheParams);
		count++;

		// Like-Dermatology
		ModelParameterMap derParams = new StdMedicalDepartmentParameters2();
		for (int i = 1; i <= nDepartments[count]; i++)
			StdMedicalDptModel.createModel(factory, "DER"+ i, derParams);
		count++;

		// Like-Ophthalmology
		ModelParameterMap ophParams = new StdMedicalDepartmentParameters3();
		for (int i = 1; i <= nDepartments[count]; i++)
			StdMedicalDptModel.createModel(factory, "OPH" + i, ophParams);
		count++;
	}

	public static void createDeterministicModel(SimulationObjectFactory factory, TimeStamp scale) {
		final TimeUnit unit = factory.getSimulation().getTimeUnit();
		HospitalModelConfig.setScale(scale);
		
		// Central services
		ModelParameterMap centralParams = new ModelParameterMap(CentralServicesModel.Parameters.values().length);
		centralParams.put(CentralServicesModel.Parameters.NTECHUSS, 4);
		centralParams.put(CentralServicesModel.Parameters.NTECHRAD, 10);
		centralParams.put(CentralServicesModel.Parameters.LENGTH_USSTEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 10));
		centralParams.put(CentralServicesModel.Parameters.LENGTH_RADTEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 10));
		centralParams.put(CentralServicesModel.Parameters.LENGTH_USSREPORT, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 12));
		centralParams.put(CentralServicesModel.Parameters.LENGTH_RADREPORT, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 10));
		CentralServicesModel.createModel(factory, centralParams);
		// Central Lab
		ModelParameterMap centralLabParams = new ModelParameterMap(CentralLabModel.Parameters.values().length);
		centralLabParams.put(CentralLabModel.Parameters.NTECH, 23);
		centralLabParams.put(CentralLabModel.Parameters.N24HTECH, 5);
		centralLabParams.put(CentralLabModel.Parameters.NNURSES, 16);
		centralLabParams.put(CentralLabModel.Parameters.NXNURSES, 10);
		centralLabParams.put(CentralLabModel.Parameters.NSLOTS, 150);
		centralLabParams.put(CentralLabModel.Parameters.NCENT, 160);
		centralLabParams.put(CentralLabModel.Parameters.LENGTH_SAMPLE, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 2));
		centralLabParams.put(CentralLabModel.Parameters.LENGTH_CENT, HospitalModelConfig.getNextHighFunction(unit, 
				new TimeStamp(TimeUnit.MINUTE, 15), TimeStamp.getZero(), "ConstantVariate", 6));
		centralLabParams.put(CentralLabModel.Parameters.LENGTH_TEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 8));
		centralLabParams.put(CentralLabModel.Parameters.NHAETECH, 2);
		centralLabParams.put(CentralLabModel.Parameters.NHAENURSES, 5);
		centralLabParams.put(CentralLabModel.Parameters.NHAESLOTS, 40);
		centralLabParams.put(CentralLabModel.Parameters.LENGTH_HAETEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 15));
		centralLabParams.put(CentralLabModel.Parameters.NMICROTECH, 10);
		centralLabParams.put(CentralLabModel.Parameters.NMICRONURSES, 0);
		centralLabParams.put(CentralLabModel.Parameters.NMICROSLOTS, 50);
		centralLabParams.put(CentralLabModel.Parameters.LENGTH_MICROTEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit,
				"ConstantVariate", 20));
		centralLabParams.put(CentralLabModel.Parameters.NPATTECH, 6);
		centralLabParams.put(CentralLabModel.Parameters.NPATNURSES, 1);
		centralLabParams.put(CentralLabModel.Parameters.NPATSLOTS, 50);
		centralLabParams.put(CentralLabModel.Parameters.LENGTH_PATTEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 20));
		CentralLabModel.createModel(factory, centralLabParams);
		
		// Gynaegology
		ModelParameterMap gynParams = new ModelParameterMap(StdSurgicalDptModel.Parameters.values().length);
		gynParams.put(StdSurgicalDptModel.Parameters.NDOCTORS, 7);
		gynParams.put(StdSurgicalDptModel.Parameters.PROB_NUC_OP, 0.0);
		gynParams.put(StdSurgicalDptModel.Parameters.PROB_RAD_OP, 0.0);
		gynParams.put(StdSurgicalDptModel.Parameters.PROB_LAB_OP, 1.0);
		gynParams.put(StdSurgicalDptModel.Parameters.PROB_LABLAB_OP, 1.0);
		gynParams.put(StdSurgicalDptModel.Parameters.PROB_LABCENT_OP, 1.0);
		gynParams.put(StdSurgicalDptModel.Parameters.PROB_LABMIC_OP, 0.0);
		gynParams.put(StdSurgicalDptModel.Parameters.PROB_LABHAE_OP, 0.0);
		gynParams.put(StdSurgicalDptModel.Parameters.PROB_LABPAT_OP, 0.0);
		// First OP Appointments takes up to the next quarter
		gynParams.put(StdSurgicalDptModel.Parameters.LENGTH_OP1, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 12))); 
		// Patients wait at least one day before being admitted after last appointment. Admissions happen at 18:00
		gynParams.put(StdSurgicalDptModel.Parameters.LENGTH_OP2ADM, HospitalModelConfig.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getMinute()));
		// Next Appointment is at 8 am of several days later
		gynParams.put(StdSurgicalDptModel.Parameters.LENGTH_OP2OP, 
				HospitalModelConfig.getNextHighFunction(unit, TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 8), 
						"ConstantVariate", new TimeStamp(TimeUnit.WEEK, 1)));
		// Subsequent OP Appointments always ends at 5 minute multiples
		gynParams.put(StdSurgicalDptModel.Parameters.LENGTH_OP2, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 5))); 
		gynParams.put(StdSurgicalDptModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", 40));
		gynParams.put(StdSurgicalDptModel.Parameters.INTERARRIVAL, 
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, HospitalModelConfig.PATIENTARRIVAL, 0));
		gynParams.put(StdSurgicalDptModel.Parameters.ITERSUCC, 
				TimeFunctionFactory.getInstance("ConstantVariate", 3));
		gynParams.put(StdSurgicalDptModel.Parameters.PROB_1ST_APP, 0.2);
		StdSurgicalDptModel.createDeterministicModel(factory, "GYN", gynParams);
		StdSurgicalDptModel.createDeterministicModel(factory, "TRA", gynParams);
		StdSurgicalDptModel.createDeterministicModel(factory, "NEU", gynParams);
		StdSurgicalDptModel.createDeterministicModel(factory, "NEPH", gynParams);
		
	}

	public static void createTestModel(SimulationObjectFactory factory) {
		final TimeUnit unit = factory.getSimulation().getTimeUnit();

		ResourceType rtTech = HospitalModelConfig.createNStdHumanResources(factory, "Lab Technician", 2); 
		ResourceType rtSlot = HospitalModelConfig.createNStdMaterialResources(factory, "Test slot", 10); 
		final int nNurses = 2;
		final int nXNurses = 1;
		ResourceType rtNurse = HospitalModelConfig.createNStdHumanResources(factory, "Lab Nurse", nNurses - nXNurses);
		ResourceType rtXNurse = factory.getResourceTypeInstance("Lab Specialist Nurse");

		for (int i = 0; i < nXNurses; i++) {
			Resource res = HospitalModelConfig.getStdHumanResource(factory, "Lab Specialist Nurse " + i, rtNurse);
			res.addTimeTableEntry(HospitalModelConfig.getStdHumanResourceCycle(), HospitalModelConfig.getStdHumanResourceAvailability(), rtXNurse);
		}

		WorkGroup wgSample = factory.getWorkGroupInstance(new ResourceType[] {rtNurse}, new int[] {1});
		WorkGroup wgCent = factory.getWorkGroupInstance(new ResourceType[] {rtNurse}, new int[] {1});
		WorkGroup wgTest1 = factory.getWorkGroupInstance(new ResourceType[] {rtSlot, rtTech}, new int[] {1, 1});
		WorkGroup wgTest2 = factory.getWorkGroupInstance(new ResourceType[] {rtSlot, rtXNurse}, new int[] {1, 1});
		
		TimeDrivenActivity actOutSample = factory.getTimeDrivenActivityInstance("Take a sample OP", 2, EnumSet.noneOf(TimeDrivenActivity.Modifier.class));
		actOutSample.addWorkGroup(HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 50), wgSample);
		TimeDrivenActivity actOutCent = factory.getTimeDrivenActivityInstance("Centrifugation OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actOutCent.addWorkGroup(HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 30), wgCent);
		TimeDrivenActivity actOutTest = factory.getTimeDrivenActivityInstance("Test OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actOutTest.addWorkGroup(HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 20), 0, wgTest1);
		actOutTest.addWorkGroup(HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 40), 1, wgTest2);
		
		ElementType et = factory.getElementTypeInstance("Test Patient");
		SingleFlow root = (SingleFlow)factory.getFlowInstance("SingleFlow", actOutSample);
		SingleFlow succ1 = (SingleFlow)factory.getFlowInstance("SingleFlow", actOutCent);
		root.link(succ1);
		succ1.link(factory.getFlowInstance("SingleFlow", actOutTest));
		
		ElementCreator ec = factory.getElementCreatorInstance(TimeFunctionFactory.getInstance("ConstantVariate", 10), et, root);
		factory.getTimeDrivenGeneratorInstance(ec, new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, HospitalModelConfig.PATIENTARRIVAL, 0));
	}
	
}
