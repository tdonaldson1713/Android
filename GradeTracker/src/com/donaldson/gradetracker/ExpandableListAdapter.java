package com.donaldson.gradetracker;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.donaldson.gradetracker.DatabaseHelper.ClassCursor;
import com.donaldson.gradetracker.DatabaseHelper.GradeCursor;
import com.donaldson.gradetracker.DatabaseHelper.StudentCursor;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
	private Context mClassContext;
	private Resources r;

	private ArrayList<Class> classes = new ArrayList<Class>();
	private ArrayList<Grade> gradeList = new ArrayList<Grade>();
	private ArrayList<Integer> child_padding = new ArrayList<Integer>();
	private ArrayList<Student> students = new ArrayList<Student>();
	private ArrayList<String> grade_types = new ArrayList<String>();
	private ArrayList<String> grade_percentages = new ArrayList<String>();

	private boolean isTeacher = false;
	private boolean isPercentage = false;

	private double overall = 0;
	private double finalPercentage = 0;
	private double earnedGrade = 0;
	private double totalPossible = 0;
	private double totalEarned = 0;

	//private int[] blueback = {13, 24, 92};
	private int g; // Group Position at expansion
	private int c; // Child Position at expansion

	private long user_id = -1;
	private long class_id = -1;
	private long student_id = -1;
	
	public ExpandableListAdapter(Context context) {
		mClassContext = context;
		createChildPadding();
	}

	public ExpandableListAdapter(Context context, ArrayList<Class> parent, boolean isTeacher) {
		this.mClassContext = context;
		this.isTeacher = isTeacher;
		classes.addAll(parent);
		createChildPadding();
	}

	public void setIsPercentage(boolean isPercentage) {
		this.isPercentage = isPercentage;
	}

	private void createChildPadding() {
		child_padding.add(40); // Left
		child_padding.add(15); // Top
		child_padding.add(0); // Right
		child_padding.add(15); // Bottom
	}

	public void insertNewClass(Class newClass) {
		classes.add(newClass);
	}

	public void insertNewStudent(Student newStudent) {
		students.add(newStudent);
	}

	public void insertAllStudents(ArrayList<Student> student) {
		if (this.students.size() > 0 ) {
			students.clear();
		}

		this.students.addAll(student);
	}
	
	public void insertAllClasses(ArrayList<Class> classes) {
		if (this.classes.size() > 0) {
			this.classes.clear();
		}
		
		this.classes.addAll(classes);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		r = mClassContext.getResources();

		final String className = classes.get(groupPosition).getClassName();

		if (this.isTeacher){ // If the teacher is a student.
			if (students.size() > 0) {
				LayoutInflater inflater = LayoutInflater.from(mClassContext);
				View viewer = inflater.inflate(R.layout.list_child_view, parent, false);

				TextView txtStudentName = (TextView) viewer.findViewById(R.id.txt_student_name);
				final TextView txtStudentGrade = (TextView) viewer.findViewById(R.id.txt_student_grade);

				final Student s = students.get(childPosition);

				if (s.getClassId() == classes.get(groupPosition).getId()) {
					final String studentName = s.getFirstName() + " " + s.getLastName();
					txtStudentName = setTextInformation(txtStudentName, child_padding, studentName);
					txtStudentGrade.setPadding(0, 15, 30, 15);

					double grade = s.getOverallGrade();
					if (grade == -1) {
						txtStudentGrade.setText("");
					} else {
						txtStudentGrade.setText(grade + "%");
					}

					viewer.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							g = groupPosition;
							c = childPosition;

							if (students.size() > 0) {
								student_id = students.get(c).getId();
								class_id = students.get(c).getClassId();
								user_id = students.get(c).getUserId();
							}


							createGradeOverviewDialog(true, studentName, "\nClass: "+className);
							students.get(c).setOverallGrade(overall);
							StudentCursor cursor = StudentLab.get(mClassContext).updateStudent(students.get(c));
							finalizeStudent(cursor);
							ExpandableListAdapter.this.notifyDataSetChanged();
							resetIncrementedValues();
						}
					});
					return viewer; 
				} else {
					return emptyView(r);
				}
			} else { // If there are no students in the class.
				return emptyView(r);
			}

		} else { // if the user is a student
			LayoutInflater inflater = LayoutInflater.from(mClassContext);
			View viewer = inflater.inflate(R.layout.list_child_view_student, parent, false);
			
			TextView txtUserGrade = (TextView) viewer.findViewById(R.id.txt_student_list_grade);
			txtUserGrade.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String username = UserLab.get(mClassContext).selectUser(classes.get(0).getUserId()).getUsername();
					g = groupPosition;
					c = childPosition;

					if (classes.size() > 0) {
						student_id = 0;
						user_id = classes.get(groupPosition).getUserId();
						class_id = classes.get(groupPosition).getId();
					}

					createGradeOverviewDialog(false, username, "\nClass: "+className);
					classes.get(g).setOverallGrade(overall);
					ClassCursor cursor = ClassLab.get(mClassContext).updateClass(classes.get(g));
					finalizeClass(cursor);
					ExpandableListAdapter.this.notifyDataSetChanged();
					resetIncrementedValues();
				}
			});

			double grade = classes.get(groupPosition).getOverallGrade();
			String grade_string = "";
			if (grade == -1) {
				grade_string = "0.0%";
			} else {
				grade_string = grade + "%";
			}

			txtUserGrade = setTextInformation(txtUserGrade, child_padding, 
					r.getString(R.string.current_grade, grade_string));

			return viewer;
		}
	}

	private void finalizeStudent(StudentCursor cursor) {
		cursor.moveToFirst();
		Student mStudent = cursor.getStudentInfo();
		cursor.close();
	}

	private void finalizeClass(ClassCursor cursor) {
		cursor.moveToFirst();
		Class mClass = cursor.getClassInfo();
		cursor.close();
	}

	private void finalizeGrade(GradeCursor cursor) {
		cursor.moveToFirst();
		Grade mGrade = cursor.getGradeInfo();
		cursor.close();
	}
	/*
	 * This functions creates a view telling the user that there are no children of the group
	 * that was last selected.
	 */
	public View emptyView(Resources r) {
		TextView emptyStudents = new TextView(mClassContext);
		emptyStudents = setTextInformation(emptyStudents, child_padding, 
				r.getString(R.string.no_students));
		return emptyStudents;
	}

	/*
	 * This functions sets the information that goes into the view of the child.
	 */
	private TextView setTextInformation(TextView txt, ArrayList<Integer> padding, String text) {
		txt.setPadding(padding.get(0), padding.get(1), padding.get(2), padding.get(3));
		txt.setText(text);
		txt.setTextAppearance(mClassContext, R.style.TextColor);
		return txt;
	}

	/*
	 * This function gets the the grades out of the cursor for a specific user.
	 */
	private ArrayList<Grade> getSelectedGrades(final boolean isChild) {
		ArrayList<Grade> studentGrades = new ArrayList<Grade>();

		if (isChild) { // when the user is a teacher
			Student selectedChild = students.get(c);
			GradeCursor cursor = GradeLab.get(mClassContext).getGrades(selectedChild.getClassId(),
					selectedChild.getId());
			studentGrades = getGradesFromCursor(studentGrades, cursor);
			cursor.close();
		} else { // when the user is a student
			Class expandedGroup = classes.get(g);
			GradeCursor cursor = GradeLab.get(mClassContext).getGrades(expandedGroup.getId(),
					expandedGroup.getUserId(), false);
			studentGrades = getGradesFromCursor(studentGrades, cursor);
			cursor.close();
		}

		return studentGrades;
	}

	private ArrayList<Grade> getGradesFromCursor(ArrayList<Grade> studentGrades,
			GradeCursor cursor) {

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			studentGrades.add(cursor.getGradeInfo());
			cursor.moveToNext();
		}
		cursor.close();

		return studentGrades;
	}

	/*
	 * This functions gets all the categories from the list of grades that are
	 * saved in the database.
	 */
	private ArrayList<String> getGradeCategories(ArrayList<Grade> grades) {
		ArrayList<String> categ = new ArrayList<String>();

		for (Grade grade : grades) {
			String categoryTitle = grade.getGradeCategory();
			if (!categ.contains(categoryTitle)) {
				categ.add(categoryTitle);
			}
		}

		return categ;
	}

	/*
	 * This function creates a dialog that allows the user to see their grades for each category.
	 */
	private void createGradeOverviewDialog(final boolean isChild, String name, String className) {
		grade_percentages.clear();
		ArrayList<String> categories = list_generator(isChild);

		LayoutInflater inflater = LayoutInflater.from(mClassContext);
		View custom_view =	inflater.inflate(R.layout.dialog_grade_overview, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(mClassContext);
		final AlertDialog dialog = builder.create();
		dialog.setView(custom_view, 0, 0, 0, 0);
		
		String nameTitle = setNameTitle();

		TextView txtTitle = (TextView) custom_view.findViewById(R.id.txtGradeTitle);
		txtTitle.setText(r.getString(R.string.grade_overview_title, className, nameTitle, name));

		ListView listGrades = (ListView) custom_view.findViewById(R.id.listViewGradeOverview);

		if (categories.size() == 0) {
			categories.add(r.getString(R.string.no_categories));
		}

		if (isPercentage) {
			PercentageGradeListArrayAdapter adapter = new PercentageGradeListArrayAdapter(mClassContext, 
					R.layout.list_item_grade_overview, categories, isTeacher, class_id, student_id, dialog);
			listGrades.setAdapter(adapter);
		} else {
			PointGradeListArrayAdapter adapter = new PointGradeListArrayAdapter(mClassContext, 
					R.layout.list_item_grade_overview, categories, isTeacher, class_id, student_id, dialog);
			listGrades.setAdapter(adapter);
		}

		ImageButton btnCancel = (ImageButton) custom_view.findViewById(R.id.btn_cancel_grade);
		ImageButton btnAddGrade = (ImageButton) custom_view.findViewById(R.id.btn_add_grade);
		ImageButton btnViewAllGrades = (ImageButton) custom_view.findViewById(R.id.btn_view_all_grades);

		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				resetIncrementedValues();
				ExpandableListAdapter.this.notifyDataSetChanged();
				dialog.cancel();
			}
		});

		btnAddGrade.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
				createAddGradeDialog(grade_types, isChild);
				grade_types.clear();
			}
		});

		btnViewAllGrades.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isTeacher) {
					dialog.cancel();
					Intent i = new Intent(mClassContext, GradeBreakdownActivity.class);
					i.putExtra(GradeBreakdownActivity.ARGS_CLASS_ID, class_id);
					i.putExtra(GradeBreakdownActivity.ARGS_ALL_GRADES, true);
					i.putExtra(GradeBreakdownActivity.ARGS_IS_TEACHER, true);
					i.putExtra(GradeBreakdownActivity.ARGS_STUDENT_ID, student_id);
					mClassContext.startActivity(i);
				} else {
					dialog.cancel();
					Intent i = new Intent(mClassContext, GradeBreakdownActivity.class);
					i.putExtra(GradeBreakdownActivity.ARGS_CLASS_ID, class_id);
					i.putExtra(GradeBreakdownActivity.ARGS_ALL_GRADES, true);
					i.putExtra(GradeBreakdownActivity.ARGS_IS_TEACHER, false);
					mClassContext.startActivity(i);
				}

			}
		});

		dialog.show();
	}

	private String setNameTitle() {
		String nameTitle;
		if (isTeacher) {
			nameTitle = "\nStudent: ";
		} else {
			nameTitle = "\nUser: ";
		}

		return nameTitle;
	}

	/*
	 * This functions generates the lists necessary for creating the overview
	 * for the selected student or user for the class.
	 */
	private ArrayList<String> list_generator(final boolean isChild) {
		gradeList.clear();

		if (isChild) {
			gradeList.addAll(getSelectedGrades(true));
		} else {
			gradeList.addAll(getSelectedGrades(false));
		}

		ArrayList<String> categories = new ArrayList<String>();
		categories.addAll(getGradeCategories(gradeList));

		grade_types.addAll(categories);

		if (isPercentage) {
			categories.add(calculatePercentageGradeOverview(gradeList, categories));
		} else {
			categories.add(calculatePointsGradeOverview(gradeList, categories));
		}

		return categories;
	}

	// This function resets the values that are incremented and set when calculating
	// the grades of the student.
	public void resetIncrementedValues() {
		overall = 0;
		finalPercentage = 0;
		earnedGrade = 0;
		totalEarned = 0;
		totalPossible = 0;
	}

	private String calculatePointsGradeOverview(ArrayList<Grade> grades,
			ArrayList<String> categories) {
		resetIncrementedValues();

		int counter = 0;
		for (String c_name : categories) {
			double possible = 0;
			double earned = 0;

			for (Grade grade : grades) {
				if (grade.getGradeCategory().equals(c_name)) {
					possible += grade.getPossibleGrade();
					earned += grade.getEarnedGrade();
				}
			}

			totalPossible += possible;
			totalEarned += earned;

			earnedGrade = CalculateGrades.calculate_points(earned, possible);
			String final_name = c_name + "," + (earned + " / " + possible) + "," + earnedGrade;
			categories.set(counter, final_name);
			counter++;
		}

		DecimalFormat df = new DecimalFormat("0.00");
		overall = totalEarned / totalPossible;
		overall = Double.valueOf(df.format(overall * 100));

		formatOverallGrade();
		String final_grade = r.getString(R.string.final_grade) + "," + (totalEarned + " / " + totalPossible) + "," + overall;

		return final_grade;
	}

	private void formatOverallGrade() {
		DecimalFormat df = new DecimalFormat("0.00");
		overall = Double.valueOf(df.format(overall));
	}

	/*
	 *  This function is used to get all the grades out of the database for a given
	 *  category name. This will be used to create the text views for the entries
	 *  of the dialog view.
	 *  */
	private String calculatePercentageGradeOverview(ArrayList<Grade> grades,
			ArrayList<String> categories) {
		resetIncrementedValues();

		int counter = 0;
		for (String c_name : categories) {
			double possible = 0;
			double earned = 0;
			double percentage = 0;
			boolean first = true;

			for (Grade grade : grades) {
				if (grade.getGradeCategory().equals(c_name)) {
					possible += grade.getPossibleGrade();
					earned += grade.getEarnedGrade();

					if (first) {
						percentage = grade.getPercentage();
						finalPercentage += percentage;
						first = false;
					}
				}
			}

			earnedGrade = CalculateGrades.calculate_percentage(earned, possible, percentage);
			overall += earnedGrade;
			grade_percentages.add(c_name + " " + String.valueOf(percentage));
			String final_name = c_name + "," + earnedGrade + "," + percentage;
			categories.set(counter, final_name);
			counter++;
		}

		formatOverallGrade();

		String final_grade = r.getString(R.string.final_grade) + "," + overall + "," + finalPercentage;

		boolean changed = false;
		if (finalPercentage < 1) {
			finalPercentage *= 100;
			changed = true;
		}

		DecimalFormat df = new DecimalFormat("0.000");
		overall = (overall / finalPercentage) * 100;
		overall = Double.valueOf(df.format(overall));

		// Something was happening with the decimal format that would cause the overall
		// grade to be in the 1000s.
		if (overall > 1000) {
			overall /= 100;
		}

		if (changed) {
			finalPercentage /= 100;
		}

		return final_grade;
	}

	/*
	 * This function creates the dialog that the user must use to enter a new
	 * grade for a given student or class.
	 * 
	 * gradeCategories -> Any new categories that should be added to the spinner's
	 * 						list of grade types.
	 * class_id -> Used for adding the grade to a class, user is a student.
	 * student_id -> Used for adding the grade to a student of a class in conjunction with class_id
	 */
	@SuppressLint("NewApi")
	private void createAddGradeDialog(ArrayList<String> gradeCategories, final boolean is_child) {
		LayoutInflater inflater = LayoutInflater.from(mClassContext);
		View add_grade = inflater.inflate(R.layout.dialog_new_grade, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(mClassContext);
		final AlertDialog dialog = builder.create();
		dialog.setView(add_grade, 0, 0, 0, 0);

		/*
		 * Let's get all of the necessary views before doing anything else. 
		 */
		final ImageButton cancel_button = (ImageButton) add_grade.findViewById(R.id.imgCancelGrade);
		final ImageButton insert_button = (ImageButton) add_grade.findViewById(R.id.imgInsertGrade);
		final Spinner grade_type_spinner = (Spinner) add_grade.findViewById(R.id.spinnerGradeType);
		final EditText grade_earned = (EditText) add_grade.findViewById(R.id.editGradeEarned);
		final EditText grade_possible = (EditText) add_grade.findViewById(R.id.editGradePossible);
		final EditText grade_percentage = (EditText) add_grade.findViewById(R.id.editGradePercentage);		

		/*
		 * Since I'm loading from the classListActivity context, I have to change the typeface of the 
		 * edit texts manually.
		 */
		grade_earned.setTextAppearance(mClassContext, R.style.DialogTextColor);
		grade_possible.setTextAppearance(mClassContext, R.style.DialogTextColor);
		grade_percentage.setTextAppearance(mClassContext, R.style.DialogTextColor);

		if (!isPercentage) {
			grade_percentage.setEnabled(false);
		}

		// We're going to add more grade types according to what the user has already entered.
		if (!gradeCategories.isEmpty()) {
			ArrayList<String> spinner_categories = new ArrayList<String>();
			for (int a = 0; a < grade_type_spinner.getCount(); a++) {
				spinner_categories.add(grade_type_spinner.getItemAtPosition(a).toString());
			}

			ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(mClassContext, android.R.layout.simple_spinner_item, android.R.id.text1);
			spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			grade_type_spinner.setAdapter(spinnerAdapter);

			for (int a = 0; a < spinner_categories.size(); a++) {
				if (!spinner_categories.get(a).equals(r.getString(R.string.new_category))) {
					spinnerAdapter.add(spinner_categories.get(a));
				}
			}

			for (int a = 0; a < gradeCategories.size(); a++) {
				if (!spinner_categories.contains(gradeCategories.get(a))) {
					spinnerAdapter.add(gradeCategories.get(a));
				}
			}
			spinnerAdapter.add(r.getString(R.string.new_category));
			spinnerAdapter.notifyDataSetChanged();
		}

		if (isPercentage) {
			grade_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					boolean found = false;
					for (int a = 0; a < grade_percentages.size() && !found; a++) {
						int index = grade_percentages.get(a).indexOf(" ");
						String find_category = grade_percentages.get(a).substring(0, index);
						String selected_category = grade_type_spinner.getItemAtPosition(position).toString();

						if (!find_category.equals(selected_category)) {
							grade_percentage.setText("");
							grade_percentage.setEnabled(true);
						} else {
							String found_percentage = grade_percentages.get(a).substring(index+1);
							grade_percentage.setText(found_percentage);
							grade_percentage.setEnabled(false);
							found = true;
						}
					}
				}


				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		}
		/*
		 * Time to work with this stuff!
		 */
		insert_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				double earned = 0.0, possible = 0.0, percentage = 0.0;

				if (!grade_earned.getText().toString().equals("")) {
					earned = Double.valueOf(grade_earned.getText().toString());
				}

				if (!grade_possible.getText().toString().equals("")) {
					possible = Double.valueOf(grade_possible.getText().toString());
				}

				if (isPercentage && !grade_percentage.getText().toString().equals("")) {
					percentage = Double.valueOf(grade_percentage.getText().toString());
					if (percentage > 1) {
						percentage /= 100.0;
					}
				} else {
					percentage = 0.01;
				}

				/*
				 * BIG NOTE: We are assuming the user is smart and does not enter
				 * percentages that will cause overall percentage to exceed 100%
				 * as the sum of all the homework categories.
				 */

				// Time to put this grade into the database.
				if (earned >= 0.0 && possible > 0.0 && (percentage > 0.0 && percentage < 1.0)) {
					Grade newGrade = new Grade();

					newGrade.setEarnedGrade(earned);
					newGrade.setPossibleGrade(possible);
					newGrade.setPercentage(percentage);
					newGrade.setClassId(class_id);
					newGrade.setUserId(user_id);
					newGrade.setStudentId(student_id);
					newGrade.setGradeCategory(grade_type_spinner.getSelectedItem().toString());

					long id = GradeLab.get(mClassContext.getApplicationContext()).insertGrade(newGrade);
					newGrade.setId(id);

					/*
					 * This grade calculation is here to update the user
					 */
					ArrayList<Grade> new_grade_list = new ArrayList<Grade>();
					new_grade_list.addAll(getSelectedGrades(is_child));

					ArrayList<String> categories = new ArrayList<String>();
					categories.addAll(getGradeCategories(new_grade_list));

					if (isPercentage) {
						categories.add(calculatePercentageGradeOverview(new_grade_list, categories));
					} else {
						categories.add(calculatePointsGradeOverview(new_grade_list, categories));
					}

					String name, class_name;
					boolean new_category = false;
					if (newGrade.getGradeCategory().equals("New Category")) {
						new_category = true;
					}

					if (isTeacher) {
						name = students.get(c).getFirstName() + " " + students.get(c).getLastName();
						students.get(c).setOverallGrade(overall);
						StudentCursor cursor = StudentLab.get(mClassContext).updateStudent(students.get(c));
						finalizeStudent(cursor);
					} else {
						name = UserLab.get(mClassContext).selectUser(classes.get(g).getUserId()).getUsername();
						classes.get(g).setOverallGrade(overall);
						ClassCursor cursor = ClassLab.get(mClassContext).updateClass(classes.get(g));
						finalizeClass(cursor);
					}

					class_name = "\n"+classes.get(g).getClassName();
					ExpandableListAdapter.this.notifyDataSetChanged();
					resetIncrementedValues();
					grade_percentages.clear();
					dialog.cancel();

					// This needs to show up in the onclick for cancel as well to bring the 
					// overview up again. 
					if (new_category) {
						createGradeCategoryDialog(is_child, name, "\nClass: " + class_name, id);
					} else {
						createGradeOverviewDialog(is_child, name, "\nClass: " + class_name);
					}
				}
			}
		});

		cancel_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				resetIncrementedValues();
				grade_percentages.clear();
				ExpandableListAdapter.this.notifyDataSetChanged();
				dialog.cancel();
			}
		});

		dialog.show();
	}

	private void createGradeCategoryDialog(final boolean is_child, final String name,
			final String class_name, final long id) {
		LayoutInflater inflater = LayoutInflater.from(mClassContext);
		View category_view = inflater.inflate(R.layout.dialog_new_grade_category, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(mClassContext);
		final AlertDialog dialog = builder.create();
		dialog.setView(category_view, 0, 0, 0, 0);

		final EditText edit_new_category = (EditText) category_view.findViewById(R.id.editNewCategory);
		ImageButton img_insert_category = (ImageButton) category_view.findViewById(R.id.imgInsertCategory);

		img_insert_category.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String new_category = edit_new_category.getText().toString();
				if (!new_category.equals("")) {
					dialog.cancel();
					Grade grade = GradeLab.get(mClassContext).getGrade(id);
					grade.setGradeCategory(new_category);
					GradeCursor cursor = GradeLab.get(mClassContext).updateGrade(grade);
					finalizeGrade(cursor);
					ExpandableListAdapter.this.notifyDataSetChanged();
					createGradeOverviewDialog(is_child, name, class_name);
				} else {
					Toast.makeText(mClassContext, R.string.empty_new_category, Toast.LENGTH_SHORT).show();
				}
			}
		});

		dialog.show();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (students.size() == 0) {
			return 1;
		}
		return students.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public int getGroupCount() {
		return classes.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		LayoutInflater inflater = LayoutInflater.from(mClassContext);
		View v = inflater.inflate(R.layout.list_group_view, parent, false);

		TextView className = (TextView) v.findViewById(R.id.groupClassNameText);
		TextView semester = (TextView) v.findViewById(R.id.groupSemesterText);

		className.setText(classes.get(groupPosition).getClassName());
		semester.setText(classes.get(groupPosition).getSemester());

		return v;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
