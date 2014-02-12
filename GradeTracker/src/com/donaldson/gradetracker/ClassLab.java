package com.donaldson.gradetracker;

import android.content.Context;

import com.donaldson.gradetracker.DatabaseHelper.ClassCursor;

public class ClassLab {
	private static ClassLab sClassLab;
	private Context mAppContext;
	private DatabaseHelper mClassHelper;

	private ClassLab(Context appContext) {
		mAppContext = appContext;
		mClassHelper = new DatabaseHelper(mAppContext);
	}
	
	public static ClassLab get(Context c) {
		if (sClassLab == null) {
			sClassLab = new ClassLab(c.getApplicationContext());
		}
		return sClassLab;
	}
	
	public long insertClass(Class newClass) {
		return mClassHelper.insertClass(newClass);
	}
	
	public ClassCursor deleteClass(Class newClass) {
		return mClassHelper.deleteClass(newClass);
	}
	
	public ClassCursor updateClass(Class newClass) {
		return mClassHelper.updateClass(newClass);
	}
	
	// Get a specific class. Called when the user is expanding an item
	public Class selectClass(long id, String class_name) {
		Class c = new Class();
		ClassCursor cursor = mClassHelper.getClass(id, class_name);
		cursor.moveToFirst();
		
		if (!cursor.isAfterLast()) {
			c = cursor.getClassInfo();
		}
		
		cursor.close();
		return c;
	}
	
	public Class selectClass(long id) {
		Class c = new Class();
		ClassCursor cursor = mClassHelper.getClass(id);
		cursor.moveToFirst();
		
		if (!cursor.isAfterLast()) {
			c = cursor.getClassInfo();
		}
		
		cursor.close();
		return c;
	}
	// This function gets all the classes for a specific user.
	public ClassCursor getClasses(long user_id) {
		return mClassHelper.getClasses(user_id);
	}
}
