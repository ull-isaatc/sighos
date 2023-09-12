/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.generators;

import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import es.ull.iis.simulation.hta.osdi.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.OSDiWrapper.ManifestationType;
import es.ull.iis.simulation.hta.osdi.OSDiWrapper.ModelType;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class T1DMInstancesGenerator {
	private final OSDiWrapper wrap;
	private final static String PREFIX = "T1DM_";
	private final static String STR_MODEL_NAME = PREFIX + "StdModelDES";
	private final static String STR_DISEASE_NAME = PREFIX + "Disease";
	private final static String STR_MANIF_PREFIX = PREFIX + "Manif_";
	private final static String STR_MANIF_GROUP_PREFIX = PREFIX + "Group_Manif_";
	
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
				this.components.add(manif.getInstanceName());
		}

		/**
		 * @return the components
		 */
		public Set<String> getComponents() {
			return components;
		}
		
		public String getInstanceName() {
			return STR_MANIF_GROUP_PREFIX + name();
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
		/**
		 * @param description
		 * @param type
		 */
		private Manifestation(String description, ManifestationType type) {
			this.description = description;
			this.type = type;
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
		
		public String getInstanceName() {
			return STR_MANIF_PREFIX + name();
		}		
	}
	/**
	 * @throws OWLOntologyCreationException 
	 * @throws OWLOntologyStorageException 
	 * 
	 */
	public T1DMInstancesGenerator(String path) throws OWLOntologyCreationException, OWLOntologyStorageException {
		wrap = new OSDiWrapper(path);
		wrap.createModel(STR_MODEL_NAME, ModelType.DES, "ICR", "First example of T1DM model", "Spain", 2022, "");
		wrap.createDisease(STR_DISEASE_NAME, "Type I Diabetes Mellitus", STR_MODEL_NAME, "do:9744", "icd:E10", "omim:222100", "snomed:46635009");
		for (Manifestation manif : Manifestation.values()) {
			wrap.createManifestation(manif.getInstanceName(), manif.getType(), manif.getDescription(), STR_MODEL_NAME, STR_DISEASE_NAME);
		}
		for (GroupOfManifestations group : GroupOfManifestations.values()) {
			wrap.createGroupOfManifestations(group.getInstanceName(), STR_MODEL_NAME, group.getComponents());
		}
		wrap.printIndividuals(true);
		wrap.save();
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
