package com.donaldson.gradetracker.classlist;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.donaldson.gradetracker.fgc.FutureGradePercentage;
import com.donaldson.gradetracker.gpa.GPACalculatorActivity;
import com.donaldson.gradetracker.gradeutils.CalculateGrades;
import com.donaldson.gradetracker.optionsmenu.ActionItem;
import com.donaldson.gradetracker.optionsmenu.QuickAction;
import com.donaldson.gradetracker.purchases.IabHelper;
import com.donaldson.gradetracker.purchases.IabResult;
import com.donaldson.gradetracker.purchases.Inventory;
import com.donaldson.gradetracker.purchases.Purchase;
import com.donaldson.gradetracker.settings.SettingsActivity;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ClassListActivity extends Activity {
	public static final String TAG = "com.donaldson.gradetracker";
	public static final String ARG_USER_ID = "user_id";
	public static final int REQUEST_NEW_CLASS = 0;

	ExpandableListView classExpandList;
	//public Button generalAddButton;
	public ImageButton generalAddButton;

	private long user_id;
	private String userStatus;
	private Class expandedClass;
	private boolean isTeacher = false;
	private boolean addClass = true;
	private ArrayList<Class> classes = new ArrayList<Class>();
	public static ArrayList<Student> students = new ArrayList<Student>();
	private Class selected_FGC_class = null;
	public static ExpandableListAdapter expListAdapter = null;
	private ArrayList<Class> gpa_classes = new ArrayList<Class>();


	private String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnESwMkz0jdtEK/LBrTRiAf5zIR31ajyBZt1q4Vrl4USLkdh6eUK6oIz3Yeike6sUREIGcEF5qQlt3Dxh4eu3jgSkUMZOqse5hpumjiffpaXcXxCl3pFWeot3tNC2+yINHdPvwoYGo536/sJ55d6LlhbY3ZdUziTRnFCPjqny4+xnQW1MTjBee9tRYneNQJ6wXhAe4bUJt+fGrPvlhGrre/LXVqJWwQc1UesZDptTpTZe+b/OyRKPCbLd2hgvmkPJnZFnymsCAD+2A93bGHSvtwVjgnbQbBU+AAUTRnrKGJ3BiRfRFfBTjeR+B/CE2aLJXncPDNI5dxNC4bCRo1s5yQIDAQAB"; // My key from the developer console for the application.

	private IabHelper mPlayStoreHelper;
	private String SKU_FGC = "fgc_calc";
	private String SKU_GPA = "gpa_calc";
	private boolean mFGCUnlocked = false;
	private boolean mGPAUnlocked = false;
	private static final int FGC_REQUEST_CODE = 1;
	private static final int GPA_REQUEST_CODE = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_fragment);

		classExpandList = (ExpandableListView) findViewById(R.id.expandableListClass);
		generalAddButton = (ImageButton) findViewById(R.id.buttonGeneralAdd);

		final List<String> skuList = new ArrayList<String>();
		skuList.add(SKU_FGC);
		skuList.add(SKU_GPA);

		// Create a dialog that is shown until the query of the in-app purchases is finished.
		// This is needed so the app doesn't crash if the user clicks one of the in-app purchases
		// before they are completely loaded.
		LayoutInflater inflater = LayoutInflater.from(this);
		final View dialogView = inflater.inflate(R.layout.dialog_loading_database, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		/*
		 * Setup the connection to the play store
		 */
		mPlayStoreHelper = new IabHelper(this, base64EncodedPublicKey);
		mPlayStoreHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			@Override	
			public void onIabSetupFinished(IabResult result) {
				final AlertDialog load_dialog = builder.create();
				load_dialog.setView(dialogView, 0, 0, 0, 0);
				load_dialog.show();
				final long start = System.currentTimeMillis();

				if (result.isSuccess()) {
					mPlayStoreHelper.queryInventoryAsync(true, skuList, new IabHelper.QueryInventoryFinishedListener() {
						public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
							if (result.isFailure()) {
								return;
							}

							Log.d(TAG, "Successful Setup");

							// Get whether the item has been purchased
							mFGCUnlocked = inventory.hasPurchase(SKU_FGC);
							mGPAUnlocked = inventory.hasPurchase(SKU_GPA);

							// Dismiss the loading dialog.
							load_dialog.dismiss();
						}
					});
				}
			}
		});

		initialLoad();

		user_id = getIntent().getLongExtra(ARG_USER_ID, -1L);
		userStatus = UserLab.get(getApplicationContext()).selectUser(user_id).getStatus();

		if (userStatus.equals("teacher")) {
			isTeacher = true;
		} else {
			isTeacher = false;
			addClass = true;
		}


		setUpClasses();

		expListAdapter = new ExpandableListAdapter(this, classes, isTeacher);
		classExpandList.setAdapter(expListAdapter);
		classExpandList.setGroupIndicator(null);

		classExpandList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {

				for (int a = 0; a < classExpandList.getAdapter().getCount(); a++) {
					if (a != groupPosition) {
						classExpandList.collapseGroup(a);
					}
				}

				String gradingSystem = classes.get(groupPosition).getGradingCategory();
				if (gradingSystem.equals("Percentage")) {
					expListAdapter.setIsPercentage(true);
				} else {
					expListAdapter.setIsPercentage(false);
				}

				if (isTeacher) {
					addClass = false;
					findStudents(classes.get(groupPosition).getId());
					expandedClass = classes.get(groupPosition);
				}
			}
		});

		classExpandList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
			@Override
			public void onGroupCollapse(int groupPosition) {
				addClass = true;
				students.clear();
			}
		});

		generalAddButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				createQuickActionMenu(v);
			}
		});
	}

	private void createQuickActionMenu(View v) {
		final QuickAction mQuickAction 	= new QuickAction(this);

		//Add action item
		ActionItem addAction = new ActionItem();
		addAction.setIcon(getResources().getDrawable(R.drawable.ic_add));
		mQuickAction.addActionItem(addAction);

		if (!isTeacher) {
			//Accept action item
			ActionItem accAction = new ActionItem();

			if (mFGCUnlocked) {
				accAction.setIcon(getResources().getDrawable(R.drawable.future));
			} else {
				accAction.setIcon(getResources().getDrawable(R.drawable.future_locked));
			}

			//Upload action item
			ActionItem upAction = new ActionItem();

			if (mGPAUnlocked) {
				upAction.setIcon(getResources().getDrawable(R.drawable.gpa));
			} else {
				upAction.setIcon(getResources().getDrawable(R.drawable.gpa_locked));
			}

			mQuickAction.addActionItem(accAction);
			mQuickAction.addActionItem(upAction);
		}

		if (!addClass && isTeacher) {
			ActionItem stuAction = new ActionItem();
			stuAction.setIcon(getResources().getDrawable(R.drawable.student));

			mQuickAction.addActionItem(stuAction);
		}

		// Settings action item
		ActionItem setAction = new ActionItem();
		setAction.setIcon(getResources().getDrawable(R.drawable.settings));
		mQuickAction.addActionItem(setAction);

		//setup the action item click listener
		mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {			
			@Override
			public void onItemClick(int pos) {

				if (pos == 0) { 
					if (addClass) { // Add Class (student/teacher)
						createDialog(R.layout.dialog_new_class, R.id.insertClass,
								R.id.cancelClass);
					} else {
						Toast.makeText(getApplicationContext(), "Unable to create a class. Please close the open class.", Toast.LENGTH_SHORT).show();
					}
				} else if (pos == 1) { // Add student(teacher), Future Grade(student), or settings(teacher)
					if (!addClass && isTeacher) {
						createDialog(R.layout.dialog_new_student, R.id.insertStudent,
								R.id.cancelStudent);
					} else if (!isTeacher) {
						if (mFGCUnlocked) {
							createFGCDialog();
						} else {
							purchaseItem(SKU_FGC, FGC_REQUEST_CODE);
						}
					} else {
						openSettings();
					}

				} else if (pos == 2) { // GPA(student) or settings(teacher)
					if (!isTeacher) {
						if (mGPAUnlocked) {
							createGPADialog();
						} else {
							purchaseItem(SKU_GPA, GPA_REQUEST_CODE);
						}
					} else {
						openSettings();
					}
				} else if (pos == 3) { // Settings (student)
					openSettings();
				}
			}
		});

		mQuickAction.show(v);
		mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_RIGHT);
	}

	private void purchaseItem(String item, int requestCode) {
		mPlayStoreHelper.launchPurchaseFlow(this, item, requestCode, new IabHelper.OnIabPurchaseFinishedListener() {
			@Override
			public void onIabPurchaseFinished(IabResult result, Purchase info) {
				// TODO Auto-generated method stub
				// Don't have to do anything since we're not consuming the item
				// If we want to consume an item, this is where we would do that at.
				if (result.isFailure()) {
					Log.d("TAG", "Error purchase: " + result);
					Toast.makeText(getApplicationContext(), R.string.error_purchase, Toast.LENGTH_SHORT).show();
					return;
				} else if (info.getSku().equals(SKU_FGC)) {
					mFGCUnlocked = true;
					return;
				} else if (info.getSku().equals(SKU_GPA)) {
					mGPAUnlocked = true;
					return;
				}
			}
		});
	}

	@Override
	public void onDestroy() {
		if (mPlayStoreHelper != null) {
			mPlayStoreHelper.dispose();
		}
		mPlayStoreHelper = null;
		super.onDestroy();
	}

	private void createGPADialog() {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View dialogView = inflater.inflate(R.layout.dialog_gpa, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		final ListView list_classes = (ListView) dialogView.findViewById(R.id.gpa_list_view);
		final ArrayList<String> class_names = new ArrayList<String>();

		for (int a = 0; a < classes.size(); a++) {
			class_names.add(classes.get(a).getClassName());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, class_names);
		list_classes.setAdapter(adapter);
		dialog.setView(dialogView, 0, 0, 0, 0);
		dialog.show();

		ImageButton imgBtnCalculate = (ImageButton) dialogView.findViewById(R.id.imgBtnCalculate);
		imgBtnCalculate.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				gpa_classes.clear();
				SparseBooleanArray checkedItems = list_classes.getCheckedItemPositions();

				for (int i=0; i<checkedItems.size(); i++) {
					if (checkedItems.valueAt(i)) {
						int position = (int) list_classes.getAdapter().getItemId(checkedItems.keyAt(i)); // Get the position in the adapter
						gpa_classes.add(classes.get(position));
					}
				}

				ArrayList classes = new ArrayList();
				for (int a = 0; a < gpa_classes.size(); a++) {
					classes.add(new Class(gpa_classes.get(a)));
				}

				Toast.makeText(getApplicationContext(), String.valueOf(checkedItems.size()), Toast.LENGTH_SHORT).show();

				if (checkedItems.size() > 0) {
					dialog.dismiss();
					Intent i = new Intent(getApplicationContext(), GPACalculatorActivity.class);
					i.putParcelableArrayListExtra(GPACalculatorActivity.ARGS_LIST_CLASSES, classes);
					startActivity(i);
				} else {
					Toast.makeText(getApplicationContext(), R.string.no_selected_gpa, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void createFGCDialog() {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View dialogView = inflater.inflate(R.layout.dialog_fgc, null);

		final ListView list_classes = (ListView) dialogView.findViewById(R.id.fgc_list_view);
		final ArrayList<String> class_names = new ArrayList<String>();
		
		for (int a = 0; a < classes.size(); a++) {
			class_names.add(classes.get(a).getClassName() + " - " + classes.get(a).getOverallGrade() + "%");
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, class_names);
		list_classes.setAdapter(adapter);

		final EditText editGradePossible = (EditText) dialogView.findViewById(R.id.editGradePossible);
		final EditText editGradeDesired = (EditText) dialogView.findViewById(R.id.editGradeDesired);
		final ImageButton btnCalculate = (ImageButton) dialogView.findViewById(R.id.calculateFGCButton);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		dialog.setView(dialogView, 0, 0, 0, 0);
		dialog.show();

		list_classes.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos,
					long arg3) {
				selected_FGC_class = classes.get(pos);	
			}
		});

		btnCalculate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (selected_FGC_class != null) {
					double desired, possible_desired;

					if (!editGradeDesired.getText().toString().equals("") && !editGradePossible.getText().toString().equals("")) {
						desired = Double.valueOf(editGradeDesired.getText().toString());
						possible_desired = Double.valueOf(editGradePossible.getText().toString());

						
						if (desired > 100.0 || desired <= 0.0) {
							Toast.makeText(getApplicationContext(), R.string.incorrect_desired_grade, Toast.LENGTH_SHORT).show();
						} else if (selected_FGC_class == null) {
							Toast.makeText(getApplicationContext(), R.string.no_selected_class, Toast.LENGTH_SHORT).show();
						} else if (possible_desired <= 0.0) { 
							Toast.makeText(getApplicationContext(), R.string.incorrect_possible_grade, Toast.LENGTH_SHORT).show();
						} else if (selected_FGC_class.getOverallGrade() == -1.0) {
							Toast.makeText(getApplicationContext(), R.string.class_grade_is_zero, Toast.LENGTH_LONG).show();
						} else {
							dialog.dismiss();
							ArrayList<Grade> grades = new ArrayList<Grade>();
							grades.addAll(getGradesFromCursor());

							if (selected_FGC_class.getGradingCategory().equals("Percentage")) {
								ArrayList<FutureGradePercentage> categories = new ArrayList<FutureGradePercentage>();

								for (int a = 0; a < grades.size(); a++) {
									if (categories.size() == 0) {
										categories.add(new FutureGradePercentage(grades.get(a).getGradeCategory(), 0, 0, grades.get(a).getPercentage()));
									} else {
										boolean found = false;
										for (int b = 0; b < categories.size(); b++) {
											if (categories.get(b).getCategory().equals(grades.get(a).getGradeCategory())) {
												found = true;
												break;
											}
										}

										if (!found) {
											categories.add(new FutureGradePercentage(grades.get(a).getGradeCategory(), 0, 0, grades.get(a).getPercentage()));
										}
									}
								}

								double earned = 0, possible = 0, totalPercentage = 0;
								for (int a = 0; a < categories.size(); a++) {
									earned = possible = 0;

									for (int b = 0; b < grades.size(); b++) {
										if (categories.get(a).getCategory().equals(grades.get(b).getGradeCategory())) {
											earned += grades.get(b).getEarnedGrade();
											possible += grades.get(b).getPossibleGrade();
										}
									}

									categories.get(a).setEarned(earned);
									categories.get(a).setPossible(possible);
									totalPercentage += categories.get(a).getPercentage();
								}

								double inc_total = 0;
								ArrayList<String> category_grade = new ArrayList<String>();
								for (int a = 0; a < categories.size(); a++) {
									for (int b = 0; b < categories.size(); b++) {
										if (a != b) {
											inc_total += (categories.get(b).getEarned() / categories.get(b).getPossible()) * categories.get(b).getPercentage();
										}
									}

									double new_possible = categories.get(a).getPossible() + possible_desired;

									DecimalFormat df = new DecimalFormat("0.00");
									double new_total = Double.valueOf(df.format(CalculateGrades.calculate_future_percentage(new_possible, categories.get(a).getEarned(), 
											categories.get(a).getPercentage(), desired, totalPercentage, inc_total)));

									double new_percentage_total = Double.valueOf(df.format((new_total / possible_desired) * 100));
									category_grade.add(categories.get(a).getCategory() + "\n\t- " + new_total + " / " + possible_desired + "\n\t- " + new_percentage_total + "%");
									inc_total = 0;
								}

								displayFutureGradePercentage(category_grade, desired);

							} else {
								double earned = 0, possible = 0, neededGrade = 0;
								for (int a = 0; a < grades.size(); a++) {
									earned += grades.get(a).getEarnedGrade();
									possible += grades.get(a).getPossibleGrade();
								}

								neededGrade = CalculateGrades.calculate_future_points(earned, possible, desired, possible_desired);
								displayFutureGradePoint(neededGrade, possible_desired, desired);
							}
						}
					} else {
						Toast.makeText(getApplicationContext(), R.string.empty_fgc_field, Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), R.string.no_selected_future, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onResume() {
		classes.clear();
		setUpClasses();

		expListAdapter.insertAllClasses(classes);
		expListAdapter.notifyDataSetChanged();

		for (int a = 0; a < classExpandList.getAdapter().getCount(); a++) {
			classExpandList.collapseGroup(a);
		}

		super.onResume();
	}

	private ArrayList<Grade> getGradesFromCursor() {
		ArrayList<Grade> grades = new ArrayList<Grade>();
		GradeCursor cursor = GradeLab.get(getApplicationContext()).getGrades(selected_FGC_class.getId(),
				selected_FGC_class.getUserId(), false);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			grades.add(cursor.getGradeInfo());
			cursor.moveToNext();
		}
		cursor.close();

		return grades;
	}

	private void displayFutureGradePercentage(ArrayList<String> grades, double desired) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View dialogView = inflater.inflate(R.layout.dialog_fgc_percentage, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, grades);
		ListView list_options = (ListView) dialogView.findViewById(R.id.listViewPossibilities);
		list_options.setAdapter(adapter);

		TextView txtTitle = (TextView) dialogView.findViewById(R.id.txtFGCPercentageTitle);
		txtTitle.setText(txtTitle.getText().toString() + " for " + selected_FGC_class.getClassName() + "\nto have " + desired + "%");
		ImageButton imgBtn = (ImageButton) dialogView.findViewById(R.id.cancel_fgc_percentage);
		imgBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		dialog.setView(dialogView, 0, 0, 0, 0);
		dialog.show();
	}

	private void displayFutureGradePoint(double needed, double desired_possible, double desired) {
		DecimalFormat df = new DecimalFormat("0.00");
		double needed_percentage = Double.valueOf(df.format((needed / desired_possible) * 100));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.future_title)  + " for " + selected_FGC_class.getClassName() + " to earn " + desired + "%");
		builder.setMessage(getString(R.string.desired_message) + "\n\t- " + needed + " / " + desired_possible + "\n\t- " + needed_percentage + "%");
		builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void findStudents(long class_id) {
		StudentCursor cursor = StudentLab.get(getApplicationContext()).getStudentsInClass(class_id);
		cursor.moveToFirst();

		clearStudents();

		while (!cursor.isAfterLast()) {
			students.add(cursor.getStudentInfo());
			cursor.moveToNext();
		}

		cursor.close();

		for (int a = 0; a < students.size(); a++) {
			Log.d("TEST", students.get(a).getOverallGrade() + "");
		}

		expListAdapter.insertAllStudents(students);
		expListAdapter.notifyDataSetChanged();
	}

	public void clearStudents() {
		if (students.size() > 0 ) {
			students.clear();
		}
	}

	/*
	 * This function gets all the classes out of the cursor and places them into the
	 * ListArray classes so that we can use them for the expandable list view
	 */
	public void setUpClasses() {
		ClassCursor cursor = ClassLab.get(getApplicationContext()).getClasses(user_id);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			classes.add(cursor.getClassInfo());
			cursor.moveToNext();
		}

		cursor.close();
	}

	private void initialLoad() {
		scaleExpandableListView();
		scaleAddButton();
	}

	/* 
	 * This function is used to scale the expandable list view according to
	 * the size of the screen. This is necessary so that static buttons can exist 
	 * to add a new class or student to a specific class.
	 */
	private void scaleExpandableListView() {
		long height = getLayoutHeight();
		classExpandList.getLayoutParams().height = (int) (height * 0.75);
		classExpandList.setPadding(0, (int)(height*0.025), 0, (int)(height*0.025));
	}

	/*
	 * Scale the add button to the rest of the screen after the expandable list has
	 * has sized accordingly.
	 */
	private void scaleAddButton() {
		long height = getLayoutHeight();
		generalAddButton.getLayoutParams().height = (int) (height * 0.15);
		generalAddButton.setPadding(0, (int)(height*0.025), 0, (int)(height*0.025));
	}

	/*
	 * Sets the text of the button according to whether the expandable list is 
	 * expanded or not.
	 */
	/*private void setButtonText(String btnText) {
		generalAddButton.setText(btnText);
	}*/

	private long getLayoutHeight() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}

	@SuppressLint("NewApi")
	public void createDialog(int layoutId, int insertId, int cancelId) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View dialogView = inflater.inflate(layoutId, null);
		final ImageButton insertButton = (ImageButton) dialogView.findViewById(insertId);
		final ImageButton cancelButton = (ImageButton) dialogView.findViewById(cancelId);
		final Spinner semesterSpinner;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		/*
		 * We only need to create the spinner stuff if the user is creating a new class. 
		 * We don't need to go through this work if the user is entering a student since 
		 * they don't need to enter what semester the student is in.
		 */
		if (addClass) {
			semesterSpinner = (Spinner) dialogView.findViewById(R.id.semesterSpinner);

			ArrayList<String> entries = new ArrayList<String>();
			String entry = "";
			String[] semester_name_array = getResources().getStringArray(R.array.semesters);
			String currentYear = getCurrentYear();

			for (int a = 0; a < semester_name_array.length; a++) {
				entry = semester_name_array[a] + " " + currentYear; 
				entries.add(entry);
			}	

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
					android.R.layout.simple_dropdown_item_1line, entries);

			semesterSpinner.setAdapter(adapter);
		}

		insertButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (addClass) { // if we're adding a class
					final AutoCompleteTextView className = (AutoCompleteTextView) 
							dialogView.findViewById(R.id.autoCompClassName);
					final Spinner semesterSpinner = (Spinner) 
							dialogView.findViewById(R.id.semesterSpinner);
					final Spinner gradingSpinner = (Spinner)
							dialogView.findViewById(R.id.gradeSpinner);

					if (className.getText().toString().equals("")) {
						Toast.makeText(getApplicationContext(), getString(R.string.no_class_title), Toast.LENGTH_SHORT).show();
					} else {
						Class newClass = new Class();

						newClass.setClassName(className.getText().toString());
						newClass.setSemester(semesterSpinner.getSelectedItem().toString());
						newClass.setGradingCategory(gradingSpinner.getSelectedItem().toString());
						newClass.setUserId(user_id);

						long id = ClassLab.get(getApplicationContext()).insertClass(newClass);
						newClass.setId(id);
						classes.add(newClass);

						expListAdapter.insertNewClass(newClass);
						expListAdapter.notifyDataSetChanged();

						dialog.cancel();
					}
				} else { // if we're adding a student
					final AutoCompleteTextView firstName = (AutoCompleteTextView)
							dialogView.findViewById(R.id.autoCompFirstName);
					final AutoCompleteTextView lastName = (AutoCompleteTextView)
							dialogView.findViewById(R.id.autoCompLastName);

					if (lastName.getText().toString().equals("") || 
							firstName.getText().toString().equals("")) {
						Toast.makeText(getApplicationContext(), getString(R.string.empty_name_string), Toast.LENGTH_SHORT).show();
					} else {
						Student newStudent = new Student();

						newStudent.setClassId(expandedClass.getId());
						newStudent.setFirstName(firstName.getText().toString());
						newStudent.setLastName(lastName.getText().toString());
						newStudent.setUserId(user_id);

						long id = StudentLab.get(getApplicationContext()).insertStudent(newStudent);
						newStudent.setId(id);
						students.add(newStudent);

						findStudents(expandedClass.getId());

						dialog.cancel();
					}
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

	@SuppressLint("SimpleDateFormat")
	private String getCurrentYear() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		String year = format.format(new Date());
		return year;
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.logout_title);
		builder.setMessage(R.string.logout_message);
		builder.setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ClassListActivity.super.onBackPressed();
			}
		});

		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void openSettings() {
		Intent i = new Intent(this, SettingsActivity.class);
		i.putExtra(ARG_USER_ID, user_id);
		startActivity(i);
	}
}
