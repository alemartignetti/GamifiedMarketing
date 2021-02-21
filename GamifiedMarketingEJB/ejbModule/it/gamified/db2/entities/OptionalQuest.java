package it.gamified.db2.entities;

import javax.persistence.*;


/* Used for easy managed of optional answers */

@Embeddable
public class OptionalQuest {

	public OptionalQuest() {
		
	}
	
	public OptionalQuest(String age, String sex, String exp) {
		
		setSex(sex != null ? Sex.valueOf(sex) : null); // returns null if no valid
		try {
			Integer age_num = Integer.parseInt(age);
			setAge(age_num);
		} catch (NumberFormatException e) {
			System.out.println("Bad formatted age, considered null.");
		}
		setExpertise(exp != null ? Expertise.valueOf(exp) : null);
		
	}
	
	private Integer age;
	
	public enum Sex {
	    MALE,
	    FEMALE,
	    OTHER;
	}
	
	@Column(columnDefinition = "ENUM('MALE', 'FEMALE', 'OTHER')")
	@Enumerated(EnumType.STRING)
	private Sex sex;
	
	public enum Expertise {
	    LOW,
		HIGH,
		MEDIUM;
	}

	@Column(columnDefinition = "ENUM('LOW', 'HIGH', 'MEDIUM')")
	@Enumerated(EnumType.STRING)
	private Expertise expertise;
	
	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Expertise getExpertise() {
		return expertise;
	}

	public void setExpertise(Expertise expertise) {
		this.expertise = expertise;
	}

}