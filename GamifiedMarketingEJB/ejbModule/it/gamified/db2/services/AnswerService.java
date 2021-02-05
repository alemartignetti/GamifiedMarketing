package it.gamified.db2.services;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import it.gamified.db2.exceptions.*;
import it.gamified.db2.entities.Answer;
import it.gamified.db2.entities.MarketingQuestion;
import it.gamified.db2.entities.OptionalQuest;
import it.gamified.db2.entities.Questionnaire;
import it.gamified.db2.entities.User;

import java.util.HashMap;
import java.util.HashSet;

@Stateless
public class AnswerService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	private EntityManager em;
	@EJB(name = "it.gamified.db2.services/UserService")
	private UserServices uService;
	@EJB(name = "it.gamified.db2.services/QuestionnaireService")
	private QuestionnaireService qService;
	
	public AnswerService() {
	}
	
	public Answer getDailyAnswer(int userId) throws AnswerDuplicate, Exception {
		Questionnaire questionnaire;
		try {
			questionnaire = qService.findDailyQuestionnaire();
		} catch (NonUniqueDailyQuestionnaire | NoDailyQuestionnaire e) {
			e.printStackTrace();
			throw new Exception("Unexpected Error");
		}
		User user = em.find(User.class, userId);

		List<Answer> answer = em
				.createQuery("Select a from Answer a " + "where (a.questionnaire = :quest) and (a.user = :user)",
						Answer.class)
				.setParameter("user", user).setParameter("quest", questionnaire).getResultList();

		if (answer.isEmpty()) {
			return null;
		} else if (answer.size() > 1) {
			throw new AnswerDuplicate("More than one answer in DB! Integrity constraint violation.");
		} else {
			return answer.get(0);
		}
	}
	
	// NEEDED BECAUSE ANSWERS ARE NOT EAGERLY FETCHED
	public List<Answer> findAnswers(int quest_id) {
		Questionnaire q = em.find(Questionnaire.class, quest_id);
		return q.getAnswers();
	}

	public Answer getAnswerByUserAndQuestionnaire(int questId, int userId) throws AnswerDuplicate {
		Questionnaire questionnaire = em.find(Questionnaire.class, questId);
		User user = em.find(User.class, userId);

		List<Answer> answer = em
				.createQuery("Select a from Answer a " + "where (a.questionnaire = :quest) and (a.user = :user)",
						Answer.class)
				.setParameter("user", user).setParameter("quest", questionnaire).getResultList();

		if (answer.isEmpty()) {
			return null;
		} else if (answer.size() > 1) {
			throw new AnswerDuplicate("More than one answer in DB! Integrity constraint violation.");
		} else {
			return answer.get(0);
		}
	}

	public void submitAnswer(int questId, int userId, HashMap<Integer, String> answersQuestionnaire, OptionalQuest optionalAnswer) throws OffensiveWord, InvalidQuestionAnswer{
		//Explicitly start the transaction
			Questionnaire quest = em.find(Questionnaire.class, questId);
			User user = em.find(User.class, userId);
	
			Answer answer = new Answer(user, quest);
			List<String> queryResult = em.createNamedQuery("getAllOffensive", String.class).getResultList();
			
			// This is to improve efficiency of disjoint check, since we know no duplicate offensiveword is present
			// and if so it would be negligeble since useless.
			Set<String> offensiveWords = new HashSet<String>(queryResult);
			List<MarketingQuestion> questions = quest.getQuestions();
	
			for(final Entry<Integer, String> e : answersQuestionnaire.entrySet()) {
				Integer questionID = e.getKey();
				String answerText = e.getValue();
				//answerText is turned in lowerCase and with punctuation removed for checking the badWord
				String lowerCaseAnswer = answerText.replaceAll("[^a-zA-Z ]", "").toLowerCase();
				//split every word into the string
	
				List<String> wordList = new ArrayList<String>();
				for(String word : lowerCaseAnswer.split(" ")) {
					wordList.add(word);
				}
				// This is to improve efficiency of disjoint check, since we know no duplicate word is present
				// and if so it would be negligeble since useless.
				Set<String> words = new HashSet<String>(wordList);
	
				//Verify if intersection is not null
				if(!Collections.disjoint(offensiveWords, words)) {
					throw new OffensiveWord("Offensive word used!");
				}
				
				MarketingQuestion priorQuestion = null;
				for(MarketingQuestion q : questions) {
					if(questionID == q.getId()) {
						priorQuestion = q;
						break;
					}
				}
				
				if(priorQuestion == null) {
					throw new InvalidQuestionAnswer("Answer for non existent question.");
				}
				
				System.out.println(answerText);
				answer.setAnswerText(priorQuestion, answerText);
			}
			
			answer.setOptionalAnswer(optionalAnswer);
			
			System.out.println("Alright!");
	
			user.addAnswer(answer);
			em.persist(answer);
	
			System.out.println("Done!");
	}
/*	public void submitAnswer(int questId, int userId, HashMap<Integer, String> answersQuestionnaire, OptionalQuest optionalAnswer) throws OffensiveWord, InvalidQuestionAnswer{
		//Explicitly start the transaction
		try {
			tx.begin();
			Questionnaire quest = em.find(Questionnaire.class, questId);
			User user = em.find(User.class, userId);
	
			Answer answer = new Answer(user, quest);
			List<String> queryResult = em.createNamedQuery("getAllOffensive", String.class).getResultList();
			
			// This is to improve efficiency of disjoint check, since we know no duplicate offensiveword is present
			// and if so it would be negligeble since useless.
			Set<String> offensiveWords = new HashSet<String>(queryResult);
			List<MarketingQuestion> questions = quest.getQuestions();
	
			for(final Entry<Integer, String> e : answersQuestionnaire.entrySet()) {
				Integer questionID = e.getKey();
				String answerText = e.getValue();
				//answerText is turned in lowerCase and with punctuation removed for checking the badWord
				String lowerCaseAnswer = answerText.replaceAll("[^a-zA-Z ]", "").toLowerCase();
				//split every word into the string
	
				List<String> wordList = new ArrayList<String>();
				for(String word : lowerCaseAnswer.split(" ")) {
					wordList.add(word);
				}
				// This is to improve efficiency of disjoint check, since we know no duplicate word is present
				// and if so it would be negligeble since useless.
				Set<String> words = new HashSet<String>(wordList);
	
				//Verify if intersection is not null
				if(!Collections.disjoint(offensiveWords, words)) {
					throw new OffensiveWord("Offensive word used!");
				}
				
				MarketingQuestion priorQuestion = null;
				for(MarketingQuestion q : questions) {
					if(questionID == q.getId()) {
						priorQuestion = q;
						break;
					}
				}
				
				if(priorQuestion == null) {
					throw new InvalidQuestionAnswer("Answer for non existent question.");
				}
				
				answer.setAnswerText(priorQuestion, answerText);
			}
			
			answer.setOptionalAnswer(optionalAnswer);
			
			System.out.println("Alright!");
	
			user.addAnswer(answer);
			em.persist(answer);
			tx.commit();
	
			System.out.println("Done!");
		} catch(Exception e) {
			System.out.println("Excp");
			try {
				tx.rollback();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (SystemException e1) {
				e1.printStackTrace();
			}
		}
	}*/

}