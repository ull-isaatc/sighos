/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.generators;

import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.DataItemType;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.ManifestationType;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.ModelType;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.TemporalBehavior;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMInstancesGenerator {
	private final OSDiWrapper wrap;
	private final static String INSTANCE_PREFIX = "T1DM_";
	private final static String STR_MODEL_NAME = "StdModelDES";
	private final static String STR_DISEASE_NAME ="Disease";
	
	public enum GroupOfManifestations {
		NEU(EnumSet.of(Manifestation.NEU, Manifestation.LEA)),
		NPH(EnumSet.of(Manifestation.ALB1, Manifestation.ALB2, Manifestation.ESRD)),
		RET(EnumSet.of(Manifestation.BGRET, Manifestation.PRET, Manifestation.ME, Manifestation.BLI)),
		CHD(EnumSet.of(Manifestation.ANGINA, Manifestation.HF, Manifestation.STROKE, Manifestation.MI)),
		HYPO(EnumSet.of(Manifestation.SHE));
		private final Set<String> components;

		/**
		 * @param components
		 */
		private GroupOfManifestations(Set<Manifestation> components) {			
			this.components = new TreeSet<>();
			for (Manifestation manif : components)
				this.components.add(manif.name());
		}

		/**
		 * @return the components
		 */
		public Set<String> getComponents() {
			return components;
		}

		public void generate(OSDiWrapper wrap) {
			wrap.createGroupOfManifestations(name(), components);
		}
	};
	
	public enum Manifestation {
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
		private final String description;
		private final OSDiWrapper.ManifestationType type;
		private final Set<Manifestation> exclusions;
		private String instanceName = null;
		/**
		 * @param description
		 * @param type
		 */
		private Manifestation(String description, ManifestationType type) {
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
		public Set<Manifestation> getExclusions() {
			return exclusions;
		}		
		
		public void addExclusions(Set<Manifestation> newExclusions) {
			exclusions.addAll(newExclusions);
		}
		
		public void generate(OSDiWrapper wrap) {
			final Set<String> strExclusions = new TreeSet<>();
			for (Manifestation exclManif : exclusions)
				strExclusions.add(exclManif.name());
			instanceName = wrap.createManifestation(name(), type, description, strExclusions, STR_DISEASE_NAME);			
		}
		
		public String getInstanceName() {
			return instanceName;
		}
	}
	
	static {
		// Define all the exclusions among manifestations
		Manifestation.ANGINA.addExclusions(Set.of(Manifestation.HF, Manifestation.STROKE, Manifestation.MI));
		Manifestation.HF.addExclusions(Set.of(Manifestation.ANGINA, Manifestation.STROKE, Manifestation.MI));
		Manifestation.STROKE.addExclusions(Set.of(Manifestation.HF, Manifestation.ANGINA, Manifestation.MI));
		Manifestation.MI.addExclusions(Set.of(Manifestation.HF, Manifestation.STROKE, Manifestation.ANGINA));
		Manifestation.PRET.addExclusions(Set.of(Manifestation.BGRET));
		Manifestation.BLI.addExclusions(Set.of(Manifestation.BGRET, Manifestation.PRET, Manifestation.ME));
		Manifestation.LEA.addExclusions(Set.of(Manifestation.NEU));
		Manifestation.ALB2.addExclusions(Set.of(Manifestation.ALB1));
		Manifestation.ESRD.addExclusions(Set.of(Manifestation.ALB1, Manifestation.ALB2));
	}
	
	/**
	 * @throws OWLOntologyCreationException 
	 * @throws OWLOntologyStorageException 
	 * 
	 */
	public T1DMInstancesGenerator(String path) throws OWLOntologyCreationException, OWLOntologyStorageException {
		wrap = new OSDiWrapper(path, STR_MODEL_NAME, INSTANCE_PREFIX);
		wrap.createWorkingModel(ModelType.DES, "ICR", "First example of T1DM model", "Spain", 2022, "");
		final String diseaseIRI = wrap.createDisease(STR_DISEASE_NAME, "Type I Diabetes Mellitus", "do:9744", "icd:E10", "omim:222100", "snomed:46635009");
		final String diseaseCostIRI = wrap.createCost(STR_DISEASE_NAME + OSDiWrapper.STR_ANNUAL_COST_SUFFIX, 
				"Value computed by substracting the burden of complications from the global burden of DM1 in Spain; finally divided by the prevalent DM1 population", 
				"Crespo et al. 2012: http://dx.doi.org/10.1016/j.avdiab.2013.07.007", TemporalBehavior.ANNUAL, 2012, "1116.733023", null, DataItemType.CURRENCY_EURO);
		OSDiWrapper.ObjectProperty.HAS_FOLLOW_UP_COST.add(wrap, diseaseIRI, diseaseCostIRI);
		
		for (Manifestation manif : Manifestation.values()) {
			manif.generate(wrap);
		}
		generateCostsForManifestations();
		for (GroupOfManifestations group : GroupOfManifestations.values()) {
			group.generate(wrap);
		}
		wrap.printIndividuals(true);
		wrap.save();
	}

	private void generateCostsForManifestations() {
		// ---- CHD manifestations ----
		// Angina
		String costIRI = wrap.createCost(Manifestation.ANGINA + OSDiWrapper.STR_ANNUAL_COST_SUFFIX, "Year 2+ of Angina", 
				"https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ANNUAL, 2016, "532.01", null, DataItemType.CURRENCY_EURO);
		OSDiWrapper.ObjectProperty.HAS_COST.add(wrap, Manifestation.ANGINA.getInstanceName(), costIRI);
		costIRI = wrap.createCost(Manifestation.ANGINA + OSDiWrapper.STR_ONETIME_COST_SUFFIX, "Episode of Angina", 
				"https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ANNUAL, 2016, "1985.96", null, DataItemType.CURRENCY_EURO);
		OSDiWrapper.ObjectProperty.HAS_COST.add(wrap, Manifestation.ANGINA.getInstanceName(), costIRI);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			T1DMInstancesGenerator gen = new T1DMInstancesGenerator("resources/OSDi.owl");
		} catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
			e.printStackTrace();
		}

	}

}
