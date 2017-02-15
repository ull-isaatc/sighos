/**
 * 
 */
package es.ull.iis.simulation.sequential.flow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.sequential.ActivityManager;
import es.ull.iis.simulation.sequential.Element;
import es.ull.iis.simulation.sequential.Resource;
import es.ull.iis.simulation.sequential.WorkThread;

/**
 * @author Iv�n Castilla
 *
 */
public class FlowBehaviour {
	private final Element elem;
	/** A structure to control the arrival of incoming branches */
	protected final TreeMap<Flow, MergeFlowControl> control = new TreeMap<Flow, MergeFlowControl>();
	/** List used by the control system. */
	protected final TreeMap<Flow, TreeMap<WorkThread, Integer>> checkList = new TreeMap<Flow, TreeMap<WorkThread,Integer>>();

	/**
	 * 
	 */
	public FlowBehaviour(Element elem) {
		this.elem = elem;
	}

	public void next(WorkThread wThread) {
		final es.ull.iis.simulation.model.flow.Flow f = wThread.getCurrentFlow();
		wThread.setLastFlow(f);
		if (f instanceof es.ull.iis.simulation.model.flow.ThreadSplitFlow) {
			final int nInstances = ((es.ull.iis.simulation.model.flow.ThreadSplitFlow)f).getNInstances();
			for (int i = 0; i < nInstances; i++)
				elem.addRequestEvent(((es.ull.iis.simulation.model.flow.ThreadSplitFlow)f).getSuccessor(), wThread.getInstanceSubsequentWorkThread(wThread.isExecutable(), f, wThread.getToken()));
	        wThread.notifyEnd();			
		}
		else if (f instanceof es.ull.iis.simulation.model.flow.ConditionalFlow) {
			final ArrayList<Flow> successorList = ((es.ull.iis.simulation.model.flow.ConditionalFlow)f).getSuccessorList();
			final ArrayList<Condition> conditionList = ((es.ull.iis.simulation.model.flow.ConditionalFlow)f).getConditionList();
			if (wThread.isExecutable()) {
				if (f instanceof es.ull.iis.simulation.model.flow.ExclusiveChoiceFlow) {
					boolean res = false;
					for (int i = 0; i < successorList.size(); i++) {
						if (!res) {
							// Check the succesor's conditions.
							res = conditionList.get(i).check(elem);
							elem.addRequestEvent(successorList.get(i), wThread.getInstanceSubsequentWorkThread(res, f, wThread.getToken()));
						}
						else
							elem.addRequestEvent(successorList.get(i), wThread.getInstanceSubsequentWorkThread(false, f, wThread.getToken()));
					}
				}
				else { // For MultiChoiceFlow
					for (int i = 0; i < successorList.size(); i++) {
						final boolean res = conditionList.get(i).check(elem);
						elem.addRequestEvent(successorList.get(i), wThread.getInstanceSubsequentWorkThread(res, f, wThread.getToken()));
					}
				}
			}
			else
				for (int i = 0; i < successorList.size(); i++)
					elem.addRequestEvent(successorList.get(i), wThread.getInstanceSubsequentWorkThread(false, f, wThread.getToken()));
			wThread.notifyEnd();
		}
		else if (f instanceof es.ull.iis.simulation.model.flow.ParallelFlow) {
			final ArrayList<Flow> successorList = ((es.ull.iis.simulation.model.flow.ParallelFlow)f).getSuccessorList();
			if (successorList.size() > 0)
				for(Flow succ : successorList)
					elem.addRequestEvent(succ, wThread.getInstanceSubsequentWorkThread(wThread.isExecutable(), f, wThread.getToken()));
	        wThread.notifyEnd();
		}
		else if (f instanceof es.ull.iis.simulation.model.flow.ProbabilitySelectionFlow) {
			final ArrayList<Flow> successorList = ((es.ull.iis.simulation.model.flow.ProbabilitySelectionFlow)f).getSuccessorList();
			final ArrayList<Double> probabilities = ((es.ull.iis.simulation.model.flow.ProbabilitySelectionFlow)f).getProbabilities();
			if (wThread.isExecutable()) {
				double ref = Math.random();
				double aux = 0.0;
				for (int i = 0; i < successorList.size(); i++) {
					boolean res = (ref >= aux) && (ref < (aux + probabilities.get(i)));
					aux += probabilities.get(i);
					elem.addRequestEvent(successorList.get(i), wThread.getInstanceSubsequentWorkThread(res, f, wThread.getToken()));					
				}			
			}
			else
				for (int i = 0; i < successorList.size(); i++)
					elem.addRequestEvent(successorList.get(i), wThread.getInstanceSubsequentWorkThread(false, f, wThread.getToken()));
			wThread.notifyEnd();
		}
		else if (f instanceof es.ull.iis.simulation.model.flow.SingleSuccessorFlow) {
			final Flow successor = ((es.ull.iis.simulation.model.flow.SingleSuccessorFlow)f).getSuccessor();
			// If there is a valid successor, the same workthread continues
			if (successor != null)
				// FIXME: I'm creating a new event. This is logically correct, but in terms of efficiency it should be better to invoke the method directly.
				// The same can be applied to every single successor flow
				elem.addRequestEvent(successor, wThread);
			else {
				wThread.notifyEnd();
			}
		}
		
	}

