package it.gamified.db2.services;
import java.util.List;
import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;

import it.gamified.db2.exceptions.*;
import it.gamified.db2.entities.Questionnaire;
import it.gamified.db2.entities.Review;
import it.gamified.db2.entities.User;

@Stateless
public class AnswerService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	private EntityManager em;

	public AnswerService() {
	}

	//We chose this type of method for retrieving the daily questionnaire
	//Might be a problem for midnight
	public void submitAnswer() throws OffensiveWord {
		
	}
}