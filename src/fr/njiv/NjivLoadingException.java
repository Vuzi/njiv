package fr.njiv;

/**
 * 
 * @author Vuzi
 *
 */
public class NjivLoadingException extends Exception {

	private static final long serialVersionUID = -1928533208907810815L;
	
	Exception exception = null;

	public NjivLoadingException(String msg) {
		super(msg);
	}
	
	public NjivLoadingException(String msg, Exception exception) {
		super(msg);
		this.exception = exception;
	}
	
}
