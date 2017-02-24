package es.ull.iis.simulation.sequential;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.flow.ANDJoinFlow;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.DelayFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.FlowExecutor;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.MergeFlow;
import es.ull.iis.simulation.model.flow.MultiMergeFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.simulation.model.flow.SimpleMergeFlow;
import es.ull.iis.simulation.model.flow.ThreadMergeFlow;
import es.ull.iis.simulation.sequential.flow.GeneralizedMergeFlowControl;
import es.ull.iis.simulation.sequential.flow.MergeFlowControl;
import es.ull.iis.simulation.sequential.flow.SafeMergeFlowControl;
import es.ull.iis.simulation.variable.EnumVariable;

/**
 * Represents case instances that make use of activity flows in order to carry out
 * their events.
 * 
 * @author Iván Castilla Rodríguez
 */
public class ElementEngine extends EventSourceEngine<Element> implements es.ull.iis.simulation.model.ElementEngine {
	/** Activity queues in which this element is. This list is used to notify the activities
	 * when the element becomes available. */
	protected final ArrayList<FlowExecutor> inQueue = new ArrayList<FlowExecutor>();
	/** Presential work thread which the element is currently carrying out */
	protected FlowExecutor current = null;
	/** Main execution thread */
	protected final FlowExecutor mainThread;
	/** A structure to control the arrival of incoming branches */
	protected final TreeMap<Flow, MergeFlowControl> control = new TreeMap<Flow, MergeFlowControl>();
	/** List used by the control system. */
	protected final TreeMap<Flow, TreeMap<FlowExecutor, Integer>> checkList = new TreeMap<Flow, TreeMap<FlowExecutor,Integer>>();
	protected TreeMap<Flow, Long> lastTs = new TreeMap<Flow, Long>();

	private final es.ull.iis.simulation.model.Element modelElem;
	
	/**
	 * Creates a new element.
	 * @param simul Simulation object
	 * @param et Element type this element belongs to
	 * @param flow First step of this element's flow
	 */
	public ElementEngine(SequentialSimulationEngine simul, es.ull.iis.simulation.model.Element modelElem) {
		super(simul, modelElem, "E");
		this.modelElem = modelElem;
		mainThread = FlowExecutor.getInstanceMainWorkThread(this);
	}

	/**
	 * @return the modelElem
	 */
	public es.ull.iis.simulation.model.Element getModelElem() {
		return modelElem;
	}

	/**
	 * If the element is currently performing an activity, returns the work
	 * thread used by the element. If the element is not performing any presential 
	 * activity, returns null.
	 * @return The work thread corresponding to the current presential activity being
	 * performed by this element.
	 */
	public FlowExecutor getCurrent() {
		return current;
	}

	/**
	 * Sets the work thread corresponding to the current presential activity 
	 * being performed by this element.Creates the events to notify the activities that this element is now
	 * available. All the activities this element is in their queues are notified. 
	 * @param current The work thread corresponding to the current presential activity 
	 * being performed by this element. A null value indicates that the element has 
	 * finished performing the activity.
	 */
	public void setCurrent(FlowExecutor current) {
		this.current = current;
		if (current == null) {
			// Checks if there are pending activities that haven't noticed the
			// element availability
			for (int i = 0; (current == null) && (i < inQueue.size()); i++) {
				final FlowExecutor fe = inQueue.get(i);
	            final RequestResourcesFlow act = (RequestResourcesFlow) fe.getCurrentFlow();

				if (modelElem.isDebugEnabled())
					modelElem.debug("Calling availableElement()\t" + act + "\t" + act.getDescription());
				fe.availableElement(simul.getRequestResource(act));
			}
		}
	}

	/**
	 * Notifies a new work thread is waiting in an activity queue.
	 * @param wt Work thread waiting in queue.
	 */
	@Override
	public void incInQueue(FlowExecutor fe) {
			inQueue.add(fe);
	}

