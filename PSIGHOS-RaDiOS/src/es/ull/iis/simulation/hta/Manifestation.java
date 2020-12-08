/**
 * 
 */
package es.ull.iis.simulation.hta;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.StartWithComplicationParam;
import es.ull.iis.simulation.hta.submodels.Disease;
import es.ull.iis.simulation.hta.submodels.SecondOrderDisease;

/**
 * A stage of a {@link ChronicComplication chronic complication} defined in the model. Different chronic complications submodels
 * can define different stages that are registered at the beginning of the simulation. 
 * @author Iván Castilla Rodríguez
 *
 */
public class Manifestation implements Named, Comparable<Manifestation> {
	public enum Type {
		ACUTE,
		CHRONIC
	}
	/** Short name of the complication stage */
	private final String name;
	/** Full description of the complication stage */
	private final String description;
	/** Disease this manifestation is related to */
	private final SecondOrderDisease disease;
	/** An index to be used when this class is used in TreeMaps or other ordered structures. The order is unique among the
	 * complications defined to be used within a simulation */ 
	private int ord = -1;
	private final Type type;
	
	/**
	 * Creates a new complication stage of a {@link ChronicComplication chronic complication} defined in the model
	 * @param name Name of the stage
	 * @param description Full description of the stage
	 * @param disease Main chronic complication
	 */
	public Manifestation(String name, String description, SecondOrderDisease disease, Type type) {
		this.name = name;
		this.description = description;
		this.disease = disease;
		this.type = type;
	}
	
	/**
	 * Returns the description of the complication
	 * @return the description of the complication
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@link SecondOrderDisease} this manifestation is related to.
	 * @return the {@link SecondOrderDisease} this manifestation is related to
	 */
	public SecondOrderDisease getDisease() {
		return disease;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	@Override
	public String name() {
		return name;
	}
	
	/**
	 * Returns the order assigned to this stage in a simulation.
	 * @return the order assigned to this stage in a simulation
	 */
	public int ordinal() {
		return ord;
	}
	
	/**
	 * Assigns the order that this stage have in a simulation
	 * @param ord order that this stage have in a simulation
	 */
	public void setOrder(int ord) {
		if (this.ord == -1)
			this.ord = ord;
	}

	@Override
	public int compareTo(Manifestation o) {
		if (ord > o.ord)
			return 1;
		if (ord < o.ord)
			return -1;
		return 0;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public Instance getInstance(final Disease disease, final double du, final double[] cost, final StartWithComplicationParam pInit, final double imr) {
		return new Instance(disease, du, cost, pInit, imr);
	}

	public Instance getInstance(final Disease disease, final double du, final double[] cost, double pInit, final double imr, final int nPatients) {
		final StartWithComplicationParam param = (pInit > 0.0) ? new StartWithComplicationParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), nPatients, pInit) : null;
		return new Instance(disease, du, cost, param, imr);
	}

	public class Instance implements Named {
		/** Disutility associated to a year in this stage */
		private final double du;
		/** [initial cost, yearly cost] of being in this stage */
		private final double[] cost;
		/** Probability that a patient starts in this stage */
		private final StartWithComplicationParam pInit;
		/** Increased mortality rate associated to this stage */
		private final double imr;
		private final Disease disease;
		
		/**
		 * @param du
		 * @param cost
		 * @param pInit
		 */
		private Instance(Disease disease, final double du, final double[] cost, final StartWithComplicationParam pInit, final double imr) {
			this.du = du;
			this.cost = cost;
			this.pInit = pInit;
			this.imr = imr;
			this.disease = disease;
		}
		
		public String name() {
			return name;
		}
		/**
		 * @return the disease
		 */
		public Disease getDisease() {
			return disease;
		}

		public double getDisutility() {
			return du;
		}
		
		public double[] getCosts() {
			return cost;
		}
		
		public boolean hasComplicationAtStart(Patient pat) {
			return (pInit == null) ? false : pInit.getValue(pat);
		}
		
		public double getIMR() {
			return imr;
		}
		
		/**
		 * @return the type
		 */
		public Type getType() {
			return type;
		}

	}
}
