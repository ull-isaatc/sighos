package es.ull.iis.simulation.hta.radios;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.tenerife.ull.ontology.radios.Constants;
import es.tenerife.ull.ontology.radios.json.schema4simulation.Development;
import es.tenerife.ull.ontology.radios.json.schema4simulation.Disease;
import es.tenerife.ull.ontology.radios.json.schema4simulation.Manifestation;
import es.tenerife.ull.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.radios.exceptions.TransformException;

public class RadiosDisease extends es.ull.iis.simulation.hta.progression.StagedDisease {
	private Disease disease;
	
	public RadiosDisease(SecondOrderParamsRepository repository, String name, String description) {
		super(repository, name, description);
	}

	public RadiosDisease(SecondOrderParamsRepository repository, Disease disease) throws TransformException {
		super(repository, disease.getName(), Constants.CONSTANT_EMPTY_STRING);
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
			if (naturalDevelopment != null && CollectionUtils.notIsEmpty(naturalDevelopment.getManifestations())) {
				Map<String, RadiosManifestation> radiosManifestations = new HashMap<>();
				List<Manifestation> manifestations = naturalDevelopment.getManifestations();
				
				// First pass: generate RaDiOS manifesations
				for (Manifestation manifestation : manifestations) {
					RadiosManifestation radiosManifestation = new RadiosManifestation(repository, this, manifestation);
					addManifestation(radiosManifestation);
					radiosManifestations.put(manifestation.getName(), radiosManifestation);
				}
				
				// Second pass: generate RaDiOS transitions
				for (Manifestation manifestation : manifestations) {
					addTransition(new RadiosTransition(repository, getNullManifestation(), radiosManifestations.get(manifestation.getName()), Boolean.FALSE));
					if (CollectionUtils.notIsEmpty(manifestation.getPrecedingManifestations())) {
						for (String precedingManifestation : manifestation.getPrecedingManifestations()) {
							addTransition(new RadiosTransition(repository, radiosManifestations.get(precedingManifestation), radiosManifestations.get(manifestation.getName()), Boolean.FALSE));
						}
					}
				}
			} else {
				throw new TransformException("ERROR => The selected disease has no associated natural development. The specification of this development is mandatory.");
			}
		}
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
	
	@Override
	public void registerSecondOrderParameters() {
		// FIXME: en el caso de RaDiOS este método no tiene sentido
	}

	@Override
	public DiseaseProgression getProgression(Patient pat) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getAnnualCostWithinPeriod(Patient pat, double initAge, double endAge) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDisutility(Patient pat, DisutilityCombinationMethod method) {
		// TODO Auto-generated method stub
		return 0;
	}
}
