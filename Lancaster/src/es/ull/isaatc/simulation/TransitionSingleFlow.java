/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;

import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.util.RandomPermutation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TransitionSingleFlow extends SingleFlow {

	public TransitionSingleFlow(Element elem, TransitionActivity act) {
		super(elem, act);
		executionWG = act.initialTransition;
	}

	public TransitionSingleFlow(GroupFlow parent, Element elem, TransitionActivity act) {
		super(parent, elem, act);
		executionWG = act.initialTransition;
	}

	@Override
	protected ArrayList<SingleFlow> finish() {
		if (elem.isDebugEnabled())
			elem.debug("Requests Transition to\t" + act + "\t" + act.getDescription());
		
		// Beginning MUTEX access to activity manager
		act.getManager().waitSemaphore();

		elem.debug("MUTEX\trequesting\t" + act + " (req. act.)");    	
        elem.waitSemaphore();
		elem.debug("MUTEX\tadquired\t" + act + " (req. act.)");
		ArrayList<ActivityManager> amList = null;
		if (act.isFeasible(this)) { 
			amList = releaseCaughtResources();
			if (executionWG == ((TransitionActivity)act).finalTransition) {
				if (!act.isNonPresential())
					elem.setCurrentSF(null);				
			}
			else {
				// Resets the time left counter not to be considered an interruptible activity
				timeLeft = Double.NaN;
				elem.carryOutActivity(this);
			}
		}
		// There are not possible transitions. The element must stay in the same activity.
		else {
			double auxTs = elem.getTs() + executionWG.getDuration();
			elem.getSimul().getListenerController().notifyListeners(new ElementInfo(elem, ElementInfo.Type.STAACT,
					elem.getTs(), act.getIdentifier()));
			elem.debug("Re-starts\t" + act + "\t" + act.getDescription());
			elem.addEvent(elem.new FinalizeActivityEvent(auxTs , this));
		}
		elem.debug("MUTEX\treleasing\t" + act + " (req. act.)");    	
    	elem.signalSemaphore();
		elem.debug("MUTEX\tfreed\t" + act + " (req. act.)");    	
		
		// Ending MUTEX access to activity manager
		act.getManager().signalSemaphore();

		ArrayList<SingleFlow> sfList = null;
		// Resources from the previous transition were freed
		if (amList != null) {
			int[] order = RandomPermutation.nextPermutation(amList.size());
			for (int ind : order) {
				ActivityManager am = amList.get(ind);
				am.waitSemaphore();
				am.availableResource();
				am.signalSemaphore();
			}
			// Only if there are no more transitions
			if (elem.getCurrentSF() == null) {
				// FIXME: CUIDADO CON ESTO!!! Comparando con 0.0
				if (timeLeft == 0.0) {
			    	sfList = new ArrayList<SingleFlow>();
			        finished = true;
			        if (parent != null)
			            sfList.addAll(parent.finish());
				}
			}
		}

        return sfList;
	}
}
