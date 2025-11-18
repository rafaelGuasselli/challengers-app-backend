package model;

import java.util.ArrayList;

public final class User {
	private int amountOfBronzeMedals;
	private int amountOfSilverMedals;
	private int amountOfGoldMedals;
	private ArrayList<Challange> challanges;
	private	String email;
	private String id;
	private String name;
	private String profilePicture;

	public int getAmountOfBronzeMedals() {
		return amountOfBronzeMedals;
	}

	public void setAmountOfBronzeMedals(int amountOfBronzeMedals) {
		this.amountOfBronzeMedals = amountOfBronzeMedals;
	}

	public int getAmountOfSilverMedals() {
		return amountOfSilverMedals;
	}

	public void setAmountOfSilverMedals(int amountOfSilverMedals) {
		this.amountOfSilverMedals = amountOfSilverMedals;
	}

	public int getAmountOfGoldMedals() {
		return amountOfGoldMedals;
	}

	public void setAmountOfGoldMedals(int amountOfGoldMedals) {
		this.amountOfGoldMedals = amountOfGoldMedals;
	}

	public ArrayList<Challange> getChallanges() {
		return challanges;
	}

	public void setChallanges(ArrayList<Challange> challanges) {
		this.challanges = challanges;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
}
