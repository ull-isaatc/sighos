package es.ull.iis.simulation.hta.osdi.generators;

import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.DataItemType;

public enum GroupOfManifestationsTemplate {
	NEU(EnumSet.of(ManifestationTemplate.NEU, ManifestationTemplate.LEA)),
	NPH(EnumSet.of(ManifestationTemplate.ALB1, ManifestationTemplate.ALB2, ManifestationTemplate.ESRD)),
	RET(EnumSet.of(ManifestationTemplate.BGRET, ManifestationTemplate.PRET, ManifestationTemplate.ME, ManifestationTemplate.BLI)),
	CHD(EnumSet.of(ManifestationTemplate.ANGINA, ManifestationTemplate.HF, ManifestationTemplate.STROKE, ManifestationTemplate.MI)) {
		@Override
		protected void createParameters(OSDiWrapper wrap) {
			final String instanceName = wrap.getManifestationGroupInstanceName(name());

			String incidenceIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_GROUP_PREFIX + this + OSDiWrapper.STR_INCIDENCE_SUFFIX);
			wrap.createParameter(incidenceIRI, OSDiWrapper.Clazz.INCIDENCE, "Base incidence of any manifestation related to CHD when HbA1c is 9.1", "Hoerger 2004", 2004, 0.0045, DataItemType.DI_PROBABILITY);
			T1DMInstancesGenerator.generateCIParameters(wrap, incidenceIRI, OSDiWrapper.Clazz.INCIDENCE, "Base incidence of any manifestation related to CHD when HbA1c is 9.1", "Hoerger 2004", new double[] {0.001, 0.0084}, 2004);
			OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION.add(instanceName, incidenceIRI);
			
			String baseRrIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_GROUP_PREFIX + this + OSDiWrapper.STR_INCIDENCE_SUFFIX + "_Base" + OSDiWrapper.STR_RELATIVE_RISK_SUFFIX);
			wrap.createParameter(baseRrIRI, OSDiWrapper.Clazz.PARAMETER, "Base RR for CHD-related complication, associated to a 1 PP increment of HbA1c with respect to 9.1", 
					"Selvin et al. https://doi.org/2004 10.7326/0003-4819-141-6-200409210-00007", 2004, 1.15, DataItemType.DI_RELATIVE_RISK);
			T1DMInstancesGenerator.generateCIParameters(wrap, baseRrIRI, OSDiWrapper.Clazz.PARAMETER, "Base RR for CHD-related complication, associated to a 1 PP increment of HbA1c with respect to 9.1", 
					"Selvin et al. https://doi.org/2004 10.7326/0003-4819-141-6-200409210-00007", new double[] {0.92, 1.43}, 2004);
			
			final TreeSet<String> dependentAttributes = new TreeSet<>();
			dependentAttributes.add("HbA1c");
			final TreeSet<String> dependentParameters = new TreeSet<>();
			dependentParameters.add(OSDiWrapper.STR_MANIF_GROUP_PREFIX + this + OSDiWrapper.STR_INCIDENCE_SUFFIX + "_Base" + OSDiWrapper.STR_RELATIVE_RISK_SUFFIX);
			String rrIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_GROUP_PREFIX + this + OSDiWrapper.STR_INCIDENCE_SUFFIX + OSDiWrapper.STR_RELATIVE_RISK_SUFFIX);
			wrap.createParameter(rrIRI, OSDiWrapper.Clazz.PARAMETER, "RR for CHD-related complication, associated to a 1 PP increment of HbA1c with respect to 9.1", 
					"Selvin et al. https://doi.org/2004 10.7326/0003-4819-141-6-200409210-00007", 2004, baseRrIRI + "^("+ wrap.getAttributeInstanceName("HbA1c") + " - 9.1)", dependentAttributes, dependentParameters, DataItemType.DI_RELATIVE_RISK);
			// TODO: Characterize pathway from NPH to CHD
//			incidenceIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_INCIDENCE_SUFFIX);
//			wrap.createParameter(incidenceIRI, OSDiWrapper.Clazz.INCIDENCE, "Incidence of any manifestation related to CHD", "Klein 2014", 2014, 0.0224, DataItemType.DI_PROBABILITY);
//			T1DMInstancesGenerator.generateCIParameters(wrap, incidenceIRI, OSDiWrapper.Clazz.INCIDENCE, "Incidence of any manifestation related to CHD", "Klein 2014", new double[] {0.013, 0.034}, 2014);
			OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION.add(instanceName, rrIRI);
			
		}
	},
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
		createParameters(wrap);
	}
	
	protected void createParameters(OSDiWrapper wrap) {		
	}
	
}