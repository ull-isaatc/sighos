/**
 * 
 */
package es.ull.iis.simulation.hta.interventions;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.model.Describable;
import es.ull.iis.simulation.model.Identifiable;

/**
 * The second order characterization of an intervention. The {@link #addSecondOrderParams(SecondOrderParamsRepository)} 
 * must be invoked from the {@link SecondOrderParamsRepository} to register the second order parameters. 
 * The {@link #getInstance(int, SecondOrderParamsRepository)} method creates an instance of the intervention by sampling
 * from the second order parameters.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class SecondOrderIntervention implements Describable {
	/** A short name for the intervention */
	final private String shortName;
	/** A full description of the intervention */
	final private String description;

	/**
	 * Creates a second order characterization of an intervention
	 * @param shortName Short name
	 * @param description Full description
	 */
	public SecondOrderIntervention(final String shortName, final String description) {
		this.shortName = shortName;
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Returns a short name for the intervention 
	 * @return A short name for the intervention 
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Registers the second order parameters associated to this intervention in a repository
	 * @param secParams Repository for second order parameters
	 */
	public abstract void addSecondOrderParams(SecondOrderParamsRepository secParams);

	/**
	 * Creates an instance of this intervention
	 * @param id unique identifier for the intervention
	 * @param secParams Repository for sampling from the second order parameters
	 * @return an instance of this intervention
	 */
	public abstract Intervention getInstance(int id, SecondOrderParamsRepository secParams);

	/**
	 * An instance for an intervention for a disease
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public abstract class Intervention implements Identifiable, Describable {
		/** The duration (in years) of the effect of the intervention */
		final private double yearsOfEffect;
		/** A unique identifier of the intervention */
		final private int id;

		/**
		 * Creates a new intervention for a disease
		 * @param yearsOfEffect duration (in years) of the effect of the intervention
		 */
		public Intervention(final int id, final double yearsOfEffect) {
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
		public abstract double getHBA1cLevel(Patient pat);
		
		/**
		 * Returns the annual cost of this intervention
		 * @param pat A patient
		 * @return the annual cost of this intervention
		 */
		public abstract double getAnnualCost(Patient pat);

		/**
		 * Returns a disutility value inherent to the intervention. A negative value represents an intervention that improves the utility
		 * @param pat A patient
		 * @return a disutility value inherent to the intervention. A negative value represents an intervention that improves the utility
		 */
		public double getDisutility(Patient pat) {
			return 0.0;
		}

		@Override
		public int getIdentifier() {
			return id;
		}
		
		@Override
		public String getDescription() {
			return description;
		}
		
		/**
		 * Returns the second order description of the intervention
		 * @return the second order description of the intervention
		 */
		public SecondOrderIntervention getSecondOrderDiabetesIntervention() {
			return SecondOrderIntervention.this;
		}
	}
}
