/**
 * 
 */
package es.ull.iis.simulation.core.factory;

import es.ull.iis.simulation.core.Element;
import es.ull.iis.simulation.core.ElementCreator;
import es.ull.iis.simulation.core.WorkThread;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.FinalizerFlow;
import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.core.flow.RequestResourcesFlow;
import es.ull.iis.simulation.model.engine.ResourceTypeEngine;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public enum UserMethod {
	BEFORE_CREATE_ELEMENTS("beforeCreateElements", ElementCreator.class, "public int beforeCreateElements(int n)", Integer.class),
	AFTER_CREATE_ELEMENTS("afterCreateElements", ElementCreator.class, "public void afterCreateElements()"),
	BEFORE_ROLE_ON("beforeRoleOn", ResourceTypeEngine.class, "public double beforeRoleOn()"),
	BEFORE_ROLE_OFF("beforeRoleOff", ResourceTypeEngine.class, "public double beforeRoleOff()"),
	AFTER_ROLE_ON("afterRoleOn", ResourceTypeEngine.class, "public void afterRoleOn()"),
	AFTER_ROLE_OFF("afterRoleOff", ResourceTypeEngine.class, "public void afterRoleOff()"),
	// TODO: Change to get package automatically
	BEFORE_REQUEST("beforeRequest", Flow.class, "public boolean beforeRequest(es.ull.iis.simulation.core.Element e)", Element.class),
	AFTER_FINALIZE("afterFinalize", FinalizerFlow.class, "public void afterFinalize(es.ull.iis.simulation.core.WorkThread wThread)", WorkThread.class),
	AFTER_START("afterStart", ActivityFlow.class, "public void afterStart(es.ull.iis.simulation.core.WorkThread wThread)", WorkThread.class),
	IN_QUEUE("inqueue", RequestResourcesFlow.class, "public void inqueue(es.ull.iis.simulation.core.WorkThread wThread)", WorkThread.class);
	
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
