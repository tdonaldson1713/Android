package com.donaldson.gradetracker;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.donaldson.gradetracker.DatabaseHelper.ClassCursor;
import com.donaldson.gradetracker.DatabaseHelper.GradeCursor;
import com.donaldson.gradetracker.DatabaseHelper.StudentCursor;

public class GradeBreakdownActivity extends Activity {
	public static final String ARGS_CLASS_ID = "class_id";
	public static final String ARGS_ALL_GRADES = "all_grades";
	public static final String ARGS_IS_TEACHER = "is_teacher";
	public static final String ARGS_STUDENT_ID = "student_id";
	public static final String ARGS_CATEGORY_NAME = "grade_category";

	private boolean allGrades;
	private boolean hasUpdate = false;
	private boolean isTeacher;
	private boolean isPercentage;
	private long class_id;
	private long student_id;
	private String category_title = "";

	private ArrayList<Grade> grades;
	private Bundle mExtras;
	private Class mClass;
	private DecimalFormat df = new DecimalFormat("0.00");
	private ListView mListView;
	private ListViewAllCategoriesAdapter adapterAll;
	private ListViewCategoryAdapter adapterOne;
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
		grades.clear();
		
		mListView = (ListView) findViewById(R.id.list_view_grades);
		getGrades();

		if (mClass.getGradingCategory().equals("Percentage")) {
			isPercentage = true;
		} else {
			isPercentage = false;
		}

		if (allGrades) {
			adapterAll = new ListViewAllCategoriesAdapter(this, R.layout.list_item_grade_activity, grades, isPercentage);
			
			adapterAll.registerDataSetObserver(new DataSetObserver() {
				@Override
				public void onChanged() {
					listViewSetup();
				}
			});
			
			mListView.setAdapter(adapterAll); 
		} else {
			adapterOne = new ListViewCategoryAdapter(this, R.layout.list_item_grade_activity, grades, isPercentage);
			adapterOne.registerDataSetObserver(new DataSetObserver() {
				@Override
				public void onChanged() {
					listViewSetup();
				}
			});
			mListView.setAdapter(adapterOne);
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

	@Override
	public void onBackPressed() {
		final_update();

		if (isPercentage) {
			if (isTeacher) {
				mStudent.setOverallGrade(calculatePercentage());
				StudentCursor cursorTemp = StudentLab.get(getApplicationContext()).updateStudent(mStudent);
				finalizeStudent(cursorTemp);
			} else {
				mClass.setOverallGrade(calculatePercentage());
				ClassCursor cursorTemp = ClassLab.get(getApplicationContext()).updateClass(mClass);
				finalizeClass(cursorTemp);
			}
		} else { // isPoints
			if (isTeacher) {
				mStudent.setOverallGrade(calculatePoints());
				StudentCursor cursorTemp = StudentLab.get(getApplicationContext()).updateStudent(mStudent);
				finalizeStudent(cursorTemp);
			} else {
				mClass.setOverallGrade(calculatePoints());
				ClassCursor cursorTemp = ClassLab.get(getApplicationContext()).updateClass(mClass);
				finalizeClass(cursorTemp);
			}
		}

		super.onBackPressed();
	}

	private void final_update() {
		GradeCursor cursor = null;

		if (isTeacher) {
			cursor = GradeLab.get(this).getGrades(class_id, student_id);
		} else {
			cursor = GradeLab.get(this).getGrades(class_id, mClass.getUserId(), false);
		} 

		grades.clear();

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			grades.add(cursor.getGradeInfo());
			cursor.moveToNext();
		}

		cursor.close();
	}

	private ArrayList<String> getCategories() {
		ArrayList<String> categories = new ArrayList<String>();
		for (int a = 0; a < grades.size(); a++) {
			if (!categories.contains(grades.get(a).getGradeCategory())) {
				categories.add(grades.get(a).getGradeCategory());
			}
		}

		return categories;
	}


	private double calculatePercentage() {
		double runningEarned = 0;
		double runningPossible = 0;
		double runningTotal = 0;
		double runningPercentage = 0;
		double individualPercentage = 0;
		boolean first;

		ArrayList<String> categories = new ArrayList<String>();
		categories.addAll(getCategories());

		for (int a = 0; a < categories.size(); a++) {
			first = true;
			for (int b = 0; b < grades.size(); b++) {
				if (categories.get(a).equals(grades.get(b).getGradeCategory())) {
					runningEarned += grades.get(b).getEarnedGrade();
					runningPossible += grades.get(b).getPossibleGrade();
					if (first) {
						first = false;
						individualPercentage = grades.get(b).getPercentage();
						runningPercentage += individualPercentage;
					}
				}
			}

			runningTotal += CalculateGrades.calculate_percentage(runningEarned, runningPossible, individualPercentage);
			runningEarned = 0; 
			runningPossible = 0;
		}

		double grade = (runningTotal / runningPercentage);
		return Double.valueOf(df.format(grade));
	}

	private double calculatePoints() {
		double runningEarned = 0;
		double runningPossible = 0;

		for (int a = 0; a < grades.size(); a++) {
			runningEarned += grades.get(a).getEarnedGrade();
			runningPossible += grades.get(a).getPossibleGrade();
		}

		double grade = CalculateGrades.calculate_points(runningEarned, runningPossible);
		return Double.valueOf(df.format(grade));
	}

	private void finalizeStudent(StudentCursor cursorTemp) {
		cursorTemp.moveToFirst();

		if (!cursorTemp.isAfterLast()) {
			Student mStudent = cursorTemp.getStudentInfo();
		}

		cursorTemp.close();
	}

	private void finalizeClass(ClassCursor cursorTemp) {
		cursorTemp.moveToFirst();

		if (!cursorTemp.isAfterLast()) {
			Class mClass = cursorTemp.getClassInfo();
		}

		cursorTemp.close();
	}
}
