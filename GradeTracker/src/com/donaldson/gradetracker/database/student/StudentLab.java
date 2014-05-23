package com.donaldson.gradetracker.database.student;

import android.content.Context;

import com.donaldson.gradetracker.database.DatabaseHelper;
import com.donaldson.gradetracker.database.DatabaseHelper.StudentCursor;

public class StudentLab {
	private static StudentLab sStudentLab;
	private Context mAppContext;
	private DatabaseHelper mStudentHelper;
	
	private StudentLab(Context appContext) {
		mAppContext = appContext;
		mStudentHelper = new DatabaseHelper(mAppContext);
	}
	
	public static StudentLab get(Context c) {
		if (sStudentLab == null) {
			sStudentLab = new StudentLab(c.getApplicationContext());
		}
		return sStudentLab;
	}
	
	public long insertStudent(Student newStudent) {
		return mStudentHelper.insertStudent(newStudent);
	}
	
	public StudentCursor deleteStudent(Student newStudent) {
		return mStudentHelper.deleteStudent(newStudent);
	}
	
	public StudentCursor updateStudent(Student newStudent) {
		return mStudentHelper.updateStudent(newStudent);
	}
	
	// Get a specific class. Called when the user is expanding an item
	public Student selectStudent(long id, String first_name, String last_name) {
		Student s = new Student();
		StudentCursor cursor = mStudentHelper.getStudent(id, first_name, last_name);
		cursor.moveToFirst();
		
		if (!cursor.isAfterLast()) {
			s = cursor.getStudentInfo();
		}
		
		cursor.close();
		return s;
	}
	
	public Student selectStudent(long id) {
		Student s = new Student();
		StudentCursor cursor = mStudentHelper.getStudent(id);
		cursor.moveToFirst();
		
		if (!cursor.isAfterLast()) {
			s = cursor.getStudentInfo();
		}
		
		cursor.close();
		return s;
	}
	
	// This function gets all the students for a specific user.
	public StudentCursor getStudents(long user_id) {
		return mStudentHelper.getStudents(user_id);
	}
	
	public StudentCursor getStudentsInClass(long class_id) {
		return mStudentHelper.getStudentsInClass(class_id);
	}
}
