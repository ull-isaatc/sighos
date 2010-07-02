/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.function.TimeFunction;
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
import es.ull.isaatc.simulation.common.condition.ElementTypeCondition;
import es.ull.isaatc.simulation.common.condition.PercentageCondition;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.flow.ExclusiveChoiceFlow;
import es.ull.isaatc.simulation.common.flow.FinalizerFlow;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.common.flow.ForLoopFlow;
import es.ull.isaatc.simulation.common.flow.InitializerFlow;
import es.ull.isaatc.simulation.common.flow.InterleavedRoutingFlow;
import es.ull.isaatc.simulation.common.flow.ProbabilitySelectionFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.StructuredSynchroMergeFlow;
import es.ull.isaatc.simulation.common.flow.SynchronizationFlow;
import es.ull.isaatc.util.WeeklyPeriodicCycle;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class StdSurgicalSubModel {
	public enum Parameters implements ModelParameterMap.ModelParameter {
		NBEDS(Integer.class, "Beds available"),
		NSBEDS(Integer.class, "Beds available for short surgeries"),
		NSURGERIES(Integer.class, "Surgeries available for the service"),
		NSURGEONS(Integer.class, "Surgeons available for the service"),
		NDOCTORS(Integer.class, "Doctors available for the service"),
		NSCRUBNURSES(Integer.class, "Scrub Nurses available for the service"),
		NSURGERY_ASSIST(Integer.class, "Surgery Assistants: since 1 can be used in two surgeries, it is considered 2 resources"),
		PROB_RAD_OP(Double.class, "Probability of performing an X-Ray test during appointments"),
		PROB_NUC_OP(Double.class, "Probability of performing a scanner test during appointments"),
		PROB_LAB_OP(Double.class, "Probability of performing a lab test during appointments"),
		PROB_LABLAB_OP(Double.class, "Probability of performing a central lab test during appointments"),
		PROB_LABHAE_OP(Double.class, "Probability of performing a Haematology lab test during appointments"),
		PROB_LABMIC_OP(Double.class, "Probability of performing a Microbiology lab test during appointments"),
		PROB_LABPAT_OP(Double.class, "Probability of performing an Anatomopathology lab test during appointments"),
		LENGTH_OP1(SimulationTimeFunction.class, "Length of 1st appointment"),
		LENGTH_OP2(SimulationTimeFunction.class, "Length of successive appointment"),
		LENGTH_POP(SimulationTimeFunction.class, "Length of post-surgery appointment"),
		LENGTH_OP2ADM(SimulationTimeFunction.class, "Length between last appointment and admission"),
		LENGTH_OP2OP(SimulationTimeFunction.class, "Time between successive appointments"),
		LENGTH_SUR2POP(SimulationTimeFunction.class, "Time between surgery and post-surgery appointment"),
		LENGTH_SUR(SimulationTimeFunction.class, "Length of surgery"),
		LENGTH_SSUR(SimulationTimeFunction.class, "Length of short surgery"),
		LENGTH_ASUR(SimulationTimeFunction.class, "Length of ambulatory surgery"),
		LENGTH_SPACU(SimulationTimeFunction.class, "Length of P.A.C.U. stay after short surgery"),
		LENGTH_APACU(SimulationTimeFunction.class, "Length of P.A.C.U. stay after ambulatory surgery"),
		LENGTH_PACU(SimulationTimeFunction.class, "Length of P.A.C.U. stay"),
		LENGTH_ICU(SimulationTimeFunction.class, "Length of I.C.U. stay"),
		LENGTH_SUR2EXIT(SimulationTimeFunction.class, "Minimum stay after surgery"),
		LENGTH_SSUR2EXIT(SimulationTimeFunction.class, "Minimum stay after short surgery"),
		PROB_ADM(Double.class, "Probability of being admitted"),
		NPATIENTS(TimeFunction.class, "Number of patients that arrives at the service"),
		INTERARRIVAL(SimulationCycle.class, "Time between successive arrivals of patients"),
		NAPATIENTS(TimeFunction.class, "Number of patients that arrives at the service for ambulatory surgery"),
		AINTERARRIVAL(SimulationCycle.class, "Time between successive arrivals of patients for ambulatory surgery"),
		NSPATIENTS(TimeFunction.class, "Number of patients that arrives at the service for short surgery"),
		SINTERARRIVAL(SimulationCycle.class, "Time between successive arrivals of patients for short surgery"),
		ITERSUCC(TimeFunction.class, "Number of successive appointments"),
		PROB_1ST_APP(Double.class, "Probability of a doctor being devoted to first appointment");
		
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
		ElementType et = factory.getElementTypeInstance(code + " Patient");
		ElementType sEt = factory.getElementTypeInstance(code + " Patient (short surgery)");
		ElementType aEt = factory.getElementTypeInstance(code + " AMB Patient");
		
		// Resource types and standard resources
		ResourceType rtBed = HospitalModelTools.createNStdMaterialResources(factory, code + " Bed", (Integer)params.get(Parameters.NBEDS)); 
		ResourceType rtSBed = HospitalModelTools.createNStdMaterialResources(factory, code + " S Bed", (Integer)params.get(Parameters.NSBEDS)); 
		ResourceType rtSurgeon = HospitalModelTools.createNStdHumanResources(factory, code + " Surgeon", (Integer)params.get(Parameters.NSURGEONS));
		ResourceType rtSurgery = HospitalModelTools.createNStdMaterialResources(factory, code + " Surgery", (Integer)params.get(Parameters.NSURGERIES));
		ResourceType rtScrubNurse = HospitalModelTools.createNStdHumanResources(factory, code + " Scrub Nurse", (Integer)params.get(Parameters.NSCRUBNURSES)); 
		ResourceType rtDoctorFirst = factory.getResourceTypeInstance(code + " Doctor 1st");
		ResourceType rtDoctorSucc = factory.getResourceTypeInstance(code + " Doctor Succ");
		ResourceType rtSurgeryAssist = factory.getResourceTypeInstance(code + " Surgery Assistant");
		
		SimulationCycle doctorFirstCycle = new SimulationWeeklyPeriodicCycle(simul.getTimeUnit(), 
				WeeklyPeriodicCycle.WEEKDAYS, HospitalModelTools.DAYSTART, 0);
		final double probFirstApp = ((Double)params.get(Parameters.PROB_1ST_APP)).doubleValue();
		long hoursFirst = Math.round(HospitalModelTools.WORKHOURS.getValue() * probFirstApp);
		SimulationCycle doctorSuccCycle = new SimulationWeeklyPeriodicCycle(simul.getTimeUnit(), 
				WeeklyPeriodicCycle.WEEKDAYS, HospitalModelTools.DAYSTART.add(new TimeStamp(TimeUnit.HOUR, hoursFirst)), 0);

		// Resources with non-standard behaviour
		final int nDoctors = ((Integer)params.get(Parameters.NDOCTORS)).intValue();
		for (int i = 0; i < nDoctors; i++) {
			Resource res = factory.getResourceInstance(code + " Doctor " + i);
			res.addTimeTableEntry(doctorFirstCycle, new TimeStamp(TimeUnit.HOUR, hoursFirst), rtDoctorFirst);
			res.addTimeTableEntry(doctorSuccCycle, new TimeStamp(TimeUnit.HOUR, HospitalModelTools.WORKHOURS.getValue() - hoursFirst), rtDoctorSucc);
		}
		final int nSurgAssist = ((Integer)params.get(Parameters.NSURGERY_ASSIST)).intValue();
		// Surgery assistants are doubled because can be used in two surgeries at the same time
		for (int i = 0; i < nSurgAssist; i++) {
			HospitalModelTools.getStdHumanResource(factory, code + " Surgery Assistant" + i + "a", rtSurgeryAssist);
			HospitalModelTools.getStdHumanResource(factory, code + " Surgery Assistant" + i + "b", rtSurgeryAssist);
		}

		// Workgroups
		WorkGroup wgBed = factory.getWorkGroupInstance(new ResourceType[] {rtBed}, new int[] {1});
		WorkGroup wgSBed = factory.getWorkGroupInstance(new ResourceType[] {rtSBed}, new int[] {1});
		WorkGroup wgDocFirst = factory.getWorkGroupInstance(new ResourceType[] {rtDoctorFirst}, new int[] {1});
		WorkGroup wgDocSucc = factory.getWorkGroupInstance(new ResourceType[] {rtDoctorSucc}, new int[] {1});
		WorkGroup wgSur = factory.getWorkGroupInstance(new ResourceType[] {rtSurgery, rtSurgeon, rtScrubNurse, rtSurgeryAssist, SurgicalSubModel.getAnaesthetistRT()}, new int[] {1, 1, 1, 1, 1});

		// Time Driven Activities
		TimeDrivenActivity actFirstApp = HospitalModelTools.createStdTimeDrivenActivity(factory, code + " 1st Out App",
				(SimulationTimeFunction)params.get(Parameters.LENGTH_OP1), wgDocFirst, true);
		TimeDrivenActivity actDelaySucc = HospitalModelTools.getDelay(factory, code + " Waiting for next appointment", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_OP2OP), false);
		TimeDrivenActivity actSuccApp = HospitalModelTools.createStdTimeDrivenActivity(factory, code + " Successive Out App",
				(SimulationTimeFunction)params.get(Parameters.LENGTH_OP2), wgDocSucc, true);
		TimeDrivenActivity actDelayPOP = HospitalModelTools.getDelay(factory, code + " Waiting for post-surgery appointment", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_SUR2POP), false);
		TimeDrivenActivity actDelayAppAdm = HospitalModelTools.getDelay(factory, code + " Waiting for admission", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_OP2ADM), false);
		TimeDrivenActivity actPostSurApp = HospitalModelTools.createStdTimeDrivenActivity(factory, code + " Post-Surgery App",
				(SimulationTimeFunction)params.get(Parameters.LENGTH_POP), wgDocSucc, true);
		TimeDrivenActivity actSurgery = HospitalModelTools.createStdTimeDrivenActivity(factory, code + " Surgery", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_SUR), wgSur, true);
		TimeDrivenActivity actShortSurgery = HospitalModelTools.createStdTimeDrivenActivity(factory, code + " Short Surgery", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_SSUR), wgSur, true);
		TimeDrivenActivity actAmbSurgery = HospitalModelTools.createStdTimeDrivenActivity(factory, code + " AMB Surgery", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_ASUR), wgSur, true);
		TimeDrivenActivity actDelayPostSur = HospitalModelTools.getDelay(factory, code + " Recovering after Surgery",
				(SimulationTimeFunction)params.get(Parameters.LENGTH_SUR2EXIT), true);
		TimeDrivenActivity actDelayPostShortSur = HospitalModelTools.getDelay(factory, code + " Recovering after Short Surgery",
				(SimulationTimeFunction)params.get(Parameters.LENGTH_SSUR2EXIT), true);

		// Flows
		// Let's do that patients can directly go to the main loop (just to avoid warmup)
		ProbabilitySelectionFlow root = (ProbabilitySelectionFlow)factory.getFlowInstance("ProbabilitySelectionFlow");
		SingleFlow first = (SingleFlow)factory.getFlowInstance("SingleFlow", actFirstApp);
		ExclusiveChoiceFlow firstDecision = (ExclusiveChoiceFlow)factory.getFlowInstance("ExclusiveChoiceFlow");
		first.link(firstDecision);
		root.link(first, 0.15);
		
		StructuredSynchroMergeFlow testDecision = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
		Flow[] labTest = CentralLabSubModel.getOutFlow(factory, (Double)params.get(Parameters.PROB_LABLAB_OP), (Double)params.get(Parameters.PROB_LABHAE_OP),
				(Double)params.get(Parameters.PROB_LABMIC_OP), (Double)params.get(Parameters.PROB_LABPAT_OP)); 
		testDecision.addBranch((InitializerFlow)labTest[0], (FinalizerFlow)labTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_LAB_OP) * 100));
		Flow[] nucTest = CentralServicesSubModel.getNuclearFlow(factory);
		testDecision.addBranch((InitializerFlow)nucTest[0], (FinalizerFlow)nucTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_NUC_OP) * 100));
		Flow[] radTest = CentralServicesSubModel.getRadiologyFlow(factory);
		testDecision.addBranch((InitializerFlow)radTest[0], (FinalizerFlow)radTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_RAD_OP) * 100));
		
		InterleavedRoutingFlow beforeSucc = (InterleavedRoutingFlow)factory.getFlowInstance("InterleavedRoutingFlow");
		beforeSucc.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actDelaySucc));
		beforeSucc.addBranch(testDecision);
		
		SingleFlow succ = (SingleFlow)factory.getFlowInstance("SingleFlow", actSuccApp);
		beforeSucc.link(succ);

		ForLoopFlow mainLoop = (ForLoopFlow)factory.getFlowInstance("ForLoopFlow", beforeSucc, succ, (TimeFunction)params.get(Parameters.ITERSUCC));
		firstDecision.link(mainLoop, new PercentageCondition(80));
		root.link(mainLoop, 0.85);
		ExclusiveChoiceFlow admittanceDecision = (ExclusiveChoiceFlow)factory.getFlowInstance("ExclusiveChoiceFlow");
		mainLoop.link(admittanceDecision);
		double percAdmission = ((Double)params.get(Parameters.PROB_ADM)).doubleValue() * 100;
		SingleFlow admission = (SingleFlow)factory.getFlowInstance("SingleFlow", actDelayAppAdm);
		admittanceDecision.link(admission, new PercentageCondition(percAdmission));
		ExclusiveChoiceFlow surgeryTypeDecision = (ExclusiveChoiceFlow)factory.getFlowInstance("ExclusiveChoiceFlow"); 
		admission.link(surgeryTypeDecision);
		
		// FIXME: 3-Phase simulation doesn't work with this
		// Flow for ordinary surgical patients
		FlowDrivenActivity actStay = factory.getFlowDrivenActivityInstance(code + " Stay in bed");
		SingleFlow surgicalPhase = (SingleFlow)factory.getFlowInstance("SingleFlow", actStay);
		SingleFlow surgery = (SingleFlow)factory.getFlowInstance("SingleFlow", actSurgery);

		SurgicalSubModel.addPACUWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_PACU), et);
		SurgicalSubModel.addICUWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_ICU), et);
		SingleFlow PACU = (SingleFlow)factory.getFlowInstance("SingleFlow", SurgicalSubModel.getActPACU());
		surgery.link(PACU);
		ProbabilitySelectionFlow afterSurgery = (ProbabilitySelectionFlow)factory.getFlowInstance("ProbabilitySelectionFlow");
		PACU.link(afterSurgery);
		SingleFlow ICU = (SingleFlow)factory.getFlowInstance("SingleFlow", SurgicalSubModel.getActICU());
		afterSurgery.link(ICU, 0.2);
		SingleFlow postICU = (SingleFlow)factory.getFlowInstance("SingleFlow", actDelayPostSur);
		ICU.link(postICU);
		SingleFlow resurgery = (SingleFlow)factory.getFlowInstance("SingleFlow", actSurgery);
		afterSurgery.link(resurgery, 0.1);
		resurgery.link(PACU);
		SingleFlow postOp = (SingleFlow)factory.getFlowInstance("SingleFlow", actDelayPostSur);
		afterSurgery.link(postOp, 0.7);
		SynchronizationFlow afterOpSync = (SynchronizationFlow)factory.getFlowInstance("SynchronizationFlow");
		postICU.link(afterOpSync);
		postOp.link(afterOpSync);
				
		actStay.addWorkGroup(surgery, afterOpSync, wgBed);
		SingleFlow delayPostSur = (SingleFlow)factory.getFlowInstance("SingleFlow", actDelayPOP);
		surgicalPhase.link(delayPostSur);
		SingleFlow postSurApp = (SingleFlow)factory.getFlowInstance("SingleFlow", actPostSurApp);
		delayPostSur.link(postSurApp);
		surgeryTypeDecision.link(surgicalPhase, new ElementTypeCondition(et));
		
