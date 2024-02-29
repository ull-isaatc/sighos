package es.ull.iis.simulation.hta.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.beust.jcommander.Parameter;

import es.ull.iis.simulation.hta.CommonArguments;
import es.ull.iis.simulation.hta.HTAExperiment;
import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.DoNothingIntervention;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.populations.Population;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;
import es.ull.iis.simulation.hta.progression.calculator.ConstantTimeToEventCalculator;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.InfoReceiver;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.TimeUnit;

public class TestDeathSubmodel {
    public enum TESTS {
        NATURAL_DEATH,
        DEATH_BY_ACUTE,
        DEATH_BY_CHRONIC_LER,
        DEATH_BY_CHRONIC_IMR
    }
    private final TestArguments arguments = new TestArguments();
	private final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
    private static final double DEF_LER = 10;
    private static final double DEF_IMR = 2;

    @BeforeEach
    public void setUp() {
        arguments.nRuns = 0;
        arguments.nPatients = 1;
        arguments.singlePatientOutput = 0;
    }

    @Test
    @DisplayName("Test the death in absence of complications")
    public void testNaturalDeath() {
        arguments.example = TESTS.NATURAL_DEATH;
        try {
            final HTAExperiment exp = new BasicDiseaseExperiment(arguments, baos);
            exp.run();
            System.out.println(baos.toString());
        } catch (MalformedSimulationModelException e) {
            e.printStackTrace();
        }
    }   

    @Test
    @DisplayName("Test the death with an acute manifestation that produces death after " + BasicDisease.YEARS_AMONG_ACUTE_MANIFESTATIONS + " years of evolution")
    public void testDeathByAcuteEvent() {
        arguments.example = TESTS.DEATH_BY_ACUTE;
        try {
            final HTAExperiment exp = new BasicDiseaseExperiment(arguments, baos);
            exp.run();
            System.out.println(baos.toString());
        } catch (MalformedSimulationModelException e) {
            e.printStackTrace();
        }
    }   

    @Test
    @DisplayName("Test the death with a chronic manifestation that reduces life expectancy by " + TestDeathSubmodel.DEF_LER + " years")
    public void testDeathByLER() {
        arguments.example = TESTS.DEATH_BY_CHRONIC_LER;
        try {
            final HTAExperiment exp = new BasicDiseaseExperiment(arguments, baos);
            exp.run();
            System.out.println(baos.toString());
        } catch (MalformedSimulationModelException e) {
            e.printStackTrace();
        }
    }   


    @Test
    @DisplayName("Test the death with a chronic manifestation that increases mortality rate by " + TestDeathSubmodel.DEF_IMR)
    public void testDeathByIMR() {
        arguments.example = TESTS.DEATH_BY_CHRONIC_IMR;
        try {
            final HTAExperiment exp = new BasicDiseaseExperiment(arguments, baos);
            exp.run();
            System.out.println(baos.toString());
        } catch (MalformedSimulationModelException e) {
            e.printStackTrace();
        }
    }   


    public static class BasicDiseaseExperiment extends HTAExperiment {
        private final TESTS example;
        private final long expectedDeathTs;
        public BasicDiseaseExperiment(TestArguments arguments, ByteArrayOutputStream simResult) throws MalformedSimulationModelException {
            super(arguments, simResult);
            this.example = arguments.example; 
            double lifeExpectancy = Population.DEF_MAX_AGE - Population.DEF_MIN_AGE;
            switch (example) {
                case DEATH_BY_ACUTE:
                    lifeExpectancy = BasicDisease.YEARS_AMONG_ACUTE_MANIFESTATIONS;
                    break;
                case DEATH_BY_CHRONIC_LER:
                    lifeExpectancy -= TestDeathSubmodel.DEF_LER;
                    break;
                case DEATH_BY_CHRONIC_IMR:
                    lifeExpectancy = BasicDisease.YEARS_TO_MANIFESTATION1 + (lifeExpectancy - BasicDisease.YEARS_TO_MANIFESTATION1) / TestDeathSubmodel.DEF_IMR;
                    break;
                case NATURAL_DEATH:
                default:
                    break;
            }
            expectedDeathTs = getModel().getSimulationTimeUnit().convert(lifeExpectancy, TimeUnit.YEAR);
        }

