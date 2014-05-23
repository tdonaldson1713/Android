package com.donaldson.gradetracker.database.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Class implements Parcelable {
	private double mOverallGrade;
	private long mId;
	private long mUserId; // Will be needed for finding the classes for a specific user.
	private String mClassName;
	private String mSemester;
	private String mGradingCategory;
	private double mGPAScale;
	private double mCredits;
	
	public Class() {
		mId = -1;
		mUserId = -1;
		mOverallGrade = -1;
		mClassName = "";
		mSemester = "";
		mGradingCategory = "";
		mGPAScale = -1;
		mCredits = -1;
	}

	public Class(Class c) {
		mId = c.getId();
		mUserId = c.getUserId();
		mOverallGrade = c.getOverallGrade();
		mClassName = c.getClassName();
		mSemester = c.getSemester();
		mGradingCategory = c.getGradingCategory();
	}

	public Class(Parcel source) {
		mOverallGrade = source.readDouble();
		mId = source.readLong();
		mUserId = source.readLong();
		mClassName = source.readString();
		mSemester = source.readString();
		mGradingCategory = source.readString();
	}

	public Class(long id, long userId, String className, String semester, String gradingCategory, double overallGrade) {
		mId = id;
		mUserId = userId;
		mClassName = className;
		mSemester = semester;
		mGradingCategory = gradingCategory;
		mOverallGrade = overallGrade;
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

	public String getClassName() {
		return mClassName;
	}

	public void setClassName(String className) {
		mClassName = className;
	}

	public String getSemester() {
		return mSemester;
	}

	public void setSemester(String semester) {
		mSemester = semester;
	}

	public String getGradingCategory() {
		return mGradingCategory;
	}

	public void setGradingCategory(String gradingCategory) {
		mGradingCategory = gradingCategory;
	}

	public double getOverallGrade() {
		return mOverallGrade;
	}

	public void setOverallGrade(double overallGrade) {
		if (Double.isNaN(overallGrade)) {
			mOverallGrade = -1;
		} else {
			mOverallGrade = overallGrade;
		}
	}

	public double getGPAScale() {
		return mGPAScale;
	}

	public void setGPAScale(double gPAScale) {
		mGPAScale = gPAScale;
	}

	public double getCredits() {
		return mCredits;
	}

	public void setCredits(double credits) {
		mCredits = credits;
	}

	@Override
	public int describeContents() {
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(mOverallGrade);
		dest.writeLong(mId);
		dest.writeLong(mUserId);
		dest.writeString(mClassName);
		dest.writeString(mSemester);
		dest.writeString(mGradingCategory);
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Class createFromParcel(Parcel in) {
			return new Class(in);
		}

		public Class[] newArray(int size) {
			return new Class[size];
		}
	};
}
