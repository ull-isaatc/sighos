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
public class DiabPlusExplorationStdIntervention extends SecondOrderDiabetesIntervention {
	final private double annualCost;
	final private double hba1cLevel;
	/**
	 * @param shortName
	 * @param description
	 */
	public DiabPlusExplorationStdIntervention(double annualCost, double hba1cLevel, boolean base) {
		super("DIAB+EXPLORE_" + hba1cLevel, "No intervention with level of HbA1c = " + hba1cLevel);
		this.annualCost = annualCost;
		this.hba1cLevel = hba1cLevel;
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
			return hba1cLevel;
		}

		@Override
		public double getAnnualCost(DiabetesPatient pat) {
			return annualCost;
		}
		
	}

}
