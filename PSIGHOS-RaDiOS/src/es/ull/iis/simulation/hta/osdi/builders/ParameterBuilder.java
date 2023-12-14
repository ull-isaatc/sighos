package es.ull.iis.simulation.hta.osdi.builders;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.osdi.OSDiGenericRepository;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.wrappers.ExpressableWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ExpressionWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ValuableWrapper;
import es.ull.iis.simulation.hta.params.ConstantNatureParameter;
import es.ull.iis.simulation.hta.params.FirstOrderNatureParameter;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.ParameterDescription;
import es.ull.iis.simulation.hta.params.SecondOrderNatureParameter;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

public class ParameterBuilder {

    /**
     * Creates a parameter from its IRI.
     * If the parameter defines just a constant value ({@link OSDiWrapper.ObjectProperty.HAS_EXPECTED_VALUE}), it is created as a {@link ConstantParameter}.
     * If the parameter defines a first order uncertainty characterization {@link OSDiWrapper.ObjectProperty.HAS_FIRST_ORDER_UNCERTAINTY}, it is created a
     * {@link FirstOrderParameter}, and the potential expected value and further uncertainty characterization {@link OSDiWrapper.ObjectProperty.HAS_SECOND_ORDER_UNCERTAINTY} is ignored.
     * If the parameter defines a second order uncertainty characterization {@link OSDiWrapper.ObjectProperty.HAS_FIRST_ORDER_UNCERTAINTY} and an  expected value 
     * ({@link OSDiWrapper.ObjectProperty.HAS_EXPECTED_VALUE}), it is created a {@link SecondOrderParameter}.
     * @param secParams The repository where the parameter is defined
     * @param paramIRI The IRI of the parameter
     */
    public static Parameter getInstance(OSDiGenericRepository secParams, String paramIRI, String defaultDescription) throws MalformedOSDiModelException {
		// FIXME: Rethink. I should avoid the straightforward use of the parameters. Instead, I should use the name or extract the value
        Parameter param = secParams.getParameter(paramIRI);
        if (param != null)
            return param;

		final OSDiWrapper wrap = secParams.getOwlWrapper();
        if (!wrap.containsIndividual(paramIRI))
            throw new MalformedOSDiModelException("The parameter " + paramIRI + " is not defined in the ontology");
		ValuableWrapper paramWrap = new ValuableWrapper(wrap, paramIRI, defaultDescription);

		final ArrayList<ExpressableWrapper> firstOrderUncertaintyParams = paramWrap.getFirstOrderUncertaintyParams();
		final ArrayList<ExpressableWrapper> secondOrderUncertaintyParams = paramWrap.getSecondOrderUncertaintyParams();
		final ExpressionWrapper expression = paramWrap.getExpression();
		// Defines first order uncertainty, so ignores further characterization
		if (firstOrderUncertaintyParams.size() > 0) {
			wrap.printWarning(secondOrderUncertaintyParams.size() > 0, paramIRI, OSDiWrapper.ObjectProperty.HAS_SECOND_ORDER_UNCERTAINTY, "Using only first order uncertainty characterization: second order ignored");
			wrap.printWarning(expression != null, paramIRI, OSDiWrapper.ObjectProperty.HAS_EXPRESSION, "Using only first order uncertainty characterization: expression ignored");
			param = parseUncertaintyParameters(secParams, paramWrap, firstOrderUncertaintyParams, expression, true);
		}
        // If the parameter defines an expression, further uncertainty characterization is ignored
		else if (expression != null) {
			// TODO: Treat the expression neither as a first-order/second-order parameter. Either create a new type of probabilistic parameter or parse with Javaluator/Expression language
		}
		else if (!Double.isNaN(paramWrap.getDeterministicValue())) {
			// Just a constant parameter
			if (secondOrderUncertaintyParams.size() == 0) {
            	param = new ConstantNatureParameter(secParams, paramIRI, getParameterDescription(paramWrap), paramWrap.getDeterministicValue());
			}
			else {
				param = parseUncertaintyParameters(secParams, paramWrap, secondOrderUncertaintyParams, expression, false);
			}
		}
		else if (secondOrderUncertaintyParams.size() > 0) {
            throw new MalformedOSDiModelException("The parameter " + paramIRI + " defines an expression with second order nature, but it does not define an expected value");
		}
		else {
            throw new MalformedOSDiModelException("The parameter " + paramIRI + " does not define a valid value (expression, expected value or similar");
		}
        return param;
    }

