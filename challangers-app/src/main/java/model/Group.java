package model;

import java.util.ArrayList;
import java.util.Map;


public class Group {
	private String id;
	private String adminId;
	private String InviteCode;
	private boolean isPrivate;
	private String name;
	private String picture;
	
	private ArrayList<Challange> challanges;
	private ArrayList<GroupJoinSolicitation> joinSolicitations;
	private Map<User, Integer> participants;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getInviteCode() {
		return InviteCode;
	}

	public void setInviteCode(String inviteCode) {
		InviteCode = inviteCode;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
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

	public ArrayList<Challange> getChallanges() {
		return challanges;
	}

	public void setChallanges(ArrayList<Challange> challanges) {
		this.challanges = challanges;
	}

	public ArrayList<GroupJoinSolicitation> getJoinSolicitations() {
		return joinSolicitations;
	}

	public void setJoinSolicitations(ArrayList<GroupJoinSolicitation> joinSolicitations) {
		this.joinSolicitations = joinSolicitations;
	}

	public Map<User, Integer> getParticipants() {
		return participants;
	}

	public void setParticipants(Map<User, Integer> participants) {
		this.participants = participants;
	}
}
