package it.gamified.db2.exceptions;

public class PastDate extends Exception{
	private static final long serialVersionUID = 1L;
	
	public PastDate(String message) {
		super(message);
	}

}
