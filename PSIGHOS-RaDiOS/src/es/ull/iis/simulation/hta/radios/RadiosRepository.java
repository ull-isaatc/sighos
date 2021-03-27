/**
 * 
 */
package es.ull.iis.simulation.hta.radios;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import es.ull.iis.ontology.radios.Constants;
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
import es.ull.iis.simulation.hta.radios.exceptions.TransformException;
import es.ull.iis.simulation.hta.simpletest.NullIntervention;
import es.ull.iis.simulation.hta.simpletest.TestNotDiagnosedPopulation;

/**
 * @author David Prieto González
 */
public class RadiosRepository extends SecondOrderParamsRepository {
	private ObjectMapper mapper; 
	private CostCalculator costCalc;
	private UtilityCalculator utilCalc;

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
	public RadiosRepository(int nRuns, int nPatients, Schema4Simulation radiosDiseaseInstance, Integer timeHorizont) throws JsonParseException, JsonMappingException, MalformedURLException, IOException, TransformException, JAXBException {
		super(nRuns, nPatients);

		initialize(nRuns, nPatients, radiosDiseaseInstance, timeHorizont);
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
	public RadiosRepository(int nRuns, int nPatients, String pathToRaDiOSJson, Integer timeHorizont) throws JsonParseException, JsonMappingException, MalformedURLException, IOException, TransformException, JAXBException {
		super(nRuns, nPatients);

		mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).setSerializationInclusion(Include.NON_NULL).setSerializationInclusion(Include.NON_EMPTY);
		Schema4Simulation radiosDiseaseInstance = mapper.readValue(new File(pathToRaDiOSJson), Schema4Simulation.class);

		initialize(nRuns, nPatients, radiosDiseaseInstance, timeHorizont);
	}
	
	/**
	 * @param nRuns
	 * @param nPatients
	 * @param radiosDiseaseInstance
	 * @param timeHorizont
	 * @throws TransformException
	 * @throws JAXBException
	 */
	private void initialize (int nRuns, int nPatients, Schema4Simulation radiosDiseaseInstance, Integer timeHorizont) throws TransformException, JAXBException {
		costCalc = new DiseaseCostCalculator(this);
		utilCalc = new DiseaseUtilityCalculator(this, DisutilityCombinationMethod.ADD, BasicConfigParams.DEF_U_GENERAL_POP);

		RadiosDisease disease = new RadiosDisease(this, radiosDiseaseInstance.getDisease(), timeHorizont);		
		registerDisease(disease);

		Population population = new TestNotDiagnosedPopulation(this, disease);
		registerPopulation(population);
		
		if (CollectionUtils.notIsEmpty(radiosDiseaseInstance.getDisease().getInterventions())) {
			for (Intervention intervention : radiosDiseaseInstance.getDisease().getInterventions()) {
				if (Constants.DATAPROPERTYVALUE_KIND_INTERVENTION_SCREENING_VALUE.equalsIgnoreCase(intervention.getKind())) {
					RadiosScreeningIntervention radiosIntervention = new RadiosScreeningIntervention(this, intervention, disease.getNaturalDevelopmentName(), timeHorizont, 
							disease.getCostTreatments(), disease.getCostFollowUps(), disease.getCostScreenings(), disease.getCostClinicalDiagnosis()); 
					this.registerIntervention(radiosIntervention);
				} else {
					RadiosBasicIntervention radiosIntervention = new RadiosBasicIntervention(this, intervention, disease.getNaturalDevelopmentName(), timeHorizont, 
							disease.getCostTreatments(), disease.getCostFollowUps(), disease.getCostScreenings(), disease.getCostClinicalDiagnosis()); 
					this.registerIntervention(radiosIntervention);
				}
			}
		} 
		if (CollectionUtils.isEmpty(radiosDiseaseInstance.getDisease().getInterventions()) || radiosDiseaseInstance.getDisease().getInterventions().size() < 2) {
			this.registerIntervention(new NullIntervention(this));
		}
		
		// El submodelo de mortalidad (por defecto podemos usar el que te pongo)
		registerDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
	}
	
	@Override
	public CostCalculator getCostCalculator() {
		return costCalc;
	}

	@Override
	public UtilityCalculator getUtilityCalculator() {
		return utilCalc;
	}


}
