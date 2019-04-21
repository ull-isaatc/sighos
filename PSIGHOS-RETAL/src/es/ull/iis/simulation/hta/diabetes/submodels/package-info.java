/**
 * Classes to represent different chronic and acute complications, as well as death submodels. When creating a diabetes 
 * model, the user can include or not each complication, and choose one of the models for each. Currently, the following submodels
 * are defined for each chronic complication (by extending {@link ChronicComplicationSubmodel}):
 * <ul>
 * <li>Retinopathy: {@link SimpleRETSubmodel}, {@link SheffieldRETSubmodel}</li>
 * <li>Nephropathy: {@link SimpleNPHSubmodel}, {@link SheffieldNPHSubmodel}</li>
 * <li>Neuropathy: {@link SimpleNEUSubmodel}</li>
 * <li>Coronary heart disease: {@link SimpleCHDSubmodel}</li>
 *  </ul>
 * Only severe hypoglycemia is included as an acute event, either using {@link LySevereHypoglycemiaEvent} or {@link BattelinoSevereHypoglycemiaEvent}.
 * <p>A death submodel for the Spanish population is also included ({@link StandardSpainDeathSubmodel}).

 *  
 * @author Iván Castilla Rodríguez
 *
 */
package es.ull.iis.simulation.hta.diabetes.submodels;
