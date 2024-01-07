package es.ull.iis.simulation.hta.osdi.wrappers;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlExpression;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.osdi.OSDiGenericModel;
import es.ull.iis.simulation.hta.params.Parameter;

public class ExpressionLanguageParameter extends Parameter {
	private final JexlExpression exprToEvaluate;

    public ExpressionLanguageParameter(HTAModel model, String name, String description, String source, int year, ParameterType type, String expression) {
        super(model, name, description, source, year, type);
		this.exprToEvaluate = OSDiGenericModel.JEXL.createExpression(expression);
    }

    @Override
    public double getValue(Patient pat) {
		final JexlContext jc = new ExpressionLanguagePatient(pat);
        double value = 0.0;
		try {
			value = (double)exprToEvaluate.evaluate(jc);
		} catch (JexlException ex) {
			System.err.println(ex.getMessage());
		}
        return value;
    }
}
