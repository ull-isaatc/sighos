/**
 * 
 */
package es.ull.isaatc.simulation.common.factory;

import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationFactory {
	/**
	 * Define los distintos tipos de simulación que pueden probarse<ul>
	 * <il>SEQUENTIAL: Ejecución secuencial optimizada</li>
	 * <il>SEQUENTIAL2: Ejecución secuencial a la que se le añade un buffer intermedio para que se parezca más a la ejecución paralela</li>
	 * <il>SIMEVENTS: Ejecución que intenta optimizar los eventos simultáneos usando un pool de threads</li>
	 * <il>SIMEVENTS3PHASE: Ejecución que intenta optimizar los eventos simultáneos usando un pool de threads. En este caso se
	 * trata de mejorar el resultado haciendo una ejecución en dos fases (más propiamente en 3) que elimina muchos de los bloqueos.</li>
	 * <il>INTERVAL: Modificación de SIMEVENTS para que se pueda adaptar en el futuro para una ejecución con reloj intervalar.
	 * La mayor diferencia es que los hilos de ejecución se definen internamente a la simulación.</li>
	 * <il>BUFFERED: Versión modificada de INTERVAL que incluye buffers internos a cada hilo de ejecución para almacenar
	 * localmente eventos de próxima ejecución y eventos a planificar en la lista de espera</li>
	 * <il>GROUPED: Versión modificada de BUFFERED que envía los eventos en bloques. Esto también obliga a modificar la
	 * cola de eventos futuros para que los almacene en bloques.</li>
	 * <il>GROUPEDX: Extensión de GROUPED que utiliza el hilo principal (que reparte los eventos) como otro hilo ejecutor más</li>
	 * <il>GROUPEDXX: Pequeña modificación de GROUPEDX que intenta no avisar a todos los AM desde Element, sino sólo a aquellos 
	 * no previamente avisados desde addAvailableElementEvents</il>
	 * <il>GROUPED3PHASE: Híbrido de GROUPED y SIMEVENTS3PHASE , que elimina completamente los semáforos del AM.</li>
	 * @author Iván Castilla Rodríguez
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
