/**
 * 
 */
package es.ull.isaatc.simulation.xml.validation;

import es.ull.isaatc.simulation.xml.BaseComponent;
import es.ull.isaatc.simulation.xml.ComponentRef;
import es.ull.isaatc.simulation.xml.ModelMappingTable;
import es.ull.isaatc.simulation.xml.Resource;
import es.ull.isaatc.simulation.xml.Resource.TimeTable;
import es.ull.isaatc.util.OrderedList;

/**
 * @author Roberto Muñoz
 */
public class ResourceValidate extends DescComponentValidate {

    /** Resource type validator */
    ResourceTypeValidate resTypeVal;

    private static String ERROR_TYPE = "Resource error";
    
    /** Error messages */
    protected static final String NELEM_DEFINITION = "Element number is incorrect";
    protected static final String TIMETABLE_DEFINITION = "Time table entry definition";
    protected static final String TIMETABLE_DURATION = "Time table entry duration";
    protected static final String RESTYPE_REFERENCE = "Time table resource type reference";

    public ResourceValidate(ResourceTypeValidate resTypeVal, OrderedList<ModelMappingTable> modelList) {
	super(modelList);
	this.resTypeVal = resTypeVal;
    }

    /**
     * 
     * @param res
     * @return
     */
    protected boolean checkNElem(Resource res) {

	boolean hasError = false;

	if (res.getUnits() < 0) {
	    error(res, NELEM_DEFINITION);
	    hasError = true;
	}
	return hasError;
    }

    /**
     * 
     * @param res
     * @return
     */
    protected boolean checkTimeTable(Resource res) {
	boolean hasError = false;

	for (TimeTable entry : res.getTimeTable()) {
	    // FIXME: hacer lo del chequeo del período con ciclos
	    if (entry.getDur().getValue() < 0) {
		error(res, TIMETABLE_DURATION);
		hasError = true;
	    }
	    for (ComponentRef ref : entry.getRtRef()) {
		if (!resTypeVal.checkReference(ref)) {
		    error(res, RESTYPE_REFERENCE);
		    hasError = true;
		}
	    }
	}
	return hasError;
    }

    @Override
    public boolean validate(BaseComponent component) throws ModelException {
	Resource res = (Resource)component;
	boolean hasError = false;

	hasError |= checkComponent(res);
	hasError |= checkNElem(res);
	hasError |= checkTimeTable(res);

	if (hasError) {
	    throw new ModelException(ERROR_TYPE);
	}

	return !hasError;
    }

    @Override
    public boolean checkReference(ComponentRef ref) {
	return true;
    }
}
