/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.common.flow.ForLoopFlow;
import es.ull.isaatc.simulation.common.ElementCreator;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.FlowDrivenActivity;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.SimulationCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.SimulationWeeklyPeriodicCycle;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.condition.ElementTypeCondition;
import es.ull.isaatc.simulation.common.condition.PercentageCondition;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationUserCode;
import es.ull.isaatc.simulation.common.factory.UserMethod;
import es.ull.isaatc.simulation.common.flow.DoWhileFlow;
import es.ull.isaatc.simulation.common.flow.ExclusiveChoiceFlow;
import es.ull.isaatc.simulation.common.flow.FinalizerFlow;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.common.flow.InitializerFlow;
import es.ull.isaatc.simulation.common.flow.InterleavedRoutingFlow;
import es.ull.isaatc.simulation.common.flow.ProbabilitySelectionFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.StructuredSynchroMergeFlow;
import es.ull.isaatc.util.WeeklyPeriodicCycle;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class StdMedicalDptModel {
	public enum Parameters implements ModelParameterMap.ModelParameter {
		NDOCTORS(Integer.class, "Doctors available"),
		NBEDS(Integer.class, "Beds available"),
		LENGTH_OP1(SimulationTimeFunction.class, "Length of 1st appointment"),
		LENGTH_OP2(SimulationTimeFunction.class, "Length of successive appointment"),
		LENGTH_OP2OP(SimulationTimeFunction.class, "Length between successive appointments"),
		LENGTH_OP2ADM(SimulationTimeFunction.class, "Length between last appointment and admission"),
		PROB_RAD_OP(Double.class, "Probability of performing an X-Ray test during appointments"),
		PROB_NUC_OP(Double.class, "Probability of performing a scanner test during appointments"),
		PROB_LAB_OP(Double.class, "Probability of performing a lab test during appointments"),
		PROB_LABCENT_OP(Double.class, "Probability of centrifugation of sample during appointments"),
		PROB_LABLAB_OP(Double.class, "Probability of performing a central lab test during appointments"),
		PROB_LABHAE_OP(Double.class, "Probability of performing a Haematology lab test during appointments"),
		PROB_LABMIC_OP(Double.class, "Probability of performing a Microbiology lab test during appointments"),
		PROB_LABPAT_OP(Double.class, "Probability of performing an Anatomopathology lab test during appointments"),
		PROB_RAD_IP(Double.class, "Probability of performing an X-Ray test during stay"),
		PROB_NUC_IP(Double.class, "Probability of performing a scanner test during stay"),
		PROB_LAB_IP(Double.class, "Probability of performing a lab test during stay"),
		PROB_LABCENT_IP(Double.class, "Probability of centrifugation of sample during stay"),
		PROB_LABLAB_IP(Double.class, "Probability of performing a central lab test during stay"),
		PROB_LABHAE_IP(Double.class, "Probability of performing a Haematology lab test during stay"),
		PROB_LABMIC_IP(Double.class, "Probability of performing a Microbiology lab test during stay"),
		PROB_LABPAT_IP(Double.class, "Probability of performing an Anatomopathology lab test during stay"),
		LOS(SimulationTimeFunction.class, "Lenght of stay after admission"),
		PROB_ADM(Double.class, "Probability of being admitted"),
		NCPATIENTS(TimeFunction.class, "Number of chronic patients that arrives at the department"),
		NPATIENTS(TimeFunction.class, "Number of patients that arrives at the department"),
		INTERARRIVAL(SimulationCycle.class, "Time between successive arrivals of patients"),
		ITERSUCC(TimeFunction.class, "Number of successive appointments"),
		PROB_1ST_APP(Double.class, "Probability of a doctor being devoted to first appointment"),
		HOURS_INTERIPTEST(Integer.class, "Time (in hours) between successive IP tests");
		
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
	
	public static void createModel(SimulationObjectFactory factory, String code, ModelParameterMap params) {
		for (Parameters p : Parameters.values())
			if (params.get(p) == null)
				throw new IllegalArgumentException("Param <<" + p + ">> missing");

		final Simulation simul = factory.getSimulation();
		// Element types
		ElementType et = factory.getElementTypeInstance(code + " Elective Patient");
		ElementType cEt = factory.getElementTypeInstance(code + " Chronic Patient");
		et.addElementVar("maketest", 0);
		cEt.addElementVar("maketest", 0);
		
		// Resource types and standard resources
		ResourceType rtDoctorFirst = factory.getResourceTypeInstance(code + " Doctor 1st");
		ResourceType rtDoctorSucc = factory.getResourceTypeInstance(code + " Doctor Succ");
		ResourceType rtBed = HospitalModelConfig.createNStdMaterialResources(factory, code + " MED Bed", (Integer)params.get(Parameters.NBEDS));
		
		SimulationCycle doctorFirstCycle = new SimulationWeeklyPeriodicCycle(simul.getTimeUnit(), 
				WeeklyPeriodicCycle.WEEKDAYS, HospitalModelConfig.DAYSTART, 0);
		final double probFirstApp = ((Double)params.get(Parameters.PROB_1ST_APP)).doubleValue();
		long hoursFirst = Math.round(HospitalModelConfig.WORKHOURS.getValue() * probFirstApp);
		SimulationCycle doctorSuccCycle = new SimulationWeeklyPeriodicCycle(simul.getTimeUnit(), 
				WeeklyPeriodicCycle.WEEKDAYS, HospitalModelConfig.DAYSTART.add(new TimeStamp(TimeUnit.HOUR, hoursFirst)), 0);

		// Resources with non-standard behaviour
		final int nDoctors = ((Integer)params.get(Parameters.NDOCTORS)).intValue();
		for (int i = 0; i < nDoctors; i++) {
			Resource res = factory.getResourceInstance(code + " Doctor " + i);
			res.addTimeTableEntry(doctorFirstCycle, new TimeStamp(TimeUnit.HOUR, hoursFirst), rtDoctorFirst);
			res.addTimeTableEntry(doctorSuccCycle, new TimeStamp(TimeUnit.HOUR, HospitalModelConfig.WORKHOURS.getValue() - hoursFirst), rtDoctorSucc);
		}

		// Workgroups
		WorkGroup wgDocFirst = factory.getWorkGroupInstance(new ResourceType[] {rtDoctorFirst}, new int[] {1});
		WorkGroup wgDocSucc = factory.getWorkGroupInstance(new ResourceType[] {rtDoctorSucc}, new int[] {1});
		WorkGroup wgBed = factory.getWorkGroupInstance(new ResourceType[] {rtBed}, new int[] {1});

		// Time Driven Activities
		TimeDrivenActivity actFirstApp = HospitalModelConfig.createStdTimeDrivenActivity(factory, code + " 1st Out App", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_OP1), wgDocFirst, true);
		TimeDrivenActivity actDelaySucc = HospitalModelConfig.getDelay(factory, code + " Waiting for next appointment", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_OP2OP), false); 
		TimeDrivenActivity actSuccApp = HospitalModelConfig.createStdTimeDrivenActivity(factory, code + " Successive Out App", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_OP2), wgDocSucc, true);
		TimeDrivenActivity actDelayAppAdm = HospitalModelConfig.getDelay(factory, code + " Waiting for admission", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_OP2ADM), false); 
		TimeDrivenActivity actDelayStay = HospitalModelConfig.getDelay(factory, code + " Waiting for recovery", 
				(SimulationTimeFunction)params.get(Parameters.LOS), false);
		
		// Flows
		// Let's do that patients can directly go to the main loop (just to avoid warmup)
		ProbabilitySelectionFlow root = (ProbabilitySelectionFlow)factory.getFlowInstance("ProbabilitySelectionFlow");
		SingleFlow first = (SingleFlow)factory.getFlowInstance("SingleFlow", actFirstApp);
		ExclusiveChoiceFlow firstDecision = (ExclusiveChoiceFlow)factory.getFlowInstance("ExclusiveChoiceFlow");
		first.link(firstDecision);
		root.link(first, 0.15);
		
		StructuredSynchroMergeFlow oPTests = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
		Flow[] labOPTest = CentralLabModel.getOPFlow(factory, (Double)params.get(Parameters.PROB_LABCENT_OP), (Double)params.get(Parameters.PROB_LABLAB_OP), (Double)params.get(Parameters.PROB_LABHAE_OP),
				(Double)params.get(Parameters.PROB_LABMIC_OP), (Double)params.get(Parameters.PROB_LABPAT_OP)); 
		oPTests.addBranch((InitializerFlow)labOPTest[0], (FinalizerFlow)labOPTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_LAB_OP) * 100));
		Flow[] nucOPTest = CentralServicesModel.getOPNuclearFlow(factory);
		oPTests.addBranch((InitializerFlow)nucOPTest[0], (FinalizerFlow)nucOPTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_NUC_OP) * 100));
		Flow[] radOPTest = CentralServicesModel.getOPRadiologyFlow(factory);
		oPTests.addBranch((InitializerFlow)radOPTest[0], (FinalizerFlow)radOPTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_RAD_OP) * 100));
		
		InterleavedRoutingFlow beforeSucc = (InterleavedRoutingFlow)factory.getFlowInstance("InterleavedRoutingFlow");
		beforeSucc.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actDelaySucc));
		beforeSucc.addBranch(oPTests);
		
		SingleFlow succ = (SingleFlow)factory.getFlowInstance("SingleFlow", actSuccApp);
		beforeSucc.link(succ);

		ForLoopFlow mainLoop = (ForLoopFlow)factory.getFlowInstance("ForLoopFlow", beforeSucc, succ, (TimeFunction)params.get(Parameters.ITERSUCC));
		firstDecision.link(mainLoop, new PercentageCondition(80));
		root.link(mainLoop, 0.85);
		
		SingleFlow admission = (SingleFlow)factory.getFlowInstance("SingleFlow", actDelayAppAdm);
		SimulationUserCode userCode1 = new SimulationUserCode();
		userCode1.add(UserMethod.AFTER_FINALIZE, "<%SET(@E.maketest, 0)%>;");
		SingleFlow stayInBed = (SingleFlow)factory.getFlowInstance("SingleFlow", userCode1, actDelayStay);
		InterleavedRoutingFlow parallelStay = (InterleavedRoutingFlow)factory.getFlowInstance("InterleavedRoutingFlow");
		admission.link(parallelStay);
		parallelStay.addBranch(stayInBed);

		final Condition iPTestCond = new Condition() {
			public boolean check(es.ull.isaatc.simulation.common.Element e) {
				if (e.getVar("maketest").getValue().intValue() != 0)
					return true;
				return false;
			}
		};

		StructuredSynchroMergeFlow iPTests = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
		Flow[] labIPTest = CentralLabModel.getIPFlow(factory, (Double)params.get(Parameters.PROB_LABCENT_IP), (Double)params.get(Parameters.PROB_LABLAB_IP), (Double)params.get(Parameters.PROB_LABHAE_IP),
				(Double)params.get(Parameters.PROB_LABMIC_IP), (Double)params.get(Parameters.PROB_LABPAT_IP)); 
		iPTests.addBranch((InitializerFlow)labIPTest[0], (FinalizerFlow)labIPTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_LAB_IP) * 100));
		Flow[] nucIPTest = CentralServicesModel.getOPNuclearFlow(factory);
		iPTests.addBranch((InitializerFlow)nucIPTest[0], (FinalizerFlow)nucIPTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_NUC_IP) * 100));
		Flow[] radIPTest = CentralServicesModel.getOPRadiologyFlow(factory);
		iPTests.addBranch((InitializerFlow)radIPTest[0], (FinalizerFlow)radIPTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_RAD_IP) * 100));
		int hours = (Integer)params.get(Parameters.HOURS_INTERIPTEST);
		SingleFlow waitTilNext = (SingleFlow)factory.getFlowInstance("SingleFlow", HospitalModelConfig.getWaitTilNext(factory, code + " Wait until next test", 
				new TimeStamp(TimeUnit.HOUR, hours), TimeStamp.getZero()));
		waitTilNext.link(iPTests);
		DoWhileFlow iterIPTests = (DoWhileFlow)factory.getFlowInstance("DoWhileFlow", waitTilNext, iPTests, iPTestCond);
		parallelStay.addBranch(iterIPTests);

		FlowDrivenActivity actStay = (FlowDrivenActivity)factory.getFlowDrivenActivityInstance(code + " Being in bed");
		actStay.addWorkGroup(admission, parallelStay, wgBed);
		SimulationUserCode userCode2 = new SimulationUserCode();
		userCode2.add(UserMethod.AFTER_START, "<%SET(@E.maketest, 1)%>;");
		SingleFlow stay = (SingleFlow)factory.getFlowInstance("SingleFlow", userCode2, actStay);
		
		StructuredSynchroMergeFlow admittanceDecision = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
		mainLoop.link(admittanceDecision);
		double percAdmission = ((Double)params.get(Parameters.PROB_ADM)).doubleValue() * 100;
		admittanceDecision.addBranch(stay, new PercentageCondition(percAdmission));

		// Chronic patients start main loop again
		ExclusiveChoiceFlow chronicDecision = (ExclusiveChoiceFlow)factory.getFlowInstance("ExclusiveChoiceFlow"); 
		chronicDecision.link(mainLoop, new ElementTypeCondition(cEt));
		admittanceDecision.link(chronicDecision);
		
		// Patients
		TimeFunction nPatients = (TimeFunction)params.get(Parameters.NPATIENTS);
		ElementCreator ec = factory.getElementCreatorInstance(nPatients, et, root);
		SimulationCycle interArrival = (SimulationCycle)params.get(Parameters.INTERARRIVAL);
		factory.getTimeDrivenGeneratorInstance(ec, interArrival);

		TimeFunction ncPatients = (TimeFunction)params.get(Parameters.NCPATIENTS);
		ElementCreator cEc = factory.getElementCreatorInstance(ncPatients, cEt, root);
		SimulationCycle interCArrival = (SimulationCycle)params.get(Parameters.INTERARRIVAL);
		factory.getTimeDrivenGeneratorInstance(cEc, interCArrival);
	}
}
