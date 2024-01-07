package es.ull.iis.simulation.hta.osdi.generators;

import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.osdi.ontology.ModifiableOSDiWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper;

public enum GroupOfManifestationsTemplate {
	// NEU(EnumSet.of(DiseaseProgressionTemplate.NEU, DiseaseProgressionTemplate.LEA)),
	// NPH(EnumSet.of(DiseaseProgressionTemplate.ALB1, DiseaseProgressionTemplate.ALB2, DiseaseProgressionTemplate.ESRD)),
	// RET(EnumSet.of(DiseaseProgressionTemplate.BGRET, DiseaseProgressionTemplate.PRET, DiseaseProgressionTemplate.ME, DiseaseProgressionTemplate.BLI)),
	// HYPO(EnumSet.of(DiseaseProgressionTemplate.SHE)),
	CHD(EnumSet.of(DiseaseProgressionTemplate.ANGINA, DiseaseProgressionTemplate.HF, DiseaseProgressionTemplate.STROKE, DiseaseProgressionTemplate.MI));
	private final Set<String> components;

	/**
	 * @param components
	 */
	private GroupOfManifestationsTemplate(Set<DiseaseProgressionTemplate> components) {			
		this.components = new TreeSet<>();
		for (DiseaseProgressionTemplate manif : components)
			this.components.add(manif.name());
	}

	/**
	 * @return the components
	 */
	public Set<String> getComponents() {
		return components;
	}

	public void generate(ModifiableOSDiWrapper wrap) {
		wrap.createGroupOfManifestations(OSDiWrapper.InstanceIRI.MANIFESTATION_GROUP.getIRI(name()), components);
	}
	
}