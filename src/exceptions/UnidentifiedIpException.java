package exceptions;

public class UnidentifiedIpException extends Exception{
	private static final long serialVersionUID = -2918791577964310040L;

	public UnidentifiedIpException(String msg) {
		super(msg);
	}
}
