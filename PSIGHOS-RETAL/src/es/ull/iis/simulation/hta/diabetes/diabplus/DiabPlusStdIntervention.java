/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.diabplus;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla
 *
 */
public class DiabPlusStdIntervention extends SecondOrderDiabetesIntervention {
	final private double objectiveHbA1cLevel;
	final private double annualCost;

	/**
	 * @param shortName
	 * @param description
	 */
	public DiabPlusStdIntervention(double objectiveHbA1cLevel, double annualCost, boolean base) {
		super(base ? "DIAB+BASE" : "DIAB+INT", "Standard intervention: do what the clinician says");
		this.objectiveHbA1cLevel = objectiveHbA1cLevel;
		this.annualCost = annualCost;
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {

	}

	@Override
	public DiabetesIntervention getInstance(int id, SecondOrderParamsRepository secParams) {
		return new Instance(id);
	}
	
	class Instance extends DiabetesIntervention {

		public Instance(int id) {
			super(id, BasicConfigParams.DEF_MAX_AGE);
		}

		@Override
		public double getHBA1cLevel(DiabetesPatient pat) {
			return objectiveHbA1cLevel;
		}

		@Override
		public double getAnnualCost(DiabetesPatient pat) {
			return annualCost;
		}
		
	}

}
