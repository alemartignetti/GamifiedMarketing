package it.gamified.db2.exceptions;

public class InvalidQuestionAnswer extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidQuestionAnswer(String message) {
		super(message);
	}
}
