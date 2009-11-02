/**
 * 
 */
package es.ull.isaatc.simulation.common;

import java.util.EnumSet;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface TimeDrivenActivity extends Activity {
	/** Indicates special characteristics of this activity */
	enum Modifier {
	    /** Indicates that this activity is non presential, i.e., an element can perform other activities at
	     * the same time */
		NONPRESENTIAL,
		/** Indicates that the activity can be interrupted in case the required resources end their
		 * availability time */
		INTERRUPTIBLE
	}
	boolean isNonPresential();
	boolean isInterruptible();
	EnumSet<Modifier> getModifiers();
}
