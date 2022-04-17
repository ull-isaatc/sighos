/**
 * 
 */
package es.ull.iis.simulation.hta.radios;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import jakarta.xml.bind.JAXBException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.Development;
import es.ull.iis.ontology.radios.json.schema4simulation.Intervention;
import es.ull.iis.ontology.radios.json.schema4simulation.Schema4Simulation;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseCostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseUtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.populations.Population;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;
import es.ull.iis.simulation.hta.progression.StandardDisease;
import es.ull.iis.simulation.hta.radios.exceptions.TransformException;
import es.ull.iis.simulation.hta.radios.transforms.ValueTransform;
import es.ull.iis.simulation.hta.radios.utils.CostUtils;
import es.ull.iis.simulation.hta.radios.wrappers.Matrix;
import es.ull.iis.simulation.hta.simpletest.NullIntervention;

/**
 * @author David Prieto Gonzï¿½lez
 */
public class RadiosRepository extends SecondOrderParamsRepository {
	private ObjectMapper mapper; 
	private CostCalculator costCalc;
	private UtilityCalculator utilCalc;
	private Matrix costTreatments;
	private Matrix costFollowUps;
	private Matrix costScreenings;
	private Matrix costClinicalDiagnosis;

	/**
	 * For a repository, it is necessary to register the population {registerPopulation(...)}, the disease {registerDisease(...)}, the 
	 * interventions {registerIntervention(...)} and the submodel of death {registerDeathSubmodel(...)}, as the most relevant things.
	 * 
	 * @param nRuns
	 * @param nPatients
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws JAXBException 
	 * @throws TransformException 
	 */
	public RadiosRepository(int nRuns, int nPatients, Schema4Simulation radiosDiseaseInstance, Integer timeHorizont, boolean allAffected, List<String> intenvetionsToCompare) 
			throws JsonParseException, JsonMappingException, MalformedURLException, IOException, TransformException, JAXBException {
		super(nRuns, nPatients);

		initialize(nRuns, nPatients, radiosDiseaseInstance, timeHorizont, allAffected, intenvetionsToCompare);
	}
	
	/**
	 * For a repository, it is necessary to register the population {registerPopulation(...)}, the disease {registerDisease(...)}, the 
	 * interventions {registerIntervention(...)} and the submodel of death {registerDeathSubmodel(...)}, as the most relevant things.
	 * 
	 * @param nRuns
	 * @param nPatients
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws JAXBException 
	 * @throws TransformException 
	 */
	public RadiosRepository(int nRuns, int nPatients, String pathToRaDiOSJson, Integer timeHorizont, boolean allAffected, List<String> intenventionsToCompare) 
			throws JsonParseException, JsonMappingException, MalformedURLException, IOException, TransformException, JAXBException {
		super(nRuns, nPatients);

		mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).setSerializationInclusion(Include.NON_NULL).setSerializationInclusion(Include.NON_EMPTY);
		Schema4Simulation radiosDiseaseInstance = mapper.readValue(new File(pathToRaDiOSJson), Schema4Simulation.class);

