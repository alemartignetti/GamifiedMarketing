package it.gamified.db2.services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.gamified.db2.entities.Review;
import it.gamified.db2.entities.User;
import it.gamified.db2.entities.Questionnaire;

@Stateless
public class ReviewService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	private EntityManager em;

	public ReviewService() {
	}

	//We chose this type of method for retrieving the daily questionnaire
	//Might be a problem for midnight
	public List<Review> findReviewsByQuestionnaireRefresh(int questId) {
		Questionnaire questionnaire = em.find(Questionnaire.class, questId);
		em.refresh(questionnaire);
		List<Review> reviews = questionnaire.getReviews();
		return reviews;
	}

}