	public void request(WorkThread wThread) {
		final es.ull.iis.simulation.model.flow.Flow f = wThread.getCurrentFlow();
		if (!wThread.wasVisited(f)) {
			if (wThread.isExecutable()) {
				if (!f.beforeRequest(wThread)) {
					wThread.cancel(f);
					next(wThread);
				}
				else {
					if (f instanceof es.ull.iis.simulation.model.flow.StructuredFlow) {
						if (f instanceof es.ull.iis.simulation.model.flow.WhileDoFlow) {
							finish(wThread);
						}
						else if (f instanceof es.ull.iis.simulation.model.flow.ForLoopFlow) {
							int iter = Math.round((float)((es.ull.iis.simulation.model.flow.ForLoopFlow)f).getIterations().getValue(elem));
							if (iter > 0) {
								TreeMap<WorkThread, Integer> chk = checkList.get(f);
								if (chk == null) {
									chk = new TreeMap<WorkThread, Integer>();
									checkList.put(f, chk);
								}
								chk.put(wThread, iter);
								final InitializerFlow initialFlow = ((es.ull.iis.simulation.model.flow.StructuredFlow)f).getInitialFlow();
								elem.addRequestEvent(initialFlow, wThread.getInstanceDescendantWorkThread(initialFlow));						
							}
							else {
								wThread.cancel(f);
								next(wThread);
							}
						}
						else {
							final InitializerFlow initialFlow = ((es.ull.iis.simulation.model.flow.StructuredFlow)f).getInitialFlow();
							elem.addRequestEvent(initialFlow, wThread.getInstanceDescendantWorkThread(initialFlow));						
						}
					}
					else {
						next(wThread);						
					}
				}
			} else {
				wThread.updatePath(f);
				next(wThread);
			}
		} else
			wThread.notifyEnd();
	}

