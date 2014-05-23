package com.donaldson.gradetracker.classlist;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.donaldson.gradetracker.R;
import com.donaldson.gradetracker.database.DatabaseHelper.ClassCursor;
import com.donaldson.gradetracker.database.DatabaseHelper.GradeCursor;
import com.donaldson.gradetracker.database.DatabaseHelper.StudentCursor;
import com.donaldson.gradetracker.database.classes.Class;
import com.donaldson.gradetracker.database.classes.ClassLab;
import com.donaldson.gradetracker.database.grade.Grade;
import com.donaldson.gradetracker.database.grade.GradeLab;
import com.donaldson.gradetracker.database.student.Student;
import com.donaldson.gradetracker.database.student.StudentLab;
import com.donaldson.gradetracker.database.user.UserLab;
import com.donaldson.gradetracker.gradebreakdown.GradeBreakdownActivity;
import com.donaldson.gradetracker.gradeutils.CalculateGrades;
import com.donaldson.gradetracker.overview.percentages.PercentageGradeListArrayAdapter;
import com.donaldson.gradetracker.overview.points.PointGradeListArrayAdapter;


public class ExpandableListAdapter extends BaseExpandableListAdapter {
	private Context mClassContext;
	private Resources r;

	private ArrayList<Class> classes = new ArrayList<Class>();
	private ArrayList<Grade> gradeList = new ArrayList<Grade>();
	private ArrayList<Integer> child_padding = new ArrayList<Integer>();
	public static ArrayList<Student> students = new ArrayList<Student>();
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

