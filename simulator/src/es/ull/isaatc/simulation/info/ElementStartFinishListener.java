/**
 * 
 */
package es.ull.isaatc.simulation.info;

import java.util.HashMap;

/**
 * A listener to compute the amount of started and finished elements.
 * @author Iván Castilla Rodríguez
 */
public class ElementStartFinishListener implements SimulationListener {
	/** The amount of started and finished elements. */
	private int nElem[] = new int[2];
	/** The amount of started and finished elements by element type. */
	private HashMap<Integer, int[]> nElemXType = new HashMap<Integer, int[]>();
	/** The identifier of the first element created. */
	protected int firstElementId;
	/** The identifier of the last element created. */
	protected int lastElementId;

	/**
	 * Default constructor. 
	 *
	 */
	public ElementStartFinishListener() {		
	}
	
	/**
	 * Returns the amount of started elements.
	 * @return The amount of started elements.
	 */
	public int getNStartedElem() {
		return nElem[0];
	}
	
	/**
	 * Returns the amount of finished elements.
	 * @return The amount of finished elements.
	 */
	public int getNFinishedElem() {
		return nElem[1];
	}
	
	/**
	 * Returns the amount of started and finished elements by element type.
	 * @return The amount of started and finished elements by element type.
	 */
	public HashMap<Integer, int[]> getNElemXType() {
		return nElemXType;
	}

	/**
	 * Returns the identifier of the first element created.
	 * @return The identifier of the first element created.
	 */
	public int getFirstElementId() {
		return firstElementId;
	}

	/**
	 * Returns the identifier of the last element created.
	 * @return The identifier of the last element created.
	 */
	public int getLastElementId() {
		return lastElementId;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationComponentInfo)
	 */
	public void infoEmited(SimulationObjectInfo info) {
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo)info;
			int []aux;
			switch (eInfo.getType()) {
				case START:
					nElem[0]++;
					aux = nElemXType.get(eInfo.getValue());
					if (aux == null)
						aux = new int[2];
					aux[0]++;
					nElemXType.put(eInfo.getValue(), aux);
				break;
				case FINISH:
					nElem[1]++;
					aux = nElemXType.get(eInfo.getValue());
					if (aux == null)
						aux = new int[2];
					aux[1]++;
					nElemXType.put(eInfo.getValue(), aux);
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationStartInfo)
	 */
	public void infoEmited(SimulationStartInfo info) {
		firstElementId = info.getFirstElementId();
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
	 */
	public void infoEmited(SimulationEndInfo info) {
		lastElementId = info.getLastElementId();
	}

	// Nothing to do
	public void infoEmited(TimeChangeInfo info) {
	}

}
