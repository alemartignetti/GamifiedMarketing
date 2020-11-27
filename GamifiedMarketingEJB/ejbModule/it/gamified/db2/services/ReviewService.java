package it.gamified.db2.services;


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
	
}