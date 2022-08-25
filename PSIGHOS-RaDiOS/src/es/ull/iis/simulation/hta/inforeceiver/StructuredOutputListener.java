/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

/**
 * The listeners that implement this interface must be prepared to print results in a single line, finishing by a tab.
 * @author Iván Castilla Rodríguez
 *
 */
public interface StructuredOutputListener {
	String STR_AVG_PREFIX = "AVG_";
	String STR_L95CI_PREFIX = "L95CI_";
	String STR_U95CI_PREFIX = "U95CI_";
	String SEP = "\t";

}
