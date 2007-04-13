/**
 * 
 */
package es.ull.isaatc.simulation.xml;

/**
 * @author Roberto Muñoz
 */
public class FunctionChoiceUtility {

    public static TimeFunction getSelectedFunction(FunctionChoice fc) {

	if (fc.getConstant() != null)
	    return fc.getConstant(); 
	if (fc.getPoly() != null)
	    return fc.getPoly(); 
	if (fc.getRandom() != null)
	    return fc.getRandom(); 
	return null;
    }
}
