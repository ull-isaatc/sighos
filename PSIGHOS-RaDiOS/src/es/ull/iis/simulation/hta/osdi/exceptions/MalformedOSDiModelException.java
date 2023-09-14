package es.ull.iis.simulation.hta.osdi.exceptions;

import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.Clazz;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.DataProperty;

public class MalformedOSDiModelException extends Exception {
	public MalformedOSDiModelException() {
		super();
	}

	public MalformedOSDiModelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

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

	public MalformedOSDiModelException(Throwable cause) {
		super(cause);
	}

}