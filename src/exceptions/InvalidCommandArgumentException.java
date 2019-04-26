package exceptions;

public class InvalidCommandArgumentException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidCommandArgumentException(String msg) {
		super(msg);
	}
}
