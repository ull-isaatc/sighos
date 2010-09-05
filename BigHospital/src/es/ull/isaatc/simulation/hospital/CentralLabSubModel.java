/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import java.util.EnumSet;

import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.condition.PercentageCondition;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.StructuredSynchroMergeFlow;

/**
 * Model of the central laboratories of a hospital.
 * 
 * @author Iván Castilla Rodríguez
 */
public class CentralLabSubModel {
	private static TimeDrivenActivity actOutSample = null;
	private static TimeDrivenActivity actOutCent = null;
	private static TimeDrivenActivity actOutTest = null;
	private static TimeDrivenActivity actOutHaeTest = null;
	private static TimeDrivenActivity actOutMicTest = null;
	private static TimeDrivenActivity actOutPatTest = null;
	private static TimeDrivenActivity actInSample = null;
	private static TimeDrivenActivity actInCent = null;
	private static TimeDrivenActivity actInTest = null;
	private static TimeDrivenActivity actInHaeTest = null;
	private static TimeDrivenActivity actInMicTest = null;
	private static TimeDrivenActivity actInPatTest = null;
	
	public enum Parameters implements ModelParameterMap.ModelParameter {
		NTECH(Integer.class, "Number of technicians"),
		// Instead of creating shifts, we assume a specific amount of technicians available 24/7
		N24HTECH(Integer.class, "Number of technicians working 24 hours"),
		NNURSES(Integer.class, "Number of nurses"),
		NXNURSES(Integer.class, "Number of specialist nurses"),
		NSLOTS(Integer.class, "'Slots' for tests"),
		NCENT(Integer.class, "'Slots' for centrifugation"),
		LENGTH_SAMPLE(SimulationTimeFunction.class, "Duration of taking a sample"),
		LENGTH_CENT(SimulationTimeFunction.class, "Duration of centrifugation"),
		// The test slot is an abstraction, result of dividing the length of a test by the number of tests
		// that can be done at the same time by a technician.
		LENGTH_TEST(SimulationTimeFunction.class, "Duration of test slot"),
		NHAETECH(Integer.class, "Number of technicians from Haematology Lab"),
		NHAENURSES(Integer.class, "Number of nurses from Haematology Lab"),
		NHAESLOTS(Integer.class, "'Slots' for tests from Haematology Lab"),
		LENGTH_HAETEST(SimulationTimeFunction.class, "Duration of test slot for Haematology Lab"),
		NMICROTECH(Integer.class, "Number of technicians from Microbiology Lab"),
		NMICRONURSES(Integer.class, "Number of nurses from Microbiology Lab"),
		NMICROSLOTS(Integer.class, "'Slots' for tests from Microbiology Lab"),
		LENGTH_MICROTEST(SimulationTimeFunction.class, "Duration of test slot from Microbiology Lab"),
		NPATTECH(Integer.class, "Number of technicians from Anatomopathology Lab"),
		NPATNURSES(Integer.class, "Number of nurses from Anatomopathology Lab"),
		NPATSLOTS(Integer.class, "'Slots' for tests from Anatomopathology Lab"),
		LENGTH_PATTEST(SimulationTimeFunction.class, "Duration of test slot from Anatomopathology Lab");
		
		private final Class<?> type;
		private final String description;
		
		private Parameters(Class<?> type, String description) {
			this.type = type;
			this.description = description;
		}
		
		@Override
		public Class<?> getType() {
			return type;
		}
		
		@Override
		public String toString() {
			return super.toString() + ": " + description;
		}
	}
	
