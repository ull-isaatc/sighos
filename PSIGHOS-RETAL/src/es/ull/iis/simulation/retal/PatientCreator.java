/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.Random;

import es.ull.iis.simulation.sequential.BasicElement;
import es.ull.iis.simulation.sequential.BasicElementCreator;
import es.ull.iis.simulation.sequential.Generator;
import es.ull.isaatc.function.ConstantFunction;
import es.ull.isaatc.function.TimeFunction;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class PatientCreator implements BasicElementCreator {
	/** Number of objects created each time this creator is invoked. */
	protected final TimeFunction nPatients;
	/** Associated simulation */
	protected final RETALSimulation simul;
	/** A distribution to characterize the initial age of each generated patient */
	protected final TimeFunction initialAges;
	/** Probability of being men */
	protected final double pMen;
	protected final Random RNG_SEX = new Random();

	/**
	 * 
	 */
	public PatientCreator(RETALSimulation simul, TimeFunction nPatients, double pMen, TimeFunction initialAges) {
		// TODO Auto-generated constructor stub
		this.nPatients = nPatients;
		this.simul = simul;
		this.pMen = pMen;
		this.initialAges = initialAges;
	}

	protected Patient createPatient() {
		return new Patient(simul, initialAges.getValue(0), (RNG_SEX.nextDouble() < pMen) ? 0 : 1);
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.sequential.BasicElementCreator#create(es.ull.isaatc.simulation.sequential.Generator)
	 */
	@Override
	public void create(Generator gen) {
		int n = (int)nPatients.getPositiveValue(gen.getTs());
		for (int i = 0; i < n; i++) {
			Patient p = createPatient();
			final BasicElement.DiscreteEvent ev = p.getStartEvent(simul.getTs());
			p.addEvent(ev);
		}
	}

}
