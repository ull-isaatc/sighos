/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import java.util.EnumSet;

import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.condition.PercentageCondition;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.StructuredSynchroMergeFlow;

/**
 * @author Iván Castilla Rodríguez
 *
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
		NSLOTS(Integer.class, "'Slots' for analytical tests"),
		LENGTH_SAMPLE(SimulationTimeFunction.class, "Duration of taking a sample"),
		LENGTH_CENT(SimulationTimeFunction.class, "Duration of centrifugation"),
		// The analysis slot is an abstraction, result of dividing the length of a test by the number of tests
		// that can be done at the same time by a tecnician.
		LENGTH_ANALYSIS(SimulationTimeFunction.class, "Duration of analysis slot"),
		NHAETECH(Integer.class, "Number of technicians from Haematology Lab"),
		NHAENURSES(Integer.class, "Number of nurses from Haematology Lab"),
		NHAESLOTS(Integer.class, "'Slots' for analytical tests from Haematology Lab"),
		LENGTH_HAEANALYSIS(SimulationTimeFunction.class, "Duration of analysis slot for Haematology Lab"),
		NMICROTECH(Integer.class, "Number of technicians from Microbiology Lab"),
		NMICRONURSES(Integer.class, "Number of nurses from Microbiology Lab"),
		NMICROSLOTS(Integer.class, "'Slots' for analytical tests from Microbiology Lab"),
		LENGTH_MICROANALYSIS(SimulationTimeFunction.class, "Duration of analysis slot from Microbiology Lab"),
		NPATTECH(Integer.class, "Number of technicians from Anatomopathology Lab"),
		NPATNURSES(Integer.class, "Number of nurses from Anatomopathology Lab"),
		NPATSLOTS(Integer.class, "'Slots' for analytical tests from Anatomopathology Lab"),
		LENGTH_PATANALYSIS(SimulationTimeFunction.class, "Duration of analysis slot from Anatomopathology Lab");
		
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
	
	public static void createModel(SimulationObjectFactory factory, ModelParameterMap params) {
		for (Parameters p : Parameters.values())
			if (params.get(p) == null)
				throw new IllegalArgumentException("Param <<" + p + ">> missing");
		
		Simulation simul = factory.getSimulation();
		
		// Resource types and standard resources
		ResourceType rtTech = HospitalModelTools.createNStdHumanResources(factory, "Lab Technician", (Integer)params.get(Parameters.NTECH)); 
		ResourceType rtSlot = HospitalModelTools.createNStdMaterialResources(factory, "Analytical slot", (Integer)params.get(Parameters.NSLOTS)); 
		final int nNurses = ((Integer)params.get(Parameters.NNURSES)).intValue();
		final int nXNurses = ((Integer)params.get(Parameters.NXNURSES)).intValue();
		ResourceType rtNurse = HospitalModelTools.createNStdHumanResources(factory, "Lab Nurse", nNurses - nXNurses);
		ResourceType rtXNurse = factory.getResourceTypeInstance("Lab Specialist Nurse");
		
		ResourceType rtHaeTech = HospitalModelTools.createNStdHumanResources(factory, "Haematology Lab Technician", (Integer)params.get(Parameters.NHAETECH)); 
		ResourceType rtHaeNurse = factory.getResourceTypeInstance("Haematology Lab Nurse"); 
		ResourceType rtHaeSlot = HospitalModelTools.createNStdMaterialResources(factory, "Haematology Lab Analytical slot", (Integer)params.get(Parameters.NHAESLOTS)); 
		ResourceType rtMicTech = HospitalModelTools.createNStdHumanResources(factory, "Microbiology Lab Technician", (Integer)params.get(Parameters.NMICROTECH)); 
		ResourceType rtMicNurse = HospitalModelTools.createNStdHumanResources(factory, "Microbiology Lab Nurse", (Integer)params.get(Parameters.NMICRONURSES));
		ResourceType rtMicSlot = HospitalModelTools.createNStdMaterialResources(factory, "Microbiology Lab Analytical slot", (Integer)params.get(Parameters.NMICROSLOTS)); 
		ResourceType rtPatTech = HospitalModelTools.createNStdHumanResources(factory, "Anatomopathology Lab Technician", (Integer)params.get(Parameters.NPATTECH)); 
		ResourceType rtPatNurse = HospitalModelTools.createNStdHumanResources(factory, "Anatomopathology Lab Nurse", (Integer)params.get(Parameters.NPATNURSES));
		ResourceType rtPatSlot = HospitalModelTools.createNStdMaterialResources(factory, "Anatomopathology Lab Analytical slot", (Integer)params.get(Parameters.NPATSLOTS)); 
		
		// Specific Resources
		final int n24HTech = ((Integer)params.get(Parameters.N24HTECH)).intValue();
		for (int i = 0; i < n24HTech; i++) {
			// Since they work 24/7, they can be considered material resources 
			HospitalModelTools.getStdMaterialResource(factory, "24/7 Lab Technician " + i, rtTech);
		}
		for (int i = 0; i < nXNurses; i++) {
			Resource res = HospitalModelTools.getStdHumanResource(factory, "Lab Specialist Nurse " + i, rtNurse);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtXNurse);
		}
		final int nHaeNurses = (Integer)params.get(Parameters.NHAENURSES);
		for (int i = 0; i < nHaeNurses; i++) {
			Resource res = HospitalModelTools.getStdHumanResource(factory, "Haematology Lab Nurse " + i, rtHaeNurse);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtNurse);
		}
			
		// Workgroups
		WorkGroup wgSample = factory.getWorkGroupInstance(new ResourceType[] {rtNurse}, new int[] {1});
		WorkGroup wgCent = factory.getWorkGroupInstance(new ResourceType[] {rtNurse}, new int[] {1});
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
		actOutTest = factory.getTimeDrivenActivityInstance("Analysis OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actOutTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_ANALYSIS), 0, wgTest1);
		actOutTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_ANALYSIS), 1, wgTest2);
		actOutHaeTest = factory.getTimeDrivenActivityInstance("Haematology Analysis OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actOutHaeTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_HAEANALYSIS), 0, wgTestHae1);
		actOutHaeTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_HAEANALYSIS), 1, wgTestHae2);
		actOutMicTest = factory.getTimeDrivenActivityInstance("Microbiology Analysis OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actOutMicTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_MICROANALYSIS), 0, wgTestMic1);
		actOutMicTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_MICROANALYSIS), 1, wgTestMic2);
		actOutPatTest = factory.getTimeDrivenActivityInstance("Anatomopathology Analysis OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actOutPatTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_PATANALYSIS), 0, wgTestPat1);
		actOutPatTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_PATANALYSIS), 1, wgTestPat2);
		
		// Creates more priority activities for inpatients
		actInSample = factory.getTimeDrivenActivityInstance("Take a sample IP", 1, EnumSet.noneOf(TimeDrivenActivity.Modifier.class));
		actInSample.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_SAMPLE), wgSample);
		actInCent = factory.getTimeDrivenActivityInstance("Centrifugation IP", 1, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actInCent.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_CENT), wgCent);
		actInTest = factory.getTimeDrivenActivityInstance("Analysis IP", 1, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actInTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_ANALYSIS), 0, wgTest1);
		actInTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_ANALYSIS), 1, wgTest2);
		actInHaeTest = factory.getTimeDrivenActivityInstance("Haematology Analysis IP", 1, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actInHaeTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_HAEANALYSIS), 0, wgTestHae1);
		actInHaeTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_HAEANALYSIS), 1, wgTestHae2);
		actInMicTest = factory.getTimeDrivenActivityInstance("Microbiology Analysis IP", 1, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actInMicTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_MICROANALYSIS), 0, wgTestMic1);
		actInMicTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_MICROANALYSIS), 1, wgTestMic2);
		actInPatTest = factory.getTimeDrivenActivityInstance("Anatomopathology Analysis IP", 1, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actInPatTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_PATANALYSIS), 0, wgTestPat1);
		actInPatTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_PATANALYSIS), 1, wgTestPat2);
	}

	/**
	 * Creates and returns a workflow for performing laboratory tests with outpatients.
	 * @param factory
	 * @param prob_test
	 * @param prob_hae
	 * @param prob_mic
	 * @param prob_pat
	 * @return a workflow for performing laboratory tests with outpatients
	 */
	public static Flow[] getOPFlow(SimulationObjectFactory factory, double prob_test, double prob_hae, double prob_mic, double prob_pat) {
		Flow[] flow = new Flow[2];
		// Flow of a lab test
		// Pre-analytical PHASE
		flow[0] = factory.getFlowInstance("SingleFlow", actOutSample);
		SingleFlow cent = (SingleFlow)factory.getFlowInstance("SingleFlow", actOutCent);
		flow[0].link(cent);
		// Analytical PHASE
		StructuredSynchroMergeFlow testDecision = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actOutTest), new PercentageCondition(prob_test * 100));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actOutHaeTest), new PercentageCondition(prob_hae * 100));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actOutMicTest), new PercentageCondition(prob_mic * 100));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actOutPatTest), new PercentageCondition(prob_pat * 100));
		cent.link(testDecision);
		
		flow[1] = testDecision;
		return flow;
	}

	/**
	 * Creates and returns a workflow for performing laboratory tests with inpatients.
	 * @param factory
	 * @param prob_test
	 * @param prob_hae
	 * @param prob_mic
	 * @param prob_pat
	 * @return a workflow for performing laboratory tests with outpatients
	 */
	public static Flow[] getIPFlow(SimulationObjectFactory factory, double prob_test, double prob_hae, double prob_mic, double prob_pat) {
		Flow[] flow = new Flow[2];
		// Flow of a lab test
		// Pre-analytical PHASE
		flow[0] = factory.getFlowInstance("SingleFlow", actInSample);
		SingleFlow cent = (SingleFlow)factory.getFlowInstance("SingleFlow", actInCent);
		flow[0].link(cent);
		// Analytical PHASE
		StructuredSynchroMergeFlow testDecision = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actInTest), new PercentageCondition(prob_test * 100));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actInHaeTest), new PercentageCondition(prob_hae * 100));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actInMicTest), new PercentageCondition(prob_mic * 100));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actInPatTest), new PercentageCondition(prob_pat * 100));
		cent.link(testDecision);
		
		flow[1] = testDecision;
		return flow;
	}
}
