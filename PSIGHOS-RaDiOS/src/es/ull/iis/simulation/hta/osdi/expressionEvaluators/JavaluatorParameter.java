package es.ull.iis.simulation.hta.osdi.expressionEvaluators;

import com.fathzer.soft.javaluator.DoubleEvaluator;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.Parameter;

public class JavaluatorParameter extends Parameter {
    final private static DoubleEvaluator evaluator = new DoubleEvaluator();
    private final String expression;

    public JavaluatorParameter(HTAModel model, String name, String description, String source, int year, ParameterType type, String expression) {
        super(model, name, description, source, year, type);
		this.expression = expression;
    }

    @Override
    public double getValue(Patient pat) {
        final JavaluatorPatient jc = new JavaluatorPatient(pat);
        return evaluator.evaluate(expression, jc);
    }
}
