package es.ull.iis.simulation.hta.radios.exceptions;

/**
 * @author David Prieto González
 *
 */
public class TransformException extends Exception {
	private static final long serialVersionUID = -5635100754738386990L;

	public TransformException() {
		super();
	}

	/**
	 * @param message
	 */
	public TransformException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public TransformException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public TransformException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public TransformException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
