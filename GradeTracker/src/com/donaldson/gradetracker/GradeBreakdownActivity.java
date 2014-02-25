package com.donaldson.gradetracker;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
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
		mListView = (ListView) findViewById(R.id.list_view_grades);
		getGrades();

		if (mClass.getGradingCategory().equals("Percentage")) {
			isPercentage = true;
		} else {
			isPercentage = false;
		}

		if (allGrades) {
			adapterAll = new ListViewAllCategoriesAdapter(this, R.layout.list_item_grade_activity, grades, isPercentage);
			mListView.setAdapter(adapterAll); 
		} else {
			adapterOne = new ListViewCategoryAdapter(this, R.layout.list_item_grade_activity, grades, isPercentage);
			mListView.setAdapter(adapterOne);
		}

		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long id) {
				dialog_create_grade_item(pos, id);
				return true;
			}
		});

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

	private void dialog_create_grade_item(final int pos, final long id) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View v = inflater.inflate(R.layout.dialog_edit_delete_grade, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		ImageButton btnCancel = (ImageButton) v.findViewById(R.id.edg_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		ImageButton btnDelete = (ImageButton) v.findViewById(R.id.edg_delete);
		btnDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				hasUpdate = true;
				
				GradeCursor cursor = GradeLab.get(getApplicationContext()).deleteGrade(grades.get(pos));
				cursor.moveToFirst();
				Grade mGrade = cursor.getGradeInfo();
				cursor.close();

				grades.clear();
				listViewSetup();

				dialog.dismiss();
			}
		});

		ImageButton btnEdit = (ImageButton) v.findViewById(R.id.edg_edit);
		btnEdit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				createEditGradeDialog(pos);
				dialog.dismiss();
			}
		});	

		dialog.setView(v, 0, 0, 0, 0);
		dialog.show();
	}

	@Override
	public void onBackPressed() {
		if (hasUpdate) {
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


	private void createEditGradeDialog(final int pos) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View edit_grade = inflater.inflate(R.layout.dialog_new_grade, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog edit_dialog = builder.create();
		edit_dialog.setView(edit_grade, 0, 0, 0, 0);

		ImageView banner = (ImageView) edit_grade.findViewById(R.id.imageView1);
		banner.setImageResource(R.drawable.edit_grade);

		final ImageButton insert_button = (ImageButton) edit_grade.findViewById(R.id.imgInsertGrade);
		insert_button.setImageResource(R.drawable.edit);

		final Spinner grade_type_spinner = (Spinner) edit_grade.findViewById(R.id.spinnerGradeType);
		final EditText grade_earned = (EditText) edit_grade.findViewById(R.id.editGradeEarned);
		final EditText grade_possible = (EditText) edit_grade.findViewById(R.id.editGradePossible);
		final EditText grade_percentage = (EditText) edit_grade.findViewById(R.id.editGradePercentage);		

		if (!isPercentage) {
			grade_percentage.setEnabled(false);
		}

		/*
		 * Have to manually change the colors since they inherit from the parent activity
		 */
		grade_earned.setTextColor(getResources().getColor(android.R.color.black));
		grade_possible.setTextColor(getResources().getColor(android.R.color.black));
		grade_percentage.setTextColor(getResources().getColor(android.R.color.black));

		/*
		 * Put the stored values into the edit texts and populate the spinner
		 * with only the categories that have grades entered
		 */
		grade_earned.setText(String.valueOf(grades.get(pos).getEarnedGrade()));
		grade_possible.setText(String.valueOf(grades.get(pos).getPossibleGrade()));
		grade_percentage.setText(String.valueOf(grades.get(pos).getPercentage() * 100));

		ArrayList<String> categories = new ArrayList<String>();
		categories.addAll(getCategories());

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		grade_type_spinner.setAdapter(adapter);

		for (int a = 0; a < categories.size(); a++) {
			if (grade_type_spinner.getItemAtPosition(a).toString().equals(grades.get(pos).getGradeCategory())) {
				grade_type_spinner.setSelection(a);
				break;
			}
		}
		grade_type_spinner.setEnabled(false);

		insert_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				double percentage = 0.0;
				hasUpdate = true;

				if (!grade_earned.getText().toString().equals("")) {
					grades.get(pos).setEarnedGrade(Double.valueOf(grade_earned.getText().toString()));
				}

				if (!grade_possible.getText().toString().equals("")) {
					grades.get(pos).setPossibleGrade(Double.valueOf(grade_possible.getText().toString()));
				}

				if (isPercentage && !grade_percentage.getText().toString().equals("")) {
					percentage = Double.valueOf(grade_percentage.getText().toString());
					if (percentage > 1) {
						percentage /= 100.0;
					}
				} else {
					percentage = 0.01;
				}


				// This is so we can change all the categories later on.
				boolean percentage_change = false;
				String category_title = "";
				if (percentage != grades.get(pos).getPercentage()) {
					percentage_change = true;
					category_title = grades.get(pos).getGradeCategory();
				}

				grades.get(pos).setPercentage(Double.valueOf(df.format(percentage)));

				/*
				 * Finish updating. If the grade of the percentage has changed, we need to
				 * update all the grades with that category also.
				 */
				GradeCursor cursor = GradeLab.get(getApplicationContext()).updateGrade(grades.get(pos));
				cursor.moveToFirst();
				Grade mGrade = cursor.getGradeInfo();
				cursor.close();

				/*
				 * Here we will update all the grades of a category...only if
				 * the user has change the percentage.
				 */

				if (percentage_change) {
					for (int a = 0; a < grades.size(); a++) {
						if (grades.get(a).getGradeCategory().equals(category_title)) {
							grades.get(a).setPercentage(percentage);
							GradeCursor temp = GradeLab.get(getApplicationContext()).updateGrade(grades.get(a));
							temp.moveToFirst();
							Grade tempG = temp.getGradeInfo();
							cursor.close();
						}
					}
				}

				grades.clear();
				listViewSetup();

				edit_dialog.dismiss();
			}
		});

		edit_dialog.show();
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
