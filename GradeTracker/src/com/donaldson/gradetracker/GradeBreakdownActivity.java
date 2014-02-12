package com.donaldson.gradetracker;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.donaldson.gradetracker.DatabaseHelper.GradeCursor;

public class GradeBreakdownActivity extends Activity {
	public static final String ARGS_CLASS_ID = "class_id";
	public static final String ARGS_ALL_GRADES = "all_grades";
	public static final String ARGS_IS_TEACHER = "is_teacher";
	public static final String ARGS_STUDENT_ID = "student_id";
	public static final String ARGS_CATEGORY_NAME = "grade_category";

	private boolean allGrades;
	private boolean isTeacher;
	private boolean isPercentage;
	private long class_id;
	private long student_id;
	private String category_title = "";

	private ArrayList<Grade> grades;
	private Bundle mExtras;
	private Class mClass;
	private ListView mListView;
	private Student mStudent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grade_category_list);

		init();
		listViewSetup(); 
	}

	private void init() {
		mExtras = getIntent().getExtras();
		grades = new ArrayList<Grade>();

		allGrades = mExtras.getBoolean(ARGS_ALL_GRADES, false);
		isTeacher = mExtras.getBoolean(ARGS_IS_TEACHER, false);

		if (isTeacher) {
			loadTeacherData();
		} else {
			loadStudentData();
		}

		if (!allGrades) {
			loadCategoryTitle();
		}
	}

	private void listViewSetup() {
		mListView = (ListView) findViewById(R.id.list_view_grades);
		getGrades();

		if (mClass.getGradingCategory().equals("Percentage")) {
			isPercentage = true;
		} else {
			isPercentage = false;
		}

		if (allGrades) {
			ListViewAllCategoriesAdapter adapter = new ListViewAllCategoriesAdapter(this, R.layout.list_item_grade_activity, grades, isPercentage);
			mListView.setAdapter(adapter); 
		} else {
			ListViewCategoryAdapter adapter = new ListViewCategoryAdapter(this, R.layout.list_item_grade_activity, grades, isPercentage);
			mListView.setAdapter(adapter);
		}
	}

	private void getGrades() {
		GradeCursor cursor;

		if (allGrades) { // Load all grades.
			if (isTeacher) {
				cursor = GradeLab.get(this).getGrades(class_id, student_id);
			} else {
				cursor = GradeLab.get(this).getGrades(class_id, mClass.getUserId(), false);
			} 
		} else { // Only load a specific category of grades.
			if (isTeacher) {
				cursor = GradeLab.get(this).getGrades(class_id, student_id, category_title);
			} else {
				cursor = GradeLab.get(this).getGrades(class_id, category_title);
			}
		}

		getGradesFromCursor(cursor);
	}

	public void getGradesFromCursor(GradeCursor cursor) {
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			grades.add(cursor.getGradeInfo());
			cursor.moveToNext();
		}
		cursor.close();
	}

	private void loadTeacherData() {
		student_id = mExtras.getLong(ARGS_STUDENT_ID);
		mStudent = StudentLab.get(getApplicationContext()).selectStudent(student_id);
		loadStudentData();
	}

	private void loadStudentData() {
		class_id = mExtras.getLong(ARGS_CLASS_ID);
		mClass = ClassLab.get(getApplicationContext()).selectClass(class_id);
	}

	private void loadCategoryTitle() {
		category_title = mExtras.getString(ARGS_CATEGORY_NAME);
	}
}
