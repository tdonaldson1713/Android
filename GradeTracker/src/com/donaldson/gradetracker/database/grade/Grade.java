package com.donaldson.gradetracker.database.grade;

public class Grade {
	private double mEarnedGrade;
	private double mPossibleGrade;
	private double mPercentage;
	private long mId; // Grade id
	private long mUserId;
	private long mClassId;
	private long mStudentId;
	private String mGradeCategory;

	public Grade() {
		this.mEarnedGrade = this.mPossibleGrade = this.mPercentage = -1;
		this.mId = this.mUserId = this.mClassId = this.mStudentId = -1;
		this.mGradeCategory = "";
	}

	public Grade(double earnedGrade, double possibleGrade, double percentage, long g_id, 
			long u_id, long c_id, long s_id, String gradeCategory) {
		this.mEarnedGrade = earnedGrade;
		this.mPossibleGrade = possibleGrade;
		this.mPercentage = percentage;
		this.mId = g_id;
		this.mUserId = u_id;
		this.mClassId = c_id;
		this.mStudentId = s_id;
		this.mGradeCategory = gradeCategory;
	}

	public double getEarnedGrade() {
		return mEarnedGrade;
	}

	public void setEarnedGrade(double earnedGrade) {
		if (Double.isNaN(earnedGrade)) {
			mEarnedGrade = 0;
		} else {
			mEarnedGrade = earnedGrade;
		}
	}

	public double getPossibleGrade() {
		return mPossibleGrade;
	}

	public void setPossibleGrade(double possibleGrade) {
		if (Double.isNaN(possibleGrade)) {
			mPossibleGrade = 0;
		}
		else {
			mPossibleGrade = possibleGrade;
		}
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public long getUserId() {
		return mUserId;
	}

	public void setUserId(long userId) {
		mUserId = userId;
	}

	public long getClassId() {
		return mClassId;
	}

	public void setClassId(long classId) {
		mClassId = classId;
	}

	public long getStudentId() {
		return mStudentId;
	}

	public void setStudentId(long studentId) {
		mStudentId = studentId;
	}

	public String getGradeCategory() {
		return mGradeCategory;
	}

	public void setGradeCategory(String gradeCategory) {
		mGradeCategory = gradeCategory;
	}

	public double getPercentage() {
		return mPercentage;
	}

	public void setPercentage(double percentage) {
		if (Double.isNaN(percentage)) {
			mPercentage = 0;
		} else {
			mPercentage = percentage;
		}
	}
}