		initialize(nRuns, nPatients, radiosDiseaseInstance, timeHorizont, allAffected, intenventionsToCompare);
	}
	
	/**
	 * @param nRuns
	 * @param nPatients
	 * @param radiosDiseaseInstance
	 * @param timeHorizon
	 * @throws TransformException
	 * @throws JAXBException
	 */
	private void initialize (int nRuns, int nPatients, Schema4Simulation radiosDiseaseInstance, Integer timeHorizon, boolean allAffected, List<String> intenventionsToCompare) 
		throws TransformException, JAXBException {
		costCalc = new DiseaseCostCalculator(this);
		utilCalc = new DiseaseUtilityCalculator(this, DisutilityCombinationMethod.ADD, BasicConfigParams.DEF_U_GENERAL_POP);
		
		final es.ull.iis.ontology.radios.json.schema4simulation.Disease diseaseJSON = radiosDiseaseInstance.getDisease(); 

		StandardDisease disease = null;
		String naturalDevelopmentName = null;

		if (diseaseJSON.getDevelopments() != null) {
			List<Development> developments = diseaseJSON.getDevelopments();
			Development naturalDevelopment = developments.stream()
					.filter(development -> Constants.DATAPROPERTYVALUE_KIND_DEVELOPMENT_NATURAL_VALUE.equals(development.getKind()))
					.findFirst()
					.orElse(null);
			naturalDevelopmentName = naturalDevelopment.getName();
			if (naturalDevelopment != null && CollectionUtils.notIsEmpty(naturalDevelopment.getManifestations())) {
				initializeCostMatrix(naturalDevelopment, diseaseJSON, timeHorizon);

				List<es.ull.iis.ontology.radios.json.schema4simulation.Manifestation> manifestations = naturalDevelopment.getManifestations();
				disease = DiseaseFactory.getDiseaseInstance(this, diseaseJSON, naturalDevelopment.getManifestations(), timeHorizon);
			} else {
				throw new TransformException("ERROR => The selected disease has no associated natural development. The specification of this development is mandatory.");
			}
		}
		
		registerDisease(HEALTHY);
		registerDisease(disease);

		Population population = new RadiosPopulation(this, disease, ValueTransform.splitProbabilityDistribution(radiosDiseaseInstance.getDisease().getBirthPrevalence()), allAffected);
		registerPopulation(population);
		
		if (CollectionUtils.notIsEmpty(radiosDiseaseInstance.getDisease().getInterventions())) {
			for (Intervention intervention : radiosDiseaseInstance.getDisease().getInterventions()) {
				if (intenventionsToCompare.contains(intervention.getName())) {
					if (Constants.DATAPROPERTYVALUE_KIND_INTERVENTION_SCREENING_VALUE.equalsIgnoreCase(intervention.getKind())) {
						RadiosScreeningIntervention radiosIntervention = new RadiosScreeningIntervention(this, intervention, naturalDevelopmentName, timeHorizon, 
								this.costTreatments, this.costFollowUps, this.costScreenings, this.costClinicalDiagnosis, disease); 
						this.registerIntervention(radiosIntervention);
					} else {
						RadiosBasicIntervention radiosIntervention = new RadiosBasicIntervention(this, intervention, naturalDevelopmentName, timeHorizon, 
								this.costTreatments, this.costFollowUps, this.costScreenings, this.costClinicalDiagnosis, disease); 
						this.registerIntervention(radiosIntervention);
					}
				}
			}
			if (intenventionsToCompare == null || intenventionsToCompare.contains(Constants.CONSTANT_DO_NOTHING)) {
				this.registerIntervention(new NullIntervention(this));
			}
		}

		// El submodelo de mortalidad (por defecto podemos usar el que te pongo)
		registerDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
	}

	/**
	 * Initializes the cost matrix for the follow-up tests associated with the disease
	 */
	private void initializeCostMatrix(Development naturalDevelopment, es.ull.iis.ontology.radios.json.schema4simulation.Disease diseaseJSON, Integer timeHorizon) {
		this.costTreatments = new Matrix();
		this.costFollowUps = new Matrix();
		this.costScreenings = new Matrix();
		this.costClinicalDiagnosis = new Matrix();

		CostUtils.loadCostFromScreeningStrategies(this.costScreenings, diseaseJSON.getScreeningStrategies(), timeHorizon);
		CostUtils.loadCostFromClinicalDiagnosisStrategies(this.costClinicalDiagnosis, diseaseJSON.getClinicalDiagnosisStrategies(), timeHorizon);
		CostUtils.loadCostFromTreatmentStrategies(this.costTreatments, naturalDevelopment.getName(), diseaseJSON.getTreatmentStrategies(), timeHorizon);
		CostUtils.loadCostFromFollowUpStrategies(this.costFollowUps, naturalDevelopment.getName(), diseaseJSON.getFollowUpStrategies(), timeHorizon);
		List<es.ull.iis.ontology.radios.json.schema4simulation.Manifestation> manifestations = naturalDevelopment.getManifestations();
		for (es.ull.iis.ontology.radios.json.schema4simulation.Manifestation manifestation : manifestations) {
			// TODO: actualizar la matriz de costos directos asociados a cada manifestación. Para el caso de estudio todos los costos vienen asociados por tratamiento. 
			CostUtils.loadCostFromTreatmentStrategies(this.costTreatments, manifestation.getName(), manifestation.getTreatmentStrategies(), timeHorizon);
			CostUtils.loadCostFromFollowUpStrategies(this.costFollowUps, manifestation.getName(), manifestation.getFollowUpStrategies(), timeHorizon);
		}
	}

	@Override
	public CostCalculator getCostCalculator() {
		return costCalc;
	}

	@Override
	public UtilityCalculator getUtilityCalculator() {
		return utilCalc;
	}

	public SecondOrderParamsRepository configDiseaseUtilityCalculator (DisutilityCombinationMethod disutilityCombinationMethod, double value) {
		utilCalc = new DiseaseUtilityCalculator(this, disutilityCombinationMethod, value);
		return this;
	}
	
	public Matrix getCostTreatments() {
		return costTreatments;
	}

	public Matrix getCostFollowUps() {
		return costFollowUps;
	}

	public Matrix getCostScreenings() {
		return costScreenings;
	}

	public Matrix getCostClinicalDiagnosis() {
		return costClinicalDiagnosis;
	}
}
