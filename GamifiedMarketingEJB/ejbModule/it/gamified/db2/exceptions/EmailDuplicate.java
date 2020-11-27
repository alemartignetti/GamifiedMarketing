package it.gamified.db2.exceptions;

public class EmailDuplicate extends Exception {
	private static final long serialVersionUID = 1L;

	public EmailDuplicate(String message) {
		super(message);
	}
}

