package it.gamified.db2.services;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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

	public Answer getAnswerByUserAndQuestionnaire(Questionnaire questionnaire, User user) throws AnswerDuplicate {
		List<Answer> answer = em
				.createNamedQuery("Answer.getAnswerbyQuestandUser", Answer.class)
				.setParameter("user", user).setParameter("quest", questionnaire).getResultList();

		if (answer.isEmpty()) {
			return null;
		} else if (answer.size() > 1) {
			throw new AnswerDuplicate("More than one answer in DB! Integrity constraint violation.");
		} else {
			return answer.get(0);
		}
	}
	
	public Answer getAnswerByUserAndQuestionnaire(int quest_id, int user_id) throws AnswerDuplicate {
		User user = em.find(User.class, user_id);
		Questionnaire quest = em.find(Questionnaire.class, quest_id);
		List<Answer> answer = em
				.createNamedQuery("Answer.getAnswerbyQuestandUser", Answer.class)
				.setParameter("user", user).setParameter("quest", quest).getResultList();

		if (answer.isEmpty()) {
			return null;
		} else if (answer.size() > 1) {
			throw new AnswerDuplicate("More than one answer in DB! Integrity constraint violation.");
		} else {
			return answer.get(0);
		}
	}

	public void submitAnswer(Questionnaire quest, User user, HashMap<Integer, String> answersQuestionnaire, OptionalQuest optionalAnswer) throws OffensiveWord, InvalidQuestionAnswer{
			System.out.println("Preparing submission!");
			Answer answer = new Answer();
			
			System.out.println("Preparing submission: created the element!");
			
			
			// Idea: this is inefficient if Number of unique words in database >> number of unique words in question
			// Therefore: merge all words in an unique set, do a query for each unique word, and only then submit answer!
			
			//List<String> queryResult = em.createNamedQuery("getAllOffensive", String.class).getResultList();
			
			List<MarketingQuestion> questions = quest.getQuestions();
			
			//Create a common set of all question words
			Set<String> answerVocabulary = new HashSet<String>();
	
			System.out.println("Preparing submission: entering for!");
			for(final Entry<Integer, String> e : answersQuestionnaire.entrySet()) {
				String answerText = e.getValue();
				
				//answerText is turned in lowerCase and with punctuation removed for checking the badWord
				String lowerCaseAnswer = answerText.replaceAll("[^a-zA-Z ]", "").toLowerCase();
				//split every word into the string
	
				// This is to improve efficiency of disjoint check, since we know no duplicate word is present
				// and if so it would be negligeble since useless.
				for(String word : lowerCaseAnswer.split(" ")) {
					answerVocabulary.add(word);
				}
			}
			
			// Query over the set
			for(final String word : answerVocabulary) {
				
				// We just do ONE SINGLE QUERY for each UNIQUE WORD in all the questions
				TypedQuery<String> isOffensive = em.createNamedQuery("OffensiveWord.isOffensive", String.class).setParameter("supposed", word);
				
				try {
					isOffensive.getSingleResult();
				} catch (NoResultException nre) {
					continue;
				}
				
				throw new OffensiveWord("Offensive word used!");		
			}
			
			// Create the answer
			for(final Entry<Integer, String> e : answersQuestionnaire.entrySet()) {
				Integer questionID = e.getKey();
				String answerText = e.getValue();
				
				// This additional check is used to verify if a question id, not related to an existing question
				// was added in the HTML.
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
			
				
				System.out.println("Question:" + priorQuestion.getText() + "\nAnswer:" + answerText);
				answer.setAnswerText(priorQuestion, answerText);
			}
			
			
			System.out.println("Alright!");
	
			answer.setOptionalAnswer(optionalAnswer);
			answer.setUser(user);
			answer.setQuestionnaire(quest);
			quest.addAnswer(answer);
			
			em.persist(answer);
			em.merge(quest);
			em.merge(user);
	
			System.out.println("Done!");
	}
}