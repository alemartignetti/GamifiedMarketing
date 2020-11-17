package it.gamified.db2.entities;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "product", schema = "db_gamifiedschema")

public class Product implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private String name;

	@Basic(fetch = FetchType.LAZY)
	@Lob
	private byte[] image;
	
	public Product() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

}
