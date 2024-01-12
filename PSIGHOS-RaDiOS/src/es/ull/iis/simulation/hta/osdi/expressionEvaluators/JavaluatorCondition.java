package es.ull.iis.simulation.hta.osdi.expressionEvaluators;

import com.fathzer.soft.javaluator.DoubleEvaluator;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;

public class JavaluatorCondition extends Condition<DiseaseProgressionPathway.ConditionInformation> {
    // TODO: Should use a boolean evaluator (which does not exist)
    final private static DoubleEvaluator evaluator = new DoubleEvaluator();

    private final String expression;

    public JavaluatorCondition(String expression) {
        this.expression = expression;
    }
    
    @Override
    public boolean check(DiseaseProgressionPathway.ConditionInformation info) {
        final JavaluatorPatient jc = new JavaluatorPatient(info.getPatient());
        return (evaluator.evaluate(expression, jc) != 0.0);
    }
}
