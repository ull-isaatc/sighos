package es.ull.isaatc.simulation.model.flow;

import java.util.TreeMap;

import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.common.flow.FinalizerFlow;
import es.ull.isaatc.simulation.common.flow.InitializerFlow;
import es.ull.isaatc.simulation.model.Model;



/**
 * A flow which can contain other flows. This kind of flows have a single entry point
 * <code>initialFlow</code> and a single exit point <code>finalFlow</code>, and can contain 
 * one or several internal branches.
 * @author ycallero
 *
 */
public abstract class StructuredFlow extends SingleSuccessorFlow implements TaskFlow, es.ull.isaatc.simulation.common.flow.StructuredFlow {
	/**	The entry point of the internal structure */
	protected InitializerFlow initialFlow = null;
	/**	The exit point of the internal structure */
	protected FinalizerFlow finalFlow = null;

    static private TreeMap<String, String> userCompleteMethods = new TreeMap<String, String>(); 

    static {
    	userCompleteMethods.put("beforeRequest", "public boolean beforeRequest(Element e)");
    	userCompleteMethods.put("afterFinalize", "public void afterFinalize(Element e)");
    }
	
	/**
	 * Creates a new structured flow with no initial nor final step.
	 * @param model Model this flow belongs to
	 */
	public StructuredFlow(Model model) {
		super(model);
		for (String method : userCompleteMethods.keySet())
			userMethods.put(method, "");
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#addPredecessor(es.ull.isaatc.simulation.Flow)
	 */
	public void addPredecessor(es.ull.isaatc.simulation.common.flow.Flow newFlow) {
	}

	@Override
	public void afterFinalize(Element e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FinalizerFlow getFinalFlow() {
		return finalFlow;
	}

	@Override
	public InitializerFlow getInitialFlow() {
		return initialFlow;
	}
	
	@Override
	public String getCompleteMethod(String method) {
		return userCompleteMethods.get(method);
	}
}
