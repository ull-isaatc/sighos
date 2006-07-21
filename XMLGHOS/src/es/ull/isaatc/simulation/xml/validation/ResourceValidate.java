/**
 * 
 */
package es.ull.isaatc.simulation.xml.validation;

import java.util.Iterator;
import java.util.List;

import es.ull.isaatc.simulation.xml.Resource;
import es.ull.isaatc.simulation.xml.Resource.TimeTable;


/**
 * @author Roberto Muñoz
 *
 */
public class ResourceValidate extends Validate {
	/**Resource type validator */
	ResourceTypeValidate resTypeVal;
	
	/** Error messages */
	protected static final String NELEM_DEFINITION = "elements number is incorrect";
	protected static final String TIMETABLE_DEFINITION = "Time table entry definition";
	protected static final String RESTYPE_REFERENCE = "Time table resource type reference";
	/**
	 * 
	 * @param resTypeVal
	 */
	public ResourceValidate(ResourceTypeValidate resTypeVal) {
		super();
		this.resTypeVal = resTypeVal;	
	}
	
	protected boolean checkNElem(int id, int nelem) {
		boolean hasError = false;
		
		if (nelem <= 0) {
			error(id, NELEM_DEFINITION);
			hasError = true;
		}
		return hasError;
	}
	/**
	 * 
	 * @param id
	 * @param timeTable
	 * @return
	 */
	protected boolean checkTimeTable(int id, List<TimeTable> timeTable) {
		Iterator<TimeTable> timeTableIt = timeTable.iterator();
		boolean hasError = false;
		
		while (timeTableIt.hasNext()){
			TimeTable entry = timeTableIt.next();
			//FIXME: hacer lo del chequeo del período con ciclos
			if (entry.getDur().getValue() < 0) {
				error(id, TIMETABLE_DEFINITION);
				hasError = true;
			}
			Iterator<Integer> rtIt = entry.getRtId().iterator();
			while (rtIt.hasNext()) {
				if (!resTypeVal.has(rtIt.next(), null)) {
					error(id, RESTYPE_REFERENCE);
					hasError = true;
				}
			}
		}
		return hasError;
	}
	
	@Override
	public boolean validate(Object valObj) throws ModelException {
		Resource r = (Resource)valObj;
		boolean hasError = false;
		
		hasError |= checkId(r.getId());
		hasError |= checkDescription(r.getId(), r.getDescription());
		hasError |= checkNElem(r.getId(), r.getUnits());
		hasError |= checkTimeTable(r.getId(), r.getTimeTable());
		hasError |= has(r.getId(), r.getDescription());
		
		if (hasError) {
			throw new ModelException("Resource error");
		}
		add(r.getId(), r.getDescription());
		return !hasError;
	}
}
