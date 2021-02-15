package it.gamified.db2.entities;

import java.io.Serializable;
import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The persistent class for the usertable database table.
 * 
 */
@Entity
@Table(name = "usertable", schema = "db_gamifiedschema")
@NamedQueries({
	@NamedQuery(name = "User.loginVerification", query = "SELECT r FROM User r "
			+ "WHERE (r.username = ?1 and r.password = ?2) OR (r.email = ?1 and r.password = ?2)"),
	@NamedQuery(name = "User.checkUsername", query = "SELECT r.username FROM User r WHERE r.username = ?1"),
	@NamedQuery(name = "User.checkEmail", query = "SELECT r.email FROM User r WHERE r.email = ?1"),
	@NamedQuery(name = "User.getLeaderboard", query = "SELECT u FROM User u WHERE EXISTS ( SELECT a from u.answers a where a.questionnaire = :quest ) ORDER BY u.points DESC")
})
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String username;

	private String password;
	
	private String email;
	
	private int points;
	
	public enum Role {
	    USER,
	    ADMIN;
	}
	
	// **OWNER RELATIONSHIP**
	// ----- FETCH TYPE -----
	// The fetch type is LAZY since we do not want to use relationship during normal use
	// ----- CASCADE --------
	// REFRESH: it is used for checking if the user has answered to the questionnaire straight away
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.REFRESH)
	private List<Answer> answers;
	
	// **POSTED BY RELATIONSHIP**
	// Not implemented
	
	// **POSTED BY RELATIONSHIP**
	// Not implemented

	@Column(columnDefinition = "ENUM('USER', 'ADMIN')")
	@Enumerated(EnumType.STRING)
	private Role urole;
	 
	@Column(nullable = false, columnDefinition = "TINYINT", length = 1)
	private boolean blocked;

	public User() {
	}

	public User(String username, String email, String password) {

        this.username = username;

        this.email = email;

        this.password = password;

        this.urole = Role.USER;

        this.blocked = false;
        
        this.points = 0;

    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
	
	// Answer manages user addition
	public void addAnswer(Answer answer) {
		if(answers == null) answers = new ArrayList<Answer>();
		this.answers.add(answer);
	}

	public Role getUrole() {
		return urole;
	}

	public void setUrole(Role urole) {
		this.urole = urole;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

}