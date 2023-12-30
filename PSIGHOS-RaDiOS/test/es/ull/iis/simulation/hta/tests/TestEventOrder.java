package es.ull.iis.simulation.hta.tests;

import org.junit.jupiter.api.Test;

import com.beust.jcommander.Parameter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.CommonArguments;
import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.HTAExperiment;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.DoNothingIntervention;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
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

/**
 * A class to test event order for different combination of manifestations
 */
public class TestEventOrder {
    public enum TESTS {
        ONE_CHRONIC,
        TWO_CHRONIC,
        TWO_CHRONIC_EXCLUSIVE,
        ACUTE
    }
    private final TestArguments arguments = new TestArguments();
	private final ByteArrayOutputStream baos = new ByteArrayOutputStream ();

    @BeforeEach
    public void setUp() {
        arguments.nRuns = 0;
        arguments.nPatients = 1;
        arguments.singlePatientOutput = 0;
    }

    @Test
    @DisplayName("Test the event order for a disease with an acute manifestations that appears every ten years")
    public void testEventOrderAcuteManifestations() {
        arguments.example = TESTS.ACUTE;
        try {
            final HTAExperiment exp = new BasicDiseaseExperiment(arguments, baos);
            exp.run();
            System.out.println(baos.toString());
        } catch (MalformedSimulationModelException e) {
            e.printStackTrace();
        }
    }   

    @Test
    @DisplayName("Test the event order for a disease with two exclusive chronic manifestations and a pathway between them")
    public void testEventOrderChronicManifestations() {
        arguments.example = TESTS.TWO_CHRONIC;
        try {
            final HTAExperiment exp = new BasicDiseaseExperiment(arguments, baos);
            exp.run();
            System.out.println(baos.toString());
        } catch (MalformedSimulationModelException e) {
            e.printStackTrace();
        }
    }   

    @Test
    @DisplayName("Test the event order for a disease with two exclusive chronic manifestations and a pathway between them")
    public void testEventOrderChronicExclusiveManifestations() {
        arguments.example = TESTS.TWO_CHRONIC_EXCLUSIVE;
        try {
            final HTAExperiment exp = new BasicDiseaseExperiment(arguments, baos);
            exp.run();
            System.out.println(baos.toString());
        } catch (MalformedSimulationModelException e) {
            e.printStackTrace();
        }
    }   

    @Test
    @DisplayName("Test the event order for a disease with one chronic manifestations")
    public void testEventOrderChronicManifestation() {
        arguments.example = TESTS.ONE_CHRONIC;
        try {
            final HTAExperiment exp = new BasicDiseaseExperiment(arguments, baos);
            exp.run();
            System.out.println(baos.toString());
        } catch (MalformedSimulationModelException e) {
            e.printStackTrace();
        }
    }   

    private static ArrayList<PatientEvent> getExpectedEventsForExample(TESTS example) {
        ArrayList<PatientEvent> expectedEvents = new ArrayList<PatientEvent>();
        expectedEvents.add(new PatientEvent(0, 0, PatientInfo.Type.START));
        if (example == TESTS.ONE_CHRONIC || example == TESTS.TWO_CHRONIC || example == TESTS.TWO_CHRONIC_EXCLUSIVE) {
            int year = BasicDisease.YEARS_TO_MANIFESTATION1;
            expectedEvents.add(new PatientEvent(0, TimeUnit.DAY.convert(year, TimeUnit.YEAR), PatientInfo.Type.START_MANIF));
            year += BasicDisease.YEARS_TO_MANIFESTATION2;
            if (example != TESTS.ONE_CHRONIC) {
                expectedEvents.add(new PatientEvent(0, TimeUnit.DAY.convert(year, TimeUnit.YEAR), PatientInfo.Type.START_MANIF));
                if (example == TESTS.TWO_CHRONIC_EXCLUSIVE) {
                    expectedEvents.add(new PatientEvent(0, TimeUnit.DAY.convert(year, TimeUnit.YEAR), PatientInfo.Type.END_MANIF));
                }
            }
        }
        else if (example == TESTS.ACUTE) {
            int year = BasicDisease.YEARS_AMONG_ACUTE_MANIFESTATIONS;
            while (year < BasicConfigParams.DEF_MAX_AGE - BasicConfigParams.DEF_MIN_AGE) {
                expectedEvents.add(new PatientEvent(0, TimeUnit.DAY.convert(year, TimeUnit.YEAR), PatientInfo.Type.START_MANIF));
                year += BasicDisease.YEARS_AMONG_ACUTE_MANIFESTATIONS;
            }
        }
        expectedEvents.add(new PatientEvent(0, TimeUnit.DAY.convert(BasicConfigParams.DEF_MAX_AGE - BasicConfigParams.DEF_MIN_AGE, TimeUnit.YEAR), PatientInfo.Type.DEATH));
        return expectedEvents;
    }

