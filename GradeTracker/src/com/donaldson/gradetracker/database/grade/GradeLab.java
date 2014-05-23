package com.donaldson.gradetracker.database.grade;

import android.content.Context;

import com.donaldson.gradetracker.database.DatabaseHelper;
import com.donaldson.gradetracker.database.DatabaseHelper.GradeCursor;

public class GradeLab {
	private static GradeLab sGradeLab;
	private Context mAppContext;
	private DatabaseHelper mGradeHelper;
	
	private GradeLab(Context appContext) {
		mAppContext = appContext;
		mGradeHelper = new DatabaseHelper(mAppContext);
	}
	
	public static GradeLab get(Context c) {
		if (sGradeLab == null) {
			sGradeLab = new GradeLab(c.getApplicationContext());
		}
		return sGradeLab;
	}
	
	public long insertGrade(Grade newGrade) {
		return mGradeHelper.insertGrade(newGrade);
	}
	
	public GradeCursor deleteGrade(Grade newGrade) {
		return mGradeHelper.deleteGrade(newGrade);
	}
	
	public GradeCursor updateGrade(Grade newGrade) {
		return mGradeHelper.updateGrade(newGrade);
	}
	
	public Grade getGrade(long grade_id) {
		GradeCursor cursor = mGradeHelper.getGrade(grade_id);
		
		cursor.moveToFirst();
		if (cursor.isAfterLast() || cursor.isBeforeFirst()) {
			return null;
		}
		
		Grade newGrade = cursor.getGradeInfo();
		cursor.close();
		return newGrade;
	}
	public GradeCursor getGrades(long class_id, long user_id, boolean ph) {
		return mGradeHelper.getGrade(class_id, user_id, ph);
	}
	
	public GradeCursor getGrades(long class_id, String category) {
		return mGradeHelper.getGrade(class_id, category);
	}
	
	public GradeCursor getGrades(long class_id, long student_id) {
		return mGradeHelper.getGrade(class_id, student_id);
	}
	
	public GradeCursor getGrades(long class_id, long student_id, String category) {
		return mGradeHelper.getGrade(class_id, student_id, category);
	}
}
