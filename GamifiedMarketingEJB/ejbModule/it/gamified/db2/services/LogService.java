package it.gamified.db2.services;

import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.gamified.db2.entities.Log;
import it.gamified.db2.entities.Questionnaire;
import it.gamified.db2.entities.User;

@Stateless
public class LogService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	private EntityManager em;

	//Register Log
	public void registerLog(User user, String type) {
		
		// No user merge since user has no mapping of Log relationship
		Log log = new Log(user, type);
		em.persist(log);
		
	}
	
	public List<Log> getCancelledLogs(Questionnaire quest){
		Date ref_date = quest.getRef_date();
		Calendar next_day = new GregorianCalendar();
		
		next_day.setTime(ref_date);
		next_day.set(Calendar.DAY_OF_YEAR, next_day.get(Calendar.DAY_OF_YEAR) + 1);
		
		next_day.set(Calendar.HOUR_OF_DAY, 0);
		next_day.set(Calendar.MINUTE, 0);
		next_day.set(Calendar.SECOND, 0);
		
		Date next_date = next_day.getTime();
		
		List<Log> logs = em.createNamedQuery("Log.cancelledLogs", Log.class)
				.setParameter("dateq", ref_date)
				.setParameter("dateq1", next_date)
				.getResultList();
		return logs;
		
	}
}