    public static class BasicDiseaseExperiment extends HTAExperiment {
        private final TESTS example;
        public BasicDiseaseExperiment(TestArguments arguments, ByteArrayOutputStream simResult) throws MalformedSimulationModelException {
            super(arguments, simResult);
            this.example = arguments.example;
        }

        @Override
        public ArrayList<InfoReceiver> createAdditionalListeners(int id) {
            ArrayList<InfoReceiver> listeners = new ArrayList<InfoReceiver>();
            ArrayList<PatientEvent> expectedEvents = getExpectedEventsForExample(example);
            listeners.add(new ExactOrderPatientEventChecker(expectedEvents));
            return listeners;
        }
        
        @Override
        public HTAModel createModel(CommonArguments arguments) throws MalformedSimulationModelException {
            return new BasicDiseaseModel(this, ((TestArguments)arguments).example);
        }

        /**
         * @return the example
         */
        public TESTS getExample() {
            return example;
        }
    }
    public static class BasicDiseaseModel extends HTAModel {
        private final TESTS example;

        public BasicDiseaseModel(BasicDiseaseExperiment experiment, TESTS example) {
            super(experiment);
            this.example = example;
            final Disease disease = new BasicDisease(this);
            try {
                new TestPopulation(this, disease);
            } catch (MalformedSimulationModelException e) {
                e.printStackTrace();
            }
			new DoNothingIntervention(this);
        }

        /**
         * @return the example
         */
        public TESTS getExample() {
            return example;
        }
    }

    public static class BasicDisease extends Disease {
        public static final int YEARS_AMONG_ACUTE_MANIFESTATIONS = 10;
        public static final int YEARS_TO_MANIFESTATION1 = 1;
        public static final int YEARS_TO_MANIFESTATION2 = 1;
        /**
         * @param model Repository with common information about the disease 
         */
        public BasicDisease(BasicDiseaseModel model) {
            super(model, "D0", "Test disease 0");
            if (model.getExample() == TESTS.ACUTE) {
                final DiseaseProgression acuteManif1 = new TestAcuteManifestation(model, this, "ACUTE_MANIF1", 1000, 0.2);
		        new DiseaseProgressionPathway(model, "PATH_ACUTE1", "Pathway to acute manifestation 1", acuteManif1,
			        new ConstantTimeToEventCalculator(YEARS_AMONG_ACUTE_MANIFESTATIONS));
            }
            else {
                final DiseaseProgression manif1 = new TestChronicManifestation(model, this, "MANIF1", 100, 0.2);
                new DiseaseProgressionPathway(model, "PATHWAY1",  "Pathway to chronic manifestation 1", manif1,
                    new ConstantTimeToEventCalculator(YEARS_TO_MANIFESTATION1));
                if (model.getExample() != TESTS.ONE_CHRONIC) {
                    final DiseaseProgression manif2 = new TestChronicManifestation(model, this, "MANIF2", 1000, 0.5);
                    final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new PreviousDiseaseProgressionCondition(manif1);
                    new DiseaseProgressionPathway(model, "PATHWAY1_2", "Pathway from chronic manifestaion 1 to chronic manifestation 2", manif2, 
                        new ConstantTimeToEventCalculator(YEARS_TO_MANIFESTATION2), cond); 
                    if (model.getExample() == TESTS.TWO_CHRONIC_EXCLUSIVE) {
                        addExclusion(manif2, manif1);
                    }
                }                
            }
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
        public TestChronicManifestation(BasicDiseaseModel model, Disease disease, String name, double annualCost, double disutility) {
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

    public static class TestAcuteManifestation extends DiseaseProgression {
        private final double onsetCost;
        private final double onsetDisutility;

        /**
         * @param model
         * @param disease
         */
        public TestAcuteManifestation(BasicDiseaseModel model, Disease disease, String name, double onsetCost, double onsetDisutility) {
            super(model, name, "Chronic manifestation of test disease", disease, Type.ACUTE_MANIFESTATION);
            this.onsetCost = onsetCost;
            this.onsetDisutility = onsetDisutility;
        }

        @Override
        public void createParameters() {
            addUsedParameter(StandardParameter.ONSET_COST, "", "Test", HTAModel.getStudyYear(), onsetCost);
            addUsedParameter(StandardParameter.ONSET_DISUTILITY, "", "Test", onsetDisutility);
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

    public static class TestArguments extends CommonArguments {
		@Parameter(names = { "--example", "-ex" }, description = "Example to test with", order = 3)
		public TESTS example = TESTS.ONE_CHRONIC;
	}

}
