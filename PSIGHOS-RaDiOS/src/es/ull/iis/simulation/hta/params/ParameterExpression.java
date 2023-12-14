package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

public interface ParameterExpression {
    public double getValue(Patient pat);
}
