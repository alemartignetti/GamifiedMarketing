package it.gamified.db2.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="questionnaire", schema="db_gamifiedschema")
public class Questionnaire implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Temporal(TemporalType.DATE)
	private Date creation_date;
	
	@ManyToOne
	@JoinColumn(name="prod_id")
	private Product product;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "questionnaire", cascade = CascadeType.ALL)
	private List<MarketingQuestion> questions;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCreation_date() {
		return creation_date;
	}

	public void setCreation_date(Date creation_date) {
		this.creation_date = creation_date;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public List<MarketingQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<MarketingQuestion> questions) {
		this.questions = questions;
	}
}
