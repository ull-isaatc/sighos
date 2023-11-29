/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.Set;

import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository.ParameterType;
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
	public ParameterModifierWrapper(OSDiWrapper wrap, String modifierName, Intervention intervention) throws MalformedOSDiModelException {
		super(wrap, modifierName, "Modification produced by intervention " + intervention.name());
		modifiedItems = OSDiWrapper.ObjectProperty.MODIFIES.getValues(modifierName, true);
		if (getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_CONTINUOUS_VARIABLE))
			type = Type.SET;
		else if (getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_FACTOR) || getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_RELATIVE_RISK))
			type = Type.FACTOR;
		else if (getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_MEAN_DIFFERENCE))
			type = Type.DIFF;
		else {
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.PARAMETER, modifierName, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "None of the data item types defined for the modification are currently supported");
		}
		this.intervention = intervention;		
	}

	public void registerParameter(SecondOrderParamsRepository secParams) {
		final Parameter param = new Parameter(secParams, getParamId(), getDescription(), getSource(), getDeterministicValue(), getProbabilisticValue());
		secParams.addParameter(param, ParameterType.MODIFICATION);
		ParameterModifier mod = null;
		switch(type) {
		case DIFF:
			mod = new DiffParameterModifier(param);
			break;
		case FACTOR:
			mod = new FactorParameterModifier(param);
			break;
		case SET:
			mod = new SetParameterModifier(param);
			break;
		default: // Should never occur
			mod = new SetParameterModifier(param);			
			break;		
		}
		for (String modifiedParam : modifiedItems) {
			secParams.addParameterModifier(modifiedParam, intervention, mod);
		}
	}
}
