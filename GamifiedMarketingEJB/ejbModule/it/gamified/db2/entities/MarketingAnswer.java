package it.gamified.db2.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="marketinganswer", schema="db_gamifiedschema")
public class MarketingAnswer implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
	
	public enum Type {
	    LOGIN,
	    CANCEL;
	}
	
	@Column(columnDefinition = "ENUM('LOGIN', 'CANCEL')")
	@Enumerated(EnumType.STRING)
	private Type type;
}

