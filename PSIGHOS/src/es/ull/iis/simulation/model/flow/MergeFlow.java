package es.ull.iis.simulation.model.flow;

import java.util.Map;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.engine.MergeFlowEngine;
import es.ull.iis.simulation.model.engine.SimulationEngine;

/**
 * A flow which merges several incoming branches into a single outgoing branch. The incoming 
 * branches are handled in an internal structure and managed per element.<p> When an incoming 
 * branch arrives, the <code>arrive</code> is invoked. Both, true and false element instances 
 * are computed. Then, the flow checks (depending on the implementation) if this branch 
 * <code>canPass</code> or not. If it can pass, the outgoing flow is activated. Next, 
 * the flow checks if the branch <code>canReset</code> the structure, if so, it's reset.
 * Only true element instances can pass, but both true and false threads can reset the structure.
 * In case the structure has to be reset and it has not been activated, a new false element instance
 * continues the execution.
 * @author ycallero
 */
public abstract class MergeFlow extends SingleSuccessorFlow implements JoinFlow {
	/** Amount of incoming branches */
	protected int incomingBranches;
	/** A structure to control the arrival of incoming branches */
	protected Map<Element, MergeFlowControl> control;
	/** The engine that implements the functioning of the flow */
	private MergeFlowEngine engine;
	/** Indicates if the node is safe or it has to control several triggers for 
	 * the same element through the same incoming branch before reset */ 
	protected final boolean safe;
	
	/**
	 * Create a new MergeFlow intended to be used in a safe context.
	 * @param model The simulation model this flow belongs to
	 */
	public MergeFlow(final Simulation model) {
		this(model, true);
	}

	/**
	 * Create a new MergeFlow which can be used in a safe context or a general one.
	 * @param safe True for safe context; false in other case
	 */
	public MergeFlow(final Simulation model, final boolean safe) {
		super(model);
		this.safe = safe;
	}

	@Override
	public void addPredecessor(final Flow newFlow) {
		incomingBranches++;
	}

	/**
	 * Returns how many incoming branches this flow has.
	 * @return How many incoming branches this flow has
	 */
	public int getIncomingBranches() {
		return incomingBranches;
	}

	/**
	 * Returns the safety of this flow. 
	 * @return The safety of this flow
	 */
	public boolean isSafe() {
		return safe;
	}

	@Override
	public void afterFinalize(final ElementInstance ei) {}

	/**
	 * Controls the arrival of an incoming branch.
	 * @param ei The thread of control of the incoming branch
	 */
	protected void arrive(final ElementInstance ei) {
		if (!control.containsKey(ei.getElement()))
			control.put(ei.getElement(), getNewBranchesControl());
		control.get(ei.getElement()).arrive(ei);
	}

	/**
	 * Checks if the last incoming branch can pass.
	 * @param ei The thread of control of the incoming branch
	 * @return True if the incoming branch activates the outgoing branch; false in other case.
	 */
	protected abstract boolean canPass(final ElementInstance ei);
	
	/**
	 * Checks if the last incoming branch can reset the control structure. The structure
	 * has to be reset when all the incoming branches has been activated once.
	 * @param ei The thread of control of the incoming branch
	 * @return True if all the incoming branches were activated once; false in other case.
	 */
	protected boolean canReset(final ElementInstance ei) {
		return control.get(ei.getElement()).canReset(incomingBranches);
	}

	/**
	 * Resets the control structure, so the next incoming branch for the same element 
	 * will use a new control structure. 
	 * @param ei The thread of control of the incoming branch
	 */
	protected void reset(final ElementInstance ei) {
		if (control.get(ei.getElement()).reset())
			control.remove(ei.getElement());
	}
	
	/**
	 * Checks if the control structure has been activated at least once.
	 * @param ei The thread of control of the incoming branch
	 * @return True if the control structure was activated at least once.
	 */
	protected boolean isActivated(final ElementInstance ei) {
		return control.get(ei.getElement()).isActivated();
	}

	@Override
	public void request(final ElementInstance ei) {
		final Element elem = ei.getElement();
		if (!ei.wasVisited(this)) {
			if (ei.isExecutable()) {
				if (!beforeRequest(ei))
					ei.cancel(this);
			}
			elem.getEngine().waitProtectedFlow(this);
			arrive(ei);
			if (canPass(ei)) {
				control.get(elem).setActivated();
				next(ei);
			}
			else {
				// If no one of the branches was true, the thread of control must continue anyway
				if (canReset(ei) && !isActivated(ei))
					next(ei.getSubsequentElementInstance(false, this, control.get(elem).getOutgoingFalseToken()));
				ei.notifyEnd();
			}
			if (canReset(ei))
				reset(ei);
			elem.getEngine().signalProtectedFlow(this);
		} else
			ei.notifyEnd();
	}
	
	/**
	 * Returns a {@link MergeFlowControl} instance to control the behaviour of the flow
	 * @return a {@link MergeFlowControl} instance to control the behaviour of the flow
	 */
	protected MergeFlowControl getNewBranchesControl() {
		return (safe)? new SafeMergeFlowControl(this) : new GeneralizedMergeFlowControl(this, engine.getGeneralizedBranchesControlInstance()); 
	}

	@Override
	public void assignSimulation(final SimulationEngine simul) {
		super.assignSimulation(simul);
		engine = simul.getMergeFlowEngineInstance(this);
		control = engine.getControlStructureInstance();
	}
}


