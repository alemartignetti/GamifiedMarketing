package it.gamified.db2.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="logtable", schema="db_gamifiedschema")
public class Log implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
	
	@ManyToOne
	@JoinColumn(name = "user")
	private User user;
	
	public enum Type {
	    LOGIN,
	    CANCEL;
	}
	
	@Column(columnDefinition = "ENUM('LOGIN', 'CANCEL')")
	@Enumerated(EnumType.STRING)
	private Type type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
