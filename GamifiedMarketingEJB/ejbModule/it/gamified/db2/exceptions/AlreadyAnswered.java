package it.gamified.db2.exceptions;

public class AlreadyAnswered extends Exception {
	private static final long serialVersionUID = 1L;

	public AlreadyAnswered(String message) {
		super(message);
	}
}

