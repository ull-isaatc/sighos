package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.Set;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

public class UncertaintyProcessor {
	
	private UncertaintyProcessor() {
		
	}
	
	
	private static RandomVariate parseExpressionPropertyAsProbabilityDistribution(String individualIRI) throws MalformedOSDiModelException {
		final String strValue = OSDiWrapper.DataProperty.HAS_EXPRESSION.getValue(individualIRI, "");
		if (strValue == "")
			return null;
		return ProbabilityDistribution.getInstanceFromExpression(strValue);		
	}
	
	private static RandomVariate getRandomVariateFromAvgAndCIs(double avg, double[] ci) {
		final double sd = Statistics.sdFrom95CI(ci);
		final double []paramsBeta = Statistics.betaParametersFromNormal(avg, sd);
		return RandomVariateFactory.getInstance("BetaVariate", paramsBeta[0], paramsBeta[1]);
	}
	
	public static RandomVariate process(OSDiWrapper wrap, ValuableWrapper deterministicValue, OSDiWrapper.ObjectProperty uncertaintyProperty) throws MalformedOSDiModelException {
		final String paramId = deterministicValue.getParamId();
		// Takes the uncertainty that characterizes this parameter, but only if it's included in the working model 
		final Set<String> uncertaintyParams = uncertaintyProperty.getValues(paramId, true);
		if (uncertaintyParams.size() == 1) {
//			final ExpressionWrapper expWrap = new ExpressionWrapper(OSDiWrapper.DataProperty.HAS_EXPRESSION.getValue((String)uncertaintyParams.toArray()[0], ""));
//			if (ExpressionWrapper.SupportedType.PROBABILITY_DISTRIBUTION.equals(expWrap.getType())) {
//				return expWrap.getRnd();
//			}
//			if (ExpressionWrapper.SupportedType.CONSTANT.equals(expWrap.getType())) {
//				return RandomVariateFactory.getInstance("ConstantVariate", expWrap.getConstantValue());
//			}
			return parseExpressionPropertyAsProbabilityDistribution((String)uncertaintyParams.toArray()[0]);
		}
		if (uncertaintyParams.size() == 2) {
			final String firstParamId = (String)uncertaintyParams.toArray()[0];
			Set<String> firstParamTypes = OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE.getValues(firstParamId);
			if (firstParamTypes.size() == 0)				
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, firstParamId, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Do not know what to do with uncertainty characterization since data item type is not defined");
			final ExpressionWrapper firstParamExp = new ExpressionWrapper(OSDiWrapper.DataProperty.HAS_EXPRESSION.getValue(firstParamId, null));
			final String secondParamId = (String)uncertaintyParams.toArray()[1];
			Set<String> secondParamTypes = OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE.getValues(secondParamId);
			if (secondParamTypes.size() == 0)				
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, secondParamId, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Do not know what to do with uncertainty characterization since data item type is not defined");
			if (firstParamTypes.contains(OSDiWrapper.DataItemType.DI_LOWER95CONFIDENCELIMIT.getInstanceName())) {
				if (secondParamTypes.contains(OSDiWrapper.DataItemType.DI_UPPER95CONFIDENCELIMIT.getInstanceName())) {
					return getRandomVariateFromAvgAndCIs(deterministicValue.getDeterministicValue(), null);
				}
				else {
					throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, secondParamId, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Upper and lower confidence intervals required to represent uncertainty. Found lower but not higher. Instead: " + secondParamTypes.toArray()[0]);
				}				
			}
			else if (secondParamTypes.contains(OSDiWrapper.DataItemType.DI_LOWER95CONFIDENCELIMIT.getInstanceName())) {
				if (firstParamTypes.contains(OSDiWrapper.DataItemType.DI_UPPER95CONFIDENCELIMIT.getInstanceName())) {
					// TODO: Process
				}
				else {
					throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, firstParamId, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Upper and lower confidence intervals required to represent uncertainty. Found higher but not lower. Instead: " + firstParamTypes.toArray()[0]);
				}				
			}

			return parseExpressionPropertyAsProbabilityDistribution((String)uncertaintyParams.toArray()[0]);
		}
		
		if (uncertaintyParams.size() > 2)
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramId, uncertaintyProperty, "More than two uncertainty characterization for a parameter not supported. Currently " + uncertaintyParams.size());
		return null;
	}

}
