package it.gamified.db2.services;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.gamified.db2.exceptions.*;
import it.gamified.db2.entities.Answer;
import it.gamified.db2.entities.MarketingAnswer;
import it.gamified.db2.entities.MarketingQuestion;
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

	public AnswerService() {
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

	public void submitAnswer(int questId, int userId, HashMap<Integer, String> answersQuestionnaire, HashMap<String, String> optionalAnswer) throws OffensiveWord, InvalidQuestionAnswer{
		//Explicitly start the transaction

		Questionnaire quest = em.find(Questionnaire.class, questId);
		User user = em.find(User.class, userId);

		Answer answer = new Answer(user, quest);
		List<String> queryResult = em.createNamedQuery("getAllOffensive", String.class).getResultList();
		// This is to improve efficiency of disjoint check, since we know no duplicate offensiveword is present
		// and if so it would be negligeble since useless.
		Set<String> offensiveWords = new HashSet<String>(queryResult);
		List<MarketingQuestion> questions = em.createNamedQuery("MarketingQuestion.questionByQuestionnaire", MarketingQuestion.class).setParameter(1, quest).getResultList();

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

			MarketingAnswer markAns = new MarketingAnswer(answer, priorQuestion, answerText);
			answer.addAnswer(markAns);
		}
		
		System.out.println("Alright!");

		//Add optional questions
		if(optionalAnswer.containsKey("sex")) {
			String val = optionalAnswer.get("sex");
			if (val != null) {
				answer.setSex(Answer.Sex.valueOf(val));
			}
		}
		if(optionalAnswer.containsKey("expertise")) {
			String val = optionalAnswer.get("expertise");
			if (val != null) {
				answer.setExpertise(Answer.Expertise.valueOf(val));
			}
		}
		if(optionalAnswer.containsKey("age")) {
			String val = optionalAnswer.get("age");
			if (val != null) {
				try {
					Integer age = Integer.parseInt(val);
					answer.setAge(age);
				} catch (NumberFormatException e) {
					System.out.println("Bad formatted age, considered null.");
				}
			}
		}

		user.addAnswer(answer);
		em.persist(answer);

		System.out.println("Done!");
	}

	public List<Answer> findAnswers(Questionnaire quest) {
		List<Answer> answers = em.createQuery("Select a from Answer a " + "where a.questionnaire = :quest", Answer.class)
				.setParameter("quest", quest).getResultList();

		if (answers.isEmpty())
			System.out.println("EMPTY");
		
		return answers;
	}

}