	public void request(WorkThread wThread, ReleaseResourcesFlow f) {
		final Collection<Resource> caughtResources = wThread.releaseResources(f.getResourcesId());
		if (caughtResources == null) {
			elem.error("Trying to release group of resources not already created. ID:" + f.getResourcesId());
		}
        TreeSet<ActivityManager> amList = new TreeSet<ActivityManager>();
        // Generate unavailability periods.
        for (Resource res : caughtResources) {
        	final long cancellationDuration = getResourceCancellation(res.getCurrentResourceType());
        	if (cancellationDuration > 0) {
				long actualTs = elem.getTs();
				res.setNotCanceled(false);
				simul.getInfoHandler().notifyInfo(new ResourceInfo(simul, res, res.getCurrentResourceType(), ResourceInfo.Type.CANCELON, actualTs));
				res.generateCancelPeriodOffEvent(actualTs, cancellationDuration);
			}
			elem.debug("Returned " + res);
        	// The resource is freed
        	if (res.releaseResource()) {
        		// The activity managers involved are included in the list
        		for (ActivityManager am : res.getCurrentManagers()) {
        			amList.add(am);
        		}
        	}
        }

        // FIXME: Preparado para hacerlo aleatorio
//					final int[] order = RandomPermutation.nextPermutation(amList.size());
//					for (int ind : order) {
//						ActivityManager am = amList.get(ind);
//						// FIXME: Esto deber�a ser un evento por cada AM
//						am.availableResource();
//					}

		for (ActivityManager am : amList) {
			am.availableResource();
		}
		
		// TODO Change by more appropriate messages
		simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wThread, ElementActionInfo.Type.ENDACT, elem.getTs()));
		if (elem.isDebugEnabled())
			elem.debug("Finishes\t" + this + "\t" + description);
		afterFinalize(wThread);
		next(wThread);
	}
	
	public void finish(WorkThread wThread) {
		final es.ull.iis.simulation.model.flow.Flow f = wThread.getCurrentFlow();
		if (f instanceof es.ull.iis.simulation.model.flow.StructuredFlow) {
			if (f instanceof es.ull.iis.simulation.model.flow.DoWhileFlow) {
				if (((es.ull.iis.simulation.model.flow.DoWhileFlow)f).getCondition().check(elem)) {
					final InitializerFlow initialFlow = ((es.ull.iis.simulation.model.flow.StructuredFlow)f).getInitialFlow();
					elem.addRequestEvent(initialFlow, wThread.getInstanceDescendantWorkThread(initialFlow));						
				} else {
					((es.ull.iis.simulation.model.flow.DoWhileFlow)f).afterFinalize(wThread);
					next(wThread);
				}
			}
			else if (f instanceof es.ull.iis.simulation.model.flow.WhileDoFlow) {
				// The loop condition is checked
				if (((es.ull.iis.simulation.model.flow.WhileDoFlow)f).getCondition().check(elem)) {
					final InitializerFlow initialFlow = ((es.ull.iis.simulation.model.flow.StructuredFlow)f).getInitialFlow();
					elem.addRequestEvent(initialFlow, wThread.getInstanceDescendantWorkThread(initialFlow));						
				} else {
					((es.ull.iis.simulation.model.flow.WhileDoFlow)f).afterFinalize(wThread);
					next(wThread);
				}
			}
			else if (f instanceof es.ull.iis.simulation.model.flow.ForLoopFlow) {
				int iter = checkList.get(f).get(wThread);
				if (--iter > 0) {
					final InitializerFlow initialFlow = ((es.ull.iis.simulation.model.flow.StructuredFlow)f).getInitialFlow();
					elem.addRequestEvent(initialFlow, wThread.getInstanceDescendantWorkThread(initialFlow));						
					checkList.get(f).put(wThread, iter);
				}
				else {
					checkList.get(f).remove(wThread);
					((es.ull.iis.simulation.model.flow.ForLoopFlow)f).afterFinalize(wThread);
					next(wThread);
				}
			}
			else {
				((es.ull.iis.simulation.model.flow.StructuredFlow)f).afterFinalize(wThread);
				next(wThread);
			}
		}		
	}
}

abstract class MergeFlow extends SingleSuccessorFlow implements JoinFlow {
	protected void arrive(WorkThread wThread) {
		if (!control.containsKey(elem))
			control.put(elem, getNewBranchesControl());
		control.get(elem).arrive(wThread);
	}

	protected boolean canReset(WorkThread wThread) {
		return control.get(elem).canReset(incomingBranches);
	}

	protected void reset(WorkThread wThread) {
		if (control.get(elem).reset())
			control.remove(elem);
	}

	protected boolean isActivated(WorkThread wThread) {
		return control.get(elem).isActivated();
	}

	public void request(WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (!beforeRequest(elem))
					wThread.setExecutable(false, this);
			}