	/**
	 * Notifies a work thread has finished waiting in an activity queue.
	 * @param wt Work thread that was waiting in a queue.
	 */
	@Override
	public void decInQueue(FlowExecutor fe) {
			inQueue.remove(fe);
	}

	public void next(FlowExecutor fe) {
		final es.ull.iis.simulation.model.flow.Flow f = fe.getCurrentFlow();
		fe.setLastFlow(f);
		if (f instanceof es.ull.iis.simulation.model.flow.ThreadSplitFlow) {
			final int nInstances = ((es.ull.iis.simulation.model.flow.ThreadSplitFlow)f).getNInstances();
			for (int i = 0; i < nInstances; i++)
				modelElem.addRequestEvent(((es.ull.iis.simulation.model.flow.ThreadSplitFlow)f).getSuccessor(), fe.getInstanceSubsequentWorkThread(fe.isExecutable(), f, fe.getToken()));
	        fe.notifyEnd();			
		}
		else if (f instanceof es.ull.iis.simulation.model.flow.ConditionalFlow) {
			final ArrayList<Flow> successorList = ((es.ull.iis.simulation.model.flow.ConditionalFlow)f).getSuccessorList();
			final ArrayList<Condition> conditionList = ((es.ull.iis.simulation.model.flow.ConditionalFlow)f).getConditionList();
			if (fe.isExecutable()) {
				if (f instanceof es.ull.iis.simulation.model.flow.ExclusiveChoiceFlow) {
					boolean res = false;
					for (int i = 0; i < successorList.size(); i++) {
						if (!res) {
							// Check the succesor's conditions.
							res = conditionList.get(i).check(fe);
							modelElem.addRequestEvent(successorList.get(i), fe.getInstanceSubsequentWorkThread(res, f, fe.getToken()));
						}
						else
							modelElem.addRequestEvent(successorList.get(i), fe.getInstanceSubsequentWorkThread(false, f, fe.getToken()));
					}
				}
				else { // For MultiChoiceFlow
					for (int i = 0; i < successorList.size(); i++) {
						final boolean res = conditionList.get(i).check(fe);
						modelElem.addRequestEvent(successorList.get(i), fe.getInstanceSubsequentWorkThread(res, f, fe.getToken()));
					}
				}
			}
			else
				for (int i = 0; i < successorList.size(); i++)
					modelElem.addRequestEvent(successorList.get(i), fe.getInstanceSubsequentWorkThread(false, f, fe.getToken()));
			fe.notifyEnd();
		}
		else if (f instanceof es.ull.iis.simulation.model.flow.ParallelFlow) {
			final ArrayList<Flow> successorList = ((es.ull.iis.simulation.model.flow.ParallelFlow)f).getSuccessorList();
			if (successorList.size() > 0)
				for(Flow succ : successorList)
					modelElem.addRequestEvent(succ, fe.getInstanceSubsequentWorkThread(fe.isExecutable(), f, fe.getToken()));
	        fe.notifyEnd();
		}
		else if (f instanceof es.ull.iis.simulation.model.flow.ProbabilitySelectionFlow) {
			final ArrayList<Flow> successorList = ((es.ull.iis.simulation.model.flow.ProbabilitySelectionFlow)f).getSuccessorList();
			final ArrayList<Double> probabilities = ((es.ull.iis.simulation.model.flow.ProbabilitySelectionFlow)f).getProbabilities();
			if (fe.isExecutable()) {
				double ref = Math.random();
				double aux = 0.0;
				for (int i = 0; i < successorList.size(); i++) {
					boolean res = (ref >= aux) && (ref < (aux + probabilities.get(i)));
					aux += probabilities.get(i);
					modelElem.addRequestEvent(successorList.get(i), fe.getInstanceSubsequentWorkThread(res, f, fe.getToken()));					
				}			
			}
			else
				for (int i = 0; i < successorList.size(); i++)
					modelElem.addRequestEvent(successorList.get(i), fe.getInstanceSubsequentWorkThread(false, f, fe.getToken()));
			fe.notifyEnd();
		}
		else if (f instanceof es.ull.iis.simulation.model.flow.SingleSuccessorFlow) {
			final Flow successor = ((es.ull.iis.simulation.model.flow.SingleSuccessorFlow)f).getSuccessor();
			// If there is a valid successor, the same workthread continues
			if (successor != null)
				// FIXME: I'm creating a new event. This is logically correct, but in terms of efficiency it should be better to invoke the method directly.
				// The same can be applied to every single successor flow
				modelElem.addRequestEvent(successor, fe);
			else {
				fe.notifyEnd();
			}
		}
		
	}
	
