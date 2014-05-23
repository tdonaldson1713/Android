package com.donaldson.gradetracker.database.student;

public class Student {
	private double mOverallGrade;
	private long mId;
	private String mFirstName;
	private String mLastName;
	private long mUserId;
	private long mClassId;

	public Student() {
		mId = mClassId = mUserId = -1;
		mOverallGrade = -1;
		mFirstName = mLastName = "";
	}

	Student(long id, String firstName, String lastName, long studentId, long userId, double overallGrade) {
		mId = id;
		mFirstName = firstName;
		mLastName = lastName;
		mClassId = studentId;
		mUserId = userId;
		mOverallGrade = overallGrade;
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getFirstName() {
		return mFirstName;
	}

	public void setFirstName(String firstName) {
		mFirstName = firstName;
	}

	public String getLastName() {
		return mLastName;
	}

	public void setLastName(String lastName) {
		mLastName = lastName;
	}

	public long getClassId() {
		return mClassId;
	}

	public void setClassId(long studentId) {
		mClassId = studentId;
	}

	public long getUserId() {
		return mUserId;
	}

	public void setUserId(long userId) {
		mUserId = userId;
	}

	public double getOverallGrade() {
		return mOverallGrade;
	}

	public void setOverallGrade(double overallGrade) {
		if (Double.isNaN(overallGrade)) {
			overallGrade = 0.0; 
		} else {
			mOverallGrade = overallGrade;
		}
	}
}
