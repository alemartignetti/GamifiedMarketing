package it.gamified.db2.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="questionnaire", schema="db_gamifiedschema")
@NamedQueries({
	@NamedQuery(name = "Questionnaire.getQuestionnaire", query = "Select q from Questionnaire q where q.ref_date = :date")
})
public class Questionnaire implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Temporal(TemporalType.DATE)
	private Date ref_date;
	
	private String product_name;
	
	@Basic(fetch = FetchType.LAZY)
	@Lob
	private byte[] image;
	
	// **BELONGING RELATIONSHIP**
	// ----- FETCH TYPE -----
	// The fetch type is EAGER since we need navigation to show questions
	// ----- CASCADE --------
	// REMOVE: We need to remove each questions when needed
	// ORPHAN REMOVAL: Set to true since questions not linked to a given questionnaire are deleted
	// PERSIST: When questionnaire is created, AT LEAST one question is inserted
	// ----- ORDERING --------
	// Ordered by NUM
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "questionnaire", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
	@OrderBy("num ASC")
	private List<MarketingQuestion> questions;

	// **POSTED TO RELATIONSHIP**
	// ----- FETCH TYPE -----
	// The fetch type is EAGER since we need navigation to show the reviews on HomePage
	// ----- CASCADE --------
	// REMOVE: We need to remove each review when the questionnaire is deleted
	// ORPHAN REMOVAL: Set to true since reviews not linked to a given questionnaire are deleted
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "questionnaire", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
	private List<Review> reviews;
	
	// **REFERRING TO RELATIONSHIP**
	// ----- FETCH TYPE -----
	// The fetch type is DEFAULT since we do not want to bring all the answers in questionnaire during normal navigation
	// ----- CASCADE --------
	// REMOVE: We need to remove each answer when questionnaire is needed
	// ORPHAN REMOVAL: Set to true since answer not linked to a given questionnaire are deleted
	
	@OneToMany(mappedBy = "questionnaire", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Answer> answers;
	
	public Questionnaire() {
		
	}
	
	public Questionnaire(Date ref_date, String name, byte[] image) {
		
		setProduct_name(name);
		setRef_date(ref_date);
		setImage(image);
		
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getRef_date() {
		return ref_date;
	}

	public void setRef_date(Date ref_date) {
		this.ref_date = ref_date;
	}
	

	public List<MarketingQuestion> getQuestions() {
		if(questions == null) {
			questions = new ArrayList<MarketingQuestion>();
		}
		return questions;
	}

	public void setQuestions(List<MarketingQuestion> questions) {
		this.questions = questions;
	}

	public List<Review> getReviews() {
		if(reviews == null) {
			reviews = new ArrayList<Review>();
		}
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	
	public void addReview(Review review) {
		this.reviews.add(review);
		review.setQuestionnaire(this);
	}
	
	public void addQuestion(MarketingQuestion question) {
		getQuestions().add(question);
		question.setQuestionnaire(this);
	}
	
	public void addAnswer(Answer answer) {
		this.answers.add(answer);
		answer.setQuestionnaire(this);
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public byte[] getImage() {
		return image;
	}
	
	public String getImageData() {
		if(image != null) {
			return Base64.getMimeEncoder().encodeToString(image);
		}
		else return null;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
	
	
}
