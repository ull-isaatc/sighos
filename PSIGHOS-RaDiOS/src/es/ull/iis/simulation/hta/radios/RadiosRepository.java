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

import es.ull.iis.ontology.radios.json.schema4simulation.Intervention;
import es.ull.iis.ontology.radios.json.schema4simulation.Schema4Simulation;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Patient;
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
import es.ull.iis.simulation.hta.simpletest.TestPopulation;

/**
 * @author Iván Castilla
 * @author David Prieto González
 */
public class RadiosRepository extends SecondOrderParamsRepository {
	private final ObjectMapper mapper; 
	private final CostCalculator costCalc;
	private final UtilityCalculator utilCalc;

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

		costCalc = new DiseaseCostCalculator(this);
		utilCalc = new DiseaseUtilityCalculator(this, DisutilityCombinationMethod.ADD, BasicConfigParams.DEF_U_GENERAL_POP);
		
		mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).setSerializationInclusion(Include.NON_NULL).setSerializationInclusion(Include.NON_EMPTY);
		Schema4Simulation radiosDiseaseInstance = mapper.readValue(new File(pathToRaDiOSJson), Schema4Simulation.class);

		RadiosDisease disease = new RadiosDisease(this, radiosDiseaseInstance.getDisease(), timeHorizont);		
		registerDisease(disease);

		Population population = new TestPopulation(this, disease);
		registerPopulation(population);
		
		if (CollectionUtils.notIsEmpty(radiosDiseaseInstance.getDisease().getInterventions())) {
			for (Intervention intervention : radiosDiseaseInstance.getDisease().getInterventions()) {
				RadiosIntervention radiosIntervention = new RadiosIntervention(this, intervention, disease.getNaturalDevelopmentName(), timeHorizont, 
						disease.getCostTreatments(), disease.getCostFollowUps(), disease.getCostScreenings(), disease.getCostClinicalDiagnosis()); 
				this.registerIntervention(radiosIntervention);
//				
//				Patient patient = generatePatient(disease, radiosIntervention, population, this);
//				
//				double a = radiosIntervention.getFullLifeCost(patient);
//				System.err.println("Intervention: " + intervention.getName() + " ==> Full life cost: " + a + "\n\n");
			}
		}
		
		// El submodelo de mortalidad (por defecto podemos usar el que te pongo)
		registerDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
		
	}
	
	@SuppressWarnings("unused")
	private Patient generatePatient (RadiosDisease disease, RadiosIntervention intervention, Population population, SecondOrderParamsRepository repository) {
		DiseaseProgressionSimulation dps = new DiseaseProgressionSimulation(0, intervention, repository, 10);
		Patient pat = new Patient(dps, intervention, population);
//		for (Manifestation manifestation : pat.getManifestationEvents().keySet()) {
//			pat.getManifestationEvents().get(manifestation).getFirst().getTs(); // Tiempo en el cual sucede la manifestación;
//		}
//		pat.getDetailedState().add(new TestAcuteManifestation1("#SCD_Manif_SplenicSequestration", repository, disease));
//		pat.getDetailedState().add(new TestAcuteManifestation1("#SCD_Manif_SplenicSequestration_Recurrent", repository, disease));
//		pat.getDetailedState().add(new TestAcuteManifestation1("#SCD_Manif_Meningitis", repository, disease));
//		pat.getDetailedState().add(new TestAcuteManifestation1("#SCD_Manif_PneumococcalSepsis", repository, disease));
//		pat.getDetailedState().add(new TestAcuteManifestation1("#SCD_Manif_Stroke", repository, disease));
//		pat.getDetailedState().add(new TestAcuteManifestation1("#SCD_Manif_Stroke_Recurrent", repository, disease));
		
		return pat;
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
