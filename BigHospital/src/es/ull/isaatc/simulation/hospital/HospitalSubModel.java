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
import es.ull.isaatc.simulation.hospital.scenarios.StdMedicalServiceParameters1;
import es.ull.isaatc.simulation.hospital.scenarios.StdMedicalServiceParameters2;
import es.ull.isaatc.simulation.hospital.scenarios.StdMedicalServiceParameters3;
import es.ull.isaatc.simulation.hospital.scenarios.StdSurgicalParameters;
import es.ull.isaatc.simulation.hospital.scenarios.StdSurgicalServiceParameters1;
import es.ull.isaatc.simulation.hospital.scenarios.StdSurgicalServiceParameters2;
import es.ull.isaatc.simulation.hospital.scenarios.StdSurgicalServiceParameters3;
import es.ull.isaatc.util.WeeklyPeriodicCycle;

/**
 *  
 * @author Iván Castilla Rodríguez
 */
public final class HospitalSubModel {

	public static void createModel(SimulationObjectFactory factory, TimeStamp scale, int []nServices) {
		HospitalModelConfig.setScale(scale);
		
		// Central services
		ModelParameterMap centralParams = new StdCentralServicesParameters();
		CentralServicesSubModel.createModel(factory, centralParams);
		// Central Lab
		ModelParameterMap centralLabParams = new StdCentralLabParameters();
		CentralLabSubModel.createModel(factory, centralLabParams);
		// Surgical common services
		ModelParameterMap surParams = new StdSurgicalParameters();
		SurgicalSubModel.createModel(factory, surParams);
		
		int count = 0;
		// Like-Gynaegology services
		ModelParameterMap gynParams = new StdSurgicalServiceParameters1();
		for (int i = 1; i <= nServices[count]; i++)
			StdSurgicalSubModel.createModel(factory, "GYN" + i, gynParams);
		count++;

		// Like-Traumatology
		ModelParameterMap traParams = new StdSurgicalServiceParameters2();
		for (int i = 1; i <= nServices[count]; i++)
			StdSurgicalSubModel.createModel(factory, "TRA" + i, traParams);
		count++;

		// Like-Nephrology
		ModelParameterMap nepParams = new StdSurgicalServiceParameters3();
		for (int i = 1; i <= nServices[count]; i++)
			StdSurgicalSubModel.createModel(factory, "NEP" + i, nepParams);
		count++;
		
		// Like-Rheumatology
		ModelParameterMap rheParams = new StdMedicalServiceParameters1();
		for (int i = 1; i <= nServices[count]; i++)
			StdMedicalSubModel.createModel(factory, "RHE" + i, rheParams);
		count++;

		// Like-Dermatology
		ModelParameterMap derParams = new StdMedicalServiceParameters2();
		for (int i = 1; i <= nServices[count]; i++)
			StdMedicalSubModel.createModel(factory, "DER"+ i, derParams);
		count++;

		// Like-Ophthalmology
		ModelParameterMap ophParams = new StdMedicalServiceParameters3();
		for (int i = 1; i <= nServices[count]; i++)
			StdMedicalSubModel.createModel(factory, "OPH" + i, ophParams);
		count++;
	}

