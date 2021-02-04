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

	public LogService() {
	}

	public List<Log> findLogs(Questionnaire quest) {
		Date ref_date = quest.getRef_date();
		
		List<Log> logs = em
				.createQuery("Select l from Log l " + "where l.timestamp = :date", Log.class)
				.setParameter("date", ref_date, TemporalType.DATE).getResultList();
		
		return logs;
	}

}