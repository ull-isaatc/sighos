/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.interventions;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.model.Describable;
import es.ull.iis.simulation.model.Identifiable;

/**
 * @author icasrod
 *
 */
public abstract class SecondOrderDiabetesIntervention implements Describable {
	/** A short name for the intervention */
	final private String shortName;
	/** A full description of the intervention */
	final private String description;

	/**
	 * 
	 * @param id Unique identifier 
	 * @param shortName Short name
	 * @param description Full description
	 */
	public SecondOrderDiabetesIntervention(final String shortName, final String description) {
		this.shortName = shortName;
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public String getShortName() {
		return shortName;
	}

	public abstract void addSecondOrderParams(SecondOrderParamsRepository secParams);

	public abstract DiabetesIntervention getInstance(int id, SecondOrderParamsRepository secParams);

	/**
	 * An instance for an intervention for diabetes
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public abstract class DiabetesIntervention implements Identifiable {
		/** The duration (in years) of the effect of the intervention */
		final private double yearsOfEffect;
		/** A unique identifier of the intervention */
		final private int id;

		/**
		 * Creates a new intervention for T1DM
		 * @param yearsOfEffect duration (in years) of the effect of the intervention
		 */
		public DiabetesIntervention(final int id, final double yearsOfEffect) {
			this.id = id;
			this.yearsOfEffect = yearsOfEffect;
		}
		
		/**
		 * Returns the duration (in years) of the effect of the intervention
		 * @return the duration (in years) of the effect of the intervention
		 */
		public double getYearsOfEffect() {
			return yearsOfEffect;
		}
		
		/**
		 * Returns the HbA1c level of a patient at a specific timestamp
		 * @param pat A patient
		 * @return the HbA1c level of a patient at a specific timestamp
		 */
		public abstract double getHBA1cLevel(DiabetesPatient pat);
		
		/**
		 * Returns the annual cost of this intervention
		 * @param pat A patient
		 * @return the annual cost of this intervention
		 */
		public abstract double getAnnualCost(DiabetesPatient pat);

		/**
		 * Returns a disutility value inherent to the intervention. A negative value represents an intervention that improves the utility
		 * @param pat A patient
		 * @return a disutility value inherent to the intervention. A negative value represents an intervention that improves the utility
		 */
		public double getDisutility(DiabetesPatient pat) {
			return 0.0;
		}

		@Override
		public int getIdentifier() {
			return id;
		}
		
		public String getDescription() {
			return description;
		}
	}
}
