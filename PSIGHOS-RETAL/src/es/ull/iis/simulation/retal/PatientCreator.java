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
 * @author Iván Castilla
 *
 */
public class PatientCreator implements BasicElementCreator {
	/** Number of objects created each time this creator is invoked. */
	private final int nPatients;
	/** Associated simulation */
	private final RETALSimulation simul;
	/** A distribution to characterize the initial age of each generated patient */
	private final TimeFunction initialAges;
	/** Probability of being men */
	private final double pMen;
	private final Random RNG_SEX = new Random();
	private final Patient[] copyOf;
	private final int intervention;

	/**
	 * @param simul
	 * @param nPatients
	 * @param pMen
	 */
	public PatientCreator(RETALSimulation simul, int nPatients, double pMen, TimeFunction initialAges) {
		this.nPatients = nPatients;
		this.simul = simul;
		this.pMen = pMen;
		this.initialAges = initialAges;
		this.copyOf = null;
		this.intervention = 0;
	}
	
	/**
	 * @param simul
	 * @param nPatients
	 * @param pMen
	 */
	public PatientCreator(RETALSimulation simul, Patient[] copyOf, double pMen, TimeFunction initialAges, int intervention) {
		this.nPatients = copyOf.length;
		this.simul = simul;
		this.pMen = pMen;
		this.initialAges = initialAges;
		this.copyOf = copyOf;
		this.intervention = intervention;
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.sequential.BasicElementCreator#create(es.ull.iis.simulation.sequential.Generator)
	 */
	@Override
	public void create(Generator gen) {
		if (copyOf == null) {
			for (int i = 0; i < nPatients; i++) {
				final double age = initialAges.getValue(0);
				final int sex = (RNG_SEX.nextDouble() < pMen) ? 0 : 1;
				Patient p = new Patient(simul, age, sex, intervention);
				simul.addGeneratedPatient(p, i);
				final BasicElement.DiscreteEvent ev = p.getStartEvent(simul.getTs());
				p.addEvent(ev);
			}
		}
		else {
			for (int i = 0; i < nPatients; i++) {
				Patient p = new Patient(simul, copyOf[i], intervention);
				simul.addGeneratedPatient(p, i);
				final BasicElement.DiscreteEvent ev = p.getStartEvent(simul.getTs());
				p.addEvent(ev);
			}			
		}
	}	
}
