/**
 * 
 */
package es.ull.isaatc.simulation.common.factory;

import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.common.ElementCreator;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.TaskFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public enum UserMethod {
	BEFORE_CREATE_ELEMENTS("beforeCreateElements", ElementCreator.class, "public int beforeCreateElements(int n)", Integer.class),
	AFTER_CREATE_ELEMENTS("afterCreateElements", ElementCreator.class, "public void afterCreateElements()"),
	BEFORE_ROLE_ON("beforeRoleOn", ResourceType.class, "public double beforeRoleOn()"),
	BEFORE_ROLE_OFF("beforeRoleOff", ResourceType.class, "public double beforeRoleOff()"),
	AFTER_ROLE_ON("afterRoleOn", ResourceType.class, "public void afterRoleOn()"),
	AFTER_ROLE_OFF("afterRoleOff", ResourceType.class, "public void afterRoleOff()"),
	BEFORE_REQUEST("beforeRequest", Flow.class, "public boolean beforeRequest(Element e)", Element.class),
	AFTER_FINALIZE("afterFinalize", TaskFlow.class, "public void afterFinalize(Element e)", Element.class),
	AFTER_START("afterStart", SingleFlow.class, "public void afterStart(Element e)", Element.class),
	IN_QUEUE("inqueue", SingleFlow.class, "public void inqueue(Element e)", Element.class);
	
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
