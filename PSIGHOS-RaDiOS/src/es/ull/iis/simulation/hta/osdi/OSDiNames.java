/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import es.ull.iis.simulation.hta.Named;

/**
 * @author Iván Castilla
 *
 */
public interface OSDiNames {

	/**
	 * Names of the classes defined in the ontology
	 * @author Iván Castilla
	 *
	 */
	public static enum Class implements Named {
		CLINICAL_DIAGNOSIS("#ClinicalDiagnosis"),	
		DEVELOPMENT("#Development"),
		DISEASE("#Disease"),
		DRUG("#Drug"),
		FOLLOWUP("#FollowUp"),
		GUIDELINE("#Guideline"),
		INTERVENTION("#Intervention"),
		MANIFESTATION("#Manifestation"),
		MANIFESTATION_PATHWAY("#ManifestationPathway"),
		MODIFICATION("#Modification"),
		MANIFESTATION_MODIFICATION("#ManifestationModification"),
		DEVELOPMENT_MODIFICACION("#DevelopmentModificacion"),
		PARAMETER("#Parameter"),
		EPIDEMIOLOGICAL_PARAMETER("#EpidemiologicalParameter"),
		COST("#Cost"),
		UTILITY("#Utility"),
		POPULATION("#Population"),
		SCREENING("#Screening"),
		STRATEGY("#Strategy"),
		STRATEGY_STEP("#StrategyStep"),
		TREATMENT("#Treatment");

		private final String name;
		private Class(String name) {
			this.name = name;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
	}

	/**
	 * Names of the classes defined in the ontology
	 * @author Iván Castilla
	 *
	 */
	public static enum DataProperty implements Named {
		CLINICAL_DIAGNOSIS("#ClinicalDiagnosis"),	
		DEVELOPMENT("#Development"),
		DISEASE("#Disease"),
		DRUG("#Drug"),
		FOLLOWUP("#FollowUp"),
		GUIDELINE("#Guideline"),
		INTERVENTION("#Intervention"),
		MANIFESTATION("#Manifestation"),
		MANIFESTATION_PATHWAY("#ManifestationPathway"),
		MODIFICATION("#Modification"),
		MANIFESTATION_MODIFICATION("#ManifestationModification"),
		DEVELOPMENT_MODIFICACION("#DevelopmentModificacion"),
		PARAMETER("#Parameter"),
		EPIDEMIOLOGICAL_PARAMETER("#EpidemiologicalParameter"),
		COST("#Cost"),
		UTILITY("#Utility"),
		POPULATION("#Population"),
		SCREENING("#Screening"),
		STRATEGY("#Strategy"),
		STRATEGY_STEP("#StrategyStep"),
		TREATMENT("#Treatment");

		private final String name;
		private DataProperty(String name) {
			this.name = name;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
	}
	
}
