/**
 * 
 */
package es.ull.iis.simulation.sequential;

import java.util.ArrayDeque;
import java.util.ArrayList;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.sequential.flow.SingleFlow;
import es.ull.iis.util.RandomPermutation;

/**
 * @author Iván Castilla
 *
 */
public class ReleaseResourcesFlow extends SingleFlow {
	private final int resourcesId;

	/**
	 * @param simul
	 * @param description
	 */
	public ReleaseResourcesFlow(Simulation simul, String description, int resourcesId) {
		super(simul, description);
		this.resourcesId = resourcesId;
	}
	
	/**
	 * @param simul
	 * @param description
	 * @param priority
	 */
	public ReleaseResourcesFlow(Simulation simul, String description, int resourcesId, int priority) {
		super(simul, description, priority);
		this.resourcesId = resourcesId;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.core.BasicStep#request(es.ull.iis.simulation.core.WorkThread)
	 */
	@Override
	public void request(WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (beforeRequest(wThread.getElement())) {
					carryOut(wThread, null);
				}
				else {
					wThread.setExecutable(false, this);
					next(wThread);
				}
			}
			else {
				wThread.updatePath(this);
				next(wThread);
			}
		} else
			wThread.notifyEnd();
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.core.BasicStep#finish(es.ull.iis.simulation.core.WorkThread)
	 */
	@Override
	public void finish(WorkThread wThread) {
		final Element elem = wThread.getElement();

		final ArrayList<ActivityManager> amList = wThread.releaseResources(resourcesId);

		final int[] order = RandomPermutation.nextPermutation(amList.size());
		for (int ind : order) {
			ActivityManager am = amList.get(ind);
			// FIXME: Esto debería ser un evento por cada AM
			am.availableResource();
		}

		// FIXME: Esto sustituye a lo anterior para que sea determinista
//		for (ActivityManager am : amList)
//			am.availableResource();
		
		// TODO Change by more appropriate messages
		simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wThread, elem, ElementActionInfo.Type.ENDACT, elem.getTs()));
		if (elem.isDebugEnabled())
			elem.debug("Finishes\t" + this + "\t" + description);
		afterFinalize(wThread.getElement());
		next(wThread);
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "REL";
	}

	@Override
	public ArrayDeque<Resource> isFeasible(WorkThread wt) {
		return null;
	}

	@Override
	public boolean validElement(WorkThread wThread) {
		return true;
	}

	@Override
	public void carryOut(WorkThread wThread, ArrayDeque<Resource> solution) {
		wThread.getSingleFlow().afterStart(wThread.getElement());
		finish(wThread);
	}
	

}
