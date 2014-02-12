package com.donaldson.gradetracker;


public class UserAccount {
	long mId;
	String mUsername;
	String mPassword;
	String mEmail;
	String mStatus;
	String mPhoneNumber;
	
	public UserAccount() {
		mId = -1;
		mUsername = "";
		mPassword = "";
		mEmail = "";
		mStatus = "";
		mPhoneNumber = "";
	}
	
	public UserAccount(String username, String password, String email, String status, String phoneNumber) {
		mId = -1;
		mUsername = username;
		mPassword = password;
		mEmail = email;
		mStatus = status;
		mPhoneNumber = phoneNumber;
	}
	
	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getUsername() {
		return mUsername;
	}
	
	public void setUsername(String username) {
		mUsername = username;
	}
	
	public String getPassword() {
		return mPassword;
	}
	
	public void setPassword(String password) {
		mPassword = password;
	}
	
	public String getEmail() {
		return mEmail;
	}
	
	public void setEmail(String email) {
		mEmail = email;
	}

	public String getStatus() {
		return mStatus;
	}

	public void setStatus(String status) {
		mStatus = status;
	}

	public String getPhoneNumber() {
		return mPhoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		mPhoneNumber = phoneNumber;
	}
}
