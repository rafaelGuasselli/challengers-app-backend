package model;

import java.util.ArrayList;
import java.util.Date;

public class Challange {
	private ArrayList<ChallangeIteration> challangeIterations;
	private ArrayList<ChallangeValidationRequest> challangeValidationRequests;
	private String description;
	private Date startDate;
	private Date endDate;
	private String id;
	private int maxAmountOfIterations;
	private String name;
	private String picture;
	private int score;
	private String groupId;

	public ArrayList<ChallangeIteration> getChallangeIterations() {
		return challangeIterations;
	}

	public void setChallangeIterations(ArrayList<ChallangeIteration> challangeIterations) {
		this.challangeIterations = challangeIterations;
	}

	public ArrayList<ChallangeValidationRequest> getChallangeValidationRequests() {
		return challangeValidationRequests;
	}

	public void setChallangeValidationRequests(ArrayList<ChallangeValidationRequest> challangeValidationRequests) {
		this.challangeValidationRequests = challangeValidationRequests;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getMaxAmountOfIterations() {
		return maxAmountOfIterations;
	}

	public void setMaxAmountOfIterations(int maxAmountOfIterations) {
		this.maxAmountOfIterations = maxAmountOfIterations;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}
