package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link StructuredFlow} which defines a repetitive subflow. Different subclasses
 * of this class represent different loop structures: {@link WhileDoFlow}, {@link DoWhileFlow},
 * {@link ForLoopFlow}...
 * Meets the Structured Loop pattern (WFP21). 
 * @author Yeray Callero
 */
public interface StructuredLoopFlow<WT extends WorkThread<?>> extends StructuredFlow<WT> {
}

