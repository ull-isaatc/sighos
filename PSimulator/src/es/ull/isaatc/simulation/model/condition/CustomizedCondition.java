package es.ull.isaatc.simulation.model.condition;

import java.util.Collection;
import java.util.TreeMap;

import es.ull.isaatc.simulation.model.VariableHandler;


/**
 * A logical condition which is used for create restrictions or 
 * uncertainty situations.
 * 
 * @author ycallero
 */

public class CustomizedCondition extends es.ull.isaatc.simulation.common.condition.Condition implements VariableHandler {
	/** The condition expressed in the condition's format */
    protected TreeMap<String, String> userMethods = new TreeMap<String, String>();
    protected String imports = "";

    static private TreeMap<String, String> userCompleteMethods = new TreeMap<String, String>(); 

    static {
    	userCompleteMethods.put("check", "public boolean check(Element e)");
    }
    
	
	/** 
	 * Creates a new Condition.
	 * @param id Condition's identifier
	 */
	public CustomizedCondition(){
		for (String method : userCompleteMethods.keySet())
			userMethods.put(method, "");
	}
	
	/** 
	 * Creates a new Condition.
	 * @param id Condition's identifier
	 */
	public CustomizedCondition(String checkContent){
		userMethods.put("check", checkContent);
	}
	
	@Override
	public boolean setMethod(String method, String body) {
		if (userMethods.containsKey(method)) {
			userMethods.put(method, body);
			return true;
		}
		return false;
	}

	@Override
	public String getBody(String method) {
		return userMethods.get(method);
	}

	@Override
	public String getImports() {
		return imports;
	}

	@Override
	public void setImports(String imports) {
		this.imports = imports;
	}

	@Override
	public Collection<String> getMethods() {
		return userMethods.keySet();
	}

	@Override
	public String getCompleteMethod(String method) {
		return userCompleteMethods.get(method);
	}
}
