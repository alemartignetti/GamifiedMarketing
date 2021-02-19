package it.gamified.db2.services;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import javax.persistence.NonUniqueResultException;

import it.gamified.db2.entities.Log;
import it.gamified.db2.entities.Questionnaire;
import it.gamified.db2.entities.User;
import it.gamified.db2.exceptions.*;

import java.util.Date;
import java.util.List;

@Stateless
public class UserServices {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	private EntityManager em;

	public UserServices() {
	}

	public User loginVerification(String username, String password) throws LoginException, NonUniqueResultException {
		List<User> userList = null;
		System.out.println("Called");
		try {
			userList = em.createNamedQuery("User.loginVerification", User.class).setParameter(1, username).setParameter(2, password)
					.getResultList();
		} catch (PersistenceException e) {
			throw new LoginException("Credentials could not be verified due to some internal error.");
		}
		if (userList.isEmpty())
			return null;
		else if (userList.size() == 1)
			return userList.get(0);
		throw new NonUniqueResultException("Integrity Error: more than one user corresponding to same email/username");

	}
	
	public User registerUser(String username, String email, String password) throws PersistenceException, UsernameDuplicate, EmailDuplicate, NonUniqueResultException, RegistrationException {
		
		// Check if email is present already in database
		List<String> emailList;
		try {
			emailList = em.createNamedQuery("User.checkEmail", String.class).setParameter(1, email)
					.getResultList();
		} catch (PersistenceException e) {
			throw new RegistrationException("Registration could not happen due to internal error.");
		}
		
		if (!emailList.isEmpty()) {
			throw new EmailDuplicate("Email already associated with an existing user");
		}
		else if (emailList.size() > 1) {
			throw new NonUniqueResultException("Integrity Error: more than one user corresponding to same email");
		}
		
		List<String> userList;
		try {
			userList = em.createNamedQuery("User.checkUsername", String.class).setParameter(1, username)
					.getResultList();
		} catch (PersistenceException e) {
			throw new RegistrationException("Registration could not happen due to internal error.");
		}
		
		if (!userList.isEmpty()) {
			throw new UsernameDuplicate("User already associated with an existing user");
		}
		else if (userList.size() > 1) {
			throw new NonUniqueResultException("Integrity Error: more than one user corresponding to same username");
		}
		
        User u = new User(username,email,password);
        em.persist(u);

        return u;
    }
	
	public void blockUser(User user) throws Exception {
		try {
			user.setBlocked(true);
			em.merge(user);
		} catch (PersistenceException e) {
			throw new Exception("Could not block user");
		}
	}
	
	public List<User> getLeaderboard(int quest_id){
		
		Questionnaire quest = em.find(Questionnaire.class, quest_id);
		// The method should get an ordered list of users based on a given questionnaire, if the answer exists.
		// Simply use a named query and get the ordered user by point where exist an answer
		List<User> users = em.createNamedQuery("User.getLeaderboard", User.class).setParameter("quest", quest).setHint("javax.persistence.cache.storeMode", "REFRESH").getResultList();
		
		return users;
		
	}
}
