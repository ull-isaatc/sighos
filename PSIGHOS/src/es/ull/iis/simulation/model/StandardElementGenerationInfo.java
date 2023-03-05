package es.ull.iis.simulation.model;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.location.Location;

/**
 * Description of a set of {@link Element elements} a generator can create.
 * @author Iván Castilla Rodríguez
 */
public class StandardElementGenerationInfo extends Generator.GenerationInfo {
	/** Type of the created elements. */
	protected final ElementType et;
	/** Description of the flow that the elements carry out. */
	protected final InitializerFlow flow;
	/** Function to determine the size of the elements created */ 
	protected final TimeFunction size;
	/** The initial {@link Location} where the elements appear */
	protected final Location initLocation;
	
	/**
	 * Creates a new kind of elements to generate.
	 * @param et Element type
	 * @param flow Description of the activity flow that the elements carry out.
	 * @param size A function to determine the size of the generated elements 
	 * @param initLocation The initial {@link Location} where the elements appear
	 * @param prop Proportion of elements corresponding to this flow.
	 */
	public StandardElementGenerationInfo(final ElementType et, final InitializerFlow flow, final int size, final Location initLocation, final double prop) {
		this(et, flow, TimeFunctionFactory.getInstance("ConstantVariate", size), initLocation, prop);
	}
	
	/**
	 * Creates a new kind of elements to generate.
	 * @param et Element type
	 * @param flow Description of the activity flow that the elements carry out.
	 * @param size A function to determine the size of the generated elements 
	 * @param initLocation The initial {@link Location} where the elements appear
	 * @param prop Proportion of elements corresponding to this flow.
	 */
	public StandardElementGenerationInfo(final ElementType et, final InitializerFlow flow, final TimeFunction size, final Location initLocation, final double prop) {
		super(prop);
		this.et = et;
		this.flow = flow;
		this.size = size;
		this.initLocation = initLocation;
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
	 * Returns the function that determines the size of the generated elements 
	 * @return the function that determines the size of the generated elements
	 */
	public int getSize(Element e) {
		return (size == null) ? 0 : (int)size.getValue(e);
	}

	/**
	 * Returns the initial {@link Location} where the elements appear
	 * @return The initial {@link Location} where the elements appear
	 */
	public Location getInitLocation() {
		return initLocation;
	}
}