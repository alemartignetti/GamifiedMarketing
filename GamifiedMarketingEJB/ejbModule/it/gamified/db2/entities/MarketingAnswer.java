package it.gamified.db2.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="marketinganswer", schema="db_gamifiedschema")
public class MarketingAnswer implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String text;
	
	@ManyToOne
	@JoinColumn(name="question")
	private MarketingQuestion question;
	
	@ManyToOne
	@JoinColumn(name="answer")
	private Answer answer;

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

	public MarketingQuestion getQuestion() {
		return question;
	}

	public void setQuestion(MarketingQuestion question) {
		this.question = question;
	}

	public Answer getAnswer() {
		return answer;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}
}

