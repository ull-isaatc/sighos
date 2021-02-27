package es.ull.iis.simulation.hta.radios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.xml.bind.JAXBException;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.Development;
import es.ull.iis.ontology.radios.json.schema4simulation.Disease;
import es.ull.iis.ontology.radios.json.schema4simulation.Manifestation;
import es.ull.iis.ontology.radios.json.schema4simulation.PrecedingManifestation;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.radios.exceptions.TransformException;
import es.ull.iis.simulation.hta.radios.utils.CostUtils;
import es.ull.iis.simulation.hta.radios.wrappers.Matrix;

/**
 * @author David Prieto Gonz�lez
 */
public class RadiosDisease extends es.ull.iis.simulation.hta.progression.StagedDisease {
	private Disease disease;
	private Matrix costTreatments;
	private Matrix costFollowUps;
	private Matrix costScreenings;
	private Matrix costClinicalDiagnosis;
	private Integer timeHorizont;
	private String naturalDevelopmentName;
	
	private boolean debug = false; 
	
	public RadiosDisease(SecondOrderParamsRepository repository, Disease disease, Integer timeHorizont) throws TransformException, JAXBException {
		super(repository, disease.getName(), Constants.CONSTANT_EMPTY_STRING);
		
		this.timeHorizont = timeHorizont;

		setDisease(disease);
		if (getDisease() == null) {
			throw new TransformException ("ERROR => You must specify a disease for the simulation.");
		}
		if (getDisease().getDevelopments() != null) {
			List<Development> developments = getDisease().getDevelopments();
			Development naturalDevelopment = developments.stream()
					.filter(development -> Constants.DATAPROPERTYVALUE_KIND_DEVELOPMENT_NATURAL_VALUE.equals(development.getKind()))
					.findFirst()
					.orElse(null);
			this.naturalDevelopmentName = naturalDevelopment.getName();
			if (naturalDevelopment != null && CollectionUtils.notIsEmpty(naturalDevelopment.getManifestations())) {
				initializeCostMatrix(naturalDevelopment);

				List<Manifestation> manifestations = naturalDevelopment.getManifestations();
				Map<String, RadiosManifestation> resultFirstPassManifestationsAnalysis = firstPassManifestationsAnalysis(repository, manifestations);
				secondPassManifestationsAnalysis(repository, resultFirstPassManifestationsAnalysis, manifestations);
			} else {
				throw new TransformException("ERROR => The selected disease has no associated natural development. The specification of this development is mandatory.");
			}
		}
	}

	/**
	 * Initializes the cost matrix for the follow-up tests associated with the disease
	 */
	private void initializeCostMatrix(Development naturalDevelopment) {
		this.costTreatments = new Matrix();
		this.costFollowUps = new Matrix();
		this.costScreenings = new Matrix();
		this.costClinicalDiagnosis = new Matrix();

		CostUtils.loadCostFromTreatmentStrategies(this.costTreatments, naturalDevelopment.getName(), disease.getTreatmentStrategies(), timeHorizont);
		CostUtils.loadCostFromFollowUpStrategies(this.costFollowUps, naturalDevelopment.getName(), disease.getFollowUpStrategies(), timeHorizont);
		CostUtils.loadCostFromScreeningStrategies(this.costScreenings, disease.getScreeningStrategies(), timeHorizont);
		CostUtils.loadCostFromClinicalDiagnosisStrategies(this.costClinicalDiagnosis, disease.getClinicalDiagnosisStrategies(), timeHorizont);
		
		List<Manifestation> manifestations = naturalDevelopment.getManifestations();
		for (Manifestation manifestation : manifestations) {
			// TODO: actualizar la matriz de costos directos asociados a cada manifestaci�n. Para el caso de estudio todos los costos vienen asociados por tratamiento. 
			CostUtils.loadCostFromTreatmentStrategies(this.costTreatments, manifestation.getName(), manifestation.getTreatmentStrategies(), timeHorizont);
			CostUtils.loadCostFromFollowUpStrategies(this.costFollowUps, manifestation.getName(), manifestation.getFollowUpStrategies(), timeHorizont);
		}

		if (debug) {
			System.out.println("\nDisease - Cost matrix for Treatments:\n");
			CostUtils.showCostMatrix(this.costTreatments, "\t");
			System.out.println("\nDisease - Cost matrix for FollowUps:\n");
			CostUtils.showCostMatrix(this.costFollowUps, "\t");
			System.out.println("\nDisease - Cost matrix for Screenings:\n");
			CostUtils.showCostMatrix(this.costScreenings, "\t");
			System.out.println("\nDisease - Cost matrix for Clinical Diagnosis:\n");
			CostUtils.showCostMatrix(this.costClinicalDiagnosis, "\t");
		}
	}

