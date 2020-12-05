package it.gamified.db2.services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.gamified.db2.entities.MarketingQuestion;
import it.gamified.db2.entities.Questionnaire;

@Stateless
public class MarketingQuestionService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	private EntityManager em;

	public MarketingQuestionService() {
	}

	public List<MarketingQuestion> getMarketingQuestionsbyQuestionnaire(int questId){
		Questionnaire questionnaire = em.find(Questionnaire.class, questId);
		em.refresh(questionnaire);
		return questionnaire.getQuestions();
	}
	
	//TO-DO method to check num questions
}
