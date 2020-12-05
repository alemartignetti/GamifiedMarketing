package it.gamified.db2.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;


@Entity
@Table(name="answer", schema="db_gamifiedschema")
public class Answer implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "quest_id")
	private Questionnaire questionnaire;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "answer", cascade = CascadeType.ALL)
	private List<MarketingAnswer> answers;
	
	private Integer age;
	
	public enum Sex {
	    MALE,
	    FEMALE,
	    OTHER;
	}
	
	@Column(columnDefinition = "ENUM('MALE', 'FEMALE', 'OTHER')")
	@Enumerated(EnumType.STRING)
	private Sex sex;
	
	public enum Expertise {
	    LOW,
		HIGH,
		MEDIUM;
	}

	@Column(columnDefinition = "ENUM('LOW', 'HIGH', 'MEDIUM')")
	@Enumerated(EnumType.STRING)
	private Expertise expertise;
	 
	@Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

	public Answer(User user, Questionnaire quest) {
		
		setUser(user);
		setQuestionnaire(quest);
		setTimestamp(new Date());
		
	}
	
	public Answer() {
		
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
	}

	public Questionnaire getQuestionnaire() {
		return questionnaire;
	}

	public void setQuestionnaire(Questionnaire questionnaire) {
		this.questionnaire = questionnaire;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Expertise getExpertise() {
		return expertise;
	}

	public void setExpertise(Expertise expertise) {
		this.expertise = expertise;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public List<MarketingAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<MarketingAnswer> answers) {
		this.answers = answers;
	}
	
	public void addAnswer(MarketingAnswer answer) {
		if(this.answers == null || this.answers.isEmpty()) {
			this.answers = new ArrayList<MarketingAnswer>();
		}
		this.getAnswers().add(answer);
	}
}
