/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.Random;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.sequential.BasicElement;
import es.ull.iis.simulation.sequential.BasicElementCreator;
import es.ull.iis.simulation.sequential.Generator;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class PatientCreator implements BasicElementCreator {
	/** Number of objects created each time this creator is invoked. */
	protected final int nPatients;
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
	public PatientCreator(RETALSimulation simul, int nPatients, double pMen, TimeFunction initialAges) {
		// TODO Auto-generated constructor stub
		this.nPatients = nPatients;
		this.simul = simul;
		this.pMen = pMen;
		this.initialAges = initialAges;
	}

	protected Patient createPatient() {
		final int sex = (RNG_SEX.nextDouble() < pMen) ? 0 : 1;
		final double age = initialAges.getValue(0);
		return new Patient(simul, age, sex);
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.sequential.BasicElementCreator#create(es.ull.iis.simulation.sequential.Generator)
	 */
	@Override
	public void create(Generator gen) {
		for (int i = 0; i < nPatients; i++) {
			Patient p = createPatient();
			final BasicElement.DiscreteEvent ev = p.getStartEvent(simul.getTs());
			p.addEvent(ev);
		}
	}

}
