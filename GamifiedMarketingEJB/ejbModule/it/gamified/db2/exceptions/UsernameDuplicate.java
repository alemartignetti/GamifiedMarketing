package it.gamified.db2.exceptions;

public class UsernameDuplicate extends Exception {
	private static final long serialVersionUID = 1L;

	public UsernameDuplicate(String message) {
		super(message);
	}
}

