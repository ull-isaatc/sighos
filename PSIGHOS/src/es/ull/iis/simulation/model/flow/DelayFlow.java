/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;

/**
 * A flow that makes an element be delayed for a certain time
 * @author Iván Castilla
 *
 */
public abstract class DelayFlow extends SingleSuccessorFlow implements TaskFlow, ActionFlow {
    /** A brief description of the delay */
    private final String description;

    /**
     * Creates a delay flow
     * @param model The simulation model this flow belongs to
     * @param description A short text describing this flow
     */
	public DelayFlow(final Simulation model, final String description) {
		super(model);
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

    /**
     * Returns the duration of the delay
     * @param elem The element delaying
     * @return The duration of the delay
     */
    public abstract long getDurationSample(final Element elem);

	@Override
	public void addPredecessor(final Flow predecessor) {
	}

	@Override
	public void afterFinalize(final ElementInstance ei) {
	}

	@Override
	public void request(final ElementInstance ei) {
		if (!ei.wasVisited(this)) {
			if (ei.isExecutable()) {
				if (beforeRequest(ei)) {
					final Element elem = ei.getElement();
					simul.notifyInfo(new ElementActionInfo(simul, ei, elem, this, ei.getExecutionWG(), null, ElementActionInfo.Type.START, simul.getTs()));
					elem.debug("Start delay\t" + this + "\t" + getDescription());	
					ei.startDelay(getDurationSample(elem));
				}
				else {
					ei.cancel(this);
					next(ei);
				}
			}
			else {
				ei.updatePath(this);
				next(ei);
			}
		} else
			ei.notifyEnd();
	}

	@Override
	public void finish(final ElementInstance ei) {
		simul.notifyInfo(new ElementActionInfo(simul, ei, ei.getElement(), this, ei.getExecutionWG(), null, ElementActionInfo.Type.END, simul.getTs()));
		if (ei.getElement().isDebugEnabled())
			ei.getElement().debug("Finishes\t" + this + "\t" + getDescription());
		afterFinalize(ei);
		next(ei);
	}

}
