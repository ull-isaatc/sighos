package es.ull.iis.simulation.hta.osdi.generators;

import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;

public enum GroupOfManifestationsTemplate {
	NEU(EnumSet.of(ManifestationTemplate.NEU, ManifestationTemplate.LEA)),
	NPH(EnumSet.of(ManifestationTemplate.ALB1, ManifestationTemplate.ALB2, ManifestationTemplate.ESRD)),
	RET(EnumSet.of(ManifestationTemplate.BGRET, ManifestationTemplate.PRET, ManifestationTemplate.ME, ManifestationTemplate.BLI)),
	CHD(EnumSet.of(ManifestationTemplate.ANGINA, ManifestationTemplate.HF, ManifestationTemplate.STROKE, ManifestationTemplate.MI)),
	HYPO(EnumSet.of(ManifestationTemplate.SHE));
	private final Set<String> components;

	/**
	 * @param components
	 */
	private GroupOfManifestationsTemplate(Set<ManifestationTemplate> components) {			
		this.components = new TreeSet<>();
		for (ManifestationTemplate manif : components)
			this.components.add(manif.name());
	}

	/**
	 * @return the components
	 */
	public Set<String> getComponents() {
		return components;
	}

	public void generate(OSDiWrapper wrap) {
		wrap.createGroupOfManifestations(wrap.getManifestationGroupInstanceName(name()), components);
	}
}