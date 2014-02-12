package com.donaldson.gradetracker;


public class FutureGradePercentage {
	private String mCategory;
	private double mPercentage;
	private double mEarned;
	private double mPossible;
	
	public FutureGradePercentage() {
		mCategory = "";
		mEarned = mPossible = mPercentage = -1.0;
	}
	
	public FutureGradePercentage(String category, double earned, double possible, double percentage) {
		mCategory = category;
		mEarned = earned;
		mPossible = possible;
		mPercentage = percentage;
	}

	public String getCategory() {
		return mCategory;
	}

	public void setCategory(String category) {
		mCategory = category;
	}

	public double getEarned() {
		return mEarned;
	}

	public void setEarned(double earned) {
		mEarned = earned;
	}

	public double getPossible() {
		return mPossible;
	}

	public void setPossible(double possible) {
		mPossible = possible;
	}

	public double getPercentage() {
		return mPercentage;
	}

	public void setPercentage(double percentage) {
		mPercentage = percentage;
	}
}