	/**
	 * Create transitions between disease manifestations
	 * @param repository
	 * @param radiosManifestations
	 * @param manifestations
	 */
	private void secondPassManifestationsAnalysis(SecondOrderParamsRepository repository, Map<String, RadiosManifestation> radiosManifestations, List<Manifestation> manifestations) {
		for (String manifestationName : radiosManifestations.keySet()) {
			radiosManifestations.get(manifestationName).addParametersToRepository();
		}
	}

	/**
	 * Add manifestations to the disease
	 * @param repository
	 * @param manifestations
	 * @return
	 * @throws JAXBException 
	 */
	private Map<String, RadiosManifestation> firstPassManifestationsAnalysis(SecondOrderParamsRepository repository, List<Manifestation> manifestations) throws JAXBException {
		Queue<Manifestation> queueManifestations = new LinkedList<>();
		Set<String> processedManifestations = new HashSet<>();
		Map<String, RadiosManifestation> radiosManifestations = new HashMap<>();
		for (Manifestation manifestation : manifestations) {
			if (CollectionUtils.isEmpty(manifestation.getPrecedingManifestations())) {
				registerManifestation(repository, processedManifestations, radiosManifestations, manifestation);
			} else {
				queueManifestations.add(manifestation);
			}
		}
		
		while (!queueManifestations.isEmpty()) {
			Manifestation manifestation = queueManifestations.remove();
			List<String> precedingManifesationToListString = new ArrayList<>();
			if (manifestation.getPrecedingManifestations() != null) {
				for (PrecedingManifestation precedingManifestation : manifestation.getPrecedingManifestations()) {
					precedingManifesationToListString.add(precedingManifestation.getName());
				}
			}
			if (processedManifestations.containsAll(precedingManifesationToListString)) {
				registerManifestation(repository, processedManifestations, radiosManifestations, manifestation);
			} else {
				queueManifestations.add(manifestation);
			}
		}
		return radiosManifestations;		
	}

	private void registerManifestation(SecondOrderParamsRepository repository, Set<String> processedManifestations, Map<String, RadiosManifestation> radiosManifestations, Manifestation manifestation)
			throws JAXBException {
		RadiosManifestation radiosManifestation = new RadiosManifestation(repository, this, manifestation);
		addManifestation(radiosManifestation);
		radiosManifestations.put(manifestation.getName(), radiosManifestation);
		processedManifestations.add(manifestation.getName());
	}

	public RadiosDisease setDisease(Disease disease) {
		this.disease = disease;
		return this;
	}
	
	public Disease getDisease() {
		return disease;
	}
	
	public SecondOrderParamsRepository getRepository () {
		return secParams;
	}
	
	public Matrix getCostTreatments() {
		return costTreatments;
	}

	public void setCostTreatments(Matrix costTreatments) {
		this.costTreatments = costTreatments;
	}

	public Matrix getCostFollowUps() {
		return costFollowUps;
	}

	public void setCostFollowUps(Matrix costFollowUps) {
		this.costFollowUps = costFollowUps;
	}

	public Matrix getCostScreenings() {
		return costScreenings;
	}

	public void setCostScreenings(Matrix costScreenings) {
		this.costScreenings = costScreenings;
	}

	public Matrix getCostClinicalDiagnosis() {
		return costClinicalDiagnosis;
	}

	public void setCostClinicalDiagnosis(Matrix costClinicalDiagnosis) {
		this.costClinicalDiagnosis = costClinicalDiagnosis;
	}
	
	public String getNaturalDevelopmentName() {
		return naturalDevelopmentName;
	}
	
	public void setNaturalDevelopmentName(String naturalDevelopmentName) {
		this.naturalDevelopmentName = naturalDevelopmentName;
	}

	@Override
	public void registerSecondOrderParameters() {
		// TODO Auto-generated method stub
	}
}
