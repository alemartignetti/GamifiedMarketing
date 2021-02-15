package it.gamified.db2.services;

import java.util.List;
import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;

import it.gamified.db2.exceptions.*;
import it.gamified.db2.entities.Answer;
import it.gamified.db2.entities.Log;
import it.gamified.db2.entities.Questionnaire;
import it.gamified.db2.entities.Review;
import it.gamified.db2.entities.User;

@Stateless
public class LogService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	private EntityManager em;

	//Register Log
	public void registerLog(int user_id, String type) {
		
		User user = em.find(User.class, user_id);
		Log log = new Log(user, type);
		em.persist(log);
		
	}
	
	public List<Log> getCancelledLogs(int quest_id){
		
		Questionnaire quest = em.find(Questionnaire.class, quest_id);
		Date ref_date = quest.getRef_date();
		
		List<Log> logs = em.createNamedQuery("Log.cancelledLogs", Log.class).setParameter("dateq", ref_date).getResultList();
		return logs;
		
	}
}