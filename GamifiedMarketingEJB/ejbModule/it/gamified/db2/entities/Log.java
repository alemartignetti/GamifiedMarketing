package it.gamified.db2.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@NamedQueries({
	@NamedQuery(name = "Log.cancelledLogs", query = "SELECT l FROM Log l WHERE (l.type = it.gamified.db2.entities.Log.Type.CANCEL AND l.timestamp >= :dateq AND l.timestamp < :dateq1)"),
	@NamedQuery(name = "Log.removeCancelLogs", query = "DELETE FROM Log l WHERE (l.type = it.gamified.db2.entities.Log.Type.CANCEL AND l.timestamp >= :dateq AND l.timestamp < :dateq1)")
})

// and function('trunc', l.timestamp) = :dateq

@Entity
@Table(name="logtable", schema="db_gamifiedschema")
public class Log implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
	
	// **LOGGING RELATIONSHIP**
	// ----- FETCH TYPE -----
	// The fetch type is EAGER since we needE user details 
	// ----- CASCADE --------
	// Default
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;
	
	public enum Type {
	    LOGIN,
	    CANCEL;
	}
	
	@Column(columnDefinition = "ENUM('LOGIN', 'CANCEL')")
	@Enumerated(EnumType.STRING)
	private Type type;
	
	public Log() {}
	
	public Log(User u, String type) {
		
		setUser(u);
		
		Type t = null;
		if(type == "L") {
			t = Type.LOGIN;
		}
		else {
			t = Type.CANCEL;
		}
		setType(t);
		Date date = new Date();
		setTimestamp(date);
		
	}

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