		if (this.isTeacher){ // If the user is a teacher.
			if (students.size() > 0) {
				LayoutInflater inflater = LayoutInflater.from(mClassContext);
				View viewer = inflater.inflate(R.layout.list_child_view_teacher, parent, false);

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
	private void createGradeOverviewDialog(final boolean isChild, final String name, final String className) {
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
		final ImageButton btnSettings = (ImageButton) custom_view.findViewById(R.id.edit_class_img_btn);

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

		/*
		 * Everything is saving to the database, but the expandable list isn't 
		 * always updating the expandable list view.
		 */
		btnSettings.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PopupMenu popup = new PopupMenu(mClassContext, btnSettings);
				popup.getMenuInflater().inflate(R.menu.popup_class_menu, popup.getMenu());

				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						//item.getTitle();
						String lookup_class = className.substring(8);

						Class cla = new Class();
						int position = 0;

						for (int a = 0; a < classes.size(); a++) {
							if (classes.get(a).getClassName().equals(lookup_class)) {
								cla = classes.get(a);
								position = a;
							}
						}

						final Class cl = cla;
						final int pos = position;
						
						if (item.getTitle().equals(r.getString(R.string.delete_class))){ 
							AlertDialog.Builder builder = new AlertDialog.Builder(mClassContext);
							builder.setTitle("Delete");
							builder.setMessage("Are you sure you want to delete?");
							builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (isTeacher) {
										deleteClassTeacher(cl, pos);
									} else {
										deleteClassStudent(cl, 0, pos);
									}
								}
							});

							builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							});

							AlertDialog delete_dialog = builder.create();
							delete_dialog.show();

							dialog.dismiss();
						} else if (item.getTitle().equals(r.getString(R.string.edit_class))) {
							edit_class_dialog(cl);
							dialog.dismiss();
						} else if (item.getTitle().equals(r.getString(R.string.edit_student))) {
							if (!isTeacher) {
								Toast.makeText(mClassContext, r.getString(R.string.not_implemented_for_student), Toast.LENGTH_SHORT).show();
								return true;
							} 
							
							edit_student_dialog(cl);
							dialog.dismiss();
						} else if (item.getTitle().equals(r.getString(R.string.delete_student))) { 
							if (!isTeacher) {
								Toast.makeText(mClassContext, r.getString(R.string.not_implemented_for_student), Toast.LENGTH_SHORT).show();
								return true;
							}
							
							AlertDialog.Builder builder = new AlertDialog.Builder(mClassContext);
							builder.setTitle("Delete");
							builder.setMessage("Are you sure you want to delete?");
							builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									deleteStudent();
									dialog.dismiss();
								}
							});

							builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							});

							AlertDialog delete_dialog = builder.create();
							delete_dialog.show();

							dialog.dismiss();
						}

						return true;
					}
				});

				popup.show();
			}
		});
		dialog.show();
	}
	
	private void deleteClassTeacher(Class c, int pos) {
		ArrayList<Student> new_students = new ArrayList<Student>();
		ArrayList<Integer> positions_to_delete = new ArrayList<Integer>();

		for (int a = 0; a < students.size(); a++) {
			if (students.get(a).getClassId() == c.getId()) {
				new_students.add(students.get(a));
				positions_to_delete.add(a);
			}
		}

		for (int a = 0; a < new_students.size(); a++) {
			StudentCursor delete_student = StudentLab.get(mClassContext).deleteStudent(new_students.get(a));
			Student s = delete_student.getStudentInfo();
			delete_student.close();
			deleteClassStudent(c, (int)(new_students.get(a).getId()), pos);
		}

		ExpandableListAdapter.this.notifyDataSetChanged();

		for (int a = 0; a < positions_to_delete.size(); a++) {
			students.remove(positions_to_delete.get(a));
		}

		deleteClass(c, pos);		
	}

	private void deleteClassStudent(Class c, int s_id, int pos) {
		// First we have to delete all the grades for that class.
		GradeCursor cursor = GradeLab.get(mClassContext).getGrades(c.getId(), s_id);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			GradeCursor delete_grade = GradeLab.get(mClassContext).deleteGrade(cursor.getGradeInfo());
			Grade g = delete_grade.getGradeInfo();
			delete_grade.close();
			cursor.moveToNext();
		}

		cursor.close();

		if (!isTeacher) {
			deleteClass(c, pos);
		}
	}

	private void deleteClass(Class c, int pos) {
		ClassCursor class_cursor = ClassLab.get(mClassContext).deleteClass(c);
		class_cursor.moveToFirst();
		Class deleted_class = class_cursor.getClassInfo();
		class_cursor.close();

		classes.remove(pos);

		ExpandableListAdapter.this.notifyDataSetChanged();
	}

	@SuppressLint({ "NewApi", "CutPasteId" })
	public void edit_class_dialog(final Class c) {
		LayoutInflater inflater = LayoutInflater.from(mClassContext);
		final View dialogView = inflater.inflate(R.layout.dialog_new_class, null);
		final ImageButton insertButton = (ImageButton) dialogView.findViewById(R.id.insertClass);
		final ImageButton cancelButton = (ImageButton) dialogView.findViewById(R.id.cancelClass);
		final Spinner gradingSpinner = (Spinner) dialogView.findViewById(R.id.gradeSpinner);
		final Spinner semesterSpinner;

		AlertDialog.Builder builder = new AlertDialog.Builder(mClassContext);
		final AlertDialog dialog = builder.create();

		semesterSpinner = (Spinner) dialogView.findViewById(R.id.semesterSpinner);
		gradingSpinner.setEnabled(false);
		
		ArrayList<String> entries = new ArrayList<String>();
		String entry = "";
		String[] semester_name_array = r.getStringArray(R.array.semesters);
		String currentYear = getCurrentYear();

		for (int a = 0; a < semester_name_array.length; a++) {
			entry = semester_name_array[a] + " " + currentYear; 
			entries.add(entry);
		}	

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mClassContext, 
				android.R.layout.simple_dropdown_item_1line, entries);

		semesterSpinner.setAdapter(adapter);


		/*
		 * We're going to set all the fields to the information that we 
		 * already have about the class
		 */
		Log.d("TEST", c.getClassName() + " " + c.getSemester() + " " + c.getGradingCategory());
		AutoCompleteTextView cn = (AutoCompleteTextView) dialogView.findViewById(R.id.autoCompClassName);
		Spinner ss = (Spinner) dialogView.findViewById(R.id.semesterSpinner);
		cn.setText(c.getClassName());

		boolean foundSemester = false;
		for (int a = 0; a < semester_name_array.length; a++) {
			if (c.getSemester().equals(entries.get(a))) {
				ss.setSelection(a);
				foundSemester = true;
			}
		}

		if (!foundSemester) {
			adapter.add(c.getSemester());
			semesterSpinner.setAdapter(adapter);
		}
		
		/*
		 * Set up saving the information that the user has changed or not changed.
		 * We're updating the database here.
		 */
		insertButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final AutoCompleteTextView className = (AutoCompleteTextView) 
						dialogView.findViewById(R.id.autoCompClassName);
				final Spinner semesterSpinner = (Spinner) 
						dialogView.findViewById(R.id.semesterSpinner);

				if (className.getText().toString().equals("")) {
					Toast.makeText(mClassContext, r.getString(R.string.no_class_title), Toast.LENGTH_SHORT).show();
				} else {
					Class newClass = c;

					newClass.setClassName(className.getText().toString());
					newClass.setSemester(semesterSpinner.getSelectedItem().toString());
					newClass.setUserId(user_id);
					
					ClassCursor cursor = ClassLab.get(mClassContext).updateClass(newClass);
					cursor.moveToFirst();
					Class updateClass = cursor.getClassInfo();
					cursor.close();

					classes.set(g, newClass);
					
					ExpandableListAdapter.this.notifyDataSetChanged();

					dialog.dismiss();
				}
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		dialog.setView(dialogView, 0, 0, 0, 0);
		dialog.show();
	}

	private void edit_student_dialog(final Class cl) {
		LayoutInflater inflater = LayoutInflater.from(mClassContext);
		final View dialogView = inflater.inflate(R.layout.dialog_new_student, null);
		final ImageButton insertButton = (ImageButton) dialogView.findViewById(R.id.insertStudent);
		final ImageButton cancelButton = (ImageButton) dialogView.findViewById(R.id.cancelStudent);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mClassContext);
		final AlertDialog dialog = builder.create();
		
		AutoCompleteTextView firstName = (AutoCompleteTextView)
				dialogView.findViewById(R.id.autoCompFirstName);
		firstName.setText(students.get(c).getFirstName());
		AutoCompleteTextView lastName = (AutoCompleteTextView)
				dialogView.findViewById(R.id.autoCompLastName);
		lastName.setText(students.get(c).getLastName());
		
		insertButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final AutoCompleteTextView firstName = (AutoCompleteTextView)
						dialogView.findViewById(R.id.autoCompFirstName);
				final AutoCompleteTextView lastName = (AutoCompleteTextView)
						dialogView.findViewById(R.id.autoCompLastName);
				
				if (lastName.getText().toString().equals("") || 
						firstName.getText().toString().equals("")) {
					Toast.makeText(mClassContext, r.getString(R.string.empty_name_string), Toast.LENGTH_SHORT).show();
				} else {
					Student updateStudent = students.get(c);
					updateStudent.setFirstName(firstName.getText().toString());
					updateStudent.setLastName(lastName.getText().toString());
					
					StudentCursor cursor = StudentLab.get(mClassContext).updateStudent(updateStudent);
					cursor.moveToFirst();
					Student s = cursor.getStudentInfo();
					cursor.close();
					
					ExpandableListAdapter.this.notifyDataSetChanged();
					students.set(c, updateStudent);
					dialog.dismiss();
				}
			}
		});
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.setView(dialogView, 0, 0, 0, 0);
		dialog.show();
	}
	
	private void deleteStudent() {
		StudentCursor deleted_student = StudentLab.get(mClassContext).deleteStudent(students.get(c));
		deleted_student.moveToFirst();
		Student deleted = deleted_student.getStudentInfo();
		deleted_student.close();
		students.remove(c);
		ExpandableListAdapter.this.notifyDataSetChanged();
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
		double currentTotalPercentage = 0; // Used to see how much of the 100% has been accounted for.

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
		 * Before we do anything else, let's get the total amount of percentage 
		 * that has already been entered. This way we can restrict the values even 
		 * more than allowing the user to enter more than 100%..for percentages. 
		 */
		for (int a = 0; a < grade_percentages.size(); a++) {
			int index = grade_percentages.get(a).indexOf(" ");
			String found_percentage = grade_percentages.get(a).substring(index+1);
			currentTotalPercentage += Integer.valueOf((int) (Double.valueOf(found_percentage) * 100));
		}
		DecimalFormat df = new DecimalFormat("0.00");

		final double percentage_limit = Double.valueOf(df.format(1.0 - (currentTotalPercentage / 100)));

		/*
		 * Time to work with this stuff!
		 */
		insert_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				double earned = 0.0, possible = 0.0, percentage = 0.0;

				/*
				 * Since we can't assign a final boolean inside the selection listener above,
				 * we need to run through again and check if the percentage already exists.
				 */
				boolean categoryFound = false;
				for (int a = 0; a < gradeList.size(); a++) {
					if (gradeList.get(a).getGradeCategory().equals(grade_type_spinner.getSelectedItem().toString())) {
						categoryFound = true;
					}
				}

				if (!grade_earned.getText().toString().equals("")) {
					earned = Double.valueOf(grade_earned.getText().toString());
				}

				if (grade_possible.getText().toString().equals("")) {
					Toast.makeText(mClassContext, r.getString(R.string.grade_possible_add_empty), Toast.LENGTH_SHORT).show();
				} else {
					possible = Double.valueOf(grade_possible.getText().toString());
				}

				if (isPercentage && !grade_percentage.getText().toString().equals("")) {
					percentage = Double.valueOf(grade_percentage.getText().toString());
					if (percentage >= 1) {
						percentage /= 100.0;
					}
				} else {
					percentage = 0.01;
				}				

				// Time to put this grade into the database.
				if (percentage_limit == 0.0 && !categoryFound) {
					Toast.makeText(mClassContext, R.string.cannot_enter_percents, Toast.LENGTH_LONG).show();
				} else if (percentage > percentage_limit && !categoryFound) {
					Toast.makeText(mClassContext, r.getString(R.string.upper_percentage_limit, String.valueOf(percentage_limit * 100)), Toast.LENGTH_LONG).show();
				} else if (percentage < 0.0 && !categoryFound) {
					Toast.makeText(mClassContext, r.getString(R.string.lower_percentage_limit, String.valueOf(percentage_limit * 100)), Toast.LENGTH_LONG).show();
				} else if (earned >= 0.0 && possible > 0.0 && ((percentage > 0.0 && percentage <= percentage_limit) || categoryFound)) {
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

	@SuppressLint("SimpleDateFormat")
	private String getCurrentYear() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		String year = format.format(new Date());
		return year;
	}
}