	public void request(FlowExecutor fe) {
		final es.ull.iis.simulation.model.flow.Flow f = fe.getCurrentFlow();
		if (!fe.wasVisited(f)) {
			if (f instanceof MergeFlow) {
				if (fe.isExecutable()) {
					if (!f.beforeRequest(fe)) {
						fe.cancel(f);
					}
				}
				if (fe.isExecutable() || !(f instanceof ThreadMergeFlow)) {				
					arrive(fe, (MergeFlow)f);
					if (canPass(fe, (MergeFlow)f)) {
						control.get(f).setActivated();
						next(fe);
					}
					else {
						// If no one of the branches was true, the thread of control must continue anyway
						if (canReset(fe, (MergeFlow)f) && !isActivated(fe, (MergeFlow)f))
							next(fe.getInstanceSubsequentWorkThread(false, f, control.get(f).getOutgoingFalseToken()));
						fe.notifyEnd();
					}
					if (canReset(fe, (MergeFlow)f))
						reset(fe, (MergeFlow)f);
				}
			}
			else {
				if (fe.isExecutable()) {
					if (!f.beforeRequest(fe)) {
						fe.cancel(f);
						next(fe);
					}
					else {
						if (f instanceof es.ull.iis.simulation.model.flow.StructuredFlow) {
							if (f instanceof es.ull.iis.simulation.model.flow.WhileDoFlow) {
								finish(fe);
							}
							else if (f instanceof es.ull.iis.simulation.model.flow.ForLoopFlow) {
								int iter = Math.round((float)((es.ull.iis.simulation.model.flow.ForLoopFlow)f).getIterations().getValue(fe));
								if (iter > 0) {
									TreeMap<FlowExecutor, Integer> chk = checkList.get(f);
									if (chk == null) {
										chk = new TreeMap<FlowExecutor, Integer>();
										checkList.put(f, chk);
									}
									chk.put(fe, iter);
									final InitializerFlow initialFlow = ((es.ull.iis.simulation.model.flow.StructuredFlow)f).getInitialFlow();
									modelElem.addRequestEvent(initialFlow, fe.getInstanceDescendantWorkThread(initialFlow));						
								}
								else {
									fe.cancel(f);
									next(fe);
								}
							}
							else {
								final InitializerFlow initialFlow = ((es.ull.iis.simulation.model.flow.StructuredFlow)f).getInitialFlow();
								modelElem.addRequestEvent(initialFlow, fe.getInstanceDescendantWorkThread(initialFlow));						
							}
						}
						else if (f instanceof ReleaseResourcesFlow) {
							fe.releaseResources((ReleaseResourcesFlow)f);
							next(fe);
						}
						else if (f instanceof RequestResourcesFlow) {
							fe.acquireResources((RequestResourcesFlow)f);
						}
						else if (f instanceof DelayFlow) {
							fe.startDelay((DelayFlow)f);
						}
						else {
							next(fe);						
						}
					}
				} else {
					fe.updatePath(f);
					next(fe);
				}
			}
		} else
			fe.notifyEnd();
	}

