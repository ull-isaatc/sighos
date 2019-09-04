/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.params.StartWithComplicationParam;

/**
 * A stage of a {@link DiabetesChronicComplications chronic complication} defined in the model. Different chronic complications submodels
 * can define different stages that are registered at the beginning of the simulation. 
 * @author Iván Castilla Rodríguez
 *
 */
public class DiabetesComplicationStage implements Named, Comparable<DiabetesComplicationStage> {
	/** Short name of the complication stage */
	private final String name;
	/** Full description of the complication stage */
	private final String description;
	/** Main chronic complication this stage is related to */
	private final DiabetesChronicComplications mainComp;
	/** An index to be used when this class is used in TreeMaps or other ordered structures. The order is unique among the
	 * complications defined to be used within a simulation */ 
	private int ord = -1;
	
	/**
	 * Creates a new complication stage of a {@link DiabetesChronicComplications chronic complication} defined in the model
	 * @param name Name of the stage
	 * @param description Full description of the stage
	 * @param mainComp Main chronic complication
	 */
	public DiabetesComplicationStage(String name, String description, DiabetesChronicComplications mainComp) {
		this.name = name;
		this.description = description;
		this.mainComp = mainComp;
	}
	
	/**
	 * Returns the description of the complication
	 * @return the description of the complication
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@link DiabetesChronicComplications} this complication stage is related to.
	 * @return the {@link DiabetesChronicComplications} this complication stage is related to
	 */
	public DiabetesChronicComplications getComplication() {
		return mainComp;
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
	public int compareTo(DiabetesComplicationStage o) {
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

	public Instance getInstance(final double du, final double[] cost, final StartWithComplicationParam pInit, final double imr) {
		return new Instance(du, cost, pInit, imr);
	}

	public Instance getInstance(final double du, final double[] cost, double pInit, final double imr, final int nPatients) {
		final StartWithComplicationParam param = (pInit > 0.0) ? new StartWithComplicationParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), nPatients, pInit) : null;
		return new Instance(du, cost, param, imr);
	}

	public class Instance {
		/** Disutility associated to a year in this stage */
		private final double du;
		/** [initial cost, yearly cost] of being in this stage */
		private final double[] cost;
		/** Probability that a patient starts in this stage */
		private final StartWithComplicationParam pInit;
		/** Increased mortality rate associated to this stage */
		private final double imr;
		
		/**
		 * @param du
		 * @param cost
		 * @param pInit
		 */
		private Instance(final double du, final double[] cost, final StartWithComplicationParam pInit, final double imr) {
			this.du = du;
			this.cost = cost;
			this.pInit = pInit;
			this.imr = imr;
		}
		
		public double getDisutility() {
			return du;
		}
		
		public double[] getCosts() {
			return cost;
		}
		
		public boolean hasComplicationAtStart(DiabetesPatient pat) {
			return (pInit == null) ? false : pInit.getValue(pat);
		}
		
		public double getIMR() {
			return imr;
		}
	}
}
