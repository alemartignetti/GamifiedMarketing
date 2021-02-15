package it.gamified.db2.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;


@Entity
@Table(name="answer", schema="db_gamifiedschema")
public class Answer implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	// **OWNER RELATIONSHIP**
	// ----- FETCH TYPE -----
	// The fetch type is EAGER since we use it only in admin (the answer per se)
	// ----- CASCADE --------
	// Default
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	// **REFERRING TO RELATIONSHIP**
	// Not used but implemented
	
	@ManyToOne
	@JoinColumn(name = "quest_id")
	private Questionnaire questionnaire;
	
	// **ANSWERING RELATIONSHIP**
	// This type of relationship is implemented in terms of ELEMENTCOLLECTION since it can be viewed
	// as a relationship with attribute, which is the answer text to the specific question.
	// ----- FETCH TYPE -----
	// The fetch type is EAGER since we use it to show answer by means of navigation in ADMIN page
	// ----- CASCADE --------
	// Default, this is managed properly in Services
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "answering", joinColumns = @JoinColumn(name = "answer_id"))
	@MapKeyJoinColumn(name = "mkquest_id")
	@Column(name = "text")
	private Map<MarketingQuestion, String> answers = new HashMap<MarketingQuestion, String>();
	
	// Embedded element to simply the management
	@Embedded
    private OptionalQuest optionalAnswer;
	 
	@Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

	public Answer(User user, Questionnaire quest) {
		
		setUser(user);
		setQuestionnaire(quest);
		setTimestamp(new Date());
		
	}
	
	public Answer() {
		setTimestamp(new Date());
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		user.addAnswer(this);
	}

	public Questionnaire getQuestionnaire() {
		return questionnaire;
	}

	public void setQuestionnaire(Questionnaire questionnaire) {
		this.questionnaire = questionnaire;
	}


	public OptionalQuest getOptionalAnswer() {
		return optionalAnswer;
	}

	public void setOptionalAnswer(OptionalQuest optionalAnswer) {
		this.optionalAnswer = optionalAnswer;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Map<MarketingQuestion, String> getAnswers() {
		return answers;
	}

	public void setAnswers(Map<MarketingQuestion, String> answers) {
		this.answers = answers;
	}

	// Answering relationship
	public void setAnswerText(MarketingQuestion quest, String text) {
		answers.put(quest, text);
		//quest.addAnswer(this);
	}
	
	public void removeAnswerText(MarketingQuestion quest) {
		answers.remove(quest);
		quest.removeAnswer(this);
	}
}
