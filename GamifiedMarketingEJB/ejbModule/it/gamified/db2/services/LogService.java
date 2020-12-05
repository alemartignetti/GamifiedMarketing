package it.gamified.db2.services;

import java.util.List;
import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;

import it.gamified.db2.exceptions.*;
import it.gamified.db2.entities.Answer;
import it.gamified.db2.entities.Questionnaire;
import it.gamified.db2.entities.Review;
import it.gamified.db2.entities.User;

@Stateless
public class LogService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	private EntityManager em;

	public LogService() {
	}

	public List<Answer> findLogs(Questionnaire quest) {
		List<Answer> answers = em.createQuery("Select a from Answer a " + "where a.questionnaire = :quest", Answer.class)
				.setParameter("quest", quest).getResultList();

		if (answers.isEmpty())
			System.out.println("EMPTY");
		
		return answers;
	}

}