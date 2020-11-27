package it.gamified.db2.exceptions;

public class NonUniqueDailyQuestionnaire extends Exception {
	private static final long serialVersionUID = 1L;

	public NonUniqueDailyQuestionnaire(String message) {
		super(message);
	}
}

