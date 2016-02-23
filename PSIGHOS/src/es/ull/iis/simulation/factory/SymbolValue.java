package es.ull.iis.simulation.factory;

/**
 * Class which define the symbols used in expressions parser. This class define the content and the access methods of each symbol.
 * @author Yeray Callero
 *
 */
public class SymbolValue {

	/** Value obtained in the Lexical analysis */
	String value;
	/** Context (kind of user event) where the symbol was identified */
	String context;
	
	/**
	 * Generates a new SymbolValue.
	 * @param newValue The symbol's value.
	 * @param newContext The symbol's context.
	 */
	SymbolValue(String newValue, String newContext) {
		value = newValue;
		context = newContext;
	}

	/**
	 * Generates a new SymbolValue without context.
	 * @param newContext The sumbol's context.
	 */
	SymbolValue(String newContext) {
		value = new String();
		context = newContext;
	}

	/**
	 * Getter for the symbol's context.
	 * @return The context.
	 */
	public String getContext() {
		return context;
	}

	/**
	 * Getter for the symbol's value.
	 * @return The value.
	 */
	public String getValue() {
		return value;
	}

}