	public void finish(FlowExecutor fe) {
		final es.ull.iis.simulation.model.flow.Flow f = fe.getCurrentFlow();
		if (f instanceof es.ull.iis.simulation.model.flow.StructuredFlow) {
			if (f instanceof es.ull.iis.simulation.model.flow.DoWhileFlow) {
				if (((es.ull.iis.simulation.model.flow.DoWhileFlow)f).getCondition().check(fe)) {
					final InitializerFlow initialFlow = ((es.ull.iis.simulation.model.flow.StructuredFlow)f).getInitialFlow();
					modelElem.addRequestEvent(initialFlow, fe.getInstanceDescendantWorkThread(initialFlow));						
				} else {
					((es.ull.iis.simulation.model.flow.DoWhileFlow)f).afterFinalize(fe);
					next(fe);
				}
			}
			else if (f instanceof es.ull.iis.simulation.model.flow.WhileDoFlow) {
				// The loop condition is checked
				if (((es.ull.iis.simulation.model.flow.WhileDoFlow)f).getCondition().check(fe)) {
					final InitializerFlow initialFlow = ((es.ull.iis.simulation.model.flow.StructuredFlow)f).getInitialFlow();
					modelElem.addRequestEvent(initialFlow, fe.getInstanceDescendantWorkThread(initialFlow));						
				} else {
					((es.ull.iis.simulation.model.flow.WhileDoFlow)f).afterFinalize(fe);
					next(fe);
				}
			}
			else if (f instanceof es.ull.iis.simulation.model.flow.ForLoopFlow) {
				int iter = checkList.get(f).get(fe);
				if (--iter > 0) {
					final InitializerFlow initialFlow = ((es.ull.iis.simulation.model.flow.StructuredFlow)f).getInitialFlow();
					modelElem.addRequestEvent(initialFlow, fe.getInstanceDescendantWorkThread(initialFlow));						
					checkList.get(f).put(fe, iter);
				}
				else {
					checkList.get(f).remove(fe);
					((es.ull.iis.simulation.model.flow.ForLoopFlow)f).afterFinalize(fe);
					next(fe);
				}
			}
			else if (f instanceof ActivityFlow) {
				if (fe.endActivity((ActivityFlow)f)) {
					((es.ull.iis.simulation.model.flow.StructuredFlow)f).afterFinalize(fe);
					next(fe);
				}
				else {
					// Request again the activity
					request(fe);
				}					
			}
			else {
				((es.ull.iis.simulation.model.flow.StructuredFlow)f).afterFinalize(fe);
				next(fe);
			}
		}
		else if (f instanceof DelayFlow) {
			fe.endDelay((DelayFlow)f);
			next(fe);
		}
		else if (f instanceof RequestResourcesFlow) {
			fe.endDelay((RequestResourcesFlow)f);
			next(fe);
		}
	}

	protected void arrive(FlowExecutor fe, MergeFlow f) {
		if (!control.containsKey(f))
			control.put(f, getNewBranchesControl(f));
		control.get(f).arrive(fe);
	}

	protected boolean canReset(FlowExecutor fe, MergeFlow f) {
		return control.get(f).canReset(f.getIncomingBranches());
	}

	protected void reset(FlowExecutor fe, MergeFlow f) {
		if (f instanceof SimpleMergeFlow) {
			lastTs.remove(f);
		}
		if (control.get(f).reset())
			control.remove(f);
	}

	protected boolean isActivated(FlowExecutor fe, MergeFlow f) {
		return control.get(f).isActivated();
	}

	protected MergeFlowControl getNewBranchesControl(MergeFlow f) {
		return (f.isSafe())? new SafeMergeFlowControl(f) : new GeneralizedMergeFlowControl(f); 
	}

	protected boolean canPass(FlowExecutor fe, MergeFlow f) {
		if (f instanceof ANDJoinFlow) {
			return (!control.get(f).isActivated() && (control.get(f).getTrueChecked() == ((ANDJoinFlow)f).getAcceptValue()));
		}
		else if (f instanceof MultiMergeFlow) {
			return fe.isExecutable();
		}
		else if (f instanceof SimpleMergeFlow) {
			if (!lastTs.containsKey(f)) {
				lastTs.put(f, (long)-1);
			}
			if (fe.isExecutable() && (elem.getTs() > lastTs.get(f))) {
				lastTs.put(f, simul.getTs());
				return true;
			}
			return false;
		}
		return true;
	}

}