			arrive(wThread);
			if (canPass(wThread)) {
				control.get(elem).setActivated();
				next(wThread);
			}
			else {
				// If no one of the branches was true, the thread of control must continue anyway
				if (canReset(wThread) && !isActivated(wThread))
					next(wThread.getInstanceSubsequentWorkThread(false, this, control.get(elem).getOutgoingFalseToken()));
				wThread.notifyEnd();
			}
			if (canReset(wThread))
				reset(wThread);
		} else
			wThread.notifyEnd();
	}
}

abstract class ANDJoinFlow extends MergeFlow {
	protected boolean canPass(WorkThread wThread) {
		return (!control.get(elem).isActivated() 
				&& (control.get(elem).getTrueChecked() == acceptValue));
	}
}

class ThreadMergeFlow extends ANDJoinFlow {
	public void request(WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (!beforeRequest(elem))
					wThread.setExecutable(false, this);
				arrive(wThread);
				if (canPass(wThread)) {
					control.get(elem).setActivated();
					next(wThread);
				}
				else {
					// If no one of the branches was true, the thread of control must continue anyway
					if (canReset(wThread) && !isActivated(wThread))
						next(wThread.getInstanceSubsequentWorkThread(false, this, control.get(elem).getOutgoingFalseToken()));
					wThread.notifyEnd();
				}
				if (canReset(wThread))
					reset(wThread);
			}
		} else
			wThread.notifyEnd();
	}
}
class MultiMergeFlow extends ORJoinFlow {
	protected boolean canPass(WorkThread wThread) {
		return wThread.isExecutable();
	}
}

class SimpleMergeFlow extends ORJoinFlow {
	protected SortedMap<Element, Long> lastTs;

	protected boolean canPass(WorkThread wThread) {
		if (!lastTs.containsKey(elem)) {
			lastTs.put(elem, (long)-1);
		}
		if (wThread.isExecutable() && (elem.getTs() > lastTs.get(elem))) {
			lastTs.put(elem, elem.getTs());
			return true;
		}
		return false;
	}
	
	protected void reset(WorkThread wThread) {
		lastTs.remove(elem);
		super.reset(wThread);
	}
}

class RequestResourcesFlow extends SingleSuccessorFlow implements es.ull.iis.simulation.core.flow.RequestResourcesFlow, Prioritizable, QueuedObject<WorkThread> {
    protected ActivityManager manager = null;

	public void request(WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				final Element elem = elem;
				if (beforeRequest(elem)) {
					simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wThread, ElementActionInfo.Type.REQACT, elem.getTs()));
					if (elem.isDebugEnabled())
						elem.debug("Requests\t" + this + "\t" + description);
					if (validElement(wThread)) {
						// There are enough resources to perform the activity
						final ArrayDeque<Resource> solution = isFeasible(wThread); 
						if (solution != null) {
							carryOut(wThread, solution);
						}
						else {
							queueAdd(wThread); // The element is introduced in the queue
						}
					} else {
						queueAdd(wThread); // The element is introduced in the queue
					}
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

	protected void carryOut(WorkThread wThread, ArrayDeque<Resource> solution) {
		final Element elem = elem;
		wThread.acquireResources(solution, resourcesId);
		simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wThread, ElementActionInfo.Type.STAACT, elem.getTs()));
		next(wThread);
	}
	
	protected ArrayDeque<Resource> isFeasible(WorkThread wt) {
    	if (!stillFeasible)
    		return null;
        Iterator<ActivityWorkGroup> iter = workGroupTable.randomIterator();
        while (iter.hasNext()) {
        	ActivityWorkGroup wg = iter.next();
        	ArrayDeque<Resource> solution = wg.isFeasible(wt); 
            if (solution != null) {
                wt.setExecutionWG(wg);
        		wt.getElement().debug("Can carry out \t" + this + "\t" + wt.getExecutionWG());
                return solution;
            }            
        }
        stillFeasible = false;
        return null;
	}

	protected boolean validElement(WorkThread wThread) {
		return true;
	}

	�
}