	public static void createDeterministicModel(SimulationObjectFactory factory, TimeStamp scale) {
		HospitalModelConfig.setScale(scale);
		
		final TimeUnit unit = factory.getSimulation().getTimeUnit();
		HospitalModelConfig.setScale(scale);
		
		// Central services
		ModelParameterMap centralParams = new ModelParameterMap(CentralServicesSubModel.Parameters.values().length);
		centralParams.put(CentralServicesSubModel.Parameters.NTECHNUC, 4);
		centralParams.put(CentralServicesSubModel.Parameters.NTECHRAD, 10);
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_NUCTEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 10));
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_RADTEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 10));
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_NUCANALYSIS, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 12));
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_RADANALYSIS, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 10));
		CentralServicesSubModel.createModel(factory, centralParams);
		// Central Lab
		ModelParameterMap centralLabParams = new ModelParameterMap(CentralLabSubModel.Parameters.values().length);
		centralLabParams.put(CentralLabSubModel.Parameters.NTECH, 23);
		centralLabParams.put(CentralLabSubModel.Parameters.N24HTECH, 5);
		centralLabParams.put(CentralLabSubModel.Parameters.NNURSES, 16);
		centralLabParams.put(CentralLabSubModel.Parameters.NXNURSES, 10);
		centralLabParams.put(CentralLabSubModel.Parameters.NSLOTS, 150);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_SAMPLE, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 6));
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_CENT, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 10));
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_ANALYSIS, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 8));
		centralLabParams.put(CentralLabSubModel.Parameters.NHAETECH, 2);
		centralLabParams.put(CentralLabSubModel.Parameters.NHAENURSES, 5);
		centralLabParams.put(CentralLabSubModel.Parameters.NHAESLOTS, 40);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_HAEANALYSIS, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 15));
		centralLabParams.put(CentralLabSubModel.Parameters.NMICROTECH, 10);
		centralLabParams.put(CentralLabSubModel.Parameters.NMICRONURSES, 0);
		centralLabParams.put(CentralLabSubModel.Parameters.NMICROSLOTS, 50);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_MICROANALYSIS, HospitalModelConfig.getScaledSimulationTimeFunction(unit,
				"ConstantVariate", 20));
		centralLabParams.put(CentralLabSubModel.Parameters.NPATTECH, 6);
		centralLabParams.put(CentralLabSubModel.Parameters.NPATNURSES, 1);
		centralLabParams.put(CentralLabSubModel.Parameters.NPATSLOTS, 50);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_PATANALYSIS, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 20));
		CentralLabSubModel.createModel(factory, centralLabParams);
		
		// Gynaegology
		ModelParameterMap gynParams = new ModelParameterMap(StdSurgicalSubModel.Parameters.values().length);
		gynParams.put(StdSurgicalSubModel.Parameters.NDOCTORS, 7);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_NUC_OP, 0.0);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_RAD_OP, 0.0);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LAB_OP, 1.0);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABLAB_OP, 1.0);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABMIC_OP, 0.0);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABHAE_OP, 0.0);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABPAT_OP, 0.0);
		// First OP Appointments takes up to the next quarter
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP1, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 12))); 
		// Patients wait at least one day before being admitted after last appointment. Admissions happen at 18:00
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2ADM, HospitalModelConfig.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getMinute()));
		// Next Appointment is at 8 am of several days later
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2OP, 
				HospitalModelConfig.getNextHighFunction(unit, TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 8), 
						"ConstantVariate", new TimeStamp(TimeUnit.WEEK, 1)));
		// Subsequent OP Appointments always ends at 5 minute multiples
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 5))); 
		gynParams.put(StdSurgicalSubModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", 40));
		gynParams.put(StdSurgicalSubModel.Parameters.INTERARRIVAL, 
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, HospitalModelConfig.PATIENTARRIVAL, 0));
		gynParams.put(StdSurgicalSubModel.Parameters.ITERSUCC, 
				TimeFunctionFactory.getInstance("ConstantVariate", 3));
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_1ST_APP, 0.2);
		StdSurgicalSubModel.createDeterministicModel(factory, "GYN", gynParams);
		StdSurgicalSubModel.createDeterministicModel(factory, "TRA", gynParams);
		StdSurgicalSubModel.createDeterministicModel(factory, "NEU", gynParams);
		StdSurgicalSubModel.createDeterministicModel(factory, "NEPH", gynParams);
		
	}

	public static void createTestModel(SimulationObjectFactory factory) {
		final TimeUnit unit = factory.getSimulation().getTimeUnit();

		ResourceType rtTech = HospitalModelConfig.createNStdHumanResources(factory, "Lab Technician", 2); 
		ResourceType rtSlot = HospitalModelConfig.createNStdMaterialResources(factory, "Analytical slot", 10); 
		final int nNurses = 2;
		final int nXNurses = 1;
		ResourceType rtNurse = HospitalModelConfig.createNStdHumanResources(factory, "Lab Nurse", nNurses - nXNurses);
		ResourceType rtXNurse = factory.getResourceTypeInstance("Lab Specialist Nurse");

		for (int i = 0; i < nXNurses; i++) {
			Resource res = HospitalModelConfig.getStdHumanResource(factory, "Lab Specialist Nurse " + i, rtNurse);
			res.addTimeTableEntry(HospitalModelConfig.getStdHumanResourceCycle(factory.getSimulation()), HospitalModelConfig.getStdHumanResourceAvailability(factory.getSimulation()), rtXNurse);
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
		TimeDrivenActivity actOutTest = factory.getTimeDrivenActivityInstance("Analysis OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
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