        @Override
        public ArrayList<InfoReceiver> createAdditionalListeners(int id) {
            ArrayList<InfoReceiver> listeners = new ArrayList<InfoReceiver>();
            listeners.add(new DeathEventChecker(expectedDeathTs));
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
        private DiseaseProgression acuteManif1 = null;
        private DiseaseProgression manif1 = null;

        /**
         * @param model Repository with common information about the disease 
         */
        public BasicDisease(BasicDiseaseModel model) {
            super(model, "D0", "Test disease 0");
            if (!TESTS.NATURAL_DEATH.equals(model.getExample())) {
                if (TESTS.DEATH_BY_ACUTE.equals(model.getExample())) {
                    acuteManif1 = new TestAcuteManifestation(model, this, "ACUTE_MANIF1");
                    new DiseaseProgressionPathway(model, "PATH_ACUTE1", "Pathway to acute manifestation 1", acuteManif1,
                        new ConstantTimeToEventCalculator(YEARS_AMONG_ACUTE_MANIFESTATIONS));
                }
                else if (TESTS.DEATH_BY_CHRONIC_IMR.equals(model.getExample())) {
                    manif1 = new TestChronicManifestation(model, this, "MANIF1", true);
                    new DiseaseProgressionPathway(model, "PATHWAY1",  "Pathway to chronic manifestation 1", manif1,
                        new ConstantTimeToEventCalculator(YEARS_TO_MANIFESTATION1));
                }
                else if (TESTS.DEATH_BY_CHRONIC_LER.equals(model.getExample())) {
                    manif1 = new TestChronicManifestation(model, this, "MANIF1", false);
                    new DiseaseProgressionPathway(model, "PATHWAY1",  "Pathway to chronic manifestation 1", manif1,
                        new ConstantTimeToEventCalculator(YEARS_TO_MANIFESTATION1));
                }
        }
            // if (TESTS.ACUTE.equals(model.getExample()) || TESTS.ACUTE_DEATH.equals(model.getExample())) {
            // }
            // else {
            //     manif1 = new TestChronicManifestation(model, this, "MANIF1", 100, 0.2);
            //     new DiseaseProgressionPathway(model, "PATHWAY1",  "Pathway to chronic manifestation 1", manif1,
            //         new ConstantTimeToEventCalculator(YEARS_TO_MANIFESTATION1));
            //     if (!TESTS.ONE_CHRONIC.equals(model.getExample())) {
            //         manif2 = new TestChronicManifestation(model, this, "MANIF2", 1000, 0.5);
            //         final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new PreviousDiseaseProgressionCondition(manif1);
            //         new DiseaseProgressionPathway(model, "PATHWAY1_2", "Pathway from chronic manifestaion 1 to chronic manifestation 2", manif2, 
            //             new ConstantTimeToEventCalculator(YEARS_TO_MANIFESTATION2), cond); 
            //         if (TESTS.TWO_CHRONIC_EXCLUSIVE.equals(model.getExample())) {
            //             addExclusion(manif2, manif1);
            //         }
            //     }                
            // }
        }

        @Override
        public void createParameters() {
        }
    }

    public static class TestChronicManifestation extends DiseaseProgression {
        private static final double DEF_ANNUAL_COST = 100;
        private static final double DEF_DISUTILITY = 0.2;
        private final boolean applyIMRorLER;

        /**
         * @param model
         * @param disease
         */
        public TestChronicManifestation(BasicDiseaseModel model, Disease disease, String name, boolean applyIMRorLER) {
            super(model, name, "Chronic manifestation of test disease with " + (applyIMRorLER ? "IMR" : "LER"), disease, Type.CHRONIC_MANIFESTATION);
            this.applyIMRorLER = applyIMRorLER;
        }

        @Override
        public void createParameters() {
            addUsedParameter(StandardParameter.ANNUAL_COST, "", "Test", HTAModel.getStudyYear(), DEF_ANNUAL_COST);
            addUsedParameter(StandardParameter.ANNUAL_DISUTILITY, "", "Test", DEF_DISUTILITY);
            if (applyIMRorLER)
                addUsedParameter(StandardParameter.INCREASED_MORTALITY_RATE, "", "Test", TestDeathSubmodel.DEF_IMR);
            else
                addUsedParameter(StandardParameter.LIFE_EXPECTANCY_REDUCTION, "", "Test", TestDeathSubmodel.DEF_LER);
        }

    }

    public static class TestAcuteManifestation extends DiseaseProgression {
        private static final double DEF_ONSET_COST = 1000;
        private static final double DEF_ONSET_DISUTILITY = 0.2;

        /**
         * @param model
         * @param disease
         */
        public TestAcuteManifestation(BasicDiseaseModel model, Disease disease, String name) {
            super(model, name, "Acute manifestation of test disease", disease, Type.ACUTE_MANIFESTATION);
        }

        @Override
        public void createParameters() {
            addUsedParameter(StandardParameter.ONSET_COST, "", "Test", HTAModel.getStudyYear(), DEF_ONSET_COST);
            addUsedParameter(StandardParameter.ONSET_DISUTILITY, "", "Test", DEF_ONSET_DISUTILITY);
            addUsedParameter(StandardParameter.DISEASE_PROGRESSION_RISK_OF_DEATH, "Death by acute manifestation", 
                "Test", 1.0);
        }

    }

    public static class DeathEventChecker extends Listener {
        private final long expectedDeathTs;
        private boolean checked;
        public DeathEventChecker(long expectedDeathTs) {
            super("Disease 0 listener");
            this.expectedDeathTs = expectedDeathTs;
            this.checked = false;
	    	addGenerated(PatientInfo.class);
    		addEntrance(PatientInfo.class);
            addEntrance(SimulationStartStopInfo.class);
        }

        @Override
        public void infoEmited(SimulationInfo info) {
            if (info instanceof PatientInfo) {
                final PatientInfo pInfo = (PatientInfo) info;
                if (PatientInfo.Type.DEATH.equals(pInfo.getType())) {
                    assertEquals(expectedDeathTs, pInfo.getTs(), "Unexpected death time");
                    checked = true;
                }
            }
            else if (info instanceof SimulationStartStopInfo) {
                if (((SimulationStartStopInfo) info).getType() == SimulationStartStopInfo.Type.END) {
                    assertTrue(checked, "Death event not found");
                }
            }
        }
    }

    public static class TestArguments extends CommonArguments {
		@Parameter(names = { "--example", "-ex" }, description = "Example to test with", order = 3)
		public TESTS example = TESTS.NATURAL_DEATH;
	}
    
}
