package it.gamified.db2.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@NamedQueries({
	@NamedQuery(
		    name="getAllOffensive",
		    query="SELECT o.word FROM OffensiveWord o")
})

@Entity
@Table(name="offensiveword", schema="db_gamifiedschema")
public class OffensiveWord implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	private String word;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}
	
}

