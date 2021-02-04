package it.gamified.db2.entities;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "review", schema = "db_gamifiedschema")
public class Review implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String text;
	
	// **POSTED BY RELATIONSHIP**
	// ----- FETCH TYPE -----
	// The fetch type is EAGER since we need navigation to show the user
	// ----- CASCADE --------
	// Default
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;
	
	// **POSTED TO RELATIONSHIP**
	// Implemented but not used
	
	@ManyToOne
	@JoinColumn(name = "quest_id")
	private Questionnaire questionnaire;

	public Review() {
		super();
	}
	
	public Review(String text, User user, Questionnaire quest) {
		super();
		this.text = text;
		setUser(user);
		setQuestionnaire(quest);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Questionnaire getQuestionnaire() {
		return questionnaire;
	}

	public void setQuestionnaire(Questionnaire questionnaire) {
		this.questionnaire = questionnaire;
	}

}