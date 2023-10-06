package es.ull.iis.simulation.hta.osdi.exceptions;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;

public class MalformedOSDiModelException extends MalformedSimulationModelException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1683838127328936001L;

	public MalformedOSDiModelException(String message, Throwable cause) {
		super(message, cause);
	}

	public MalformedOSDiModelException(OSDiWrapper.Clazz involvedClass, String instanceName, OSDiWrapper.DataProperty involvedProperty, String content) {
		super("(" + involvedClass.getShortName() + ") " + instanceName + ":" + involvedProperty.getShortName() + "\tError parsing\"" + content + "\"");
	}

	public MalformedOSDiModelException(OSDiWrapper.Clazz involvedClass, String instanceName, OSDiWrapper.DataProperty involvedProperty, String content, Throwable cause) {
		super("(" + involvedClass.getShortName() + ") " + instanceName + ":" + involvedProperty.getShortName() + "\tError parsing\"" + content + "\"", cause);
	}

	public MalformedOSDiModelException(OSDiWrapper.Clazz involvedClass, String instanceName, OSDiWrapper.ObjectProperty involvedProperty, String content) {
		super("(" + involvedClass.getShortName() + ") " + instanceName + ":" + involvedProperty.getShortName() + "\tError parsing\"" + content + "\"");
	}

	public MalformedOSDiModelException(OSDiWrapper.Clazz involvedClass, String instanceName, OSDiWrapper.ObjectProperty involvedProperty, String content, Throwable cause) {
		super("(" + involvedClass.getShortName() + ") " + instanceName + ":" + involvedProperty.getShortName() + "\tError parsing\"" + content + "\"", cause);
	}

	public MalformedOSDiModelException(String message) {
		super(message);
	}
}