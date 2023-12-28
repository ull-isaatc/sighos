package es.ull.iis.simulation.hta.tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.CommonArguments;
import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.HTAExperiment;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.DoNothingIntervention;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;
import es.ull.iis.simulation.hta.progression.calculator.ConstantTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.condition.PreviousDiseaseProgressionCondition;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.InfoReceiver;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.TimeUnit;

public class TestDisease {

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testChronicManifestation() {
		final CommonArguments arguments = new CommonArguments();
        arguments.nRuns = 0;
        arguments.nPatients = 1;
        arguments.singlePatientOutput = 0;
		final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        try {
            final HTAExperiment exp = new BasicDiseaseExperiment(arguments, baos);
            exp.run();
            System.out.println(baos.toString());
        } catch (MalformedSimulationModelException e) {
            e.printStackTrace();
        }
    }   

    public static class BasicDiseaseExperiment extends HTAExperiment {
        public BasicDiseaseExperiment(CommonArguments arguments, ByteArrayOutputStream simResult) throws MalformedSimulationModelException {
            super(arguments, simResult);
        }

        @Override
        public ArrayList<InfoReceiver> createAdditionalListeners(int id) {
            ArrayList<InfoReceiver> listeners = new ArrayList<InfoReceiver>();
            ArrayList<PatientEvent> expectedEvents = new ArrayList<PatientEvent>();
            expectedEvents.add(new PatientEvent(0, 0, PatientInfo.Type.START));
            expectedEvents.add(new PatientEvent(0, TimeUnit.DAY.convert(1, TimeUnit.YEAR), PatientInfo.Type.START_MANIF));
            expectedEvents.add(new PatientEvent(0, TimeUnit.DAY.convert(2, TimeUnit.YEAR), PatientInfo.Type.START_MANIF));
            expectedEvents.add(new PatientEvent(0, TimeUnit.DAY.convert(2, TimeUnit.YEAR), PatientInfo.Type.END_MANIF));
            expectedEvents.add(new PatientEvent(0, TimeUnit.DAY.convert(82, TimeUnit.YEAR), PatientInfo.Type.DEATH));
            listeners.add(new ExactOrderPatientEventChecker(expectedEvents));
            return listeners;
        }
        @Override
        public HTAModel createModel(CommonArguments arguments) throws MalformedSimulationModelException {
            return new BasicDiseaseModel(this);
        }
    }
    public static class BasicDiseaseModel extends HTAModel {
        public BasicDiseaseModel(HTAExperiment experiment) {
            super(experiment);
            final Disease disease = new Disease0(this);
            try {
                new TestPopulation(this, disease);
            } catch (MalformedSimulationModelException e) {
                e.printStackTrace();
            }
			new DoNothingIntervention(this);
        }
    }

    public static class Disease0 extends Disease {
        /**
         * @param model Repository with common information about the disease 
         */
        public Disease0(HTAModel model) {
            super(model, "D0", "Test disease 0");
            final DiseaseProgression manif1 = new TestChronicManifestation(model, this, "MANIF1", 100, 0.2);
            final DiseaseProgression manif2 = new TestChronicManifestation(model, this, "MANIF2", 1000, 0.5);
            new DiseaseProgressionPathway(model, "PATHWAY1",  "Pathway to chronic manifestation 1", manif1,
                new ConstantTimeToEventCalculator(1.0));
            final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new PreviousDiseaseProgressionCondition(manif1);
            new DiseaseProgressionPathway(model, "PATHWAY1_2", "Pathway from chronic manifestaion 1 to chronic manifestation 2", manif2, 
                new ConstantTimeToEventCalculator(1.0), cond); 
            addExclusion(manif2, manif1);
        }

        @Override
        public void createParameters() {
        }
    }

    public static class TestChronicManifestation extends DiseaseProgression {
        private final double annualCost;
        private final double disutility;

        /**
         * @param model
         * @param disease
         */
        public TestChronicManifestation(HTAModel model, Disease disease, String name, double annualCost, double disutility) {
            super(model, name, "Chronic manifestation of test disease", disease, Type.CHRONIC_MANIFESTATION);
            this.annualCost = annualCost;
            this.disutility = disutility;
        }

        @Override
        public void createParameters() {
            addUsedParameter(StandardParameter.ANNUAL_COST, "", "Test", HTAModel.getStudyYear(), annualCost);
            addUsedParameter(StandardParameter.ANNUAL_DISUTILITY, "", "Test", disutility);
        }

    }

    public static class PatientEvent {
        private final long ts;
        private final PatientInfo.Type type;
        private final int patientId;

        public PatientEvent(int patientId, long ts, PatientInfo.Type type) {
            this.ts = ts;
            this.type = type;
            this.patientId = patientId;
        }
        /**
         * @return the ts
         */
        public long getTs() {
            return ts;
        }
        /**
         * @return the type
         */
        public PatientInfo.Type getType() {
            return type;
        }
        /**
         * @return the patientId
         */
        public int getPatientId() {
            return patientId;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof PatientEvent) {
                final PatientEvent ev = (PatientEvent) obj;
                return ev.getPatientId() == patientId && ev.getTs() == ts && ev.getType().equals(type);
            }
            else if (obj instanceof PatientInfo) {
                final PatientInfo pInfo = (PatientInfo) obj;
                return pInfo.getPatient().getIdentifier() == patientId && pInfo.getTs() == ts && pInfo.getType().equals(type);
            }
            return false;
        }

        @Override
        public String toString() {
            return ts + " Days\t[PAT" + patientId + "]\t" + type;
        }
    }

    public static class ExactOrderPatientEventChecker extends Listener {
        private final ArrayList<PatientEvent> expectedEvents;
        public ExactOrderPatientEventChecker(ArrayList<PatientEvent> expectedEvents) {
            super("Disease 0 listener");
            this.expectedEvents = expectedEvents;
	    	addGenerated(PatientInfo.class);
    		addEntrance(PatientInfo.class);
            addEntrance(SimulationStartStopInfo.class);
        }

        @Override
        public void infoEmited(SimulationInfo info) {
            if (info instanceof PatientInfo) {
                assertNotEquals(0, expectedEvents.size(), "Unexpected event: " + info);
                final PatientEvent ev = expectedEvents.remove(0);
                final PatientInfo pInfo = (PatientInfo) info;
                assertEquals(ev, pInfo);
            }
            else if (info instanceof SimulationStartStopInfo) {
                if (((SimulationStartStopInfo) info).getType() == SimulationStartStopInfo.Type.END) {
                    assertEquals(0, expectedEvents.size(), "Missing events: " + expectedEvents.size());
                }
            }
        }
    }
}
