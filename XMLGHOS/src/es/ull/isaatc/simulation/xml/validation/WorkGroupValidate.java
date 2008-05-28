/**
 * 
 */
package es.ull.isaatc.simulation.xml.validation;

import java.util.TreeMap;

import es.ull.isaatc.simulation.xml.BaseComponent;
import es.ull.isaatc.simulation.xml.ComponentRef;
import es.ull.isaatc.simulation.xml.ModelMappingTable;
import es.ull.isaatc.simulation.xml.WorkGroup;
import es.ull.isaatc.simulation.xml.WorkGroup.Role;

/**
 * @author Roberto Muñoz
 */
public class WorkGroupValidate extends DescComponentValidate {
	/** Resource type validator */
	ResourceTypeValidate resTypeVal;

	/** Error messages */
	protected static final String ERROR_TYPE = "Work group";

	/** Error messages */
	protected static final String RESTYPE_REFERENCE = "Work group resource type reference";

	public WorkGroupValidate(ResourceTypeValidate resTypeVal,
			TreeMap<Integer, ModelMappingTable> modelList) {
		super(modelList);
		this.resTypeVal = resTypeVal;
	}

	protected boolean checkWorkGroup(WorkGroup wg) {
		boolean hasError = false;

		// FIXME: hacer lo del chequeo del período con ciclos
		for (Role role : wg.getRole()) {
			ComponentRef ref = role.getRtRef();
			if (!resTypeVal.checkReference(ref)) {
				error(wg, RESTYPE_REFERENCE);
				hasError = true;
			}
		}
		return hasError;
	}

	@Override
	public boolean validate(BaseComponent component) throws ModelException {
		WorkGroup wg = (WorkGroup) component;
		boolean hasError = false;

//		hasError |= checkComponent(wg);
		hasError |= checkWorkGroup(wg);

		if (hasError) {
			throw new ModelException(ERROR_TYPE);
		}

		return !hasError;
	}

	@Override
	public boolean checkReference(ComponentRef ref) {
		if (ref.getModelId() < 0 || ref.getId() < 0)
			return false;
		ModelMappingTable map = modelList.get(Integer.valueOf(ref.getModelId()));
		if (map == null)
			return false;
		if (map.getWorkGroup(ref.getId()) == null)
			return false;
		return true;
	}
}
