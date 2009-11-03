/**
 * 
 */
package es.ull.isaatc.simulation.model.flow;

import java.util.TreeMap;

import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.model.Activity;
import es.ull.isaatc.simulation.model.Model;

/**
 * A flow which executes a single activity. 
 * @author Iván Castilla Rodríguez
 */
public class SingleFlow extends SingleSuccessorFlow implements TaskFlow, es.ull.isaatc.simulation.common.flow.SingleFlow {
    /** The activity to be performed */
    protected Activity act;

    static private TreeMap<String, String> userCompleteMethods = new TreeMap<String, String>(); 

    static {
    	userCompleteMethods.put("beforeRequest", "public boolean beforeRequest(Element e)");
    	userCompleteMethods.put("afterStart", "public void afterStart(Element e)");
    	userCompleteMethods.put("afterFinalize", "public void afterFinalize(Element e)");
    	userCompleteMethods.put("inqueue", "public void inqueue(Element e)");
    }
	
    
	/**
	 * Creates a new single flow..
	 * @param model Model this flow belongs to
	 * @param act Activity to be performed
	 */
	public SingleFlow(Model model, Activity act) {
		super(model);
		this.act = act;
		for (String method : userCompleteMethods.keySet())
			userMethods.put(method, "");
		
	}

	/**
	 * Obtain the Activity associated to the SingleFlow.
	 * @return The associated Activity.
	 */
	public Activity getActivity() {
		return act;
	}

	/**
	 * Set a new Activity associated to the SingleFlow.
	 * @param act The new Activity.
	 */
	public void setActivity(Activity act) {
		this.act = act;
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#addPredecessor(es.ull.isaatc.simulation.Flow)
	 */
	public void addPredecessor(es.ull.isaatc.simulation.common.flow.Flow newFlow) {
	}

	@Override
	public String getCompleteMethod(String method) {
		return userCompleteMethods.get(method);
	}

	@Override
	public void afterFinalize(Element e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterStart(Element e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inqueue(Element e) {
		// TODO Auto-generated method stub
		
	}
	
}

