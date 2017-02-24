package es.ull.iis.simulation.sequential;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.WorkToken;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.DelayFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.util.Prioritizable;

/**
 * A sequential branch of activities in an element's flow. Represents an element instance, so
 * multiple instances can be active at the same time.<p>
 * There are three types of Work threads which require a different method to be created:
 * <ol>
 * <li>Main thread. Is the element's main thread. Must be created by invoking the static method
 * <code>WorkThread.getInstanceMainWorkThread</code></li>
 * <li>Descendant thread</li>A thread created to carry out the inner flows of a structured flow.
 * To invoke, use: getInstanceDescendantWorkThread</li>
 * <li>Subsequent thread</li>A thread created to carry out a new flow after a split.
 * To invoke, use: getInstanceSubsequentWorkThread</li>
 * </ol><p>
 *  A work thread has an associated token, which can be true or false. A false token is used
 *  only for synchronization purposes and doesn't execute task flows. 
 * @author Iván Castilla Rodríguez
 */
public class WorkThread implements es.ull.iis.simulation.model.flow.FlowExecutor, Comparable<WorkThread>, Prioritizable {
    
    /** 
     * Creates a new work thread. The constructor is private since it must be invoked from the 
     * <code>getInstance...</code> methods.
     * @param token An object containing the state of the thread  
     * @param elem Element owner of this thread
     * @param initialFlow The first flow to be executed by this thread
     * @param parent The parent thread, if this thread is included within a structured flow
     */
    private WorkThread(WorkToken token, ElementEngine elem, Flow initialFlow, WorkThread parent) {
    	this.token = token;
        this.elem = elem;
        this.parent = parent;
        descendants = new ArrayList<WorkThread>();
        if (parent != null)
        	parent.addDescendant(this);
        this.initialFlow = initialFlow;
        this.id = counter++;
    }

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WT" + id + "(" + elem + ")\tFLOW: " + currentFlow;
	}
	
    /**
     * Returns the workgroup which is used to perform this flow, or <code>null</code>  
     * if the flow has not been carried out.
	 * @return the workgroup which is used to perform this flow, or <code>null</code>  
     * if the flow has not been carried out.
	 */
	public es.ull.iis.simulation.model.ActivityWorkGroup getModelWG() {
		if (executionWG == null)
			return null;
		return executionWG.getModelAWG();
	}

	public ActivityWorkGroupEngine getExecutionWG() {
		return executionWG;
	}

	/**
	 * When the single flow can be carried out, sets the workgroup used to
	 * carry out the activity.
	 * @param executionWG the workgroup which is used to carry out this flow.
	 */
	public void setExecutionWG(ActivityWorkGroupEngine executionWG) {
		this.executionWG = executionWG;
	}

    /**
     * Catch the resources needed for each resource type to carry out an activity.
     */
	public boolean acquireResources(RequestResourcesFlow reqFlow) {
		elem.simul.getInfoHandler().notifyInfo(new ElementActionInfo(elem.simul, this, elem.getModelElem(), reqFlow, getModelWG(), ElementActionInfo.Type.REQACT, elem.getTs()));
		if (elem.isDebugEnabled())
			elem.debug("Requests\t" + this + "\t" + reqFlow.getDescription());
		final RequestResources req = elem.simul.getRequestResource(reqFlow);
		if (!reqFlow.isExclusive() || (elem.getCurrent() == null)) {
			// There are enough resources to perform the activity
			final ArrayDeque<ResourceEngine> solution = req.isFeasible(this); 
			if (solution != null) {
				if (reqFlow.isExclusive()) 
					elem.setCurrent(this);
				final int resourcesId = reqFlow.getResourcesId();
				if (this.caughtResources.containsKey(resourcesId))
					elem.error("Trying to assign group of resources to already occupied group when catching. ID:" + resourcesId);
				this.caughtResources.put(resourcesId, solution);
		    	long auxTs = Long.MAX_VALUE;
		    	for (ResourceEngine res : solution) {
		    		auxTs = Math.min(auxTs, res.catchResource(this));;
		            res.getCurrentResourceType().debug("Resource taken\t" + res + "\t" + getElement());
		    	}
				minResourcesAvailability = auxTs;
				// The first time the activity is carried out (useful only for interruptible activities)
				if (timeLeft == -1) {
					// wThread.setTimeLeft(wThread.getExecutionWG().getDurationSample(elem));
					timeLeft = executionWG.getDurationSample(this);
					elem.simul.getInfoHandler().notifyInfo(new ElementActionInfo(elem.simul, this, elem.getModelElem(), reqFlow, getModelWG(), ElementActionInfo.Type.STAACT, elem.getTs()));
					elem.debug("Starts\t" + this + "\t" + reqFlow.getDescription());			
					reqFlow.afterStart(this);
				}
				else {
					elem.simul.getInfoHandler().notifyInfo(new ElementActionInfo(elem.simul, this, elem.getModelElem(), reqFlow, getModelWG(), ElementActionInfo.Type.RESACT, elem.getTs()));
					elem.debug("Continues\t" + this + "\t" + reqFlow.getDescription());			
				}
				long finishTs = elem.getTs() + timeLeft;
				// The required time for finishing the activity is reduced (useful only for interruptible activities)
				if (reqFlow.partOfInterruptible() && (finishTs - minResourcesAvailability > 0.0)) {
					timeLeft = finishTs - minResourcesAvailability;
					finishTs = minResourcesAvailability;
				}
				else {
					timeLeft = 0;
				}
				elem.addFinishEvent(finishTs, this);
				return true;
			}
		}
		req.queueAdd(this); // The element is introduced in the queue
		return false;
	}

    public int availableResource(RequestResources reqResources) {
    	final RequestResourcesFlow reqFlow = reqResources.getModelReqFlow();
		if (!reqFlow.isExclusive() || (elem.getCurrent() == null)) {
			// There are enough resources to perform the activity
			final ArrayDeque<ResourceEngine> solution = reqResources.isFeasible(this); 
			if (solution != null) {
				if (reqFlow.isExclusive()) 
					elem.setCurrent(this);
				final int resourcesId = reqFlow.getResourcesId();
				if (this.caughtResources.containsKey(resourcesId))
					elem.error("Trying to assign group of resources to already occupied group when catching. ID:" + resourcesId);
				this.caughtResources.put(resourcesId, solution);
		    	long auxTs = Long.MAX_VALUE;
		    	for (ResourceEngine res : solution) {
		    		auxTs = Math.min(auxTs, res.catchResource(this));;
		            res.getCurrentResourceType().debug("Resource taken\t" + res + "\t" + getElement());
		    	}
				minResourcesAvailability = auxTs;
				// The first time the activity is carried out (useful only for interruptible activities)
				if (timeLeft == -1) {
					// wThread.setTimeLeft(wThread.getExecutionWG().getDurationSample(elem));
					timeLeft = executionWG.getDurationSample(this);
					elem.simul.getInfoHandler().notifyInfo(new ElementActionInfo(elem.simul, this, elem.getModelElem(), reqFlow, getModelWG(), ElementActionInfo.Type.STAACT, elem.getTs()));
					elem.debug("Starts\t" + this + "\t" + reqFlow.getDescription());			
					reqFlow.afterStart(this);
				}
				else {
					elem.simul.getInfoHandler().notifyInfo(new ElementActionInfo(elem.simul, this, elem.getModelElem(), reqFlow, getModelWG(), ElementActionInfo.Type.RESACT, elem.getTs()));
					elem.debug("Continues\t" + this + "\t" + reqFlow.getDescription());			
				}
				long finishTs = elem.getTs() + timeLeft;
				// The required time for finishing the activity is reduced (useful only for interruptible activities)
				if (reqFlow.partOfInterruptible() && (finishTs - minResourcesAvailability > 0.0)) {
					timeLeft = finishTs - minResourcesAvailability;
					finishTs = minResourcesAvailability;
				}
				else {
					timeLeft = 0;
				}
				elem.addFinishEvent(finishTs, this);
				return -1;
			}
			else {
				return reqResources.getQueueSize();
			}
		}
		else {
			return 0;
		}
    }

	public void availableElement(RequestResources reqResources) {
    	final RequestResourcesFlow reqFlow = reqResources.getModelReqFlow();
    	final ArrayDeque<ResourceEngine> solution = reqResources.isFeasible(this);
		if (solution != null) {
			if (reqFlow.isExclusive()) 
				elem.setCurrent(this);
			final int resourcesId = reqFlow.getResourcesId();
			if (this.caughtResources.containsKey(resourcesId))
				elem.error("Trying to assign group of resources to already occupied group when catching. ID:" + resourcesId);
			this.caughtResources.put(resourcesId, solution);
	    	long auxTs = Long.MAX_VALUE;
	    	for (ResourceEngine res : solution) {
	    		auxTs = Math.min(auxTs, res.catchResource(this));;
	            res.getCurrentResourceType().debug("Resource taken\t" + res + "\t" + getElement());
	    	}
			minResourcesAvailability = auxTs;
	    	
			// The first time the activity is carried out (useful only for interruptible activities)
			if (timeLeft == -1) {
				// wThread.setTimeLeft(wThread.getExecutionWG().getDurationSample(elem));
				timeLeft = executionWG.getDurationSample(this);
				elem.simul.getInfoHandler().notifyInfo(new ElementActionInfo(elem.simul, this, elem.getModelElem(), reqFlow, getModelWG(), ElementActionInfo.Type.STAACT, elem.getTs()));
				elem.debug("Starts\t" + this + "\t" + reqFlow.getDescription());			
				reqFlow.afterStart(this);
			}
			else {
				elem.simul.getInfoHandler().notifyInfo(new ElementActionInfo(elem.simul, this, elem.getModelElem(), reqFlow, getModelWG(), ElementActionInfo.Type.RESACT, elem.getTs()));
				elem.debug("Continues\t" + this + "\t" + reqFlow.getDescription());			
			}
			long finishTs = elem.getTs() + timeLeft;
			// The required time for finishing the activity is reduced (useful only for interruptible activities)
			if (reqFlow.partOfInterruptible() && (finishTs - minResourcesAvailability > 0.0)) {
				timeLeft = finishTs - minResourcesAvailability;
				finishTs = minResourcesAvailability;
			}
			else {
				timeLeft = 0;
			}
			reqResources.queueRemove(this);
			elem.addFinishEvent(finishTs, this);
		}
	}
	
    /**
     * Releases the resources caught by this item to perform the activity.
     * @return A list of activity managers affected by the released resources
     */
    public boolean releaseResources(ReleaseResourcesFlow relFlow) {
        final TreeSet<ActivityManagerEngine> amList = new TreeSet<ActivityManagerEngine>();
		final Collection<ResourceEngine> resources = caughtResources.remove(relFlow.getResourcesId());
		if (resources == null) {
			elem.error("Trying to release group of resources not already created. ID:" + relFlow.getResourcesId());
			return false;
		}
        // Generate unavailability periods.
        for (ResourceEngine res : resources) {
        	final long cancellationDuration = (elem.simul.getReleaseResource(relFlow)).getResourceCancellation(res.getCurrentResourceType());
        	if (cancellationDuration > 0) {
				final long actualTs = elem.getTs();
				res.setNotCanceled(false);
				elem.simul.getInfoHandler().notifyInfo(new ResourceInfo(elem.simul, res.getModelRes(), res.getCurrentModelResourceType(), ResourceInfo.Type.CANCELON, actualTs));
				res.generateCancelPeriodOffEvent(actualTs, cancellationDuration);
			}
			elem.debug("Returned " + res);
        	// The resource is freed
        	if (res.releaseResource()) {
        		// The activity managers involved are included in the list
        		for (ActivityManagerEngine am : res.getCurrentManagers()) {
        			amList.add(am);
        		}
        	}
        }

	        // FIXME: Preparado para hacerlo aleatorio
//					final int[] order = RandomPermutation.nextPermutation(amList.size());
//					for (int ind : order) {
//						ActivityManager am = amList.get(ind);
//						am.availableResource();
//					}

		for (ActivityManagerEngine am : amList) {
			am.notifyResource();
		}
		
		// TODO Change by more appropriate messages
		elem.simul.getInfoHandler().notifyInfo(new ElementActionInfo(elem.simul, this, elem.getModelElem(), relFlow, getModelWG(), ElementActionInfo.Type.ENDACT, elem.getTs()));
		if (elem.isDebugEnabled())
			elem.debug("Finishes\t" + this + "\t" + relFlow.getDescription());
		relFlow.afterFinalize(this);
		return true;

    }

    public void startDelay(DelayFlow f) {
		elem.simul.getInfoHandler().notifyInfo(new ElementActionInfo(elem.simul, this, elem.getModelElem(), f, getModelWG(), ElementActionInfo.Type.STAACT, elem.getTs()));
		elem.debug("Starts\t" + this + "\t" + f.getDescription());			
		long finishTs = elem.getTs() + f.getDurationSample(this);
		timeLeft = 0;
		elem.addFinishEvent(finishTs, this);
    }
    
    public void endDelay(DelayFlow f) {
		elem.simul.getInfoHandler().notifyInfo(new ElementActionInfo(elem.simul, this, elem.getModelElem(), f, getModelWG(), ElementActionInfo.Type.ENDACT, elem.getTs()));
		if (elem.isDebugEnabled())
			elem.debug("Finishes\t" + this + "\t" + f.getDescription());
		f.afterFinalize(this);
    }
    
    public void endDelay(RequestResourcesFlow f) {
		// FIXME: CUIDADO CON ESTO!!! Nunca debería ser menor
		if (timeLeft <= 0.0) {
			elem.simul.getInfoHandler().notifyInfo(new ElementActionInfo(elem.simul, this, elem.getModelElem(), f, getModelWG(), ElementActionInfo.Type.ENDACT, elem.getTs()));
			if (elem.isDebugEnabled())
				elem.debug("Finishes\t" + this + "\t" + f.getDescription());
			f.afterFinalize(this);
		}
		else {
			elem.simul.getInfoHandler().notifyInfo(new ElementActionInfo(elem.simul, this, elem.getModelElem(), f, getModelWG(), ElementActionInfo.Type.INTACT, elem.getTs()));
			if (elem.isDebugEnabled())
				elem.debug("Finishes part of \t" + this + "\t" + f.getDescription() + "\t" + timeLeft);
			// Notifies the parent workthread that the activity was interrupted
			parent.setTimeLeft(timeLeft);
		}
    }
    
    public boolean endActivity(ActivityFlow f) {
    	if (f.isExclusive()) {
    		elem.setCurrent(null);
    	}
		// It was an interruptible activity and it was interrupted
		return (timeLeft <= 0.0);
    }
    
	@Override
	public boolean equals(Object o) {
		if (((WorkThread)o).id == id)
			return true;
		return false;
	}

	@Override
	public double getTime() {
		return elem.getTs();
	}

	@Override
	public ElementType getType() {
		return elem.getType();
	}


}
