/**
 * 
 */
package es.ull.iis.simulation.whellChairs;

import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.View;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.util.ExtendedMath;
import es.ull.iis.util.Statistics;

/**
 * @author usuario
 *
 */
public class TotalPatientTimeViewAuto extends View {
	TreeMap<Element, Long> times = new TreeMap<Element, Long>();
	int contadorPacientes;

	public TotalPatientTimeViewAuto() {
		super("Tiempo total por paciente");
		contadorPacientes = 0;
		addEntrance(ElementInfo.class);
		addEntrance(SimulationEndInfo.class);
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo)info;
			
			if (eInfo.getType() == ElementInfo.Type.START) {
				times.put(eInfo.getElement(), eInfo.getTs());
				System.out.println("LLEGADA\t" + eInfo.getElement() + "\t" + times.get(eInfo.getElement()));
				contadorPacientes++;
			}
			else {
				long initTime = times.get(eInfo.getElement());
				times.put(eInfo.getElement(), eInfo.getTs() + initTime);
				System.out.println("TOTALTIME\t" + eInfo.getElement() + "\t" + times.get(eInfo.getElement()));
			}
			
		}
		else if (info instanceof SimulationEndInfo) {
			double []arrayTimes = new double[times.size()];
			int cont = 0;
			long minimo = Long.MAX_VALUE;
			long maximo = 0;
			for (long tt : times.values()) {
				// con esto contaríamos el tiemp de las tareas que no hubiesen terminado como
				//"Tiempo fin de simulación" - "tiempo de comienzo de tarea"
				// NO es la mejor opción...
//				if (tt < 0) {
//					tt = ((SimulationEndInfo) info).getTs() + tt;
//				}

				// Si controlan que tdos los pacientes hayan terminado,lo de antes sobra
				arrayTimes[cont] = tt;
				cont++;
				if(tt > maximo)
					maximo = tt;
				if (tt < minimo)
					minimo = tt;
			}
			System.out.println("PACIENTES\t" + contadorPacientes + "\tMIN\t" + minimo + "\tMAX\t" + maximo + "\tPROM\t" + Statistics.average(arrayTimes));			
		}
	}

}
