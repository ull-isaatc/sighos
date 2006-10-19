package es.ull.isaatc.simulation.xml;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.ValidationEventHandler;

public class SighosValidationEventHandler implements ValidationEventHandler {

    public boolean handleEvent(ValidationEvent ve) {

	if (ve.getSeverity() == ValidationEvent.FATAL_ERROR
		|| ve.getSeverity() == ValidationEvent.ERROR) {
	    ValidationEventLocator locator = ve.getLocator();
	    System.out.printf("ERROR[%d,%d] : %s\n", locator.getLineNumber(),
		    locator.getColumnNumber(), ve.getMessage());
	    return false;
	}
	return true;
    }

}