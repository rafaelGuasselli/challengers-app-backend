package model;

public class ChallangeValidationRequest {
	private String id;
	private String description;
	private String picture;
	private int score;
	private User user;
	private String challangeId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getChallangeId() {
		return challangeId;
	}

	public void setChallangeId(String challangeId) {
		this.challangeId = challangeId;
	}
}
