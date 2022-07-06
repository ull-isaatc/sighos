/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.interventions.ScreeningStrategy;
import es.ull.iis.simulation.hta.osdi.OSDiNames.DataProperty;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla
 *
 */
public interface InterventionBuilder {

	public static Intervention getInterventionInstance(SecondOrderParamsRepository secParams, String interventionName) throws TranspilerException {
		final String description = OwlHelper.getDataPropertyValue(interventionName, OSDiNames.DataProperty.HAS_DESCRIPTION.getDescription(), "");
		final String kind = OwlHelper.getDataPropertyValue(interventionName, OSDiNames.DataProperty.HAS_INTERVENTION_KIND.getDescription(), OSDiNames.DataPropertyRange.KIND_INTERVENTION_NOSCREENING_VALUE.getDescription());
		Intervention intervention = null;
		if (OSDiNames.DataPropertyRange.KIND_INTERVENTION_SCREENING_VALUE.getDescription().equals(kind)) {			
			// TODO: Move to createSecondOrderParams when ScreeningIntervention be modified accordingly
			String strSensitivity = OwlHelper.getDataPropertyValue(interventionName, DataProperty.HAS_SENSITIVITY.getDescription(), "1.0");
			final ProbabilityDistribution probSensitivity = ValueParser.splitProbabilityDistribution(strSensitivity);
			if (probSensitivity == null)
				throw new TranspilerException("Error parsing regular expression \"" + strSensitivity + "\" for instance \"" + interventionName + "\"");
			String strSpecificity = OwlHelper.getDataPropertyValue(interventionName, DataProperty.HAS_SPECIFICITY.getDescription(), "1.0");
			final ProbabilityDistribution probSpecificity = ValueParser.splitProbabilityDistribution(strSpecificity);
			if (probSpecificity == null)
				throw new TranspilerException("Error parsing regular expression \"" + strSpecificity + "\" for instance \"" + interventionName + "\"");

			intervention = new ScreeningStrategy(secParams, interventionName, description, probSpecificity.getDeterministicValue(), probSpecificity.getDeterministicValue()) {
				
				@Override
				public void registerSecondOrderParameters() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public double getStartingCost(Patient pat) {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public double getAnnualCost(Patient pat) {
					// TODO Auto-generated method stub
					return 0;
				}
			};
		}
		else {
			intervention = new Intervention(secParams, interventionName, description) {

				@Override
				public void registerSecondOrderParameters() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public double getAnnualCost(Patient pat) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public double getStartingCost(Patient pat) {
					// TODO Auto-generated method stub
					return 0;
				}
			};
		}
		return intervention;
	}
}