	private static Parameter parseUncertaintyParameters(OSDiGenericRepository secParams, ValuableWrapper paramWrap,
			final ArrayList<ExpressableWrapper> uncertaintyParams, final ExpressionWrapper expression, boolean firstOrder) throws MalformedOSDiModelException {
		String paramIRI = paramWrap.getOriginalIndividualIRI();
		// wrap.printWarning(detValue != null, paramIRI, OSDiWrapper.DataProperty.HAS_EXPECTED_VALUE, "Using only first order uncertainty characterization: expected value ignored");
		if (uncertaintyParams.size() == 1) {
			final ExpressableWrapper uncertainParamWrap = uncertaintyParams.get(0);
			if (uncertainParamWrap instanceof ValuableWrapper)
				return parseUncertainParameter(secParams, paramWrap, (ValuableWrapper)uncertainParamWrap, firstOrder);
			if (uncertainParamWrap instanceof ExpressionWrapper)
				return parseUncertainParameter(secParams, paramWrap, (ExpressionWrapper)uncertainParamWrap, firstOrder);
		}
		else if (uncertaintyParams.size() == 2) {
			if (uncertaintyParams.get(0) instanceof ExpressionWrapper || uncertaintyParams.get(1) instanceof ExpressionWrapper)
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramIRI, OSDiWrapper.ObjectProperty.HAS_FIRST_ORDER_UNCERTAINTY, "Unsupported combination of valuables (" + uncertaintyParams.get(0).getOriginalIndividualIRI() + ", " + uncertaintyParams.get(1).getOriginalIndividualIRI() + ") to define the uncertainty");
			return parseUncertainParameter(secParams, paramWrap, (ValuableWrapper)uncertaintyParams.get(0), (ValuableWrapper)uncertaintyParams.get(1), firstOrder);
		}
		throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramIRI, OSDiWrapper.ObjectProperty.HAS_FIRST_ORDER_UNCERTAINTY, "More than two uncertainty characterization for a parameter not supported. Currently " + uncertaintyParams.size());
	}

	private static Parameter parseUncertainParameter(OSDiGenericRepository secParams, ValuableWrapper paramWrap, ExpressionWrapper uncertainExpWrap, boolean firstOrder) throws MalformedOSDiModelException{
		final double detValue = paramWrap.getDeterministicValue();
		if (uncertainExpWrap.getRnd() != null)
			if (firstOrder) {
				secParams.getOwlWrapper().printWarning(!Double.isNaN(detValue), paramWrap.getOriginalIndividualIRI(), OSDiWrapper.DataProperty.HAS_EXPECTED_VALUE, "Using only first order uncertainty characterization: expected value ignored");
				return new FirstOrderNatureParameter(secParams, paramWrap.getOriginalIndividualIRI(), getParameterDescription(paramWrap), uncertainExpWrap.getRnd());
			}
			else {
				if (Double.isNaN(detValue))
					throw new MalformedOSDiModelException("The parameter " + paramWrap.getOriginalIndividualIRI() + " defines a second order uncertainty, but it does not define an expected value");
				return new SecondOrderNatureParameter(secParams, paramWrap.getOriginalIndividualIRI(), getParameterDescription(paramWrap), detValue, uncertainExpWrap.getRnd());
			}
		else {
			// TODO: Create parameter from ad-hoc expression
			return null;
		}
	}

	private static Parameter parseUncertainParameter(OSDiGenericRepository secParams, ValuableWrapper paramWrap, ValuableWrapper uncertainParamWrap, boolean firstOrder) throws MalformedOSDiModelException{
		// If the uncertainty is characterized by a standard deviation, then we use a normal distribution
		if (uncertainParamWrap.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_STANDARD_DEVIATION)) {
			double detValue = paramWrap.getDeterministicValue();
			if (Double.isNaN(detValue))
				throw new MalformedOSDiModelException("The parameter " + paramWrap.getOriginalIndividualIRI() + " defines an uncertainty characterized by a standard deviation (" + uncertainParamWrap.getOriginalIndividualIRI() + "), but it does not define an expected value");
			if (firstOrder)
				return new FirstOrderNatureParameter(secParams, paramWrap.getOriginalIndividualIRI(), getParameterDescription(paramWrap), RandomVariateFactory.getInstance("NormalVariate", detValue, uncertainParamWrap.getDeterministicValue()));
			else
				return new SecondOrderNatureParameter(secParams, paramWrap.getOriginalIndividualIRI(), getParameterDescription(paramWrap), detValue, RandomVariateFactory.getInstance("NormalVariate", detValue, uncertainParamWrap.getDeterministicValue()));
		}
		else {
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, uncertainParamWrap.getOriginalIndividualIRI(), OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Data item type not supported for characterizing uncertainty.");
		}
	
	}

	private static Parameter parseUncertainParameter(OSDiGenericRepository secParams, ValuableWrapper paramWrap, ValuableWrapper uncertainParamWrap1, ValuableWrapper uncertainParamWrap2, boolean firstOrder) throws MalformedOSDiModelException { 
		double detValue = paramWrap.getDeterministicValue();
		final RandomVariate rnd;

		if (uncertainParamWrap1.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_LOWER_95_CONFIDENCE_LIMIT) &&
			uncertainParamWrap2.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_UPPER_95_CONFIDENCE_LIMIT)) {
				rnd = getRandomVariateFromAvgAndCIs(detValue, new double[] {uncertainParamWrap1.getDeterministicValue(), uncertainParamWrap2.getDeterministicValue()});
		} 
		else if (uncertainParamWrap2.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_LOWER_95_CONFIDENCE_LIMIT) &&
			uncertainParamWrap1.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_UPPER_95_CONFIDENCE_LIMIT)) {
				rnd = getRandomVariateFromAvgAndCIs(detValue, new double[] {uncertainParamWrap2.getDeterministicValue(), uncertainParamWrap1.getDeterministicValue()});
		}
		else {
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramWrap.getOriginalIndividualIRI(), 
			firstOrder ? OSDiWrapper.ObjectProperty.HAS_FIRST_ORDER_UNCERTAINTY : OSDiWrapper.ObjectProperty.HAS_SECOND_ORDER_UNCERTAINTY, "Unsupported combination of valuables (" + uncertainParamWrap1.getOriginalIndividualIRI() + ", " + uncertainParamWrap2.getOriginalIndividualIRI() + ") to define the uncertainty");
		}
		if (firstOrder)
			return new FirstOrderNatureParameter(secParams, paramWrap.getOriginalIndividualIRI(), getParameterDescription(paramWrap), rnd);
		else
			return new SecondOrderNatureParameter(secParams, paramWrap.getOriginalIndividualIRI(), getParameterDescription(paramWrap), detValue, rnd);
	}

    private static ParameterDescription getParameterDescription(ValuableWrapper paramWrap) {
        return new ParameterDescription(paramWrap.getDescription(), paramWrap.getSource(), paramWrap.getYear());
    }
	
	public static RandomVariate getRandomVariateFromAvgAndCIs(double avg, double[] ci) {
		final double sd = Statistics.sdFrom95CI(ci);
		final double []paramsBeta = Statistics.betaParametersFromNormal(avg, sd);
		return RandomVariateFactory.getInstance("BetaVariate", paramsBeta[0], paramsBeta[1]);
	}

}
