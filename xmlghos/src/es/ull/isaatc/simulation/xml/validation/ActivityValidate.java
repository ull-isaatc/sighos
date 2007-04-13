/**
 * 
 */
package es.ull.isaatc.simulation.xml.validation;

import es.ull.isaatc.simulation.xml.Activity;
import es.ull.isaatc.simulation.xml.BaseComponent;
import es.ull.isaatc.simulation.xml.ComponentRef;
import es.ull.isaatc.simulation.xml.ModelMappingTable;
import es.ull.isaatc.simulation.xml.Activity.WorkGroup;
import es.ull.isaatc.simulation.xml.WorkGroup.Role;
import es.ull.isaatc.util.OrderedList;

/**
 * @author Roberto Muñoz
 */
public class ActivityValidate extends DescComponentValidate {
	/** Work Group validator */
	WorkGroupValidate wgVal;

	private static String ERROR_TYPE = "Activity error";

	/** Error messages */
	protected static final String RESTYPE_REFERENCE = "Work group resource type reference";
	protected static final String WG_REFERENCE = "Work group reference";

	public ActivityValidate(WorkGroupValidate resTypeVal,
			OrderedList<ModelMappingTable> modelList) {
		super(modelList);
		this.wgVal = resTypeVal;
	}

	protected boolean checkWorkGroup(Activity act) throws ModelException {
		boolean hasError = false;

		for (WorkGroup wg : act.getWorkGroup()) {
			// FIXME: hacer lo del chequeo del período con ciclos
			if (wg.getWorkGroup() != null) {
				if (!wgVal.checkReference(wg.getWorkGroup())) {
					error(act, WG_REFERENCE);
					hasError = true;
				}
			}
			else {
				wgVal.validate(wg.getDefinition());
				for (Role role : wg.getDefinition().getRole()) {
					ComponentRef ref = role.getRtRef();
					if (!wgVal.checkReference(ref)) {
						error(act, RESTYPE_REFERENCE);
						hasError = true;
					}
				}
			}
		}
		return hasError;
	}

	@Override
	public boolean validate(BaseComponent component) throws ModelException {
		Activity act = (Activity) component;
		boolean hasError = false;

		hasError |= checkComponent(act);
		hasError |= checkWorkGroup(act);

		if (hasError) {
			throw new ModelException(ERROR_TYPE);
		}

		return !hasError;
	}

	@Override
	public boolean checkReference(ComponentRef ref) {
		if (ref.getModelId() < 0 || ref.getId() < 0)
			return false;
		ModelMappingTable map = modelList
				.get(Integer.valueOf(ref.getModelId()));
		if (map == null)
			return false;
		if (map.getActivity(ref.getId()) == null)
			return false;
		return true;
	}
}
