package es.ull.iis.simulation.hta.osdi.wrappers;

import com.fathzer.soft.javaluator.AbstractVariableSet;

import es.ull.iis.simulation.hta.Patient;

public class JavaluatorContext implements AbstractVariableSet<Double> {
    private final Patient pat;
    JavaluatorContext(Patient pat) {
        this.pat = pat;
    }

    @Override
    public Double get(String variableName) {
        return pat.getSimulation().getModel().getParameterValue(variableName, pat);
    }
    
}
