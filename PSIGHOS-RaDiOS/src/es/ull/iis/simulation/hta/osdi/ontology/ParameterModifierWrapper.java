/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.ontology;

import java.util.Set;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.Parameter.ParameterType;
import es.ull.iis.simulation.hta.params.modifiers.DiffParameterModifier;
import es.ull.iis.simulation.hta.params.modifiers.FactorParameterModifier;
import es.ull.iis.simulation.hta.params.modifiers.ParameterModifier;
import es.ull.iis.simulation.hta.params.modifiers.SetParameterModifier;

/**
 * @author Iván Castilla
 *
 */
public class ParameterModifierWrapper extends ParameterWrapper {
	enum Type {
		SET,
		DIFF,
		FACTOR
	}
	private final Set<String> modifiedItems;
	private final Type type;
	private final Intervention intervention;
	/**
	 * 
	 */
	protected ParameterModifierWrapper(OSDiWrapper wrap, String modifierName, Intervention intervention) throws MalformedOSDiModelException {
		super(wrap, modifierName, "Modification produced by intervention " + intervention.name());
		modifiedItems = OSDiObjectProperties.MODIFIES.getValues(modifierName, true);
		if (OSDiDataItemTypes.DI_CONTINUOUS_VARIABLE.equals(getDataItemType()))
			type = Type.SET;
		else if (OSDiDataItemTypes.DI_FACTOR.equals(getDataItemType()) || OSDiDataItemTypes.DI_RELATIVE_RISK.equals(getDataItemType()))
			type = Type.FACTOR;
		else if (OSDiDataItemTypes.DI_MEAN_DIFFERENCE.equals(getDataItemType()))
			type = Type.DIFF;
		else {
			throw new MalformedOSDiModelException(OSDiClasses.PARAMETER, modifierName, OSDiObjectProperties.HAS_DATA_ITEM_TYPE, "None of the data item types defined for the modification are currently supported");
		}
		this.intervention = intervention;		
	}

	@Override
	public Parameter createParameter(HTAModel model, ParameterType type) {
		final Parameter param = super.createParameter(model, type);
		ParameterModifier mod = null;
		switch(this.type) {
		case DIFF:
			mod = new DiffParameterModifier(param.name());
			break;
		case FACTOR:
			mod = new FactorParameterModifier(param.name());
			break;
		case SET:
			mod = new SetParameterModifier(param.name());
			break;
		default: // Should never occur
			mod = new SetParameterModifier(param.name());			
			break;		
		}
		for (String modifiedParam : modifiedItems) {
			model.addParameterModifier(modifiedParam, intervention, mod);
		}
		return param;
	}
}
