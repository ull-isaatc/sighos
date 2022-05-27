/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.interventions.ScreeningStrategy;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla
 *
 */
public interface InterventionBuilder {

	public static Intervention getInterventionInstance(SecondOrderParamsRepository secParams, String interventionName) {
		final String description = OwlHelper.getDataPropertyValue(interventionName, OSDiNames.DataProperty.HAS_DESCRIPTION.getDescription(), "");
		final String kind = OwlHelper.getDataPropertyValue(interventionName, OSDiNames.DataProperty.HAS_INTERVENTION_KIND.getDescription(), OSDiNames.DataPropertyRange.KIND_INTERVENTION_NOSCREENING_VALUE.getDescription());
		Intervention intervention = null;
		if (OSDiNames.DataPropertyRange.KIND_INTERVENTION_SCREENING_VALUE.getDescription().equals(kind)) {
			// TODO: Initilize properly
			double sensitivity = 1.0;
			double specificity = 1.0;
			intervention = new ScreeningStrategy(secParams, interventionName, description, sensitivity, specificity) {
				
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
