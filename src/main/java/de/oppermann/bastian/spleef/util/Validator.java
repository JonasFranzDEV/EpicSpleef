package de.oppermann.bastian.spleef.util;

/**
 * Class to validate parameters.
 * 
 * @author Bastian Oppermann
 */
public class Validator {

	// this class uses static methods
	private Validator() { }
	
	/**
	 * Throws an {@link IllegalArgumentException} if param is null.
	 * 
	 * @param param The parameter to check.
	 * @param paramName The name of the parameter.
	 */
	public static void validateNotNull(Object param, String paramName) {
		if (paramName == null) {
			throw new IllegalArgumentException("paramName cannot be null");
		}
		if (param == null) {
			throw new IllegalArgumentException(paramName + " cannot be null");
		}
	}
	
}