//		ParallelFlow parallelStay = (ParallelFlow)factory.getFlowInstance("ParallelFlow");
//		admission.link(parallelStay);
//		parallelStay.link(stayInBed);
//
//		final Condition iPTestCond = new Condition() {
//			public boolean check(es.ull.isaatc.simulation.common.Element e) {
//				if (e.getVar("maketest").getValue().intValue() != 0)
//					return true;
//				return false;
//			}
//		};
//
//		StructuredSynchroMergeFlow iPTests = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
//		Flow[] labIPTest = CentralLabSubModel.getOutFlow(factory, (Double)params.get(Parameters.PROB_LABLAB_IP), (Double)params.get(Parameters.PROB_LABHAE_IP),
//				(Double)params.get(Parameters.PROB_LABMIC_IP), (Double)params.get(Parameters.PROB_LABPAT_IP)); 
//		iPTests.addBranch((InitializerFlow)labIPTest[0], (FinalizerFlow)labIPTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_LAB_IP) * 100));
//		Flow[] nucIPTest = CentralServicesSubModel.getNuclearFlow(factory);
//		iPTests.addBranch((InitializerFlow)nucIPTest[0], (FinalizerFlow)nucIPTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_NUC_IP) * 100));
//		Flow[] radIPTest = CentralServicesSubModel.getRadiologyFlow(factory);
//		iPTests.addBranch((InitializerFlow)radIPTest[0], (FinalizerFlow)radIPTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_RAD_IP) * 100));
//		SingleFlow waitTilNextDay = (SingleFlow)factory.getFlowInstance("SingleFlow", HospitalModelTools.getWaitTilNextDay(factory, new TimeStamp(TimeUnit.HOUR, 8)));
//		waitTilNextDay.link(iPTests);
//		WhileDoFlow iterIPTests = (WhileDoFlow)factory.getFlowInstance("WhileDoFlow", waitTilNextDay, iPTests, iPTestCond);
//		parallelStay.link(iterIPTests);
		
		// Flow for short surgical patients
		FlowDrivenActivity actShortStay = factory.getFlowDrivenActivityInstance(code + " Short stay in bed");
		SingleFlow shortSurgicalPhase = (SingleFlow)factory.getFlowInstance("SingleFlow", actShortStay);
		SingleFlow shortSurgery = (SingleFlow)factory.getFlowInstance("SingleFlow", actShortSurgery);

		SurgicalSubModel.addPACUWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_SPACU), sEt);
		SingleFlow sPACU = (SingleFlow)factory.getFlowInstance("SingleFlow", SurgicalSubModel.getActPACU());
		shortSurgery.link(sPACU);
		SingleFlow postShortSur = (SingleFlow)factory.getFlowInstance("SingleFlow", actDelayPostShortSur);
		sPACU.link(postShortSur);
				
		actShortStay.addWorkGroup(shortSurgery, postShortSur, wgSBed);
		SingleFlow delayPostShortSur = (SingleFlow)factory.getFlowInstance("SingleFlow", actDelayPOP);
		shortSurgicalPhase.link(delayPostShortSur);
		SingleFlow postShortSurApp = (SingleFlow)factory.getFlowInstance("SingleFlow", actPostSurApp);
		delayPostShortSur.link(postShortSurApp);
		surgeryTypeDecision.link(shortSurgicalPhase, new ElementTypeCondition(sEt));

		// Flow for ambulatory patients
		SingleFlow ambSurgery = (SingleFlow)factory.getFlowInstance("SingleFlow", actAmbSurgery);

		SurgicalSubModel.addPACUWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_APACU), sEt);
		SingleFlow ambPACU = (SingleFlow)factory.getFlowInstance("SingleFlow", SurgicalSubModel.getActPACU());
		ambSurgery.link(ambPACU);
				
		actShortStay.addWorkGroup(ambSurgery, ambPACU, wgSBed);
		SingleFlow delayPostAmbSur = (SingleFlow)factory.getFlowInstance("SingleFlow", actDelayPOP);
		shortSurgicalPhase.link(delayPostAmbSur);
		SingleFlow postShortAmbApp = (SingleFlow)factory.getFlowInstance("SingleFlow", actPostSurApp);
		delayPostAmbSur.link(postShortAmbApp);
		surgeryTypeDecision.link(shortSurgicalPhase, new ElementTypeCondition(sEt));
		
		ElementCreator ec = factory.getElementCreatorInstance((TimeFunction)params.get(Parameters.NPATIENTS), et, root);
		factory.getTimeDrivenGeneratorInstance(ec, (SimulationCycle)params.get(Parameters.INTERARRIVAL));

		ElementCreator sEc = factory.getElementCreatorInstance((TimeFunction)params.get(Parameters.NSPATIENTS), sEt, root);
		factory.getTimeDrivenGeneratorInstance(sEc, (SimulationCycle)params.get(Parameters.SINTERARRIVAL));

		ElementCreator aEc = factory.getElementCreatorInstance((TimeFunction)params.get(Parameters.NAPATIENTS), aEt, root);
		factory.getTimeDrivenGeneratorInstance(aEc, (SimulationCycle)params.get(Parameters.AINTERARRIVAL));
	}
}
