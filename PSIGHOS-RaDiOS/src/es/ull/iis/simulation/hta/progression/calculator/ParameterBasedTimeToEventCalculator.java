package es.ull.iis.simulation.hta.progression.calculator;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.model.TimeUnit;

public class ParameterBasedTimeToEventCalculator implements TimeToEventCalculator {
    private final TimeUnit timeUnit;
    private final String parameterName;

    public ParameterBasedTimeToEventCalculator(String parameterName, TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        this.parameterName = parameterName;
    }

    public ParameterBasedTimeToEventCalculator(String parameterName) {
        this(parameterName, TimeUnit.YEAR);
    }

    @Override
    public double getTimeToEvent(Patient pat) {
        return pat.getSimulation().getModel().getParameterValue(parameterName, pat);
    }

    @Override
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
