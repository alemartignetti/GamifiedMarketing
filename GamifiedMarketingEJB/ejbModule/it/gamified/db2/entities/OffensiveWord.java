package it.gamified.db2.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@NamedQuery(
	    name="getAllOffensive",
	    query="SELECT o.word FROM OffensiveWord o")

@Entity
@Table(name="offensiveword", schema="db_gamifiedschema")
public class OffensiveWord implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String word;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}
	
}

