package it.gamified.db2.services;

import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
public class QuestionnaireService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	private EntityManager em;

	public QuestionnaireService() {
	}

	// We chose this type of method for retrieving the daily questionnaire
	// Might be a problem for midnight
	public Questionnaire findDailyQuestionnaire() throws NonUniqueDailyQuestionnaire, NoDailyQuestionnaire {
		Date currentDate = new Date();
		
		
		List<Questionnaire> questionnaires = em.createNamedQuery("Questionnaire.getQuestionnaire", Questionnaire.class)
				.setParameter("date", currentDate, TemporalType.DATE).getResultList();
		
		System.out.println("Number of daily questionnaire: " + Integer.toString(questionnaires.size()));
		if (questionnaires.size() > 1) {
			throw new NonUniqueDailyQuestionnaire(
					"Double Questionnaire of the day found. This is illegal by database constraint.");
		} else if (questionnaires.isEmpty()) {
			throw new NoDailyQuestionnaire("Daily Questionnaire is missing.");
		} else {
			Questionnaire quest = questionnaires.get(0);
			return quest;
		}
	}

	public List<Questionnaire> findAllQuestionnaires() {
		List<Questionnaire> questionnaires = em.createQuery("Select q from Questionnaire q", Questionnaire.class)
				.getResultList();
		return questionnaires;
	}

	// (String date) in format 'yyyy-MM-dd'
	public Questionnaire findQuestionnaire(String date)
			throws NonUniqueDailyQuestionnaire, NoDailyQuestionnaire, ParseException {
		Date date_obj = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		
		List<Questionnaire> questionnaires = em.createNamedQuery("Questionnaire.getQuestionnaire", Questionnaire.class)
				.setParameter("date", date_obj, TemporalType.DATE).getResultList();
		
		System.out.println("Number of daily questionnaire: " + Integer.toString(questionnaires.size()));
		if (questionnaires.size() > 1) {
			throw new NonUniqueDailyQuestionnaire(
					"Double Questionnaire of the day found. This is illegal by database constraint.");
		} else if (questionnaires.isEmpty()) {
			throw new NoDailyQuestionnaire("Daily Questionnaire is missing.");
		} else {
			Questionnaire quest = questionnaires.get(0);
			return quest;
		}
	}
	
	public Questionnaire findQuestionnaire(int quest_id) {
		Questionnaire q = em.find(Questionnaire.class, quest_id);
		return q;
	}
	
	public void deleteQuestionnaire(int id) {
		Questionnaire toBeDeleted = em.find(Questionnaire.class, id);
		em.remove(toBeDeleted);
	}

	public void addReviewToQuestionnaire(Questionnaire q, String text, int uid) {
		//em.refresh(em.merge(q));
		q = em.find(Questionnaire.class, q.getId());
		User u = em.find(User.class, uid);
		Review r = new Review(text, u, q);

		System.out.println("USER: " + uid + " posting review to QUESTIONNAIRE: " + q.getId());
		q.addReview(r);
		em.persist(r);

		System.out.println("Success");
	}

}
