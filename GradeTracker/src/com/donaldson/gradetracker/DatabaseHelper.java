package com.donaldson.gradetracker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static final String TAG = "UserDatabaseHelper";
	public static final String DB_NAME = "gradetracker.db";
	public static final int VERSION = 1;
	private static final int ENCRYPTION = 5;
	private static final String COLUMN_ID = "_id";

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_USER);
		db.execSQL(CREATE_TABLE_CLASS);
		db.execSQL(CREATE_TABLE_STUDENT);
		db.execSQL(CREATE_TABLE_GRADE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {}

	//*************************************************************************************************
	//*************************************************************************************************
	// Table User Definitions
	private static final String TABLE_USER = "user";
	private static final String COLUMN_USERNAME = "username";
	private static final String COLUMN_PASSWORD = "password";
	private static final String COLUMN_EMAIL = "email";
	private static final String COLUMN_STATUS = "status";
	private static final String COLUMN_PHONE_NUMBER = "phone_number";
	private static final String CREATE_TABLE_USER = "create table " + TABLE_USER + "(" +
			COLUMN_ID + " integer primary key autoincrement, " + 
			COLUMN_USERNAME + " varchar(100), " +
			COLUMN_PASSWORD + " varchar(100), " + 
			COLUMN_EMAIL + " varchar(100), " +
			COLUMN_STATUS + " varchar(100), " +
			COLUMN_PHONE_NUMBER + " varchar(100));";
	private static final String UPDATE_USER = "UPDATE " + TABLE_USER + // Status can't be updated.
			" SET " + 
			COLUMN_USERNAME + " = ?, " +
			COLUMN_PASSWORD + " = ?, " +
			COLUMN_EMAIL + " = ? " +
			"WHERE _id = ?";
	private static final String DELETE_USER = "DELETE FROM " + TABLE_USER + // Username and password must be verified to delete
			" WHERE _id = ? AND " +
			COLUMN_USERNAME + " = ? AND " +
			COLUMN_PASSWORD + " = ?";

	public static class UserCursor extends CursorWrapper {
		public UserCursor(Cursor c) {
			super(c);
		}

		public UserAccount getUserInfo() {
			if (isBeforeFirst() || isAfterLast()) {
				return null;
			}

			UserAccount user = new UserAccount();
			
			user.setId(getLong(getColumnIndex(COLUMN_ID)));			
			user.setEmail(getString(getColumnIndex(COLUMN_EMAIL)));
			user.setStatus(getString(getColumnIndex(COLUMN_STATUS)));
			user.setPhoneNumber(getString(getColumnIndex(COLUMN_PHONE_NUMBER)));

			String username = getString(getColumnIndex(COLUMN_USERNAME));
			String password = getString(getColumnIndex(COLUMN_PASSWORD));
			
			for (int a = 0; a < ENCRYPTION; a++) {
				username = AffineCipher.Decrypt(username);
				password = AffineCipher.Decrypt(password);
			}
			
			user.setUsername(username);
			user.setPassword(password);
			
			return user;		
		}
	}

	@SuppressLint("DefaultLocale")
	public long insertUser(UserAccount user) {
		ContentValues cv = new ContentValues();

		String username = user.getUsername();
		String password = user.getPassword();
		
		for (int a = 0; a < ENCRYPTION; a++) {
			username = AffineCipher.Encrypt(username);
			password = AffineCipher.Encrypt(password);
		}
		
		cv.put(COLUMN_USERNAME, username);
		cv.put(COLUMN_PASSWORD, password);
		cv.put(COLUMN_EMAIL, user.getEmail());
		cv.put(COLUMN_STATUS, user.getStatus().toLowerCase());
		cv.put(COLUMN_PHONE_NUMBER, user.getPhoneNumber());

		return getWritableDatabase().insert(TABLE_USER, null, cv);
	}

	public UserCursor deleteUser(UserAccount user) {
		String[] userInfo = {String.valueOf(user.getId()), user.getUsername(), user.getPassword()};
		Cursor cursor = getWritableDatabase().rawQuery(DELETE_USER, userInfo);
		return new UserCursor(cursor);
	}

	public UserCursor updateUser(UserAccount user) {
		for (int a = 0; a < ENCRYPTION; a++) {
			user.setUsername(AffineCipher.Encrypt(user.getUsername()));
			user.setPassword(AffineCipher.Encrypt(user.getPassword()));
		}
		
		String[] userInfo = {user.getUsername(), user.getPassword(), 
				user.getEmail(), String.valueOf(user.getId())};
		Cursor cursor = getWritableDatabase().rawQuery(UPDATE_USER, userInfo);
		return new UserCursor(cursor);
	}


	// Change this over to the other way.
	public UserCursor getUser(String username, String password) {
		for (int a = 0; a < ENCRYPTION; a++) {
			username = AffineCipher.Encrypt(username);
			password = AffineCipher.Encrypt(password);
		}
		
		Cursor cursor = getReadableDatabase().rawQuery("Select * FROM " + TABLE_USER +
				" WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
				new String[]{username, password});
		return new UserCursor(cursor);
	}

	// Change this over to the other way.
	public UserCursor getUserLoginFail(String username, String password) {
		for (int a = 0; a < ENCRYPTION; a++) {
			username = AffineCipher.Encrypt(username);
			password = AffineCipher.Encrypt(password);
		}
		
		Cursor cursor = getReadableDatabase().rawQuery("Select * FROM " + TABLE_USER +
				" WHERE " + COLUMN_USERNAME + "=? OR " + COLUMN_PASSWORD + "=?",
				new String[]{username, password});
		return new UserCursor(cursor);
	}

	public UserCursor getUser(long id) {
		Cursor cursor = getReadableDatabase().rawQuery("Select * FROM " + TABLE_USER +
				" WHERE _id = ?", new String[]{String.valueOf(id)});
		return new UserCursor(cursor);
	}

	// We're getting all the users in the database
	public UserCursor getUser() {
		Cursor cursor = getReadableDatabase().rawQuery("Select * FROM " + TABLE_USER, null);
		return new UserCursor(cursor);

	}
	//*************************************************************************************************
	//*************************************************************************************************


	//*************************************************************************************************
	//*************************************************************************************************
	// Table Class Definitions
	private static final String TABLE_CLASS = "class";
	private static final String COLUMN_CLASS_NAME = "class_name";
	private static final String COLUMN_SEMESTER = "semester";
	private static final String COLUMN_GRADING_CATEGORY = "grade_category";
	private static final String COLUMN_USER_ID = "user_id";
	private static final String COLUMN_OVERALL_GRADE = "overall_grade";
	private static final String CREATE_TABLE_CLASS = "create table " + TABLE_CLASS + "(" +
			COLUMN_ID + " integer primary key autoincrement, " + 
			COLUMN_CLASS_NAME +" varchar(100), " +
			COLUMN_SEMESTER + " varchar(100), " + 
			COLUMN_USER_ID + " integer, " +
			COLUMN_OVERALL_GRADE + " real, " +
			COLUMN_GRADING_CATEGORY + " varchar(100));";
	private static final String UPDATE_CLASS = "UPDATE " + TABLE_CLASS +
			" SET " + 
			COLUMN_CLASS_NAME + " = ?, " +
			COLUMN_SEMESTER + " = ?, " +
			COLUMN_OVERALL_GRADE + " = ? " +
			"WHERE _id = ?";
	private static final String DELETE_CLASS = "DELETE FROM " + TABLE_CLASS +
			" WHERE _id = ?";

	public static class ClassCursor extends CursorWrapper {
		public ClassCursor(Cursor c) {
			super(c);
		}

		public Class getClassInfo() {
			if (isBeforeFirst() || isAfterLast()) {
				return null;
			}

			Class newClass = new Class();

			newClass.setId(getLong(getColumnIndex(COLUMN_ID)));
			newClass.setClassName(getString(getColumnIndex(COLUMN_CLASS_NAME)));
			newClass.setSemester(getString(getColumnIndex(COLUMN_SEMESTER)));
			newClass.setUserId(getLong(getColumnIndex(COLUMN_USER_ID)));
			newClass.setGradingCategory(getString(getColumnIndex(COLUMN_GRADING_CATEGORY)));
			newClass.setOverallGrade(getDouble(getColumnIndex(COLUMN_OVERALL_GRADE)));

			return newClass;		
		}
	}

	public long insertClass(Class newClass) {
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_CLASS_NAME, newClass.getClassName());
		cv.put(COLUMN_SEMESTER, newClass.getSemester());
		cv.put(COLUMN_USER_ID, newClass.getUserId());
		cv.put(COLUMN_GRADING_CATEGORY, newClass.getGradingCategory());
		
		if (Double.isNaN(newClass.getOverallGrade())) {
			cv.put(COLUMN_OVERALL_GRADE, 0.0);
		} else {
			cv.put(COLUMN_OVERALL_GRADE, newClass.getOverallGrade());
		}
		
		return getWritableDatabase().insert(TABLE_CLASS, null, cv);
	}

	public ClassCursor deleteClass(Class newClass) {
		String[] classInfo = {String.valueOf(newClass.getId())};
		Cursor cursor = getWritableDatabase().rawQuery(DELETE_CLASS, classInfo);
		return new ClassCursor(cursor);
	}

	public ClassCursor updateClass(Class newClass) {
		String[] classInfo = {newClass.getClassName(), newClass.getSemester(), 
				String.valueOf(newClass.getOverallGrade()),	String.valueOf(newClass.getId())};
		Cursor cursor = getWritableDatabase().rawQuery(UPDATE_CLASS, classInfo);
		return new ClassCursor(cursor);
	}

	// This function gets one specific class. This function will be used when the user 
	// is expanding a class.
	public ClassCursor getClass(long class_id, String class_name) {
		Cursor cursor = getReadableDatabase().rawQuery("Select * FROM " + TABLE_CLASS +
				" WHERE _id " + "= ? AND " + COLUMN_CLASS_NAME + "= ?",
				new String[]{String.valueOf(class_id), class_name});
		return new ClassCursor(cursor);
	}

	public ClassCursor getClass(long class_id) {
		Cursor cursor = getReadableDatabase().rawQuery("Select * FROM " + TABLE_CLASS +
				" WHERE _id " + "= ?", new String[]{String.valueOf(class_id)});
		return new ClassCursor(cursor);
	}
	
	// This function is used to get all the classes for the specific user.
	public ClassCursor getClasses(long user_id) {
		Cursor cursor = getReadableDatabase().rawQuery("Select * FROM " + TABLE_CLASS +
				" WHERE " + COLUMN_USER_ID + "= ?",
				new String[]{String.valueOf(user_id)});
		return new ClassCursor(cursor);
	}
	//*************************************************************************************************
	//*************************************************************************************************


	//*************************************************************************************************
	//*************************************************************************************************
	// Table Student Definitions
	private static final String TABLE_STUDENT = "student";
	private static final String COLUMN_STUDENT_FIRST = "student_first_name";
	private static final String COLUMN_STUDENT_LAST = "student_last_name";
	private static final String COLUMN_CLASS_ID = "class_id"; // Table ID from Table Class
	private static final String CREATE_TABLE_STUDENT = "create table " + TABLE_STUDENT + "(" +
			COLUMN_ID + " integer primary key autoincrement, " + 
			COLUMN_STUDENT_FIRST +" varchar(100), " +
			COLUMN_STUDENT_LAST + " varchar(100), " + 
			COLUMN_CLASS_ID + " integer, " +
			COLUMN_OVERALL_GRADE + " real, " +
			COLUMN_USER_ID + " integer);";
	private static final String UPDATE_STUDENT = "UPDATE " + TABLE_STUDENT +
			" SET " + 
			COLUMN_STUDENT_FIRST + " = ?, " +
			COLUMN_STUDENT_LAST + " = ?, " +
			COLUMN_OVERALL_GRADE + " = ? " +
			"WHERE _id = ?";
	private static final String DELETE_STUDENT = "DELETE FROM " + TABLE_STUDENT +
			" WHERE _id = ?";

	public static class StudentCursor extends CursorWrapper {
		public StudentCursor(Cursor c) {
			super(c);
		}

		public Student getStudentInfo() {
			if (isBeforeFirst() || isAfterLast()) {
				return null;
			}

			Student newStudent = new Student();

			newStudent.setId(getLong(getColumnIndex(COLUMN_ID)));
			newStudent.setFirstName(getString(getColumnIndex(COLUMN_STUDENT_FIRST)));
			newStudent.setLastName(getString(getColumnIndex(COLUMN_STUDENT_LAST)));
			newStudent.setClassId(getLong(getColumnIndex(COLUMN_CLASS_ID)));
			newStudent.setUserId(getLong(getColumnIndex(COLUMN_USER_ID)));
			newStudent.setOverallGrade(getDouble(getColumnIndex(COLUMN_OVERALL_GRADE)));

			return newStudent;		
		}
	}

	public long insertStudent(Student newStudent) {
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_STUDENT_FIRST, newStudent.getFirstName());
		cv.put(COLUMN_STUDENT_LAST, newStudent.getLastName());
		cv.put(COLUMN_CLASS_ID, newStudent.getClassId());		
		cv.put(COLUMN_USER_ID, newStudent.getUserId());
		if (Double.isNaN(newStudent.getOverallGrade())) {
			cv.put(COLUMN_OVERALL_GRADE, 0.0);
		} else {
			cv.put(COLUMN_OVERALL_GRADE, newStudent.getOverallGrade());
		}
		
		return getWritableDatabase().insert(TABLE_STUDENT, null, cv);
	}

	public StudentCursor deleteStudent(Student newStudent) {
		String[] studentInfo = {String.valueOf(newStudent.getId())};
		Cursor cursor = getWritableDatabase().rawQuery(DELETE_STUDENT, studentInfo);
		return new StudentCursor(cursor);
	}

	public StudentCursor updateStudent(Student newStudent) {
		String[] studentInfo = {newStudent.getFirstName(), newStudent.getLastName(), 
				String.valueOf(newStudent.getOverallGrade()), String.valueOf(newStudent.getId())};
		Cursor cursor = getWritableDatabase().rawQuery(UPDATE_STUDENT, studentInfo);
		return new StudentCursor(cursor);
	}

	// This function gets a specific student. 
	public StudentCursor getStudent(long id, String first_name, String last_name) {
		Cursor cursor = getReadableDatabase().rawQuery("Select * FROM " + TABLE_STUDENT +
				" WHERE _id " + "= ? AND " + COLUMN_STUDENT_FIRST + "= ? AND " + COLUMN_STUDENT_LAST + " = ?",
				new String[]{String.valueOf(id), first_name, last_name});
		return new StudentCursor(cursor);
	}
	
	// This function gets a student just by the id.
	public StudentCursor getStudent(long id) {
		Cursor cursor = getReadableDatabase().rawQuery("Select * FROM " + TABLE_STUDENT + 
				" WHERE _id " + "= ?", new String[]{String.valueOf(id)});
		return new StudentCursor(cursor);
	}

	// This function is used to get all the students for all classes.
	public StudentCursor getStudents(long user_id) {
		Cursor cursor = getReadableDatabase().rawQuery("Select * FROM " + TABLE_STUDENT +
				" WHERE " + COLUMN_USER_ID + "= ?",
				new String[]{String.valueOf(user_id)});
		return new StudentCursor(cursor);
	}

	// This function is used to get all the students for a specific class
	public StudentCursor getStudentsInClass(long class_id) {
		Cursor cursor = getReadableDatabase().rawQuery("Select * FROM " + TABLE_STUDENT + 
				" WHERE " + COLUMN_CLASS_ID + "= ?", new String[]{String.valueOf(class_id)});
		return new StudentCursor(cursor);
	}

	//*************************************************************************************************
	//*************************************************************************************************
	// Table Grade Definitions
	private static final String TABLE_GRADE = "grade";
	private static final String COLUMN_GRADE_CATEGORY = "g_category";
	private static final String COLUMN_GRADE_EARNED = "g_earned";
	private static final String COLUMN_POSSILBE_GRADE = "g_possible";
	private static final String COLUMN_PERCENTAGE = "g_percentage";
	private static final String COLUMN_STUDENT_ID = "student_id"; // id from Table Student
	private static final String CREATE_TABLE_GRADE = "create table " + TABLE_GRADE + "(" +
			"_id" + " integer primary key autoincrement, " + 
			COLUMN_GRADE_EARNED +" varchar(100), " +
			COLUMN_POSSILBE_GRADE + " varchar(100), " +
			COLUMN_PERCENTAGE + " real, " +
			COLUMN_GRADE_CATEGORY + " varchar(100), " +
			COLUMN_STUDENT_ID + " integer, " +
			COLUMN_CLASS_ID + " integer, " +
			COLUMN_USER_ID + " integer);";
	private static final String UPDATE_GRADE = "UPDATE " + TABLE_GRADE +
			" SET " + 
			COLUMN_GRADE_EARNED + " = ?, " +
			COLUMN_POSSILBE_GRADE + " = ?, " + 
			COLUMN_GRADE_CATEGORY + " = ? " +
			"WHERE _id = ?";
	private static final String DELETE_GRADE = "DELETE FROM " + TABLE_GRADE +
			" WHERE _id = ?";

	public static class GradeCursor extends CursorWrapper {
		public GradeCursor(Cursor c) {
			super(c);
		}

		// This all has to have Student changed to grade
		public Grade getGradeInfo() {
			if (isBeforeFirst() || isAfterLast()) {
				return null;
			}

			Grade newGrade = new Grade();

			newGrade.setId(getLong(getColumnIndex(COLUMN_ID)));
			newGrade.setClassId(getLong(getColumnIndex(COLUMN_CLASS_ID)));
			newGrade.setUserId(getLong(getColumnIndex(COLUMN_USER_ID)));
			newGrade.setStudentId(getLong(getColumnIndex(COLUMN_STUDENT_ID)));
			newGrade.setPercentage(getDouble(getColumnIndex(COLUMN_PERCENTAGE)));
			newGrade.setEarnedGrade(getDouble(getColumnIndex(COLUMN_GRADE_EARNED)));
			newGrade.setPossibleGrade(getDouble(getColumnIndex(COLUMN_POSSILBE_GRADE)));
			newGrade.setGradeCategory(getString(getColumnIndex(COLUMN_GRADE_CATEGORY)));

			return newGrade;		
		}
	}

	public long insertGrade(Grade newGrade) {
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_CLASS_ID, newGrade.getClassId());
		cv.put(COLUMN_USER_ID, newGrade.getUserId());
		cv.put(COLUMN_PERCENTAGE, newGrade.getPercentage());
		cv.put(COLUMN_STUDENT_ID, newGrade.getStudentId());		
		cv.put(COLUMN_GRADE_EARNED, newGrade.getEarnedGrade());
		cv.put(COLUMN_POSSILBE_GRADE, newGrade.getPossibleGrade());
		cv.put(COLUMN_GRADE_CATEGORY, newGrade.getGradeCategory());

		
		try {
			long id = getWritableDatabase().insertOrThrow(TABLE_GRADE, null, cv);
			return id;
		} catch(SQLException e) {
			Log.e("Exception", "SQLException"+String.valueOf(e.getMessage()));
			e.printStackTrace();
			return -1;
		}
	}

	public GradeCursor deleteGrade(Grade newGrade) {
		String[] gradeInfo = {String.valueOf(newGrade.getId())};
		Cursor cursor = getWritableDatabase().rawQuery(DELETE_GRADE, gradeInfo);
		return new GradeCursor(cursor);
	}

	public GradeCursor updateGrade(Grade newGrade) {
		String[] gradeInfo = {String.valueOf(newGrade.getEarnedGrade()), 
				String.valueOf(newGrade.getPossibleGrade()), newGrade.getGradeCategory(), 
				String.valueOf(newGrade.getId())};
		Cursor cursor = getWritableDatabase().rawQuery(UPDATE_GRADE, gradeInfo);
		return new GradeCursor(cursor);
	}
	
	/**
	 * This function gets one specific grade from the database by the id
	 * of the grade
	 */
	public GradeCursor getGrade(long grade_id) {
		Cursor cursor = getReadableDatabase().rawQuery("Select * FROM " + TABLE_GRADE + 
				" WHERE _id = ?", new String[]{String.valueOf(grade_id)});
		return new GradeCursor(cursor);
	}
	
	/**
	 * This function returns a grade for a user that is a student
	 */
	public GradeCursor getGrade(long class_id, long user_id, boolean ph) {
		Cursor cursor = getReadableDatabase().rawQuery("Select * FROM " + TABLE_GRADE +
				" WHERE " + COLUMN_CLASS_ID + "= ? AND " + COLUMN_USER_ID + " = ?",
				new String[]{ String.valueOf(class_id), String.valueOf(user_id)});
		return new GradeCursor(cursor);
	}

	/**
	 * This functions returns the grades for a student if the user is a teacher.
	 */
	public GradeCursor getGrade(long class_id, long student_id) {
		Cursor cursor = getReadableDatabase().rawQuery("Select * FROM " + TABLE_GRADE +
				" WHERE " + COLUMN_CLASS_ID + "= ? AND " + COLUMN_STUDENT_ID + " = ?",
				new String[]{String.valueOf(class_id), String.valueOf(student_id)});
		return new GradeCursor(cursor);
	}

	
	/**
	 * This function returns the grade for a student by category if the user is a teacher
	 */
	public GradeCursor getGrade(long class_id, long student_id, String category) {
		Cursor cursor = getReadableDatabase().rawQuery("Select * FROM " + TABLE_GRADE +
				" WHERE " + COLUMN_CLASS_ID + "= ? AND " + COLUMN_STUDENT_ID + " = ? " +
				" AND " + COLUMN_GRADE_CATEGORY + "= ?",
				new String[]{String.valueOf(class_id), String.valueOf(student_id), category});
		return new GradeCursor(cursor);
	}

	/**
	 * This function returns the grade by category for a user that is a student.
	 */
	public GradeCursor getGrade(long class_id, String category) {
		Cursor cursor = getReadableDatabase().rawQuery("Select * FROM " + TABLE_GRADE +
				" WHERE " + COLUMN_CLASS_ID + "= ? AND " + COLUMN_GRADE_CATEGORY + "= ?",
				new String[]{ String.valueOf(class_id), category});
		return new GradeCursor(cursor);
	}
	//*************************************************************************************************
	//*************************************************************************************************


}
