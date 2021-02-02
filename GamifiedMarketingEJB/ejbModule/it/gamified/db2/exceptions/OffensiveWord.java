package it.gamified.db2.exceptions;

public class OffensiveWord extends Exception {
	private static final long serialVersionUID = 1L;
	
	public OffensiveWord(String message, String offensive) {
		super(message);
	}

	
}
