package es.ull.iis.simulation.hta.params;

import java.util.Map;

public interface UsesParameters {
    String getUsedParameterName(UsedParameter param);
    void setUsedParameterName(UsedParameter param, String name);
    Map<UsedParameter, String> getUsedParameterNames();

}
