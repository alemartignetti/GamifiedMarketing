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
import it.gamified.db2.entities.MarketingQuestion;
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
		List<Questionnaire> questionnaires = em.createNamedQuery("Questionnaire.getAll", Questionnaire.class)
				.getResultList();
		return questionnaires;
	}
	
	public List<Questionnaire> findPastQuestionnaires() {
		Date current_date = new Date();
		List<Questionnaire> questionnaires = em.createNamedQuery("Questionnaire.getPastQuest", Questionnaire.class)
				.setParameter(1, current_date)
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
		// All managed by cascade
		Questionnaire toBeDeleted = em.find(Questionnaire.class, id);
		em.remove(toBeDeleted);
	}

	public void addReviewToQuestionnaire(Questionnaire q, User u, String text) {
		Review r = new Review(text, u, q);

		System.out.println("USER: " + u.getId() + " posting review to QUESTIONNAIRE: " + q.getId());
		q.addReview(r);
		em.persist(r);
		em.merge(q);

		System.out.println("Success");
	}
	

	public Questionnaire createQuestionnaire(String product_name, byte[] image, Date ref_date, List<String> questions) throws NonUniqueDailyQuestionnaire, NoDailyQuestionnaire{
		
		// Check that no questionnaire exist with given date
		List<Questionnaire> questionnaires = em.createNamedQuery("Questionnaire.getQuestionnaire", Questionnaire.class)
				.setParameter("date", ref_date, TemporalType.DATE).getResultList();

		if (questionnaires.size() > 1) {
			throw new NonUniqueDailyQuestionnaire(
					"Double Questionnaire already present. This is illegal by database constraint.");
		} else if (!questionnaires.isEmpty()) {
			throw new NoDailyQuestionnaire("Questionnaire for the given date already Present.");
		}
		
        Questionnaire quest = new Questionnaire(ref_date, product_name, image);	
        int ordering = 1;
        
        for(String text : questions ){
                MarketingQuestion mk = new MarketingQuestion(text, ordering);
                quest.addQuestion(mk);
                ordering += 1;
        }
        
        em.persist(quest); // Persist cascade on marketingQuestions;

        return quest;

	}
}
