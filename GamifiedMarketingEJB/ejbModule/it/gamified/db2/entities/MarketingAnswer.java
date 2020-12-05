package it.gamified.db2.entities;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "marketinganswer", schema="db_gamifiedschema")
public class MarketingAnswer implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String text;
	
	@ManyToOne
	@JoinColumn(name="markquest_id")
	private MarketingQuestion question;
	
	@ManyToOne
	@JoinColumn(name="answer_id")
	private Answer answer;

	public MarketingAnswer(Answer answer, MarketingQuestion question, String text) {
		setAnswer(answer);
		setQuestion(question);
		setText(text);
	}
	
	public MarketingAnswer() {
		
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

