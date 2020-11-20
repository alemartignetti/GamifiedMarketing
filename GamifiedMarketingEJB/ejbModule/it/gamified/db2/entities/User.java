package it.gamified.db2.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

/**
 * The persistent class for the usertable database table.
 * 
 */
@Entity
@Table(name = "usertable", schema = "db_gamifiedschema")
@NamedQuery(name = "User.loginVerification", query = "SELECT r FROM User r "
		+ "WHERE (r.username = ?1 and r.password = ?2) OR (r.email = ?1 and r.password = ?2)")
@NamedQuery(name = "User.checkUsername", query = "SELECT r.username FROM User r WHERE r.username = ?1")
@NamedQuery(name = "User.checkEmail", query = "SELECT r.email FROM User r WHERE r.email = ?1")

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
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
	private List<Answer> answers;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
	private List<Review> reviews;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
	private List<Log> logs;

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

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public List<Log> getLogs() {
		return logs;
	}

	public void setLogs(List<Log> logs) {
		this.logs = logs;
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