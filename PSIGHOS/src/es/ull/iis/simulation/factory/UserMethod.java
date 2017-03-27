/**
 * 
 */
package es.ull.iis.simulation.factory;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Generator;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.FinalizerFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public enum UserMethod {
	BEFORE_CREATE_ELEMENTS("beforeCreateElements", Generator.class, "public int beforeCreateElements(int n)", Integer.class),
	AFTER_CREATE_ELEMENTS("afterCreateElements", Generator.class, "public void afterCreateElements()"),
	BEFORE_ROLE_ON("beforeRoleOn", ResourceType.class, "public long beforeRoleOn()"),
	BEFORE_ROLE_OFF("beforeRoleOff", ResourceType.class, "public long beforeRoleOff()"),
	AFTER_ROLE_ON("afterRoleOn", ResourceType.class, "public void afterRoleOn()"),
	AFTER_ROLE_OFF("afterRoleOff", ResourceType.class, "public void afterRoleOff()"),
	BEFORE_REQUEST("beforeRequest", Flow.class, "public boolean beforeRequest(" + ElementInstance.class.getName() + " ei)", ElementInstance.class),
	AFTER_FINALIZE("afterFinalize", FinalizerFlow.class, "public void afterFinalize(" + ElementInstance.class.getName() + " ei)", ElementInstance.class),
	AFTER_START("afterAcquire", RequestResourcesFlow.class, "public void afterStart(" + ElementInstance.class.getName() + " ei)", ElementInstance.class),
	IN_QUEUE("inqueue", RequestResourcesFlow.class, "public void inqueue(" + ElementInstance.class.getName() + " ei)", ElementInstance.class),
	EXP_END("end", Experiment.class, "public void end()"),
	SIM_INI("init", Simulation.class, "public void init()"),
	SIM_END("end", Simulation.class, "public void end()"),
	BEFORE_CLOCK_TICK("beforeClockTick", Simulation.class, "public void beforeClockTick()"),
	AFTER_CLOCK_TICK("afterClockTick", Simulation.class, "public void afterClockTick()");
	
	private Class<?> containerClass;
	private String name;
	private String methodHeading;
	private Class<?> []methodParams;
	
	/**
	 * @param name
	 * @param methodHeading
	 * @param methodParams
	 */
	private UserMethod(String name, Class<?> containerClass, String methodHeading, Class<?> []methodParams) {
		this.name = name;
		this.containerClass = containerClass;
		this.methodHeading = methodHeading;
		this.methodParams = methodParams;
	}
	
	/**
	 * @param name
	 * @param methodHeading
	 * @param methodParam
	 */
	private UserMethod(String name, Class<?> containerClass, String methodHeading, Class<?> methodParam) {
		this(name, containerClass, methodHeading, new Class<?>[] {methodParam});
	}
	
	/**
	 * @param name
	 * @param methodHeading
	 */
	private UserMethod(String name, Class<?> containerClass, String methodHeading) {
		this(name, containerClass, methodHeading, new Class<?>[0]);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the containerClass
	 */
	public Class<?> getContainerClass() {
		return containerClass;
	}
	
	/**
	 * @return the methodHeading
	 */
	public String getMethodHeading() {
		return methodHeading;
	}
	
	/**
	 * @return the methodParams
	 */
	public Class<?>[] getMethodParams() {
		return methodParams;
	}
}
