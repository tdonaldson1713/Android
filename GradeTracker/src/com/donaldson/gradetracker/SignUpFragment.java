package com.donaldson.gradetracker;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.donaldson.gradetracker.DatabaseHelper.UserCursor;

public class SignUpFragment extends Fragment {
	String userName;
	String userPassword;
	String userEmail;
	String userStatus;
	String userPhoneNumber;
	long id;
	UserAccount mUser;
	UserLab mUserLab;

	EditText editPassword;
	EditText editUsername;
	EditText editEmail;
	EditText editPhone;
	RadioButton radioTeacher;
	RadioButton radioStudent;
	ImageButton imgBtnFinalize;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mUserLab = UserLab.get(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_sign_up, parent, false);

		editUsername = (EditText) v.findViewById(R.id.editUsername);
		editPassword = (EditText) v.findViewById(R.id.editPassword);
		editEmail = (EditText) v.findViewById(R.id.editEmail);
		editPhone = (EditText) v.findViewById(R.id.editPhoneNumber);
		radioTeacher = (RadioButton) v.findViewById(R.id.radioTeacher);
		radioStudent = (RadioButton) v.findViewById(R.id.radioStudent);
		imgBtnFinalize = (ImageButton) v.findViewById(R.id.imgBtnFinalize);

		editEmail.setVisibility(EditText.INVISIBLE);
		editPhone.setVisibility(EditText.INVISIBLE);

		radioTeacher.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (radioStudent.isChecked()) {
					radioStudent.setChecked(false);
				}
				radioTeacher.setChecked(true);
				userStatus = radioTeacher.getText().toString();
			}
		});

		radioStudent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (radioTeacher.isChecked()) {
					radioTeacher.setChecked(false);
				}
				radioStudent.setChecked(true);
				userStatus = radioStudent.getText().toString();
			}
		});

		imgBtnFinalize.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!radioStudent.isChecked() && !radioTeacher.isChecked()) {
					Toast.makeText(getActivity(), R.string.radio_warning, Toast.LENGTH_SHORT).show();
				} else {
					if (getUsers()) {
						Toast.makeText(getActivity(), R.string.existing_users, Toast.LENGTH_SHORT).show();
					} else {
						if (checkValidity()) {
							saveUserInfo();
							id = mUserLab.insertUser(mUser);
							mUser.setId(id);
							destroyFragment();

							// This is included so that the user auto-logs in after sign up
							// instead of returning back to the start-up screen.
							Intent i = new Intent(getActivity(), ClassListActivity.class);
							i.putExtra(ClassListActivity.ARG_USER_ID, mUser.getId());
							startActivity(i);
						} else {
							Toast.makeText(getActivity(), R.string.invalid_characters, Toast.LENGTH_SHORT).show();
						}
						
					}
				}
			}
		});

		return v;
	}

	private boolean checkValidity() {
		String u, p;
		u = editUsername.getText().toString();
		p = editPassword.getText().toString();
		
		for (int a = 0; a < u.length(); a++) {
			if (!AffineCipher.alphabet.contains(u.subSequence(a, a+1))) {
				return false;
			}
		}
		
		for (int a = 0; a < p.length(); a++) {
			if (!AffineCipher.alphabet.contains(p.subSequence(a, a+1))) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean getUsers() {
		ArrayList<UserAccount> users = new ArrayList<UserAccount>();
		String user = editUsername.getText().toString();

		UserCursor cursor = UserLab.get(getActivity()).selectAllUsers();
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			users.add(cursor.getUserInfo());
			cursor.moveToNext();
		}
		
		cursor.close();

		for (int a = 0; a < users.size(); a++) {
			if (users.get(a).getUsername().equals(user)) {
				return true;
			}
		}

		return false;
	}

	private void saveUserInfo() {
		userName = editUsername.getText().toString();
		userPassword = editPassword.getText().toString();
		userEmail = editEmail.getText().toString();
		userPhoneNumber = editPhone.getText().toString();
		mUser = new UserAccount(userName, userPassword, userEmail, userStatus, userPhoneNumber);
	}

	private void destroyFragment() {
		FragmentManager manager = getActivity().getSupportFragmentManager();
		FragmentTransaction trans = manager.beginTransaction();
		trans.remove(SignUpFragment.this);
		trans.commit();
		getActivity().finish();
	}
}
