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
		
		// Tipos de Quirófanos (ambulantes y no ambulantes; de urgencias o programados)
		for (AdmissionType adm : AdmissionType.values())
			for (PatientType type : PatientType.values()) {
				ResourceType rt = new ResourceType(adm.ordinal()* 2 + type.ordinal(), this, adm.getName() + "_" + type.getName());
				new WorkGroup(adm.ordinal()* 2 + type.ordinal(), this, adm.getName() + "_" + type.getName()).add(rt, 1);
			}
			
		// Quirófanos
		for (OperationTheatre op : input.getOpTheatres()) {
			Resource r = new Resource(op.getIndex(), this, op.getName());
			for (OperationTheatre.TimetableEntry tte : op.getTimetableEntryList())
				r.addTimeTableEntry(tte.getCycle(), tte.getOpenTime(), getResourceType(tte.getAdmissionType().ordinal() * 2 + tte.getPatientType().ordinal()));				
		}
		
		// Activities, Element types, Generators
		for (PatientCategory pt : input.getPatientCategories()) {
			// Obtengo una exponencial que genere los pacientes usando como dato de partida
			// el total de pacientes llegados en el periodo de estudio
			TimeFunction expo = TimeFunctionFactory.getInstance("ExponentialVariate", (TimeUnit.MINUTES.convert(input.getObservedDays(), TimeUnit.DAYS)) / (double)pt.getTotal());
			Cycle c = new RoundedPeriodicCycle(expo.getValue(0.0), expo, 0, RoundedPeriodicCycle.Type.ROUND, TimeUnit.MINUTES.convert(1, TimeUnit.DAYS));
			ElementType et = new ElementType(pt.getIndex(), this, pt.getName() + "_" + pt.getAdmissionType().getName() + "_" + pt.getPatientType().getName());
			Activity act = new Activity(pt.getIndex(), this, pt.getName() + "_" + pt.getAdmissionType().getName() + "_" + pt.getPatientType().getName());
			act.addWorkGroup(TimeFunctionFactory.getInstance(pt.getDist() + "Variate", new Object[] {pt.getParam1(), pt.getParam2()}), getWorkGroup(pt.getAdmissionType().ordinal() * 2 + pt.getPatientType().ordinal()));
			ElementCreator ec = new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1), et, new SingleMetaFlow(pt.getIndex(), RandomVariateFactory.getInstance("ConstantVariate", 1), act));
	        new TimeDrivenGenerator(this, ec, c);
		}
		
	}

}
