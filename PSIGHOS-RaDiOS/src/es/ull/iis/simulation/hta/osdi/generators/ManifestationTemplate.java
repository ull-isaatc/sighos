package es.ull.iis.simulation.hta.osdi.generators;

import java.util.Set;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.ManifestationType;

public enum ManifestationTemplate {
	SHE("Severe Hypoglycemic Episode", ManifestationType.ACUTE),
	ANGINA("Angina", ManifestationType.CHRONIC),
	HF("Heart Failure", ManifestationType.CHRONIC),
	STROKE("Stroke", ManifestationType.CHRONIC),
	MI("Myocardial Infarction", ManifestationType.CHRONIC),
	BGRET("Background Retinopathy", ManifestationType.CHRONIC),
	PRET("Proliferative Retinopathy", ManifestationType.CHRONIC),
	ME("Macular Edema", ManifestationType.CHRONIC),
	BLI("Blindness", ManifestationType.CHRONIC),
	NEU("Neuropathy", ManifestationType.CHRONIC),
	LEA("Lower Extremity Amputation", ManifestationType.CHRONIC),
	ALB1("Microalbuminuria", ManifestationType.CHRONIC),
	ALB2("Macroalbuminuria", ManifestationType.CHRONIC),
	ESRD("End-Stage Renal Disease", ManifestationType.CHRONIC);
	
	static {
		// Define all the exclusions among manifestations
		ManifestationTemplate.ANGINA.addExclusions(Set.of(ManifestationTemplate.HF, ManifestationTemplate.STROKE, ManifestationTemplate.MI));
		ManifestationTemplate.HF.addExclusions(Set.of(ManifestationTemplate.ANGINA, ManifestationTemplate.STROKE, ManifestationTemplate.MI));
		ManifestationTemplate.STROKE.addExclusions(Set.of(ManifestationTemplate.HF, ManifestationTemplate.ANGINA, ManifestationTemplate.MI));
		ManifestationTemplate.MI.addExclusions(Set.of(ManifestationTemplate.HF, ManifestationTemplate.STROKE, ManifestationTemplate.ANGINA));
		ManifestationTemplate.PRET.addExclusions(Set.of(ManifestationTemplate.BGRET));
		ManifestationTemplate.BLI.addExclusions(Set.of(ManifestationTemplate.BGRET, ManifestationTemplate.PRET, ManifestationTemplate.ME));
		ManifestationTemplate.LEA.addExclusions(Set.of(ManifestationTemplate.NEU));
		ManifestationTemplate.ALB2.addExclusions(Set.of(ManifestationTemplate.ALB1));
		ManifestationTemplate.ESRD.addExclusions(Set.of(ManifestationTemplate.ALB1, ManifestationTemplate.ALB2));
	}
	
	private final String description;
	private final OSDiWrapper.ManifestationType type;
	private final Set<ManifestationTemplate> exclusions;
	private String instanceName = null;
	/**
	 * @param description
	 * @param type
	 */
	private ManifestationTemplate(String description, ManifestationType type) {
		this.description = description;
		this.type = type;
		this.exclusions = new TreeSet<>();
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @return the type
	 */
	public OSDiWrapper.ManifestationType getType() {
		return type;
	}
	
	/**
	 * @return the exclusions
	 */
	public Set<ManifestationTemplate> getExclusions() {
		return exclusions;
	}		
	
	public void addExclusions(Set<ManifestationTemplate> newExclusions) {
		exclusions.addAll(newExclusions);
	}
	
	public void generate(OSDiWrapper wrap) {
		final Set<String> strExclusions = new TreeSet<>();
		for (ManifestationTemplate exclManif : exclusions)
			strExclusions.add(exclManif.name());
		instanceName = wrap.getManifestationInstanceName(name());
		wrap.createManifestation(instanceName, type, description, strExclusions, wrap.getDiseaseInstanceName(T1DMInstancesGenerator.STR_DISEASE_NAME));			
	}
	
	public String getInstanceName() {
		return instanceName;
	}
}