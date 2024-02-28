package es.ull.iis.simulation.hta.populations;

import java.util.Arrays;

import es.ull.iis.simulation.hta.HTAExperiment;
import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.DiscreteRandomVariate;
import simkit.random.RandomVariate;

/**
 * A basic class to generate non-correlated information about patients who can either suffer or not a specific disease. During an {@link HTAExperiment experiment}, 
 * using this population ensures that the individuals created have always the same characteristics, regardless of the run.
 * TODO: Currently using arrays to store the information for each patient. In following versions, the patient generators should use the same information for the 
 * already created patients, instead of invoking this class.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class FirstOrderOnlyPopulation extends Population {
	protected final Disease disease;
	private final RandomVariate rndBaselineAge;
	private final DiscreteRandomVariate rndSex;
	private final DiscreteRandomVariate rndDisease;
	private final DiscreteRandomVariate rndDiagnosed;
    private final double[] initAges;
    private final int[] sexs;
    private final Boolean[] haveDisease;
    private final Boolean[] areDiagnosed;

    public FirstOrderOnlyPopulation(HTAModel model, String name, String description, Disease disease) throws MalformedSimulationModelException {
        super(model, name, description);
        this.disease = disease;
        this.rndBaselineAge = initBaselineAgeVariate();
        this.rndSex = initSexVariate();
        this.rndDisease = initDiseaseVariate();
        this.rndDiagnosed = initDiagnosedVariate();
        final int nPatients = model.getExperiment().getNPatients();
        this.initAges = new double[nPatients];
        Arrays.fill(this.initAges, Double.NaN);
        this.sexs = new int[nPatients];
        Arrays.fill(this.sexs, -1);
        this.haveDisease = new Boolean[nPatients];
        Arrays.fill(this.haveDisease, null);
        this.areDiagnosed = new Boolean[nPatients];
        Arrays.fill(this.areDiagnosed, null);
    }

    @Override
    public int getSex(Patient pat) {
        if (sexs[pat.getIdentifier()] == -1) {
            sexs[pat.getIdentifier()] = rndSex.generateInt();
        }
        return sexs[pat.getIdentifier()];
    }

    @Override
    public double getInitAge(Patient pat) {
        if (Double.isNaN(initAges[pat.getIdentifier()])) {
            initAges[pat.getIdentifier()] = rndBaselineAge.generate();
        }
        return initAges[pat.getIdentifier()];
    }

    @Override
    public Disease getDisease(Patient pat) {
        if (haveDisease[pat.getIdentifier()] == null) {
            haveDisease[pat.getIdentifier()] = rndDisease.generateInt() == 1;
        }
        return haveDisease[pat.getIdentifier()] ? disease : getModel().HEALTHY;
    }

    @Override
    public boolean isDiagnosedFromStart(Patient pat) {
        if (areDiagnosed[pat.getIdentifier()] == null) {
            areDiagnosed[pat.getIdentifier()] = rndDiagnosed.generateInt() == 1;
        }
        return areDiagnosed[pat.getIdentifier()];
    }

	/**
	 * Creates and returns a distribution that represents the probability of having a disease according to the population characteristics.
	 * TODO: Should be changed to a "time to disease" distribution to fully connect with the concepts of prevalence and incidence. If prevalence were
	 * used, time to would be 0 for prevalent and MAX for non-prevalent; if incidence were used, different times to event would be created. The latter
	 * would also require a new type of patient event and patient info.  
	 * @return a distribution that represents the probability of having a disease according to the population characteristics
	 */
	protected abstract DiscreteRandomVariate initDiseaseVariate();

	/**
	 * Creates and returns a distribution that should return {@link BasicConfigParams.MAN} if the patient is a male, 
	 * and {@link BasicConfigParams.WOMAN} if she is a female.
	 * @return Distribution that should return 0 if the patient is a male, and 1 if she is a female.
	 */
	protected abstract DiscreteRandomVariate initSexVariate();
	
	/**
	 * Creates and returns a distribution which returns 1 (true) if the patient will start with a diagnosis according 
	 * to the population characteristics; and 0 (false) otherwise
	 * @return a distribution which returns 1 (true) if the patient will start with a diagnosis according 
	 * to the population characteristics; and 0 (false) otherwise
	 */
	protected abstract DiscreteRandomVariate initDiagnosedVariate();	
	
	/**
	 * Creates and returns a function to assign the baseline age
	 * @return a function to assign the baseline age
	 */
	protected abstract RandomVariate initBaselineAgeVariate();
}
