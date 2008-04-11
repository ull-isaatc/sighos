/**
 * 
 */
package es.ull.isaatc.simulation.xml.validation;

import java.util.TreeMap;

import es.ull.isaatc.simulation.xml.BaseComponent;
import es.ull.isaatc.simulation.xml.ComponentRef;
import es.ull.isaatc.simulation.xml.ModelMappingTable;

/**
 * @author Roberto Muñoz
 */
public class ResourceTypeValidate extends DescComponentValidate {

	private static String ERROR_TYPE = "Resource type error";

	public ResourceTypeValidate(TreeMap<Integer, ModelMappingTable> modelList) {
		super(modelList);
	}

	@Override
	public boolean validate(BaseComponent component) throws ModelException {
		boolean hasError = !super.validate(component);

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
		if (map.getResourceType(ref.getId()) == null)
			return false;
		return true;
	}
}
