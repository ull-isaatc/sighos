/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen;

import java.util.concurrent.TimeUnit;

import simkit.random.RandomNumberFactory;
import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.RoundedPeriodicCycle;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimGS extends StandAloneLPSimulation {
	private GSExcelInputWrapper input;
	
	public SimGS(int id, double startTs, double endTs, GSExcelInputWrapper input) {
		super(id, "General Surgery (" + id +")", startTs, endTs);
		this.input = input;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Simulation#createModel()
	 */
	@Override
	protected void createModel() {
		// Añado una semilla aleatoria
		RandomVariateFactory.setDefaultRandomNumber(RandomNumberFactory.getInstance(System.currentTimeMillis()));
		
		// Tipos de Quirófanos (ambulantes y no ambulantes)
		for (OperationTheatreType type : OperationTheatreType.values()) {
			ResourceType rt = new ResourceType(type.ordinal(), this, type.getName());
			new WorkGroup(type.ordinal(), this, type.getName()).add(rt, 1);
		}
		WorkGroup wgNoAmb = getWorkGroup(OperationTheatreType.OR.ordinal());
		WorkGroup wgAmb = getWorkGroup(OperationTheatreType.DC.ordinal());
			
		// Quirófanos
		for (OperationTheatre op : input.getOpTheatres()) {
			Resource r = new Resource(op.getIndex(), this, op.getName());
			r.addTimeTableEntry(op.getCycle(), TimeUnit.MINUTES.convert(input.getOpTheatreAvailabilityHours(), TimeUnit.HOURS), getResourceType(op.getType().ordinal()));
		}
		
		// Activities, Element types, Generators
		for (PatientType pt : input.getPatientTypes()) {
			// Obtengo una exponencial que genere los pacientes usando como dato de partida
			// el total de pacientes llegados en el periodo de estudio
			TimeFunction expo = TimeFunctionFactory.getInstance("ExponentialVariate", (TimeUnit.MINUTES.convert(input.getObservedDays(), TimeUnit.DAYS)) / pt.getTotal());
			Cycle c = new RoundedPeriodicCycle(expo.getValue(0.0), expo, 0, RoundedPeriodicCycle.Type.ROUND, TimeUnit.MINUTES.convert(1, TimeUnit.DAYS));
			ElementCreator ec = new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1));

			if (pt.getTotal(OperationTheatreType.OR) > 0) {
				ElementType etOR = new ElementType(pt.getIndex(OperationTheatreType.OR), this, pt.getName() + " OR");
				Activity actOR = new Activity(pt.getIndex(OperationTheatreType.OR), this, pt.getName() + " OR");
				actOR.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", new Object[] {pt.getAverage(OperationTheatreType.OR), pt.getStdDev(OperationTheatreType.OR)}), wgNoAmb);
				ec.add(etOR, new SingleMetaFlow(pt.getIndex(OperationTheatreType.OR), RandomVariateFactory.getInstance("ConstantVariate", 1), actOR), pt.getProbability(OperationTheatreType.OR));
			}
			if (pt.getTotal(OperationTheatreType.DC) > 0) {
				ElementType etDC = new ElementType(pt.getIndex(OperationTheatreType.DC), this, "Patient " + pt.getName() + " DC");
				Activity actDC = new Activity(pt.getIndex(OperationTheatreType.DC), this, "A" + pt.getName() + " DC");
				actDC.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", new Object[] {pt.getAverage(OperationTheatreType.DC), pt.getStdDev(OperationTheatreType.DC)}), wgAmb);
				ec.add(etDC, new SingleMetaFlow(pt.getIndex(OperationTheatreType.DC), RandomVariateFactory.getInstance("ConstantVariate", 1), actDC), pt.getProbability(OperationTheatreType.DC));
			}				
	        new TimeDrivenGenerator(this, ec, c);
		}
		
	}

}
