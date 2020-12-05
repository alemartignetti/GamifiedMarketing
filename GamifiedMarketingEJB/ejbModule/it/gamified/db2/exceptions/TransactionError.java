package it.gamified.db2.exceptions;

public class TransactionError extends Exception {
	private static final long serialVersionUID = 1L;

	public TransactionError(String message) {
		super(message);
	}
}


