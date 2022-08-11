package es.ull.iis.simulation.hta.osdi.exceptions;

import es.ull.iis.simulation.hta.osdi.OSDiNames;

public class TranspilerException extends Exception {
	private static final long serialVersionUID = -7888718733496716636L;

	public TranspilerException() {
		super();
	}

	public TranspilerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TranspilerException(String message, Throwable cause) {
		super(message, cause);
	}

	public TranspilerException(OSDiNames.Class involvedClass, String instanceName, OSDiNames.DataProperty involvedProperty, String content) {
		super("(" + involvedClass.getDescription() + ") " + instanceName + ":" + involvedProperty.getDescription() + "\tError parsing\"" + content + "\"");
	}

	public TranspilerException(OSDiNames.Class involvedClass, String instanceName, OSDiNames.DataProperty involvedProperty, String content, Throwable cause) {
		super("(" + involvedClass.getDescription() + ") " + instanceName + ":" + involvedProperty.getDescription() + "\tError parsing\"" + content + "\"", cause);
	}

	public TranspilerException(String message) {
		super(message);
	}

	public TranspilerException(Throwable cause) {
		super(cause);
	}
}
