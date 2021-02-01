//package it.gamified.db2.entities;
//
//import java.io.Serializable;
//import javax.persistence.*;
//
//import java.util.Base64;
//import java.util.List;
//
///**
// * The persistent class for the usertable database table.
// * 
// */
//@Entity
//@Table(name = "product", schema = "db_gamifiedschema")
//public class Product implements Serializable {
//	private static final long serialVersionUID = 1L;
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private int id;
//
//	private String name;
//	
//	@Basic(fetch = FetchType.LAZY)
//	@Lob
//	private byte[] image;
//	
//	@OneToOne(mappedBy = "product")
//	private Questionnaire questionnaire;
//
//	
//	public int getId() {
//		return id;
//	}
//
//
//	public void setId(int id) {
//		this.id = id;
//	}
//
//
//	public String getName() {
//		return name;
//	}
//
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//
//	public byte[] getImage() {
//		return image;
//	}
//	
//	public String getImageData() {
//		return Base64.getMimeEncoder().encodeToString(image);
//	}
//
//
//	public void setImage(byte[] image) {
//		this.image = image;
//	}
//
//
//	public Questionnaire getQuestionnaire() {
//		return questionnaire;
//	}
//
//
//	public void setQuestionnaire(Questionnaire questionnaire) {
//		this.questionnaire = questionnaire;
//	}
//
//
//	public Product() {
//	}
//
//	
//}