package it.gamified.db2.entities;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "usertable", schema = "db_gamifiedschema")
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
	private int quest_id;

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

	public int getQuest_id() {
		return quest_id;
	}

	public void setQuest_id(int quest_id) {
		this.quest_id = quest_id;
	}
   
}
