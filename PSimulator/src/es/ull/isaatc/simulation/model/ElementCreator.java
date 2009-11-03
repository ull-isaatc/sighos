/**
 * 
 */
package es.ull.isaatc.simulation.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.model.flow.InitializerFlow;

/**
 * Defines the way a generator creates elements when it's time to create them.
 * @author Iván Castilla Rodríguez
 */
public class ElementCreator extends ModelObject implements VariableHandler, es.ull.isaatc.simulation.common.ElementCreator {
	/** Number of objects created each time this creator is invoked. */
	protected final TimeFunction nElem;
	/** Each flow that will be generated */
	protected final ArrayList<GenerationTrio> genTrio;
	static private int counter = 0;
    protected TreeMap<String, String> userMethods = new TreeMap<String, String>();
    protected String imports = "";
    static private TreeMap<String, String> userCompleteMethods = new TreeMap<String, String>(); 

    static {
    	userCompleteMethods.put("beforeCreateElements", "public int beforeCreateElements(int n)");
    	userCompleteMethods.put("afterCreateElements", "public void afterCreateElements()");
    	userCompleteMethods.put("initializeElementVars", "public void initializeElementVars(Element e)");
    }
    
	/**
	 * Creates a creator of elements.
	 * @param model Model this object belongs to.
	 * @param nElem Number of objects created each time this creator is invoked.
	 */
	public ElementCreator(Model model, TimeFunction nElem) {
		super(counter++, model);
		genTrio = new ArrayList<GenerationTrio>();
		this.nElem = nElem;
		for (String method : userCompleteMethods.keySet())
			userMethods.put(method, "");
	}
	
	/**
	 * Creates a creator of a single type of elements.
	 * @param model Model this object belongs to.
	 * @param nElem Number of objects created each time this creator is invoked.
	 * @param et The type of the elements to be created
	 * @param flow The description of the flow of the elements to be created.
	 */
	public ElementCreator(Model model, TimeFunction nElem, ElementType et, InitializerFlow flow) {
		this(model, nElem);
		genTrio.add(new GenerationTrio(et, flow, 1.0));
	}
	
	/**
	 * @return the nElem
	 */
	public TimeFunction getNElem() {
		return nElem;
	}

	@Override
	public void afterCreateElements() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int beforeCreateElements(int n) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Adds a [element type, metaflow, proportion] trio.
	 * @param et Element type
	 * @param flow Description of the activity flow that the elements carry out.
	 * @param prop Proportion of elements corresponding to this metaflow.
	 */
	public void add(ElementType et, InitializerFlow flow, double prop) {
		genTrio.add(new GenerationTrio(et, flow, prop));
	}

	/**
	 * @return the genTrio
	 */
	public ArrayList<GenerationTrio> getGenTrio() {
		return genTrio;
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
	
	/**
	 * Description of a set of elements a generator can create.
	 * @author Iván Castilla Rodríguez
	 */
	public class GenerationTrio {
		/** Type of the created elements. */
		protected final ElementType et;
		/** Description of the activity flow that the elements carry out. */
		protected final InitializerFlow flow;
		/** Proportion of elements corresponding to this flow. */
		protected final double prop;
		
		/**
		 * Creates a new kind of elements to generate.
		 * @param et Element type
		 * @param flow Description of the activity flow that the elements carry out.
		 * @param prop Proportion of elements corresponding to this flow.
		 */
		public GenerationTrio(ElementType et, InitializerFlow flow, double prop) {
			super();
			this.et = et;
			this.flow = flow;
			this.prop = prop;
		}
		
		/**
		 * Returns the element type.
		 * @return Returns the element type.
		 */
		public ElementType getElementType() {
			return et;
		}
		
		/**
		 * Returns the flow.
		 * @return the flow
		 */
		public InitializerFlow getFlow() {
			return flow;
		}

		/**
		 * Returns the proportion of elements to be created of this kind of elements.
		 * @return Returns the proportion.
		 */
		public double getProp() {
			return prop;
		}
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "EC";
	}


}
