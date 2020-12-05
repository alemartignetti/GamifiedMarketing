package it.gamified.db2.entities;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "marketingquestion", schema = "db_gamifiedschema")

@NamedQuery(name = "MarketingQuestion.questionByQuestionnaire", query = "SELECT m FROM MarketingQuestion m WHERE m.questionnaire = ?1")
public class MarketingQuestion implements Serializable {
	private static final long serialVersionUID = 1L;

	public MarketingQuestion() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private int num;

	private String text;
	
	@ManyToOne
	@JoinColumn(name="quest_id")
	private Questionnaire questionnaire;

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
}
