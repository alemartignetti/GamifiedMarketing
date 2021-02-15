package it.gamified.db2.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "marketingquestion", schema = "db_gamifiedschema")

public class MarketingQuestion implements Serializable {
	private static final long serialVersionUID = 1L;

	public MarketingQuestion() {
		super();
	}
	
	public MarketingQuestion(String text, int num) {
		setText(text);
		setNum(num);
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private int num;

	private String text;
	
	// **BELONGING RELATIONSHIP**
	// Not used but needed to map other direction
	
	@ManyToOne
	@JoinColumn(name="quest_id")
	private Questionnaire questionnaire;
	
	// **ANSWERING RELATIONSHIP**
	// Not used but needed to map other direction
	
	@ManyToMany
	@JoinTable(name = "answering", joinColumns = @JoinColumn(name = "mkquest_id"), inverseJoinColumns = @JoinColumn(name = "answer_id"))
	private List<Answer> answers;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Questionnaire getQuestionnaire() {
		return questionnaire;
	}

	public void setQuestionnaire(Questionnaire questionnaire) {
		this.questionnaire = questionnaire;
	}
   
	public int escapeMarkNum(String nameFromHtml) {
		String prefix = "markquestid_";
		if (nameFromHtml != null && nameFromHtml.startsWith(prefix)) {
			try {
				String intString = nameFromHtml.split(prefix)[1];
				return Integer.parseInt(intString);
			} catch(NumberFormatException e) {
				return -1;
			}
	    }
	    return -1;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
	
	//Managed by answer
	public void addAnswer(Answer answer) {
		this.answers.add(answer);
	}
	
	public void removeAnswer(Answer answer) {
		this.answers.remove(answer);
	}
}
