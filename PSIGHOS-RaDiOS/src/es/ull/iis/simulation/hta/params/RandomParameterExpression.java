package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;
import simkit.random.RandomVariate;

public class RandomParameterExpression implements ParameterExpression {
    private final RandomVariate rnd;

    public RandomParameterExpression(RandomVariate rnd) {
        this.rnd = rnd;
    }

    @Override
    public double getValue(Patient pat) {
        return rnd.generate();
    }
}
