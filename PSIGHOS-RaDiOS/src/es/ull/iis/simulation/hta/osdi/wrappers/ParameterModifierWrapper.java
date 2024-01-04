/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.Set;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataItemTypes;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiClasses;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiObjectProperties;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.SecondOrderNatureParameter;
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
	public ParameterModifierWrapper(OSDiWrapper wrap, String modifierName, Intervention intervention) throws MalformedOSDiModelException {
		super(wrap, modifierName, "Modification produced by intervention " + intervention.name());
		modifiedItems = OSDiObjectProperties.MODIFIES.getValues(modifierName, true);
		if (getDataItemTypes().contains(OSDiDataItemTypes.DI_CONTINUOUS_VARIABLE))
			type = Type.SET;
		else if (getDataItemTypes().contains(OSDiDataItemTypes.DI_FACTOR) || getDataItemTypes().contains(OSDiDataItemTypes.DI_RELATIVE_RISK))
			type = Type.FACTOR;
		else if (getDataItemTypes().contains(OSDiDataItemTypes.DI_MEAN_DIFFERENCE))
			type = Type.DIFF;
		else {
			throw new MalformedOSDiModelException(OSDiClasses.PARAMETER, modifierName, OSDiObjectProperties.HAS_DATA_ITEM_TYPE, "None of the data item types defined for the modification are currently supported");
		}
		this.intervention = intervention;		
	}

	public void registerParameter(HTAModel model) {
		// TODO: Create the parameter according to its definition
		final Parameter param = new SecondOrderNatureParameter(model, getOriginalIndividualIRI(), getDescription(), getSource(), getYear(), ParameterType.OTHER, getDeterministicValue(), getProbabilisticValue());
		model.addParameter(param);
		ParameterModifier mod = null;
		switch(type) {
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
	}
}
