/**
 * 
 */
package es.ull.isaatc.simulation.common.factory;

import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class SimulationFactory {
	/**
	 * Define los distintos tipos de simulaci�n que pueden probarse<ul>
	 * <il>SEQUENTIAL: Ejecuci�n secuencial optimizada</li>
	 * <il>SEQUENTIAL2: Ejecuci�n secuencial a la que se le a�ade un buffer intermedio para que se parezca m�s a la ejecuci�n paralela</li>
	 * <il>SIMEVENTS: Ejecuci�n que intenta optimizar los eventos simult�neos usando un pool de threads</li>
	 * <il>SIMEVENTS3PHASE: Ejecuci�n que intenta optimizar los eventos simult�neos usando un pool de threads. En este caso se
	 * trata de mejorar el resultado haciendo una ejecuci�n en dos fases (m�s propiamente en 3) que elimina muchos de los bloqueos.</li>
	 * <il>INTERVAL: Modificaci�n de SIMEVENTS para que se pueda adaptar en el futuro para una ejecuci�n con reloj intervalar.
	 * La mayor diferencia es que los hilos de ejecuci�n se definen internamente a la simulaci�n.</li>
	 * <il>BUFFERED: Versi�n modificada de INTERVAL que incluye buffers internos a cada hilo de ejecuci�n para almacenar
	 * localmente eventos de pr�xima ejecuci�n y eventos a planificar en la lista de espera</li>
	 * <il>GROUPED: Versi�n modificada de BUFFERED que env�a los eventos en bloques. Esto tambi�n obliga a modificar la
	 * cola de eventos futuros para que los almacene en bloques.</li>
	 * <il>GROUPEDX: Extensi�n de GROUPED que utiliza el hilo principal (que reparte los eventos) como otro hilo ejecutor m�s</li>
	 * <il>GROUPEDXX: Peque�a modificaci�n de GROUPEDX que intenta no avisar a todos los AM desde Element, sino s�lo a aquellos 
	 * no previamente avisados desde addAvailableElementEvents</il>
	 * <il>GROUPED3PHASE: H�brido de GROUPED y SIMEVENTS3PHASE , que elimina completamente los sem�foros del AM.</li>
	 * @author Iv�n Castilla Rodr�guez
	 */
	public enum SimulationType {
		SEQUENTIAL, 
		SEQUENTIAL2, 
		SIMEVENTS,
		SIMEVENTS3PHASE,
		INTERVAL,
		BUFFERED,
		GROUPED,
		GROUPEDX,
		GROUPEDXX,
		PASIVE,
		GROUPED3PHASE2
		
	}
	public static SimulationObjectFactory getInstance(SimulationType type, int id, String description, TimeUnit unit, TimeStamp startTs, TimeStamp endTs) {
		switch (type) {
		case SEQUENTIAL: return new es.ull.isaatc.simulation.sequential.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case SEQUENTIAL2: return new es.ull.isaatc.simulation.lessSequential.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case SIMEVENTS: return new es.ull.isaatc.simulation.threaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case SIMEVENTS3PHASE: return new es.ull.isaatc.simulation.optThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case INTERVAL: return new es.ull.isaatc.simulation.intervalThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case BUFFERED: return new es.ull.isaatc.simulation.intervalBufferThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case GROUPED: return new es.ull.isaatc.simulation.groupedThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case GROUPEDX: return new es.ull.isaatc.simulation.groupedExtraThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case GROUPEDXX: return new es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case PASIVE: return new es.ull.isaatc.simulation.groupedExtraPasiveThreads.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case GROUPED3PHASE2: return new es.ull.isaatc.simulation.xoptGroupedThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		}
		return null;
	}

	public static SimulationObjectFactory getInstance(SimulationType type, int id, String description, TimeUnit unit, long startTs, long endTs) {
		switch (type) {
		case SEQUENTIAL: return new es.ull.isaatc.simulation.sequential.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case SEQUENTIAL2: return new es.ull.isaatc.simulation.lessSequential.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case SIMEVENTS: return new es.ull.isaatc.simulation.threaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case SIMEVENTS3PHASE: return new es.ull.isaatc.simulation.optThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case INTERVAL: return new es.ull.isaatc.simulation.intervalThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case BUFFERED: return new es.ull.isaatc.simulation.intervalBufferThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case GROUPED: return new es.ull.isaatc.simulation.groupedThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case GROUPEDX: return new es.ull.isaatc.simulation.groupedExtraThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case GROUPEDXX: return new es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case PASIVE: return new es.ull.isaatc.simulation.groupedExtraPasiveThreads.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case GROUPED3PHASE2: return new es.ull.isaatc.simulation.xoptGroupedThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		}
		return null;
	}
}
