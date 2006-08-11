/**
 * 
 */
package es.ull.isaatc.simulation.info;

import java.util.HashMap;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ElementStartFinishListener implements SimulationListener {
	private int nElem[] = new int[2];
	private HashMap<Integer, int[]> nElemXType = new HashMap<Integer, int[]>();
	protected int firstElementId;
	protected int lastElementId;

	public ElementStartFinishListener() {		
	}
	
	public int getNStartedElem() {
		return nElem[0];
	}
	
	public int getNFinishedElem() {
		return nElem[1];
	}
	
	/**
	 * @return Returns the nElemXType.
	 */
	public HashMap<Integer, int[]> getNElemXType() {
		return nElemXType;
	}

	/**
	 * @return Returns the firstElementId.
	 */
	public int getFirstElementId() {
		return firstElementId;
	}

	/**
	 * @return Returns the lastElementId.
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

	public void infoEmited(TimeChangeInfo info) {
		// TODO Auto-generated method stub
		
	}

}
