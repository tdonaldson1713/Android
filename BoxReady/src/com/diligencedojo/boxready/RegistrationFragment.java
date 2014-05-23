package com.diligencedojo.boxready;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class RegistrationFragment extends Fragment {

	public static final String TAG = "RegistrationFragment";

	private EditText mAge;
	private RadioGroup sexRadGroup;
	private String ageString;
	private Integer spinnerChoice;
	private Spinner flSpinner;
	String[] fitLevArray;
	String[] repsChanged;
	Integer sex;
	Integer age;
	Integer fitnessLevel;
	RadioGroup saveRadio;
	String saveAge;
	Integer saveFitnessLevel;
	String FILE_NAME;
	String savedSex;
	String savedAge;
	String savedFitnessLevel;
	private static boolean regComplete = false;

	// ****************************************************************************************************//
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	// ****************************************************************************************************//
	// create the Registration View and validate/save user inputs
	// ****************************************************************************************************//
	// From manifest****(takes away action bar)
	// android:theme="@android:style/Theme.NoTitleBar.Fullscreen"
	// @TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {

		View v = inflater
				.inflate(R.layout.fragment_registration, parent, false);

		// makes action bar appear with no text (title)
		getActivity().getActionBar().setDisplayShowTitleEnabled(false);

		// FITNESS LEVEL SPINNER*******//
		// assign value to Spinner object from Registration View
		flSpinner = (Spinner) v.findViewById(R.id.fitnessSpinner);

		// Create an ArrayAdapter using the string array and a default spinner
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				v.getContext(), R.array.fitness_level_spinner_array,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		flSpinner.setAdapter(adapter);

		// FINALIZE REGISTRATION BUTTON********//
		Button mFinalizeReg = (Button) v.findViewById(R.id.finalize_reg_info);
		sexRadGroup = (RadioGroup) v.findViewById(R.id.radioSexGroup);
		mAge = (EditText) v.findViewById(R.id.editAge);
		fitLevArray = getResources().getStringArray(
				R.array.fitness_level_spinner_array);

		// PROBLEM HERE RETRIEVING AND SETTING VISIBILITY OF RADIO BUTTONS
		// Retrieving saved registration values
		// ***************************************
		// retrieve saved sex (1 = male and 0 = female)
		// if (!getPref("ageKey", v.getContext()).equals(null)) {
		// savedSex = getPref("sexKey", v.getContext());
//		if (!savedSex.isEmpty()) {
//			int sex = Integer.parseInt(savedSex);
//			if (sex == 1) {
//				RadioButton male = (RadioButton) v
//						.findViewById(R.id.maleRadioButton);
//				male.setPressed(isVisible());
//				regComplete = true;
//			} else if (sex == 0) {
//				RadioButton female = (RadioButton) v
//						.findViewById(R.id.femaleRadioButton);
//				female.setPressed(isVisible());
//				regComplete = true;
//			}
//		} else
//			regComplete = false;
//		// }
//
//		// retrieve saved age
//		savedAge = getPref("ageKey", v.getContext());
//		if (!savedAge.isEmpty()) {
//			// age = Integer.parseInt(savedAge);
//			mAge.setText(savedAge);
//			regComplete = true;
//		} else
//			regComplete = false;
//
//		// retrieve saved fitness level
//		savedFitnessLevel = getPref("fitnessKey", v.getContext());
//		if (!savedFitnessLevel.isEmpty()) {
//			int flPos = Integer.parseInt(savedFitnessLevel);
//			// fitnessLevel = flPos;
//			flSpinner.setSelection(flPos);
//			regComplete = true;
//		} else
//			regComplete = false;

		// *************************************************************//
		mFinalizeReg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean regValidated = false;

				// *******SEX*******//
				// get selected radio button from radioGroup
				int selectedId = sexRadGroup.getCheckedRadioButtonId();

				// *****sets values of view FOR TESTING PURPOSES*******//
				 selectedId = R.id.maleRadioButton;
				 sex = 1;
				 ageString = Integer.toString(55);
				 age = 55;
				 spinnerChoice = flSpinner.getSelectedItemPosition();
				 spinnerChoice = 1;
				 fitnessLevel = 1;
				 regValidated = true;
				// //**************************************************//

//				if (selectedId == -1) {
//					// no radio button selected
//					Toast.makeText(v.getContext(), "Choose Sex",
//							Toast.LENGTH_SHORT).show();
//					regValidated = false;
//				} else if (selectedId == R.id.maleRadioButton) {
//					sex = 1;
//					savedSex = Integer.toString(sex);
//					putPref("sexKey", savedSex, v.getContext());
//					// putPref("sexKey", String.valueOf(selectedId),
//					// v.getContext());
//					regValidated = true;
//					regComplete = true;
//				} else if (selectedId == R.id.femaleRadioButton) {
//					// Log.i(TAG, "Chosen Sex: " + selectedId);
//					// Toast.makeText(v.getContext(), "Female",
//					// Toast.LENGTH_SHORT).show();
//					sex = 0;
//					savedSex = Integer.toString(sex);
//					putPref("sexKey", savedSex, v.getContext());
//					// putPref("sexKey", String.valueOf(selectedId),
//					// v.getContext());
//					regValidated = true;
//					regComplete = true;
//				} // *******END SEX VALIDATION*******//
//
//				// *******AGE*******//
//				// get age entered in EditText box
//				ageString = mAge.getText().toString();
//
//				if (regValidated == true) {
//					if (ageString.matches("")) {
//						// no age entered
//						Toast.makeText(v.getContext(), "Enter Age",
//								Toast.LENGTH_SHORT).show();
//						regValidated = false;
//					} else {
//						age = Integer.parseInt(mAge.getText().toString());
//						putPref("ageKey", ageString, v.getContext());
//						// Toast.makeText(v.getContext(), ageString,
//						// Toast.LENGTH_SHORT).show();
//						regValidated = true;
//						regComplete = true;
//					}
//				} // *******END AGE VALIDATION*******//
//
//				// *******FITNESS LEVEL*******//
//				// get selection from fitness level spinner unless it is in the
//				// first element of the array (that is the -select one- display)
//				spinnerChoice = flSpinner.getSelectedItemPosition();
//
//				if (regValidated == true) {
//					if (spinnerChoice == 0) {
//						Toast.makeText(v.getContext(), "Select Fitness Level",
//								Toast.LENGTH_SHORT).show();
//						regValidated = false;
//					} else {
//						// Toast.makeText(v.getContext(), spinnerString,
//						// Toast.LENGTH_SHORT).show();
//						fitnessLevel = spinnerChoice;
//						putPref("fitnessKey", Integer.toString(fitnessLevel),
//								v.getContext());
//						// String savedFl = getPref("fitnessKey",
//						// v.getContext());
//						// Log.d("Fitness Level: ", savedFl);
//						regValidated = true;
//						regComplete = true;
//					}
//				} // *******END FITNESS LEVEL VALIDATION*******//

				// if all fields have been completed, switch to the movement
				// gallery view
				// (-> MovementSelectionActivity -> MovementSelectionFragment)
				// ************************************************************************
				// Log.d("Sex: ", Integer.toString(sex));
				// Log.d("Age: ", Integer.toString(age));
				// Log.d("Fitness Level: ", Integer.toString(fitnessLevel));

				if (regValidated == true) {
					repsChanged = regConditions(sex, age, fitnessLevel);
					// for (int i = 0; i < repsChanged.length; i++) {
					// Log.d("repsChanged", "new in: " + repsChanged[i]);
					// }

					Intent regIntent = new Intent(v.getContext(),
							AppExplanationActivity.class);
					regIntent.putExtra("repsAdj", repsChanged);
					onIntentChange(regIntent);
				}

			}// *******END onClick*******************************************//

		});// *******END setOnClickListener**********************************//

		setHasOptionsMenu(true);

		return v;
	}// *******END onCreateView*********************************************//

	// ****************************************************************************************************//
	// the next 2 functions are for saving and retrieving the registration info
	// ****************************************************************************************************//

	public static void putPref(String key, String value, Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getPref(String key, Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferences.getString(key, "");
	}

	// ****************************************************************************************************//
	// this function switches to the view to the application explanation
	// ****************************************************************************************************//

	public void onIntentChange(Intent regIntent) {
		startActivityForResult(regIntent, 0);
	}

	// ****************************************************************************************************//
	// the next two functions control the Upgrade button on the action bar
	// ****************************************************************************************************//
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.upgrade_action_bar, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.upgrade_action_bar:
			// Log.d("upgradeApp", "pressed!");
			upgradeApp();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}// End onOptionsItemSelected

	// this will transfer the user to the upgrade screen
	private void upgradeApp() {
		Log.d("upgradeApp", "pressed!");

		// Intent upgradeIntent = new Intent();
	}

	// ****************************************************************************************************//
	// this function handles the registration condition settings
	// ****************************************************************************************************//
	public String[] regConditions(Integer sex, Integer age, Integer fitnessLevel) {

		// for access to the base-value rep array
		Movement m = new Movement();
		String[] modArray = new String[m.reps.length];
		Double adjustment;

		for (int i = 0; i < modArray.length; i++) {

			if (i == 6) { // flag for run
				modArray[i] = "9999";
			} else {
				modArray[i] = m.reps[i];
			}
		}

		// check for male**********
		// *************************//
		if (sex == 1) { // 1 = male?
			// 100% of base-value rep array

			// conditions for age of male
			// **************************//
			if (age < 35) {
				repsChanged = modArray;
			} else if (age > 34 && age < 45) {
				adjustment = 0.9;
				repsChanged = adjustReps(adjustment, modArray);
				for (int i = 0; i < repsChanged.length; i++) {
					// Log.d("35-44", "new in: " + repsChanged[i]);
				}
			} else if (age > 44 && age < 55) {
				adjustment = 0.8;
				repsChanged = adjustReps(adjustment, modArray);
				for (int i = 0; i < repsChanged.length; i++) {
					// Log.d("45-54", "new in: " + repsChanged[i]);
				}
			} else { // age == 55+
				adjustment = 0.75;
				repsChanged = adjustReps(adjustment, modArray);
				for (int i = 0; i < repsChanged.length; i++) {
					// Log.d("55+", "new in: " + repsChanged[i]);
				}
			}

			// conditions for fitness level of male
			// ************************************//
			if (fitnessLevel == 1) { // out of shape
				adjustment = 0.9;
				repsChanged = adjustReps(adjustment, repsChanged);
				for (int i = 0; i < repsChanged.length; i++) {
					// Log.d("out of shape", "new in: " + repsChanged[i]);
				}
			} else if (fitnessLevel == 2) { // avg shape
				for (int i = 0; i < repsChanged.length; i++) {
					// 100% of reps
					// Log.d("out of shape", "new in: " + repsChanged[i]);
				}
			} else {// it can only equal 3 //
				adjustment = 1.1;
				repsChanged = adjustReps(adjustment, modArray);
				for (int i = 0; i < repsChanged.length; i++) {
					// Log.d("out of shape", "new in: " + repsChanged[i]);
				}
			}
		}

		// check for female**********
		// *************************//
		if (sex == 0) { // 0 = female?
			// 60% of base-value rep array
			adjustment = 0.6;
			repsChanged = adjustReps(adjustment, modArray);

			// conditions for age of female
			// **************************//
			if (age < 35) {
				for (int i = 0; i < repsChanged.length; i++) {
					// Log.d("female", "new in: " + repsChanged[i]);
				}
			} else if (age > 34 && age < 45) {
				adjustment = 0.9;
				repsChanged = adjustReps(adjustment, modArray);
				for (int i = 0; i < repsChanged.length; i++) {
					// Log.d("f35-44", "new in: " + repsChanged[i]);
				}
			} else if (age > 44 && age < 55) {
				adjustment = 0.8;
				repsChanged = adjustReps(adjustment, modArray);
				for (int i = 0; i < repsChanged.length; i++) {
					// Log.d("f45-54", "new in: " + repsChanged[i]);
				}
			} else {// age == 55+
				adjustment = 0.75;
				repsChanged = adjustReps(adjustment, modArray);
				for (int i = 0; i < repsChanged.length; i++) {
					// Log.d("f55+", "new in: " + repsChanged[i]);
				}
			}

			// conditions for fitness level of female
			// ************************************//
			if (fitnessLevel == 1) { // out of shape?
				adjustment = 0.9;
				repsChanged = adjustReps(adjustment, repsChanged);
				for (int i = 0; i < repsChanged.length; i++) {
					// Log.d("fout of shape", "new in: " + repsChanged[i]);
				}
			} else if (fitnessLevel == 2) { // avg shape?
				for (int i = 0; i < repsChanged.length; i++) {
					// 100% of reps
					// Log.d("fout of shape", "new in: " + repsChanged[i]);
				}
			} else {// it can only equal 3 //
				adjustment = 1.1;
				repsChanged = adjustReps(adjustment, modArray);
				for (int i = 0; i < repsChanged.length; i++) {
					// Log.d("fout of shape", "new in: " + repsChanged[i]);
				}
			}

		}

		return repsChanged;
	}

	// ****************************************************************************************************//

	public String[] adjustReps(Double adjustment, String[] modArray) {

		Double[] dubModArray = new Double[modArray.length];
		Integer[] intArray = new Integer[modArray.length];

		// flag the run and change all values to doubles
		for (int i = 0; i < modArray.length; i++) {

			if (i == 6) {
				modArray[i] = "9999"; // flag run element
				dubModArray[i] = Double.valueOf(modArray[i]);
			} else {
				dubModArray[i] = Double.valueOf(modArray[i]);
			}
		}

		// replace flag and fill modArray w modified reps
		for (int i = 0; i < modArray.length; i++) {

			if (i == 6) {
				modArray[i] = "1/4 mile";
			} else {
				// adjust reps
				dubModArray[i] = dubModArray[i] * adjustment;
				// round values and put in int array
				intArray[i] = (int) Math.round(dubModArray[i]);
				// change to string
				modArray[i] = Integer.toString(intArray[i]);
			}
		}
		return modArray;
	}
}
