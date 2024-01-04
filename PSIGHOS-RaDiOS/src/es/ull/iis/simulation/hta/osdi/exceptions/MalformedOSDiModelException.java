package es.ull.iis.simulation.hta.osdi.exceptions;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataProperties;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiObjectProperties;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiClasses;

public class MalformedOSDiModelException extends MalformedSimulationModelException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1683838127328936001L;

	public MalformedOSDiModelException(String message, Throwable cause) {
		super(message, cause);
	}

	public MalformedOSDiModelException(OSDiClasses involvedClass, String instanceName, OSDiDataProperties involvedProperty, String content) {
		super("(" + involvedClass.getShortName() + ") " + instanceName + ":" + involvedProperty.getShortName() + "\tError parsing\"" + content + "\"");
	}

	public MalformedOSDiModelException(OSDiClasses involvedClass, String instanceName, OSDiDataProperties involvedProperty, String content, Throwable cause) {
		super("(" + involvedClass.getShortName() + ") " + instanceName + ":" + involvedProperty.getShortName() + "\tError parsing\"" + content + "\"", cause);
	}

	public MalformedOSDiModelException(OSDiClasses involvedClass, String instanceName, OSDiObjectProperties involvedProperty, String content) {
		super("(" + involvedClass.getShortName() + ") " + instanceName + ":" + involvedProperty.getShortName() + "\tError parsing\"" + content + "\"");
	}

	public MalformedOSDiModelException(OSDiClasses involvedClass, String instanceName, OSDiObjectProperties involvedProperty, String content, Throwable cause) {
		super("(" + involvedClass.getShortName() + ") " + instanceName + ":" + involvedProperty.getShortName() + "\tError parsing\"" + content + "\"", cause);
	}

	public MalformedOSDiModelException(String message) {
		super(message);
	}
}