	/**
	 * Creates the model for the central laboratories with the specified parameters
	 * @param factory A factory for simulation components
	 * @param params Parameters to create the model
	 */
	public static void createModel(SimulationObjectFactory factory, ModelParameterMap params) {
		// First checks if all the parameters have been properly set
		for (Parameters p : Parameters.values())
			if (params.get(p) == null)
				throw new IllegalArgumentException("Param <<" + p + ">> missing");
		
		// Resource types and standard resources
		ResourceType rtTech = HospitalModelConfig.createNStdHumanResources(factory, "Lab Technician", (Integer)params.get(Parameters.NTECH)); 
		ResourceType rtSlot = HospitalModelConfig.createNStdMaterialResources(factory, "Test slot", (Integer)params.get(Parameters.NSLOTS)); 
		ResourceType rtCent = HospitalModelConfig.createNStdMaterialResources(factory, "Centrifugation slot", (Integer)params.get(Parameters.NCENT)); 
		final int nNurses = ((Integer)params.get(Parameters.NNURSES)).intValue();
		final int nXNurses = ((Integer)params.get(Parameters.NXNURSES)).intValue();
		ResourceType rtNurse = HospitalModelConfig.createNStdHumanResources(factory, "Lab Nurse", nNurses - nXNurses);
		ResourceType rtXNurse = factory.getResourceTypeInstance("Lab Specialist Nurse");
		
		ResourceType rtHaeTech = HospitalModelConfig.createNStdHumanResources(factory, "Haematology Lab Technician", (Integer)params.get(Parameters.NHAETECH)); 
		ResourceType rtHaeNurse = factory.getResourceTypeInstance("Haematology Lab Nurse"); 
		ResourceType rtHaeSlot = HospitalModelConfig.createNStdMaterialResources(factory, "Haematology Lab Test slot", (Integer)params.get(Parameters.NHAESLOTS)); 
		ResourceType rtMicTech = HospitalModelConfig.createNStdHumanResources(factory, "Microbiology Lab Technician", (Integer)params.get(Parameters.NMICROTECH)); 
		ResourceType rtMicNurse = HospitalModelConfig.createNStdHumanResources(factory, "Microbiology Lab Nurse", (Integer)params.get(Parameters.NMICRONURSES));
		ResourceType rtMicSlot = HospitalModelConfig.createNStdMaterialResources(factory, "Microbiology Lab Test slot", (Integer)params.get(Parameters.NMICROSLOTS)); 
		ResourceType rtPatTech = HospitalModelConfig.createNStdHumanResources(factory, "Anatomopathology Lab Technician", (Integer)params.get(Parameters.NPATTECH)); 
		ResourceType rtPatNurse = HospitalModelConfig.createNStdHumanResources(factory, "Anatomopathology Lab Nurse", (Integer)params.get(Parameters.NPATNURSES));
		ResourceType rtPatSlot = HospitalModelConfig.createNStdMaterialResources(factory, "Anatomopathology Lab Test slot", (Integer)params.get(Parameters.NPATSLOTS)); 
		
		// Specific Resources
		final int n24HTech = ((Integer)params.get(Parameters.N24HTECH)).intValue();
		for (int i = 0; i < n24HTech; i++) {
			// Since they work 24/7, they can be considered material resources 
			HospitalModelConfig.getStdMaterialResource(factory, "24/7 Lab Technician " + i, rtTech);
		}
		for (int i = 0; i < nXNurses; i++) {
			Resource res = HospitalModelConfig.getStdHumanResource(factory, "Lab Specialist Nurse " + i, rtNurse);
			res.addTimeTableEntry(HospitalModelConfig.getStdHumanResourceCycle(), HospitalModelConfig.getStdHumanResourceAvailability(), rtXNurse);
		}
		final int nHaeNurses = (Integer)params.get(Parameters.NHAENURSES);
		for (int i = 0; i < nHaeNurses; i++) {
			Resource res = HospitalModelConfig.getStdHumanResource(factory, "Haematology Lab Nurse " + i, rtHaeNurse);
			res.addTimeTableEntry(HospitalModelConfig.getStdHumanResourceCycle(), HospitalModelConfig.getStdHumanResourceAvailability(), rtNurse);
		}
			
		// Workgroups
		WorkGroup wgSample = factory.getWorkGroupInstance(new ResourceType[] {rtNurse}, new int[] {1});
		WorkGroup wgCent = factory.getWorkGroupInstance(new ResourceType[] {rtCent}, new int[] {1});
		WorkGroup wgTest1 = factory.getWorkGroupInstance(new ResourceType[] {rtSlot, rtTech}, new int[] {1, 1});
		WorkGroup wgTest2 = factory.getWorkGroupInstance(new ResourceType[] {rtSlot, rtXNurse}, new int[] {1, 1});
		WorkGroup wgTestHae1 = factory.getWorkGroupInstance(new ResourceType[] {rtHaeSlot, rtHaeTech}, new int[] {1, 1});
		WorkGroup wgTestHae2 = factory.getWorkGroupInstance(new ResourceType[] {rtHaeSlot, rtHaeNurse}, new int[] {1, 1});
		WorkGroup wgTestMic1 = factory.getWorkGroupInstance(new ResourceType[] {rtMicSlot, rtMicTech}, new int[] {1, 1});
		WorkGroup wgTestMic2 = factory.getWorkGroupInstance(new ResourceType[] {rtMicSlot, rtMicNurse}, new int[] {1, 1});
		WorkGroup wgTestPat1 = factory.getWorkGroupInstance(new ResourceType[] {rtPatSlot, rtPatTech}, new int[] {1, 1});
		WorkGroup wgTestPat2 = factory.getWorkGroupInstance(new ResourceType[] {rtPatSlot, rtPatNurse}, new int[] {1, 1});
		
		// Activities
		actOutSample = factory.getTimeDrivenActivityInstance("Take a sample OP", 2, EnumSet.noneOf(TimeDrivenActivity.Modifier.class));
		actOutSample.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_SAMPLE), wgSample);
		actOutCent = factory.getTimeDrivenActivityInstance("Centrifugation OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actOutCent.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_CENT), wgCent);
		actOutTest = factory.getTimeDrivenActivityInstance("Test OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actOutTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_TEST), 0, wgTest1);
		actOutTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_TEST), 1, wgTest2);
		actOutHaeTest = factory.getTimeDrivenActivityInstance("Haematology Test OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actOutHaeTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_HAETEST), 0, wgTestHae1);
		actOutHaeTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_HAETEST), 1, wgTestHae2);
		actOutMicTest = factory.getTimeDrivenActivityInstance("Microbiology Test OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actOutMicTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_MICROTEST), 0, wgTestMic1);
		actOutMicTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_MICROTEST), 1, wgTestMic2);
		actOutPatTest = factory.getTimeDrivenActivityInstance("Anatomopathology Test OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actOutPatTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_PATTEST), 0, wgTestPat1);
		actOutPatTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_PATTEST), 1, wgTestPat2);
		
		// Creates more priority activities for inpatients
		actInSample = factory.getTimeDrivenActivityInstance("Take a sample IP", 1, EnumSet.noneOf(TimeDrivenActivity.Modifier.class));
		actInSample.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_SAMPLE), wgSample);
		actInCent = factory.getTimeDrivenActivityInstance("Centrifugation IP", 1, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actInCent.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_CENT), wgCent);
		actInTest = factory.getTimeDrivenActivityInstance("Test IP", 1, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actInTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_TEST), 0, wgTest1);
		actInTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_TEST), 1, wgTest2);
		actInHaeTest = factory.getTimeDrivenActivityInstance("Haematology Test IP", 1, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actInHaeTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_HAETEST), 0, wgTestHae1);
		actInHaeTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_HAETEST), 1, wgTestHae2);
		actInMicTest = factory.getTimeDrivenActivityInstance("Microbiology Test IP", 1, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actInMicTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_MICROTEST), 0, wgTestMic1);
		actInMicTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_MICROTEST), 1, wgTestMic2);
		actInPatTest = factory.getTimeDrivenActivityInstance("Anatomopathology Test IP", 1, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actInPatTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_PATTEST), 0, wgTestPat1);
		actInPatTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_PATTEST), 1, wgTestPat2);
	}

	/**
	 * Creates and returns a workflow for performing laboratory tests with outpatients.
	 * @param factory A factory for simulation components
	 * @param prob_cent Probability of centrifuge the sample
	 * @param prob_test Probability of requiring a laboratory test
	 * @param prob_hae Probability of requiring a test from the Haematology lab
	 * @param prob_mic Probability of requiring a test from the Microbiology lab
	 * @param prob_pat Probability of requiring a test from the Anatomopathology Lab
	 * @return A workflow for performing laboratory tests with outpatients
	 */
	public static Flow[] getOPFlow(SimulationObjectFactory factory, double prob_cent, double prob_test, double prob_hae, double prob_mic, double prob_pat) {
		Flow[] flow = new Flow[2];
		// Flow of a lab test
		// Preparation of the sample
		flow[0] = factory.getFlowInstance("SingleFlow", actOutSample);
		StructuredSynchroMergeFlow centDecision = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
		centDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actOutCent), new PercentageCondition(prob_cent * 100));
		flow[0].link(centDecision);
		// Test
		StructuredSynchroMergeFlow testDecision = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actOutTest), new PercentageCondition(prob_test * 100));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actOutHaeTest), new PercentageCondition(prob_hae * 100));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actOutMicTest), new PercentageCondition(prob_mic * 100));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actOutPatTest), new PercentageCondition(prob_pat * 100));
		centDecision.link(testDecision);
		
		flow[1] = testDecision;
		return flow;
	}

	/**
	 * Creates and returns a workflow for performing laboratory tests with inpatients.
	 * @param factory A factory for simulation components
	 * @param prob_test
	 * @param prob_hae
	 * @param prob_mic
	 * @param prob_pat
	 * @return A workflow for performing laboratory tests with outpatients
	 */
	public static Flow[] getIPFlow(SimulationObjectFactory factory, double prob_cent, double prob_test, double prob_hae, double prob_mic, double prob_pat) {
		Flow[] flow = new Flow[2];
		// Flow of a lab test
		// Preparation of the sample
		flow[0] = factory.getFlowInstance("SingleFlow", actInSample);
		StructuredSynchroMergeFlow centDecision = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
		centDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actInCent), new PercentageCondition(prob_cent * 100));
		flow[0].link(centDecision);
		// Test
		StructuredSynchroMergeFlow testDecision = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actInTest), new PercentageCondition(prob_test * 100));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actInHaeTest), new PercentageCondition(prob_hae * 100));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actInMicTest), new PercentageCondition(prob_mic * 100));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actInPatTest), new PercentageCondition(prob_pat * 100));
		centDecision.link(testDecision);
		
		flow[1] = testDecision;
		return flow;
	}
}
